package com.eaglegenomics.simlims.core.exception;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * An exception that describes when an activity fails, and if it is retriable or
 * requeuable. Retriable means try it again immediately as the problem is very
 * short-term/temporary, requeuable means try it again later as the problem
 * might be fixed by then.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public class ActivityFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	private final boolean retry;
	private final boolean requeue;

	public ActivityFailedException(String message, boolean retry,
			boolean requeue) {
		this(message, null, retry, requeue);
	}

	public ActivityFailedException(String message, Throwable cause,
			boolean retry, boolean requeue) {
		super(message);
		if (cause != null) {
			initCause(cause);
		}
		this.retry = retry;
		this.requeue = requeue;
	}

	public ActivityFailedException(Throwable cause, boolean retry,
			boolean requeue) {
		this(cause.getMessage(), cause, retry, requeue);
	}

	public boolean isRetry() {
		return retry;
	}

	public boolean isRequeue() {
		return requeue;
	}
}
