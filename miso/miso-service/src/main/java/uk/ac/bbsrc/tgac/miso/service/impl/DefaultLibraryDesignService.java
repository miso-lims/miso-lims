package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.service.LibraryDesignService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryDesignService implements LibraryDesignService {

  @Autowired
  private LibraryDesignDao libraryDesignDao;

  @Override
  public Collection<LibraryDesign> list() throws IOException {
    return libraryDesignDao.getLibraryDesigns();
  }

  @Override
  public Collection<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException {
    return libraryDesignDao.getLibraryDesignByClass(sampleClass);
  }

  @Override
  public LibraryDesign get(long libraryDesignId) throws IOException {
    return libraryDesignDao.getLibraryDesign(libraryDesignId);
  }

  public void setLibraryDesignDao(LibraryDesignDao libraryDesignDao) {
    this.libraryDesignDao = libraryDesignDao;
  }

}
