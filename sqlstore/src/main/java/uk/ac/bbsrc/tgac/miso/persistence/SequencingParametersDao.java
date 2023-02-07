package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

public interface SequencingParametersDao extends BulkSaveDao<SequencingParameters> {

  SequencingParameters getByNameAndInstrumentModel(String name, InstrumentModel instrumentModel) throws IOException;

  List<SequencingParameters> listByInstrumentModel(InstrumentModel instrumentModel) throws IOException;

  long getUsageByRuns(SequencingParameters sequencingParameters) throws IOException;

  long getUsageByPoolOrders(SequencingParameters sequencingParameters) throws IOException;

  long getUsageBySequencingOrders(SequencingParameters sequencingParameters) throws IOException;

}
