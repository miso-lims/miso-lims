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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.store.SecurityStore;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleQcException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
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
public class SQLSampleDAO implements SampleStore {
  private static final String TABLE_NAME = "Sample";

  public static final String SAMPLES_SELECT = "SELECT sampleId, name, description, scientificName, taxonIdentifier, alias, accession, securityProfile_profileId, identificationBarcode, locationBarcode, "
      + "sampleType, receivedDate, qcPassed, project_projectId, lastModifier, volume, emptied, boxPositionId, (SELECT boxId FROM BoxPosition WHERE BoxPosition.boxPositionId = "
      + TABLE_NAME
      + ".boxPositionId) AS boxId, (SELECT alias FROM Box, BoxPosition WHERE Box.boxId = BoxPosition.boxId AND BoxPosition.boxPositionId = "
      + TABLE_NAME + ".boxPositionId) AS boxAlias , (SELECT row FROM BoxPosition WHERE BoxPosition.boxPositionId = " + TABLE_NAME
      + ".boxPositionId) AS boxRow, (SELECT `column` FROM BoxPosition WHERE BoxPosition.boxPositionId = " + TABLE_NAME
      + ".boxPositionId) AS boxColumn " + "FROM " + TABLE_NAME;

  public static final String SAMPLES_SELECT_LIMIT = SAMPLES_SELECT + " ORDER BY sampleId DESC LIMIT ?";

  public static final String SAMPLES_SELECT_RECEIVED_DATE = SAMPLES_SELECT
      + " group by receivedDate,project_projectId ORDER BY DATE(receivedDate) DESC LIMIT ?";

  public static final String SAMPLE_SELECT_BY_ID = SAMPLES_SELECT + " " + "WHERE sampleId = ?";

  public static final String SAMPLE_SELECT_BY_ALIAS = SAMPLES_SELECT + " " + "WHERE alias = ?";

  public static final String SAMPLE_SELECT_BY_IDENTIFICATION_BARCODE = SAMPLES_SELECT + " " + "WHERE identificationBarcode = ?";

  public static final String SAMPLES_SELECT_FROM_BARCODE_LIST = SAMPLES_SELECT + " " + "WHERE identificationBarcode IN (";

  public static final String SAMPLE_SELECT_BY_BOX_POSITION_ID = SAMPLES_SELECT + " WHERE boxPositionId = ?";

  public static final String SAMPLES_SELECT_BY_SEARCH = SAMPLES_SELECT + " WHERE " + "identificationBarcode LIKE ? OR " + "name LIKE ? OR "
      + "alias LIKE ? OR " + "description LIKE ? OR " + "scientificName LIKE ? ";

  public static final String SAMPLE_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET name=:name, description=:description, scientificName=:scientificName, taxonIdentifier=:taxonIdentifier, alias=:alias, accession=:accession, securityProfile_profileId=:securityProfile_profileId, "
      + "identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode, sampleType=:sampleType, receivedDate=:receivedDate, "
      + "qcPassed=:qcPassed, project_projectId=:project_projectId, lastModifier=:lastModifier, volume=:volume, emptied=:emptied "
      + "WHERE sampleId=:sampleId";

  public static final String SAMPLE_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE sampleId=:sampleId";

  public static String SAMPLES_SELECT_BY_PROJECT_ID = SAMPLES_SELECT + " " + "WHERE project_projectId = ?";

  public static final String SAMPLES_SELECT_BY_EXPERIMENT_ID = SAMPLES_SELECT
      + " WHERE sampleId IN (SELECT samples_sampleId FROM Experiment_Sample WHERE Experiment_experimentId=?)";

  public static final String SAMPLE_SELECT_BY_LIBRARY_ID = SAMPLES_SELECT
      + " WHERE sampleId IN (SELECT sample_sampleId FROM Library WHERE libraryId=?)";

  public static final String EXPERIMENT_SAMPLE_DELETE_BY_SAMPLE_ID = "DELETE FROM Experiment_Sample "
      + "WHERE samples_sampleId=:samples_sampleId";

  public static final String SAMPLES_BY_RELATED_SUBMISSION = SAMPLES_SELECT
      + " WHERE sampleId IN (SELECT samples_sampleId FROM Submission_Sample WHERE submission_submissionId=?)";

  public static final String SAMPLE_TYPES_SELECT = "SELECT name FROM SampleType";

  protected static final Logger log = LoggerFactory.getLogger(SQLSampleDAO.class);

  private JdbcTemplate template;
  private Store<SecurityProfile> securityProfileDAO;
  private ProjectStore projectDAO;
  private LibraryStore libraryDAO;
  private SampleQcStore sampleQcDAO;
  private NoteStore noteDAO;
  private CascadeType cascadeType;
  private boolean autoGenerateIdentificationBarcodes;
  private ChangeLogStore changeLogDAO;
  private SecurityStore securityDAO;

  @Autowired
  private MisoNamingScheme<Sample> sampleNamingScheme;

  public MisoNamingScheme<Sample> getSampleNamingScheme() {
    return sampleNamingScheme;
  }

  public void setSampleNamingScheme(MisoNamingScheme<Sample> sampleNamingScheme) {
    this.sampleNamingScheme = sampleNamingScheme;
  }

  @Autowired
  private MisoNamingScheme<Sample> namingScheme;

  @Override
  public MisoNamingScheme<Sample> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Sample> namingScheme) {
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

  public void setNoteDAO(NoteStore noteDAO) {
    this.noteDAO = noteDAO;
  }

  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
  }

  public void setSampleQcDAO(SampleQcStore sampleQcDAO) {
    this.sampleQcDAO = sampleQcDAO;
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

  public void setAutoGenerateIdentificationBarcodes(boolean autoGenerateIdentificationBarcodes) {
    this.autoGenerateIdentificationBarcodes = autoGenerateIdentificationBarcodes;
  }

  public boolean getAutoGenerateIdentificationBarcodes() {
    return autoGenerateIdentificationBarcodes;
  }

  /**
   * Generates a unique barcode. Note that the barcode will change when the alias is changed.
   * 
   * @param sample
   */
  public void autoGenerateIdBarcode(Sample sample) {
    String barcode = sample.getName() + "::" + sample.getAlias();
    sample.setIdentificationBarcode(barcode);
  }

  private void purgeCache(Sample sample) {
    cacheManager.getCache("sampleCache").remove(sample);
  }

  private void purgeListCache(Sample s, boolean replace) {
    Cache cache = cacheManager.getCache("sampleListCache");
    DbUtils.updateListCache(cache, replace, s, Sample.class);
  }

  private void purgeListCache(Sample s) {
    purgeListCache(s, true);
  }

  // TODO finish this
  public int[] batchSave(final Collection<Sample> samples) throws IOException {
    List<SqlParameterSource> batch = new ArrayList<SqlParameterSource>();
    try {
      for (Sample sample : samples) {
        Long securityProfileId = sample.getSecurityProfile().getProfileId();
        if (this.cascadeType != null) {
          securityProfileId = securityProfileDAO.save(sample.getSecurityProfile());
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("alias", sample.getAlias());
        params.addValue("accession", sample.getAccession());
        params.addValue("description", sample.getDescription());
        params.addValue("scientificName", sample.getScientificName());
        params.addValue("taxonIdentifier", sample.getTaxonIdentifier());
        params.addValue("locationBarcode", sample.getLocationBarcode());
        params.addValue("sampleType", sample.getSampleType());
        params.addValue("receivedDate", sample.getReceivedDate());
        params.addValue("qcPassed", sample.getQcPassed().toString());
        params.addValue("project_projectId", sample.getProject().getProjectId());
        params.addValue("securityProfile_profileId", securityProfileId);
        params.addValue("lastModifier", sample.getLastModifier().getUserId());
        params.addValue("emptied", sample.isEmpty());
        params.addValue("volume", sample.getVolume());

        if (sampleNamingScheme.validateField("name", sample.getName()) && sampleNamingScheme.validateField("alias", sample.getAlias())) {
          batch.add(params);
          purgeCache(sample);
        } else {
          throw new IOException("Cannot save sample - invalid field:" + sample.toString());
        }
      }
      SimpleJdbcTemplate sTemplate = new SimpleJdbcTemplate(template);
      return sTemplate.batchUpdate(SAMPLE_UPDATE, batch.toArray(new SqlParameterSource[samples.size()]));
    } catch (MisoNamingException e) {
      throw new IOException("Cannot save sample - issue with naming scheme", e);
    }
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "sampleCache",
      "lazySampleCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(Sample sample) throws IOException {
    Long securityProfileId = sample.getSecurityProfile().getProfileId();
    if (this.cascadeType != null) {
      securityProfileId = securityProfileDAO.save(sample.getSecurityProfile());
    }
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", sample.getAlias());
    params.addValue("accession", sample.getAccession());
    params.addValue("description", sample.getDescription());
    params.addValue("scientificName", sample.getScientificName());
    params.addValue("taxonIdentifier", sample.getTaxonIdentifier());
    params.addValue("locationBarcode", sample.getLocationBarcode());
    params.addValue("sampleType", sample.getSampleType());
    params.addValue("receivedDate", sample.getReceivedDate());
    params.addValue("project_projectId", sample.getProject().getProjectId());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("lastModifier", sample.getLastModifier().getUserId());
    params.addValue("volume", sample.getVolume());
    params.addValue("emptied", sample.isEmpty());

    if (sample.getQcPassed() != null) {
      params.addValue("qcPassed", sample.getQcPassed().toString());
    } else {
      params.addValue("qcPassed", sample.getQcPassed());
    }

    if (sample.getId() == AbstractSample.UNSAVED_ID) {
      // if the sample naming scheme doesn't allow duplicates, and a sample alias already exists
      if (!sampleNamingScheme.allowDuplicateEntityNameFor("alias") && !listByAlias(sample.getAlias()).isEmpty()) {
        throw new IOException("NEW: A sample with this alias already exists in the database");
      } else {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("sampleId");
        try {
          sample.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

          String name = sampleNamingScheme.generateNameFor("name", sample);
          sample.setName(name);

          if (sampleNamingScheme.validateField("name", sample.getName()) && sampleNamingScheme.validateField("alias", sample.getAlias())) {
            if (autoGenerateIdentificationBarcodes) {
              autoGenerateIdBarcode(sample);
            } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user

            params.addValue("name", name);
            params.addValue("identificationBarcode", sample.getIdentificationBarcode());

            Number newId = insert.executeAndReturnKey(params);
            if (newId.longValue() != sample.getId()) {
              log.error("Expected Sample ID doesn't match returned value from database insert: rolling back...");
              new NamedParameterJdbcTemplate(template).update(SAMPLE_DELETE,
                  new MapSqlParameterSource().addValue("sampleId", newId.longValue()));
              throw new IOException("Something bad happened. Expected Sample ID doesn't match returned value from DB insert");
            }
          } else {
            throw new IOException("Cannot save sample - invalid field:" + sample.toString());
          }
        } catch (MisoNamingException e) {
          throw new IOException("Cannot save sample - issue with naming scheme", e);
        }
      }
    } else {
      SqlRowSet ss = template.queryForRowSet(SAMPLE_SELECT_BY_ALIAS, new Object[] { sample.getAlias() });
      if (!sampleNamingScheme.allowDuplicateEntityNameFor("alias") && ss.next() && ss.getLong("sampleId") != sample.getId()) {
        throw new IOException("UPD: A sample with this alias already exists in the database");
      } else {
        try {
          if (sampleNamingScheme.validateField("name", sample.getName()) && sampleNamingScheme.validateField("alias", sample.getAlias())) {
            params.addValue("sampleId", sample.getId());
            params.addValue("name", sample.getName());

            if (autoGenerateIdentificationBarcodes) {
              autoGenerateIdBarcode(sample);
            } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is already set
            params.addValue("identificationBarcode", sample.getIdentificationBarcode());
            NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
            namedTemplate.update(SAMPLE_UPDATE, params);
          } else {
            throw new IOException("Cannot save sample - invalid field value: " + sample.toString());
          }
        } catch (MisoNamingException e) {
          throw new IOException("Cannot save sample - issue with naming scheme", e);
        }
      }
    }

    if (this.cascadeType != null) {
      Project p = sample.getProject();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (p != null) {
          // set project progress to ACTIVE if in a prior waiting state (APPROVED)
          if (sample.getReceivedDate() != null && p.getProgress().equals(ProgressType.APPROVED)) {
            p.setProgress(ProgressType.ACTIVE);
          }

          projectDAO.save(p);
        }
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (p != null) {
          DbUtils.updateCaches(cacheManager, p, Project.class);
          for (ProjectOverview po : p.getOverviews()) {
            DbUtils.updateCaches(cacheManager, po, ProjectOverview.class);
          }
        }
      }

      if (!sample.getNotes().isEmpty()) {
        for (Note n : sample.getNotes()) {
          noteDAO.saveSampleNote(sample, n);
        }
      }

      purgeListCache(sample);
    }

    return sample.getId();
  }

  @Override
  @Cacheable(cacheName = "sampleListCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public List<Sample> listAll() {
    return template.query(SAMPLES_SELECT, new SampleMapper(true));
  }

  @Override
  public List<Sample> listAllWithLimit(long limit) throws IOException {
    return template.query(SAMPLES_SELECT_LIMIT, new Object[] { limit }, new SampleMapper(true));
  }

  @Override
  public List<Sample> listAllByReceivedDate(long limit) throws IOException {
    return template.query(SAMPLES_SELECT_RECEIVED_DATE, new Object[] { limit }, new SampleMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<Sample> listBySearch(String query) {
    String mySQLQuery = "%" + query.replaceAll("_", Matcher.quoteReplacement("\\_")) + "%";
    return template.query(SAMPLES_SELECT_BY_SEARCH, new String[] { mySQLQuery, mySQLQuery, mySQLQuery, mySQLQuery, mySQLQuery },
        new SampleMapper(true));
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "sampleCache",
      "lazySampleCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(Sample sample) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (sample.isDeletable()
        && (namedTemplate.update(SAMPLE_DELETE, new MapSqlParameterSource().addValue("sampleId", sample.getId())) == 1)) {
      Project p = sample.getProject();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (p != null) projectDAO.save(p);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (p != null) {
          DbUtils.updateCaches(cacheManager, p, Project.class);
        }
      }

      purgeListCache(sample, false);

      return true;
    }
    return false;
  }

  @Override
  @Cacheable(cacheName = "sampleCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public Sample get(long sampleId) throws IOException {
    List eResults = template.query(SAMPLE_SELECT_BY_ID, new Object[] { sampleId }, new SampleMapper());
    Sample e = eResults.size() > 0 ? (Sample) eResults.get(0) : null;
    return e;
  }

  @Override
  public Sample lazyGet(long sampleId) throws IOException {
    List eResults = template.query(SAMPLE_SELECT_BY_ID, new Object[] { sampleId }, new SampleMapper(true));
    Sample e = eResults.size() > 0 ? (Sample) eResults.get(0) : null;
    return e;
  }

  @Override
  public Sample getByBarcode(String barcode) throws IOException {
    List eResults = template.query(SAMPLE_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode }, new SampleMapper(true));
    Sample e = eResults.size() > 0 ? (Sample) eResults.get(0) : null;
    return e;
  }

  @Override
  public List<Sample> getByBarcodeList(List<String> barcodeList) {
    return DbUtils.getByBarcodeList(template, barcodeList, SAMPLES_SELECT_FROM_BARCODE_LIST, new SampleMapper(true));
  }

  @Override
  public Boxable getByPositionId(long positionId) {
    List<Sample> eResults = template.query(SAMPLE_SELECT_BY_BOX_POSITION_ID, new Object[] { positionId }, new SampleMapper(true));
    Sample e = eResults.size() > 0 ? eResults.get(0) : null;
    return e;
  }

  @Override
  public List<Sample> listByProjectId(long projectId) throws IOException {
    List<Sample> samples = template.query(SAMPLES_SELECT_BY_PROJECT_ID, new Object[] { projectId }, new SampleMapper(true));
    Collections.sort(samples);
    return samples;
  }

  @Override
  public List<Sample> listByExperimentId(long experimentId) throws IOException {
    return template.query(SAMPLES_SELECT_BY_EXPERIMENT_ID, new Object[] { experimentId }, new SampleMapper(true));
  }

  @Override
  public Collection<Sample> listByAlias(String alias) throws IOException {
    return template.query(SAMPLE_SELECT_BY_ALIAS, new Object[] { alias }, new SampleMapper(true));
  }

  @Override
  public List<String> listAllSampleTypes() throws IOException {
    return template.queryForList(SAMPLE_TYPES_SELECT, String.class);
  }

  @Override
  public List<Sample> listBySubmissionId(long submissionId) throws IOException {
    return template.query(SAMPLES_BY_RELATED_SUBMISSION, new Object[] { submissionId }, new SampleMapper());
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

  public class SampleMapper extends CacheAwareRowMapper<Sample> {
    public SampleMapper() {
      super(Sample.class);
    }

    public SampleMapper(boolean lazy) {
      super(Sample.class, lazy);
    }

    @Override
    public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("sampleId");
      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.info("Cache hit on map for sample " + id);
          log.info("Cache hit on map for sample with element " + element);
          Sample sample = (Sample) element.getObjectValue();
          if (sample == null) throw new NullPointerException("The cache is full of lies!!!");
          if (sample.getId() == 0) {
            DbUtils.updateCaches(lookupCache(cacheManager), id);
          } else {
            return (Sample) element.getObjectValue();
          }
        }
      }

      Sample s = dataObjectFactory.getSample();
      s.setId(id);
      s.setName(rs.getString("name"));
      s.setAlias(rs.getString("alias"));
      s.setAccession(rs.getString("accession"));
      s.setDescription(rs.getString("description"));
      s.setScientificName(rs.getString("scientificName"));
      s.setTaxonIdentifier(rs.getString("taxonIdentifier"));
      s.setIdentificationBarcode(rs.getString("identificationBarcode"));
      s.setLocationBarcode(rs.getString("locationBarcode"));
      s.setSampleType(rs.getString("sampleType"));
      s.setReceivedDate(rs.getDate("receivedDate"));
      s.setVolume(rs.getLong("volume"));
      s.setEmpty(rs.getBoolean("emptied"));
      s.setBoxPositionId(rs.getLong("boxPositionId"));
      s.setBoxAlias(rs.getString("boxAlias"));
      s.setBoxId(rs.getLong("boxId"));
      int row = rs.getInt("boxRow");
      if (!rs.wasNull()) s.setBoxPosition(BoxUtils.getPositionString(row, rs.getInt("boxColumn")));
      if (rs.getString("qcPassed") != null) {
        s.setQcPassed(Boolean.parseBoolean(rs.getString("qcPassed")));
      } else {
        s.setQcPassed(null);
      }

      try {
        s.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
        s.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        if (!isLazy()) {
          s.setProject(projectDAO.get(rs.getLong("project_projectId")));

          for (Library l : libraryDAO.listBySampleId(id)) {
            s.addLibrary(l);
          }

          for (SampleQC qc : sampleQcDAO.listBySampleId(id)) {
            s.addQc(qc);
          }

          s.setNotes(noteDAO.listBySample(id));
        } else {
          s.setProject(projectDAO.lazyGet(rs.getLong("project_projectId")));
        }
        s.getChangeLog().addAll(changeLogDAO.listAllById(TABLE_NAME, id));
      } catch (IOException e1) {
        log.error("sample row mapper", e1);
      } catch (MalformedLibraryException e) {
        log.error("sample row mapper", e);
      } catch (MalformedSampleQcException e) {
        log.error("sample row mapper", e);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), s));
      }
      return s;
    }
  }
}
