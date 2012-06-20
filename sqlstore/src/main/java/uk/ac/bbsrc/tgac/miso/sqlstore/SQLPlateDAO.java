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
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractPlate;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlateMaterialType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.PlateStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import javax.persistence.CascadeType;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

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
  public static final String PLATE_SELECT =
          "SELECT plateId, name, description, creationDate, plateMaterialType, identificationBarcode, locationBarcode, size, tagBarcodeId, securityProfile_profileId " +
          "FROM Plate";

  public static final String PLATE_SELECT_BY_ID =
          PLATE_SELECT + " WHERE plateId = ?";

  public static final String PLATE_SELECT_BY_ID_BARCODE =
          PLATE_SELECT + " WHERE identificationBarcode = ?";

  public static final String PLATE_UPDATE =
          "UPDATE Plate " +
          "SET plateId=:plateId, name=:name, description=:description, creationDate=:creationDate, plateMaterialType=:plateMaterialType, identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode, size=:size, tagBarcodeId=:tagBarcodeId, securityProfile_profileId=:securityProfile_profileId " +
          "WHERE plateId=:plateId";

  public static final String PLATE_DELETE =
          "DELETE FROM Plate WHERE plateId=:plateId";

  public static final String PLATE_BARCODES_SELECT =
          "SELECT plateBarcodeId, name, sequence, materialType " +
          "FROM PlateBarcodes";

  public static final String PLATE_BARCODE_SELECT_BY_NAME =
          PLATE_BARCODES_SELECT +
          " WHERE name = ? ORDER by plateBarcodeId";

  public static final String PLATE_BARCODE_SELECT_BY_MATERIAL_TYPE =
          PLATE_BARCODES_SELECT +
          " WHERE materialType = ? ORDER by plateBarcodeId";

  protected static final Logger log = LoggerFactory.getLogger(SQLPlateDAO.class);

  @Autowired
  private DataObjectFactory dataObjectFactory;

  private JdbcTemplate template;
  private CascadeType cascadeType;
  private LibraryStore libraryDAO;
  private Store<SecurityProfile> securityProfileDAO;

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
  public Plate lazyGet(long plateId) throws IOException {
    List eResults = template.query(PLATE_SELECT_BY_ID, new Object[]{plateId}, new LazyPlateMapper());
    return eResults.size() > 0 ? (Plate) eResults.get(0) : null;
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
  public Plate get(long plateId) throws IOException {
    List eResults = template.query(PLATE_SELECT_BY_ID, new Object[]{plateId}, new PlateMapper());
    return eResults.size() > 0 ? (Plate) eResults.get(0) : null;
  }

  public Plate getPlateByIdentificationBarcode(String barcode) throws IOException {
    List eResults = template.query(PLATE_SELECT_BY_ID_BARCODE, new Object[]{barcode}, new PlateMapper());
    return eResults.size() > 0 ? (Plate) eResults.get(0) : null;
  }

  @Override
  public Collection<Plate> listAll() throws IOException {
    return template.query(PLATE_SELECT, new PlateMapper());
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = Exception.class)
  @TriggersRemove(
    cacheName="plateCache",
    keyGenerator = @KeyGenerator(
      name = "HashCodeCacheKeyGenerator",
      properties = {
        @Property(name="includeMethod", value="false"),
        @Property(name="includeParameterTypes", value="false")
      }
    )
  )
  public long save(Plate plate) throws IOException {
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
          .addValue("tagBarcodeId", plate.getTagBarcode().getTagBarcodeId())
          .addValue("securityProfile_profileId", securityProfileId);

    if (plate.getPlateId() == AbstractPlate.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                            .withTableName("Plate")
                            .usingGeneratedKeyColumns("plateId");
      String name = "PLA"+ DbUtils.getAutoIncrement(template, "Plate");
      params.addValue("name", name);
      params.addValue("identificationBarcode", name + "::" + plate.getTagBarcode());
      Number newId = insert.executeAndReturnKey(params);
      plate.setPlateId(newId.longValue());
      plate.setName(name);
    }
    else {
      params.addValue("plateId", plate.getPlateId());
      params.addValue("name", plate.getName());
      params.addValue("identificationBarcode", plate.getName() + "::" + plate.getTagBarcode());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(PLATE_UPDATE, params);
    }

    if (this.cascadeType != null && this.cascadeType.equals(CascadeType.PERSIST)) {
      if (!plate.getElements().isEmpty()) {

        /*
        MapSqlParameterSource eparams = new MapSqlParameterSource();
        eparams.addValue("library_libraryId", library.getLibraryId());
        NamedParameterJdbcTemplate libNamedTemplate = new NamedParameterJdbcTemplate(template);
        libNamedTemplate.update(LIBRARY_TAGBARCODE_DELETE_BY_LIBRARY_ID, eparams);

        SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template)
                .withTableName("Library_TagBarcode");

        MapSqlParameterSource ltParams = new MapSqlParameterSource();
        ltParams.addValue("library_libraryId", library.getLibraryId())
                .addValue("barcode_barcodeId", library.getTagBarcode().getTagBarcodeId());

        eInsert.execute(ltParams);
        */
      }
    }    

    return plate.getPlateId();
  }

  @Override
  @TriggersRemove(
    cacheName="plateCache",
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
    return plate.isDeletable() &&
           (namedTemplate.update(PLATE_DELETE,
                                 new MapSqlParameterSource().addValue("plateId", plate.getPlateId())) == 1);
  }

  public class LazyPlateMapper implements RowMapper<Plate> {
    public Plate mapRow(ResultSet rs, int rowNum) throws SQLException {
      int plateSize = rs.getInt("size");

      Plate plate = dataObjectFactory.getPlateOfSize(plateSize);
      plate.setPlateId(rs.getLong("plateId"));
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
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }
      return plate;
    }
  }

  public class PlateMapper implements RowMapper<Plate> {
    public Plate mapRow(ResultSet rs, int rowNum) throws SQLException {
      int plateSize = rs.getInt("size");
      
      Plate plate = dataObjectFactory.getPlateOfSize(plateSize);
      plate.setPlateId(rs.getLong("plateId"));
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

        //plate.setElements();
      }
      catch (IOException e1) {
        e1.printStackTrace();
      }
      return plate;
    }
  }
}
