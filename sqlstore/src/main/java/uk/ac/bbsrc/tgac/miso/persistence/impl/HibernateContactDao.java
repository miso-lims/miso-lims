package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact_;
import uk.ac.bbsrc.tgac.miso.persistence.ContactStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateContactDao extends HibernateSaveDao<Contact> implements ContactStore {

  public HibernateContactDao() {
    super(Contact.class);
  }

  @Override
  public List<Contact> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Contact_.CONTACT_ID, ids);
  }

  @Override
  public List<Contact> listBySearch(String search) throws IOException {
    QueryBuilder<Contact, Contact> builder = new QueryBuilder<>(currentSession(), Contact.class, Contact.class);
    builder.addPredicate(builder.getCriteriaBuilder().like(builder.getRoot().get(Contact_.name), "%" + search + "%"));
    return builder.getResultList();
  }

  @Override
  public Contact getByEmail(String email) throws IOException {
    return getBy(Contact_.EMAIL, email);
  }

}
