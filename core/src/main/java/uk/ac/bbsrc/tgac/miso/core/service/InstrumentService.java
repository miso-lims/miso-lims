package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface InstrumentService extends DeleterService<Instrument>, ListService<Instrument>, PaginatedDataSource<Instrument>,
    SaveService<Instrument> {

  Collection<Instrument> listByPlatformType(PlatformType platformType) throws IOException;

  List<Instrument> listByType(InstrumentType type) throws IOException;

  Instrument getByName(String name) throws IOException;

  Instrument getByUpgradedInstrumentId(long instrumentId) throws IOException;

}
