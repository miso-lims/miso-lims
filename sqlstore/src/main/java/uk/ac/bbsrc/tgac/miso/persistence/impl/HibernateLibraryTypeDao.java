package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryTypeDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateLibraryTypeDao extends HibernateSaveDao<LibraryType> implements LibraryTypeDao {

  public HibernateLibraryTypeDao() {
    super(LibraryType.class);
  }

  @Override
  public LibraryType getByPlatformAndDescription(PlatformType platform, String description) throws IOException {
    return (LibraryType) currentSession().createCriteria(LibraryType.class)
        .add(Restrictions.eq("platformType", platform))
        .add(Restrictions.eq("description", description))
        .uniqueResult();
  }

  @Override
  public List<LibraryType> listByPlatform(PlatformType platform) throws IOException {
    @SuppressWarnings("unchecked")
    List<LibraryType> results = currentSession().createCriteria(LibraryType.class)
        .add(Restrictions.eq("platformType", platform))
        .list();
    return results;
  }

  @Override
  public List<LibraryType> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("libraryTypeId", idList);
  }

  @Override
  public long getUsageByLibraries(LibraryType type) throws IOException {
    return getUsageBy(LibraryImpl.class, "libraryType", type);
  }

  @Override
  public long getUsageByLibraryTemplates(LibraryType type) throws IOException {
    return getUsageBy(LibraryTemplate.class, "libraryType", type);
  }

}
