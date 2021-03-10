package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetCategory;
import uk.ac.bbsrc.tgac.miso.persistence.WorksetCategoryDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateWorksetCategoryDao extends HibernateSaveDao<WorksetCategory> implements WorksetCategoryDao {

  public HibernateWorksetCategoryDao() {
    super(WorksetCategory.class);
  }

  @Override
  public WorksetCategory getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(WorksetCategory category) throws IOException {
    return getUsageBy(Workset.class, "category", category);
  }

  @Override
  public List<WorksetCategory> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("categoryId", ids);
  }

}
