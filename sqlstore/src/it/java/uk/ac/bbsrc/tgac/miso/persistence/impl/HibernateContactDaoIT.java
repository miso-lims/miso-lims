package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;

public class HibernateContactDaoIT extends AbstractHibernateSaveDaoTest<Contact, HibernateContactDao> {

  public HibernateContactDaoIT() {
    super(Contact.class, 1L, 2);
  }

  @Override
  public HibernateContactDao constructTestSubject() {
    HibernateContactDao sut = new HibernateContactDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public Contact getCreateItem() {
    Contact contact = new Contact();
    contact.setName("New Person");
    contact.setEmail("new@example.com");
    return contact;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Contact, String> getUpdateParams() {
    return new UpdateParameters<>(1L, Contact::getName, Contact::setName, "New Name");
  }

  @Test
  public void testListBySearch() throws Exception {
    List<Contact> results = getTestSubject().listBySearch("someone");
    assertEquals(1, results.size());
    assertEquals("Someone", results.get(0).getName());
  }

  @Test
  public void testGetByEmail() throws Exception {
    String email = "everyone@example.com";
    Contact contact = getTestSubject().getByEmail(email);
    assertNotNull(contact);
    assertEquals(email, contact.getEmail());
  }

  @Test
  public void testListByIdList() throws Exception {
    List<Long> ids = Lists.newArrayList(1L, 2L);
    List<Contact> results = getTestSubject().listByIdList(ids);
    assertEquals(2, results.size());
    assertNotNull(results.stream().filter(contact -> contact.getId() == 1L).findFirst().orElse(null));
    assertNotNull(results.stream().filter(contact -> contact.getId() == 2L).findFirst().orElse(null));
  }

}
