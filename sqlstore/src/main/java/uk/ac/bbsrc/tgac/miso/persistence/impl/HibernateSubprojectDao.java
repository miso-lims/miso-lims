package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSubprojectDao extends HibernateSaveDao<Subproject> implements SubprojectDao {

  public HibernateSubprojectDao() {
    super(Subproject.class, SubprojectImpl.class);
  }

  @Override
  public long getUsage(Subproject subproject) {
    return (long) currentSession().createCriteria(DetailedSampleImpl.class)
        .add(Restrictions.eq("subproject", subproject))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

  @Override
  public List<Subproject> listByProjectId(Long projectId) {
    @SuppressWarnings("unchecked")
    List<Subproject> subprojects = currentSession().createCriteria(SubprojectImpl.class)
        .add(Restrictions.eq("parentProject.id", projectId))
        .list();
    return subprojects;
  }

  @Override
  public List<Subproject> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("subprojectId", ids);
  }

  @Override
  public Subproject getByProjectAndAlias(Project project, String alias) {
    return (Subproject) currentSession().createCriteria(SubprojectImpl.class)
        .add(Restrictions.eq("parentProject", project))
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

}
