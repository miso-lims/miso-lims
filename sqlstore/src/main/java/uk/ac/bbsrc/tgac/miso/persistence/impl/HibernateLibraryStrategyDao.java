package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStrategyDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateLibraryStrategyDao extends HibernateSaveDao<LibraryStrategyType> implements LibraryStrategyDao {

  public HibernateLibraryStrategyDao() {
    super(LibraryStrategyType.class);
  }

  @Override
  public LibraryStrategyType getByName(String name) throws IOException {
    return getBy("name", name);
  }

  @Override
  public long getUsageByLibraries(LibraryStrategyType type) throws IOException {
    return getUsageBy(LibraryImpl.class, "libraryStrategyType", type);
  }

  @Override
  public long getUsageByLibraryDesigns(LibraryStrategyType type) throws IOException {
    return getUsageBy(LibraryDesign.class, "libraryStrategyType", type);
  }

  @Override
  public List<LibraryStrategyType> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("libraryStrategyTypeId", idList);
  }

}
