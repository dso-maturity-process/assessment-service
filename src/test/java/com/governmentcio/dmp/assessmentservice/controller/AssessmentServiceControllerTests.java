
package com.governmentcio.dmp.assessmentservice.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.governmentcio.dmp.Application;
import com.governmentcio.dmp.model.SurveyInstance;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 * 
 *         Tests for Assessment service controller
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssessmentServiceControllerTests {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	private static final String BASE_URL = "/assessment";

	/**
	 * 
	 */
	@Test
	public void test_Getting_all_SurveyInstances() {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<Iterable<SurveyInstance>> response = restTemplate.exchange(
				createURLWithPort("/allSurveyInstances"), HttpMethod.GET, entity,
				new ParameterizedTypeReference<Iterable<SurveyInstance>>() {
				});

		assertNotNull(response);

	}

	/**
	 * 
	 */
	@Test
	public void test_SurveyInstance_CRUD_Functionality() {

		// Prepare acceptable media type
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

		Long surveyTemplateId = 9191L;
		String name = "Test survey name";
		String description = "Test survey description";
		Long projectId = 8181L;

		SurveyInstance surveyInstance = new SurveyInstance();
		surveyInstance.setName(name);
		surveyInstance.setDescription(description);
		surveyInstance.setProjectid(projectId);
		surveyInstance.setSurveytemplateid(surveyTemplateId);

		// Prepare header
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		HttpEntity<SurveyInstance> surveyEntity = new HttpEntity<SurveyInstance>(
				surveyInstance, headers);

		ResponseEntity<SurveyInstance> response = restTemplate.exchange(
				createURLWithPort("/addSurveyInstance"), HttpMethod.POST, surveyEntity,
				SurveyInstance.class);

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		SurveyInstance newSurveyInstance = response.getBody();

		assertNotNull(newSurveyInstance);

		assertTrue(newSurveyInstance.getName().equals(name));

		// Get the SurveyInstance just added

		response = restTemplate.exchange(
				createURLWithPort("/getSurveyInstance/" + newSurveyInstance.getId()),
				HttpMethod.GET, surveyEntity,
				new ParameterizedTypeReference<SurveyInstance>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		SurveyInstance retrievedSurveyInstance = response.getBody();

		assertNotNull(retrievedSurveyInstance);

		assertTrue(
				retrievedSurveyInstance.getId().equals(newSurveyInstance.getId()));
		assertTrue(
				retrievedSurveyInstance.getName().equals(newSurveyInstance.getName()));

		// Update the SurveyInstance with new text

		String updatedDescription = "This is an update to the survey instance description";

		retrievedSurveyInstance.setDescription(updatedDescription);

		headers.setAccept(acceptableMediaTypes);
		HttpEntity<
				SurveyInstance> updatedQuestionEntity = new HttpEntity<SurveyInstance>(
						retrievedSurveyInstance, headers);

		ResponseEntity<Void> responseVoid = restTemplate.exchange(
				createURLWithPort("/updateSurveyInstance"), HttpMethod.POST,
				updatedQuestionEntity, new ParameterizedTypeReference<Void>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		// Remove the SurveyInstance

		responseVoid = restTemplate.exchange(
				createURLWithPort(
						"/removeSurveyInstance/" + retrievedSurveyInstance.getId()),
				HttpMethod.DELETE, surveyEntity,
				new ParameterizedTypeReference<Void>() {
				});

		assertNotNull(responseVoid);

		assertTrue(responseVoid.getStatusCode() == HttpStatus.OK);

		// Ensure SurveyInstance removed

		response = restTemplate.exchange(
				createURLWithPort("/getSurveyInstance/" + newSurveyInstance.getId()),
				HttpMethod.GET, surveyEntity,
				new ParameterizedTypeReference<SurveyInstance>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		SurveyInstance removedSurveyInstance = response.getBody();

		assertNull(removedSurveyInstance);

	}

	/**
	 *
	 */
	@Test
	public void testHealth() {

		AssessmentServiceController asc = new AssessmentServiceController();

		assertTrue(asc.healthz());

	}

	/**
	 * Returns a valid URL for local host, available port and user supplied
	 * mapping.
	 * 
	 * @param string Mapping to controller function.
	 * @return Valid URL for local host and port.
	 */
	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + BASE_URL + uri;
	}

}
