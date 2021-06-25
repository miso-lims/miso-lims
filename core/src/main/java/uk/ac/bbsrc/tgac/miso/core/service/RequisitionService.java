package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface RequisitionService
    extends DeleterService<Requisition>, NoteService<Requisition>, PaginatedDataSource<Requisition>, SaveService<Requisition> {

  Requisition getByAlias(String alias) throws IOException;

}
