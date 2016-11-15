package com.eaglegenomics.simlims.core;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * DataReferences have priorities. Each priority also has a numeric value - the
 * lower the value, the lower priority.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public enum ActivityDataPriority implements Comparable<ActivityDataPriority> {
	LOW(5), NORMAL(10), HIGH(15);

	private final int level;

	private ActivityDataPriority(int level) {
		this.level = level;
	}

	/**
	 * Obtain the arbitrary level for this priority. This is the numeric value
	 * that corresponds to the enum value. It is useful for storing in
	 * databases.
	 */
	public int getLevel() {
		return level;
	}
}
