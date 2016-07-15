package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;

public interface SampleAdditionalInfoDao {

  List<SampleAdditionalInfo> getSampleAdditionalInfo() throws IOException;

  SampleAdditionalInfo getSampleAdditionalInfo(Long id) throws IOException;

  SampleAdditionalInfo getSampleAdditionalInfoBySampleId(Long id) throws IOException;

  void deleteSampleAdditionalInfo(SampleAdditionalInfo sampleAdditionalInfo);

}