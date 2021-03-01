package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.math.BigDecimal;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateQcDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.ContainerQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;

public class HibernateContainerQCDaoIT
    extends AbstractHibernateQcDaoTest<ContainerQC, HibernateContainerQcDao, SequencerPartitionContainer, ContainerQcControlRun> {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  public HibernateContainerQCDaoIT() {
    super(ContainerQC.class, SequencerPartitionContainerImpl.class, ContainerQcControlRun.class, QcTarget.Container, 16L, 1, 1, 7L, 1, 1);
  }

  @Override
  public HibernateContainerQcDao constructTestSubject() {
    return new HibernateContainerQcDao();
  }

  @Override
  protected ContainerQC makeQc(SequencerPartitionContainer entity) {
    ContainerQC qc = new ContainerQC();
    qc.setContainer(entity);
    return qc;
  }

  @Override
  protected QcControlRun makeControlRun(ContainerQC qc) {
    ContainerQcControlRun controlRun = new ContainerQcControlRun();
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
  protected BigDecimal getConcentration(SequencerPartitionContainer entity) {
    return null;
  }

  @Override
  protected void setConcentration(SequencerPartitionContainer entity, BigDecimal concentration) {
    // Do nothing
  }


}
