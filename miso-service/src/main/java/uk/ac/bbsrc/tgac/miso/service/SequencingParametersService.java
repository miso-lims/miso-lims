package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

public interface SequencingParametersService {

  Long create(SequencingParameters sequencingParameters) throws IOException;

  void delete(Long sequencingParametersId) throws IOException;

  SequencingParameters get(Long sequencingParametersId) throws IOException;

  Set<SequencingParameters> getAll() throws IOException;

  Set<SequencingParameters> getForPlatform(Long platformId) throws IOException;

  void update(SequencingParameters sequencingParameters) throws IOException;
}
