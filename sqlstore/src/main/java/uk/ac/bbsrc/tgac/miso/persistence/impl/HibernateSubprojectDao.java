package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.Join;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSubprojectDao extends HibernateSaveDao<Subproject> implements SubprojectDao {

  public HibernateSubprojectDao() {
    super(Subproject.class, SubprojectImpl.class);
  }

  @Override
  public long getUsage(Subproject subproject) {
    return getUsageBy(DetailedSampleImpl.class, DetailedSampleImpl_.SUBPROJECT, subproject);
  }

  @Override
  public List<Subproject> listByProjectId(Long projectId) {
    QueryBuilder<Subproject, SubprojectImpl> builder =
        new QueryBuilder<>(currentSession(), SubprojectImpl.class, Subproject.class);
    Join<SubprojectImpl, ProjectImpl> parentProject = builder.getJoin(builder.getRoot(), SubprojectImpl_.parentProject);
    builder.addPredicate(builder.getCriteriaBuilder().equal(parentProject.get(ProjectImpl_.id), projectId));
    return builder.getResultList();
  }

  @Override
  public List<Subproject> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("subprojectId", ids);
  }

  @Override
  public Subproject getByProjectAndAlias(Project project, String alias) {
    QueryBuilder<Subproject, SubprojectImpl> builder =
        new QueryBuilder<>(currentSession(), SubprojectImpl.class, Subproject.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SubprojectImpl_.parentProject), project));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SubprojectImpl_.alias), alias));
    return builder.getSingleResultOrNull();
  }

}
