package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface RequisitionDao extends PaginatedDataSource<Requisition>, SaveDao<Requisition> {

  Requisition getByAlias(String alias) throws IOException;

}
