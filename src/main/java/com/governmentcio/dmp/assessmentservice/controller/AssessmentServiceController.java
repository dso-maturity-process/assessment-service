/**
 * 
 */
package com.governmentcio.dmp.assessmentservice.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.governmentcio.dmp.dao.DomainFactory;
import com.governmentcio.dmp.dao.SurveyInstanceDao;
import com.governmentcio.dmp.exception.AssessmentServiceException;
import com.governmentcio.dmp.model.SurveyInstance;
import com.governmentcio.dmp.repository.SurveyInstanceRepository;

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

	@Autowired
	private SurveyInstanceRepository surveyInstanceRepository;

	/**
	 * 
	 * @return
	 */
	@GetMapping("/allSurveyInstances")
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

	/**
	 * 
	 * @param surveyTemplateId
	 * @param projectId
	 * @param name
	 * @param description
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/addSurveyInstance", method = RequestMethod.POST)
	public SurveyInstance addSurveyInstance(
			@RequestBody(required = true) SurveyInstance surveyInstance) {

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

		SurveyInstanceDao newSurveyInstanceDao = surveyInstanceRepository
				.save(surveyInstanceDao);

		LOG.info("Survey instance [" + surveyInstance.getName() + "] added.");

		return DomainFactory.createSurveyInstance(newSurveyInstanceDao);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/getSurveyInstance/{id}")
	public SurveyInstance getSurveyInstanceById(@PathVariable Long id)
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

	/**
	 * 
	 * @param surveyInstance
	 * @throws SurveyServiceException
	 */
	@Transactional
	@RequestMapping(value = "/updateSurveyInstance", method = RequestMethod.POST)
	public void updateSurveyInstance(
			@RequestBody(required = true) SurveyInstance surveyInstance)
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

	/**
	 * 
	 * @param id
	 */
	@DeleteMapping("/removeSurveyInstance/{id}")
	public void removeSurveyInstance(@PathVariable Long id)
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

	/**
	 * 
	 * @param name
	 * @return
	 */
	@PostMapping("/healthz")
	public boolean healthz() {

		LOG.info("Checking health...");

		LOG.info("Assessment service is healthy");

		return true; // TODO: Replace canned response.
	}

}
