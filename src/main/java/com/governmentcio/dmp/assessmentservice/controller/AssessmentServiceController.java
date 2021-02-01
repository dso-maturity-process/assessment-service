/**
 * 
 */
package com.governmentcio.dmp.assessmentservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.governmentcio.dmp.assessmentservice.service.AssessmentService;
import com.governmentcio.dmp.exception.AssessmentServiceException;
import com.governmentcio.dmp.model.SurveyInstance;
import com.governmentcio.dmp.model.SurveyResponse;
import com.governmentcio.dmp.utility.ServiceHealth;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 *
 */
@RestController
@RequestMapping("/assessment")
public class AssessmentServiceController {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(AssessmentServiceController.class.getName());

	AssessmentService assessmentService;

	/**
	 * @return the assessmentService
	 */
	public AssessmentService getAssessmentService() {
		return assessmentService;
	}

	/**
	 * @param assessmentService the assessmentService to set
	 */
	@Autowired
	public void setAssessmentService(AssessmentService assessmentService) {
		this.assessmentService = assessmentService;
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping("/allSurveyInstances")
	public Iterable<SurveyInstance> getSurveyInstances() {
		return assessmentService.getSurveyInstances();
	}

	/**
	 * 
	 * @return
	 */
	@GetMapping("/allSurveyInstancesByProjectId")
	public Iterable<SurveyInstance> getSurveyInstancesByProjectId(
			@RequestParam(value = "projectId", required = true) final Long projectId) {
		return assessmentService.getSurveyInstancesByProjectId(projectId);
	}

	/**
	 * 
	 * @param surveyTemplateId
	 * @param projectId
	 * @param name
	 * @param description
	 * @return
	 */
	@RequestMapping(value = "/addSurveyInstance", method = RequestMethod.POST)
	public SurveyInstance addSurveyInstance(
			@RequestBody(required = true) SurveyInstance surveyInstance) {
		return assessmentService.addSurveyInstance(surveyInstance);
	}

	/**
	 * 
	 * @param surveyInstanceId
	 * @param question
	 * @param sequence
	 * @return
	 * @throws AssessmentServiceException
	 */
	@RequestMapping(value = "/addSurveyResponse", method = RequestMethod.POST)
	public SurveyResponse addSurveyResponse(
			@RequestParam(value = "surveyInstanceId", required = true) Long surveyInstanceId,
			@RequestParam(value = "question", required = true) String question,
			@RequestParam(value = "sequence", required = true) Long sequence)
			throws AssessmentServiceException {
		return assessmentService.addSurveyResponse(surveyInstanceId, question,
				sequence);
	}

	/**
	 * 
	 * @param surveyResponse
	 * @throws AssessmentServiceException
	 */
	@RequestMapping(value = "/updateSurveyResponse", method = RequestMethod.POST)
	public void updateSurveyResponse(
			@RequestBody(required = true) SurveyResponse surveyResponse)
			throws AssessmentServiceException {
		assessmentService.updateSurveyResponse(surveyResponse);
	}

	/**
	 * 
	 * @param id
	 */
	@DeleteMapping("/removeSurveyResponse/{id}")
	public void removeSurveyResponse(@PathVariable Long id)
			throws AssessmentServiceException {
		assessmentService.removeSurveyResponse(id);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/getSurveyInstance/{id}")
	public SurveyInstance getSurveyInstanceById(@PathVariable Long id)
			throws AssessmentServiceException {
		return assessmentService.getSurveyInstanceById(id);
	}

	/**
	 * 
	 * @param surveyInstance
	 * @throws SurveyServiceException
	 */
	@RequestMapping(value = "/updateSurveyInstance", method = RequestMethod.POST)
	public void updateSurveyInstance(
			@RequestBody(required = true) SurveyInstance surveyInstance)
			throws AssessmentServiceException {
		assessmentService.updateSurveyInstance(surveyInstance);
	}

	/**
	 * 
	 * @param id
	 */
	@DeleteMapping("/removeSurveyInstance/{id}")
	public void removeSurveyInstance(@PathVariable Long id)
			throws AssessmentServiceException {

		assessmentService.removeSurveyInstance(id);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/healthz")
	public ServiceHealth healthz() {

		LOG.info("Checking health...");

		return new ServiceHealth("Assessment", true); // TODO: Replace canned
																									// response.
	}

}
