package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

public interface SequencingParametersService extends BulkSaveService<SequencingParameters>,
    DeleterService<SequencingParameters>, ListService<SequencingParameters> {

  List<SequencingParameters> listByInstrumentModelId(long instrumentModelId) throws IOException;

}
