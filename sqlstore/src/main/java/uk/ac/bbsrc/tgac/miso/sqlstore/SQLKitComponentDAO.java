/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.eaglegenomics.simlims.core.Note;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.KitComponent;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitComponentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.store.KitComponentDescriptorStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitComponentStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.DateUtils;
import uk.ac.bbsrc.tgac.miso.core.util.KitUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey, Michal Zak
 * @since 0.0.2
 */
public class SQLKitComponentDAO implements KitComponentStore {
  private static final String TABLE_NAME = "KitComponent";

  public static final String KIT_COMPONENTS_SELECT = "SELECT kc.kitComponentId, kc.identificationBarcode, kc.locationBarcode, kc.lotNumber, kc.kitReceivedDate, kc.kitExpiryDate, kc.exhausted, kc.kitComponentDescriptorId "
      +
      "FROM " + TABLE_NAME + " kc";


  public static final String KIT_COMPONENT_SELECT_BY_ID = KIT_COMPONENTS_SELECT + " WHERE kc.kitComponentId = ?";

  public static final String KIT_COMPONENT_SELECT_BY_IDENTIFICATION_BARCODE = KIT_COMPONENTS_SELECT + " WHERE kc.identificationBarcode = ?";

  public static final String KIT_COMPONENT_SELECT_BY_LOCATION_BARCODE = KIT_COMPONENTS_SELECT + " WHERE kc.locationBarcode = ?";

  public static final String KIT_COMPONENT_SELECT_BY_LOT_NUMBER = KIT_COMPONENTS_SELECT + " WHERE kc.lotNumber = ?";

  public static final String KIT_COMPONENT_SELECT_BY_RECEIVED_DATE = KIT_COMPONENTS_SELECT + " WHERE kc.kitReceivedDate = ?";

  public static final String KIT_COMPONENT_SELECT_BY_EXPIRY_DATE = KIT_COMPONENTS_SELECT + " WHERE kc.kitExpiryDate = ?";

  public static final String KIT_COMPONENT_SELECT_BY_EXHAUSTED = KIT_COMPONENTS_SELECT + " WHERE kc.exhausted = ?";

  public static final String KIT_COMPONENT_SELECT_BY_KIT_COMPONENT_DESCRIPTOR_ID = KIT_COMPONENTS_SELECT
      + " WHERE kc.kitComponentDescriptorId = ?";

  public static final String KIT_COMPONENT_SELECT_BY_KIT_DESCRIPTOR_ID = KIT_COMPONENTS_SELECT + " WHERE kc.kitComponentDescriptorId = ?";

  // TODO - fix. I don't see how this could have ever worked.
  // public static final String KIT_COMPONENTS_SELECT_BY_TYPE = "SELECT k.kitComponentId, k.identificationBarcode, k.locationBarcode,
  // k.lotNumber, k.kitReceivedDate, k.kitExpiryDate, k.exhausted, k.kitComponentDescriptorId, ek.experiments_experimentId "
  // +
  // "FROM " + TABLE_NAME + " k, Experiment_Kit ek " +
  // "WHERE ek.kitComponents_kitComponentId=k.kitComponentId " +
  // "AND ek.experiments_experimentId=?";

  public static final String KIT_COMPONENT_UPDATE = "UPDATE " + TABLE_NAME + " " +
      "SET identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode, lotNumber=:lotNumber, kitReceivedDate=:kitReceivedDate, kitExpiryDate=:kitExpiryDate, exhausted=:exhausted,kitComponentDescriptorId=:kitComponentDescriptorId "
      +
      "WHERE kitComponentId=:kitComponentId";

  // TODO: NOT SURE ABOUT THESE
  // I'm not sure how this ever could have worked...
  // public static final String KIT_COMPONENTS_SELECT_BY_MANUFACTURER = "SELECT k.kitComponentId, k.identificationBarcode,
  // k.locationBarcode, k.lotNumber, k.kitReceivedDate, k.kitExpiryDate, k.exhausted, k.kitComponentDescriptorId,
  // ek.experiments_experimentId "
  // +
  // "FROM " + TABLE_NAME + " k, Experiment_Kit ek " +
  // "WHERE ek.kitComponents_kitComponentId=k.kitComponentId " +
  // "AND ek.experiments_experimentId=?";
  // TODO: NOT SURE ABOUT THESE
  public static final String KIT_COMPONENTS_SELECT_BY_RELATED_EXPERIMENT = "SELECT k.kitComponentId, k.identificationBarcode, k.locationBarcode, k.lotNumber, k.kitReceivedDate, k.kitExpiryDate, k.exhausted, k.kitComponentDescriptorId, ek.experiments_experimentId "
      +
      "FROM " + TABLE_NAME + " k, Experiment_Kit ek " +
      "WHERE ek.kitComponents_kitComponentId=k.kitComponentId " +
      "AND ek.experiments_experimentId=?";

  // TODO: THERE'S NO Library_Kit TABLE IN THE DB
  public static final String KIT_COMPONENTS_SELECT_BY_RELATED_LIBRARY = "SELECT k.kitComponentId, k.identificationBarcode, k.locationBarcode, k.lotNumber, k.kitReceivedDate, k.kitExpiryDate, k.exhausted, k.kitComponentDescriptorId, ek.experiments_experimentId "
      +
      "FROM " + TABLE_NAME + " k, Library_Kit lk " +
      "WHERE lk.kits_kitComponentId=k.kitComponentId " +
      "AND lk.libraries_libraryId=?";

  protected static final Logger log = LoggerFactory.getLogger(SQLKitComponentDAO.class);
  private JdbcTemplate jdbcTemplate;
  private NoteStore noteDAO;
  private CascadeType cascadeType;
  private KitComponentDescriptorStore kitComponentDescriptorDAO;

  public KitComponentDescriptorStore getKitComponentDescriptorDAO() {
    return kitComponentDescriptorDAO;
  }

  public void setNoteDAO(NoteStore noteDAO) {
    this.noteDAO = noteDAO;
  }

  public void setKitComponentDescriptorDAO(KitComponentDescriptorStore kitComponentDescriptorDAO) {
    this.kitComponentDescriptorDAO = kitComponentDescriptorDAO;
  }

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.jdbcTemplate = template;
  }

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @Override
  public int count() throws IOException {
    return jdbcTemplate.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public KitComponent get(long id) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_ID, new Object[] { id }, new KitComponentMapper());
    return eResults.size() > 0 ? (KitComponent) eResults.get(0) : null;
  }

  @Override
  public KitComponent lazyGet(long id) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_ID, new Object[] { id }, new KitComponentMapper(true));
    return eResults.size() > 0 ? (KitComponent) eResults.get(0) : null;
  }

  @Override
  public KitComponent getKitComponentByIdentificationBarcode(String barcode) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_IDENTIFICATION_BARCODE, new Object[] { barcode }, new KitComponentMapper());
    return eResults.size() > 0 ? (KitComponent) eResults.get(0) : null;
  }

  @Override
  public Collection<KitComponent> listAll() throws IOException {
    return jdbcTemplate.query(KIT_COMPONENTS_SELECT, new KitComponentMapper());
  }

  @Override
  public List<KitComponent> listKitComponentsByLocationBarcode(String locationBarcode) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_LOCATION_BARCODE, new Object[] { locationBarcode }, new KitComponentMapper());
  }

  @Override
  public List<KitComponent> listKitComponentsByLotNumber(String lotNumber) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_LOT_NUMBER, new Object[] { lotNumber }, new KitComponentMapper());
  }

  @Override
  public List<KitComponent> listKitComponentsByReceivedDate(LocalDate receivedDate) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_RECEIVED_DATE, new Object[] { DateUtils.asDate(receivedDate) }, new KitComponentMapper());
  }

  @Override
  public List<KitComponent> listKitComponentsByExpiryDate(LocalDate expiryDate) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_EXPIRY_DATE, new Object[] { DateUtils.asDate(expiryDate) }, new KitComponentMapper());
  }

  @Override
  public List<KitComponent> listKitComponentsByExhausted(boolean exhausted) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_EXHAUSTED, new Object[] { KitUtils.toInt(exhausted) }, new KitComponentMapper());
  }

  @Override
  public List<KitComponent> listKitComponentsByKitComponentDescriptorId(long kitComponentDescriptorId) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_KIT_COMPONENT_DESCRIPTOR_ID, new Object[] { kitComponentDescriptorId },
        new KitComponentMapper());
  }

  @Override
  public List<KitComponent> listKitComponentsByKitDescriptorId(long kitDescriptorId) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_SELECT_BY_KIT_DESCRIPTOR_ID, new Object[] { kitDescriptorId }, new KitComponentMapper());
  }

  // TODO: DON'T WANT TO MESS WITH THOSE
  @Override
  public List<KitComponent> listByExperiment(long experimentId) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENTS_SELECT_BY_RELATED_EXPERIMENT, new Object[] { experimentId }, new KitComponentMapper());
  }

  @CoverageIgnore
  @Override
  public List<KitComponent> listByLibrary(long libraryId) throws IOException {
    throw new NotImplementedException("List by library not implemented");
    // return jdbcTemplate.query(KIT_COMPONENTS_SELECT_BY_RELATED_LIBRARY, new Object[] { libraryId }, new KitComponentMapper());
  }

  @CoverageIgnore
  @Override
  public List<KitComponent> listByManufacturer(String manufacturerName) throws IOException {
    throw new NotImplementedException("listByManufacturer has never been implemented");
    // return jdbcTemplate.query(KIT_COMPONENTS_SELECT_BY_MANUFACTURER, new Object[] { manufacturerName }, new KitComponentMapper());
  }

  @CoverageIgnore
  @Override
  public List<KitComponent> listByType(KitType kitType) throws IOException {
    throw new NotImplementedException("listByType never implemented");
    // return jdbcTemplate.query(KIT_COMPONENTS_SELECT_BY_TYPE, new Object[] { kitType.getKey() }, new KitComponentMapper());
  }

  @Override
  public long save(KitComponent kitComponent) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("identificationBarcode", kitComponent.getIdentificationBarcode())
        .addValue("locationBarcode", kitComponent.getLocationBarcode())
        .addValue("lotNumber", kitComponent.getLotNumber())
        .addValue("kitReceivedDate", DateUtils.asDate(kitComponent.getKitReceivedDate()))
        .addValue("kitExpiryDate", DateUtils.asDate(kitComponent.getKitExpiryDate()))
        .addValue("exhausted", KitUtils.toInt(kitComponent.isExhausted()))
        .addValue("kitComponentDescriptorId", kitComponent.getKitComponentDescriptor().getId());

    if (kitComponent.getId() == KitComponentImpl.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
          .withTableName(TABLE_NAME)
          .usingGeneratedKeyColumns("kitComponentId");
      Number newId = insert.executeAndReturnKey(params);
      kitComponent.setId(newId.longValue());
    } else {
      params.addValue("kitComponentId", kitComponent.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
      namedTemplate.update(KIT_COMPONENT_UPDATE, params);
    }

    if (this.cascadeType != null && this.cascadeType.equals(CascadeType.PERSIST)) {
      if (!kitComponent.getNotes().isEmpty()) {
        for (Note n : kitComponent.getNotes()) {
          noteDAO.saveKitNote(kitComponent, n);
        }
      }
    }
    return kitComponent.getId();
  }

  public class KitComponentMapper implements RowMapper<KitComponent> {
    private boolean lazy = false;

    public KitComponentMapper() {
      super();
    }

    public KitComponentMapper(boolean lazy) {
      super();
      this.lazy = lazy;
    }

    @Override
    public KitComponent mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("kitComponentId");

      KitComponent kitComponent = new KitComponentImpl();

      try {

        if (!lazy) {
          long kitComponentDescriptorId = rs.getLong("kitComponentDescriptorId");
          KitComponentDescriptor kcd = kitComponentDescriptorDAO.getKitComponentDescriptorById(kitComponentDescriptorId);

          kitComponent.setKitComponentDescriptor(kcd);
        }
        kitComponent.setId(id);
        kitComponent.setIdentificationBarcode(rs.getString("identificationBarcode"));
        kitComponent.setLocationBarcode(rs.getString("locationBarcode"));
        kitComponent.setLotNumber(rs.getString("lotNumber"));
        kitComponent.setKitReceivedDate(DateUtils.asLocalDate(rs.getDate("kitReceivedDate")));
        kitComponent.setKitExpiryDate(DateUtils.asLocalDate(rs.getDate("kitExpiryDate")));
        kitComponent.setExhausted(KitUtils.toBoolean(rs.getInt("exhausted")));

        kitComponent.setNotes(noteDAO.listByKit(rs.getLong("kitComponentId")));
      } catch (IOException e) {
        e.printStackTrace();
      }
      return kitComponent;
    }
  }

  @CoverageIgnore
  @Override
  public long saveChangeLog(JSONObject changeLog) throws IOException {
    throw new NotImplementedException("saveChangeLog not implemented");

    // MapSqlParameterSource params = new MapSqlParameterSource();
    // params.addValue("userId", changeLog.getLong("userId"))
    // .addValue("kitComponentId", changeLog.getLong("kitComponentId"))
    // .addValue("locationBarcodeOld", changeLog.getString("locationBarcodeOld"))
    // .addValue("locationBarcodeNew", changeLog.getString("locationBarcodeNew"))
    // .addValue("exhausted", changeLog.getBoolean("exhausted"))
    // .addValue("logDate", DateUtils.getTimeStampFromJSON((JSONObject) changeLog.get("logDate")));
    //
    // // NO UPDATE - ALWAYS INSERT
    //
    // SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
    // .withTableName("KitChangeLog")
    // .usingGeneratedKeyColumns("kitChangeLogId");
    // Number newId = insert.executeAndReturnKey(params);
    //
    // return (long) newId;

  }

  @CoverageIgnore
  @Override
  public JSONArray getKitChangeLog() throws IOException {
    throw new NotImplementedException("getKitChangeLog not implemented!");
    // List<JSONObject> changeLogs = jdbcTemplate.query(
    // "select * from KitChangeLog",
    // new RowMapper<JSONObject>() {
    // @Override
    // public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
    // JSONObject changeLog = new JSONObject();
    // changeLog.put("userId", rs.getLong("userId"));
    // changeLog.put("kitComponentId", rs.getLong("kitComponentId"));
    // changeLog.put("locationBarcodeOld", rs.getString("locationBarcodeOld"));
    // changeLog.put("locationBarcodeNew", rs.getString("locationBarcodeNew"));
    // changeLog.put("exhausted", rs.getBoolean("exhausted"));
    // changeLog.put("logDate", DateUtils.getStringFromTimeStamp(rs.getTimestamp("logDate")));
    // return changeLog;
    // }
    // });
    //
    // JSONArray result = new JSONArray();
    // for (JSONObject log : changeLogs) {
    // result.add(log);
    // }
    //
    // return result;

  }

  @CoverageIgnore
  @Override
  public JSONArray getKitChangeLogByKitComponentId(long kitComponentId) throws IOException {
    throw new NotImplementedException("getKitChangeLogByKitComponentId not implemented!");
    // List<JSONObject> changeLogs = jdbcTemplate.query(
    // "select * from KitChangeLog WHERE kitComponentId=?", new Object[] { kitComponentId },
    // new RowMapper<JSONObject>() {
    // @Override
    // public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
    // JSONObject changeLog = new JSONObject();
    // changeLog.put("userId", rs.getLong("userId"));
    // changeLog.put("locationBarcodeOld", rs.getString("locationBarcodeOld"));
    // changeLog.put("locationBarcodeNew", rs.getString("locationBarcodeNew"));
    // changeLog.put("exhausted", rs.getBoolean("exhausted"));
    // changeLog.put("logDate", DateUtils.getStringFromTimeStamp(rs.getTimestamp("logDate")));
    // return changeLog;
    // }
    // });
    //
    // JSONArray result = new JSONArray();
    // for (JSONObject log : changeLogs) {
    // result.add(log);
    // }
    //
    // return result;
  }

  @Override
  public boolean isKitComponentAlreadyLogged(String identificationBarcode) throws IOException {
    KitComponent kitComponent = getKitComponentByIdentificationBarcode(identificationBarcode);
    return (kitComponent == null) ? false : true;
  }
}