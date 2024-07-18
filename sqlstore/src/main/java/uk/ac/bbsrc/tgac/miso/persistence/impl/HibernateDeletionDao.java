package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateDeletionDao implements DeletionStore, JpaCriteriaPaginatedDataSource<Deletion, Deletion> {

  private static final List<SingularAttribute<Deletion, String>> SEARCH_PROPERTIES =
      Arrays.asList(Deletion_.description);

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
  public SingularAttribute<Deletion, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? Deletion_.user : null;
  }

  @Override
  public SingularAttribute<Deletion, ?> propertyForDate(DateType type) {
    return Deletion_.changeTime;
  }

  @Override
  public List<SingularAttribute<Deletion, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Path<?> propertyForSortColumn(Root<Deletion> root, String original) {
    switch (original) {
      case "userName":
        return root.get(Deletion_.user);
      default:
        return root.get(original);
    }
  }

  @Override
  public SingularAttribute<Deletion, ?> getIdProperty() {
    return Deletion_.id;
  }

  @Override
  public Class<Deletion> getEntityClass() {
    return Deletion.class;
  }

  @Override
  public Class<Deletion> getResultClass() {
    return Deletion.class;
  }

}
