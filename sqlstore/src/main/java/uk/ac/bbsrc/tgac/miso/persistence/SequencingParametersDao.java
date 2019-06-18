package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersCollection;

public interface SequencingParametersDao extends SequencingParametersCollection {

  List<SequencingParameters> list() throws IOException;

  List<SequencingParameters> listByInstrumentModel(InstrumentModel instrumentModel) throws IOException;

  SequencingParameters get(long id) throws IOException;

  long create(SequencingParameters sequencingParameters);

  long update(SequencingParameters sequencingParameters);

}
