package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.SampleAnalyteStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

public class SQLSampleAnalyteDAO implements SampleAnalyteStore {

  protected static final Logger log = LoggerFactory.getLogger(SQLSampleAnalyteDAO.class);
  private static final String TABLE_NAME = "SampleAnalyte";

  public static final String SAMPLE_ANALYTE_SELECT = "SELECT sampleAnalyteId, aliquotNumber, creationDate, lastUpdated, purpose, region, stockNumber, tubeId "
      + "FROM " + TABLE_NAME;

  public static final String SAMPLE_ANALYTE_SELECT_BY_ID = SAMPLE_ANALYTE_SELECT + " WHERE sampleAnalyteId = ?";

  public static final String SAMPLE_ANALYTE_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET aliquotNumber=:aliquotNumber, purpose=:purpose, region=:region, stockNumber=:stockNumber, tubeId=:tubeId, lastUpdated=:lastUpdated "
      + "WHERE sampleAnalyteId=:sampleAnalyteId";

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  private JdbcTemplate template;

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public long save(SampleAnalyte sampleAnalyte) throws IOException {
    Date now = new Date();
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("aliquotNumber", sampleAnalyte.getAliquotNumber()).addValue("purpose", sampleAnalyte.getPurpose())
        .addValue("region", sampleAnalyte.getRegion()).addValue("stockNumber", sampleAnalyte.getStockNumber())
        .addValue("tubeId", sampleAnalyte.getTubeId());

    if (sampleAnalyte.getSampleAnalyteId() == SampleAnalyte.UNSAVED_ID) {
      params.addValue("creationDate", now).addValue("lastUpdated", now);
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("sampleAnalyteId");
      Number newId = insert.executeAndReturnKey(params);
      sampleAnalyte.setSampleAnalyteId(newId.longValue());
    } else {
      params.addValue("sampleAnalyteId", sampleAnalyte.getSampleAnalyteId()).addValue("lastUpdated", now);
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(SAMPLE_ANALYTE_UPDATE, params);
    }
    return sampleAnalyte.getSampleAnalyteId();
  }

  @Override
  public SampleAnalyte get(long id) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = template.query(SAMPLE_ANALYTE_SELECT_BY_ID, new Object[] { id }, new SampleAnalyteMapper());
    return eResults.size() > 0 ? (SampleAnalyte) eResults.get(0) : null;
  }

  @Override
  public SampleAnalyte lazyGet(long id) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = template.query(SAMPLE_ANALYTE_SELECT_BY_ID, new Object[] { id }, new SampleAnalyteMapper(true));
    return eResults.size() > 0 ? (SampleAnalyte) eResults.get(0) : null;
  }

  @Override
  public Collection<SampleAnalyte> listAll() throws IOException {
    return template.query(SAMPLE_ANALYTE_SELECT, new SampleAnalyteMapper(true));
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  public class SampleAnalyteMapper extends CacheAwareRowMapper<SampleAnalyte> {
    public SampleAnalyteMapper() {
      // Not cached at present
      super(SampleAnalyte.class, false, false);
    }

    public SampleAnalyteMapper(boolean lazy) {
      // Not cached at present
      super(SampleAnalyte.class, lazy, false);
    }

    @Override
    public SampleAnalyte mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("sampleAnalyteId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug("Cache hit on map for SampleAnalyte " + id);
          return (SampleAnalyte) element.getObjectValue();
        }
      }

      SampleAnalyte s = dataObjectFactory.getSampleAnalyte();
      s.setSampleAnalyteId(id);
      s.setAliquotNumber(rs.getInt("aliquotNumber"));
      s.setCreationDate(rs.getDate("creationDate"));
      s.setLastUpdated(rs.getDate("lastUpdated"));
      s.setPurpose(rs.getString("purpose"));
      s.setRegion(rs.getString("region"));
      s.setStockNumber(rs.getInt("stockNumber"));
      s.setTubeId(rs.getString("tubeId"));

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), s));
      }

      return s;
    }
  }

}
