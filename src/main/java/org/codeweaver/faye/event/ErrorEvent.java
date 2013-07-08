package org.codeweaver.faye.event;

/**
 * Created with IntelliJ IDEA. User: Berwyn Codeweaver Date: 08/07/13 Time:
 * 15:24 To change this template use File | Settings | File Templates.
 */
public class ErrorEvent {

	private final String	error;
	private final ErrorType	errorType;
	private final Throwable	throwable;

	public ErrorEvent(final String error, final ErrorType errorType,
			final Throwable throwable) {
		this.error = error;
		this.errorType = errorType;
		this.throwable = throwable;
	}

	public String getError() {
		return error;
	}

	public ErrorType getErrorType() {
		return errorType;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public enum ErrorType {
		CONNECTION, SUBSCRIPTION, MESSAGE;
	}
}
