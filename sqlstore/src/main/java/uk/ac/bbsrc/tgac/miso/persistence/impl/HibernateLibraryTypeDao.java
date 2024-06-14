package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate_;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType_;
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
    QueryBuilder<LibraryType, LibraryType> builder =
        new QueryBuilder<>(currentSession(), LibraryType.class, LibraryType.class);
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryType_.platformType), platform));
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryType_.description), description));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<LibraryType> listByPlatform(PlatformType platform) throws IOException {
    QueryBuilder<LibraryType, LibraryType> builder =
        new QueryBuilder<>(currentSession(), LibraryType.class, LibraryType.class);
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryType_.platformType), platform));
    return builder.getResultList();
  }

  @Override
  public List<LibraryType> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(LibraryType_.LIBRARY_TYPE_ID, idList);
  }

  @Override
  public long getUsageByLibraries(LibraryType type) throws IOException {
    return getUsageBy(LibraryImpl.class, LibraryImpl_.LIBRARY_TYPE, type);
  }

  @Override
  public long getUsageByLibraryTemplates(LibraryType type) throws IOException {
    return getUsageBy(LibraryTemplate.class, LibraryTemplate_.LIBRARY_TYPE, type);
  }

}
