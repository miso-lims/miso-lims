package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.annotations.VisibleForTesting;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ContactRole;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ProjectContactsAndRole;

public class HibernateContactRoleDaoIT extends AbstractHibernateSaveDaoTest<ContactRole, HibernateContactRoleDao> {

  public HibernateContactRoleDaoIT() {
    super(ContactRole.class, 1L, 4);
  }

  @Override
  public HibernateContactRoleDao constructTestSubject() {
    HibernateContactRoleDao sut = new HibernateContactRoleDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public ContactRole getCreateItem() {
    ContactRole contactRole = new ContactRole();
    contactRole.setName("New Contact role");
    return contactRole;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<ContactRole, String> getUpdateParams() {
    return new UpdateParameters<>(1L, ContactRole::getName, ContactRole::setName, "New name");
  }

  @Test
  public void testGetByName() throws Exception {
    String name = "role1";
    ContactRole contactRole = getTestSubject().getByName(name);
    assertNotNull(contactRole);
    assertEquals(name, contactRole.getName());
  }

  @Test
  public void testGetUsage() throws Exception {
    ProjectContactsAndRole contactRole =
        (ProjectContactsAndRole) currentSession().get(ProjectContactsAndRole.class, 1L);
    assertEquals(1L, getTestSubject().getUsage(contactRole.getContactRole()));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateContactRoleDao::listByIdList, Arrays.asList(1L, 2L));
  }
}
