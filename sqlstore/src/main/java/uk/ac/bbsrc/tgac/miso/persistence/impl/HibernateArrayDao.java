package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel_;
import uk.ac.bbsrc.tgac.miso.core.data.Array_;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateArrayDao extends HibernateSaveDao<Array>
    implements ArrayStore, JpaCriteriaPaginatedDataSource<Array, Array> {

  public HibernateArrayDao() {
    super(Array.class);
  }

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public void setDetailedSample(boolean isDetailed) {
    this.detailedSample = isDetailed;
  }

  @Override
  public Array getByAlias(String alias) throws IOException {
    return getBy(Array_.ALIAS, alias);
  }

  @Override
  public Array getBySerialNumber(String serialNumber) throws IOException {
    return getBy(Array_.SERIAL_NUMBER, serialNumber);
  }

  @Override
  public List<Array> listBySampleId(long sampleId) throws IOException {
    QueryBuilder<Array, Array> builder = getQueryBuilder();
    Root<Array> root = builder.getRoot();
    Join<Array, SampleImpl> sampleJoin = builder.getJoin(root, Array_.samples);
    builder.addPredicate(
        builder.getCriteriaBuilder()
            .or(builder.getCriteriaBuilder().equal(sampleJoin.get(SampleImpl_.sampleId), sampleId),
                builder.getCriteriaBuilder().isNull(sampleJoin.get(SampleImpl_.sampleId))));
    return builder.getResultList();
  }

  @Override
  public List<Sample> getArrayableSamplesBySearch(String search) throws IOException {
    if (search == null) {
      throw new NullPointerException("No search String provided");
    }

    QueryBuilder<Sample, SampleImpl> builder = new QueryBuilder<>(currentSession(), SampleImpl.class, Sample.class);
    Root<SampleImpl> root = builder.getRoot();
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().equal(root.get(SampleImpl_.identificationBarcode), search),
        builder.getCriteriaBuilder().equal(root.get(SampleImpl_.name), search),
        builder.getCriteriaBuilder().equal(root.get(SampleImpl_.alias), search)));

    if (detailedSample) {
      Root<DetailedSampleImpl> detailedSampleRoot = builder.getRoot(DetailedSampleImpl.class);
      Join<DetailedSampleImpl, SampleClassImpl> sampleJoin =
          builder.getJoin(detailedSampleRoot, DetailedSampleImpl_.sampleClass);
      builder.addPredicate(builder.getCriteriaBuilder().equal(sampleJoin.get(SampleClassImpl_.sampleCategory),
          SampleAliquot.CATEGORY_NAME));
    }

    builder.addPredicate(builder.getCriteriaBuilder().equal(root.get(SampleImpl_.discarded), false));
    return builder.getResultList();
  }

  @Override
  public List<Array> getArraysBySearch(String search) throws IOException {
    QueryBuilder<Array, Array> builder = getQueryBuilder();
    Root<Array> root = builder.getRoot();
    builder.addPredicate(
        builder.getCriteriaBuilder().or(builder.getCriteriaBuilder().equal(root.get(Array_.serialNumber), search),
            builder.getCriteriaBuilder().equal(root.get(Array_.alias), search)));
    List<Array> results = builder.getResultList();
    return results;
  }

  @Override
  public String getFriendlyName() {
    return "Array";
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<Array> root) {
    return Arrays.asList(root.get(Array_.alias), root.get(Array_.serialNumber), root.get(Array_.description));
  }

  @Override
  public SingularAttribute<Array, ?> getIdProperty() {
    return Array_.id;
  }

  @Override
  public Class<Array> getEntityClass() {
    return Array.class;
  }

  @Override
  public Class<Array> getResultClass() {
    return Array.class;
  }

  @Override
  public Path<?> propertyForDate(Root<Array> root, DateType type) {
    switch (type) {
      case ENTERED:
        return root.get(Array_.creationTime);
      case UPDATE:
        return root.get(Array_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, Array> builder, String original) {
    if ("arrayModelId".equals(original)) {
      Join<Array, ArrayModel> arrayModel = builder.getJoin(builder.getRoot(), Array_.arrayModel);
      return arrayModel.get(ArrayModel_.id);
    } else {
      return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<Array, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? Array_.creator : Array_.lastModifier;
  }

}
