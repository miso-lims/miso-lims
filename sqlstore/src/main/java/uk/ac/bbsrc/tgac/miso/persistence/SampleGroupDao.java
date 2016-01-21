package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;

public interface SampleGroupDao {

  List<SampleGroupId> getSampleGroups();

  SampleGroupId getSampleGroup(Long id);

  Long addSampleGroup(SampleGroupId sampleGroup);

  void deleteSampleGroup(SampleGroupId sampleGroup);

  void update(SampleGroupId sampleGroup);

}