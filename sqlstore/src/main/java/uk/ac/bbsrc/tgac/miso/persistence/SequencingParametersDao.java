package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

public interface SequencingParametersDao extends SaveDao<SequencingParameters> {

  public SequencingParameters getByNameAndInstrumentModel(String name, InstrumentModel instrumentModel) throws IOException;

  public List<SequencingParameters> listByInstrumentModel(InstrumentModel instrumentModel) throws IOException;

  public long getUsageByRuns(SequencingParameters sequencingParameters) throws IOException;

  public long getUsageByPoolOrders(SequencingParameters sequencingParameters) throws IOException;

  public long getUsageBySequencingOrders(SequencingParameters sequencingParameters) throws IOException;

}
