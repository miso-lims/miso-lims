package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTissueMaterialDao extends HibernateSaveDao<TissueMaterial> implements TissueMaterialDao {

  public HibernateTissueMaterialDao() {
    super(TissueMaterialImpl.class);
  }

  @Override
  public TissueMaterial getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(TissueMaterial tissueMaterial) {
    return getUsageBy(SampleTissueImpl.class, "tissueMaterial", tissueMaterial);
  }

  @Override
  public List<TissueMaterial> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("tissueMaterialId", ids);
  }

}
