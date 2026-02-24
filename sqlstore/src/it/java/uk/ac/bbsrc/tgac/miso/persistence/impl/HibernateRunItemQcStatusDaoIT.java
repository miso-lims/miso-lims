package uk.ac.bbsrc.tgac.miso.persistence.impl;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;

public class HibernateRunItemQcStatusDaoIT
    extends AbstractHibernateSaveDaoTest<RunItemQcStatus, HibernateRunItemQcStatusDao> {

  public HibernateRunItemQcStatusDaoIT() {
    super(RunItemQcStatus.class, 3L, 3);
  }

  @Override
  public HibernateRunItemQcStatusDao constructTestSubject() {
    HibernateRunItemQcStatusDao sut = new HibernateRunItemQcStatusDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public RunItemQcStatus getCreateItem() {
    RunItemQcStatus status = new RunItemQcStatus();
    status.setDescription("New Status");
    status.setQcPassed(true);
    return status;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<RunItemQcStatus, String> getUpdateParams() {
    return new UpdateParameters<>(3L, RunItemQcStatus::getDescription, RunItemQcStatus::setDescription,
        "Changed");
  }

}
