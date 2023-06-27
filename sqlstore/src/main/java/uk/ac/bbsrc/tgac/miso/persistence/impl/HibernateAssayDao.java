package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
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
    return (Assay) currentSession().createCriteria(getEntityClass())
        .add(Restrictions.eq("alias", alias))
        .add(Restrictions.eq("version", version))
        .uniqueResult();
  }

  @Override
  public long getUsage(Assay assay) throws IOException {
    return getUsageBy(Requisition.class, "assay", assay);
  }

  @Override
  public List<Assay> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("assayId", ids);
  }

  @Override
  public void deleteAssayMetric(AssayMetric metric) throws IOException {
    currentSession().delete(metric);
  }

}
