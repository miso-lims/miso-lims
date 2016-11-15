package com.eaglegenomics.simlims.core;

import java.util.Date;
import java.util.Map;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Every piece of data that is queued in SimLIMS has a unique reference made up
 * of a combination of the request, iteration and various other attributes that
 * can specify exactly where it came from and what it is associated with.
 * <p>
 * Internally, each queued piece of data has a DataReference which points to an
 * actual piece of data. All other information is specific only to the queue
 * entry. This interface specifies only the common parts which are found in all
 * queues.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public interface ActivityData extends Securable {
	/**
	 * Use this constant to refer to non-indexed DataReference references, e.g.
	 * where the referenced object is a single entity (a well). Where the
	 * referenced object is multiple entities (e.g. a plate with many wells)
	 * then the indexing refers to the individual entities within it (the wells)
	 * whilst NO_INDEX refers to the whole parent entity (the plate).
	 */
	public static final String NO_INDEX = "__NO_INDEX__";

	/**
	 * The ID for unsaved data.
	 */
	public static final long UNSAVED_ID = -1;

	/**
	 * Internal use only.
	 */
	public long getUniqueId();

	public void setUniqueId(long uniqueId);

	public void setCreationDate(Date creationDate);

	public Date getCreationDate();

	/**
	 * What priority is this data? Defaults to
	 * {@link ActivityDataPriority#NORMAL}.
	 */
	public ActivityDataPriority getPriority();

	public void setPriority(ActivityDataPriority priority);

	public void setDataReference(DataReference dataReference);

	public DataReference getDataReference();

	/**
	 * If the data reference is indexed, this maps the index of the object that
	 * the reference is pointing to to entries in this data instance. It
	 * defaults to an empty set, but as a minimum should contain a single entry
	 * keyed with {@link #NO_INDEX} which refers to the entire DataReference.
	 */
	public Map<String, Entry> getIndexedEntries();

	public void setIndexedEntries(Map<String, Entry> index);

	public void setActivityAlias(String activityAlias);

	/**
	 * Gets the alias of the activity that this piece of input is queued for.
	 */
	public String getActivityAlias();

	public Activity getActivity();

	public void setActivity(Activity activity);

	public Entry createEntry();

	/**
	 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
	 * <p>
	 * Entries within the ActivityData refer to specific indexes within the
	 * ActivityData's DataReference.
	 * 
	 * @author Richard Holland
	 * @since 0.0.1
	 */
	public interface Entry extends Securable {

		public void setRequest(Request request);

		public Request getRequest();

		public Protocol getProtocol();

		public void setProtocol(Protocol protocol);

		public int getExecutionCount();

		/**
		 * Must always be >= 1.
		 */
		public void setExecutionCount(int executionCount);
	}
}
