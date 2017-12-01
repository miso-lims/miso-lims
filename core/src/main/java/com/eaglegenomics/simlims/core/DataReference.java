package com.eaglegenomics.simlims.core;

import java.io.IOException;

import com.eaglegenomics.simlims.core.store.DataReferenceStore;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Every piece of data that flows through SimLIMS has a unique reference made up
 * of a combination of the request, iteration and various other attributes that
 * can specify exactly where it came from and what it is associated with.
 * <p>
 * Internally, every object has two attributes which actually define the data
 * being referenced - a class name for the object holding the actual data, and a
 * unique reference ID that identifies that object.
 * <p>
 * DataReferences are stored in DataReferenceStore instances which know how to
 * translate the ID and class name into actual objects either for storing or for
 * retrieval, and can wrap them in DataReference references for use in the rest
 * of the system. DataReferenceStores are never referenced directly or even from
 * the DataReference object itself - all this is done via an ActivitySession.
 * <p>
 * DataReference objects do not have a copy of their original data. They only
 * reference it by ID and class for a DataReferenceStore to resolve.
 * DataReference objects should never be created directly - they should always
 * be created via methods on the DataReferenceStore which know how to handle
 * unstored data until it is stored. For that reason the DataReference
 * constructors should never be used anywhere except in DataReferenceStore
 * implementations.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public interface DataReference {
	/**
	 * Use this ID to indicate that a reference has not yet been saved, and
	 * therefore does not yet have a unique ID.
	 */
	public static final long UNSAVED_ID = -1;

	/**
	 * Returns the type of data that this reference is pointing to.
	 */
	public <C> Class<C> getReferenceClass();

	public <C> void setReferenceClass(Class<C> referenceClass);

	/**
	 * Obtain the unique ID of the data this reference points to. This ID in
	 * combination with the value from {@link #getDataClass()} is all that is
	 * required to resolve the reference into an actual object using a
	 * {@link DataReferenceStore}. Defaults to a negative value unless set
	 * otherwise using {@link #setId()}. Negative values imply unstored data,
	 * which become positive when the data has been stored. Defaults to
	 * {@link UNSAVED_ID}.
	 */
	public long getReferenceId();

	public void setReferenceId(long referenceId);

	/**
	 * Asks this data reference to store any unsaved data and set the reference
	 * ID if it is unset. There's an internal reference to the actual store, set
	 * up by the factory, enabling this data to save itself.
	 */
	public void save() throws IOException;

	/**
	 * Asks this data reference to update the data it points to to have the new
	 * specified value. This replaces existing data and the existing reference
	 * ID will now point to this new piece of data. The change is not made
	 * permanent until {@link #store()} has been called.
	 */
	public void update(Object data);

	/**
	 * Loads the specified object from the store and turns it back into a real
	 * object. There's an internal reference to the actual store, set up by the
	 * factory, enabling this data to load itself.
	 */
	public <C> C resolve() throws IOException;
}
