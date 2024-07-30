package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.persistence.SubmissionStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSubmissionDao extends HibernateSaveDao<Submission> implements SubmissionStore {

  public HibernateSubmissionDao() {
    super(Submission.class);
  }

}
