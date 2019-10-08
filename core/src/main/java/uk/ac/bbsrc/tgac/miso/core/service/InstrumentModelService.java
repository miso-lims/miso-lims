package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface InstrumentModelService extends DeleterService<InstrumentModel>, ListService<InstrumentModel>,
    PaginatedDataSource<InstrumentModel>, SaveService<InstrumentModel> {

  Set<PlatformType> listActivePlatformTypes() throws IOException;

}
