package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;

public interface SampleAnalyteService {

  SampleAnalyte get(Long sampleAnalyteId) throws IOException;

  Long create(SampleAnalyte sampleAnalyte, Long sampleId, Long samplePurposeId, Long sampleGroupId, Long tissueMaterialId)
      throws IOException;

  void update(SampleAnalyte sampleAnalyte, Long samplePurposeId, Long sampleGroupId, Long tissueMaterialId) throws IOException;

  Set<SampleAnalyte> getAll() throws IOException;

  void delete(Long sampleAnalyteId) throws IOException;

}