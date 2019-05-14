package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface ServiceRecordService extends DeleterService<ServiceRecord>, SaveService<ServiceRecord> {

  public Collection<ServiceRecord> listByInstrument(long instrumentId) throws IOException;

}
