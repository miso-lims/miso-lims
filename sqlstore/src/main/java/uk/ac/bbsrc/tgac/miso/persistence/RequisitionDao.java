package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface RequisitionDao extends PaginatedDataSource<Requisition>, SaveDao<Requisition> {

  Requisition getByAlias(String alias) throws IOException;

  RequisitionSupplementalSample getSupplementalSample(Requisition requisition, Sample sample) throws IOException;

  void saveSupplementalSample(RequisitionSupplementalSample sample) throws IOException;

  void removeSupplementalSample(RequisitionSupplementalSample sample) throws IOException;

}
