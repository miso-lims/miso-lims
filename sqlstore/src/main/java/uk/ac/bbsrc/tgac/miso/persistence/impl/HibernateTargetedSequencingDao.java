package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.TargetedSequencingStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTargetedSequencingDao extends HibernateSaveDao<TargetedSequencing>
    implements TargetedSequencingStore, JpaCriteriaPaginatedDataSource<TargetedSequencing, TargetedSequencing> {

  private static final List<SingularAttribute<TargetedSequencing, String>> SEARCH_PROPERTIES =
      Arrays.asList(TargetedSequencing_.alias);

  public HibernateTargetedSequencingDao() {
    super(TargetedSequencing.class);
  }

  @Override
  public TargetedSequencing getByAlias(String alias) throws IOException {
    return getBy(TargetedSequencing_.alias, alias);
  }

  @Override
  public List<TargetedSequencing> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(TargetedSequencing_.TARGETED_SEQUENCING_ID, idList);
  }

  @Override
  public long getUsage(TargetedSequencing targetedSequencing) throws IOException {
    return getUsageBy(LibraryAliquot.class, LibraryAliquot_.targetedSequencing, targetedSequencing);
  }

  @Override
  public String getFriendlyName() {
    return "Targeted Sequencing";
  }

  @Override
  public SingularAttribute<TargetedSequencing, ?> getIdProperty() {
    return TargetedSequencing_.targetedSequencingId;
  }

  @Override
  public Class<TargetedSequencing> getEntityClass() {
    return TargetedSequencing.class;
  }

  @Override
  public Class<TargetedSequencing> getResultClass() {
    return TargetedSequencing.class;
  }

  @Override
  public List<SingularAttribute<TargetedSequencing, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public SingularAttribute<TargetedSequencing, ?> propertyForDate(DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(Root<TargetedSequencing> root, String original) {
    switch (original) {
      case "id":
        return root.get(TargetedSequencing_.targetedSequencingId);
      default:
        return root.get(original);
    }
  }

  @Override
  public SingularAttribute<TargetedSequencing, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }
}
