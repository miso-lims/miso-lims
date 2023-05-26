package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.time.LocalDate;
import java.time.ZoneId;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;

public class HibernateSubmissionDaoIT extends AbstractHibernateSaveDaoTest<Submission, HibernateSubmissionDao> {

  public HibernateSubmissionDaoIT() {
    super(Submission.class, 1L, 3);
  }

  @Override
  public HibernateSubmissionDao constructTestSubject() {
    HibernateSubmissionDao sut = new HibernateSubmissionDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public Submission getCreateItem() {
    Submission sub = new Submission();
    sub.setAlias("Test Sub");
    sub.setTitle("Test sub");
    sub.setCreationDate(LocalDate.now(ZoneId.systemDefault()));
    sub.setVerified(false);
    sub.setCompleted(false);
    return sub;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Submission, String> getUpdateParams() {
    return new UpdateParameters<>(2L, Submission::getTitle, Submission::setTitle, "Changed");
  }

}
