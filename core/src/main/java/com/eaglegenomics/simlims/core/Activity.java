package com.eaglegenomics.simlims.core;

import com.eaglegenomics.simlims.core.exception.ActivityFailedException;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * The basic definition of how an Activity will behave. An Activity is a
 * component in a Protocol. There are two flavours, each with their own
 * interface definition - {@link ManualActivity} and {@link AutomatedActivity}.
 * There are abstract versions which fill in the basic behaviour by default
 * leaving only business logic to be completed by the user.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public interface Activity extends Securable {

	/**
	 * The default number of times an activity will be rerun after its previous
	 * run failed with a retriable failure (see {@link ActivityFailedException}.
	 */
	public static final int DEFAULT_MAX_ATTEMPTS = 3;

	public void setMaxAttempts(int maxInputDataLockAttempts);

	/**
	 * Sets the number of times an activity will get rerun. Should default to
	 * {@link #DEFAULT_MAX_INPUT_LOCK_RETRIES}.
	 */
	public int getMaxAttempts();

	/**
	 * Get the version number of this activity. Activities with the same name
	 * but different version numbers will be treated as separate activities.
	 */
	public int getVersion();

	public void setVersion(int version);

	public String getName();

	public void setName(String name);

	/**
	 * Get the security role name which users must have in order to be able to
	 * work with this activity. If not specified otherwise, it will default to
	 * the unique identifier for this activity.
	 */
	public String getRole();

	public void setRole(String role);

	public String getDescription();

	public void setDescription(String description);

	/**
	 * The unique identifier is a string that uniquely references this activity
	 * by name and version number. Should default to "Name v1" where Name is
	 * getName() and 1 is getVersion().
	 */
	public String getUniqueIdentifier();

	public <C> void setInputDataClass(Class<C> inputDataClass);

	public <C> void setOutputDataClass(Class<C> outputDataClass);

	/**
	 * Gets the Class of data that this activity consumes as input.
	 */
	public <C> Class<C> getInputDataClass();

	/**
	 * Gets the Class of data that this activity produces as output.
	 */
	public <C> Class<C> getOutputDataClass();

	/**
	 * Performs the task that the activity is intended to do. This method can
	 * assume that all checking for security and validity of input has been
	 * done. All output should be stored into the output map provided by the
	 * session. This will have been called from within an
	 * {@link ActivitySession} session which will have handled the security etc.
	 * 
	 * @param activitySession
	 *            the session this activity is being run within. These sessions
	 *            provide resolution facilities for data and will have locked
	 *            all input data before this method is called. Utility methods
	 *            on the session are used to provide results.
	 * @throws ActivityFailedException
	 *             if the activity failed.
	 */
	public void execute(ActivitySession activitySession)
			throws ActivityFailedException;
}
