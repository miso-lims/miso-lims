package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl_;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateDeletionDao implements DeletionStore, JpaCriteriaPaginatedDataSource<Deletion, Deletion> {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void delete(Deletable deletable, User user) {
    Deletion deletion = new Deletion();
    deletion.setTargetType(deletable.getDeleteType());
    deletion.setTargetId(deletable.getId());
    deletion.setDescription(deletable.getDeleteDescription());
    deletion.setUser(user);
    deletion.setChangeTime(new Date());
    currentSession().remove(deletable);
    currentSession().persist(deletion);
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
  public Path<?> propertyForDate(QueryBuilder<?, Deletion> builder, DateType type) {
    return builder.getRoot().get(Deletion_.changeTime);
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<Deletion> root) {
    return Arrays.asList(root.get(Deletion_.description));
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, Deletion> builder, String original) {
    switch (original) {
      case "userName":
        return builder.getJoin(builder.getRoot(), Deletion_.user).get(UserImpl_.fullName);
      default:
        return builder.getRoot().get(original);
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
