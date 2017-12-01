package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;

public interface SampleValidRelationshipDao {

  List<SampleValidRelationship> getSampleValidRelationship();

  SampleValidRelationship getSampleValidRelationship(Long id);

  Long addSampleValidRelationship(SampleValidRelationship sampleValidRelationship);

  void deleteSampleValidRelationship(SampleValidRelationship sampleValidRelationship);

  void update(SampleValidRelationship sampleValidRelationship);

}