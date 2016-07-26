package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

@Transactional(rollbackFor = Exception.class)
public class SQLChangeLogDAO implements ChangeLogStore {
  private static class ChangeLogMapper implements RowMapper<ChangeLog> {

    @Override
    public ChangeLog mapRow(ResultSet rs, int rownum) throws SQLException {
      ChangeLog cl = new ChangeLog();
      cl.setColumnsChanged(rs.getString("columnsChanged"));
      cl.setSummary(rs.getString("message"));
      cl.setUserId(rs.getLong("userId"));
      cl.setTime(rs.getTimestamp("changeTime"));
      return cl;
    }

  }

  private static enum ChangeLogType {
    BOX("BoxChangeLog", "boxId"),
    EXPERIMENT("ExperimentChangeLog", "experimentId"),
    KITDESCRIPTOR("KitDescriptorChangeLog", "kitDescriptorId"),
    LIBRARY("LibraryChangeLog", "libraryId"),
    PLATE("PlateChangeLog", "plateId"),
    POOL("PoolChangeLog", "poolId"),
    RUN("RunChangeLog", "runId"),
    SAMPLE("SampleChangeLog", "sampleId"),
    SEQUENCERPARTITIONCONTAINER("SequencerPartitionContainerChangeLog", "containerId"),
    STUDY("StudyChangeLog", "studyId");
    
    private final String tableName;
    private final String idColumn;
    
    private ChangeLogType(String tableName, String idColumn) {
      this.tableName = tableName;
      this.idColumn = idColumn;
    }
    
    public String getTableName() {
      return tableName;
    }
    
    public String getIdColumn() {
      return idColumn;
    }
    
    public static ChangeLogType get(String type) {
      return ChangeLogType.valueOf(type.toUpperCase());
    }
  }

  public static final String CHANGELOG_SELECT = "SELECT c.columnsChanged, c.message, c.userId, c.changeTime FROM %s c";
  public static final String CHANGELOG_SELECT_WHERE = CHANGELOG_SELECT + " WHERE c.%s = ? ORDER BY c.changeTime DESC";
  public static final String CHANGELOG_DELETE_BY_ENTITY_ID = "DELETE FROM %s WHERE %s = ?";

  private JdbcTemplate template;

  @CoverageIgnore
  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  @CoverageIgnore
  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public List<ChangeLog> listAll(String type) {
    ChangeLogType cl = ChangeLogType.get(type);
    return template.query(String.format(CHANGELOG_SELECT, cl.getTableName()), new ChangeLogMapper());
  }

  @Override
  public List<ChangeLog> listAllById(String type, long id) {
    ChangeLogType cl = ChangeLogType.get(type);
    return template.query(String.format(CHANGELOG_SELECT_WHERE, cl.getTableName(), cl.getIdColumn()),
        new Object[] { id }, new ChangeLogMapper());
  }
  
  @Override
  public void deleteAllById(String type, long id) {
    ChangeLogType cl = ChangeLogType.get(type);
    template.update(String.format(CHANGELOG_DELETE_BY_ENTITY_ID, cl.getTableName(), cl.getIdColumn()), new Object[] {id});
  }

  @Override
  public void create(String type, long objectId, ChangeLog changeLog) {
    ChangeLogType cl = ChangeLogType.get(type);
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(cl.getIdColumn(), objectId);
    params.addValue("columnsChanged", changeLog.getColumnsChanged());
    params.addValue("userId", changeLog.getUserId());
    params.addValue("message", changeLog.getSummary());
    params.addValue("changeTime", changeLog.getTime());
    SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(cl.getTableName());
    insert.execute(params);
  }
}
