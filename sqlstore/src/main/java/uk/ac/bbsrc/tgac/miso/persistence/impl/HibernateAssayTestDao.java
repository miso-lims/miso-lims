package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay_;
import uk.ac.bbsrc.tgac.miso.persistence.AssayTestDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateAssayTestDao extends HibernateSaveDao<AssayTest> implements AssayTestDao {

  public HibernateAssayTestDao() {
    super(AssayTest.class);
  }

  @Override
  public AssayTest getByAlias(String alias) throws IOException {
    return getBy(AssayTest_.ALIAS, alias);
  }

  @Override
  public long getUsage(AssayTest test) throws IOException {
    return getUsageInCollection(Assay.class, Assay_.ASSAY_TESTS, test);
  }

  @Override
  public List<AssayTest> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(AssayTest_.TEST_ID, ids);
  }
}
