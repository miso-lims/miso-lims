package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SampleValidRelationshipDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleValidRelationshipDao extends HibernateProviderDao<SampleValidRelationship>
    implements SampleValidRelationshipDao {

  public HibernateSampleValidRelationshipDao() {
    super(SampleValidRelationship.class, SampleValidRelationshipImpl.class);
  }

  @Override
  public SampleValidRelationship getByClasses(SampleClass parent, SampleClass child) throws IOException {
    QueryBuilder<SampleValidRelationship, SampleValidRelationshipImpl> builder =
        new QueryBuilder<>(currentSession(), SampleValidRelationshipImpl.class, SampleValidRelationship.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleValidRelationshipImpl_.parent), parent));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleValidRelationshipImpl_.child), child));
    return builder.getSingleResultOrNull();
  }

  @Override
  public void delete(SampleValidRelationship sampleValidRelationship) throws IOException {
    currentSession().delete(sampleValidRelationship);
  }

}
