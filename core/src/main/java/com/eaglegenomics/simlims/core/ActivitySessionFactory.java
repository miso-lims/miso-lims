package com.eaglegenomics.simlims.core;

import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import com.eaglegenomics.simlims.core.store.DataReferenceStore;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * The factory allows ActivitySession objects to be created on demand. Examples
 * may be for creating Hibernate or file-backed data stores to provide object
 * resolution for the session. The data store provided to the factory is the
 * place where these variations are specified. The factory itself is not an
 * interface because it has only a single purpose. The data store provides all
 * the configuration that the factory needs.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public class ActivitySessionFactory {
	private DataReferenceStore dataReferenceStore;
	private ProtocolManager protocolManager;

	/**
	 * Set the ProtocolManager instance which this activity session will be
	 * using in order to obtain inputs and manage input/output mapping. Must be
	 * called before this factory is used otherwise nasty things happen.
	 */
	public void setProtocolManager(ProtocolManager protocolManager) {
		this.protocolManager = protocolManager;
	}

	/**
	 * Sets the data store used to resolve and store DataReference references.
	 * This could be Hibernate, a file, or anything. It has no default value so
	 * this method must be called very early on, or wired up using Spring, in
	 * order to prevent unexpected failures.
	 */
	public void setDataReferenceStore(DataReferenceStore dataReferenceStore) {
		this.dataReferenceStore = dataReferenceStore;
	}

	/**
	 * Creates a session object which encapsulates all activity around a single
	 * instance of an activity. For instance, a single session maps to a single
	 * execution of an automated activity, or the process of displaying,
	 * interacting with and submitting a user interface for a manual activity.
	 * The session handles input selection and output mapping, and enables
	 * conversion from the DataReference objects to real objects that the
	 * activity can use.
	 * 
	 * @param user
	 *            the user credentials to use when running the activity. These
	 *            need to be valid for the entire lifecycle of the activity.
	 * @param activity
	 *            the activity to create a session for.
	 * @return a session object which can be used to interact with this
	 *         activity.
	 */
	public ActivitySession createActivitySession(final User user,
			final Activity activity) {
		return new ActivitySession() {

			public Activity getActivity() {
				return activity;
			}

			public DataReferenceStore getDataReferenceStore() {
				return ActivitySessionFactory.this.dataReferenceStore;
			}

			public ProtocolManager getProtocolManager() {
				return ActivitySessionFactory.this.protocolManager;
			}

			public User getUser() {
				return user;
			}
		};
	}
}
