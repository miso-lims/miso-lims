package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface InstrumentModelService {

  InstrumentModel get(long instrumentModelId) throws IOException;

  Collection<InstrumentModel> list() throws IOException;

  Set<PlatformType> listActivePlatformTypes() throws IOException;

  Collection<String> listDistinctPlatformTypeNames() throws IOException;

  InstrumentPosition getInstrumentPosition(long positionId) throws IOException;
}
