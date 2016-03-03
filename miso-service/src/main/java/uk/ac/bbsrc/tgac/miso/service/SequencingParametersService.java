package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

public interface SequencingParametersService {

  SequencingParameters get(Long sequencingParametersId) throws IOException;

  Long create(SequencingParameters sequencingParameters) throws IOException;

  void update(SequencingParameters sequencingParameters) throws IOException;

  Set<SequencingParameters> getAll() throws IOException;

  void delete(Long sequencingParametersId) throws IOException;
}
