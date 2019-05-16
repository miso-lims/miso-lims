package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface InstrumentService extends PaginatedDataSource<Instrument>, SaveService<Instrument> {

  Collection<Instrument> listByPlatformType(PlatformType platformType) throws IOException;

  Collection<Instrument> list() throws IOException;

  Instrument getByName(String name) throws IOException;

  Instrument getByUpgradedInstrumentId(long instrumentId) throws IOException;

}
