package uk.ac.bbsrc.tgac.miso.core.store;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface DeletionStore extends PaginatedDataSource<Deletion> {

  public void delete(Deletable deletable, User user);

}
