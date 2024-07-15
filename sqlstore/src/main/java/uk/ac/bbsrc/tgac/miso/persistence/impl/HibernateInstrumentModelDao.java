package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Join;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel_;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentModelStore;

/**
 * uk.ac.bbsrc.tgac.miso.hibernate.persistence.impl
 *
 * @author Heather Armstrong
 * @since 0.2.43
 */

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentModelDao extends HibernateSaveDao<InstrumentModel>
    implements JpaCriteriaPaginatedDataSource<InstrumentModel, InstrumentModel>, InstrumentModelStore {

  private static final List<SingularAttribute<? super InstrumentModel, String>> SEARCH_PROPERTIES =
      Arrays.asList(InstrumentModel_.alias);

  @Autowired
  private JdbcTemplate template;

  public HibernateInstrumentModelDao() {
    super(InstrumentModel.class);
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public InstrumentModel getByAlias(String alias) throws IOException {
    return getBy(InstrumentModel_.ALIAS, alias);
  }

  private static final RowMapper<PlatformType> platformTypeMapper = (rs, rowNum) -> {
    String plat = rs.getString("platform");
    return PlatformType.valueOf(plat);
  };

  @Override
  public Set<PlatformType> listActivePlatformTypes() throws IOException {
    return Sets.newHashSet(getJdbcTemplate().query("SELECT platform FROM ActivePlatformTypes", platformTypeMapper));
  }

  @Override
  public long getUsage(InstrumentModel model) throws IOException {
    return getUsageBy(InstrumentImpl.class, InstrumentImpl_.INSTRUMENT_MODEL, model);
  }

  @Override
  public int getMaxContainersUsed(InstrumentModel model) throws IOException {
    QueryBuilder<InstrumentImpl, InstrumentImpl> insBuilder =
        new QueryBuilder<>(currentSession(), InstrumentImpl.class, InstrumentImpl.class);
    insBuilder.addPredicate(
        insBuilder.getCriteriaBuilder().equal(insBuilder.getRoot().get(InstrumentImpl_.instrumentModel), model));
    List<InstrumentImpl> instruments = insBuilder.getResultList();

    if (instruments.isEmpty()) {
      return 0;
    }

    QueryBuilder<Run, Run> runBuilder = new QueryBuilder<>(currentSession(), Run.class, Run.class);
    In<InstrumentImpl> inClause = runBuilder.getCriteriaBuilder().in(runBuilder.getRoot().get(Run_.sequencer));
    for (InstrumentImpl instrument : instruments) {
      inClause.value(instrument);
    }
    runBuilder.addPredicate(inClause);
    List<Run> runs = runBuilder.getResultList();

    return runs.stream().mapToInt(run -> run.getRunPositions().size()).max().orElse(0);
  }

  @Override
  public InstrumentPosition getPosition(long id) {
    QueryBuilder<InstrumentPosition, InstrumentPosition> builder =
        new QueryBuilder<>(currentSession(), InstrumentPosition.class, InstrumentPosition.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(InstrumentPosition_.positionId), id));
    return builder.getSingleResultOrNull();
  }

  @Override
  public String getFriendlyName() {
    return "Instrument Model";
  }

  @Override
  public SingularAttribute<InstrumentModel, ?> getIdProperty() {
    return InstrumentModel_.instrumentModelId;
  }

  @Override
  public Class<InstrumentModel> getEntityClass() {
    return InstrumentModel.class;
  }

  @Override
  public Class<InstrumentModel> getResultClass() {
    return InstrumentModel.class;
  }

  @Override
  public List<SingularAttribute<? super InstrumentModel, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public SingularAttribute<InstrumentModel, ?> propertyForDate(DateType type) {
    return null;
  }

  @Override
  public SingularAttribute<InstrumentModel, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public long createPosition(InstrumentPosition position) throws IOException {
    return (long) currentSession().save(position);
  }

  @Override
  public void deletePosition(InstrumentPosition position) throws IOException {
    currentSession().delete(position);
  }

  @Override
  public long getPositionUsage(InstrumentPosition position) throws IOException {
    LongQueryBuilder<Run> builder = new LongQueryBuilder<>(currentSession(), Run.class);
    Join<Run, RunPosition> runJoin = builder.getJoin(builder.getRoot(), Run_.runPositions);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(runJoin.get(RunPosition_.position), position));
    return builder.getCount();
  }
}
