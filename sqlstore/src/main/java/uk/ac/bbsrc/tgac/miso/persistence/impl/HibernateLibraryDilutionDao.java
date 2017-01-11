package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;

public class HibernateLibraryDilutionDao implements LibraryDilutionStore {

  @Autowired
  private SessionFactory sessionFactory;
  @Value("${miso.autoGenerateIdentificationBarcodes:true}")
  private boolean autoGenerateIdentificationBarcodes;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  /**
   * Generates a unique barcode based on the library's name and alias.
   * Note that the barcode will change when the alias is changed.
   *
   * @param dilution
   */
  public void autoGenerateIdBarcode(LibraryDilution dilution) {
    String barcode = dilution.getName() + "::" + dilution.getLibrary().getAlias();
    dilution.setIdentificationBarcode(barcode);
  }

  @Override
  public long save(LibraryDilution dilution) throws IOException {
    long id;
    if (dilution.getId() == AbstractLibrary.UNSAVED_ID) {
      id = (long) currentSession().save(dilution);
    } else {
      currentSession().update(dilution);
      id = dilution.getId();
    }
    return id;
  }

  @Override
  public LibraryDilution get(long id) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LibraryDilution lazyGet(long id) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<LibraryDilution> listAll() throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int count() throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean remove(LibraryDilution t) throws IOException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setNamingScheme(NamingScheme namingScheme) {
    // TODO Auto-generated method stub

  }

  @Override
  public Collection<LibraryDilution> listByLibraryId(long libraryId) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformtype) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchAndPlatform(String query, PlatformType platformType)
      throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchOnly(String query) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectAndPlatform(long projectId, PlatformType platformType)
      throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LibraryDilution getLibraryDilutionByIdAndPlatform(long dilutionId, PlatformType platformType) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<LibraryDilution> listAllWithLimit(long limit) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<LibraryDilution> listBySearchOffsetAndNumResultsAndPlatform(int offset, int limit, String querystr, String sortDir,
      String sortCol, PlatformType platform) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int countByPlatform(PlatformType platform) throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Integer countAllBySearchAndPlatform(String search, PlatformType platform) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
