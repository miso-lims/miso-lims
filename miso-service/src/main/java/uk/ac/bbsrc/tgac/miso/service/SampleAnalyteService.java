package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.dto.SampleAnalyteDto;

public interface SampleAnalyteService {

  SampleAnalyte get(Long sampleAnalyteId) throws IOException;

  Long create(SampleAnalyte sampleAnalyte, Long sampleId, Long samplePurposeId, Long sampleGroupId, Long tissueMaterialId)
      throws IOException;

  Long create(SampleAnalyte sampleAnalyte) throws IOException;

  void update(SampleAnalyte sampleAnalyte, Long samplePurposeId, Long sampleGroupId, Long tissueMaterialId) throws IOException;

  Set<SampleAnalyte> getAll() throws IOException;

  void delete(Long sampleAnalyteId) throws IOException;

  Long create(SampleAnalyteDto sampleAnalyteDto) throws IOException;

  SampleAnalyte to(SampleAnalyteDto sampleAnalyteDto) throws IOException;

}