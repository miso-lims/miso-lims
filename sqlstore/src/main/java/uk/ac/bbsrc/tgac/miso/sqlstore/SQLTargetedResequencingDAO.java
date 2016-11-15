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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedResequencing;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedResequencingStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
public class SQLTargetedResequencingDAO implements TargetedResequencingStore {

  private static String SELECT = "SELECT targetedResequencingId, alias, description, kitDescriptorId, archived, "
      + "createdBy, creationDate, updatedBy, lastUpdated  FROM TargetedResequencing";

  private static final String SELECT_BY_ID = SELECT + " WHERE targetedResequencingId=?";

  private static final String UPDATE = "UPDATE TargetedResequencing "
      + "SET targetedResequencingId=:targetedResequencingId, alias=:alias, description=:description, kitDescriptorId=:kitDescriptorId, "
      + "archived=:archived, createdBy=:createdBy, creationDate=:creationDate, updatedBy=:updatedBy WHERE dilutionId=:dilutionId";

  protected static final Logger log = LoggerFactory.getLogger(SQLTargetedResequencingDAO.class);

  private JdbcTemplate template;
  private SecurityStore securityDAO;

  @Autowired
  private KitStore kitStore;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private SecurityManager securityManager;

  @CoverageIgnore
  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @CoverageIgnore
  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  @CoverageIgnore
  public void setSecurityDAO(SecurityStore securityDAO) {
    this.securityDAO = securityDAO;
  }

  @CoverageIgnore
  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Override
  public long save(TargetedResequencing targetedResequencing) throws IOException {
    Date now = new Date();
    User currentUser = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", targetedResequencing.getAlias());
    params.addValue("description", targetedResequencing.getDescription());
    params.addValue("kitDescriptorId", targetedResequencing.getKitDescriptor().getId());
    params.addValue("archived", targetedResequencing.isArchived());
    params.addValue("lastUpdated", now);
    params.addValue("updatedBy", currentUser);
    if (targetedResequencing.getTargetedResequencingId() == TargetedResequencing.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName("TargetedResequencing")
          .usingGeneratedKeyColumns("targetedResequencingId");
      targetedResequencing.setTargetedResequencingId(DbUtils.getAutoIncrement(template, "TargetedResequencing"));
      params.addValue("creationDate", now);
      params.addValue("createdBy", currentUser);
      insert.executeAndReturnKey(params);
    } else {
      params.addValue("targetedResequencingId", targetedResequencing.getTargetedResequencingId());
      params.addValue("createdBy", targetedResequencing.getCreatedBy().getUserId());
      params.addValue("creationDate", targetedResequencing.getCreationDate());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(UPDATE, params);
    }
    return targetedResequencing.getTargetedResequencingId();
  }

  @Override
  public TargetedResequencing get(long targetedResequencingId) throws IOException {
    List<TargetedResequencing> results = template.query(SELECT_BY_ID, new Object[] { targetedResequencingId },
        new TargetedResequencingMapper());
    TargetedResequencing e = results.size() > 0 ? (TargetedResequencing) results.get(0) : null;
    return e;
  }

  @Override
  public TargetedResequencing lazyGet(long targetedResequencingId) throws IOException {
    List<TargetedResequencing> results = template.query(SELECT_BY_ID, new Object[] { targetedResequencingId },
        new TargetedResequencingMapper(true));
    TargetedResequencing e = results.size() > 0 ? (TargetedResequencing) results.get(0) : null;
    return e;
  }

  @Override
  public Collection<TargetedResequencing> listAll() throws IOException {
    return template.query("SELECT * FROM TargetedResequencing", new TargetedResequencingMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM TargetedResequencing");
  }

  public class TargetedResequencingMapper extends CacheAwareRowMapper<TargetedResequencing> {
    public TargetedResequencingMapper() {
      super(TargetedResequencing.class);
    }

    public TargetedResequencingMapper(boolean lazy) {
      super(TargetedResequencing.class, lazy);
    }

    @Override
    public TargetedResequencing mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("targetedResequencingId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug(String.format("Cache hit on map for TargetedResequencing %d", id));
          return (TargetedResequencing) element.getObjectValue();
        }
      }

      TargetedResequencing targetedResequencing = dataObjectFactory.getTargetedResequencing();
      targetedResequencing.setTargetedResequencingId(id);
      targetedResequencing.setAlias(rs.getString("alias"));
      targetedResequencing.setDescription(rs.getString("description"));
      targetedResequencing.setArchived(rs.getBoolean("archived"));
      targetedResequencing.setCreationDate(rs.getDate("creationDate"));
      targetedResequencing.setLastUpdated(rs.getDate("lastUpdated"));

      try {
        targetedResequencing.setCreatedBy(securityDAO.getUserById(rs.getLong("createdBy")));
        targetedResequencing.setUpdatedBy(securityDAO.getUserById(rs.getLong("updatedBy")));
        targetedResequencing.setKitDescriptor(kitStore.getKitDescriptorById(rs.getLong("kitDescriptorId")));
      } catch (IOException e1) {
        log.error("library dilution row mapper", e1);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), targetedResequencing));
      }

      return targetedResequencing;
    }
  }

}
