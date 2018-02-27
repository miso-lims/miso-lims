package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

public interface SamplePurposeService extends DeleterService<SamplePurpose> {

  Long create(SamplePurpose samplePurpose) throws IOException;

  void update(SamplePurpose samplePurpose) throws IOException;

  Set<SamplePurpose> getAll() throws IOException;

}