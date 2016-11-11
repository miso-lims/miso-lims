package com.eaglegenomics.simlims.core;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * The basic definition of how an automated Activity will behave, i.e. one that
 * acts entirely independently and has no user interaction. The data count per
 * session controls how many data items the activity will consume unless told
 * otherwise, with the default specified by {@link #DEFAULT_MAX_DATA_COUNT}. The
 * activity won't get run unless {@link #DEFAULT_MIN_DATA_COUNT} items are
 * waiting in the queue, OR the oldest item in the queue exeeds
 * {@link #DEFAULT_MAX_DATA_AGE} hours in age.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public interface AutomatedActivity extends Activity {
	/**
	 * The default maximum number of data items an activity will consume per
	 * execution session.
	 */
	public static final int DEFAULT_MAX_DATA_COUNT = 1;

	/**
	 * The default minimum number of data items an activity will consume per
	 * execution session.
	 */
	public static final int DEFAULT_MIN_DATA_COUNT = 1;

	/**
	 * The default age after which the activity will process data even if the
	 * minimum amount of data in the queue has not been reached. The value is
	 * set in hours.
	 */
	public static final int DEFAULT_MAX_DATA_AGE = 24;

	/**
	 * Set the minimum number of data items this automated activity will consume
	 * per execution session. Defaults to {@link #DEFAULT_MIN_DATA_COUNT}. If
	 * data in the queue is older than {@link #getMaxDataAge()} then this value
	 * is ignored.
	 */
	public void setMinDataCountPerSession(int minDataCountPerSession);

	public int getMinDataCountPerSession();

	/**
	 * Set the maximum number of data items this automated activity will consume
	 * per execution session. Defaults to {@link #DEFAULT_MAX_DATA_COUNT}.
	 */
	public void setMaxDataCountPerSession(int minDataCountPerSession);

	public int getMaxDataCountPerSession();

	/**
	 * Set how old data can get before the minimum data per session value is
	 * ignored. Defaults to {@link #DEFAULT_MAX_DATA_AGE}.
	 * 
	 * @param maxDataAge
	 *            the maximum age in hours of data before it gets processed
	 *            regardless of queue size.
	 */
	public void setMaxDataAge(int maxDataAge);

	public int getMaxDataAge();
}
