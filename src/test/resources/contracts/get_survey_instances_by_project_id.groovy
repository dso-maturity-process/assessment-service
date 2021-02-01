import org.springframework.cloud.contract.spec.Contract

Contract.make {
	description "should return survey instances for a project id"

	request {
		url "/assessment/allSurveyInstancesByProjectId/30001"
		method GET()
	}

	response {
		status OK()
		headers {
			contentType( applicationJson())
		}
		body (
		  surveyinstanceid: 10001,
		  surveytemplateid: 20001,
			projectid: 30001,
			startTimestamp: 01-01-2021,
			name: "VA - Initial Survey - WEEMS",
			description: "First survey for WEEMS",
			"surveyresponses" : 
			[
    			[ 
    			id : 20001,
    			sequence : 1,
    			question : "This is the first question",
    			answer : "This is the answer to the first question"
    			],
    		  [ 
    		  id : 20002,    			
    		  sequence : 2,
    			question : "This is the second question",
    			answer : "This is the answer to the second question"
    			]
    	]
		)
	}
}