package com.eaglegenomics.simlims.core;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * A basic activity data implementation. Does nothing that the abstract class
 * doesn't do.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public class BasicActivityData extends AbstractActivityData {
	public Entry createEntry() {
		return new Entry();
	}

	public class Entry extends AbstractActivityData.Entry {

	}
}
