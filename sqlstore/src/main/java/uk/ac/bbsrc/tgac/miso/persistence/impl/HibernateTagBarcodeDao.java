package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcodeFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.TagBarcodeStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateTagBarcodeDao implements TagBarcodeStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSubprojectDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public TagBarcode getTagBarcodeById(long id) {
    Query query = currentSession().createQuery("from TagBarcode where id = :id");
    query.setLong("id", id);
    return (TagBarcode) query.uniqueResult();
  }

  @Override
  public List<TagBarcodeFamily> getTagBarcodeFamilies() {
    Query query = currentSession().createQuery("from TagBarcodeFamily");
    @SuppressWarnings("unchecked")
    List<TagBarcodeFamily> list = query.list();
    return list;
  }

  @Override
  public List<TagBarcodeFamily> getTagBarcodeFamiliesByPlatform(PlatformType platformType) {
    Query query = currentSession().createQuery("from TagBarcodeFamily where platformType = :platform");
    query.setParameter("platform", platformType);
    @SuppressWarnings("unchecked")
    List<TagBarcodeFamily> list = query.list();
    return list;
  }

  @Override
  public TagBarcodeFamily getTagBarcodeFamilyByName(String name) {
    Query query = currentSession().createQuery("from TagBarcodeFamily where name = :name");
    query.setString("name", name);
    return (TagBarcodeFamily) query.uniqueResult();
  }

  @Override
  public List<TagBarcode> listAllTagBarcodes(PlatformType platformType) {
    Query query = currentSession().createQuery("from TagBarcode where family.platformType = :platform");
    query.setParameter("platform", platformType);
    @SuppressWarnings("unchecked")
    List<TagBarcode> list = query.list();
    return list;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<TagBarcode> listAllTagBarcodes() {
    Query query = currentSession().createQuery("from TagBarcode");
    @SuppressWarnings("unchecked")
    List<TagBarcode> list = query.list();
    return list;
  }

}
