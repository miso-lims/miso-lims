package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface SequencingParametersService extends ListService<SequencingParameters>, SaveService<SequencingParameters> {

  List<SequencingParameters> listByInstrumentModelId(long instrumentModelId) throws IOException;

}
