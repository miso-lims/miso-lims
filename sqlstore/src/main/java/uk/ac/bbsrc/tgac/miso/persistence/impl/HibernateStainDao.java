package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.Stain_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.StainDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStainDao extends HibernateSaveDao<Stain> implements StainDao {

  public HibernateStainDao() {
    super(Stain.class);
  }

  @Override
  public Stain getByName(String name) throws IOException {
    return getBy(Stain_.name, name);
  }

  @Override
  public long getUsage(Stain stain) throws IOException {
    return getUsageBy(SampleSlideImpl.class, SampleSlideImpl_.stain, stain);
  }

  @Override
  public List<Stain> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(Stain_.STAIN_ID, ids);
  }

}
