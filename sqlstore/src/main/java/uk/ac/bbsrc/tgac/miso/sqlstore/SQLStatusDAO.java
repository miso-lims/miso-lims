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

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.StatusStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.Status;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLStatusDAO implements StatusStore {
  public static final String STATUSES_SELECT =
          "SELECT statusId, health, startDate, completionDate, runName, instrumentName, xml, lastUpdated " +
          "FROM Status";

  public static final String STATUS_UPDATE =
          "UPDATE Status " +
          "SET health=:health, startDate=:startDate, completionDate=:completionDate, runName=:runName, instrumentName=:instrumentName, xml=:xml " +
          "WHERE statusId=:statusId";

  public static final String STATUS_SELECT_BY_ID =
          STATUSES_SELECT + " " + "WHERE statusId = ?";

  public static final String STATUS_SELECT_BY_SEQUENCER_NAME =
          STATUSES_SELECT + " " + "WHERE runName LIKE CONCAT('%', ? ,'%')";

  public static final String STATUS_SELECT_BY_INSTRUMENT_NAME =
          STATUSES_SELECT + " " + "WHERE instrumentName = ?";  
  
  public static final String STATUS_SELECT_BY_RUN_NAME =
          STATUSES_SELECT + " " + "WHERE runName = ?";  

  public static final String STATUS_SELECT_BY_HEALTH =
          STATUSES_SELECT + " " + "WHERE health = ?";

  protected static final Logger log = LoggerFactory.getLogger(SQLStatusDAO.class);
  private JdbcTemplate template;

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

  public long save(Status status) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("runName", status.getRunName())
            .addValue("health", status.getHealth().getKey())
            .addValue("startDate", status.getStartDate())
            .addValue("completionDate", status.getCompletionDate())
            .addValue("instrumentName", status.getInstrumentName());

    Blob xmlblob = null;
    try {
      if (status.getXml() != null) {
        byte[] rbytes = status.getXml().getBytes();
        xmlblob = new SerialBlob(rbytes);
        params.addValue("xml", xmlblob);
      }
      else {
        params.addValue("xml", null);
      }
    }
    catch (SerialException e) {
      e.printStackTrace();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    if (status.getStatusId() == null) {
      Status savedStatus = getByRunName(status.getRunName());
      if (savedStatus == null) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(template)
                .withTableName("Status")
                .usingGeneratedKeyColumns("statusId");

        if (status.getHealth().equals(HealthType.Running) && status.getStartDate() == null) {
          //run freshly started
          params.addValue("startDate", new Date());
        }

        Number newId = insert.executeAndReturnKey(params);
        status.setStatusId(newId.longValue());
      }
      else {
        status.setStatusId(savedStatus.getStatusId());
        params.addValue("statusId", status.getStatusId());
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
        namedTemplate.update(STATUS_UPDATE, params);        
      }
    }
    else {
      params.addValue("statusId", status.getStatusId());
      params.addValue("startDate", new SimpleDateFormat("yyyy-MM-dd").format(status.getStartDate()));
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(STATUS_UPDATE, params);
    }

    return status.getStatusId();
  }

  public List<Status> listAll() {
    List results = template.query(STATUSES_SELECT, new StatusMapper());
    return results;
  }

  public List<Status> listAllBySequencerName(String sequencerName) {
    List results = template.query(STATUS_SELECT_BY_SEQUENCER_NAME, new Object[]{sequencerName}, new StatusMapper());
    return results;
  }

  public List<Status> listAllByInstrumentName(String instrumentName) {
    List results = template.query(STATUS_SELECT_BY_INSTRUMENT_NAME, new Object[]{instrumentName}, new StatusMapper());
    return results;
  }

  public List<Status> listByHealth(String health) {
    List results = template.query(STATUS_SELECT_BY_HEALTH, new Object[]{health}, new StatusMapper());
    return results;
  }

  public Status get(long statusId) throws IOException {
    List eResults = template.query(STATUS_SELECT_BY_ID, new Object[]{statusId}, new StatusMapper());
    Status e = eResults.size() > 0 ? (Status) eResults.get(0) : null;
    return e;
  }

  public Status getByRunName(String runName) throws IOException {
    List eResults = template.query(STATUS_SELECT_BY_RUN_NAME, new Object[]{runName}, new StatusMapper());
    Status e = eResults.size() > 0 ? (Status) eResults.get(0) : null;
    return e;
  }

  public class StatusMapper implements RowMapper<Status> {
    public Status mapRow(ResultSet rs, int rowNum) throws SQLException {
      Status s = dataObjectFactory.getStatus();
      s.setStatusId(rs.getLong("statusId"));
      s.setHealth(HealthType.valueOf(rs.getString("health")));
      s.setStartDate(rs.getDate("startDate"));
      s.setCompletionDate(rs.getDate("completionDate"));
      s.setRunName(rs.getString("runName"));
      s.setInstrumentName(rs.getString("instrumentName"));
      s.setLastUpdated(rs.getTimestamp("lastUpdated"));
      
      Blob xmlblob = rs.getBlob("xml");
      if (xmlblob != null) {
        if (xmlblob.length() > 0) {
          byte[] rbytes = xmlblob.getBytes(1, (int)xmlblob.length());
          s.setXml(new String(rbytes));
        }
      }
      return s;
    }
  }
}
