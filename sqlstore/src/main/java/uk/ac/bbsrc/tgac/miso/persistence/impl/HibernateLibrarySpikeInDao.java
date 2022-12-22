package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.persistence.LibrarySpikeInDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateLibrarySpikeInDao extends HibernateSaveDao<LibrarySpikeIn> implements LibrarySpikeInDao {

  public HibernateLibrarySpikeInDao() {
    super(LibrarySpikeIn.class);
  }

  @Override
  public LibrarySpikeIn getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public List<LibrarySpikeIn> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("id", idList);
  }

  @Override
  public long getUsage(LibrarySpikeIn spikeIn) throws IOException {
    return getUsageBy(LibraryImpl.class, "spikeIn", spikeIn);
  }

}
