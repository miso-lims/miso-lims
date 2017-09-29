package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;

public interface SampleGroupService {

  SampleGroupId get(Long sampleGroupId) throws IOException;

  Long create(SampleGroupId sampleGroup, Long projectId, Long subprojectId) throws IOException;

  void update(SampleGroupId sampleGroup) throws IOException;

  Set<SampleGroupId> getAll() throws IOException;

  Set<SampleGroupId> getAllForProject(Long projectId) throws AuthorizationException, IOException;

  Set<SampleGroupId> getAllForSubproject(Long subprojectId) throws AuthorizationException, IOException;

  void delete(Long sampleGroupId) throws IOException;

}