package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.TargetedSequencingStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTargetedSequencingDao extends HibernateSaveDao<TargetedSequencing>
    implements TargetedSequencingStore, HibernatePaginatedDataSource<TargetedSequencing> {

  private static final String[] SEARCH_PROPERTIES = new String[] { "alias" };

  public HibernateTargetedSequencingDao() {
    super(TargetedSequencing.class);
  }

  @Override
  public Session currentSession() {
    return super.currentSession();
  }

  @Override
  public TargetedSequencing getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public List<TargetedSequencing> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList("targetedSequencingId", idList);
  }

  @Override
  public long getUsage(TargetedSequencing targetedSequencing) throws IOException {
    return getUsageBy(LibraryAliquot.class, "targetedSequencing", targetedSequencing);
  }

  @Override
  public String getFriendlyName() {
    return "Targeted Sequencing";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends TargetedSequencing> getRealClass() {
    return TargetedSequencing.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUser(boolean creator) {
    return null;
  }
}
