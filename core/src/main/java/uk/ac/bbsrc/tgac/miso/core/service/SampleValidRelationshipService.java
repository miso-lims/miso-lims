package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

public interface SampleValidRelationshipService {

  public SampleValidRelationship get(Long sampleValidRelationshipId) throws IOException;

  public SampleValidRelationship getByClasses(SampleClass parent, SampleClass child) throws IOException;

  public Set<SampleValidRelationship> getAll() throws IOException;

  public void delete(SampleValidRelationship sampleValidRelationship) throws IOException;

}