package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryDesignCodeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDesignCodeDao extends HibernateSaveDao<LibraryDesignCode> implements LibraryDesignCodeDao {

  public HibernateLibraryDesignCodeDao() {
    super(LibraryDesignCode.class);
  }

  @Override
  public LibraryDesignCode getByCode(String code) throws IOException {
    return getBy("code", code);
  }

  @Override
  public long getUsageByLibraries(LibraryDesignCode code) throws IOException {
    return getUsageBy(DetailedLibraryImpl.class, "libraryDesignCode", code);
  }

  @Override
  public long getUsageByLibraryDesigns(LibraryDesignCode code) throws IOException {
    return getUsageBy(LibraryDesign.class, "libraryDesignCode", code);
  }

  @Override
  public List<LibraryDesignCode> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("libraryDesignCodeId", idList);
  }

}
