package com.eaglegenomics.simlims.core.store;

import java.io.IOException;

import com.eaglegenomics.simlims.core.DataReference;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * A data store can take class names and IDs from DataReference objects and
 * resolve them to actual useful objects. In reverse it can also take these
 * useful objects and store them back to its backing system, assisting with the
 * generation of DataReference reference objects to take their place.
 * <p>
 * The only place in user code that DataReferenceStore should make an appearance
 * is in Activity implementations.
 * <p>
 * Note that the store provides no methods for directly deleting data. This
 * helps prevent accidental data loss and assists with validation.
 * <p>
 * IOExceptions on methods indicate problems with the backing store.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public interface DataReferenceStore {

	/**
	 * Creates a reference to the specified object.
	 * <p>
	 * The store may check to see if it already has a reference to that same
	 * object. If it does, it returns the existing data reference, otherwise it
	 * creates a new one. However this check is not compulsory and should not be
	 * relied on to occur.
	 * 
	 * @throws IllegalArgumentException
	 *             if the object does not conform to any restrictions that the
	 *             store implementation may impose.
	 */
	public DataReference create(Object data) throws IllegalArgumentException,
			IOException;

	/**
	 * Locates and loads the data reference for the given details. The store may
	 * not check if they are valid - it'll just return a DataReference that
	 * assumes the details are valid. Only when the resolve() method is called
	 * on the DataReference object will you find out if it actually exists.
	 */
	public <C> DataReference get(Long refId, Class<C> refClass);
}
