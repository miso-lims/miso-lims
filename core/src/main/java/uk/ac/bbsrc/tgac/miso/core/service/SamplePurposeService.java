package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

public interface SamplePurposeService extends DeleterService<SamplePurpose>, ListService<SamplePurpose> {

  Long create(SamplePurpose samplePurpose) throws IOException;

  void update(SamplePurpose samplePurpose) throws IOException;

}