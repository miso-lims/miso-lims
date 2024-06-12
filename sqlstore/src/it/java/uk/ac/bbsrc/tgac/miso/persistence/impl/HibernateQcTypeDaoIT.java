package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public class HibernateQcTypeDaoIT extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateQcTypeDao dao;

  @Before
  public void setup() throws IOException {
    dao = new HibernateQcTypeDao();
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testList() throws IOException {
    Collection<QcType> qcTypes = dao.list();
    assertNotNull(qcTypes);
    assertEquals(15, qcTypes.size());
  }

  @Test
  public void testListByNameAndTarget() throws Exception {
    String name = "QuBit";
    QcTarget target = QcTarget.Library;
    List<QcType> qcTypes = dao.listByNameAndTarget(name, target);
    assertNotNull(qcTypes);
    assertEquals(1, qcTypes.size());
    assertEquals(name, qcTypes.get(0).getName());
    assertEquals(target, qcTypes.get(0).getQcTarget());
  }

  @Test
  public void testGet() throws IOException {
    QcType qcType = dao.get(3L);
    assertNotNull(qcType);
    assertEquals("Bioanalyser", qcType.getName());
    assertEquals(QcTarget.Sample, qcType.getQcTarget());
  }

  @Test
  public void testGetNone() throws IOException {
    QcType qcType = dao.get(9999L);
    assertNull(qcType);
  }

  @Test
  public void testCreate() throws Exception {
    QcType qcType = new QcType();
    qcType.setName("Thing Check");
    qcType.setPrecisionAfterDecimal(2);
    qcType.setQcTarget(QcTarget.Sample);
    qcType.setUnits("whatsits");
    qcType.setCorrespondingField(QcCorrespondingField.NONE);
    long savedId = dao.create(qcType);

    clearSession();

    QcType saved = (QcType) currentSession().get(QcType.class, savedId);
    assertNotNull(saved);
    assertEquals(qcType.getName(), saved.getName());
  }

  @Test
  public void testUpdate() throws Exception {
    QcType original = (QcType) currentSession().get(QcType.class, 1L);
    assertNotNull(original);
    String newName = "Wild Guess";
    assertNotEquals(newName, original.getName());
    original.setName(newName);
    dao.update(original);

    QcType saved = (QcType) currentSession().get(QcType.class, 1L);
    assertNotNull(saved);
    assertEquals(newName, saved.getName());
  }

  @Test
  public void testGetUsage() throws Exception {
    QcType qcType = (QcType) currentSession().get(QcType.class, 3L);
    assertNotNull(qcType);
    assertEquals(15L, dao.getUsage(qcType));
  }

  @Test
  public void testGetControl() throws Exception {
    QcControl control = dao.getControl(1L);
    assertNotNull(control);
    assertEquals("Control 1", control.getAlias());
  }

  @Test
  public void testCreateControl() throws Exception {
    QcControl control = new QcControl();
    control.setAlias("Shiny New Control");
    QcType qcType = (QcType) currentSession().get(QcType.class, 1L);
    control.setQcType(qcType);
    long savedId = dao.createControl(control);

    clearSession();

    QcControl saved = (QcControl) currentSession().get(QcControl.class, savedId);
    assertNotNull(saved);
    assertEquals(control.getAlias(), saved.getAlias());

    QcType savedQcType = (QcType) currentSession().get(QcType.class, 1L);
    assertNotNull(savedQcType);
    assertEquals(1, savedQcType.getControls().size());
    assertEquals(control.getAlias(), savedQcType.getControls().iterator().next().getAlias());
  }

  @Test
  public void testDeleteControl() throws Exception {
    QcControl control = (QcControl) currentSession().get(QcControl.class, 3L);
    assertNotNull(control);
    dao.deleteControl(control);

    clearSession();

    QcControl deleted = (QcControl) currentSession().get(QcControl.class, 3L);
    assertNull(deleted);
  }

  @Test
  public void testGetControlUsage() throws Exception {
    QcControl control = (QcControl) currentSession().get(QcControl.class, 1L);
    assertNotNull(control);
    assertEquals(2L, dao.getControlUsage(control));
  }

  @Test
  public void testGetKitUsage() throws Exception {
    QcType qcType = (QcType) currentSession().get(QcType.class, 7L);
    KitDescriptor kit = (KitDescriptor) currentSession().get(KitDescriptor.class, 3L);
    assertNotNull(qcType);
    assertNotNull(kit);
    assertEquals(1L, dao.getKitUsage(qcType, kit));
  }

}
