package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Group_;
import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer_;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSecurityDao implements SecurityStore {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return getEntityManager().unwrap(Session.class);
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Group getGroupById(Long groupId) throws IOException {
    return (Group) currentSession().get(Group.class, groupId);
  }

  @Override
  public Group getGroupByName(String groupName) throws IOException {
    if (groupName == null)
      throw new NullPointerException("Can not get by null group name");

    QueryBuilder<Group, Group> builder = new QueryBuilder<>(currentSession(), Group.class, Group.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Group_.name), groupName));
    return builder.getSingleResultOrNull();
  }

  @Override
  public User getUserById(Long userId) throws IOException {
    return (User) currentSession().get(UserImpl.class, userId);
  }

  @Override
  public User getUserByLoginName(String loginName) throws IOException {
    QueryBuilder<User, UserImpl> builder = new QueryBuilder<>(currentSession(), UserImpl.class, User.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(UserImpl_.loginName), loginName));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<Group> listAllGroups() throws IOException {
    QueryBuilder<Group, Group> builder = new QueryBuilder<>(currentSession(), Group.class, Group.class);
    return builder.getResultList();
  }

  @Override
  public List<User> listAllUsers() throws IOException {
    QueryBuilder<User, UserImpl> builder = new QueryBuilder<>(currentSession(), UserImpl.class, User.class);
    return builder.getResultList();
  }

  @Override
  public List<User> listUsersBySearch(String search) throws IOException {
    QueryBuilder<User, UserImpl> builder = new QueryBuilder<>(currentSession(), UserImpl.class, User.class);
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().like(builder.getRoot().get(UserImpl_.fullName), '%' + search + '%'),
        builder.getCriteriaBuilder().like(builder.getRoot().get(UserImpl_.loginName), search)));
    return builder.getResultList();
  }

  @Override
  public long saveGroup(Group group) throws IOException {
    long id;
    if (!group.isSaved()) {
      currentSession().persist(group);
      id = group.getId();
    } else {
      id = currentSession().merge(group).getId();
    }
    return id;
  }

  @Override
  public long saveUser(User user) throws IOException {
    long id;
    if (!user.isSaved()) {
      currentSession().persist(user);
      id = user.getId();
    } else {
      id = currentSession().merge(user).getId();
    }
    return id;
  }

  @Override
  public long getUsageByTransfers(Group group) throws IOException {
    LongQueryBuilder<Transfer> builder = new LongQueryBuilder<>(currentSession(), Transfer.class);
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(Transfer_.senderGroup), group),
        builder.getCriteriaBuilder().equal(builder.getRoot().get(Transfer_.recipientGroup), group)));
    return builder.getCount();
  }
}
