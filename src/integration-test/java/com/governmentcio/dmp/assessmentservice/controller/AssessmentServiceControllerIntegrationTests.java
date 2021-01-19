
package com.governmentcio.dmp.assessmentservice.controller;

/**
 * 
 *  @author <a href=mailto:support@governmentcio.com>support</a>
 *  
 */

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.governmentcio.dmp.Application;
import com.governmentcio.dmp.utility.ServiceHealth;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 * 
 *         Tests for Assessment service controller
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssessmentServiceControllerIntegrationTests {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	private static final String BASE_URL = "/assessment";

	/**
	 *
	 */
	@Test
	public void testHealth() throws JSONException {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

		ResponseEntity<ServiceHealth> response = restTemplate.exchange(
				createURLWithPort("/healthz"), HttpMethod.GET, entity,
				new ParameterizedTypeReference<ServiceHealth>() {
				});

		assertNotNull(response);

		assertTrue(response.getStatusCode() == HttpStatus.OK);

		ServiceHealth srvHealth = response.getBody();

		assertNotNull(srvHealth);

		assertTrue(srvHealth.isHealthy());
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
