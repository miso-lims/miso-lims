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
import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.store.SecurityStore;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractStudy;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLStudyDAO implements StudyStore {
  private static final String TABLE_NAME = "Study";

  public static final String STUDIES_SELECT = "SELECT studyId, name, description, alias, accession, securityProfile_profileId, project_projectId, studyType, lastModifier "
      + "FROM " + TABLE_NAME;

  public static final String STUDIES_SELECT_LIMIT = STUDIES_SELECT + " ORDER BY studyId DESC LIMIT ?";

  public static final String STUDY_SELECT_BY_ID = STUDIES_SELECT + " " + "WHERE studyId = ?";

  public static final String STUDIES_SELECT_BY_SEARCH = STUDIES_SELECT + " WHERE " + "name LIKE ? OR " + "alias LIKE ? OR "
      + "description LIKE ? ";

  public static final String STUDY_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET name=:name, description=:description, alias=:alias, accession=:accession, securityProfile_profileId=:securityProfile_profileId, project_projectId=:project_projectId, studyType=:studyType, lastModifier=:lastModifier "
      + "WHERE studyId=:studyId";

  public static final String STUDY_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE studyId=:studyId";

  public static final String STUDY_SELECT_BY_EXPERIMENT_ID = "SELECT s.studyId, s.name, s.description, s.alias, s.accession, s.securityProfile_profileId, s.project_projectId, s.studyType, s.lastModifier "
      + "FROM " + TABLE_NAME + " s, Experiment e " + "WHERE s.studyId=e.study_studyId " + "AND e.experimentId=?";

  public static final String STUDY_SELECT_BY_STUDY_TYPE = "SELECT s.studyId, s.name, s.description, s.alias, s.accession, s.securityProfile_profileId, s.project_projectId, s.studyType, s.lastModifier "
      + "FROM " + TABLE_NAME + " s, StudyType t " + "WHERE s.studyType=t.name " + "AND t.name=?";

  public static final String STUDIES_BY_RELATED_PROJECT = "SELECT s.studyId, s.name, s.description, s.alias, s.accession, s.securityProfile_profileId, s.project_projectId, s.studyType, s.lastModifier "
      + "FROM " + TABLE_NAME + " s, Project_Study ps " + "WHERE s.studyId=ps.studies_studyId " + "AND ps.Project_projectId=?";

  public static final String STUDIES_BY_RELATED_SUBMISSION = "SELECT s.studyId, s.name, s.description, s.alias, s.accession, s.securityProfile_profileId, s.project_projectId, s.studyType, s.lastModifier "
      + "FROM " + TABLE_NAME + " s, Submission_Study ss " + "WHERE s.studyId=ss.studies_studyId " + "AND ss.submission_submissionId=?";

  public static final String STUDIES_BY_RELATED_LIBRARY = "SELECT " + "stu.* FROM Study stu "
      + "INNER JOIN Experiment exp ON stu.studyId = exp.study_studyId "
      + "INNER JOIN Pool_Experiment pex ON exp.experimentId = pex.experiments_experimentId "
      + "INNER JOIN Pool pool ON pool.poolId = pex.pool_poolId " + "INNER JOIN Pool_Elements pel ON pel.pool_poolId = pex.pool_poolId "
      + "INNER JOIN LibraryDilution ldi ON ldi.dilutionId = pel.elementId "
      + "INNER JOIN Library lib ON ldi.library_libraryId = lib.libraryId " + "INNER JOIN Sample sam ON sam.sampleId = lib.sample_sampleId "
      + "INNER JOIN Project pro ON pro.projectId = sam.project_projectId "
      + "WHERE sam.project_projectId = stu.project_projectId AND lib.libraryId = ?";

  public static final String STUDY_TYPES_SELECT = "SELECT name " + "FROM StudyType";

  protected static final Logger log = LoggerFactory.getLogger(SQLStudyDAO.class);

  private JdbcTemplate template;
  private ProjectStore projectDAO;
  private ExperimentStore experimentDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private CascadeType cascadeType;
  private ChangeLogStore changeLogDAO;
  private SecurityStore securityDAO;

  @Autowired
  private MisoNamingScheme<Study> namingScheme;

  @Override
  public MisoNamingScheme<Study> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Study> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setProjectDAO(ProjectStore projectDAO) {
    this.projectDAO = projectDAO;
  }

  public void setExperimentDAO(ExperimentStore experimentDAO) {
    this.experimentDAO = experimentDAO;
  }

  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  private void purgeListCache(Study s, boolean replace) {
    Cache cache = cacheManager.getCache("studyListCache");
    DbUtils.updateListCache(cache, replace, s, Study.class);
  }

  private void purgeListCache(Study s) {
    purgeListCache(s, true);
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "studyCache",
      "lazyStudyCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(Study study) throws IOException {
    Long securityProfileId = study.getSecurityProfile().getProfileId();
    if (this.cascadeType != null) {
      securityProfileId = securityProfileDAO.save(study.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", study.getAlias());
    params.addValue("accession", study.getAccession());
    params.addValue("description", study.getDescription());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("project_projectId", study.getProject().getProjectId());
    params.addValue("studyType", study.getStudyType());

    params.addValue("lastModifier", study.getLastModifier().getUserId());
    if (study.getId() == AbstractStudy.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("studyId");
      try {
        study.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", study);
        study.setName(name);

        if (namingScheme.validateField("name", study.getName())) {
          params.addValue("name", name);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != study.getId()) {
            log.error("Expected Study ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(STUDY_DELETE,
                new MapSqlParameterSource().addValue("studyId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Study ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save Study - invalid field:" + study.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Study - issue with naming scheme", e);
      }

      Project p = study.getProject();

      SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("Project_Study");

      MapSqlParameterSource poParams = new MapSqlParameterSource();
      poParams.addValue("Project_projectId", p.getProjectId());
      poParams.addValue("studies_studyId", study.getId());
      try {
        pInsert.execute(poParams);
      } catch (DuplicateKeyException dke) {
        // ignore
      }
    } else {
      try {
        if (namingScheme.validateField("name", study.getName())) {
          params.addValue("studyId", study.getId());
          params.addValue("name", study.getName());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(STUDY_UPDATE, params);
        } else {
          throw new IOException("Cannot save Study - invalid field:" + study.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Study - issue with naming scheme", e);
      }
    }

    if (this.cascadeType != null) {
      Project p = study.getProject();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (p != null) projectDAO.save(p);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (p != null) {
          DbUtils.updateCaches(cacheManager, p, Project.class);
        }
      }

      purgeListCache(study);
    }

    return study.getId();
  }

  @Override
  @Cacheable(cacheName = "studyListCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public List<Study> listAll() {
    return template.query(STUDIES_SELECT, new StudyMapper(true));
  }

  @Override
  public List<Study> listAllWithLimit(long limit) throws IOException {
    return template.query(STUDIES_SELECT_LIMIT, new Object[] { limit }, new StudyMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<Study> listBySearch(String query) {
    String mySQLQuery = "%" + query.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    return template.query(STUDIES_SELECT_BY_SEARCH, new Object[] { mySQLQuery, mySQLQuery, mySQLQuery }, new StudyMapper(true));
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "studyCache",
      "lazyStudyCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(Study study) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (study.isDeletable() && (namedTemplate.update(STUDY_DELETE, new MapSqlParameterSource().addValue("studyId", study.getId())) == 1)) {
      Project p = study.getProject();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (p != null) projectDAO.save(p);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (p != null) {
          DbUtils.updateCaches(cacheManager, p, Project.class);
        }
      }

      purgeListCache(study, false);

      return true;
    }
    return false;
  }

  @Override
  @Cacheable(cacheName = "studyCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public Study get(long studyId) throws IOException {
    List eResults = template.query(STUDY_SELECT_BY_ID, new Object[] { studyId }, new StudyMapper());
    Study e = eResults.size() > 0 ? (Study) eResults.get(0) : null;
    return e;
  }

  @Override
  public Study lazyGet(long studyId) throws IOException {
    List eResults = template.query(STUDY_SELECT_BY_ID, new Object[] { studyId }, new StudyMapper(true));
    Study e = eResults.size() > 0 ? (Study) eResults.get(0) : null;
    return e;
  }

  @Override
  public List<Study> listByProjectId(long projectId) throws IOException {
    return template.query(STUDIES_BY_RELATED_PROJECT, new Object[] { projectId }, new StudyMapper(true));
  }

  @Override
  public List<Study> listBySubmissionId(long submissionId) throws IOException {
    return template.query(STUDIES_BY_RELATED_SUBMISSION, new Object[] { submissionId }, new StudyMapper());
  }

  @Override
  public List<Study> listByLibraryId(long libraryId) throws IOException {
    return template.query(STUDIES_BY_RELATED_LIBRARY, new Object[] { libraryId }, new StudyMapper(true));
  }

  @Override
  public Study getByExperimentId(long experimentId) throws IOException {
    List eResults = template.query(STUDY_SELECT_BY_EXPERIMENT_ID, new Object[] { experimentId }, new StudyMapper());
    Study e = eResults.size() > 0 ? (Study) eResults.get(0) : null;
    return e;
  }

  public List<Study> getByStudyType(long typeId) throws IOException {
    return template.query(STUDY_SELECT_BY_STUDY_TYPE, new Object[] { typeId }, new StudyMapper());
  }

  @Override
  public List<String> listAllStudyTypes() throws IOException {
    return template.queryForList(STUDY_TYPES_SELECT, String.class);
  }

  public ChangeLogStore getChangeLogDAO() {
    return changeLogDAO;
  }

  public void setChangeLogDAO(ChangeLogStore changeLogDAO) {
    this.changeLogDAO = changeLogDAO;
  }

  public SecurityStore getSecurityDAO() {
    return securityDAO;
  }

  public void setSecurityDAO(SecurityStore securityDAO) {
    this.securityDAO = securityDAO;
  }

  public class StudyMapper extends CacheAwareRowMapper<Study> {
    public StudyMapper() {
      super(Study.class);
    }

    public StudyMapper(boolean lazy) {
      super(Study.class, lazy);
    }

    @Override
    public Study mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("studyId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Study " + id);
          return (Study) element.getObjectValue();
        }
      }
      Study s = dataObjectFactory.getStudy();
      s.setId(id);
      s.setName(rs.getString("name"));
      s.setAlias(rs.getString("alias"));
      s.setAccession(rs.getString("accession"));
      s.setDescription(rs.getString("description"));
      s.setStudyType(rs.getString("studyType"));
      try {
        s.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));

        if (!isLazy()) {
          s.setProject(projectDAO.get(rs.getLong("project_projectId")));

          for (Experiment e : experimentDAO.listByStudyId(id)) {
            s.addExperiment(e);
          }
        } else {
          s.setProject(projectDAO.lazyGet(rs.getLong("project_projectId")));
        }
        s.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        s.getChangeLog().addAll(changeLogDAO.listAllById(TABLE_NAME, id));
      } catch (IOException e1) {
        log.error("study row mapper", e1);
      } catch (MalformedExperimentException e) {
        log.error("study row mapper", e);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), s));
      }

      return s;
    }
  }
}
