package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
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
  public List<Path<String>> getSearchProperties(Root<TargetedSequencing> root) {
    return Arrays.asList(root.get(TargetedSequencing_.alias));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, TargetedSequencing> builder, DateType type) {
    return null;
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, TargetedSequencing> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(TargetedSequencing_.targetedSequencingId);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<TargetedSequencing, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }
}
