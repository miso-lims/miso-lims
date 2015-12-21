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

import javax.persistence.CascadeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractDilution;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.DilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.EmPCRStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Deprecated
public class SQLDilutionDAO implements DilutionStore {
  public static String DILUTION_SELECT_BY_ID_AND_LIBRARY_PLATFORM = "SELECT DISTINCT * " + "FROM Library l "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = l.libraryId "
      + "INNER JOIN emPCRDilution ed ON ed.library_libraryId = l.libraryId " + "WHERE ld.dilutionId = ? OR ed.dilutionId = ? "
      + "AND l.platformName = ?";

  public static String LIBRARY_DILUTION_SELECT = "SELECT dilutionId, name, concentration, library_libraryId, identificationBarcode, creationDate, dilutionUserName, securityProfile_profileId "
      + "FROM LibraryDilution";

  public static String LIBRARY_DILUTION_SELECT_BY_LIBRARY_PLATFORM = "SELECT ld.dilutionId, ld.name, ld.concentration, ld.library_libraryId, ld.identificationBarcode, ld.creationDate, ld.dilutionUserName, ld.securityProfile_profileId, l.platformName "
      + "FROM LibraryDilution ld, Library l " + "WHERE ld.library_libraryId = l.libraryId " + "AND l.platformName = ?";

  public static String LIBRARY_DILUTION_SELECT_BY_PROJECT_AND_LIBRARY_PLATFORM = "SELECT ld.* FROM Project p "
      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " + "WHERE li.platformName=? " + "AND p.projectId=?";

  public static String LIBRARY_DILUTION_SELECT_BY_PROJECT = "SELECT ld.* FROM Project p "
      + "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " + "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId "
      + "INNER JOIN LibraryDilution ld ON ld.library_libraryId = li.libraryId " + "WHERE p.projectId=?";

  public static final String LIBRARY_DILUTION_SELECT_BY_DILUTION_ID = LIBRARY_DILUTION_SELECT + " WHERE dilutionId=?";

  public static final String LIBRARY_DILUTION_SELECT_BY_LIBRARY_ID = LIBRARY_DILUTION_SELECT + " WHERE library_libraryId=?";

  public static final String LIBRARY_DILUTION_SELECT_BY_IDENTIFICATION_BARCODE = LIBRARY_DILUTION_SELECT + " WHERE identificationBarcode=?";

  public static final String LIBRARY_DILUTIONS_BY_RELATED_ILLUMINA_POOL_ID = "SELECT p.dilutions_dilutionId, l.dilutionId, l.name, l.concentration, l.library_libraryId, l.identificationBarcode, l.creationDate, l.dilutionUserName, l.securityProfile_profileId "
      + "FROM LibraryDilution l, Pool_LibraryDilution p " + "WHERE l.dilutionId=p.dilutions_dilutionId " + "AND p.pool_poolId=?";

  public static final String LIBRARY_DILUTIONS_BY_RELATED_EMPCR_POOL_ID = "SELECT p.dilutions_dilutionId, l.dilutionId, l.name, l.concentration, l.library_libraryId, l.identificationBarcode, l.creationDate, l.dilutionUserName, l.securityProfile_profileId "
      + "FROM LibraryDilution l, Pool_LibraryDilution p WHERE l.dilutionId=p.dilutions_dilutionId " + "AND p.name LIKE 'EFO%' "
      + "AND p.pool_poolId=?";

  public static final String LIBRARY_DILUTION_UPDATE = "UPDATE LibraryDilution "
      + "SET name=:name, concentration=:concentration, library_libraryId=:library_libraryId, identificationBarcode=:identificationBarcode, creationDate=:creationDate, securityProfile_profileId=:securityProfile_profileId "
      + "WHERE dilutionId=:dilutionId";

  public static final String LIBRARY_DILUTION_DELETE = "DELETE FROM LibraryDilution WHERE dilutionId=:dilutionId";

  public static String LIBRARY_DILUTION_SELECT_BY_SEARCH = "SELECT ld.dilutionId, ld.name, ld.concentration, ld.library_libraryId, ld.identificationBarcode, ld.creationDate, ld.dilutionUserName, ld.securityProfile_profileId "
      + "FROM LibraryDilution ld WHERE ld.name LIKE :search OR ld.identificationBarcode LIKE :search";

  public static String EMPCR_DILUTION_SELECT = "SELECT dilutionId, name, concentration, emPCR_pcrId, identificationBarcode, creationDate, dilutionUserName, securityProfile_profileId "
      + "FROM emPCRDilution";

  public static String EMPCR_DILUTION_SELECT_BY_LIBRARY_PLATFORM = "SELECT ed.dilutionId, ed.name, ed.concentration, ed.emPCR_pcrId, ed.identificationBarcode, ed.creationDate, ed.dilutionUserName, ed.securityProfile_profileId, e.dilution_dilutionId, l.platformName "
      + "FROM emPCRDilution ed, emPCR e, LibraryDilution ld, Library l WHERE ed.emPCR_pcrId = e.pcrId "
      + "AND ld.dilutionId = e.dilution_dilutionId AND ld.library_libraryId = l.libraryId " + "AND l.platformName = ?";

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

  public static final String EMPCR_DILUTION_SELECT_BY_LS454 = "SELECT p.dilutions_dilutionId, l.dilutionId, l.name, l.concentration, l.emPCR_pcrId, l.identificationBarcode, l.creationDate, l.dilutionUserName, l.securityProfile_profileId "
      + "FROM emPCRDilution l, Pool_emPCRDilution p " + "WHERE l.dilutionId=p.dilutions_dilutionId";

  public static final String EMPCR_DILUTION_SELECT_BY_SOLID = "SELECT p.dilutions_dilutionId, l.dilutionId, l.name, l.concentration, l.emPCR_pcrId, l.identificationBarcode, l.creationDate, l.dilutionUserName, l.securityProfile_profileId "
      + "FROM emPCRDilution l, Pool_emPCRDilution p " + "WHERE l.dilutionId=p.dilutions_dilutionId";

  public static final String EMPCR_DILUTIONS_BY_RELATED_LS454_POOL_ID = EMPCR_DILUTION_SELECT_BY_LS454 + " AND p.pool_poolId=?";

  public static final String EMPCR_DILUTIONS_BY_RELATED_SOLID_POOL_ID = EMPCR_DILUTION_SELECT_BY_SOLID + " AND p.pool_poolId=?";

  public static final String EMPCR_DILUTION_UPDATE = "UPDATE emPCRDilution "
      + "SET name=:name, concentration=:concentration, emPCR_pcrId=:emPCR_pcrId, identificationBarcode=:identificationBarcode, creationDate=:creationDate, securityProfile_profileId=:securityProfile_profileId "
      + "WHERE dilutionId=:dilutionId";

  public static final String EMPCR_DILUTION_DELETE = "DELETE FROM emPCRDilution WHERE dilutionId=:dilutionId";

  public static final String EMPCR_DILUTION_SELECT_BY_SEARCH = "SELECT ed.dilutionId, ed.name, ed.concentration, ed.emPCR_pcrId, ed.identificationBarcode, ed.creationDate, ed.dilutionUserName, ed.securityProfile_profileId, e.dilution_dilutionId "
      + "FROM emPCRDilution ed, emPCR e, LibraryDilution ld WHERE ed.emPCR_pcrId = e.pcrId " + "AND ld.dilutionId = e.dilution_dilutionId "
      + "AND (ed.name LIKE :search OR ld.name LIKE :search OR ed.identificationBarcode LIKE :search)";

  protected static final Logger log = LoggerFactory.getLogger(SQLDilutionDAO.class);

  private JdbcTemplate template;
  private EmPCRStore emPcrDAO;
  private LibraryStore libraryDAO;
  private Store<SecurityProfile> securityProfileDAO;
  private CascadeType cascadeType;

  @Autowired
  private MisoNamingScheme<Dilution> namingScheme;

  @Override
  public MisoNamingScheme<Dilution> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Dilution> namingScheme) {
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
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearch(String query, PlatformType platformType) {
    String squery = "%" + query + "%";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("search", squery);
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(LIBRARY_DILUTION_SELECT_BY_SEARCH, params, new LazyLibraryDilutionMapper());
  }

  @Override
  public List<LibraryDilution> listByLibraryId(long libraryId) throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT_BY_LIBRARY_ID, new Object[] { libraryId }, new LazyLibraryDilutionMapper());
  }

  @Override
  public List<? extends Dilution> listAllDilutionsByPlatform(PlatformType platformType) throws IOException {
    if (platformType.equals(PlatformType.ILLUMINA)) {
      return template.query(LIBRARY_DILUTION_SELECT, new LazyLibraryDilutionMapper());
    } else if (platformType.equals(PlatformType.LS454)) {
      return template.query(EMPCR_DILUTION_SELECT_BY_LS454, new LazyEmPCRDilutionMapper());
    } else if (platformType.equals(PlatformType.SOLID)) {
      return template.query(EMPCR_DILUTION_SELECT_BY_SOLID, new LazyEmPCRDilutionMapper());
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutions() throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT, new LazyLibraryDilutionMapper());
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT_BY_LIBRARY_PLATFORM, new Object[] { platformType.getKey() },
        new LazyLibraryDilutionMapper());
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException {
    return template.query(LIBRARY_DILUTION_SELECT_BY_PROJECT, new Object[] { projectId }, new LazyLibraryDilutionMapper());
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByPlatform(PlatformType platformType) throws IOException {
    return template.query(EMPCR_DILUTION_SELECT_BY_LIBRARY_PLATFORM, new Object[] { platformType.getKey() }, new LazyEmPCRDilutionMapper());
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByProjectId(long projectId) throws IOException {
    return template.query(EMPCR_DILUTION_SELECT_BY_PROJECT, new Object[] { projectId }, new LazyEmPCRDilutionMapper());
  }

  @Override
  public Collection<? extends Dilution> listAllDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    if (platformType.equals(PlatformType.ILLUMINA)) {
      return template.query(LIBRARY_DILUTION_SELECT_BY_PROJECT_AND_LIBRARY_PLATFORM, new Object[] { platformType.getKey(), projectId },
          new LazyLibraryDilutionMapper());
    } else if (platformType.equals(PlatformType.LS454) || platformType.equals(PlatformType.SOLID)) {
      List<Dilution> dils = new ArrayList<Dilution>();
      dils.addAll(template.query(LIBRARY_DILUTION_SELECT_BY_PROJECT_AND_LIBRARY_PLATFORM, new Object[] { platformType.getKey(), projectId },
          new LazyLibraryDilutionMapper()));
      dils.addAll(template.query(EMPCR_DILUTION_SELECT_BY_PROJECT_AND_LIBRARY_PLATFORM, new Object[] { platformType.getKey(), projectId },
          new LazyEmPCRDilutionMapper()));
      return dils;
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public Collection<? extends Dilution> listAllDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException {
    if (platformType.equals(PlatformType.ILLUMINA)) {
      return template.query(LIBRARY_DILUTIONS_BY_RELATED_ILLUMINA_POOL_ID, new Object[] { poolId }, new LazyLibraryDilutionMapper());
    } else if (platformType.equals(PlatformType.LS454)) {
      return template.query(EMPCR_DILUTIONS_BY_RELATED_LS454_POOL_ID, new Object[] { poolId }, new LazyEmPCRDilutionMapper());
    } else if (platformType.equals(PlatformType.SOLID)) {
      return template.query(EMPCR_DILUTIONS_BY_RELATED_SOLID_POOL_ID, new Object[] { poolId }, new LazyEmPCRDilutionMapper());
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsBySearch(String query, PlatformType platformType) {
    String squery = "%" + query + "%";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("search", squery);

    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(EMPCR_DILUTION_SELECT_BY_SEARCH, params, new LazyEmPCRDilutionMapper());
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutions() throws IOException {
    return template.query(EMPCR_DILUTION_SELECT, new LazyEmPCRDilutionMapper());
  }

  @Override
  public Collection<emPCRDilution> listAllByEmPCRId(long pcrId) throws IOException {
    return template.query(EMPCR_DILUTION_SELECT_BY_PCR_ID, new Object[] { pcrId }, new LazyEmPCRDilutionMapper());
  }

  @Override
  public Collection<LibraryDilution> listByIlluminaPoolId(long poolId) throws IOException {
    return template.query(LIBRARY_DILUTIONS_BY_RELATED_ILLUMINA_POOL_ID, new Object[] { poolId }, new LazyLibraryDilutionMapper());
  }

  @Override
  public Collection<emPCRDilution> listByLS454PoolId(long poolId) throws IOException {
    return template.query(EMPCR_DILUTIONS_BY_RELATED_LS454_POOL_ID, new Object[] { poolId }, new LazyEmPCRDilutionMapper());
  }

  @Override
  public Collection<emPCRDilution> listBySolidPoolId(long poolId) throws IOException {
    return template.query(EMPCR_DILUTIONS_BY_RELATED_SOLID_POOL_ID, new Object[] { poolId }, new LazyEmPCRDilutionMapper());
  }

  @Override
  public Collection<LibraryDilution> listByEmPCRPoolId(long poolId) throws IOException {
    return template.query(LIBRARY_DILUTIONS_BY_RELATED_EMPCR_POOL_ID, new Object[] { poolId }, new LazyLibraryDilutionMapper());
  }

  @Override
  public Dilution getDilutionByIdAndPlatform(long dilutionId, PlatformType platformType) throws IOException {
    if (platformType.equals(PlatformType.ILLUMINA)) {
      return getLibraryDilutionById(dilutionId);
    } else if (platformType.equals(PlatformType.LS454) || platformType.equals(PlatformType.SOLID)) {
      Dilution a = getEmPCRDilutionById(dilutionId);
      Dilution b = getLibraryDilutionById(dilutionId);

      if (a != null && a.getLibrary().getPlatformName().equals(platformType.getKey())) {
        return a;
      } else if (b != null && b.getLibrary().getPlatformName().equals(platformType.getKey())) {
        return b;
      }
      return null;
    } else {
      return null;
    }
  }

  @Override
  @Cacheable(cacheName = "libraryDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException {
    List eResults = template.query(LIBRARY_DILUTION_SELECT_BY_DILUTION_ID, new Object[] { dilutionId }, new LibraryDilutionMapper());
    LibraryDilution e = eResults.size() > 0 ? (LibraryDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    List eResults = template.query(LIBRARY_DILUTION_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode },
        new LibraryDilutionMapper());
    LibraryDilution e = eResults.size() > 0 ? (LibraryDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = Exception.class)
  @TriggersRemove(cacheName = "libraryDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long saveLibraryDilution(LibraryDilution dilution) throws IOException {
    Long securityProfileId = dilution.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) { // && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(dilution.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("concentration", dilution.getConcentration());
    params.addValue("library_libraryId", dilution.getLibrary().getId());
    params.addValue("creationDate", dilution.getCreationDate());
    params.addValue("securityProfile_profileId", securityProfileId);
    params.addValue("dilutionUserName", dilution.getDilutionCreator());

    if (dilution.getId() == AbstractDilution.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName("LibraryDilution").usingGeneratedKeyColumns("dilutionId");
      try {
        dilution.setId(DbUtils.getAutoIncrement(template, "LibraryDilution"));

        String name = namingScheme.generateNameFor("name", dilution);
        dilution.setName(name);

        if (namingScheme.validateField("name", dilution.getName())) {
          String barcode = name + "::" + dilution.getLibrary().getAlias();
          params.addValue("name", name);

          params.addValue("identificationBarcode", barcode);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != dilution.getId()) {
            log.error("Expected LibraryDilution ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(LIBRARY_DILUTION_DELETE,
                new MapSqlParameterSource().addValue("dilutionId", newId.longValue()));
            throw new IOException("Something bad happened. Expected LibraryDilution ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save LibraryDilution - invalid field:" + dilution.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save LibraryDilution - issue with naming scheme", e);
      }
    } else {
      try {
        if (namingScheme.validateField("name", dilution.getName())) {
          params.addValue("dilutionId", dilution.getId());
          params.addValue("name", dilution.getName());
          params.addValue("identificationBarcode", dilution.getName() + "::" + dilution.getLibrary().getAlias());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(LIBRARY_DILUTION_UPDATE, params);
        } else {
          throw new IOException("Cannot save LibraryDilution - invalid field:" + dilution.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save LibraryDilution - issue with naming scheme", e);
      }
    }

    if (this.cascadeType != null) {
      Library l = dilution.getLibrary();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (l != null) libraryDAO.save(l);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (l != null) {
          Cache pc = cacheManager.getCache("libraryCache");
          pc.remove(DbUtils.hashCodeCacheKeyFor(l.getId()));
        }
      }
    }

    return dilution.getId();
  }

  @Override
  @Cacheable(cacheName = "emPCRDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public emPCRDilution getEmPCRDilutionById(long dilutionId) throws IOException {
    List eResults = template.query(EMPCR_DILUTION_SELECT_BY_DILUTION_ID, new Object[] { dilutionId }, new LazyEmPCRDilutionMapper());
    emPCRDilution e = eResults.size() > 0 ? (emPCRDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcode(String barcode) throws IOException {
    List eResults = template.query(EMPCR_DILUTION_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode },
        new LazyEmPCRDilutionMapper());
    emPCRDilution e = eResults.size() > 0 ? (emPCRDilution) eResults.get(0) : null;
    return e;
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = "emPCRDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long saveEmPCRDilution(emPCRDilution dilution) throws IOException {
    Long securityProfileId = dilution.getSecurityProfile().getProfileId();
    if (securityProfileId == null || (this.cascadeType != null)) { // && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(dilution.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("concentration", dilution.getConcentration());
    params.addValue("emPCR_pcrId", dilution.getEmPCR().getId());
    params.addValue("creationDate", dilution.getCreationDate());
    params.addValue("dilutionUserName", dilution.getDilutionCreator());
    params.addValue("securityProfile_profileId", securityProfileId);

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
          Cache pc = cacheManager.getCache("empcrCache");
          pc.remove(DbUtils.hashCodeCacheKeyFor(e.getId()));
        }
      }
    }

    return dilution.getId();
  }

  @Override
  public long save(Dilution dilution) throws IOException {
    if (dilution instanceof LibraryDilution) {
      return saveLibraryDilution((LibraryDilution) dilution);
    } else if (dilution instanceof emPCRDilution) {
      return saveEmPCRDilution((emPCRDilution) dilution);
    } else {
      return AbstractDilution.UNSAVED_ID;
    }
  }

  @Override
  public Dilution get(long id) throws IOException {
    throw new UnsupportedOperationException("Simple get not available for dilutions");
  }

  @Override
  public Dilution lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<Dilution> listAll() throws IOException {
    ArrayList<Dilution> dilutions = new ArrayList<Dilution>();
    for (PlatformType platformType : PlatformType.values()) {
      dilutions.addAll(listAllDilutionsByPlatform(platformType));
    }
    return dilutions;
  }

  @Override
  public int count() throws IOException {
    int ld = template.queryForInt("SELECT count(*) FROM LibraryDilution");
    int ed = template.queryForInt("SELECT count(*) FROM emPCRDilution");
    return ld + ed;
  }

  @Override
  public boolean remove(Dilution dilution) throws IOException {
    if (dilution instanceof LibraryDilution) {
      return removeLibraryDilution((LibraryDilution) dilution);
    } else if (dilution instanceof emPCRDilution) {
      return removeEmPCRDilution((emPCRDilution) dilution);
    } else
      return false;
  }

  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = "libraryDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean removeLibraryDilution(LibraryDilution d) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (d.isDeletable()
        && (namedTemplate.update(LIBRARY_DILUTION_DELETE, new MapSqlParameterSource().addValue("dilutionId", d.getId())) == 1)) {
      Library l = d.getLibrary();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (l != null) libraryDAO.save(l);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (l != null) {
          Cache pc = cacheManager.getCache("libraryCache");
          pc.remove(DbUtils.hashCodeCacheKeyFor(l.getId()));
        }
      }
      return true;
    }
    return false;
  }

  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = "emPCRDilutionCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean removeEmPCRDilution(emPCRDilution d) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (d.isDeletable()
        && (namedTemplate.update(EMPCR_DILUTION_DELETE, new MapSqlParameterSource().addValue("dilutionId", d.getId())) == 1)) {
      emPCR e = d.getEmPCR();
      if (this.cascadeType.equals(CascadeType.PERSIST)) {
        if (e != null) emPcrDAO.save(e);
      } else if (this.cascadeType.equals(CascadeType.REMOVE)) {
        if (e != null) {
          Cache pc = cacheManager.getCache("empcrCache");
          pc.remove(DbUtils.hashCodeCacheKeyFor(e.getId()));
        }
      }
      return true;
    }
    return false;
  }

  public class LazyLibraryDilutionMapper implements RowMapper<LibraryDilution> {
    @Override
    public LibraryDilution mapRow(ResultSet rs, int rowNum) throws SQLException {
      LibraryDilution libraryDilution = dataObjectFactory.getLibraryDilution();
      libraryDilution.setId(rs.getLong("dilutionId"));
      libraryDilution.setName(rs.getString("name"));
      libraryDilution.setConcentration(rs.getDouble("concentration"));
      libraryDilution.setIdentificationBarcode(rs.getString("identificationBarcode"));
      libraryDilution.setCreationDate(rs.getDate("creationDate"));
      libraryDilution.setDilutionCreator(rs.getString("dilutionUserName"));

      try {
        libraryDilution.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        libraryDilution.setLibrary(libraryDAO.lazyGet(rs.getLong("library_libraryId")));
      } catch (IOException e) {
        log.error("Cannot map from database to LibraryDilution: ", e);
      }

      return libraryDilution;
    }
  }

  public class LibraryDilutionMapper implements RowMapper<LibraryDilution> {
    @Override
    public LibraryDilution mapRow(ResultSet rs, int rowNum) throws SQLException {
      LibraryDilution libraryDilution = dataObjectFactory.getLibraryDilution();
      libraryDilution.setId(rs.getLong("dilutionId"));
      libraryDilution.setName(rs.getString("name"));
      libraryDilution.setConcentration(rs.getDouble("concentration"));
      libraryDilution.setIdentificationBarcode(rs.getString("identificationBarcode"));
      libraryDilution.setCreationDate(rs.getDate("creationDate"));
      libraryDilution.setDilutionCreator(rs.getString("dilutionUserName"));

      try {
        libraryDilution.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        Library library = libraryDAO.get(rs.getLong("library_libraryId"));
        libraryDilution.setLibrary(library);
      } catch (IOException e1) {
        log.error("library dilution row mapper", e1);
      }
      return libraryDilution;
    }
  }

  public class LazyEmPCRDilutionMapper implements RowMapper<emPCRDilution> {
    @Override
    public emPCRDilution mapRow(ResultSet rs, int rowNum) throws SQLException {
      emPCRDilution pcrDilution = dataObjectFactory.getEmPCRDilution();
      pcrDilution.setId(rs.getLong("dilutionId"));
      pcrDilution.setName(rs.getString("name"));
      pcrDilution.setConcentration(rs.getDouble("concentration"));
      pcrDilution.setIdentificationBarcode(rs.getString("identificationBarcode"));
      pcrDilution.setCreationDate(rs.getDate("creationDate"));
      pcrDilution.setDilutionCreator(rs.getString("dilutionUserName"));

      try {
        pcrDilution.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        pcrDilution.setEmPCR(emPcrDAO.lazyGet(rs.getLong("emPCR_pcrId")));
      } catch (IOException e) {
        log.error("Cannot map from database to emPCRDilution: ", e);
      }
      return pcrDilution;
    }
  }

  public class EmPCRDilutionMapper implements RowMapper<emPCRDilution> {
    @Override
    public emPCRDilution mapRow(ResultSet rs, int rowNum) throws SQLException {
      emPCRDilution pcrDilution = dataObjectFactory.getEmPCRDilution();
      pcrDilution.setId(rs.getLong("dilutionId"));
      pcrDilution.setName(rs.getString("name"));
      pcrDilution.setConcentration(rs.getDouble("concentration"));
      pcrDilution.setIdentificationBarcode(rs.getString("identificationBarcode"));
      pcrDilution.setCreationDate(rs.getDate("creationDate"));
      pcrDilution.setDilutionCreator(rs.getString("dilutionUserName"));

      try {
        pcrDilution.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        pcrDilution.setEmPCR(emPcrDAO.get(rs.getLong("emPCR_pcrId")));
      } catch (IOException e1) {
        log.error("EmPCR dilution row mapper", e1);
      }
      return pcrDilution;
    }
  }
}
