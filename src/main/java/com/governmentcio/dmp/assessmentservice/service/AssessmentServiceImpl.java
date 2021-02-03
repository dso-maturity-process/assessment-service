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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.governmentcio.dmp.dao.DomainFactory;
import com.governmentcio.dmp.dao.SurveyInstanceDao;
import com.governmentcio.dmp.dao.SurveyResponseDao;
import com.governmentcio.dmp.exception.AssessmentServiceException;
import com.governmentcio.dmp.model.QuestionTemplate;
import com.governmentcio.dmp.model.SurveyInstance;
import com.governmentcio.dmp.model.SurveyResponse;
import com.governmentcio.dmp.model.SurveyTemplate;
import com.governmentcio.dmp.repository.SurveyInstanceRepository;
import com.governmentcio.dmp.repository.SurveyResponseRepository;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support</a>
 *
 */
@Service
public class AssessmentServiceImpl implements AssessmentService {

	@Value("${survey.service.host}")
	private String surveyServiceHost;

	@Value("${survey.service.port}")
	private Long surveyServicePort;

	@Value("${survey.service.name}")
	private String surveyServiceName;

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(AssessmentServiceImpl.class.getName());

	@Autowired
	private SurveyInstanceRepository surveyInstanceRepository;

	@Autowired
	private SurveyResponseRepository surveyResponseRepository;

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
	 * getSurveyInstancesByProjectId(java.lang.Long)
	 */
	@Override
	public Iterable<SurveyInstance> getSurveyInstancesByProjectId(
			final Long projectId) {

		Iterable<
				SurveyInstanceDao> iterableSurveyInstanceDaos = surveyInstanceRepository
						.findByProjectId(projectId, PageRequest.of(0, 10));

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
		if (null == surveyInstance.getUserid()) {
			throw new IllegalArgumentException("User ID was null");
		}
		if (null == surveyInstance.getRoleid()) {
			throw new IllegalArgumentException("Role ID was null");
		}

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<
				SurveyTemplate> surveyTemplateResponseEntity = restTemplate
						.getForEntity(
								createSurveyURLWithPort("/getSurveyTemplateById/"
										+ surveyInstance.getSurveytemplateid()),
								SurveyTemplate.class);

		Set<QuestionTemplate> questionTemplates = surveyTemplateResponseEntity
				.getBody().getQuestionTemplates();

		SurveyInstanceDao surveyInstanceDao = new SurveyInstanceDao(
				surveyInstance.getSurveytemplateid(), surveyInstance.getProjectid(),
				surveyInstance.getUserid(), surveyInstance.getRoleid(),
				surveyInstance.getName());

		surveyInstanceDao.setDescription(surveyInstance.getDescription());

		LOG.info("Adding survey instance [" + surveyInstance.getName() + "]-["
				+ surveyInstance.getDescription() + "].");

		SurveyInstanceDao newSurveyInstanceDao = surveyInstanceRepository
				.save(surveyInstanceDao);

		for (QuestionTemplate questionTemplate : questionTemplates) {

			if (null != questionTemplate) {

				SurveyResponseDao surveyResponseDao = new SurveyResponseDao(
						questionTemplate.getText(), "", questionTemplate.getSequence(),
						surveyInstanceDao);

				SurveyResponseDao newSurveyResponseDao = surveyResponseRepository
						.save(surveyResponseDao);

				newSurveyInstanceDao.getSurveyResponseDaos().add(newSurveyResponseDao);

			}
		}

		newSurveyInstanceDao = surveyInstanceRepository.save(newSurveyInstanceDao);

		LOG.info("Survey instance [" + newSurveyInstanceDao.getName() + "] added.");

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
					"SurveyInstance not found for removal using id [" + id + "]");
		}

		surveyInstanceRepository.delete(surveyInstanceOptional.get());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.assessmentservice.service.AssessmentService#
	 * addSurveyResponse(java.lang.Long, java.lang.String, java.lang.Long)
	 */
	@Override
	@Transactional
	public SurveyResponse addSurveyResponse(final Long surveyInstanceId,
			final String question, final Long sequence)
			throws AssessmentServiceException {

		if ((null == question) || (question.length() == 0)) {
			throw new IllegalArgumentException("Question text was null or empty");
		}

		Optional<
				SurveyInstanceDao> surveyInstanceOptional = surveyInstanceRepository
						.findById(surveyInstanceId);

		if (!surveyInstanceOptional.isPresent()) {
			throw new AssessmentServiceException(
					"No survey found using id [" + surveyInstanceId + "]");
		}

		SurveyInstanceDao surveyInstanceDao = surveyInstanceOptional.get();

		SurveyResponse newSurveyResponse = new SurveyResponse();

		newSurveyResponse.setQuestion(question);
		newSurveyResponse.setSequence(sequence);

		SurveyResponseDao surveyResponseDao = DomainFactory
				.createSurveyResponseDao(newSurveyResponse);

		surveyResponseDao.setSurveyInstanceDao(surveyInstanceDao);

		surveyResponseDao = surveyResponseRepository.save(surveyResponseDao);

		surveyInstanceDao.getSurveyResponseDaos().add(surveyResponseDao);

		surveyInstanceRepository.save(surveyInstanceDao);

		return DomainFactory.createSurveyResponse(surveyResponseDao);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.assessmentservice.service.AssessmentService#
	 * updateSurveyResponse(com.governmentcio.dmp.model.SurveyResponse)
	 */
	@Override
	@Transactional
	public void updateSurveyResponse(final SurveyResponse surveyResponse)
			throws AssessmentServiceException {

		if (null == surveyResponse) {
			throw new IllegalArgumentException("SurveyResponse was null");
		}

		Optional<
				SurveyResponseDao> surveyResponseOptional = surveyResponseRepository
						.findById(surveyResponse.getId());

		if (!surveyResponseOptional.isPresent()) {
			throw new AssessmentServiceException(
					"SurveyResponse not found for update.");
		}

		SurveyResponseDao surveyResponseDaoToUpdate = surveyResponseOptional.get();

		surveyResponseDaoToUpdate.setQuestion(surveyResponse.getQuestion());
		surveyResponseDaoToUpdate.setAnswer(surveyResponse.getAnswer());
		surveyResponseDaoToUpdate.setSequence(surveyResponse.getSequence());

		surveyResponseRepository.save(surveyResponseDaoToUpdate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.governmentcio.dmp.assessmentservice.service.AssessmentService#
	 * removeSurveyResponse(java.lang.Long)
	 */
	@Override
	@Transactional
	public void removeSurveyResponse(final Long id)
			throws AssessmentServiceException {

		Optional<
				SurveyResponseDao> surveyResponseOptional = surveyResponseRepository
						.findById(id);

		if (!surveyResponseOptional.isPresent()) {
			throw new AssessmentServiceException(
					"SurveyResponse not found for removal using id [" + id + "]");
		}

		surveyResponseRepository.delete(surveyResponseOptional.get());
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	private String createSurveyURLWithPort(String uri) {
		return "http://" + surveyServiceHost + ":" + surveyServicePort + "/"
				+ surveyServiceName + uri;
	}

}
