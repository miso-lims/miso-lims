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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.google.common.collect.LinkedListMultimap;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlateMaterialType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.*;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DaoLookup;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import javax.persistence.CascadeType;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 12-Sep-2011
 * @since 0.1.1
 */
public class SQLPlateDAO implements PlateStore {
  private static final String TABLE_NAME = "Plate";

  public static final String PLATE_SELECT =
          "SELECT plateId, name, description, creationDate, plateMaterialType, identificationBarcode, locationBarcode, size, tagBarcodeId, securityProfile_profileId " +
          "FROM "+TABLE_NAME;

  public static final String PLATE_SELECT_BY_ID =
          PLATE_SELECT + " WHERE plateId = ?";

  public static final String PLATE_SELECT_BY_ID_BARCODE =
          PLATE_SELECT + " WHERE identificationBarcode = ?";

  public static final String PLATE_UPDATE =
          "UPDATE "+TABLE_NAME+" " +
          "SET plateId=:plateId, name=:name, description=:description, creationDate=:creationDate, plateMaterialType=:plateMaterialType, identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode, size=:size, tagBarcodeId=:tagBarcodeId, securityProfile_profileId=:securityProfile_profileId " +
          "WHERE plateId=:plateId";

  public static final String PLATE_DELETE =
          "DELETE FROM "+TABLE_NAME+" WHERE plateId=:plateId";

  public static final String PLATE_BARCODES_SELECT =
          "SELECT plateBarcodeId, name, sequence, materialType " +
          "FROM PlateBarcodes";

  public static final String PLATE_BARCODE_SELECT_BY_NAME =
          PLATE_BARCODES_SELECT +
          " WHERE name = ? ORDER by plateBarcodeId";

  public static final String PLATE_BARCODE_SELECT_BY_MATERIAL_TYPE =
          PLATE_BARCODES_SELECT +
          " WHERE materialType = ? ORDER by plateBarcodeId";

  public static final String PLATE_ELEMENT_SELECT_BY_PLATE_ID =
          "SELECT * FROM Plate_Elements " +
          "WHERE plate_plateId=? ORDER BY elementPosition";

  public static final String PLATE_ELEMENT_DELETE_BY_PLATE_ID =
          "DELETE FROM Plate_Elements " +
          "WHERE plate_plateId=:plate_plateId";

  public static String PLATES_SELECT_BY_PROJECT_ID =
          "SELECT pl.* FROM Project p " +
          "INNER JOIN Sample sa ON sa.project_projectId = p.projectId " +
          "INNER JOIN Library li ON li.sample_sampleId = sa.sampleId " +
          "INNER JOIN Plate_Elements pe ON li.libraryId = pe.elementId " +
          "INNER JOIN Plate pl ON pl.plateId = pe.plate_plateId " +
          "WHERE p.projectId = ? AND pe.elementType = '" + Library.class.getName() + "'";

  public static String PLATE_SELECT_BY_SEARCH =
          PLATE_SELECT + " WHERE name LIKE :search OR identificationBarcode LIKE :search";

  protected static final Logger log = LoggerFactory.getLogger(SQLPlateDAO.class);

  @Autowired
  private DataObjectFactory dataObjectFactory;

  private JdbcTemplate template;
  private CascadeType cascadeType;
  private LibraryStore libraryDAO;
  private SampleStore sampleDAO;
  private LibraryDilutionStore dilutionDAO;
  private Store<SecurityProfile> securityProfileDAO;

  @Autowired
  private DaoLookup daoLookup;

  public void setDaoLookup(DaoLookup daoLookup) {
      this.daoLookup = daoLookup;
    }

  @Autowired
  private MisoNamingScheme<Plate<? extends List<? extends Plateable>, ? extends Plateable>> namingScheme;

  @Override
  public MisoNamingScheme<Plate<? extends List<? extends Plateable>, ? extends Plateable>> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Plate<? extends List<? extends Plateable>, ? extends Plateable>> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setLibraryDAO(LibraryStore libraryDAO) {
    this.libraryDAO = libraryDAO;
  }

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  public void setDilutionDAO(LibraryDilutionStore dilutionDAO) {
    this.dilutionDAO = dilutionDAO;
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

  @Override
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> lazyGet(long plateId) throws IOException {
    List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> eResults = template.query(PLATE_SELECT_BY_ID, new Object[]{plateId}, new PlateMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  @Cacheable(cacheName="plateCache",
    keyGenerator = @KeyGenerator (
      name = "HashCodeCacheKeyGenerator",
      properties = {
        @Property(name="includeMethod", value="false"),
        @Property(name="includeParameterTypes", value="false")
      }
    )
  )
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> get(long plateId) throws IOException {
    List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> eResults = template.query(PLATE_SELECT_BY_ID, new Object[]{plateId}, new PlateMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> getPlateByIdentificationBarcode(String barcode) throws IOException {
    List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> eResults = template.query(PLATE_SELECT_BY_ID_BARCODE, new Object[]{barcode}, new PlateMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAll() throws IOException {
    return template.query(PLATE_SELECT, new PlateMapper());
  }

  @Override
  public List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listByProjectId(long projectId) throws IOException {
    List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> plates = template.query(PLATES_SELECT_BY_PROJECT_ID, new Object[]{projectId}, new PlateMapper(true));
    Collections.sort(plates);
    return plates;
  }

  @Override
  public List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listBySearch(String query) throws IOException {
    String squery = "%" + query + "%";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("search", squery);
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return namedTemplate.query(PLATE_SELECT_BY_SEARCH, params, new PlateMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM "+TABLE_NAME);
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = Exception.class)
  @TriggersRemove(
    cacheName={"plateCache", "lazyPlateCache"},
    keyGenerator = @KeyGenerator(
      name = "HashCodeCacheKeyGenerator",
      properties = {
        @Property(name="includeMethod", value="false"),
        @Property(name="includeParameterTypes", value="false")
      }
    )
  )
  public long save(Plate<? extends List<? extends Plateable>, ? extends Plateable> plate) throws IOException {
    Long securityProfileId = plate.getSecurityProfile().getProfileId();
    if (securityProfileId == SecurityProfile.UNSAVED_ID  ||
        (this.cascadeType != null)) { // && this.cascadeType.equals(CascadeType.PERSIST))) {
      securityProfileId = securityProfileDAO.save(plate.getSecurityProfile());
    }

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("description", plate.getDescription())
          .addValue("creationDate", plate.getCreationDate())
          .addValue("plateMaterialType", plate.getPlateMaterialType().getKey())
          .addValue("locationBarcode", plate.getLocationBarcode())
          .addValue("size", plate.getSize())
          .addValue("securityProfile_profileId", securityProfileId);

    if (plate.getTagBarcode() != null) {
      params.addValue("tagBarcodeId", plate.getTagBarcode().getId());
    } 

    if (plate.getId() == AbstractPlate.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                            .withTableName(TABLE_NAME)
                            .usingGeneratedKeyColumns("plateId");
      try {
        plate.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", plate);
        plate.setName(name);

        if (namingScheme.validateField("name", plate.getName())) {
          String barcode = "";
          if (plate.getTagBarcode() != null) {
            barcode = plate.getName() + "::" + plate.getTagBarcode();
          }
          else {
            //TODO this should be alias
            barcode = plate.getName() + "::" + plate.getDescription();
          }          
          params.addValue("name", name);

          params.addValue("identificationBarcode", barcode);

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != plate.getId()) {
            log.error("Expected Plate ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(PLATE_DELETE, new MapSqlParameterSource().addValue("plateId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Plate ID doesn't match returned value from DB insert");
          }
        }
        else {
          throw new IOException("Cannot save Plate - invalid field:" + plate.toString());
        }
      }
      catch (MisoNamingException e) {
        throw new IOException("Cannot save Plate - issue with naming scheme", e);
      }
      /*
      String name = "PLA"+ DbUtils.getAutoIncrement(template, TABLE_NAME);
      params.addValue("name", name);
      params.addValue("identificationBarcode", name + "::" + plate.getTagBarcode());
      Number newId = insert.executeAndReturnKey(params);
      plate.setPlateId(newId.longValue());
      plate.setName(name);
      */
    }
    else {
      if (plate.getTagBarcode() != null) {
        params.addValue("tagBarcodeId", plate.getTagBarcode().getId());
      } else {
        params.addValue("tagBarcodeId", null);
      }
      try {
        String plateBarcode = "";
        if (plate.getTagBarcode() != null) {
          plateBarcode = plate.getName() + "::" + plate.getTagBarcode();
        }
        else {
          //TODO this should be alias
          plateBarcode = plate.getName() + "::" + plate.getDescription();
        }
        if (namingScheme.validateField("name", plate.getName())) {
          params.addValue("plateId", plate.getId())
                .addValue("name", plate.getName())
                .addValue("description", plate.getDescription())
                .addValue("identificationBarcode", plateBarcode)
                .addValue("locationBarcode", plate.getLocationBarcode());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          namedTemplate.update(PLATE_UPDATE, params);
        }
        else {
          throw new IOException("Cannot save Plate - invalid field:" + plate.toString());
        }
      }
      catch (MisoNamingException e) {
        throw new IOException("Cannot save Plate - issue with naming scheme", e);
      }
      /*
      params.addValue("plateId", plate.getPlateId());
      params.addValue("name", plate.getName());
      params.addValue("identificationBarcode", plate.getName() + "::" + plate.getTagBarcode());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(PLATE_UPDATE, params);
      */
    }

    if (this.cascadeType != null && this.cascadeType.equals(CascadeType.PERSIST)) {
      if (!plate.getElements().isEmpty()) {
        String eType = plate.getElementType().getName();
        MapSqlParameterSource eparams = new MapSqlParameterSource();
        eparams.addValue("plate_plateId", plate.getId());
        NamedParameterJdbcTemplate nt = new NamedParameterJdbcTemplate(template);
        nt.update(PLATE_ELEMENT_DELETE_BY_PLATE_ID, eparams);

        SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
                .withTableName("Plate_Elements");

        int pos = 1;
        for (Plateable n : plate.getElements()) {
          if (n.getId() == 0) {
            Store<? super Plateable> dao = daoLookup.lookup(n.getClass());
            if (dao != null) {
              dao.save(n);
            }
            else {
              log.error("No dao class found for " + n.getClass().getName());
            }
          }
          MapSqlParameterSource ltParams = new MapSqlParameterSource();
          ltParams.addValue("plate_plateId", plate.getId())
                  .addValue("elementType", eType)
                  .addValue("elementPosition", pos)
                  .addValue("elementId", n.getId());

          eInsert.execute(ltParams);
          pos++;
        }
      }
    }    

    return plate.getId();
  }

  @Override
  @TriggersRemove(
    cacheName={"plateCache", "lazyPlateCache"},
    keyGenerator = @KeyGenerator(
      name = "HashCodeCacheKeyGenerator",
      properties = {
        @Property(name="includeMethod", value="false"),
        @Property(name="includeParameterTypes", value="false")
      }
    )
  )
  public boolean remove(Plate plate) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (plate.isDeletable() &&
           (namedTemplate.update(PLATE_DELETE,
                                 new MapSqlParameterSource().addValue("plateId", plate.getId())) == 1)) {
      MapSqlParameterSource eparams = new MapSqlParameterSource();
      eparams.addValue("plate_plateId", plate.getId());
      namedTemplate.update(PLATE_ELEMENT_DELETE_BY_PLATE_ID, eparams);
      return true;
    }
    return false;
  }

  public class PlateMapper extends CacheAwareRowMapper<Plate<? extends List<? extends Plateable>, ? extends Plateable>> {
    public PlateMapper() {
      super((Class<Plate<? extends List<? extends Plateable>, ? extends Plateable>>)((ParameterizedType)new TypeReference<Plate<? extends List<? extends Plateable>, ? extends Plateable>>(){}.getType()).getRawType());
    }

    public PlateMapper(boolean lazy) {
      super((Class<Plate<? extends List<? extends Plateable>, ? extends Plateable>>)((ParameterizedType)new TypeReference<Plate<? extends List<? extends Plateable>, ? extends Plateable>>(){}.getType()).getRawType(), lazy);
    }

    @Override
    public Plate<? extends List<? extends Plateable>, ? extends Plateable> mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("plateId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Plate " + id);
          return (Plate<? extends List<? extends Plateable>, ? extends Plateable>)element.getObjectValue();
        }
      }

      int plateSize = rs.getInt("size");
      
      //Plate<LinkedList<Plateable>, ? extends Plateable> plate = dataObjectFactory.<LinkedList<Plateable>, Plateable> getPlateOfSize(plateSize);
      Plate<LinkedList<Plateable>, Plateable> plate = dataObjectFactory.getPlateOfSize(plateSize);
      plate.setId(id);
      plate.setName(rs.getString("name"));
      plate.setCreationDate(rs.getDate("creationDate"));
      plate.setDescription(rs.getString("description"));
      plate.setIdentificationBarcode(rs.getString("identificationBarcode"));
      plate.setLocationBarcode(rs.getString("locationBarcode"));

      //plate.setLastUpdated(rs.getTimestamp("lastUpdated"));

      try {
        plate.setSecurityProfile(securityProfileDAO.get(rs.getLong("securityProfile_profileId")));
        plate.setPlateMaterialType(PlateMaterialType.get(rs.getString("plateMaterialType")));
        plate.setTagBarcode(libraryDAO.getTagBarcodeById(rs.getLong("tagBarcodeId")));

        if (!isLazy()) {
          plate.setElements(resolvePlateElements(plate.getId()));
        }
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id) ,plate));
      }

      return plate;
    }
  }

  private LinkedList<Plateable> resolvePlateElements(long plateId) throws IOException, SQLException {
    try {
      LinkedList<Plateable> elements = new LinkedList<Plateable>();
      List<Map<String, Object>> rows = template.queryForList(PLATE_ELEMENT_SELECT_BY_PLATE_ID, plateId);
      for (Map<String, Object> map : rows) {
        Class<? extends Plateable> clz = Class.forName((String)map.get("elementType")).asSubclass(Plateable.class);
        Store<? extends Plateable> dao = daoLookup.lookup(clz);
        if (dao != null) {
          elements.add(dao.get((Long) map.get("elementId")));
        }
        else {
          throw new SQLException("No DAO found or more than one found.");
        }
      }
      return elements;
    }
    catch (ClassNotFoundException e) {
      throw new IOException(e);
    }
  }

  private Store<? extends Plateable> daoLookup(Class<?> elementType) throws IllegalArgumentException {
    if (Plateable.class.isAssignableFrom(elementType)) {
      if (Library.class.isAssignableFrom(elementType)) {
        return libraryDAO;
      }
      else if (Sample.class.isAssignableFrom(elementType)) {
        return sampleDAO;
      }
      else if (Dilution.class.isAssignableFrom(elementType)) {
        return dilutionDAO;
      }
      else {
        return null;
      }
    }
    else {
      throw new IllegalArgumentException("Element type " + elementType.getName() + " is not a valid Plateable type");
    }
  }
}
