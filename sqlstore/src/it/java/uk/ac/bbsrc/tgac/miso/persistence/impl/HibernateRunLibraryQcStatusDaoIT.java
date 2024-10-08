package uk.ac.bbsrc.tgac.miso.persistence.impl;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;

public class HibernateRunLibraryQcStatusDaoIT
    extends AbstractHibernateSaveDaoTest<RunLibraryQcStatus, HibernateRunLibraryQcStatusDao> {

  public HibernateRunLibraryQcStatusDaoIT() {
    super(RunLibraryQcStatus.class, 3L, 3);
  }

  @Override
  public HibernateRunLibraryQcStatusDao constructTestSubject() {
    HibernateRunLibraryQcStatusDao sut = new HibernateRunLibraryQcStatusDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public RunLibraryQcStatus getCreateItem() {
    RunLibraryQcStatus status = new RunLibraryQcStatus();
    status.setDescription("New Status");
    status.setQcPassed(true);
    return status;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<RunLibraryQcStatus, String> getUpdateParams() {
    return new UpdateParameters<>(3L, RunLibraryQcStatus::getDescription, RunLibraryQcStatus::setDescription,
        "Changed");
  }

}
