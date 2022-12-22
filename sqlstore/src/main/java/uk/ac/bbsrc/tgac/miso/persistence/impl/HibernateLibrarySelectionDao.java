package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.persistence.LibrarySelectionDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateLibrarySelectionDao extends HibernateSaveDao<LibrarySelectionType>
    implements LibrarySelectionDao {

  public HibernateLibrarySelectionDao() {
    super(LibrarySelectionType.class);
  }

  @Override
  public LibrarySelectionType getByName(String name) throws IOException {
    return (LibrarySelectionType) currentSession().createCriteria(LibrarySelectionType.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public long getUsageByLibraries(LibrarySelectionType type) throws IOException {
    return getUsageBy(LibraryImpl.class, "librarySelectionType", type);
  }

  @Override
  public long getUsageByLibraryDesigns(LibrarySelectionType type) throws IOException {
    return getUsageBy(LibraryDesign.class, "librarySelectionType", type);
  }


  @Override
  public List<LibrarySelectionType> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("librarySelectionTypeId", idList);
  }

}
