package com.anemortalkid.yummers.foodpreference;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.anemortalkid.yummers.associates.Associate;
import com.anemortalkid.yummers.associates.AssociateController;
import com.anemortalkid.yummers.associates.AssociateRepository;
import com.anemortalkid.yummers.util.TestUtil;

public class FoodPreferenceControllerTest {

	private MockMvc mvc;

	@Mock
	private FoodPreferenceRepository mockFoodRepository;

	@Mock
	private AssociateRepository associateRepository;

	@Mock
	private AssociateController mockAssociateController;

	@InjectMocks
	private FoodPreferenceController foodPreferenceController;

	private List<FoodPreference> foodPreferenceList;
	private List<Associate> associateList;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(foodPreferenceController).build();

	}

}
