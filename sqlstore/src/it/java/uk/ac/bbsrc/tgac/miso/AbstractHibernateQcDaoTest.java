package uk.ac.bbsrc.tgac.miso;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateQcStore;

public abstract class AbstractHibernateQcDaoTest<T extends QC, U extends HibernateQcStore<T>, V extends QualityControllable<T>, W extends QcControlRun>
    extends AbstractDAOTest {

  private final Class<T> qcClass;
  private final Class<? extends V> entityClass;
  private final Class<W> controlRunClass;
  private final long entityWithOneQcId;
  private final long existingQcId;
  private final QcTarget qcTarget;
  private final long qcTypeId;
  private final long controlTypeId;
  private final long qcWithControlId;
  private final long qcControlId;

  private U dao;

  public AbstractHibernateQcDaoTest(Class<T> qcClass, Class<? extends V> entityClass, Class<W> controlRunClass,
      QcTarget qcTarget, long qcTypeId,
      long entityWithOneQcId, long existingQcId, long controlTypeId, long qcWithControlId, long qcControlId) {
    this.qcClass = qcClass;
    this.entityClass = entityClass;
    this.controlRunClass = controlRunClass;
    this.entityWithOneQcId = entityWithOneQcId;
    this.existingQcId = existingQcId;
    this.qcTarget = qcTarget;
    this.qcTypeId = qcTypeId;
    this.controlTypeId = controlTypeId;
    this.qcWithControlId = qcWithControlId;
    this.qcControlId = qcControlId;
  }

  @Before
  public void setUp() throws Exception {
    dao = constructTestSubject();
    dao.setSessionFactory(getSessionFactory());
  }

  public abstract U constructTestSubject();

  public U getTestSubject() {
    return dao;
  }

  @Test
  public void testGet() throws IOException {
    T qc = dao.get(qcWithControlId);
    assertNotNull(qc);
    assertEquals(qcWithControlId, qc.getId());
  }

  @Test
  public void testGetEntity() throws Exception {
    QualityControlEntity entity = dao.getEntity(entityWithOneQcId);
    assertEquals(qcTarget, entity.getQcTarget());
    assertEquals(entityWithOneQcId, entity.getId());
  }

  @Test
  public void testListForEntity() throws IOException {
    Collection<T> qcs = dao.listForEntity(entityWithOneQcId);
    assertNotNull(qcs);
    assertEquals(1, qcs.size());
    assertEquals(existingQcId, qcs.iterator().next().getId());
  }

  @Test
  public void testCreate() throws IOException {
    V entity = entityClass.cast(currentSession().get(entityClass, entityWithOneQcId));
    T qc = makeQc(entity);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    QcType qcType = (QcType) currentSession().get(QcType.class, qcTypeId);
    qc.setType(qcType);
    qc.setResults(new BigDecimal("987"));
    qc.setCreator(user);
    qc.setCreationTime(new Date());
    qc.setLastModified(new Date());
    long id = dao.save(qc);

    clearSession();

    T saved = qcClass.cast(currentSession().get(qcClass, id));
    assertNotNull(saved);
    assertEquals(entity.getQcTarget(), saved.getEntity().getQcTarget());
    assertEquals(entity.getId(), saved.getEntity().getId());
    assertEquals(qcType.getId(), saved.getType().getId());
    assertEquals(qc.getResults().compareTo(saved.getResults()), 0);
  }

  protected abstract T makeQc(V entity);

  @Test
  public void testUpdate() throws Exception {
    BigDecimal newValue = new BigDecimal(12345);
    T before = qcClass.cast(currentSession().get(qcClass, qcWithControlId));
    assertNotEquals(0, newValue.compareTo(before.getResults()));

    before.setResults(newValue);
    dao.save(before);

    clearSession();

    T after = qcClass.cast(currentSession().get(qcClass, qcWithControlId));
    assertEquals(0, newValue.compareTo(after.getResults()));
  }

  @Test
  public void testDeleteControlRun() throws Exception {
    QcControlRun control = (QcControlRun) currentSession().get(controlRunClass, qcControlId);
    assertNotNull(control);
    dao.deleteControlRun(control);

    clearSession();

    assertNull(currentSession().get(controlRunClass, qcControlId));
  }

  @Test
  public void testCreateControlRun() throws Exception {
    T qc = qcClass.cast(currentSession().get(qcClass, qcWithControlId));
    QcControlRun controlRun = makeControlRun(qc);
    QcControl control = (QcControl) currentSession().get(QcControl.class, controlTypeId);
    controlRun.setControl(control);
    String lot = "TESTLOT";
    controlRun.setLot(lot);
    controlRun.setQcPassed(true);
    long savedId = dao.createControlRun(controlRun);

    clearSession();

    QcControlRun saved = (QcControlRun) currentSession().get(controlRunClass, savedId);
    assertNotNull(saved);
    assertEquals(lot, saved.getLot());
  }

  protected abstract QcControlRun makeControlRun(T qc);

  @Test
  public void testUpdateControlRun() throws Exception {
    QcControlRun before = (QcControlRun) currentSession().get(controlRunClass, qcControlId);
    boolean originalValue = before.isQcPassed();
    before.setQcPassed(!originalValue);
    dao.updateControlRun(before);

    clearSession();

    QcControlRun after = (QcControlRun) currentSession().get(controlRunClass, qcControlId);
    assertNotEquals(originalValue, after.isQcPassed());
  }

  @Test
  public void testUpdateEntity() throws Exception {
    V before = entityClass.cast(currentSession().get(entityClass, entityWithOneQcId));
    BigDecimal newConcentration = new BigDecimal("23.45");
    BigDecimal beforeConcentration = getConcentration(before);
    if (beforeConcentration != null) {
      assertNotEquals(0, newConcentration.compareTo(beforeConcentration));
    }
    dao.updateEntity(entityWithOneQcId, QcCorrespondingField.CONCENTRATION, newConcentration, "nM");

    clearSession();

    V after = entityClass.cast(currentSession().get(entityClass, entityWithOneQcId));
    assertEquals(0, newConcentration.compareTo(getConcentration(after)));
  }

  protected abstract BigDecimal getConcentration(V entity);

  protected abstract void setConcentration(V entity, BigDecimal concentration);

  @Test
  public void testListByIdList() throws Exception {
    List<Long> ids = Arrays.asList(existingQcId);
    List<T> results = dao.listByIdList(ids);
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals(qcTarget, results.get(0).getEntity().getQcTarget());
    assertEquals(existingQcId, results.get(0).getEntity().getId());
  }

  @Test
  public void testListByIdListNone() throws Exception {
    List<T> results = dao.listByIdList(Collections.emptyList());
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

}
