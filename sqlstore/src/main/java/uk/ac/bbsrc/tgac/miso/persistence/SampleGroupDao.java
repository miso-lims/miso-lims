package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;

public interface SampleGroupDao {

  List<SampleGroupId> getSampleGroupId();

  SampleGroupId getSampleGroupId(Long id);

  Long addSampleGroupId(SampleGroupId sampleGroup);

  void deleteSampleGroupId(SampleGroupId sampleGroup);

  void update(SampleGroupId sampleGroup);

}