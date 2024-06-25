package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PrinterStore extends SaveDao<Printer>, PaginatedDataSource<Printer> {
}
