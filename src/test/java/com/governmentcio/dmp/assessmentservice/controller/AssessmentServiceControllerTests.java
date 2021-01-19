
package com.governmentcio.dmp.assessmentservice.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.governmentcio.dmp.Application;
import com.governmentcio.dmp.model.QuestionTemplate;
import com.governmentcio.dmp.model.SurveyInstance;
import com.governmentcio.dmp.model.SurveyTemplate;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 * 
 *         Tests for Assessment service controller
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = "com.governmentcio.dmp:survey-service:+:stubs:8090")
class AssessmentServiceControllerTests {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	private static final String BASE_URL = "/assessment";

	@Test
	public void get_SurveyTemplate_by_Name() {
		// given:
		RestTemplate restTemplate = new RestTemplate();

		// when:
		ResponseEntity<SurveyTemplate> surveyTemplateResponseEntity = restTemplate
				.getForEntity(
						"http://localhost:8090/survey/getSurveyTemplate/VeteransAdministration-DSO",
						SurveyTemplate.class);

		// then:
		BDDAssertions.then(surveyTemplateResponseEntity.getStatusCodeValue())
				.isEqualTo(200);
		BDDAssertions.then(surveyTemplateResponseEntity.getBody().getId())
				.isEqualTo(10001l);
		BDDAssertions.then(surveyTemplateResponseEntity.getBody().getName())
				.isEqualTo("VeteransAdministration-DSO");
		BDDAssertions.then(surveyTemplateResponseEntity.getBody().getDescription())
				.isEqualTo("Primary survey for the VA");

		Set<QuestionTemplate> questionTemplates = surveyTemplateResponseEntity
				.getBody().getQuestionTemplates();

		BDDAssertions.then(questionTemplates.size()).isEqualTo(2);

		for (QuestionTemplate questionTemplate : questionTemplates) {
			if (questionTemplate.getId() == 20001L) {
				BDDAssertions.then(questionTemplate.getText())
						.isEqualTo("Text for the first question");
			} else if (questionTemplate.getId() == 20002L) {
				BDDAssertions.then(questionTemplate.getText())
						.isEqualTo("Text for the second question");
			} else {
				BDDAssertions.then(false);
			}
		}
	}

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
