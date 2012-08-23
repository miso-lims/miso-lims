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

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.googlecode.ehcache.annotations.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.*;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;

import javax.persistence.CascadeType;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLProjectDAO implements ProjectStore {
  private static final String TABLE_NAME = "Project";

  public static final String PROJECTS_SELECT =
          "SELECT projectId, name, alias, description, creationDate, securityProfile_profileId, progress, lastUpdated " +
          "FROM "+TABLE_NAME;

  public static final String PROJECT_SELECT_BY_ID =
          PROJECTS_SELECT + " WHERE projectId = ?";

  public static final String PROJECTS_SELECT_BY_SEARCH =
          PROJECTS_SELECT + " WHERE " +
          "name LIKE ? OR " +
          "alias LIKE ? OR " +
          "description LIKE ? ";

  public static final String PROJECT_UPDATE =
          "UPDATE "+TABLE_NAME+" " +
          "SET name=:name, alias=:alias, description=:description, creationDate=:creationDate, securityProfile_profileId=:securityProfile_profileId, progress=:progress " +
          "WHERE projectId=:projectId";

  public static final String PROJECT_DELETE =
          "DELETE FROM "+TABLE_NAME+" WHERE projectId=:projectId";

  public static final String PROJECT_SELECT_BY_STUDY_ID =
          "SELECT p.projectId, p.name, p.alias, p.description, p.creationDate, p.securityProfile_profileId, p.progress " +
          "FROM "+TABLE_NAME+" p, Study s " +
          "WHERE p.projectId=s.project_projectId " +
          "AND s.studyId=?";

  //OVERVIEWS
  public static final String OVERVIEWS_SELECT =
          "SELECT p.project_projectId, " +
          "po.overviewId, " +
          "po.principalInvestigator, " +
          "po.startDate, " +
          "po.endDate, " +
          "po.numProposedSamples, " +
          "po.locked, " +
          "po.lastUpdated, " +
          "po.allSampleQcPassed, " +
          "po.libraryPreparationComplete, " +
          "po.allLibraryQcPassed, " +
          "po.allPoolsConstructed, " +
          "po.allRunsCompleted, " +
          "po.primaryAnalysisCompleted " +
          "FROM ProjectOverview po, Project_ProjectOverview p " +
          "WHERE po.overviewId=p.overviews_overviewId";

  public static final String OVERVIEW_SELECT_BY_ID =
          OVERVIEWS_SELECT + " AND po.overviewId=?";

  public static final String OVERVIEW_SELECT_BY_RELATED_PROJECT =
          OVERVIEWS_SELECT + " AND p.project_projectId=?";

  public static final String OVERVIEW_UPDATE =
          "UPDATE ProjectOverview " +
          "SET principalInvestigator=:principalInvestigator, " +
          "startDate=:startDate, " +
          "endDate=:endDate, " +
          "numProposedSamples=:numProposedSamples, " +
          "locked=:locked, " +
          "allSampleQcPassed=:allSampleQcPassed, " +
          "libraryPreparationComplete=:libraryPreparationComplete, " +
          "allLibraryQcPassed=:allLibraryQcPassed, " +
          "allPoolsConstructed=:allPoolsConstructed, " +
          "allRunsCompleted=:allRunsCompleted, " +
          "primaryAnalysisCompleted=:primaryAnalysisCompleted " +
          "WHERE overviewId=:overviewId";

  public static final String OVERVIEW_DELETE =
          "DELETE FROM ProjectOverview WHERE overviewId=:overviewId";

  public static final String OVERVIEWS_DELETE_BY_PROJECT_ID =
          "DELETE FROM ProjectOverview WHERE project_projectId=:project_projectId";

  public static String SAMPLES_BY_PROJECT_ID =
          "SELECT sa.* " +
          "FROM "+TABLE_NAME+" p " +
          "LEFT JOIN Study st ON st.project_projectId = p.projectId " +
          "LEFT JOIN Experiment ex ON st.studyId = ex.study_studyId " +
          "INNER JOIN Experiment_Sample exsa ON ex.experimentId = exsa.experiment_experimentId " +
          "LEFT JOIN Sample sa ON exsa.samples_sampleId = sa.sampleId " +
          "WHERE p.projectId=?";

  public static final String OVERVIEW_RELATED_INFORMATION_BY_PROJECT_ID =
          "SELECT " +
          "p.projectId, " +
          "st.studyId, " +
          "ex.experimentId, " +
          "sa.sampleId, " +
          "sa.receivedDate, " +
          "li.libraryId, " +
          "li.creationDate, " +
          "r.runId, " +
          "r.platformType, " +
          "pl.platformId " +
          "pl.instrumentModel " +
          "FROM "+TABLE_NAME+" p " +
          "LEFT JOIN Study st ON st.project_projectId = p.projectId " +
          "LEFT JOIN Experiment ex ON st.studyId = ex.study_studyId " +
          "INNER JOIN Experiment_Sample exsa ON ex.experimentId = exsa.experiment_experimentId " +
          "LEFT JOIN Sample sa ON exsa.samples_sampleId = sa.sampleId " +
          "LEFT JOIN Library li ON li.sample_sampleId = sa.sampleId " +
          "INNER JOIN Experiment_Run exru ON ex.experimentId = exru.experiment_experimentId " +
          "LEFT JOIN Run r ON r.runId = exru.runs_runId " +
          "LEFT JOIN Platform pl ON r.platform_platformId = pl.platformId " +
          "WHERE p.projectId=?";

  public static final String ISSUE_KEYS_SELECT_BY_PROJECT_ID =
          "SELECT issueKey FROM Project_Issues WHERE project_projectId=?";

  public static final String PROJECT_ISSUES_DELETE_BY_PROJECT_ID =
          "DELETE FROM Project_Issues " +
          "WHERE project_projectId=:project_projectId";

  protected static final Logger log = LoggerFactory.getLogger(SQLProjectDAO.class);
  private JdbcTemplate template;
  private StudyStore studyDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private CascadeType cascadeType;
  private SampleStore sampleDAO;
  private LibraryStore libraryDAO;
  private RunStore runDAO;
  private NoteStore noteDAO;
  private WatcherStore watcherDAO;

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setStudyDAO(StudyStore studyDAO) {
    this.studyDAO = studyDAO;
  }

  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
  }

  public void setRunDAO(RunStore runDAO) {
    this.runDAO = runDAO;
  }

  public void setNoteDAO(NoteStore noteDAO) {
    this.noteDAO = noteDAO;
  }

  public void setWatcherDAO(WatcherStore watcherDAO) {
    this.watcherDAO = watcherDAO;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  private void purgeListCache(Project p, boolean replace) {
    Cache cache = cacheManager.getCache("projectListCache");
    if (cache.getKeys().size() > 0) {
      Object cachekey = cache.getKeys().get(0);
      List<Project> c = (List<Project>)cache.get(cachekey).getValue();
      if (c.remove(p)) {
        if (replace) {
          c.add(p);
        }
      }
      else {
        c.add(p);
      }
      cache.put(new Element(cachekey, c));
    }
  }

  private void purgeListCache(Project p) {
    purgeListCache(p, true);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  @TriggersRemove(
          cacheName = "projectCache",
          keyGenerator = @KeyGenerator(
                  name = "HashCodeCacheKeyGenerator",
                  properties = {
                          @Property(name = "includeMethod", value = "false"),
                          @Property(name = "includeParameterTypes", value = "false")
                  }
          )
  )
  public long save(Project project) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

    Long securityProfileId = project.getSecurityProfile().getProfileId();
    if (securityProfileId == SecurityProfile.UNSAVED_ID ||
        (this.cascadeType != null)) { // && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(project.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", project.getAlias())
            .addValue("description", project.getDescription())
            .addValue("creationDate", project.getCreationDate())
            .addValue("securityProfile_profileId", securityProfileId)
            .addValue("progress", project.getProgress().getKey());

    if (project.getProjectId() == AbstractProject.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
              .withTableName(TABLE_NAME)
              .usingGeneratedKeyColumns("projectId");
      String name = "PRO" + DbUtils.getAutoIncrement(template, TABLE_NAME);
      params.addValue("name", name);
      Number newId = insert.executeAndReturnKey(params);
      project.setProjectId(newId.longValue());
      project.setName(name);
    }
    else {
      params.addValue("projectId", project.getProjectId());
      params.addValue("name", project.getName());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(PROJECT_UPDATE, params);
    }

    if (this.cascadeType != null) {
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        MapSqlParameterSource eParams = new MapSqlParameterSource();
        eParams.addValue("project_projectId", project.getProjectId());
        NamedParameterJdbcTemplate eNamedTemplate = new NamedParameterJdbcTemplate(template);
        eNamedTemplate.update(PROJECT_ISSUES_DELETE_BY_PROJECT_ID, eParams);

        if (project.getIssueKeys() != null && !project.getIssueKeys().isEmpty()) {
          for (String s : project.getIssueKeys()) {
            SimpleJdbcInsert fInsert = new SimpleJdbcInsert(template).withTableName("Project_Issues");
            MapSqlParameterSource fcParams = new MapSqlParameterSource();
            fcParams.addValue("project_projectId", project.getProjectId())
                    .addValue("issueKey", s);

            try {
              fInsert.execute(fcParams);
            }
            catch (DuplicateKeyException dke) {
              log.warn("This Project/Issue Key combination already exists - not inserting: " + dke.getMessage());
            }
          }
        }
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

  public long saveOverview(ProjectOverview overview) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("principalInvestigator", overview.getPrincipalInvestigator())
            .addValue("startDate", overview.getStartDate())
            .addValue("endDate", overview.getEndDate())
            .addValue("numProposedSamples", overview.getNumProposedSamples())
            .addValue("locked", overview.getLocked())
            .addValue("allSampleQcPassed", overview.getAllSampleQcPassed())
            .addValue("libraryPreparationComplete", overview.getLibraryPreparationComplete())
            .addValue("allLibraryQcPassed", overview.getAllLibrariesQcPassed())
            .addValue("allPoolsConstructed", overview.getAllPoolsConstructed())
            .addValue("allRunsCompleted", overview.getAllRunsCompleted())
            .addValue("primaryAnalysisCompleted", overview.getPrimaryAnalysisCompleted());

    if (overview.getOverviewId() == ProjectOverview.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
              .withTableName("ProjectOverview")
              .usingGeneratedKeyColumns("overviewId");
      Number newId = insert.executeAndReturnKey(params);
      overview.setOverviewId(newId.longValue());

      Project p = overview.getProject();

      SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template)
              .withTableName("Project_ProjectOverview");

      MapSqlParameterSource poParams = new MapSqlParameterSource();
      poParams.addValue("project_projectId", p.getProjectId())
              .addValue("overviews_overviewId", overview.getOverviewId());

      try {
        pInsert.execute(poParams);
      }
      catch (DuplicateKeyException dke) {
        log.warn("This Project/Overview combination already exists - not inserting: " + dke.getMessage());
      }
    }
    else {
      params.addValue("overviewId", overview.getOverviewId());
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

    watcherDAO.removeWatchedEntityByUser(overview, user);

    for (User u : overview.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(overview, u);
    }

    return overview.getOverviewId();
  }

  @Cacheable(cacheName="projectListCache",
      keyGenerator = @KeyGenerator(
              name = "HashCodeCacheKeyGenerator",
              properties = {
                      @Property(name="includeMethod", value="false"),
                      @Property(name="includeParameterTypes", value="false")
              }
      )
  )
  public List<Project> listAll() {
    return template.query(PROJECTS_SELECT, new LazyProjectMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM "+TABLE_NAME);
  }

  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(
          cacheName = "projectCache",
          keyGenerator = @KeyGenerator(
                  name = "HashCodeCacheKeyGenerator",
                  properties = {
                          @Property(name = "includeMethod", value = "false"),
                          @Property(name = "includeParameterTypes", value = "false")
                  }
          )
  )
  public boolean remove(Project project) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    boolean ok = true;
    if (project.isDeletable() &&
        (namedTemplate.update(PROJECT_DELETE,
                              new MapSqlParameterSource().addValue("projectId", project.getProjectId())) == 1)) {
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
    return (overview.isDeletable() &&
           (namedTemplate.update(OVERVIEW_DELETE,
                                 new MapSqlParameterSource().addValue("overviewId", overview.getOverviewId())) == 1));
  }

  @Cacheable(cacheName = "projectCache",
             keyGenerator = @KeyGenerator(
                     name = "HashCodeCacheKeyGenerator",
                     properties = {
                             @Property(name = "includeMethod", value = "false"),
                             @Property(name = "includeParameterTypes", value = "false")
                     }
             )
  )
  public Project get(long projectId) throws IOException {
    List eResults = template.query(PROJECT_SELECT_BY_ID, new Object[]{projectId}, new ProjectMapper());
    Project e = eResults.size() > 0 ? (Project) eResults.get(0) : null;
    return e;
  }

  public Project lazyGet(long projectId) throws IOException {
    List eResults = template.query(PROJECT_SELECT_BY_ID, new Object[]{projectId}, new LazyProjectMapper());
    Project e = eResults.size() > 0 ? (Project) eResults.get(0) : null;
    return e;
  }

  public List<Project> listBySearch(String query) {
    String mySQLQuery = "%" + query + "%";
    return template.query(PROJECTS_SELECT_BY_SEARCH, new Object[]{mySQLQuery,mySQLQuery,mySQLQuery}, new LazyProjectMapper());
  }

  public Project getByStudyId(long studyId) throws IOException {
    List eResults = template.query(PROJECT_SELECT_BY_STUDY_ID, new Object[]{studyId}, new ProjectMapper());
    Project e = eResults.size() > 0 ? (Project) eResults.get(0) : null;
    return e;
  }

  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    List eResults = template.query(OVERVIEW_SELECT_BY_ID, new Object[]{overviewId}, new ProjectOverviewMapper());
    ProjectOverview e = eResults.size() > 0 ? (ProjectOverview) eResults.get(0) : null;
    return e;
  }

  public ProjectOverview lazyGetProjectOverviewById(long overviewId) throws IOException {
    List eResults = template.query(OVERVIEW_SELECT_BY_ID, new Object[]{overviewId}, new LazyProjectOverviewMapper());
    ProjectOverview e = eResults.size() > 0 ? (ProjectOverview) eResults.get(0) : null;
    return e;
  }

  public List<ProjectOverview> listOverviewsByProjectId(long projectId) throws IOException {
    return template.query(OVERVIEW_SELECT_BY_RELATED_PROJECT, new Object[]{projectId}, new LazyProjectOverviewMapper());
  }

  public List<String> listIssueKeysByProjectId(long projectId) throws IOException {
    return template.queryForList(ISSUE_KEYS_SELECT_BY_PROJECT_ID, new Object[]{projectId}, String.class);
  }

  public class LazyProjectMapper implements RowMapper<Project> {
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
      Project project = dataObjectFactory.getProject();
      project.setProjectId(rs.getLong("projectId"));
      project.setName(rs.getString("name"));
      project.setAlias(rs.getString("alias"));
      project.setDescription(rs.getString("description"));
      project.setCreationDate(rs.getDate("creationDate"));
      project.setProgress(ProgressType.get(rs.getString("progress")));
      project.setLastUpdated(rs.getTimestamp("lastUpdated"));

      try {
        project.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        project.setIssueKeys(listIssueKeysByProjectId(rs.getLong("projectId")));
        project.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(project.getWatchableIdentifier())));
        if (project.getSecurityProfile() != null &&
            project.getSecurityProfile().getOwner() != null)
          project.addWatcher(project.getSecurityProfile().getOwner());
        for (User u : watcherDAO.getWatchersByWatcherGroup("ProjectWatchers")) {
          project.addWatcher(u);
        }
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }
      return project;
    }
  }

  public class ProjectMapper implements RowMapper<Project> {
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
      Project project = dataObjectFactory.getProject();
      project.setProjectId(rs.getLong("projectId"));
      project.setName(rs.getString("name"));
      project.setAlias(rs.getString("alias"));
      project.setDescription(rs.getString("description"));
      project.setCreationDate(rs.getDate("creationDate"));
      project.setProgress(ProgressType.get(rs.getString("progress")));
      project.setLastUpdated(rs.getTimestamp("lastUpdated"));

      try {
        project.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));

        Collection<ProjectOverview> overviews = listOverviewsByProjectId(rs.getLong("projectId"));
        for (ProjectOverview po : overviews) {
          po.setProject(project);
        }
        project.setOverviews(overviews);

        project.setSamples(sampleDAO.listByProjectId(rs.getLong("projectId")));
        project.setStudies(studyDAO.listByProjectId(rs.getLong("projectId")));
        project.setIssueKeys(listIssueKeysByProjectId(rs.getLong("projectId")));

        project.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(project.getWatchableIdentifier())));
        if (project.getSecurityProfile() != null &&
            project.getSecurityProfile().getOwner() != null)
          project.addWatcher(project.getSecurityProfile().getOwner());
        for (User u : watcherDAO.getWatchersByWatcherGroup("ProjectWatchers")) {
          project.addWatcher(u);
        }
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }
      return project;
    }
  }

  public class LazyProjectOverviewMapper implements RowMapper<ProjectOverview> {
    public ProjectOverview mapRow(ResultSet rs, int rowNum) throws SQLException {
      ProjectOverview overview = new ProjectOverview();

      try {
        Project p = lazyGet(rs.getLong("project_projectId"));
        overview.setProject(p);

        overview.setOverviewId(rs.getLong("overviewId"));
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

        //overview.setSamples(sampleDAO.listByProjectId(rs.getLong("project_projectId")));
        overview.setSamples(p.getSamples());
        overview.setLibraries(libraryDAO.listByProjectId(rs.getLong("project_projectId")));
        overview.setRuns(runDAO.listByProjectId(rs.getLong("project_projectId")));
        overview.setNotes(noteDAO.listByProjectOverview(rs.getLong("overviewId")));

        overview.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(overview.getWatchableIdentifier())));
        if (overview.getProject().getSecurityProfile() != null &&
            overview.getProject().getSecurityProfile().getOwner() != null)
          overview.addWatcher(overview.getProject().getSecurityProfile().getOwner());
        for (User u : watcherDAO.getWatchersByWatcherGroup("ProjectWatchers")) {
          overview.addWatcher(u);
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }

      return overview;
    }
  }

  public class ProjectOverviewMapper implements RowMapper<ProjectOverview> {
    public ProjectOverview mapRow(ResultSet rs, int rowNum) throws SQLException {
      ProjectOverview overview = new ProjectOverview();

      try {
        Project p = lazyGet(rs.getLong("project_projectId"));
        overview.setProject(p);

        overview.setOverviewId(rs.getLong("overviewId"));
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

        //overview.setSamples(sampleDAO.listByProjectId(rs.getLong("project_projectId")));
        overview.setSamples(p.getSamples());
        overview.setLibraries(libraryDAO.listByProjectId(rs.getLong("project_projectId")));
        overview.setRuns(runDAO.listByProjectId(rs.getLong("project_projectId")));
        overview.setNotes(noteDAO.listByProjectOverview(rs.getLong("overviewId")));

        overview.setWatchers(new HashSet<User>(watcherDAO.getWatchersByEntityName(overview.getWatchableIdentifier())));
        if (overview.getProject().getSecurityProfile() != null &&
            overview.getProject().getSecurityProfile().getOwner() != null)
          overview.addWatcher(overview.getProject().getSecurityProfile().getOwner());
        for (User u : watcherDAO.getWatchersByWatcherGroup("ProjectWatchers")) {
          overview.addWatcher(u);
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }

      return overview;
    }
  }
}
