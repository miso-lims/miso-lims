package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SampleIndexFamilyDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSampleIndexFamilyDao extends HibernateSaveDao<SampleIndexFamily> implements SampleIndexFamilyDao {

  public HibernateSampleIndexFamilyDao() {
    super(SampleIndexFamily.class);
  }

  @Override
  public SampleIndexFamily getByName(String name) throws IOException {
    return getBy(SampleIndexFamily_.NAME, name);
  }

  @Override
  public long getUsage(SampleIndexFamily family) throws IOException {
    return getUsageBySampleCategory(family, SampleTissueProcessingImpl.class, SampleTissueProcessingImpl_.index)
        + getUsageBySampleCategory(family, SampleStockImpl.class, SampleStockImpl_.index);
  }

  private <T> long getUsageBySampleCategory(SampleIndexFamily family, Class<T> implementationClass,
      SingularAttribute<T, SampleIndex> indexAttribute) throws IOException {
    LongQueryBuilder<T> builder =
        new LongQueryBuilder<>(currentSession(), implementationClass);
    Join<T, SampleIndex> indexJoin =
        builder.getJoin(builder.getRoot(), indexAttribute);
    Join<SampleIndex, SampleIndexFamily> indexFamilyJoin = builder.getJoin(indexJoin, SampleIndex_.family);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(indexFamilyJoin.get(SampleIndexFamily_.INDEX_FAMILY_ID),
            family.getId()));
    return builder.getCount();
  }

}
