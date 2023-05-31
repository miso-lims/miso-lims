package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateProjectDao extends HibernateSaveDao<Project>
    implements ProjectStore, HibernatePaginatedDataSource<Project> {

  private static final String[] SEARCH_PROPERTIES = new String[] {"name", "title",
      "description", "code"};
  private final static List<AliasDescriptor> STANDARD_ALIASES = Collections.emptyList();

  public HibernateProjectDao() {
    super(ProjectImpl.class);
  }

  @Override
  public Project getByTitle(String title) throws IOException {
    return getBy("title", title);
  }

  @Override
  public Project getByCode(String code) throws IOException { // TODO: rename pending
    return getBy("code", code);
  }

  @Override
  public long getUsage(Project project) throws IOException {
    return (long) currentSession().createCriteria(SampleImpl.class)
        .add(Restrictions.eq("project", project))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public String getFriendlyName() {
    return "Project";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Project> getRealClass() {
    return ProjectImpl.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
      case CREATE:
      case ENTERED:
        return "creationTime";
      case UPDATE:
        return "lastModified";
      default:
        return null;

    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public void restrictPaginationByPipeline(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.createAlias("pipeline", "pipeline");
    criteria.add(DbUtils.textRestriction(query, "pipeline.alias"));
  }

  @Override
  public void restrictPaginationByStatus(Criteria criteria, String query, Consumer<String> errorHandler) {
    StatusType value = Arrays.stream(StatusType.values())
        .filter(status -> status.getKey().toLowerCase().equals(query.toLowerCase()))
        .findFirst().orElse(null);
    if (value == null) {
      errorHandler.accept("Invalid status: %s".formatted(query));
    } else {
      criteria.add(Restrictions.eq("status", value));
    }
  }

}
