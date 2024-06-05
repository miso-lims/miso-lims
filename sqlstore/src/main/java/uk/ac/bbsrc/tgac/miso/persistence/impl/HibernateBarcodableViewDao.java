package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static java.util.stream.Collectors.toList;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView_;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;
import uk.ac.bbsrc.tgac.miso.persistence.BarcodableViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBarcodableViewDao implements BarcodableViewDao {

  private final List<ThrowingFunction<String, BarcodableReference, IOException>> INDIVIDUAL_LOOKUPS = Lists
      .newArrayList(this::getSampleWithBarcode, this::getLibraryWithBarcode, this::getLibraryAliquotWithBarcode,
          this::getPoolWithBarcode,
          this::getBoxWithBarcode, this::getContainerWithBarcode, this::getContainerModelWithBarcode);

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<BarcodableView> searchByBarcode(String barcode, Collection<EntityType> typeFilter) {
    if (barcode == null) {
      throw new IllegalArgumentException("Barcode cannot be null!");
    } else if (typeFilter == null || typeFilter.isEmpty()) {
      throw new IllegalArgumentException("Types must be specified");
    }

    QueryBuilder<BarcodableView, BarcodableView> builder =
        new QueryBuilder<>(currentSession(), BarcodableView.class, BarcodableView.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(BarcodableView_.IDENTIFICATION_BARCODE), barcode));
    List<BarcodableView> results = builder.getResultList();

    Predicate<BarcodableView> matchesTypeFilter =
        barcodableView -> typeFilter.contains(barcodableView.getId().getTargetType());

    return results.stream().filter(matchesTypeFilter).collect(toList());
  }

  @Override
  public List<BarcodableView> searchByAlias(String alias, Collection<EntityType> typeFilter) {
    if (alias == null) {
      throw new IllegalArgumentException("Alias cannot be null!");
    } else if (typeFilter == null || typeFilter.isEmpty()) {
      throw new IllegalArgumentException("Types must be specified");
    }

    QueryBuilder<BarcodableView, BarcodableView> builder =
        new QueryBuilder<>(currentSession(), BarcodableView.class, BarcodableView.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(BarcodableView_.ALIAS), alias));
    List<BarcodableView> results = builder.getResultList();

    Predicate<BarcodableView> matchesTypeFilter =
        barcodableView -> typeFilter.contains(barcodableView.getId().getTargetType());

    return results.stream().filter(matchesTypeFilter).collect(toList());
  }

  @Override
  public List<BarcodableView> search(String query) {
    if (isStringEmptyOrNull(query)) {
      throw new IllegalArgumentException("Search is empty");
    }

    QueryBuilder<BarcodableView, BarcodableView> builder =
        new QueryBuilder<>(currentSession(), BarcodableView.class, BarcodableView.class);
    Root<BarcodableView> root = builder.getRoot();
    builder.addPredicate(builder.getCriteriaBuilder().or(
        builder.getCriteriaBuilder().equal(root.get(BarcodableView_.IDENTIFICATION_BARCODE), query),
        builder.getCriteriaBuilder().equal(root.get(BarcodableView_.ALIAS), query),
        builder.getCriteriaBuilder().equal(root.get(BarcodableView_.NAME), query)));
    List<BarcodableView> results = builder.getResultList();

    return results;
  }

  @Override
  public BarcodableReference checkForExisting(String identificationBarcode) throws IOException {
    if (isStringEmptyOrNull(identificationBarcode))
      throw new IllegalArgumentException("Search is empty");

    for (ThrowingFunction<String, BarcodableReference, IOException> lookup : INDIVIDUAL_LOOKUPS) {
      BarcodableReference ref = lookup.apply(identificationBarcode);
      if (ref != null) {
        return ref;
      }
    }
    return null;
  }

  private BarcodableReference getSampleWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, SampleImpl.class, "Sample", SampleImpl_.SAMPLE_ID,
        SampleImpl_.ALIAS,
        SampleImpl_.NAME);
  }

  private BarcodableReference getLibraryWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, LibraryImpl.class, "Library", LibraryImpl_.LIBRARY_ID,
        LibraryImpl_.ALIAS,
        LibraryImpl_.NAME);
  }

  private BarcodableReference getLibraryAliquotWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, LibraryAliquot.class, "Library aliquot",
        LibraryAliquot_.ALIQUOT_ID, LibraryAliquot_.ALIAS, LibraryAliquot_.NAME);
  }

  private BarcodableReference getPoolWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, PoolImpl.class, "Pool", PoolImpl_.POOL_ID, PoolImpl_.ALIAS,
        PoolImpl_.NAME);
  }

  private BarcodableReference getBoxWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, BoxImpl.class, "Box", BoxImpl_.BOX_ID, BoxImpl_.ALIAS,
        BoxImpl_.NAME);
  }

  private BarcodableReference getContainerWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, SequencerPartitionContainerImpl.class, "Sequencing container",
        SequencerPartitionContainerImpl_.CONTAINER_ID,
        SequencerPartitionContainerImpl_.IDENTIFICATION_BARCODE,
        null);
  }

  private BarcodableReference getContainerModelWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, SequencingContainerModel.class, "Sequencing container model",
        SequencingContainerModel_.SEQUENCING_CONTAINER_MODEL_ID,
        SequencingContainerModel_.ALIAS, null);
  }

  private BarcodableReference getItemWithBarcode(String identificationBarcode, Class<?> implementationClass,
      String entityTypeLabel, String idField,
      String primaryLabelField, String secondaryLabelField) throws IOException {

    QueryBuilder<?, ?> builder = new QueryBuilder<>(currentSession(), implementationClass, Object[].class,
        new BarcodableReference.ResultTransformer(entityTypeLabel));
    Root<?> root = builder.getRoot();
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(root.get("identificationBarcode"), identificationBarcode));

    if (secondaryLabelField != null) {
      builder.setColumns(root.get(idField), root.get(primaryLabelField), root.get(secondaryLabelField));
    } else {
      builder.setColumns(root.get(idField), root.get(primaryLabelField));
    }
    return (BarcodableReference) builder.getSingleResultOrNull();
  }

}
