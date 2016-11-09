package com.eaglegenomics.simlims.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Implements all the useful bits that activities need to know about their input
 * and output data. This is in fact a complete implementation, but is abstract
 * in order to allow concrete implementations that alter the behaviour and
 * encourage users not to rely on the base type (in case it changes).
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public abstract class AbstractActivityData implements ActivityData {

	private String activityAlias = null;
	private Date creationDate = new Date();
	private ActivityDataPriority priority = ActivityDataPriority.NORMAL;
	private long uniqueId = ActivityData.UNSAVED_ID;
	private DataReference dataReference = null;
	private Map<String, ActivityData.Entry> indexedEntries = new HashMap<String, ActivityData.Entry>();
	private Activity activity = null;

	/**
	 * The activity that this data is intended for processing by. There is no
	 * default.
	 */
	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Defaults to new Date().
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Defaults to {@link ActivityDataPriority.NORMAL}.
	 */
	public ActivityDataPriority getPriority() {
		return priority;
	}

	public void setPriority(ActivityDataPriority priority) {
		this.priority = priority;
	}

	/**
	 * This is intended for use by the ProtocolStore so that it can identify
	 * individual bits of data. The value is of no importance to the
	 * user.Defaults to {@link ActivityData.UNSAVED_ID}.
	 */
	public long getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(long uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * The bit of data that this ActivityData refers to. The same bit of data
	 * can be used in multiple inputs/outputs. If the data is of a compound
	 * type, i.e. wells on a plate, then the individual Entry objects within the
	 * ActivityData provide an index into that data. There is no default value.
	 */
	public DataReference getDataReference() {
		return dataReference;
	}

	public void setDataReference(DataReference dataReference) {
		assert (dataReference != null);
		this.dataReference = dataReference;
	}

	/**
	 * Users can read this data only if all the entries within it are readable
	 * by the user.
	 */
	public boolean userCanRead(User user) {
		for (Map.Entry<String, ActivityData.Entry> entry : getIndexedEntries()
				.entrySet()) {
			if (!entry.getValue().userCanRead(user)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Users can write this data only if all the entries within it are writeable
	 * by the user.
	 */
	public boolean userCanWrite(User user) {
		for (Map.Entry<String, ActivityData.Entry> entry : getIndexedEntries()
				.entrySet()) {
			if (!entry.getValue().userCanWrite(user)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get hold of the component Entry objects that make up this ActivityData.
	 * Each entry is keyed by an index string that is used to point to a
	 * component part of the DataReference object inside this ActivityData. If
	 * the DataReference object is not compound, or this ActivityData is just
	 * referring to the whole object, then the {@link ActivityData.NO_INDEX}
	 * constant is used as a key. The default is an empty map.
	 */
	public Map<String, ActivityData.Entry> getIndexedEntries() {
		return indexedEntries;
	}

	public void setIndexedEntries(Map<String, ActivityData.Entry> index) {
		this.indexedEntries = index;
	}

	/**
	 * The alias is used to identify separate instances of the same activity
	 * within the same protocol. There is no default value, it must be set
	 * explicitly.
	 */
	public String getActivityAlias() {
		return activityAlias;
	}

	public void setActivityAlias(String activityAlias) {
		this.activityAlias = activityAlias;
	}

	/**
	 * ActivityData objects are identical if they share the same unique
	 * identifier as specified by getUniqueId(). If that has not been set yet,
	 * then they are identical if getDataReference(), getIndexedEntries() and
	 * getActivityAlias() all match exactly.
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof AbstractActivityData))
			return false;
		AbstractActivityData them = (AbstractActivityData) obj;
		// If not saved, then compare resolved actual objects. Otherwise
		// just compare IDs and classes.
		if (getUniqueId() == ActivityData.UNSAVED_ID
				|| them.getUniqueId() == ActivityData.UNSAVED_ID) {
			return this.getDataReference().equals(them.getDataReference())
					&& this.getIndexedEntries()
							.equals(them.getIndexedEntries())
					&& this.getActivityAlias().equals(them.getActivityAlias());
		} else {
			return this.getUniqueId() == them.getUniqueId();
		}
	}

	@Override
	public int hashCode() {
		if (getUniqueId() != ActivityData.UNSAVED_ID) {
			return (int) getUniqueId();
		}
		int hashcode = getDataReference().hashCode();
		hashcode = 37 * hashcode + getIndexedEntries().hashCode();
		hashcode = 37 * hashcode + getActivityAlias().hashCode();
		return hashcode;
	}

	/**
	 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
	 * <p>
	 * Implements all the useful bits that an individual entry within an
	 * ActivityData object needs to know. This is in fact a complete
	 * implementation, but is abstract in order to allow concrete
	 * implementations that alter the behaviour and encourage users not to rely
	 * on the base type (in case it changes).
	 * 
	 * @author Richard Holland
	 * @since 0.0.1
	 */
	public class Entry implements ActivityData.Entry, Securable {
		private Request request = null;
		private int executionCount = 1;
		private Protocol protocol = null;

		public void setProtocol(Protocol protocol) {
			assert (protocol != null);
			this.protocol = protocol;
		}

		/**
		 * There is no default, this must be set explicitly.
		 */
		public Protocol getProtocol() {
			return protocol;
		}

		public Request getRequest() {
			return request;
		}

		/**
		 * Defaults to 1.
		 */
		public int getExecutionCount() {
			return executionCount;
		}

		/**
		 * Execution counts start at 1. 0 is invalid.
		 */
		public void setExecutionCount(int executionCount) {
			if (executionCount <= 0) {
				throw new IllegalArgumentException(
						"ExecutionCount must be >= 1");
			}
			this.executionCount = executionCount;
		}

		public void setRequest(Request request) {
			assert (request != null);
			this.request = request;
		}

		/**
		 * Users can read the entry if they can read the Activity of the parent
		 * ActivityData object, AND they can read the Request of this Entry, OR
		 * they can write this entry.
		 */
		public boolean userCanRead(User user) {
			return (AbstractActivityData.this.activity.userCanRead(user) && getRequest()
					.userCanRead(user))
					|| userCanWrite(user);
		}

		/**
		 * Users can read the entry if they can write the Activity of the parent
		 * ActivityData object, AND they can write the Request of this Entry.
		 */
		public boolean userCanWrite(User user) {
			return AbstractActivityData.this.activity.userCanWrite(user)
					&& getRequest().userCanWrite(user);
		}

		/**
		 * Two entries are equal if they refer to the same request and execution
		 * count.
		 */
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj == this)
				return true;
			if (!(obj instanceof AbstractActivityData.Entry))
				return false;
			AbstractActivityData.Entry them = (AbstractActivityData.Entry) obj;
			return this.getExecutionCount() == them.getExecutionCount()
					&& this.getRequest().equals(them.getRequest());
		}

		@Override
		public int hashCode() {
			int hashcode = getExecutionCount();
			hashcode = 37 * hashcode + getRequest().hashCode();
			return hashcode;
		}
	}
}
