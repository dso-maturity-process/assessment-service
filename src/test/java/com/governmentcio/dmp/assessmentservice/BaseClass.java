package com.governmentcio.dmp.assessmentservice;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.governmentcio.dmp.Application;
import com.governmentcio.dmp.assessmentservice.controller.AssessmentServiceController;
import com.governmentcio.dmp.assessmentservice.service.AssessmentService;
import com.governmentcio.dmp.exception.AssessmentServiceException;
import com.governmentcio.dmp.model.SurveyInstance;
import com.governmentcio.dmp.model.SurveyResponse;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public abstract class BaseClass {

	@Autowired
	AssessmentServiceController assessmentServiceController;

	@MockBean
	AssessmentService assessmentService;

	@Before
	public void setup() throws AssessmentServiceException {

		RestAssuredMockMvc.standaloneSetup(assessmentServiceController);

		SurveyInstance surveyInstance = new SurveyInstance(10001L, 20001L, 30001L,
				10001L, 10001L, "VA - Initial Survey - WEEMS");
		surveyInstance.setDescription("First survey for WEEMS");

		SurveyResponse surveyResponse = new SurveyResponse(20001L,
				"This is the first question");
		surveyResponse.setSequence(1L);
		surveyResponse.setAnswer("This is the answer to the first question");

		surveyResponse = new SurveyResponse(20002L, "This is the second question.");
		surveyResponse.setSequence(2L);
		surveyResponse.setAnswer("This is the answer to the second question");

		surveyInstance.getSurveyresponses().add(surveyResponse);

		Set<SurveyInstance> surveyInstances = new HashSet<SurveyInstance>();

		surveyInstances.add(surveyInstance);

		Mockito
				.when(assessmentServiceController.getSurveyInstancesByProjectId(30001L))
				.thenReturn(surveyInstances);

	}

}
