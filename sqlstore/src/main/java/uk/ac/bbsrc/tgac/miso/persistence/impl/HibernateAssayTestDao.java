package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.persistence.AssayTestDao;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateAssayTestDao extends HibernateSaveDao<AssayTest> implements AssayTestDao {

  public HibernateAssayTestDao() {
    super(AssayTest.class);
  }

  @Override
  public AssayTest getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(AssayTest test) throws IOException {
    return getUsageBy(Assay.class, "test", test); // TODO: verify this is correct
  }

  @Override
  public List<AssayTest> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("testId", ids);
  }
}
