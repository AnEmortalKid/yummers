package com.anemortalkid.yummers.schedule;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.anemortalkid.yummers.associates.Associate;
import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.experiment.FridayFinder;
import com.anemortalkid.yummers.foodpreference.FoodPreference;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceRepository;
import com.anemortalkid.yummers.foodpreference.FoodPreferenceType;
import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.rotation.Rotation;
import com.anemortalkid.yummers.rotation.RotationController;
import com.anemortalkid.yummers.slots.Slot;
import com.anemortalkid.yummers.slots.SlotRepository;
import com.anemortalkid.yummers.util.DataAnswerFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

public class FoodEventSchedulerTest {

	@Mock
	private AssociateRepository associateRepository;
	private List<Associate> associateRepositoryData;

	@Mock
	private FoodPreferenceRepository foodPreferenceRepository;
	private List<FoodPreference> snackPreferences;
	private List<FoodPreference> breakfastPreferences;

	@Mock
	private FoodEventRepository foodEventRepository;
	private List<FoodEvent> foodEventRepositoryData;

	@Mock
	private SlotRepository slotRepository;
	private List<Slot> slotRepositoryData;

	@Mock
	private RotationController rotationController;

	@InjectMocks
	private FoodEventScheduler eventScheduler;

	private MockMvc mvc;
	private static long slotId = 0L;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventScheduler).build();
		slotRepositoryData = new ArrayList<>();
		foodEventRepositoryData = new ArrayList<>();

		// setup the slots
		Mockito.when(slotRepository.findAll()).thenReturn(slotRepositoryData);
		Mockito.when(slotRepository.save(Mockito.any(Slot.class)))
				.then(DataAnswerFactory.createSaveDataAnswer(slotRepositoryData));

		Mockito.when(foodEventRepository.save(Mockito.anyList()))
				.then(new ListRepositorySaveDataListAnswer<FoodEvent>(foodEventRepositoryData));
		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				foodEventRepositoryData.clear();
				return null;
			}

		}).when(foodEventRepository).deleteAll();

		slotId = 0L;
		associateCount = 0L;
	}

	@Test
	public void testBalancedLists() throws Exception {
		setupEvenBalancedScenarioData_noRotation();

		// Set an arbitrary friday for the food events
		String startFriday = "01/01/2015";
		List<DateTime> nextFridaysFromDate = FridayFinder.getNextFridaysFromDate(startFriday, 1);
		DateTime firstDate = nextFridaysFromDate.get(0);
		Slot firstSlot = new Slot(firstDate);
		slotRepositoryData.add(firstSlot);

		Rotation firstRotation = eventScheduler.scheduleNewRotation();

		// The first order should be 0-8 and 9-16
		List<String> breakfastAssociates = firstRotation.getBreakfastAssociates();
		List<String> snackAssociates = firstRotation.getSnackAssociates();

		Assert.assertThat(breakfastAssociates, equalTo(toList("0", "1", "2", "3", "4", "5", "6", "7")));
		Assert.assertThat(snackAssociates, equalTo(toList("8", "9", "10", "11", "12", "13", "14", "15")));
		int foodEventSize = foodEventRepositoryData.size();
		Assert.assertThat(foodEventSize, equalTo(4));

		// Schedule = 2,9,16,23,30-FEB-6,13
		assertDateHas(extractDate(foodEventRepositoryData.get(0)), 2, 1);
		assertDateHas(extractDate(foodEventRepositoryData.get(1)), 9, 1);
		assertDateHas(extractDate(foodEventRepositoryData.get(2)), 16, 1);
		assertDateHas(extractDate(foodEventRepositoryData.get(3)), 23, 1);

		// Test subsequent rotation without changing associates
		Mockito.when(rotationController.getCurrentRotation())
				.thenReturn(ResponseFactory.respondOK("test", firstRotation));
		Rotation secondRotation = eventScheduler.scheduleNewRotation();
		breakfastAssociates = secondRotation.getBreakfastAssociates();
		snackAssociates = secondRotation.getSnackAssociates();
		Assert.assertThat(breakfastAssociates, equalTo(toList("1", "2", "3", "4", "5", "6", "7", "0")));
		Assert.assertThat(snackAssociates, equalTo(toList("9", "10", "11", "12", "13", "14", "15", "8")));

		// Schedule Jan 30 - Feb 6,13,20
		assertDateHas(extractDate(foodEventRepositoryData.get(0)), 30, 1);
		assertDateHas(extractDate(foodEventRepositoryData.get(1)), 6, 2);
		assertDateHas(extractDate(foodEventRepositoryData.get(2)), 13, 2);
		assertDateHas(extractDate(foodEventRepositoryData.get(3)), 20, 2);

		Mockito.when(rotationController.getCurrentRotation())
				.thenReturn(ResponseFactory.respondOK("test", secondRotation));

		// Messup the dates
		slotRepositoryData.clear();

		String marchStart = "1/3/2015";
		List<DateTime> fridays = FridayFinder.getNextFridaysFromDate(marchStart, 10);
		fridays.forEach(x -> {
			slotRepositoryData.add(new Slot(x));
		});

		/**
		 * March: 6, 13, 20 27 April: 3, 10, 17, 24 May: 1, 8
		 */
		slotRepositoryData.remove(8);
		slotRepositoryData.remove(2);
		slotRepositoryData.remove(3);
		/**
		 * March: 6, 13, 27 April: 10, 17, 24 May: 8
		 */

		// March 6, 13, 27, April 10
		Rotation thirdRotation = eventScheduler.scheduleNewRotation();
		// TODO: change to new validation methods
		// Schedule Jan 30 - Feb 6,13,20
		assertDateHas(extractDate(foodEventRepositoryData.get(0)), 6, 3);
		assertDateHas(extractDate(foodEventRepositoryData.get(1)), 13, 3);
		assertDateHas(extractDate(foodEventRepositoryData.get(2)), 27, 3);
		assertDateHas(extractDate(foodEventRepositoryData.get(3)), 10, 4);
	}

	@Test
	public void testUnbalancedLists_snackSide() throws Exception {
		setupUnbalancedScenarioData_snackSide();

		// Set an arbitrary friday for the food events
		String startFriday = "01/01/2015";
		List<DateTime> nextFridaysFromDate = FridayFinder.getNextFridaysFromDate(startFriday, 1);
		DateTime firstDate = nextFridaysFromDate.get(0);
		Slot firstSlot = new Slot(firstDate);
		slotRepositoryData.add(firstSlot);

		Rotation firstRotation = eventScheduler.scheduleNewRotation();

		// The first order should be 0-8 and 9-16
		List<String> breakfastAssociates = firstRotation.getBreakfastAssociates();
		List<String> snackAssociates = firstRotation.getSnackAssociates();

		Assert.assertThat(breakfastAssociates, equalTo(toList("0", "1", "2", "3", "4", "5", "6", "7")));
		Assert.assertThat(snackAssociates, equalTo(toList("8", "9", "10", "11", "12", "13", "14", "8")));
		int foodEventSize = foodEventRepositoryData.size();
		Assert.assertThat(foodEventSize, equalTo(4));

		// Check the next guys add up
		String nextBreakfastStarter = firstRotation.getNextBreakfastStarter();
		String nextSnackStarter = firstRotation.getNextSnackStarter();
		Assert.assertThat(nextBreakfastStarter, equalTo("1"));
		Assert.assertThat(nextSnackStarter, equalTo("9"));
	}

	@Test
	public void testUnbalancedLists_breakfastSide() throws Exception {
		setupUnbalancedScenarioData_breakfastSide();

		// Set an arbitrary friday for the food events
		String startFriday = "01/01/2015";
		List<DateTime> nextFridaysFromDate = FridayFinder.getNextFridaysFromDate(startFriday, 1);
		DateTime firstDate = nextFridaysFromDate.get(0);
		Slot firstSlot = new Slot(firstDate);
		slotRepositoryData.add(firstSlot);

		Rotation firstRotation = eventScheduler.scheduleNewRotation();

		// The first order should be 0-4 repeat of 0
		String[] expectedBreakfast = toArray("0", "1", "2", "3", "4", "0");
		String[] expectedSnack = toArray("5", "6", "7", "8", "9", "10");
		assertRotationHas(firstRotation, expectedBreakfast, expectedSnack, "1", "6");

		FoodEvent firstEvent = foodEventRepositoryData.get(0);
		assertFoodEvent(firstEvent, new String[] { "0", "1" }, new String[] { "5", "6" }, 2, 1);
	}

	@Test
	public void testAssociateAndPreferencesChangeAfterFirstRotation() throws Exception {
		setupEvenBalancedScenarioData_noRotation();
		Rotation firstRotation = eventScheduler.scheduleNewRotation();

		// reset the id counts to 0
		associateCount = 0;
		setupBalancedScenarioData_changeMidScheduled();
		Mockito.when(rotationController.shouldRegenerate()).thenReturn(true);
		Mockito.when(rotationController.getCurrentRotation())
				.thenReturn(ResponseFactory.respondOK("test", firstRotation));

		Rotation withNewData = eventScheduler.scheduleNewRotation();
		System.out.println(withNewData.getBreakfastAssociates());
		System.out.println(withNewData.getSnackAssociates());
		// TODO ASSERT THIS
	}

	private String[] toArray(String... strings) {
		return strings;
	}

	private DateTime extractDate(FoodEvent fe) {
		return fe.getDate().getSlotDate();
	}

	private void assertDateHas(DateTime dateTime, int day, int month) {
		Assert.assertThat(day, equalTo(dateTime.getDayOfMonth()));
		Assert.assertThat(month, equalTo(dateTime.getMonthOfYear()));
	}

	private void assertFoodEvent(FoodEvent foodEvent, String[] breakfast, String[] snacks, int day, int month) {
		List<String> breakfastParticipants = foodEvent.getBreakfastParticipants();
		for (int i = 0; i < breakfast.length; i++) {
			Assert.assertThat(breakfast[i], equalTo(breakfastParticipants.get(i)));
		}

		List<String> snackParticipants = foodEvent.getSnackParticipants();
		for (int i = 0; i < snacks.length; i++) {
			Assert.assertThat(snacks[i], equalTo(snackParticipants.get(i)));
		}

		assertDateHas(foodEvent.getDate().getSlotDate(), day, month);
	}

	private void assertRotationHas(Rotation rotation, String[] breakfast, String[] snacks, String nextBreakfast,
			String nextSnack) {
		List<String> breakfastAssociates = rotation.getBreakfastAssociates();
		for (int i = 0; i < breakfast.length; i++) {
			Assert.assertThat(breakfast[i], equalTo(breakfastAssociates.get(i)));
		}

		List<String> snackAssociates = rotation.getSnackAssociates();
		for (int i = 0; i < snacks.length; i++) {
			Assert.assertThat(snacks[i], equalTo(snackAssociates.get(i)));
		}

		Assert.assertThat(nextBreakfast, equalTo(rotation.getNextBreakfastStarter()));
		Assert.assertThat(nextSnack, equalTo(rotation.getNextSnackStarter()));
	}

	/**
	 * Scenario Data with 8 associates on both lists
	 */
	private void setupEvenBalancedScenarioData_noRotation() {
		// setup food preferences
		List<Associate> breakfastAssociates = new ArrayList<>();
		for (int i = 0; i < 8; i++)
			breakfastAssociates.add(generateRandomAssociate());

		List<Associate> snackAssociates = new ArrayList<>();
		for (int i = 0; i < 8; i++)
			snackAssociates.add(generateRandomAssociate());

		List<Associate> allAssociates = new ArrayList<>();
		allAssociates.addAll(breakfastAssociates);
		allAssociates.addAll(snackAssociates);

		// setup the return
		allAssociates.forEach(x -> Mockito.when(associateRepository.findOne(x.getAssociateId())).thenReturn(x));

		List<FoodPreference> breakfastAssociatePreferences = new ArrayList<>();
		breakfastAssociates
				.forEach(x -> breakfastAssociatePreferences.add(createPreference(x, FoodPreferenceType.BREAKFAST)));
		Mockito.when(foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST))
				.thenReturn(breakfastAssociatePreferences);

		List<FoodPreference> snackAssociatesPreference = new ArrayList<>();
		snackAssociates.forEach(x -> snackAssociatesPreference.add(createPreference(x, FoodPreferenceType.SNACK)));
		Mockito.when(foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.SNACK))
				.thenReturn(snackAssociatesPreference);

		// setup no rotation
		Mockito.when(rotationController.getCurrentRotation()).thenReturn(ResponseFactory.respondFail("test", "test"));
	}

	/**
	 * Scenario Data with 9 associates
	 */
	private void setupBalancedScenarioData_changeMidScheduled() {
		// setup food preferences
		List<Associate> breakfastAssociates = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			breakfastAssociates.add(generateRandomAssociate());

		List<Associate> snackAssociates = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			snackAssociates.add(generateRandomAssociate());

		List<Associate> allAssociates = new ArrayList<>();
		allAssociates.addAll(breakfastAssociates);
		allAssociates.addAll(snackAssociates);

		// setup the return
		allAssociates.forEach(x -> Mockito.when(associateRepository.findOne(x.getAssociateId())).thenReturn(x));

		List<FoodPreference> breakfastAssociatePreferences = new ArrayList<>();
		breakfastAssociates
				.forEach(x -> breakfastAssociatePreferences.add(createPreference(x, FoodPreferenceType.BREAKFAST)));
		Mockito.when(foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST))
				.thenReturn(breakfastAssociatePreferences);

		List<FoodPreference> snackAssociatesPreference = new ArrayList<>();
		snackAssociates.forEach(x -> snackAssociatesPreference.add(createPreference(x, FoodPreferenceType.SNACK)));
		Mockito.when(foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.SNACK))
				.thenReturn(snackAssociatesPreference);

		// setup no rotation
		Mockito.when(rotationController.getCurrentRotation()).thenReturn(ResponseFactory.respondFail("test", "test"));
	}

	private void setupUnbalancedScenarioData_snackSide() {
		// setup food preferences
		List<Associate> breakfastAssociates = new ArrayList<>();
		for (int i = 0; i < 8; i++)
			breakfastAssociates.add(generateRandomAssociate());

		List<Associate> snackAssociates = new ArrayList<>();
		for (int i = 0; i < 7; i++)
			snackAssociates.add(generateRandomAssociate());

		List<Associate> allAssociates = new ArrayList<>();
		allAssociates.addAll(breakfastAssociates);
		allAssociates.addAll(snackAssociates);

		// setup the return
		allAssociates.forEach(x -> Mockito.when(associateRepository.findOne(x.getAssociateId())).thenReturn(x));

		List<FoodPreference> breakfastAssociatePreferences = new ArrayList<>();
		breakfastAssociates
				.forEach(x -> breakfastAssociatePreferences.add(createPreference(x, FoodPreferenceType.BREAKFAST)));
		Mockito.when(foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST))
				.thenReturn(breakfastAssociatePreferences);

		List<FoodPreference> snackAssociatesPreference = new ArrayList<>();
		snackAssociates.forEach(x -> snackAssociatesPreference.add(createPreference(x, FoodPreferenceType.SNACK)));
		Mockito.when(foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.SNACK))
				.thenReturn(snackAssociatesPreference);

		// setup no rotation
		Mockito.when(rotationController.getCurrentRotation()).thenReturn(ResponseFactory.respondFail("test", "test"));
	}

	private void setupUnbalancedScenarioData_breakfastSide() {
		// setup food preferences
		List<Associate> breakfastAssociates = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			breakfastAssociates.add(generateRandomAssociate());

		List<Associate> snackAssociates = new ArrayList<>();
		for (int i = 0; i < 6; i++)
			snackAssociates.add(generateRandomAssociate());

		List<Associate> allAssociates = new ArrayList<>();
		allAssociates.addAll(breakfastAssociates);
		allAssociates.addAll(snackAssociates);

		// setup the return
		allAssociates.forEach(x -> Mockito.when(associateRepository.findOne(x.getAssociateId())).thenReturn(x));

		List<FoodPreference> breakfastAssociatePreferences = new ArrayList<>();
		breakfastAssociates
				.forEach(x -> breakfastAssociatePreferences.add(createPreference(x, FoodPreferenceType.BREAKFAST)));
		Mockito.when(foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.BREAKFAST))
				.thenReturn(breakfastAssociatePreferences);

		List<FoodPreference> snackAssociatesPreference = new ArrayList<>();
		snackAssociates.forEach(x -> snackAssociatesPreference.add(createPreference(x, FoodPreferenceType.SNACK)));
		Mockito.when(foodPreferenceRepository.findDistinctByPreferenceType(FoodPreferenceType.SNACK))
				.thenReturn(snackAssociatesPreference);

		// setup no rotation
		Mockito.when(rotationController.getCurrentRotation()).thenReturn(ResponseFactory.respondFail("test", "test"));
	}

	private static long associateCount = 0L;

	private Associate generateRandomAssociate() {
		String id = associateCount++ + "";
		String fn = id + "-FirstName";
		String ln = id + "-LastName";
		return new Associate(id, fn, ln);
	}

	private FoodPreference createPreference(Associate associate, FoodPreferenceType type) {
		return new FoodPreference(associate, type);
	}

	private List<String> toList(String... strings) {
		return Arrays.asList(strings);
	}

	private class ListRepositorySaveDataListAnswer<T> implements Answer<T> {
		private List<T> repositoryData;

		private ListRepositorySaveDataListAnswer(List<T> repositoryData) {
			this.repositoryData = repositoryData;
		}

		@Override
		public T answer(InvocationOnMock invocation) throws Throwable {
			List argList = invocation.getArgumentAt(0, List.class);
			repositoryData.addAll(argList);
			return null;
		}
	}
}
