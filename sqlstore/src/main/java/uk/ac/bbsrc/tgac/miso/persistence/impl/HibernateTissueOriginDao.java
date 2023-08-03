package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTissueOriginDao extends HibernateSaveDao<TissueOrigin> implements TissueOriginDao {

  public HibernateTissueOriginDao() {
    super(TissueOrigin.class, TissueOriginImpl.class);
  }

  @Override
  public TissueOrigin getByAlias(String alias) {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(TissueOrigin tissueOrigin) {
    return getUsageBy(SampleTissueImpl.class, "tissueOrigin", tissueOrigin);
  }

  @Override
  public List<TissueOrigin> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("tissueOriginId", ids);
  }

}
