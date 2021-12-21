package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

public interface SamplePurposeService extends BulkSaveService<SamplePurpose>, DeleterService<SamplePurpose>,
    ListService<SamplePurpose> {

}