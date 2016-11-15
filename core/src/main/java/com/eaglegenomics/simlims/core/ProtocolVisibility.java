package com.eaglegenomics.simlims.core;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Define a range of options for how visible a Protocol is.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public enum ProtocolVisibility {
	/**
	 * Visible to anyone and everyone.
	 */
	EVERYBODY,
	/**
	 * Visible only to internal users. This is useful for testing protocols
	 * before they are made available for public use.
	 */
	INTERNAL_ONLY,
	/**
	 * Does not appear anywhere on the system but still exists. This is the
	 * setting that should be used for obsolete protocols that are no longer
	 * available for use, but still have data related to them sitting around in
	 * the system (e.g. results to be downloaded, or requests still in
	 * progress).
	 */
	NOBODY
}