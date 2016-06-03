package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;

public interface SampleAnalyteDao {

  List<SampleAnalyte> getSampleAnalyte();

  SampleAnalyte getSampleAnalyte(Long id);

  SampleAnalyte getSampleAnalyteBySampleId(Long id);

  void deleteSampleAnalyte(SampleAnalyte sampleAnalyte);

}