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
import java.net.InetAddress;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLSequencerReferenceDAO implements SequencerReferenceStore {
  private static final String TABLE_NAME = "SequencerReference";

  private static final String SEQUENCER_REFERENCE_SELECT = "SELECT sr.referenceId, sr.name, sr.ipAddress, sr.platformId, sr.available, sr.serialNumber,"
      + " sr.dateCommissioned, sr.dateDecommissioned, sr.upgradedSequencerReferenceId, sv.lastServiced" + " FROM " + TABLE_NAME + " sr"
      + " LEFT JOIN (SELECT sequencerReferenceId, MAX(serviceDate) AS lastServiced FROM SequencerServiceRecord GROUP BY sequencerReferenceId) sv"
      + " ON sv.sequencerReferenceId = sr.referenceId";

  private static final String SEQUENCER_REFERENCE_SELECT_BY_ID = SEQUENCER_REFERENCE_SELECT + " WHERE sr.referenceId = ?";

  private static final String SEQUENCER_REFERENCE_SELECT_BY_NAME = SEQUENCER_REFERENCE_SELECT + " WHERE sr.name = ?";

  private static final String SEQUENCER_REFERENCE_SELECT_BY_PLATFORM = SEQUENCER_REFERENCE_SELECT
      + " LEFT JOIN Platform p ON sr.platformId=p.platformId" + " WHERE p.name=?";

  private static final String SEQUENCER_REFERENCE_SELECT_BY_RELATED_RUN = "";

  private static final String SEQUENCER_REFERENCE_SELECT_BY_UPGRADED_REFERENCE = SEQUENCER_REFERENCE_SELECT
      + " WHERE sr.upgradedSequencerReferenceId=?";

  private static final String SEQUENCER_REFERENCE_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET name=:name, ipAddress=:ipAddress, platformId=:platformId, available=:available, serialNumber=:serialNumber, "
      + "dateCommissioned=:dateCommissioned, dateDecommissioned=:dateDecommissioned,"
      + "upgradedSequencerReferenceId=:upgradedSequencerReferenceId " + "WHERE referenceId=:referenceId";

  private static final String SEQUENCER_REFERENCE_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE referenceId=:referenceId";

  protected static final Logger log = LoggerFactory.getLogger(SQLSequencerReferenceDAO.class);
  private JdbcTemplate template;
  private PlatformStore platformDAO;

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

  public void setPlatformDAO(PlatformStore platformDAO) {
    this.platformDAO = platformDAO;
  }

  @Override
  public long save(SequencerReference sequencerReference) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();

    Blob ipBlob = null;
    try {
      ipBlob = new SerialBlob(sequencerReference.getIpAddress().getAddress());
    } catch (SQLException e) {
      log.error("sequencer reference save", e);
    }

    params.addValue("name", sequencerReference.getName());
    params.addValue("ipAddress", ipBlob);
    params.addValue("platformId", sequencerReference.getPlatform().getPlatformId());
    params.addValue("available", sequencerReference.getAvailable());
    params.addValue("serialNumber", sequencerReference.getSerialNumber());
    params.addValue("dateCommissioned", sequencerReference.getDateCommissioned());
    params.addValue("dateDecommissioned", sequencerReference.getDateDecommissioned());
    if (sequencerReference.getUpgradedSequencerReference() != null) {
      params.addValue("upgradedSequencerReferenceId", sequencerReference.getUpgradedSequencerReference().getId());
    } else {
      params.addValue("upgradedSequencerReferenceId", null);
    }

    if (sequencerReference.getId() == AbstractSequencerReference.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("referenceId");

      Number newId = insert.executeAndReturnKey(params);
      sequencerReference.setId(newId.longValue());
    } else {
      params.addValue("referenceId", sequencerReference.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(SEQUENCER_REFERENCE_UPDATE, params);
    }

    return sequencerReference.getId();
  }

  @Override
  public SequencerReference get(long id) throws IOException {
    return get(id, false);
  }

  @Override
  public SequencerReference lazyGet(long id) throws IOException {
    return get(id, true);
  }

  public SequencerReference get(long id, boolean lazy) throws IOException {
    List<SequencerReference> eResults = template.query(SEQUENCER_REFERENCE_SELECT_BY_ID, new Object[] { id },
        new SequencerReferenceMapper(!lazy));
    SequencerReference e = eResults.size() > 0 ? (SequencerReference) eResults.get(0) : null;
    return e;
  }

  @Override
  public SequencerReference getByRunId(long runId) throws IOException {
    List<SequencerReference> eResults = template.query(SEQUENCER_REFERENCE_SELECT_BY_RELATED_RUN, new Object[] { runId },
        new SequencerReferenceMapper());
    SequencerReference e = eResults.size() > 0 ? (SequencerReference) eResults.get(0) : null;
    return e;
  }

  @Override
  public SequencerReference getByName(String referenceName) throws IOException {
    List<SequencerReference> eResults = template.query(SEQUENCER_REFERENCE_SELECT_BY_NAME, new Object[] { referenceName },
        new SequencerReferenceMapper());
    SequencerReference e = eResults.size() > 0 ? (SequencerReference) eResults.get(0) : null;
    return e;
  }

  @Override
  public Collection<SequencerReference> listAll() throws IOException {
    return template.query(SEQUENCER_REFERENCE_SELECT, new SequencerReferenceMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public Collection<SequencerReference> listByPlatformType(PlatformType platformType) throws IOException {
    return template.query(SEQUENCER_REFERENCE_SELECT_BY_PLATFORM, new Object[] { platformType.getKey() }, new SequencerReferenceMapper());
  }

  private SequencerReference getByUpgradedReference(long id) {
    List<SequencerReference> upgradedFrom = template.query(SEQUENCER_REFERENCE_SELECT_BY_UPGRADED_REFERENCE, new Object[] { id },
        new SequencerReferenceMapper(false));
    if (upgradedFrom.size() == 1) {
      return upgradedFrom.get(0);
    } else {
      return null;
    }
  }

  @Override
  public boolean remove(SequencerReference r) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return (r.isDeletable()
        && (namedTemplate.update(SEQUENCER_REFERENCE_DELETE, new MapSqlParameterSource().addValue("referenceId", r.getId())) == 1));
  }

  public class SequencerReferenceMapper implements RowMapper<SequencerReference> {

    private final boolean loadReferences;

    public SequencerReferenceMapper() {
      this.loadReferences = true;
    }

    public SequencerReferenceMapper(boolean loadReferences) {
      this.loadReferences = loadReferences;
    }

    @Override
    public SequencerReference mapRow(ResultSet rs, int rowNum) throws SQLException {
      SequencerReference c = dataObjectFactory.getSequencerReference();

      try {
        if (c != null) {
          c.setId(rs.getLong("referenceId"));
          c.setName(rs.getString("name"));
          c.setPlatform(platformDAO.get(rs.getLong("platformId")));
          c.setAvailable(rs.getBoolean("available"));
          c.setSerialNumber(rs.getString("serialNumber"));
          c.setDateCommissioned(rs.getTimestamp("dateCommissioned"));
          c.setDateDecommissioned(rs.getTimestamp("dateDecommissioned"));
          if (loadReferences) {
            int upgradedId = rs.getInt("upgradedSequencerReferenceId");
            if (upgradedId != AbstractSequencerReference.UNSAVED_ID) {
              c.setUpgradedSequencerReference(get(upgradedId));
            }
            c.setPreUpgradeSequencerReference(getByUpgradedReference(c.getId()));
          }

          Blob ipBlob = rs.getBlob("ipAddress");
          if (ipBlob != null) {
            if (ipBlob.length() > 0) {
              byte[] rbytes = ipBlob.getBytes(1, (int) ipBlob.length());
              c.setIpAddress(InetAddress.getByAddress(rbytes));
            }
          }
          c.setLastServicedDate(rs.getTimestamp("lastServiced"));
        }
      } catch (IOException e1) {
        log.error("sequence reference row mapper", e1);
      }
      return c;
    }
  }

  @Override
  public Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }
}
