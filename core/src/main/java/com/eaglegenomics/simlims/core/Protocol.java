package com.eaglegenomics.simlims.core;

import java.util.Collection;
import java.util.Map;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * The Protocol is core to the entire functionality of SimLIMS. It consists of a
 * map which describes how activities interact with each other - each activity
 * being included via an alias mapping to enable reuse of the same activity at
 * multiple points within the same Protocol.
 * <p>
 * Protocol implementations use different methods for storing the activity maps
 * and associated supporting data. An {@link AbstractProtocol} is provided to
 * give default behaviour for all types of Protocol implementation.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
public interface Protocol extends Securable {
	/**
	 * Get the public version of this protocol. Protocols with different public
	 * versions will appear as separate protocols when users are choosing which
	 * one they want to run.
	 */
	public int getPublicVersion();

	public void setPublicVersion(int publicVersion);

	/**
	 * Get the private version of this protocol. Internally this will cause
	 * separate Protocols to be created for each one where the name and public
	 * version match but the private versions differ. This allows different
	 * internally different but publicly identical protocols to be run in
	 * parallel, e.g. with minor config tweaks that only affect internal
	 * processes and not the way in which the user expects the overall protocol
	 * to behave.
	 */
	public int getPrivateVersion();

	public void setPrivateVersion(int privateVersion);

	/**
	 * Defaults to {@link ProtocolVisibility.EVERYBODY}.
	 * 
	 * @return
	 */
	public ProtocolVisibility getVisibility();

	public void setVisibility(ProtocolVisibility visibility);

	public String getName();

	public void setName(String name);

	/**
	 * Get the security role name which users must have in order to be able to
	 * work with this protocol. If not specified otherwise, it will default to
	 * the unique identifier for this protocol.
	 */
	public String getRole();

	public void setRole(String role);

	public String getDescription();

	public void setDescription(String description);

	/**
	 * Sets the map of aliases for the Activity objects used in this Protocol.
	 * By aliasing them, the same Activity object can be used in multiple
	 * locations with distinct sets of inputs and output mappings.
	 */
	public void setActivityAliasMap(Map<String, Activity> activityAliasMap);

	public Map<String, Activity> getActivityAliasMap();

	/**
	 * Sets the map which describes how this Protocol works. Each key in the map
	 * is a name that refers to keys in the activity map (see
	 * {@link #setActivityAliasMap()}), and the value associated is a list of
	 * other keys from the same activity map, to the corresponding activities of
	 * which the output is passed. An empty list indicates that this is an
	 * endpoint of the Protocol. Note that a Protocol does not have to have an
	 * endpoint, it could be circular and just stop when no more input is
	 * flowing. The startpoint is defined using {@link #setStartpoint(String))}
	 * and endpoints by {@link #setEndpoints(Collection))}.
	 */
	public void setActivityFlowMap(Map<String, String[]> activityReferenceMap);

	public Map<String, String[]> getActivityFlowMap();

	/**
	 * Defines which of the activity objects in this protocol is the starting
	 * point, i.e. the point at which the first input will be fed into in order
	 * to start a request.
	 * 
	 * @param startpoint
	 *            the alias of the startpoint activity. This must correspond to
	 *            an entry in {@link #getActivityAliasMap()}.
	 */
	public void setStartpoint(String startpoint);

	public Collection<String> getEndpoints();

	/**
	 * Defines which of the activity objects in this protocol is the ending
	 * point, i.e. the point at which outputs will be treated as results.
	 * 
	 * @param endpoints
	 *            the aliases of the endpoint activities. This must correspond
	 *            to an entry in {@link #getActivityAliasMap()}.
	 */
	public void setEndpoints(Collection<String> endpoints);

	public String getStartpoint();

	/**
	 * The unique identifier is a string that uniquely references this protocol
	 * by name and version number. Should default to "Name v1.2" where Name is
	 * getName() and 1 is getPublicVersion() and 2 is getPrivateVersion().
	 */
	public String getUniqueIdentifier();
}
