package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface RequisitionService
    extends DeleterService<Requisition>, NoteService<Requisition>, PaginatedDataSource<Requisition>,
    SaveService<Requisition> {

  Requisition getByAlias(String alias) throws IOException;

  Requisition moveToRequisition(Requisition requisition, List<Sample> samples) throws IOException;

  void addSupplementalSamples(Requisition requisition, Collection<Sample> samples) throws IOException;

  void removeSupplementalSamples(Requisition requisition, Collection<Sample> samples) throws IOException;

}
