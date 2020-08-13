package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Collections;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateDeletionDao implements DeletionStore, HibernatePaginatedDataSource<Deletion> {

  private static final String[] SEARCH_PROPERTIES = new String[] { "description" };

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public void delete(Deletable deletable, User user) {
    Deletion deletion = new Deletion();
    deletion.setTargetType(deletable.getDeleteType());
    deletion.setTargetId(deletable.getId());
    deletion.setDescription(deletable.getDeleteDescription());
    deletion.setUser(user);
    deletion.setChangeTime(new Date());
    currentSession().delete(deletable);
    currentSession().save(deletion);
  }

  @Override
  public String getFriendlyName() {
    return "Deletion";
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "user" : null;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return "changeTime";
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public Class<? extends Deletion> getRealClass() {
    return Deletion.class;
  }

}
