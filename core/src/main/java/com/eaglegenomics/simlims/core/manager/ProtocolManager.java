package com.eaglegenomics.simlims.core.manager;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.eaglegenomics.simlims.core.Activity;
import com.eaglegenomics.simlims.core.ActivityData;
import com.eaglegenomics.simlims.core.Protocol;
import com.eaglegenomics.simlims.core.Request;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.exception.InvalidProtocolException;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * The Protocol manager knows how to manage inputs as they flow through the
 * protocol. It can decide what inputs to make available, and to what
 * activities. It also maps inputs to outputs and can track the progress of an
 * individual input through a Protocol.
 * <p>
 * The interface defines basic behaviour. Concrete implementations allow for the
 * manager to be situated locally, or to interact with a remote manager,
 * depending on the application setup.
 * <p>
 * After validating, all protocols should be cached using methods provided by
 * the manager. This allows them to be looked up by name elsewhere. The manager
 * itself does not provide methods for loading or creating protocols.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public interface ProtocolManager {
	/**
	 * Validate a protocol to see if it is logically and structurally valid.
	 * 
	 * @param protocol
	 *            the protocol to validate.
	 * @throws InvalidProtocolException
	 *             if the protocol was invalid, e.g. missing activities, etc.
	 */
	public void validateProtocol(Protocol protocol)
			throws InvalidProtocolException;

	/**
	 * Caches the protocol for future reference. Cache is maintained by unique
	 * reference ID ({@see Protocol#getUniqueIdentifier()}. Protocols should be
	 * validated before being cached.
	 * <p>
	 * Caching a protocol will also cache all activities referenced within the
	 * protocol.
	 * 
	 * @param protocol
	 *            the protocol to cache.
	 */
	public void cacheProtocol(Protocol protocol) throws IOException;

	/**
	 * @return all cached protocols.
	 */
	public Collection<Protocol> listAllProtocols() throws IOException;

	/**
	 * @return all cached activities.
	 */
	public Collection<Activity> listAllActivities() throws IOException;

	/**
	 * Gets a protocol from the cache, or null if it does not exist.
	 */
	public Protocol getProtocol(String protocolUniqueIdentifier)
			throws IOException;

	/**
	 * Gets an activity from the cache, or null if it does not exist.
	 */
	public Activity getActivity(String activityUniqueIdentifier)
			throws IOException;

	/**
	 * Checks to see what data is available for input to the specified activity,
	 * without locking it. The data returned is lockable at the time of query
	 * but may not continue to be lockable.
	 */
	public Collection<ActivityData> getLockableInputData(User user,
			Activity activity) throws SecurityException, IOException;

	/**
	 * @return the data that was actually locked. This may be a subset of input,
	 *         or completely empty, depending on if someone else got there
	 *         first.
	 */
	public Collection<ActivityData> lockInputData(User user,
			Collection<ActivityData> inputData) throws SecurityException,
			IOException;

	/**
	 * @return the data that was actually unlocked. This may be a subset of
	 *         input, or completely empty, depending on if someone else got
	 *         there first.
	 */
	public Collection<ActivityData> unlockInputData(User user,
			Collection<ActivityData> inputData) throws SecurityException,
			IOException;

	/**
	 * @return true if all data is still locked, false if any of it is not
	 *         locked or does not belong to the user.
	 */
	public boolean validateInputDataLocks(User user,
			Collection<ActivityData> inputData) throws IOException;

	/**
	 * Saves the output of this activity and does the necessary queueing for the
	 * next activity, as well as logging the output in the appropriate places.
	 * 
	 * @param outputData
	 *            a map indicating which ActivityData.Entry objects in the input
	 *            map to which ActivityData.Entry objects in the output. The
	 *            keys of the map are input, the values are output. Both key and
	 *            value are split into pairs where the key (a String) is the
	 *            index (use ActivityData.NO_INDEX for unindexed data) and the
	 *            value is the ActivityData the index refers to.
	 */
	public void saveOutputData(
			User user,
			Map<Map.Entry<String, ActivityData>, Collection<Map.Entry<String, ActivityData>>> outputData)
			throws SecurityException, IOException;

	/**
	 * Saves the skipped data of this activity. This logs the action but does
	 * not pass the data onto any further activities.
	 */
	public void saveSkippedData(User user, Collection<ActivityData> skippedData)
			throws SecurityException, IOException;

	/**
	 * Saves the failed data of this activity. This logs the action but does not
	 * pass the data onto any further activities.
	 */
	public void saveFailedData(User user,
			Map<ActivityData, Throwable> failedData) throws SecurityException,
			IOException;

	/**
	 * Takes the data and puts it into the input queue of the specified
	 * activity.
	 */
	public void setupInputData(User user, Collection<ActivityData> inputData)
			throws SecurityException, IOException;

	/**
	 * Gets all available output from the specified execution ID of the request.
	 * 
	 * @return the output. The set may be empty if there is no output.
	 */
	public Collection<ActivityData> getRequestResults(User user,
			Request request, int executionCount) throws SecurityException,
			IOException;

	/**
	 * Check to see if the current (most recently started) execution is
	 * complete.
	 */
	public boolean isCurrentExecutionComplete(User user, Request request)
			throws SecurityException, IOException;

	/**
	 * Gets all activities in the cache that the user can either read or write.
	 */
	public Collection<Activity> listAllAccessibleActivities(User user)
			throws IOException;
}
