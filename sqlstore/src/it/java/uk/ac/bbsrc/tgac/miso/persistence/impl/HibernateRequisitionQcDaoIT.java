package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.math.BigDecimal;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateQcDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.RequisitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.RequisitionQcControlRun;

public class HibernateRequisitionQcDaoIT
    extends AbstractHibernateQcDaoTest<RequisitionQC, HibernateRequisitionQcDao, Requisition, RequisitionQcControlRun> {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  public HibernateRequisitionQcDaoIT() {
    super(RequisitionQC.class, Requisition.class, RequisitionQcControlRun.class, QcTarget.Requisition, 17L, 1L, 1L, 8L, 1L, 1L);
  }

  @Override
  public HibernateRequisitionQcDao constructTestSubject() {
    return new HibernateRequisitionQcDao();
  }

  @Override
  protected RequisitionQC makeQc(Requisition entity) {
    RequisitionQC qc = new RequisitionQC();
    qc.setRequisition(entity);
    return qc;
  }

  @Override
  protected QcControlRun makeControlRun(RequisitionQC qc) {
    RequisitionQcControlRun controlRun = new RequisitionQcControlRun();
    controlRun.setQc(qc);
    return controlRun;
  }

  @Override
  public void testUpdateEntity() throws Exception {
    // No valid correspondingfields for containers
    exception.expect(UnsupportedOperationException.class);
    getTestSubject().updateEntity(1L, QcCorrespondingField.CONCENTRATION, new BigDecimal(1), "nM");
  }

  @Override
  protected BigDecimal getConcentration(Requisition entity) {
    return null;
  }

  @Override
  protected void setConcentration(Requisition entity, BigDecimal concentration) {
    // do nothing
  }

}
