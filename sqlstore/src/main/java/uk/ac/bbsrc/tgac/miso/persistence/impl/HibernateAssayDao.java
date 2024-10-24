package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition_;
import uk.ac.bbsrc.tgac.miso.persistence.AssayDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateAssayDao extends HibernateSaveDao<Assay>
    implements AssayDao {

  public HibernateAssayDao() {
    super(Assay.class);
  }

  @Override
  public Assay getByAliasAndVersion(String alias, String version) throws IOException {
    QueryBuilder<Assay, Assay> builder = new QueryBuilder<>(currentSession(), Assay.class, Assay.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Assay_.alias), alias));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Assay_.version), version));
    return builder.getSingleResultOrNull();
  }

  @Override
  public long getUsage(Assay assay) throws IOException {
    return getUsageInCollection(Requisition.class, Requisition_.ASSAYS, assay);
  }

  @Override
  public List<Assay> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Assay_.ASSAY_ID, ids);
  }

  @Override
  public void deleteAssayMetric(AssayMetric metric) throws IOException {
    currentSession().remove(metric);
  }

}
