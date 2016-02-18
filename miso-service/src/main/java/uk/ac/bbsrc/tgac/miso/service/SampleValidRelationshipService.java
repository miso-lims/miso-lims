package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

public interface SampleValidRelationshipService {

  SampleValidRelationship get(Long sampleValidRelationshipId) throws IOException;

  Long create(SampleValidRelationship sampleValidRelationship, Long parentSampleClassId, Long childSampleClassId) throws IOException;

  void update(SampleValidRelationship sampleValidRelationship, Long parentSampleClassId, Long childSampleClassId) throws IOException;

  Set<SampleValidRelationship> getAll() throws IOException;

  void delete(Long sampleValidRelationshipId) throws IOException;

}