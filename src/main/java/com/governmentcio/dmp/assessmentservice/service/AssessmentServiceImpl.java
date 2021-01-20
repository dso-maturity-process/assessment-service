/**
 * 
 */
package com.governmentcio.dmp.assessmentservice.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.governmentcio.dmp.dao.DomainFactory;
import com.governmentcio.dmp.dao.SurveyInstanceDao;
import com.governmentcio.dmp.exception.AssessmentServiceException;
import com.governmentcio.dmp.model.QuestionTemplate;
import com.governmentcio.dmp.model.SurveyInstance;
import com.governmentcio.dmp.model.SurveyTemplate;
import com.governmentcio.dmp.repository.SurveyInstanceRepository;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 *
 */
@Service
public class AssessmentServiceImpl implements AssessmentService {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(AssessmentServiceImpl.class.getName());

	@Autowired
	private SurveyInstanceRepository surveyInstanceRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.assessmentservice.service.AssessmentService#
	 * getSurveyInstances()
	 */
	@Override
	public Iterable<SurveyInstance> getSurveyInstances() {

		Iterable<
				SurveyInstanceDao> iterableSurveyInstanceDaos = surveyInstanceRepository
						.findAll();

		Set<SurveyInstance> surveyInstances = new HashSet<SurveyInstance>();

		for (SurveyInstanceDao nextDao : iterableSurveyInstanceDaos) {
			SurveyInstance surveyInstance = DomainFactory
					.createSurveyInstance(nextDao);
			if (null != surveyInstance) {
				surveyInstances.add(surveyInstance);
			}
		}

		return surveyInstances;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.assessmentservice.service.AssessmentService#
	 * addSurveyInstance(com.governmentcio.dmp.model.SurveyInstance)
	 */
	@Override
	@Transactional
	public SurveyInstance addSurveyInstance(final SurveyInstance surveyInstance) {

		if (null == surveyInstance) {
			throw new IllegalArgumentException("SurveyInstance was null");
		}
		if (null == surveyInstance.getSurveytemplateid()) {
			throw new IllegalArgumentException("Survey template ID was null");
		}
		if (null == surveyInstance.getName()) {
			throw new IllegalArgumentException("Name was null");
		}
		if (null == surveyInstance.getDescription()) {
			throw new IllegalArgumentException("Description was null");
		}
		if (null == surveyInstance.getProjectid()) {
			throw new IllegalArgumentException("Project ID was null");
		}

		LOG.info("Adding survey instance [" + surveyInstance.getName() + "]-["
				+ surveyInstance.getDescription() + "].");

		SurveyInstanceDao surveyInstanceDao = new SurveyInstanceDao(
				surveyInstance.getSurveytemplateid(), surveyInstance.getProjectid(),
				surveyInstance.getName());

		surveyInstanceDao.setDescription(surveyInstance.getDescription());

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<SurveyTemplate> surveyTemplateResponseEntity = restTemplate
				.getForEntity(
						"http://localhost:8090/survey/getSurveyTemplateById/10001",
						SurveyTemplate.class);

		Set<QuestionTemplate> questionTemplates = surveyTemplateResponseEntity
				.getBody().getQuestionTemplates();

		SurveyInstanceDao newSurveyInstanceDao = surveyInstanceRepository
				.save(surveyInstanceDao);

		LOG.info("Survey instance [" + surveyInstance.getName() + "] added.");

		return DomainFactory.createSurveyInstance(newSurveyInstanceDao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.assessmentservice.service.AssessmentService#
	 * getSurveyInstanceById(java.lang.Long)
	 */
	@Override
	public SurveyInstance getSurveyInstanceById(final Long id)
			throws AssessmentServiceException {

		Optional<
				SurveyInstanceDao> surveyInstanceOptional = surveyInstanceRepository
						.findById(id);

		SurveyInstance surveyInstance = null;

		if (surveyInstanceOptional.isPresent()) {
			surveyInstance = DomainFactory
					.createSurveyInstance(surveyInstanceOptional.get());
		}

		return surveyInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.assessmentservice.service.AssessmentService#
	 * updateSurveyInstance(com.governmentcio.dmp.model.SurveyInstance)
	 */
	@Override
	@Transactional
	public void updateSurveyInstance(final SurveyInstance surveyInstance)
			throws AssessmentServiceException {

		if (null == surveyInstance) {
			throw new IllegalArgumentException("SurveyInstance was null");
		}

		Optional<
				SurveyInstanceDao> surveyInstanceOptional = surveyInstanceRepository
						.findById(surveyInstance.getId());

		if (!surveyInstanceOptional.isPresent()) {
			throw new AssessmentServiceException("SurveyInstance ["
					+ surveyInstance.getName() + "] not found for update.");
		}

		SurveyInstanceDao surveyInstanceDaoToUpdate = surveyInstanceOptional.get();

		surveyInstanceDaoToUpdate.setName(surveyInstance.getName());
		surveyInstanceDaoToUpdate.setDescription(surveyInstance.getDescription());
		surveyInstanceDaoToUpdate.setProjectId(surveyInstance.getProjectid());
		surveyInstanceDaoToUpdate
				.setSurveyTemplateId(surveyInstance.getSurveytemplateid());

		surveyInstanceRepository.save(surveyInstanceDaoToUpdate);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.assessmentservice.service.AssessmentService#
	 * removeSurveyInstance(java.lang.Long)
	 */
	@Override
	@Transactional
	public void removeSurveyInstance(final Long id)
			throws AssessmentServiceException {

		Optional<
				SurveyInstanceDao> surveyInstanceOptional = surveyInstanceRepository
						.findById(id);

		if (!surveyInstanceOptional.isPresent()) {
			throw new AssessmentServiceException(
					"SurveyInstance not found for update using id [" + id + "]");
		}

		surveyInstanceRepository.delete(surveyInstanceOptional.get());

	}

}
