package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

import com.eaglegenomics.simlims.core.User;

public class HibernateInstituteDaoTest extends AbstractDAOTest {
  
  @Autowired
  private HibernateInstituteDao dao;
  
  @Test
  public void testGetInstituteList() {
    List<Institute> list = dao.getInstitute();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testGetSingleInstitute() {
    Institute i = dao.getInstitute(1L);
    assertNotNull(i);
    assertEquals(Long.valueOf(1L), i.getId());
    assertEquals("Institute A", i.getAlias());
    assertEquals("Lab A", i.getLab());
  }
  
  @Test
  public void testGetSingleInstituteNull() {
    Institute i = dao.getInstitute(100L);
    assertNull(i);
  }

  @Test
  public void testAddInstitute() {
    Institute i = makeInstitute("Test Institute", "Test Lab");
    final Long newId = dao.addInstitute(i);
    Institute saved = dao.getInstitute(newId);
    assertEquals(i.getAlias(), saved.getAlias());
    assertEquals(i.getLab(), saved.getLab());
    assertNotNull(i.getCreationDate());
    assertNotNull(i.getLastUpdated());
  }

  @Test
  public void testDeleteInstitute() {
    Institute i = makeInstitute("Test Institute", "Test Lab");
    final Long newId = dao.addInstitute(i);
    Institute saved = dao.getInstitute(newId);
    assertNotNull(saved);
    dao.deleteInstitute(saved);
    assertNull(dao.getInstitute(newId));
  }

  @Test
  public void testUpdate() {
    Institute i = dao.getInstitute(1L);
    final Date oldDate = i.getLastUpdated();
    final String newAlias = "Changed Alias";
    final String newLab = "Changed Lab";
    i.setAlias(newAlias);
    i.setLab(newLab);
    
    dao.update(i);
    Institute updated = dao.getInstitute(1L);
    assertEquals(newAlias, updated.getAlias());
    assertEquals(newLab, updated.getLab());
    assertFalse(oldDate.equals(updated.getLastUpdated()));
  }
  
  private Institute makeInstitute(String alias, String lab) {
    Institute i = new InstituteImpl();
    i.setAlias(alias);
    i.setLab(lab);
    User user = new UserImpl();
    user.setUserId(1L);
    i.setCreatedBy(user);
    i.setUpdatedBy(user);
    Date now = new Date();
    i.setCreationDate(now);
    return i;
  }

}
