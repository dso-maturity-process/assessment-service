package com.governmentcio.dmp.assessmentservice.service;

import com.governmentcio.dmp.exception.AssessmentServiceException;
import com.governmentcio.dmp.model.SurveyInstance;

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

}