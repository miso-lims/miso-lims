package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;

public interface SampleGroupService {

  SampleGroupId get(Long sampleGroupId);

  Long create(SampleGroupId sampleGroup) throws IOException;

  void update(SampleGroupId sampleGroup) throws IOException;

  Set<SampleGroupId> getAll();

  void delete(Long sampleGroupId);

}