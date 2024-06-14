package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType_;
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
    return getBy(LibrarySelectionType_.NAME, name);
  }

  @Override
  public long getUsageByLibraries(LibrarySelectionType type) throws IOException {
    return getUsageBy(LibraryImpl.class, LibraryImpl_.LIBRARY_SELECTION_TYPE, type);
  }

  @Override
  public long getUsageByLibraryDesigns(LibrarySelectionType type) throws IOException {
    return getUsageBy(LibraryDesign.class, LibraryDesign_.LIBRARY_SELECTION_TYPE, type);
  }


  @Override
  public List<LibrarySelectionType> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(LibrarySelectionType_.LIBRARY_SELECTION_TYPE_ID, idList);
  }

}
