package com.governmentcio.dmp.assessmentservice.service;

import com.governmentcio.dmp.exception.AssessmentServiceException;
import com.governmentcio.dmp.model.SurveyInstance;
import com.governmentcio.dmp.model.SurveyResponse;

public interface AssessmentService {

	/**
	 * 
	 * @return
	 */
	Iterable<SurveyInstance> getSurveyInstances();

	/**
	 * 
	 * @param surveyTemplateId
	 * @param projectId
	 * @param name
	 * @param description
	 * @return
	 */
	SurveyInstance addSurveyInstance(SurveyInstance surveyInstance);

	/**
	 * 
	 * @param id
	 * @return
	 */
	SurveyInstance getSurveyInstanceById(Long id)
			throws AssessmentServiceException;

	/**
	 * 
	 * @param surveyInstance
	 * @throws SurveyServiceException
	 */
	void updateSurveyInstance(SurveyInstance surveyInstance)
			throws AssessmentServiceException;

	/**
	 * 
	 * @param id
	 */
	void removeSurveyInstance(Long id) throws AssessmentServiceException;

	/**
	 * 
	 * @param surveyInstanceId
	 * @param question
	 * @param sequence
	 * @return
	 * @throws AssessmentServiceException
	 */
	SurveyResponse addSurveyResponse(final Long surveyInstanceId,
			final String question, final Long sequence)
			throws AssessmentServiceException;

	/**
	 * 
	 * @param surveyResponse
	 * @throws AssessmentServiceException
	 */
	void updateSurveyResponse(final SurveyResponse surveyResponse)
			throws AssessmentServiceException;

	/**
	 * 
	 * @param id
	 * @throws AssessmentServiceException
	 */
	void removeSurveyResponse(final Long id) throws AssessmentServiceException;

}