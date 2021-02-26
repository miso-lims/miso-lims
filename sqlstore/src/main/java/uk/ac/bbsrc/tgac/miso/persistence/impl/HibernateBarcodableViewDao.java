package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static java.util.stream.Collectors.toList;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableView;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;
import uk.ac.bbsrc.tgac.miso.persistence.BarcodableViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateBarcodableViewDao implements BarcodableViewDao {

  private final List<ThrowingFunction<String, BarcodableReference, IOException>> INDIVIDUAL_LOOKUPS = Lists
      .newArrayList(this::getSampleWithBarcode, this::getLibraryWithBarcode, this::getLibraryAliquotWithBarcode, this::getPoolWithBarcode,
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

    @SuppressWarnings("unchecked")
    List<BarcodableView> results = currentSession().createCriteria(BarcodableView.class)
        .add(Restrictions.eq("identificationBarcode", barcode)).list();

    Predicate<BarcodableView> matchesTypeFilter = barcodableView -> typeFilter.contains(barcodableView.getId().getTargetType());

    return results.stream().filter(matchesTypeFilter).collect(toList());
  }

  @Override
  public List<BarcodableView> searchByAlias(String alias, Collection<EntityType> typeFilter) {
    if (alias == null) {
      throw new IllegalArgumentException("Alias cannot be null!");
    } else if (typeFilter == null || typeFilter.isEmpty()) {
      throw new IllegalArgumentException("Types must be specified");
    }

    @SuppressWarnings("unchecked")
    List<BarcodableView> results = currentSession().createCriteria(BarcodableView.class)
        .add(Restrictions.eq("alias", alias)).list();

    Predicate<BarcodableView> matchesTypeFilter = barcodableView -> typeFilter.contains(barcodableView.getId().getTargetType());

    return results.stream().filter(matchesTypeFilter).collect(toList());
  }

  @Override
  public List<BarcodableView> search(String query) {
    if (isStringEmptyOrNull(query)) {
      throw new IllegalArgumentException("Search is empty");
    }

    @SuppressWarnings("unchecked")
    List<BarcodableView> results = currentSession().createCriteria(BarcodableView.class)
        .add(Restrictions.or(
                Restrictions.eq("identificationBarcode", query),
                Restrictions.eq("alias", query),
                Restrictions.eq("name", query)))
        .list();

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
    return getItemWithBarcode(identificationBarcode, SampleImpl.class, "Sample", "alias", "name");
  }

  private BarcodableReference getLibraryWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, LibraryImpl.class, "Library", "alias", "name");
  }

  private BarcodableReference getLibraryAliquotWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, LibraryAliquot.class, "Library aliquot", "alias", "name");
  }

  private BarcodableReference getPoolWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, PoolImpl.class, "Pool", "alias", "name");
  }

  private BarcodableReference getBoxWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, BoxImpl.class, "Box", "alias", "name");
  }

  private BarcodableReference getContainerWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, SequencerPartitionContainerImpl.class, "Sequencing container", "identificationBarcode",
        null);
  }

  private BarcodableReference getContainerModelWithBarcode(String identificationBarcode) throws IOException {
    return getItemWithBarcode(identificationBarcode, SequencingContainerModel.class, "Sequencing container model", "alias", null);
  }

  private BarcodableReference getItemWithBarcode(String identificationBarcode, Class<?> implementationClass, String entityTypeLabel,
      String primaryLabelField, String secondaryLabelField) throws IOException {
    return (BarcodableReference) currentSession().createCriteria(implementationClass)
        .add(Restrictions.eq("identificationBarcode", identificationBarcode))
        .setProjection(BarcodableReference.makeProjectionList(primaryLabelField, secondaryLabelField))
        .setResultTransformer(new BarcodableReference.ResultTransformer(entityTypeLabel))
        .uniqueResult();
  }

}
