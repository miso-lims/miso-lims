package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface BulkSaveDao<T> extends SaveDao<T> {

  List<T>  listByIdList(Collection<Long> idList) throws IOException;

}
