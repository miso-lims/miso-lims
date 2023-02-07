package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.persistence.ContactStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateContactDao extends HibernateSaveDao<Contact> implements ContactStore {

  public HibernateContactDao() {
    super(Contact.class);
  }

  @Override
  public List<Contact> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("contactId", ids);
  }

  @Override
  public List<Contact> listBySearch(String search) throws IOException {
    @SuppressWarnings("unchecked")
    List<Contact> results = currentSession().createCriteria(Contact.class)
        .add(Restrictions.ilike("name", search, MatchMode.ANYWHERE))
        .list();
    return results;
  }

  @Override
  public Contact getByEmail(String email) throws IOException {
    return (Contact) currentSession().createCriteria(Contact.class)
        .add(Restrictions.eq("email", email))
        .uniqueResult();
  }

}
