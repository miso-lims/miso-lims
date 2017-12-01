package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

public interface SamplePurposeDao {

  List<SamplePurpose> getSamplePurpose();

  SamplePurpose getSamplePurpose(Long id);

  Long addSamplePurpose(SamplePurpose samplePurpose);

  void deleteSamplePurpose(SamplePurpose samplePurpose);

  void update(SamplePurpose samplePurpose);

}