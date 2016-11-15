package com.eaglegenomics.simlims.core;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Filters are used by {@link ActivitySession} instances when they want to work
 * out if a particular input should be grabbed and locked by an activity for the
 * current execution, or if they should be left on the queue untouched. The
 * default is to have the {@link FILTER_ACCEPT_ALL} filter, however filters can
 * be supplied to the execution methods of the ActivitySession if they are
 * required.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public interface ActivityDataFilter {
	/**
	 * A default implementation that accepts all input passed to it.
	 */
	public static final ActivityDataFilter FILTER_ACCEPT_ALL = new ActivityDataFilter() {
		public boolean accepts(ActivitySession activitySession,
				ActivityData data) {
			return true;
		}
	};

	/**
	 * This simple filter method simply returns a yes/no to whether or not the
	 * specified piece of data should be included in the current activity
	 * session.
	 */
	public boolean accepts(ActivitySession activitySession, ActivityData data);
}
