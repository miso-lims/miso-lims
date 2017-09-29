package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

public interface SamplePurposeService {

  SamplePurpose get(Long samplePurposeId) throws IOException;

  Long create(SamplePurpose samplePurpose) throws IOException;

  void update(SamplePurpose samplePurpose) throws IOException;

  Set<SamplePurpose> getAll() throws IOException;

  void delete(Long samplePurposeId) throws IOException;

}