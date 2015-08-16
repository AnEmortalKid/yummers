package com.anemortalkid.yummers.associates;

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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.anemortalkid.yummers.util.TestUtil;

/**
 * Unit tests for AssociateController
 * 
 * @author JMonterrubio
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class AssociateControllerTest {

	private MockMvc mvc;

	@Mock
	private AssociateRepository mockRespository;

	@InjectMocks
	private AssociateController associateController;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(associateController).build();
	}

	@Test
	public void testList() throws Exception {
		Associate a1 = new Associate("ab231", "a", "one");
		Associate a2 = new Associate("ab322", "a", "two");
		List<Associate> testAssociates = Arrays.asList(a1, a2);
		when(mockRespository.findAll()).thenReturn(testAssociates);

		MockHttpServletRequestBuilder mockRequestBuilder = MockMvcRequestBuilders.get("/associates/list")
				.accept(MediaType.APPLICATION_JSON);
		ResultActions resultActions = mvc.perform(mockRequestBuilder);
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));
		resultActions.andExpect(jsonPath("$", hasSize(2)));
		verify(mockRespository, Mockito.times(1)).findAll();
	}
}
