package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PrinterService extends PaginatedDataSource<Printer>, DeleterService<Printer> {

  public long create(Printer printer) throws IOException;

  public List<Printer> getEnabled() throws IOException;

  public long update(Printer printer) throws IOException;

}
