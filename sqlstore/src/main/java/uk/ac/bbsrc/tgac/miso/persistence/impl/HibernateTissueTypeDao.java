package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTissueTypeDao extends HibernateSaveDao<TissueType> implements TissueTypeDao {

  public HibernateTissueTypeDao() {
    super(TissueTypeImpl.class);
  }

  @Override
  public TissueType getByAlias(String alias) {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(TissueType tissueType) {
    return getUsageBy(SampleTissueImpl.class, "tissueType", tissueType);
  }

  @Override
  public List<TissueType> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("tissueTypeId", ids);
  }

}
