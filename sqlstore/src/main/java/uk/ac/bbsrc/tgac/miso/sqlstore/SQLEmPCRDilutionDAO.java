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
import java.util.List;

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.EmPCRDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.EmPCRStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
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
public class SQLEmPCRDilutionDAO implements EmPCRDilutionStore {
  public static String DILUTION_SELECT_BY_ID_AND_LIBRARY_PLATFORM = "SELECT DISTINCT * " + "FROM Library l "
      + "INNER JOIN emPCRDilution ed ON ed.library_libraryId = l.libraryId " + "WHERE ld.dilutionId = ? OR ed.dilutionId = ? "
      + "AND l.platformName = ?";

  public static String EMPCR_DILUTION_SELECT = "SELECT dilutionId, name, concentration, emPCR_pcrId, identificationBarcode, creationDate, dilutionUserName, securityProfile_profileId "
      + "FROM emPCRDilution";

  public static String EMPCR_DILUTION_SELECT_BY_LIBRARY_PLATFORM = "SELECT ed.dilutionId, ed.name, ed.concentration, ed.emPCR_pcrId, ed.identificationBarcode, ed.creationDate, ed.dilutionUserName, ed.securityProfile_profileId, e.dilution_dilutionId, l.platformName "
      + "FROM emPCRDilution ed, emPCR e, LibraryDilution ld, Library l " + "WHERE ed.emPCR_pcrId = e.pcrId "
      + "AND ld.dilutionId = e.dilution_dilutionId " + "AND ld.library_libraryId = l.libraryId " + "AND l.platformName = ?";

  public static String EMPCR_DILUTION_SELECT_BY_PROJECT_AND_LIBRARY_PLATFORM = "SELECT ed.* FROM Project p "
      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId "
      + "INNER JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " + "INNER JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId "
      + "WHERE li.platformName=? " + "AND p.projectId=?";

  public static String EMPCR_DILUTION_SELECT_BY_PROJECT = "SELECT ed.* FROM Project p "
      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId "
      + "INNER JOIN emPCR e ON e.dilution_dilutionId = ld.dilutionId " + "INNER JOIN emPCRDilution ed ON ed.emPCR_pcrId = e.pcrId "
      + "WHERE p.projectId=?";

  public static final String EMPCR_DILUTION_SELECT_BY_PCR_ID = EMPCR_DILUTION_SELECT + " WHERE emPCR_pcrId=?";

  public static final String EMPCR_DILUTION_SELECT_BY_DILUTION_ID = EMPCR_DILUTION_SELECT + " WHERE dilutionId=?";

  public static final String EMPCR_DILUTION_SELECT_BY_IDENTIFICATION_BARCODE = EMPCR_DILUTION_SELECT + " WHERE identificationBarcode=?";

  public static final String EMPCR_DILUTIONS_BY_RELATED_POOL_ID = EMPCR_DILUTION_SELECT + " AND p.pool_poolId=?";

  public static final String EMPCR_DILUTION_UPDATE = "UPDATE emPCRDilution "
      + "SET name=:name, concentration=:concentration, emPCR_pcrId=:emPCR_pcrId, identificationBarcode=:identificationBarcode, creationDate=:creationDate, securityProfile_profileId=:securityProfile_profileId "
      + "WHERE dilutionId=:dilutionId";

  public static final String EMPCR_DILUTION_DELETE = "DELETE FROM emPCRDilution WHERE dilutionId=:dilutionId";

  public static final String EMPCR_DILUTION_SELECT_BY_SEARCH = "SELECT ed.dilutionId, ed.name, ed.concentration, ed.emPCR_pcrId, ed.identificationBarcode, ed.creationDate, ed.dilutionUserName, ed.securityProfile_profileId, e.dilution_dilutionId "
      + "FROM emPCRDilution ed, emPCR e, LibraryDilution ld " + "WHERE ed.emPCR_pcrId = e.pcrId "
      + "AND ld.dilutionId = e.dilution_dilutionId "
      + "AND (ed.name LIKE :search OR ld.name LIKE :search OR ed.identificationBarcode LIKE :search)";

  protected static final Logger log = LoggerFactory.getLogger(SQLEmPCRDilutionDAO.class);

  private JdbcTemplate template;
  private EmPCRStore emPcrDAO;
  private LibraryStore libraryDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private CascadeType cascadeType;

  @Autowired
  private MisoNamingScheme<emPCRDilution> namingScheme;

  @Override
  public MisoNamingScheme<emPCRDilution> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<emPCRDilution> namingScheme) {
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

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
  }

  public void setEmPcrDAO(EmPCRStore emPcrDAO) {
    this.emPcrDAO = emPcrDAO;
  }

  public Store<SecurityProfile> getSecurityProfileDAO() {
    return securityProfileDAO;
  }

  public void setSecurityProfileDAO(Store<SecurityProfile> securityProfileDAO) {
    this.securityProfileDAO = securityProfileDAO;
  }

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByPlatformAndSearch(String query, PlatformType platformType) throws IOException {
    return listAllEmPcrDilutionsBySearch(query, platformType);
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByPlatform(PlatformType platformType) throws IOException {
    return template.query(EMPCR_DILUTION_SELECT_BY_LIBRARY_PLATFORM, new Object[] { platformType.getKey() }, new EmPCRDilutionMapper(true));
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByProjectId(long projectId) throws IOException {
    return template.query(EMPCR_DILUTION_SELECT_BY_PROJECT, new Object[] { projectId }, new EmPCRDilutionMapper(true));
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    List<emPCRDilution> dils = new ArrayList<emPCRDilution>();
    dils.addAll(template.query(EMPCR_DILUTION_SELECT_BY_PROJECT_AND_LIBRARY_PLATFORM, new Object[] { platformType.getKey(), projectId },
        new EmPCRDilutionMapper(true)));
    return dils;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException {
    return template.query(EMPCR_DILUTIONS_BY_RELATED_POOL_ID, new Object[] { poolId }, new EmPCRDilutionMapper(true));
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsBySearch(String query, PlatformType platformType) {
    String squery = "%" + query + "%";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("search", squery);

    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(EMPCR_DILUTION_SELECT_BY_SEARCH, params, new EmPCRDilutionMapper(true));
  }

  @Override
  public Collection<emPCRDilution> listAll() throws IOException {
    return template.query(EMPCR_DILUTION_SELECT, new EmPCRDilutionMapper(true));
  }

  @Override
  public Collection<emPCRDilution> listAllByEmPCRId(long pcrId) throws IOException {
    return template.query(EMPCR_DILUTION_SELECT_BY_PCR_ID, new Object[] { pcrId }, new EmPCRDilutionMapper(true));
  }

  @Override
  public emPCRDilution getEmPcrDilutionByIdAndPlatform(long dilutionId, PlatformType platformType) throws IOException {
    emPCRDilution b = get(dilutionId);
    if (b != null && b.getLibrary().getPlatformName().equals(platformType.getKey())) {
      return b;
    }
    return null;
  }

  @Override
  public emPCRDilution getEmPcrDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    return getEmPcrDilutionByBarcode(barcode);
  }

  @Override
  @Cacheable(cacheName = "emPCRDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public emPCRDilution get(long dilutionId) throws IOException {
    List eResults = template.query(EMPCR_DILUTION_SELECT_BY_DILUTION_ID, new Object[] { dilutionId }, new EmPCRDilutionMapper(true));
    emPCRDilution e = eResults.size() > 0 ? (emPCRDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  public emPCRDilution lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public emPCRDilution getEmPcrDilutionByBarcode(String barcode) throws IOException {
    List eResults = template.query(EMPCR_DILUTION_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode },
        new EmPCRDilutionMapper(true));
    emPCRDilution e = eResults.size() > 0 ? (emPCRDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "emPCRDilutionCache",
      "lazyEmPCRDilutionCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(emPCRDilution dilution) throws IOException {
    Long securityProfileId = dilution.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) { // && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(dilution.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("concentration", dilution.getConcentration()).addValue("emPCR_pcrId", dilution.getEmPCR().getId())
        .addValue("creationDate", dilution.getCreationDate()).addValue("dilutionUserName", dilution.getDilutionCreator())
        .addValue("securityProfile_profileId", securityProfileId);

    if (dilution.getId() == AbstractDilution.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName("emPCRDilution").usingGeneratedKeyColumns("dilutionId");

      try {
        dilution.setId(DbUtils.getAutoIncrement(template, "emPCRDilution"));

        String name = namingScheme.generateNameFor("name", dilution);
        dilution.setName(name);
        if (namingScheme.validateField("name", dilution.getName())) {
          String barcode = name + "::" + dilution.getEmPCR().getName();
          params.addValue("name", name);

          params.addValue("identificationBarcode", barcode);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != dilution.getId()) {
            log.error("Expected emPCRDilution ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(EMPCR_DILUTION_DELETE,
                new MapSqlParameterSource().addValue("dilutionId", newId.longValue()));
            throw new IOException("Something bad happened. Expected emPCRDilution ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save emPCRDilution - invalid field:" + dilution.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save emPCRDilution - issue with naming scheme", e);
      }
    } else {
      try {
        if (namingScheme.validateField("name", dilution.getName())) {
          params.addValue("dilutionId", dilution.getId());
          params.addValue("name", dilution.getName());
          params.addValue("identificationBarcode", dilution.getName() + "::" + dilution.getLibrary().getAlias());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(EMPCR_DILUTION_UPDATE, params);
        } else {
          throw new IOException("Cannot save emPCRDilution - invalid field:" + dilution.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save emPCRDilution - issue with naming scheme", e);
      }
    }

    if (this.cascadeType != null) {
      emPCR e = dilution.getEmPCR();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (e != null) emPcrDAO.save(e);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (e != null) {
          DbUtils.updateCaches(cacheManager, e, emPCR.class);
        }
      }
    }

    return dilution.getId();
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM emPCRDilution");
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = { "emPCRDilutionCache",
      "lazyEmPCRDilutionCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
          @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(emPCRDilution d) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (d.isDeletable()
        && (namedTemplate.update(EMPCR_DILUTION_DELETE, new MapSqlParameterSource().addValue("dilutionId", d.getId())) == 1)) {
      emPCR e = d.getEmPCR();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (e != null) emPcrDAO.save(e);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (e != null) {
          DbUtils.updateCaches(cacheManager, e, emPCR.class);
        }
      }
      return true;
    }
    return false;
  }

  public class EmPCRDilutionMapper extends CacheAwareRowMapper<emPCRDilution> {
    public EmPCRDilutionMapper() {
      super(emPCRDilution.class);
    }

    public EmPCRDilutionMapper(boolean lazy) {
      super(emPCRDilution.class, lazy);
    }

    @Override
    public emPCRDilution mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("dilutionId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for emPCRDilution " + id);
          return (emPCRDilution) element.getObjectValue();
        }
      }

      emPCRDilution pcrDilution = dataObjectFactory.getEmPCRDilution();
      pcrDilution.setId(id);
      pcrDilution.setName(rs.getString("name"));
      pcrDilution.setConcentration(rs.getDouble("concentration"));
      pcrDilution.setIdentificationBarcode(rs.getString("identificationBarcode"));
      pcrDilution.setCreationDate(rs.getDate("creationDate"));
      pcrDilution.setDilutionCreator(rs.getString("dilutionUserName"));

      try {
        pcrDilution.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        if (!isLazy()) {
          pcrDilution.setEmPCR(emPcrDAO.get(rs.getLong("emPCR_pcrId")));
        } else {
          pcrDilution.setEmPCR(emPcrDAO.lazyGet(rs.getLong("emPCR_pcrId")));
        }
      } catch (IOException e1) {
        log.error("EmPCR dilution row mapper", e1);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), pcrDilution));
      }

      return pcrDilution;
    }
  }
}
