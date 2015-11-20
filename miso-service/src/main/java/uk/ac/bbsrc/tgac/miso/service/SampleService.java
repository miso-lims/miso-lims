package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;

public interface SampleService {

  Sample get(Long sampleId);

  Long create(Sample sample) throws IOException;

  void update(Sample sample) throws IOException;

  Set<Sample> getAll();

  void delete(Long sampleId);

}