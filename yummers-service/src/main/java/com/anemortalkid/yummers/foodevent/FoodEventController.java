package com.anemortalkid.yummers.foodevent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;

@RestController
@RequestMapping("/foodevents")
public class FoodEventController {

	@Autowired
	private FoodEventRepository foodEventRepository;

	public List<FoodEvent> saveNewEvents(List<FoodEvent> newEvents) {
		// remove active ones first
		List<FoodEvent> previousActive = foodEventRepository
				.findByIsActive(true);
		previousActive.forEach(foodEvent -> foodEvent.setActive(false));
		foodEventRepository.save(previousActive);

		// add new events
		newEvents.forEach(foodEvent -> foodEvent.setActive(true));
		foodEventRepository.save(newEvents);
		return newEvents;
	}

	public FoodEvent getUpcomingEvent() {
		List<FoodEvent> activeEvents = foodEventRepository.findByIsActive(true);
		return activeEvents.isEmpty() ? null : activeEvents.get(0);
	}

	@RequestMapping(value = "/upcoming", method = RequestMethod.GET)
	public YummersResponseEntity<FoodEvent> upcomingEvent() {
		List<FoodEvent> activeEvents = foodEventRepository.findByIsActive(true);
		String callingPath = "/foodEvents/upcoming";
		FoodEvent firstEvent = activeEvents.isEmpty() ? null : activeEvents
				.get(0);
		return ResponseFactory.respondOK(callingPath, firstEvent);
	}

	public List<FoodEvent> getActiveEvents() {
		return foodEventRepository.findByIsActive(true);
	}

	@RequestMapping(value = "/listActive", method = RequestMethod.GET)
	public YummersResponseEntity<List<FoodEvent>> listActive() {
		String callingPath = "/foodEvents/listActive";
		List<FoodEvent> activeEvents = foodEventRepository.findByIsActive(true);
		return ResponseFactory.respondOK(callingPath, activeEvents);
	}

	public void deactivateEvent(FoodEvent upcomingEvent) {
		upcomingEvent.setActive(false);
		foodEventRepository.save(upcomingEvent);
	}

	public void setCalendarInviteSent(FoodEvent foodEvent, boolean inviteSent) {
		foodEvent.setCalendarInviteSent(inviteSent);
		foodEventRepository.save(foodEvent);
	}

	public void setReminderEmailSent(FoodEvent foodEvent, boolean sent) {
		foodEvent.setReminderSent(sent);
		foodEventRepository.save(foodEvent);
	}
}
