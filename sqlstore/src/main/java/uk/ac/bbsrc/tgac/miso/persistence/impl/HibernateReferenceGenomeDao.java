package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.persistence.ReferenceGenomeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateReferenceGenomeDao extends HibernateSaveDao<ReferenceGenome> implements ReferenceGenomeDao {

  public HibernateReferenceGenomeDao() {
    super(ReferenceGenome.class, ReferenceGenomeImpl.class);
  }

  @Override
  public ReferenceGenome getByAlias(String alias) {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(ReferenceGenome reference) {
    return getUsageBy(ProjectImpl.class, "referenceGenome", reference);
  }

  @Override
  public List<ReferenceGenome> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("referenceGenomeId", idList);
  }
}
