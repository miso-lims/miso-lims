package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface SampleClassService {

  SampleClass get(Long sampleClassId);

  Long create(SampleClass sampleClass) throws IOException;

  void update(SampleClass sampleClass) throws IOException;

  Set<SampleClass> getAll();

  void delete(Long sampleClassId);

}