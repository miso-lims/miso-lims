package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface RequisitionDao extends PaginatedDataSource<Requisition>, SaveDao<Requisition> {

  Requisition getByAlias(String alias) throws IOException;

  List<Requisition> listByIdList(Collection<Long> ids) throws IOException;

  RequisitionSupplementalSample getSupplementalSample(Requisition requisition, Sample sample) throws IOException;

  void saveSupplementalSample(RequisitionSupplementalSample sample) throws IOException;

  void removeSupplementalSample(RequisitionSupplementalSample sample) throws IOException;

  RequisitionSupplementalLibrary getSupplementalLibrary(Requisition requisition, Library library) throws IOException;

  void saveSupplementalLibrary(RequisitionSupplementalLibrary library) throws IOException;

  void removeSupplementalLibrary(RequisitionSupplementalLibrary library) throws IOException;

}
