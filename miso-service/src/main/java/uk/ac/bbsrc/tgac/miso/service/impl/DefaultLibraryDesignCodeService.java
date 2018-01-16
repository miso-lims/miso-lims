package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.service.LibraryDesignCodeService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryDesignCodeService implements LibraryDesignCodeService {

  @Autowired
  private LibraryDesignCodeDao libraryDesignCodeDao;

  @Override
  public Collection<LibraryDesignCode> list() throws IOException {
    return libraryDesignCodeDao.getLibraryDesignCodes();
  }

  @Override
  public LibraryDesignCode get(long libraryDesignCodeId) throws IOException {
    return libraryDesignCodeDao.getLibraryDesignCode(libraryDesignCodeId);
  }

  public void setLibraryDesignCodeDao(LibraryDesignCodeDao libraryDesignCodeDao) {
    this.libraryDesignCodeDao = libraryDesignCodeDao;
  }

}
