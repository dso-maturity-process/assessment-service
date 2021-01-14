package com.governmentcio.dmp.exception;

/**
 * 
 * @author <a href=mailto:support@governmentcio.com>support
 *
 */
public class AssessmentServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	public AssessmentServiceException() {
	}

	public AssessmentServiceException(String message) {
		super(message);
	}

	public AssessmentServiceException(Throwable cause) {
		super(cause);
	}

	public AssessmentServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssessmentServiceException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
