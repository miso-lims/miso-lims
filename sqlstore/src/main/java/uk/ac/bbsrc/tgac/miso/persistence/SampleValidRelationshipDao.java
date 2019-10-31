package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

public interface SampleValidRelationshipDao {

  public List<SampleValidRelationship> getSampleValidRelationship();

  public SampleValidRelationship getSampleValidRelationship(Long id);

  public Long addSampleValidRelationship(SampleValidRelationship sampleValidRelationship);

  public void update(SampleValidRelationship sampleValidRelationship);

  public void delete(SampleValidRelationship sampleValidRelationship) throws IOException;

}