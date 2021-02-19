package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.persistence.StudyTypeDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStudyTypeDao extends HibernateSaveDao<StudyType> implements StudyTypeDao {

  public HibernateStudyTypeDao() {
    super(StudyType.class);
  }

  @Override
  public StudyType getByName(String name) throws IOException {
    return (StudyType) currentSession().createCriteria(StudyType.class)
        .add(Restrictions.eq("name", name))
        .uniqueResult();
  }

  @Override
  public long getUsage(StudyType type) throws IOException {
    return (long) currentSession().createCriteria(StudyImpl.class)
        .add(Restrictions.eq("studyType", type))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
