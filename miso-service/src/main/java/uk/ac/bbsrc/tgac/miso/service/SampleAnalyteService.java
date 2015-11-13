package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;

public interface SampleAnalyteService {

  SampleAnalyte get(Long sampleAnalyteId);

  Long create(SampleAnalyte sampleAnalyte) throws IOException;

  void update(SampleAnalyte sampleAnalyte) throws IOException;

  Set<SampleAnalyte> getAll();

  void delete(Long sampleAnalyteId);

}