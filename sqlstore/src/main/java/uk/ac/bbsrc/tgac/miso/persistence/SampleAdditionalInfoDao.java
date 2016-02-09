package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;

public interface SampleAdditionalInfoDao {

  List<SampleAdditionalInfo> getSampleAdditionalInfo();

  SampleAdditionalInfo getSampleAdditionalInfo(Long id);

  Long addSampleAdditionalInfo(SampleAdditionalInfo sampleAdditionalInfo);

  void deleteSampleAdditionalInfo(SampleAdditionalInfo sampleAdditionalInfo);

  void update(SampleAdditionalInfo sampleAdditionalInfo);

}