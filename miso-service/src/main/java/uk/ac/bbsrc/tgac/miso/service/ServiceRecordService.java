package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;

public interface ServiceRecordService extends DeleterService<ServiceRecord> {

  public Collection<ServiceRecord> listByInstrument(long instrumentId) throws IOException;

  public long create(ServiceRecord record) throws IOException;

  public void update(ServiceRecord record) throws IOException;

  public Map<String, Integer> getColumnSizes() throws IOException;

}
