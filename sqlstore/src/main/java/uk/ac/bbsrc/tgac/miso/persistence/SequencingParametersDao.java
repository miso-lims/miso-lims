package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

public interface SequencingParametersDao {
  List<SequencingParameters> getSequencingParameters() throws IOException;

  SequencingParameters getSequencingParameters(Long id) throws IOException;

  Long addSequencingParameters(SequencingParameters sequencingParameters);

  void deleteSequencingParameters(SequencingParameters sequencingParameters);

  void update(SequencingParameters sequencingParameters);

}
