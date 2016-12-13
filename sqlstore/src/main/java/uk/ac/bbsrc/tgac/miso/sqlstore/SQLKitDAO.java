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
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractKit;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.ClusterKit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.EmPcrKit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.LibraryKit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.MultiplexingKit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.SequencingKit;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
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
@Transactional(rollbackFor = Exception.class)
public class SQLKitDAO implements KitStore {
  private static final String KIT_TABLE_NAME = "Kit";
  private static final String DESCRIPTOR_TABLE_NAME = "KitDescriptor";

  public static final String KITS_SELECT = "SELECT kitId, identificationBarcode, locationBarcode, lotNumber, kitDate, kitDescriptorId "
      + "FROM " + KIT_TABLE_NAME;

  public static final String KIT_SELECT_BY_ID = KITS_SELECT + " WHERE kitId = ?";

  public static final String KIT_SELECT_BY_BARCODE = KITS_SELECT + " WHERE identificationBarcode = ?";

  public static final String KIT_SELECT_BY_LOT_NUMBER = KITS_SELECT + " WHERE lotNumber = ?";

  public static final String KITS_SELECT_BY_TYPE = "SELECT k.kitId, k.identificationBarcode, k.locationBarcode, k.lotNumber, k.kitDate, k.kitDescriptorId "
      + "FROM " + KIT_TABLE_NAME + " k, KitDescriptor kd " + "WHERE k.kitDescriptorId = kd.kitDescriptorId " + "AND kd.kitType = ?";

  public static final String KITS_SELECT_BY_MANUFACTURER = "SELECT k.kitId, k.identificationBarcode, k.locationBarcode, k.lotNumber, k.kitDate, k.kitDescriptorId "
      + "FROM " + KIT_TABLE_NAME + " k, KitDescriptor kd " + "WHERE k.kitDescriptorId = kd.kitDescriptorId " + "AND kd.manufacturer = ?";

  public static final String KITS_SELECT_BY_RELATED_EXPERIMENT = "SELECT k.kitId, k.identificationBarcode, k.locationBarcode, k.lotNumber, k.kitDate, k.kitDescriptorId, ek.experiments_experimentId "
      + "FROM " + KIT_TABLE_NAME + " k, Experiment_Kit ek " + "WHERE ek.kits_kitId=k.kitId " + "AND ek.experiments_experimentId=?";

  public static final String KITS_SELECT_BY_RELATED_LIBRARY = "SELECT k.kitId, k.identificationBarcode, k.locationBarcode, k.lotNumber, k.kitDate, k.kitDescriptorId "
      + "FROM " + KIT_TABLE_NAME + " k, Library_Kit lk " + "WHERE lk.kits_kitId=k.kitId " + "AND lk.libraries_libraryId=?";

  public static final String KIT_UPDATE = "UPDATE " + KIT_TABLE_NAME + " "
      + "SET identificationBarcode=:identificationBarcode, locationBarcode=:locationBarcode, lotNumber=:lotNumber, kitDate=:kitDate, kitDescriptorId=:kitDescriptorId "
      + "WHERE kitId=:kitId";

  public static final String KIT_DESCRIPTORS_SELECT = "SELECT kitDescriptorId, name, version, manufacturer, partNumber, stockLevel, kitType, platformType, description, lastModifier "
      + "FROM " + DESCRIPTOR_TABLE_NAME;

  public static final String KIT_DESCRIPTOR_SELECT_BY_ID = KIT_DESCRIPTORS_SELECT + " WHERE kitDescriptorId=?";

  public static final String KIT_DESCRIPTOR_SELECT_BY_PART_NUMBER = KIT_DESCRIPTORS_SELECT + " WHERE partNumber=?";

  public static final String KIT_DESCRIPTORS_SELECT_BY_TYPE = KIT_DESCRIPTORS_SELECT + " WHERE kitType = ?";

  public static final String KIT_DESCRIPTORS_SELECT_BY_PLATFORM = KIT_DESCRIPTORS_SELECT + " WHERE platformType = ?";

  public static final String KIT_DESCRIPTOR_UPDATE = "UPDATE " + DESCRIPTOR_TABLE_NAME
      + " SET name=:name, version=:version, manufacturer=:manufacturer, partNumber=:partNumber, stockLevel=:stockLevel, kitType=:kitType, platformType=:platformType, description=:description, lastModifier=:lastModifier"
      + " WHERE kitDescriptorId=:kitDescriptorId";

  protected static final Logger log = LoggerFactory.getLogger(SQLKitDAO.class);
  private JdbcTemplate template;
  private ChangeLogStore changeLogDAO;
  private NoteStore noteDAO;
  private SecurityStore securityDAO;
  private CascadeType cascadeType;

  @Autowired
  private CacheManager cacheManager;

  @CoverageIgnore
  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public void setNoteDAO(NoteStore noteDAO) {
    this.noteDAO = noteDAO;
  }

  @CoverageIgnore
  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @CoverageIgnore
  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @CoverageIgnore
  public ChangeLogStore getChangeLogDAO() {
    return changeLogDAO;
  }

  @CoverageIgnore
  public void setChangeLogDAO(ChangeLogStore changeLogDAO) {
    this.changeLogDAO = changeLogDAO;
  }

  @CoverageIgnore
  public SecurityStore getSecurityDAO() {
    return securityDAO;
  }

  @CoverageIgnore
  public void setSecurityDAO(SecurityStore securityDAO) {
    this.securityDAO = securityDAO;
  }

  @Override
  public Kit get(long id) throws IOException {
    List<Kit> eResults = template.query(KIT_SELECT_BY_ID, new Object[] { id }, new KitMapper());
    return eResults.size() > 0 ? (Kit) eResults.get(0) : null;
  }

  @CoverageIgnore
  @Override
  public Kit lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Kit getKitByIdentificationBarcode(String barcode) throws IOException {
    List<Kit> eResults = template.query(KIT_SELECT_BY_BARCODE, new Object[] { barcode }, new KitMapper());
    return eResults.size() > 0 ? (Kit) eResults.get(0) : null;
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    List<Kit> eResults = template.query(KIT_SELECT_BY_LOT_NUMBER, new Object[] { lotNumber }, new KitMapper());
    return eResults.size() > 0 ? (Kit) eResults.get(0) : null;
  }

  @Override
  public Collection<Kit> listAll() throws IOException {
    return template.query(KITS_SELECT, new KitMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + KIT_TABLE_NAME);
  }

  @Override
  public List<Kit> listByExperiment(long experimentId) throws IOException {
    return template.query(KITS_SELECT_BY_RELATED_EXPERIMENT, new Object[] { experimentId }, new KitMapper());
  }

  @Override
  public List<Kit> listByManufacturer(String manufacturerName) throws IOException {
    return template.query(KITS_SELECT_BY_MANUFACTURER, new Object[] { manufacturerName }, new KitMapper());
  }

  @Override
  public List<Kit> listKitsByType(KitType kitType) throws IOException {
    return template.query(KITS_SELECT_BY_TYPE, new Object[] { kitType.getKey() }, new KitMapper());
  }

  @Override
  public long save(Kit kit) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("identificationBarcode", kit.getIdentificationBarcode());
    params.addValue("locationBarcode", kit.getLocationBarcode());
    params.addValue("lotNumber", kit.getLotNumber());
    params.addValue("kitDate", kit.getKitDate());
    params.addValue("kitDescriptorId", kit.getKitDescriptor().getId());

    if (kit.getId() == AbstractKit.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(KIT_TABLE_NAME).usingGeneratedKeyColumns("kitId");
      Number newId = insert.executeAndReturnKey(params);
      kit.setId(newId.longValue());
    } else {
      params.addValue("kitId", kit.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(KIT_UPDATE, params);
    }

    if (this.cascadeType != null && this.cascadeType.equals(CascadeType.PERSIST)) {
      if (!kit.getNotes().isEmpty()) {
        for (Note n : kit.getNotes()) {
          noteDAO.saveKitNote(kit, n);
        }
      }
    }
    return kit.getId();
  }

  public class KitMapper extends CacheAwareRowMapper<Kit> {
    public KitMapper() {
      super(Kit.class);
    }

    public KitMapper(boolean lazy) {
      super(Kit.class, lazy);
    }

    @Override
    public Kit mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("kitId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for Kit " + id);
          return (Kit) element.getObjectValue();
        }
      }
      Kit kit = null;

      try {
        KitDescriptor kd = getKitDescriptorById(rs.getInt("kitDescriptorId"));
        KitType kitType = kd.getKitType();

        if (kitType.equals(KitType.CLUSTERING)) {
          kit = new ClusterKit();
        } else if (kitType.equals(KitType.EMPCR)) {
          kit = new EmPcrKit();
        } else if (kitType.equals(KitType.LIBRARY)) {
          kit = new LibraryKit();
        } else if (kitType.equals(KitType.MULTIPLEXING)) {
          kit = new MultiplexingKit();
        } else if (kitType.equals(KitType.SEQUENCING)) {
          kit = new SequencingKit();
        } else {
          throw new SQLException("Unsupported KitType: " + kitType.getKey());
        }

        kit.setId(id);
        kit.setIdentificationBarcode(rs.getString("identificationBarcode"));
        kit.setLocationBarcode(rs.getString("locationBarcode"));
        kit.setLotNumber(rs.getString("lotNumber"));
        kit.setKitDate(rs.getDate("kitDate"));
        kit.setKitDescriptor(kd);
        kit.setNotes(noteDAO.listByKit(rs.getLong("kitId")));
      } catch (IOException e) {
        log.error("kit row mapper", e);
      }
      return kit;
    }
  }

  @Override
  public KitDescriptor getKitDescriptorById(long id) throws IOException {
    List<KitDescriptor> eResults = template.query(KIT_DESCRIPTOR_SELECT_BY_ID, new Object[] { id }, new KitDescriptorMapper());
    return eResults.size() > 0 ? (KitDescriptor) eResults.get(0) : null;
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException {
    List<KitDescriptor> eResults = template.query(KIT_DESCRIPTOR_SELECT_BY_PART_NUMBER, new Object[] { partNumber },
        new KitDescriptorMapper());
    return eResults.size() > 0 ? (KitDescriptor) eResults.get(0) : null;
  }

  @Override
  public List<KitDescriptor> listAllKitDescriptors() throws IOException {
    return template.query(KIT_DESCRIPTORS_SELECT, new KitDescriptorMapper());
  }

  @Override
  public List<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException {
    return template.query(KIT_DESCRIPTORS_SELECT_BY_TYPE, new Object[] { kitType.getKey() }, new KitDescriptorMapper());
  }

  public List<KitDescriptor> listKitDescriptorsByPlatform(PlatformType platformType) throws IOException {
    return template.query(KIT_DESCRIPTORS_SELECT_BY_PLATFORM, new Object[] { platformType.getKey() }, new KitDescriptorMapper());
  }

  @Override
  public long saveKitDescriptor(KitDescriptor kd) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();

    params.addValue("name", kd.getName());
    params.addValue("version", kd.getVersion());
    params.addValue("manufacturer", kd.getManufacturer());
    params.addValue("partNumber", kd.getPartNumber());
    params.addValue("stockLevel", kd.getStockLevel());
    params.addValue("kitType", kd.getKitType().getKey());
    params.addValue("platformType", kd.getPlatformType().getKey());
    params.addValue("description", kd.getDescription());
    params.addValue("lastModifier", kd.getLastModifier().getUserId());

    if (kd.getId() == KitDescriptor.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName("KitDescriptor").usingGeneratedKeyColumns("kitDescriptorId");
      Number newId = insert.executeAndReturnKey(params);
      kd.setId(newId.longValue());
    } else {
      params.addValue("kitDescriptorId", kd.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(KIT_DESCRIPTOR_UPDATE, params);
    }

    return kd.getId();
  }

  public class KitDescriptorMapper implements RowMapper<KitDescriptor> {
    @Override
    public KitDescriptor mapRow(ResultSet rs, int rowNum) throws SQLException {
      KitDescriptor kd = new KitDescriptor();
      kd.setId(rs.getLong("kitDescriptorId"));
      kd.setName(rs.getString("name"));
      kd.setVersion(rs.getDouble("version"));
      kd.setManufacturer(rs.getString("manufacturer"));
      kd.setPartNumber(rs.getString("partNumber"));
      kd.setStockLevel(rs.getInt("stockLevel"));
      kd.setDescription(rs.getString("description"));
      kd.setKitType(KitType.get(rs.getString("kitType")));
      kd.setPlatformType(PlatformType.get(rs.getString("platformType")));
      try {
        kd.setLastModifier(securityDAO.getUserById(rs.getLong("lastModifier")));
      } catch (IOException e) {
        log.error("kit descriptor row mapper", e);
      }
      kd.getChangeLog().addAll(changeLogDAO.listAllById(DESCRIPTOR_TABLE_NAME, kd.getId()));

      return kd;
    }
  }

  @Override
  public Map<String, Integer> getKitDescriptorColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, DESCRIPTOR_TABLE_NAME);
  }
}
