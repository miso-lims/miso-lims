package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;

public interface SamplePurposeService extends DeleterService<SamplePurpose>, ListService<SamplePurpose> {

  Long create(SamplePurpose samplePurpose) throws IOException;

  void update(SamplePurpose samplePurpose) throws IOException;

}