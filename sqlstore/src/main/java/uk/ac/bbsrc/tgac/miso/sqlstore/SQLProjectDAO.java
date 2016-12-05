/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractProject;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.EntityGroupStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.core.store.WatcherStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.persistence.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.BridgeCollectionUpdater;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Transactional(rollbackFor = Exception.class)
public class SQLProjectDAO implements ProjectStore {
  private static final String TABLE_NAME = "Project";

  public static final String PROJECTS_SELECT = "SELECT projectId, name, alias, shortName, description, creationDate, securityProfile_profileId, progress, lastUpdated, referenceGenomeId "
      + "FROM " + TABLE_NAME;

  public static final String PROJECTS_SELECT_LIMIT = PROJECTS_SELECT + " ORDER BY projectId DESC LIMIT ?";

  public static final String PROJECT_SELECT_BY_ID = PROJECTS_SELECT + " WHERE projectId = ?";

  public static final String PROJECT_SELECT_BY_ALIAS = PROJECTS_SELECT + " WHERE alias = ?";

  public static final String PROJECTS_SELECT_BY_SEARCH = PROJECTS_SELECT + " WHERE UPPER(name) LIKE ? OR UPPER(alias) LIKE ? OR "
      + "UPPER(description) LIKE ? OR UPPER(shortName) LIKE ? ";

  public static final String PROJECT_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET name=:name, alias=:alias, shortName=:shortName, description=:description, creationDate=:creationDate, securityProfile_profileId=:securityProfile_profileId, progress=:progress, referenceGenomeId=:referenceGenomeId "
      + "WHERE projectId=:projectId";

  public static final String PROJECT_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE projectId=:projectId";

  public static final String PROJECT_SELECT_BY_STUDY_ID = PROJECTS_SELECT
      + " WHERE projectId IN (SELECT project_projectId FROM Study WHERE studyId=?)";

  // OVERVIEWS
  public static final String OVERVIEWS_SELECT = "SELECT p.project_projectId, " + "po.overviewId, " + "po.principalInvestigator, "
      + "po.startDate, " + "po.endDate, " + "po.numProposedSamples, " + "po.locked, " + "po.lastUpdated, " + "po.allSampleQcPassed, "
      + "po.libraryPreparationComplete, " + "po.allLibraryQcPassed, " + "po.allPoolsConstructed, " + "po.allRunsCompleted, "
      + "po.primaryAnalysisCompleted " + "FROM ProjectOverview po, Project_ProjectOverview p "
      + "WHERE po.overviewId=p.overviews_overviewId";

  public static final String OVERVIEW_SELECT_BY_ID = OVERVIEWS_SELECT + " AND po.overviewId=?";

  public static final String OVERVIEW_SELECT_BY_RELATED_PROJECT = OVERVIEWS_SELECT + " AND p.project_projectId=?";

  public static final String OVERVIEW_UPDATE = "UPDATE ProjectOverview " + "SET principalInvestigator=:principalInvestigator, "
      + "startDate=:startDate, " + "endDate=:endDate, " + "numProposedSamples=:numProposedSamples, " + "locked=:locked, "
      + "allSampleQcPassed=:allSampleQcPassed, " + "libraryPreparationComplete=:libraryPreparationComplete, "
      + "allLibraryQcPassed=:allLibraryQcPassed, " + "allPoolsConstructed=:allPoolsConstructed, " + "allRunsCompleted=:allRunsCompleted, "
      + "primaryAnalysisCompleted=:primaryAnalysisCompleted " + "WHERE overviewId=:overviewId";

  public static final String OVERVIEW_DELETE = "DELETE FROM ProjectOverview WHERE overviewId=:overviewId";

  public static final String OVERVIEWS_DELETE_BY_PROJECT_ID = "DELETE FROM ProjectOverview WHERE project_projectId=:project_projectId";

  public static String SAMPLES_BY_PROJECT_ID = "SELECT sa.* " + "FROM " + TABLE_NAME + " p "
      + "LEFT JOIN Study st ON st.project_projectId = p.projectId " + "LEFT JOIN Experiment ex ON st.studyId = ex.study_studyId "
      + "INNER JOIN Experiment_Sample exsa ON ex.experimentId = exsa.experiment_experimentId "
      + "LEFT JOIN Sample sa ON exsa.samples_sampleId = sa.sampleId " + "WHERE p.projectId=?";

  public static final String ISSUE_KEYS_SELECT_BY_PROJECT_ID = "SELECT issueKey FROM Project_Issues WHERE project_projectId=?";

  public static final String PROJECT_ISSUES_DELETE_BY_PROJECT_ID = "DELETE FROM Project_Issues "
      + "WHERE project_projectId=:project_projectId";

  protected static final Logger log = LoggerFactory.getLogger(SQLProjectDAO.class);

  private static final BridgeCollectionUpdater<String> ISSUE_KEY_WRITER = new BridgeCollectionUpdater<String>("Project_Issues",
      "project_projectId", "issueKey") {

    @Override
    protected Object getId(String item) {
      return item;
    }

  };
  private JdbcTemplate template;
  private StudyStore studyDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private CascadeType cascadeType;
  private SampleStore sampleDAO;
  private EntityGroupStore entityGroupDAO;
  private LibraryStore libraryDAO;
  private RunStore runDAO;
  private NoteStore noteDAO;
  private WatcherStore watcherDAO;

  @Autowired
  private ProjectAlertManager projectAlertManager;

  @CoverageIgnore
  public void setProjectAlertManager(ProjectAlertManager projectAlertManager) {
    this.projectAlertManager = projectAlertManager;
  }

  @Autowired
  private MisoNamingScheme<Project> namingScheme;

  @Override
  @CoverageIgnore
  public MisoNamingScheme<Project> getNamingScheme() {
    return namingScheme;
  }

  @Override
  @CoverageIgnore
  public void setNamingScheme(MisoNamingScheme<Project> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private CacheManager cacheManager;

  @CoverageIgnore
  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @CoverageIgnore
  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private ReferenceGenomeDao referenceGenomeDao;

  public void setReferenceGenomeDao(ReferenceGenomeDao referenceGenomeDao) {
    this.referenceGenomeDao = referenceGenomeDao;
  }

  @CoverageIgnore
  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  @CoverageIgnore
  public void setStudyDAO(StudyStore studyDAO) {
    this.studyDAO = studyDAO;
  }

  @CoverageIgnore
  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  @CoverageIgnore
  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  @CoverageIgnore
  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  @CoverageIgnore
  public void setEntityGroupDAO(EntityGroupStore entityGroupDAO) {
    this.entityGroupDAO = entityGroupDAO;
  }

  @CoverageIgnore
  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
  }

  @CoverageIgnore
  public void setRunDAO(RunStore runDAO) {
    this.runDAO = runDAO;
  }

  @CoverageIgnore
  public void setNoteDAO(NoteStore noteDAO) {
    this.noteDAO = noteDAO;
  }

  @CoverageIgnore
  public void setWatcherDAO(WatcherStore watcherDAO) {
    this.watcherDAO = watcherDAO;
  }

  @CoverageIgnore
  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  @CoverageIgnore
  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  @CoverageIgnore
  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @CoverageIgnore
  private void purgeListCache(Project p, boolean replace) {
    if (cacheManager != null) {
      Cache cache = cacheManager.getCache("projectListCache");
      DbUtils.updateListCache(cache, replace, p);
    }
  }

  @CoverageIgnore
  private void purgeListCache(Project p) {
    purgeListCache(p, true);
  }

  @Override
  @TriggersRemove(cacheName = { "projectCache",
      "lazyProjectCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public long save(Project project) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

    Long securityProfileId = project.getSecurityProfile().getProfileId();
    if (securityProfileId == SecurityProfile.UNSAVED_ID || (this.cascadeType != null)) {
      securityProfileId = securityProfileDAO.save(project.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", project.getAlias());
    params.addValue("shortName", project.getShortName());
    params.addValue("description", project.getDescription());
    params.addValue("creationDate", project.getCreationDate());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("progress", project.getProgress().getKey());
    params.addValue("referenceGenomeId", project.getReferenceGenome().getId());

    if (project.getId() == AbstractProject.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("projectId");
      try {
        project.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", project);
        project.setName(name);

        if (namingScheme.validateField("name", project.getName())) {
          params.addValue("name", name);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != project.getId()) {
            log.error("Expected Project ID ('" + project.getId() + "') doesn't match returned value ('" + newId.longValue()
                + "') from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(PROJECT_DELETE,
                new MapSqlParameterSource().addValue("projectId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Project ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save Project - invalid field:" + project.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Project - issue with naming scheme", e);
      }
    } else {
      try {
        if (namingScheme.validateField("name", project.getName())) {
          params.addValue("projectId", project.getId());
          params.addValue("name", project.getName());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(PROJECT_UPDATE, params);
        } else {
          throw new IOException("Cannot save Project - invalid field:" + project.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Project - issue with naming scheme", e);
      }
    }

    if (this.cascadeType != null) {
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        ISSUE_KEY_WRITER.saveAll(template, project.getProjectId(), project.getIssueKeys());
        if (!project.getOverviews().isEmpty()) {
          for (ProjectOverview po : project.getOverviews()) {
            saveOverview(po);
          }
        }
      }

      watcherDAO.removeWatchedEntityByUser(project, user);

      for (User u : project.getWatchers()) {
        watcherDAO.saveWatchedEntityUser(project, u);
      }

      purgeListCache(project);
    }

    return project.getProjectId();
  }

  @Override
  public long saveOverview(ProjectOverview overview) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("principalInvestigator", overview.getPrincipalInvestigator());
    params.addValue("startDate", overview.getStartDate());
    params.addValue("endDate", overview.getEndDate());
    params.addValue("numProposedSamples", overview.getNumProposedSamples());
    params.addValue("locked", overview.getLocked());
    params.addValue("allSampleQcPassed", overview.getAllSampleQcPassed());
    params.addValue("libraryPreparationComplete", overview.getLibraryPreparationComplete());
    params.addValue("allLibraryQcPassed", overview.getAllLibrariesQcPassed());
    params.addValue("allPoolsConstructed", overview.getAllPoolsConstructed());
    params.addValue("allRunsCompleted", overview.getAllRunsCompleted());
    params.addValue("primaryAnalysisCompleted", overview.getPrimaryAnalysisCompleted());

    if (overview.getId() == ProjectOverview.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName("ProjectOverview").usingGeneratedKeyColumns("overviewId");
      Number newId = insert.executeAndReturnKey(params);
      overview.setId(newId.longValue());

      Project p = overview.getProject();

      SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("Project_ProjectOverview");

      MapSqlParameterSource poParams = new MapSqlParameterSource();
      poParams.addValue("project_projectId", p.getProjectId());
      poParams.addValue("overviews_overviewId", overview.getId());

      try {
        pInsert.execute(poParams);
      } catch (DuplicateKeyException dke) {
        log.error("This Project/Overview combination already exists - not inserting", dke);
      }
    } else {
      params.addValue("overviewId", overview.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(OVERVIEW_UPDATE, params);
    }

    if (this.cascadeType != null && this.cascadeType.equals(CascadeType.PERSIST)) {
      if (!overview.getNotes().isEmpty()) {
        for (Note n : overview.getNotes()) {
          noteDAO.saveProjectOverviewNote(overview, n);
        }
      }
    }

    if (overview.getSampleGroup() != null && !overview.getSampleGroup().getEntities().isEmpty()) {
      entityGroupDAO.save(overview.getSampleGroup());
    }

    watcherDAO.removeWatchedEntityByUser(overview, user);

    for (User u : overview.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(overview, u);
    }

    DbUtils.updateCaches(cacheManager, overview.getProject(), Project.class);
    DbUtils.updateCaches(cacheManager, overview, ProjectOverview.class);

    return overview.getId();
  }

  @Override
  @Cacheable(cacheName = "projectListCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public List<Project> listAll() {
    return template.query(PROJECTS_SELECT, new ProjectMapper(true));
  }

  @Override
  public List<Project> listAllWithLimit(long limit) throws IOException {
    return template.query(PROJECTS_SELECT_LIMIT, new Object[] { limit }, new ProjectMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  @TriggersRemove(cacheName = { "projectCache",
      "lazyProjectCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public boolean remove(Project project) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    boolean ok = true;
    if (project.isDeletable()
        && (namedTemplate.update(PROJECT_DELETE, new MapSqlParameterSource().addValue("projectId", project.getProjectId())) == 1)) {
      if (!project.getSamples().isEmpty()) {
        for (Sample s : project.getSamples()) {
          ok = sampleDAO.remove(s);
        }
      }

      if (!project.getStudies().isEmpty()) {
        for (Study s : project.getStudies()) {
          ok = studyDAO.remove(s);
        }
      }

      if (!project.getOverviews().isEmpty()) {
        for (ProjectOverview po : project.getOverviews()) {
          ok = removeOverview(po);
        }
      }

      purgeListCache(project, false);

      return ok;
    }
    return false;
  }

  public boolean removeOverview(ProjectOverview overview) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return (overview.isDeletable()
        && (namedTemplate.update(OVERVIEW_DELETE, new MapSqlParameterSource().addValue("overviewId", overview.getId())) == 1));
  }

  @Override
  @Cacheable(cacheName = "projectCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public Project get(long projectId) throws IOException {
    List<Project> eResults = template.query(PROJECT_SELECT_BY_ID, new Object[] { projectId }, new ProjectMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Project lazyGet(long projectId) throws IOException {
    List<Project> eResults = template.query(PROJECT_SELECT_BY_ID, new Object[] { projectId }, new ProjectMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Deprecated
  @Override
  @CoverageIgnore
  public List<Project> listBySearch(String query) {
    String mySQLQuery = DbUtils.convertStringToSearchQuery(query);
    return template.query(PROJECTS_SELECT_BY_SEARCH, new Object[] { mySQLQuery, mySQLQuery, mySQLQuery, mySQLQuery },
        new ProjectMapper(true));
  }

  @Override
  public Project getByAlias(String alias) throws IOException {
    List<Project> eResults = template.query(PROJECT_SELECT_BY_ALIAS, new Object[] { alias }, new ProjectMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Project getByStudyId(long studyId) throws IOException {
    List<Project> eResults = template.query(PROJECT_SELECT_BY_STUDY_ID, new Object[] { studyId }, new ProjectMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  @CoverageIgnore
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    List<ProjectOverview> eResults = template.query(OVERVIEW_SELECT_BY_ID, new Object[] { overviewId }, new ProjectOverviewMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  public ProjectOverview lazyGetProjectOverviewById(long overviewId) throws IOException {
    List<ProjectOverview> eResults = template.query(OVERVIEW_SELECT_BY_ID, new Object[] { overviewId }, new ProjectOverviewMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public List<ProjectOverview> listOverviewsByProjectId(long projectId) throws IOException {
    return template.query(OVERVIEW_SELECT_BY_RELATED_PROJECT, new Object[] { projectId }, new ProjectOverviewMapper(true));
  }

  public List<String> listIssueKeysByProjectId(long projectId) throws IOException {
    return template.queryForList(ISSUE_KEYS_SELECT_BY_PROJECT_ID, new Object[] { projectId }, String.class);
  }

  @CoverageIgnore
  public class ProjectMapper extends CacheAwareRowMapper<Project> {
    public ProjectMapper() {
      super(Project.class);
    }

    public ProjectMapper(boolean lazy) {
      super(Project.class, lazy);
    }

    @Override
    @CoverageIgnore
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("projectId");
      Project project = null;

      try {
        if (isCacheEnabled() && lookupCache(cacheManager) != null) {
          Element element;
          if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
            log.debug("Cache hit on map for Project " + id);
            return (Project) element.getObjectValue();
          }
        }

        project = dataObjectFactory.getProject();
        project.setProjectId(id);
        project.setName(rs.getString("name"));
        project.setAlias(rs.getString("alias"));
        project.setShortName(rs.getString("shortName"));
        project.setDescription(rs.getString("description"));
        project.setCreationDate(rs.getDate("creationDate"));
        project.setProgress(ProgressType.get(rs.getString("progress")));
        project.setLastUpdated(rs.getTimestamp("lastUpdated"));
        project.setReferenceGenome(referenceGenomeDao.getReferenceGenome(rs.getLong("referenceGenomeId")));

        try {
          project.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
          project.setIssueKeys(listIssueKeysByProjectId(id));
          project.setWatchers(new HashSet<>(watcherDAO.getWatchersByEntityName(project.getWatchableIdentifier())));
          if (project.getSecurityProfile() != null && project.getSecurityProfile().getOwner() != null)
            project.addWatcher(project.getSecurityProfile().getOwner());
          for (User u : watcherDAO.getWatchersByWatcherGroup("ProjectWatchers")) {
            project.addWatcher(u);
          }

          if (!isLazy()) {
            Collection<ProjectOverview> overviews = listOverviewsByProjectId(id);
            project.setOverviews(overviews);
            project.setSamples(sampleDAO.listByProjectId(id));
            project.setStudies(studyDAO.listByProjectId(id));
          }
        } catch (IOException e1) {
          log.error("project row mapper", e1);
        }

        if (projectAlertManager != null) {
          projectAlertManager.push(project);
        }

        if (isCacheEnabled() && lookupCache(cacheManager) != null) {
          lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), project));
          log.debug("Cache put for Project " + id);
        }
      } catch (net.sf.ehcache.CacheException ce) {
        log.error("project row mapper", ce);
      } catch (UnsupportedOperationException uoe) {
        log.error("project row mapper", uoe);
      }
      return project;
    }
  }

  @CoverageIgnore
  public class ProjectOverviewMapper extends CacheAwareRowMapper<ProjectOverview> {
    public ProjectOverviewMapper() {
      super(ProjectOverview.class);
    }

    public ProjectOverviewMapper(boolean lazy) {
      super(ProjectOverview.class, lazy);
    }

    @Override
    @CoverageIgnore
    public ProjectOverview mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("overviewId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id));
        if (element != null) {
          log.debug("Cache hit on map for ProjectOverview " + id);
          return (ProjectOverview) element.getObjectValue();
        }
      }
      ProjectOverview overview = new ProjectOverview();

      try {
        Project p = lazyGet(rs.getLong("project_projectId"));
        overview.setProject(p);

        overview.setId(id);
        overview.setPrincipalInvestigator(rs.getString("principalInvestigator"));
        overview.setStartDate(rs.getDate("startDate"));
        overview.setEndDate(rs.getDate("endDate"));
        overview.setNumProposedSamples(rs.getInt("numProposedSamples"));
        overview.setLocked(rs.getBoolean("locked"));
        overview.setAllSampleQcPassed(rs.getBoolean("allSampleQcPassed"));
        overview.setLibraryPreparationComplete(rs.getBoolean("libraryPreparationComplete"));
        overview.setAllLibrariesQcPassed(rs.getBoolean("allLibraryQcPassed"));
        overview.setAllPoolsConstructed(rs.getBoolean("allPoolsConstructed"));
        overview.setAllRunsCompleted(rs.getBoolean("allRunsCompleted"));
        overview.setPrimaryAnalysisCompleted(rs.getBoolean("primaryAnalysisCompleted"));
        overview.setLastUpdated(rs.getTimestamp("lastUpdated"));

        EntityGroup<ProjectOverview, Sample> osg = entityGroupDAO.getEntityGroupByParent(overview, overview.getClass());
        if (osg != null) {
          osg.setParent(overview);
          overview.setSampleGroup(osg);
        }

        overview.setLibraries(libraryDAO.listByProjectId(rs.getLong("project_projectId")));
        overview.setRuns(runDAO.listByProjectId(rs.getLong("project_projectId")));
        overview.setNotes(noteDAO.listByProjectOverview(id));

        overview.setWatchers(new HashSet<>(watcherDAO.getWatchersByEntityName(overview.getWatchableIdentifier())));
        if (overview.getProject().getSecurityProfile() != null && overview.getProject().getSecurityProfile().getOwner() != null)
          overview.addWatcher(overview.getProject().getSecurityProfile().getOwner());
        for (User u : watcherDAO.getWatchersByWatcherGroup("ProjectWatchers")) {
          overview.addWatcher(u);
        }
      } catch (IOException e) {
        log.error("project overview row mapper", e);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), overview));
        log.debug("Cache put for overview " + id);
      }

      return overview;
    }
  }

  @Override
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }
}
