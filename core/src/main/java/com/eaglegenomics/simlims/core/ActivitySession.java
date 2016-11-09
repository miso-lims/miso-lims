package com.eaglegenomics.simlims.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eaglegenomics.simlims.core.ActivityData.Entry;
import com.eaglegenomics.simlims.core.exception.ActivityFailedException;
import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import com.eaglegenomics.simlims.core.store.DataReferenceStore;
import com.eaglegenomics.simlims.core.store.ProtocolStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * An ActivitySession encapsulates the entire process of executing an activity,
 * including obtaining and locking input, releasing input and generating output.
 * The sessions are usually transactional with a single transaction to get the
 * input locked, and another transaction to generate the output.
 * <p>
 * Methods are provided to enable Activity implementations to work with the data
 * store, accounts, and consumable managers provided by this session in order to
 * create or load DataReference instances or perform other accounts or
 * consumables tasks. DataReference created, loaded or updated using the data
 * store during the Activity execution need not be saved - the session will
 * handle that at completion time.
 * <p>
 * This is a class and not an interface because its behaviour is fixed
 * regardless of what the activity does or what backing store or protocol
 * manager is in use.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public abstract class ActivitySession {
	protected static final Logger log = LoggerFactory.getLogger(ActivitySession.class);

	private int attempt = 0;

	private final Collection<ActivityData> lockableInputData = new ArrayList<ActivityData>();
	private final Collection<ActivityData> lockedInputData = new ArrayList<ActivityData>();
	private final Map<Map.Entry<String, ActivityData>, Collection<Map.Entry<String, ActivityData>>> outputData = new HashMap<Map.Entry<String, ActivityData>, Collection<Map.Entry<String, ActivityData>>>();
	private final Collection<ActivityData> skippedData = new ArrayList<ActivityData>();
	private final Map<ActivityData, Throwable> failedData = new HashMap<ActivityData, Throwable>();
	private final Properties properties = new Properties();

	/**
	 * Obtains the data store for creating/loading data.
	 */
	public abstract DataReferenceStore getDataReferenceStore();

	/**
	 * Obtains the protocol manager.
	 */
	public abstract ProtocolManager getProtocolManager();

	/**
	 * Obtains the user for this activity.
	 * 
	 * @return the user.
	 */
	public abstract User getUser();

	/**
	 * Obtains the activity.
	 */
	public abstract Activity getActivity();

	/**
	 * Properties are used to pass input to the execute() method of the
	 * Activity.
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Adds output data to the output map for the activity. Input indexes can
	 * have multiple output indexes, so call this method once for each pair. The
	 * indexes refer to the indexed Entry objects inside the input and output
	 * ActivityData objects. Use {@link ActivityData#NO_INDEX} for no index or
	 * all-object index.
	 */
	public void addOutputData(final ActivityData inputData,
			final String inputIndex, DataReference outputDataRef,
			final String outputIndex) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("Saving output data for input data "
					+ inputData.getUniqueId() + "/"
					+ inputData.getActivityAlias());
		}
		// Convert the input data+key into a single Map.Entry format object.
		Map.Entry<String, ActivityData> inputEntry = new Map.Entry<String, ActivityData>() {
			public String getKey() {
				return inputIndex;
			}

			public ActivityData getValue() {
				return inputData;
			}

			public ActivityData setValue(ActivityData value) {
				throw new UnsupportedOperationException();
			}
		};
		// Loop for each output alias for the input alias. Use RESULT alias if
		// there is no output for the input.
		Entry inputDataEntry = inputData.getIndexedEntries().get(inputIndex);
		Protocol protocol = getProtocolManager().getProtocol(
				inputDataEntry.getRequest().getProtocolUniqueIdentifier());
		String[] outputActivityAliases;
		if (protocol.getEndpoints().contains(inputData.getActivityAlias())) {
			outputActivityAliases = new String[] { ProtocolStore.RESULT_ACTIVITY_ALIAS };
		} else {
			outputActivityAliases = protocol.getActivityFlowMap().get(
					inputData.getActivityAlias());
		}
		if (log.isDebugEnabled()) {
			log.debug("Mapped input data " + inputData.getUniqueId() + "/"
					+ inputData.getActivityAlias() + " to "
					+ outputActivityAliases);
		}
		for (String outputActivityAlias : outputActivityAliases) {
			// Reuse existing data if found.
			ActivityData outputNonFinal = null;
			searchLoop: for (Collection<Map.Entry<String, ActivityData>> topLevelEntry : outputData
					.values()) {
				for (Map.Entry<String, ActivityData> entry : topLevelEntry) {
					ActivityData candidate = entry.getValue();
					if (candidate.getActivityAlias()
							.equals(outputActivityAlias)
							&& candidate.getDataReference().equals(
									outputDataRef)) {
						outputNonFinal = entry.getValue();
						break searchLoop;
					}
				}
			}
			// Create new data if not found.
			if (outputNonFinal == null) {
				if (log.isDebugEnabled()) {
					log.debug("Creating new output data object.");
				}
				outputNonFinal = new BasicActivityData();
				// NOTE: Most fields get default values.
				outputNonFinal.setActivityAlias(outputActivityAlias);
				outputNonFinal.setDataReference(outputDataRef);
				outputNonFinal.setActivity(protocol.getActivityAliasMap().get(
						outputActivityAlias));
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Reusing existing output data object "
							+ outputNonFinal.getUniqueId() + "/"
							+ outputNonFinal.getActivityAlias());
				}
			}
			// Update priority to be most urgent of current and new input.
			if (outputNonFinal.getUniqueId() == ActivityData.UNSAVED_ID
					|| inputData.getPriority().compareTo(
							outputNonFinal.getPriority()) > 0) {
				if (log.isDebugEnabled()) {
					log.debug("Raising priority on output data object "
							+ outputNonFinal.getUniqueId() + "/"
							+ outputNonFinal.getActivityAlias() + " to "
							+ inputData.getPriority());
				}
				outputNonFinal.setPriority(inputData.getPriority());
			}
			final ActivityData outputFinal = outputNonFinal;
			// Create indexed entry in output.
			Entry entry = outputNonFinal.createEntry();
			entry.setExecutionCount(inputData.getIndexedEntries().get(
					inputIndex).getExecutionCount());
			entry.setRequest(inputData.getIndexedEntries().get(inputIndex)
					.getRequest());
			entry.setProtocol(protocol);
			outputNonFinal.getIndexedEntries().put(outputIndex, entry);
			// Update output map.
			if (!outputData.containsKey(inputEntry)) {
				if (log.isDebugEnabled()) {
					log.debug("Reusing entry on output data object "
							+ outputNonFinal.getUniqueId() + "/"
							+ outputNonFinal.getActivityAlias() + " entry "
							+ outputIndex);
				}
				outputData.put(inputEntry,
						new ArrayList<Map.Entry<String, ActivityData>>());
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Created new entry on output data object "
							+ outputNonFinal.getUniqueId() + "/"
							+ outputNonFinal.getActivityAlias() + " entry "
							+ outputIndex);
				}
			}
			Collection<Map.Entry<String, ActivityData>> outputEntries = outputData
					.get(inputEntry);
			outputEntries.add(new Map.Entry<String, ActivityData>() {
				public String getKey() {
					return outputIndex;
				}

				public ActivityData getValue() {
					return outputFinal;
				}

				public ActivityData setValue(ActivityData value) {
					throw new UnsupportedOperationException();
				}
			});
		}
	}

	/**
	 * Mark input data as failed.
	 */
	public void addFailedData(ActivityData failedData, Throwable t) {
		this.failedData.put(failedData, t);
	}

	/**
	 * Mark input data as skipped.
	 */
	public void addSkippedData(ActivityData skippedData) {
		this.skippedData.add(skippedData);
	}

	/**
	 * Obtain an iterator over all the available lockable input data that
	 * matches the given filter. Note that this is a caching method - it will
	 * return the same results on subsequent calls even if the actual available
	 * data has changed. To invalidate the cache, use the
	 * {@link #invalidateLockableInputDataCache()} method.
	 * 
	 * @param filter
	 *            a filter to apply. If no filter is required, pass in
	 *            {@link ActivityDataFilter#FILTER_ACCEPT_ALL}.
	 * @return an iterator over the lockable input data. If there is no data
	 *         available, there will be no elements in the iterator.
	 */
	public Iterator<ActivityData> getLockableInputData(
			final ActivityDataFilter filter) throws SecurityException,
			IOException {
		assert (filter != null);
		if (lockableInputData.isEmpty()) {
			if (log.isDebugEnabled()) {
				log
						.debug("Obtaining new list of lockable input data from protocol manager.");
			}
			lockableInputData.addAll(getProtocolManager().getLockableInputData(
					getUser(), getActivity()));
			if (log.isDebugEnabled()) {
				log.debug("Total lockable input: " + lockableInputData.size());
			}
		}
		return new Iterator<ActivityData>() {
			private Iterator<ActivityData> allLockableInputData = lockableInputData
					.iterator();
			private ActivityData nextInputData = null;

			public boolean hasNext() {
				while (nextInputData == null) {
					if (!allLockableInputData.hasNext()) {
						return false;
					}
					nextInputData = allLockableInputData.next();
					if (!filter.accepts(ActivitySession.this, nextInputData)) {
						if (log.isDebugEnabled()) {
							log.debug("Filter rejected data "
									+ nextInputData.getUniqueId());
						}
						nextInputData = null;
					}
				}
				return true;
			}

			public ActivityData next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				ActivityData currInputData = nextInputData;
				nextInputData = null;
				return currInputData;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Invalidates the cache of lockable input data. This will cause
	 * {@link #getLockableInputData(ActivityDataFilter)} to update its results
	 * next time it is called.
	 */
	public void invalidateLockableInputDataCache() {
		if (log.isDebugEnabled()) {
			log.debug("Invalidating lockable input cache.");
		}
		lockableInputData.clear();
	}

	/**
	 * Request unlocks on the specified input data. This will release all locks
	 * already requested. The data passed in should be a subset of the output
	 * from {@link #getLockableInputData(ActivityDataFilter)}.
	 * 
	 * @param inputData
	 *            the input data to unlock.
	 * @return the input data actually unlocked. This may be a different set
	 *         depending on whether two or more processes are operating in
	 *         parallel on the same activity. It might even be completely empty,
	 *         e.g. if the user credentials don't permit the unlock to happen.
	 */
	public Collection<ActivityData> releaseInputData(
			Collection<ActivityData> inputData) throws IOException {
		assert (inputData != null);
		if (log.isDebugEnabled()) {
			log.debug("Unlocking input data.");
		}
		Collection<ActivityData> unlockedData = getProtocolManager()
				.unlockInputData(getUser(), inputData);
		if (log.isDebugEnabled()) {
			log.debug("Unlocked " + unlockedData.size()
					+ " pieces of input data.");
		}
		lockedInputData.removeAll(unlockedData);
		return unlockedData;
	}

	/**
	 * Request locks on the specified input data. This will release all locks
	 * already requested. The data passed in should be a subset of the output
	 * from {@link #getLockableInputData(ActivityDataFilter)}.
	 * 
	 * @param inputData
	 *            the input data to lock.
	 * @return the input data actually locked. This may be a different set
	 *         depending on whether two or more processes are operating in
	 *         parallel on the same activity. It might even be completely empty.
	 */
	public Collection<ActivityData> lockInputData(
			Collection<ActivityData> inputData) throws SecurityException,
			IOException {
		assert (inputData != null);
		releaseInputDataLocks();
		if (log.isDebugEnabled()) {
			log.debug("Locking input data.");
		}
		lockedInputData.addAll(getProtocolManager().lockInputData(getUser(),
				inputData));
		if (log.isDebugEnabled()) {
			log.debug("Locked " + lockedInputData.size()
					+ " pieces of input data.");
		}
		return lockedInputData;
	}

	/**
	 * Obtain the current set of locked input data as set by
	 * {@link #lockInputData(Collection)}. This set may be empty.
	 */
	public Collection<ActivityData> getLockedInputData() {
		return lockedInputData;
	}

	/**
	 * Check to see if all the input data locks are still held by the specified
	 * user.
	 */
	public boolean validateInputDataLocks() throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("Validating input data locks.");
		}
		return getProtocolManager().validateInputDataLocks(getUser(),
				lockedInputData);
	}

	/**
	 * Releases all input data locks. If any is already unlocked or no longer
	 * exists, no exception is raised and unlocking continues with the
	 * remainder. This method is also called from the session's finaliser, to
	 * make sure that locks don't go sitting round longer than they need to.
	 */
	public void releaseInputDataLocks() throws SecurityException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("Releasing input data locks.");
		}
		getProtocolManager().unlockInputData(getUser(), lockedInputData);
		lockedInputData.clear();
	}

	/**
	 * Releases all locks when called.
	 */
	@Override
	protected void finalize() throws Throwable {
		releaseInputDataLocks();
		super.finalize();
	}

	/**
	 * Execute the activity. If it fails with an ActivityFailedException, it
	 * will retry up to the specified limit in {@link Activity#getMaxAttempts()}
	 * before failing, if the exception says it is retriable. Otherwise it will
	 * not retry. Any exception from the activity, or any exception caused by
	 * checking locks are still in place before and after, will cause all inputs
	 * to be failures regardless of what the activity set them to be.
	 * <p>
	 * This method is best called from within a separate background thread in
	 * the application to prevent it blocking execution whilst the activity
	 * executes.
	 * <p>
	 * Use the various getLockedInput()/addOutputData() etc. in the Activity's
	 * own execute() method to get input and provide output. Use getProperties()
	 * on the session to get additional information to support the processing of
	 * the locked input, e.g. input from a user interface.
	 * 
	 * @return true if there were no failed inputs, false if there was at least
	 *         one.
	 */
	public boolean executeActivity() {
		boolean retry;
		do {
			attempt++;
			retry = false;
			if (log.isInfoEnabled()) {
				log.info("Executing activity "
						+ getActivity().getUniqueIdentifier() + ", attempt "
						+ attempt);
			}
			outputData.clear();
			failedData.clear();
			skippedData.clear();
			try {
				// Make sure we still have the locks before we start.
				validateInputDataLocks();
				// Process the data.
				getActivity().execute(this);
				// Make sure we still have the locks afterwards.
				validateInputDataLocks();
			} catch (ActivityFailedException actFailEx) {
				// Everything failed regardless of what the activity says.
				outputData.clear();
				skippedData.clear();
				failedData.clear();
				// Do we attempt a retry?
				if (actFailEx.isRetry()) {
					if (attempt < getActivity().getMaxAttempts()) {
						if (log.isDebugEnabled()) {
							log
									.debug(
											"Activity failed with retriable exception, retrying.",
											actFailEx);
						}
						retry = true;
					} else {
						if (log.isDebugEnabled()) {
							log
									.debug(
											"Activity retries exceeded, passing over to requeue/fail test.",
											actFailEx);
						}
					}
				}
				if (!retry) {
					if (actFailEx.isRequeue()) {
						// Do nothing - it'll just get unlocked by default and
						// returned to the queue on commit.
						if (log.isDebugEnabled()) {
							log
									.debug(
											"Activity failed with requeueable exception, requeueing.",
											actFailEx);
						}
					} else {
						// Everything failed.
						for (ActivityData inputData : lockedInputData) {
							failedData.put(inputData, actFailEx);
						}
						if (log.isDebugEnabled()) {
							log
									.debug(
											"Activity failed with unrecoverable exception, failing.",
											actFailEx);
						}
					}
				}
			} catch (IOException ioLockEx) {
				// Everything failed regardless of what the activity says.
				outputData.clear();
				skippedData.clear();
				failedData.clear();
				// Everything failed.
				for (ActivityData inputData : lockedInputData) {
					failedData.put(inputData, ioLockEx);
				}
				if (log.isDebugEnabled()) {
					log
							.debug(
									"Activity execution failed with unrecoverable IO exception, failing.",
									ioLockEx);
				}
			}
		} while (retry);
		// Success only if there were no failures.
		if (log.isInfoEnabled()) {
			log.info("Activity completed. Failures?: " + !failedData.isEmpty());
		}
		return !failedData.isEmpty();
	}

	/**
	 * Commits the results. The commit process will call out to the data store
	 * to ensure all referenced data has also been committed.
	 */
	public void finish() throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("Committing activity.");
		}
		// Check we still have our locks. If we don't we need to reprocess.
		validateInputDataLocks();
		// Store output data references in data store.
		if (log.isDebugEnabled()) {
			log.debug("Saving data references.");
		}
		for (Collection<Map.Entry<String, ActivityData>> topLevelResultData : outputData
				.values()) {
			for (Map.Entry<String, ActivityData> resultData : topLevelResultData) {
				resultData.getValue().getDataReference().save();
			}
		}
		// Save output, skipped and failed data.
		if (log.isDebugEnabled()) {
			log.debug("Saving activity outputs/skips/failures.");
		}
		getProtocolManager().saveOutputData(getUser(), outputData);
		getProtocolManager().saveSkippedData(getUser(), skippedData);
		getProtocolManager().saveFailedData(getUser(), failedData);
		// Check we still have locks on any remaining inputs. If we don't we
		// need to reprocess.
		validateInputDataLocks();
		if (log.isDebugEnabled()) {
			log.debug("Committed activity.");
		}
		// Release any remaining locks.
		releaseInputDataLocks();
	}

	/**
	 * Sets off an automated activity. If this is called on a session wrapping
	 * an instance of anything other than an implementation of the
	 * {@link AutomatedActivity} interface, an exception will be raised to
	 * inform the calling code.
	 * <p>
	 * The method handles the grabbing of input data up to a limit specified by
	 * {@link #getDataCountPerSession()} on the activity in the session. It
	 * locks what it can, then executes the activity. Afterwards it cleans up
	 * and releases locks, and stores output mappings.
	 * <p>
	 * This method is best called from within a separate background thread in
	 * the application to prevent it blocking execution whilst the automated
	 * activity executes.
	 */
	public boolean automateActivity(ActivityDataFilter dataFilter)
			throws ActivityFailedException, IOException {
		assert (dataFilter != null);
		if (!(getActivity() instanceof AutomatedActivity)) {
			IllegalArgumentException ex = new IllegalArgumentException(
					"Activity " + getActivity().getUniqueIdentifier()
							+ " is not automated.");
			if (log.isDebugEnabled()) {
				log
						.debug(
								"Attempt to call runAutomatedActivity on a non-automated activity.",
								ex);
			}
			throw ex;
		}
		AutomatedActivity automatedActivity = (AutomatedActivity) getActivity();
		if (log.isInfoEnabled()) {
			log.info("Automating activity "
					+ automatedActivity.getUniqueIdentifier());
		}
		// Find all available data and attempt to lock it.
		Collection<ActivityData> selectedInputData = new ArrayList<ActivityData>();
		if (log.isDebugEnabled()) {
			log.debug("Locking data for "
					+ automatedActivity.getUniqueIdentifier());
		}
		Iterator<ActivityData> lockableInputData = getLockableInputData(dataFilter);
		while (lockableInputData.hasNext()) {
			selectedInputData.add(lockableInputData.next());
		}
		lockInputData(selectedInputData);
		// If we don't have the minimum, or the eldest in the queue is not
		// beyond our threshold, give up (and don't forget to release
		// locks).
		if (!inputDataMeetsThreshold(automatedActivity)) {
			if (log.isInfoEnabled()) {
				log.info("Input didn't reach minimum thresholds for "
						+ automatedActivity.getUniqueIdentifier());
			}
			releaseInputDataLocks();
			return true;
		}
		// Process.
		if (log.isDebugEnabled()) {
			log.debug("Executing " + automatedActivity.getUniqueIdentifier());
		}
		executeActivity(); // If it fails, the failed map will tell us.
		// Commit.
		if (log.isDebugEnabled()) {
			log.debug("Committing " + automatedActivity.getUniqueIdentifier());
		}
		finish();
		// We succeed if there were no failures.
		if (log.isInfoEnabled()) {
			log.info("Overall processing result for "
					+ getActivity().getUniqueIdentifier() + ": succeeded? "
					+ failedData.isEmpty());
		}
		return failedData.isEmpty();
	}

	/**
	 * Check to see if the list of input data specified meets the activity's
	 * thresholds to commence processing. It uses the
	 * {@link AutomatedActivity#getMinDataCountPerSession()} method to see if
	 * there is enough. If there is enough, it returns true. If there isn't, it
	 * checks to see if any of the data is older than the value specified by
	 * {@link AutomatedActivity#getMaxDataAge()}. If it is, it returns true. If
	 * not, it returns false - i.e. don't process anything now.
	 */
	private boolean inputDataMeetsThreshold(AutomatedActivity automatedActivity) {
		if (lockedInputData.size() == 0) {
			return false;
		}
		// The queue is long enough if it meets the minimum size specified by
		// the activity.
		if (lockedInputData.size() >= automatedActivity
				.getMinDataCountPerSession()) {
			return true;
		}
		// The queue is too short, but if anything in it is older than the
		// maximum age given by the activity, process it anyway.
		Date now = new Date();
		for (ActivityData data : lockedInputData) {
			Date dataCreationDate = data.getCreationDate();
			if (now.after(dataCreationDate)) {
				long millisBetween = now.getTime() - dataCreationDate.getTime();
				long hoursBetween = millisBetween / 1000 / 60 / 60;
				if (hoursBetween > automatedActivity.getMaxDataAge()) {
					return true;
				}
			}
		}
		// The queue is too short and nothing in it is too old, therefore don't
		// process it.
		return false;
	}
}
