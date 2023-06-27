package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable;

public interface DeliverableDao extends SaveDao<Deliverable> {

  Deliverable getByName(String name) throws IOException;

  long getUsage(Deliverable deliverable) throws IOException;

  List<Deliverable> listByIdList(Collection<Long> id) throws IOException;
}
