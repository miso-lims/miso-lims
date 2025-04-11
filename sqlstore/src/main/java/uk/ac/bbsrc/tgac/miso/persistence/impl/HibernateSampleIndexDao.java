package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex_;
import uk.ac.bbsrc.tgac.miso.persistence.SampleIndexDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSampleIndexDao extends HibernateSaveDao<SampleIndex> implements SampleIndexDao {

  public HibernateSampleIndexDao() {
    super(SampleIndex.class);
  }

  @Override
  public SampleIndex getByName(String name) throws IOException {
    return getBy(SampleIndex_.name, name);
  }

  @Override
  public SampleIndex getByFamilyAndName(SampleIndexFamily family, String name) throws IOException {
    QueryBuilder<SampleIndex, SampleIndex> builder =
        new QueryBuilder<>(currentSession(), SampleIndex.class, SampleIndex.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleIndex_.family), family));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleIndex_.name), name));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<SampleIndex> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(SampleIndex_.INDEX_ID, ids);
  }

}
