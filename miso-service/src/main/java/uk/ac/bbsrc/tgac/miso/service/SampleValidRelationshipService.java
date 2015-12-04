package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

public interface SampleValidRelationshipService {

  SampleValidRelationship get(Long sampleValidRelationshipId);

  Long create(SampleValidRelationship sampleValidRelationship) throws IOException;

  void update(SampleValidRelationship sampleValidRelationship) throws IOException;

  Set<SampleValidRelationship> getAll();

  void delete(Long sampleValidRelationshipId);

}