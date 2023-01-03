package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

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
    return getBy("name", name);
  }

  @Override
  public long getUsage(StudyType type) throws IOException {
    return getUsageBy(StudyImpl.class, "studyType", type);
  }

  @Override
  public List<StudyType> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("typeId", idList);
  }
}
