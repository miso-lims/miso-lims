package com.eaglegenomics.simlims.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * com.eaglegenomics.simlims.core
 * <p/>
 * A Project interface to allow other code bases to implement it
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Project extends Securable, Serializable {
  Date getCreationDate();

  String getDescription();

  String getName();

  Long getProjectId();

  Collection<Request> getRequests();

  void setCreationDate(Date date);

  void setDescription(String description);

  void setName(String name);

  void setProjectId(Long projectId);

  void setRequests(Collection<Request> requests);
}
