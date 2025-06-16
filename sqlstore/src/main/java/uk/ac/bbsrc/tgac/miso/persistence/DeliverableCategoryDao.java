package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;

public interface DeliverableCategoryDao extends SaveDao<DeliverableCategory> {

  DeliverableCategory getByName(String name) throws IOException;

  long getUsage(DeliverableCategory category) throws IOException;

  List<DeliverableCategory> listByIdList(Collection<Long> ids) throws IOException;

}
