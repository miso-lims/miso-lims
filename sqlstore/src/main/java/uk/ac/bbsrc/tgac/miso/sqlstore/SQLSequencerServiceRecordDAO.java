package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerServiceRecordStore;

public class SQLSequencerServiceRecordDAO implements SequencerServiceRecordStore {
  
  private static final String TABLE_NAME = "SequencerServiceRecord";
  
  private static final String SERVICE_RECORD_SELECT = "SELECT recordId, sequencerReferenceId, title, details, servicedBy, phone,"
      + " serviceDate, shutdownTime, restoredTime"
      + " FROM " + TABLE_NAME;
  
  private static final String SERVICE_RECORD_SELECT_BY_ID = SERVICE_RECORD_SELECT + " WHERE recordId = ?";
  
  private static final String SERVICE_RECORD_SELECT_BY_SEQUENCER_ID = SERVICE_RECORD_SELECT + " WHERE sequencerReferenceId = ?";
  
  private static final String SERVICE_RECORD_UPDATE = "UPDATE " + TABLE_NAME
      + " SET sequencerReferenceId=:sequencerReferenceId, title=:title, details=:details, servicedBy=:servicedBy, phone=:phone,"
      + " serviceDate=:serviceDate, shutdownTime=:shutdownTime, restoredTime=:restoredTime"
      + " WHERE recordId=:recordId";
  
  private static final String SERVICE_RECORD_DELETE = "DELETE FROM " + TABLE_NAME
      + " WHERE recordId=:recordId";
  
  protected static final Logger log = LoggerFactory.getLogger(SQLSequencerServiceRecordDAO.class);
  
  private JdbcTemplate template;
  private SequencerReferenceStore sequencerReferenceDAO;
  
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
  
  public void setSequencerReferenceDAO(SequencerReferenceStore sequencerReferenceDAO) {
    this.sequencerReferenceDAO = sequencerReferenceDAO;
  }

  @Override
  public long save(SequencerServiceRecord serviceRecord) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    
    params.addValue("sequencerReferenceId", serviceRecord.getSequencerReference().getId());
    params.addValue("title", serviceRecord.getTitle());
    params.addValue("details", serviceRecord.getDetails());
    params.addValue("servicedBy", serviceRecord.getServicedByName());
    params.addValue("phone", serviceRecord.getPhone());
    params.addValue("serviceDate", serviceRecord.getServiceDate());
    params.addValue("shutdownTime", serviceRecord.getShutdownTime());
    params.addValue("restoredTime", serviceRecord.getRestoredTime());
    
    if (serviceRecord.getId() == AbstractSequencerServiceRecord.UNSAVED_ID) {
      if (serviceRecord.getSequencerReference().getDateDecommissioned() != null) {
        throw new IOException("Cannot add service records to a retired sequencer");
      }
      
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("recordId");

      Number newId = insert.executeAndReturnKey(params);
      serviceRecord.setId(newId.longValue());
    }
    else {
      params.addValue("recordId", serviceRecord.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(SERVICE_RECORD_UPDATE, params);
    }
    return serviceRecord.getId();
  }

  @Override
  public SequencerServiceRecord get(long id) throws IOException {
    List<SequencerServiceRecord> records = template.query(SERVICE_RECORD_SELECT_BY_ID, new Object[] {id}, new SequencerServiceRecordMapper());
    return (records.size() > 0) ? records.get(0) : null;
  }

  @Override
  public SequencerServiceRecord lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<SequencerServiceRecord> listAll() throws IOException {
    return template.query(SERVICE_RECORD_SELECT, new SequencerServiceRecordMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public Collection<SequencerServiceRecord> listBySequencerId(long referenceId) {
    return template.query(SERVICE_RECORD_SELECT_BY_SEQUENCER_ID, new Object[] {referenceId}, new SequencerServiceRecordMapper());
  }
  
  public class SequencerServiceRecordMapper implements RowMapper<SequencerServiceRecord> {

    @Override
    public SequencerServiceRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
      SequencerServiceRecord r = dataObjectFactory.getSequencerServiceRecord();
      
      try {
        if (r != null) {
          r.setId(rs.getLong("recordId"));
          r.setTitle(rs.getString("title"));
          r.setDetails(rs.getString("details"));
          r.setServicedByName(rs.getString("servicedBy"));
          r.setPhone(rs.getString("phone"));
          r.setServiceDate(rs.getDate("serviceDate"));
          r.setShutdownTime(rs.getDate("shutdownTime"));
          r.setRestoredTime(rs.getDate("restoredTime"));
          r.setSequencerReference(sequencerReferenceDAO.get(rs.getLong("sequencerReferenceId")));
        }
      } catch (IOException e) {
        log.error("sequence reference row mapper", e);
      }
      
      return r;
    }
    
  }
  
}
