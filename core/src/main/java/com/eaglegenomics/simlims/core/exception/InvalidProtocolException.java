package com.eaglegenomics.simlims.core.exception;

import com.eaglegenomics.simlims.core.Protocol;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * An exception that describes the problem with a protocol that has been deemed
 * to be invalid.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public class InvalidProtocolException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidProtocolException(String message, Protocol protocol) {
		super(protocol.getUniqueIdentifier() + ": " + message);
	}

}
