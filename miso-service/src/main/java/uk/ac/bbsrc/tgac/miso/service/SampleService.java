package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;

public interface SampleService {

  Sample get(Long sampleId) throws IOException;

  Long create(SampleDto sampleDto) throws IOException;

  void update(Sample sample) throws IOException;

  Set<Sample> getAll() throws IOException;

  void delete(Long sampleId) throws IOException;

}