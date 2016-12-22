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

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.KitComponentStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitDescriptorStore;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedSequencingStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
public class SQLTargetedSequencingDAO implements TargetedSequencingStore {

  private static String SELECT = "SELECT targetedSequencingId, alias, description, kitDescriptorId, archived, "
      + "createdBy, creationDate, updatedBy, lastUpdated  FROM TargetedSequencing";

  private static final String SELECT_BY_ID = SELECT + " WHERE targetedSequencingId=?";

  private static final String UPDATE = "UPDATE TargetedSequencing "
      + "SET targetedSequencingId=:targetedSequencingId, alias=:alias, description=:description, kitDescriptorId=:kitDescriptorId, "
      + "archived=:archived, createdBy=:createdBy, creationDate=:creationDate, updatedBy=:updatedBy WHERE dilutionId=:dilutionId";

  protected static final Logger log = LoggerFactory.getLogger(SQLTargetedSequencingDAO.class);

  private JdbcTemplate template;
  private SecurityStore securityDAO;

  @Autowired
  private KitComponentStore kitComponentStore;

  @Autowired
  private KitDescriptorStore kitDescriptorStore;

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
  public long save(TargetedSequencing targetedSequencing) throws IOException {
    Date now = new Date();
    User currentUser = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", targetedSequencing.getAlias());
    params.addValue("description", targetedSequencing.getDescription());
    params.addValue("kitDescriptorId", targetedSequencing.getKitDescriptor().getId());
    params.addValue("archived", targetedSequencing.isArchived());
    params.addValue("lastUpdated", now);
    params.addValue("updatedBy", currentUser);
    if (targetedSequencing.getTargetedSequencingId() == TargetedSequencing.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName("TargetedSequencing")
          .usingGeneratedKeyColumns("targetedSequencingId");
      targetedSequencing.setTargetedSequencingId(DbUtils.getAutoIncrement(template, "TargetedSequencing"));
      params.addValue("creationDate", now);
      params.addValue("createdBy", currentUser);
      insert.executeAndReturnKey(params);
    } else {
      params.addValue("targetedSequencingId", targetedSequencing.getTargetedSequencingId());
      params.addValue("createdBy", targetedSequencing.getCreatedBy().getUserId());
      params.addValue("creationDate", targetedSequencing.getCreationDate());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(UPDATE, params);
    }
    return targetedSequencing.getTargetedSequencingId();
  }

  @Override
  public TargetedSequencing get(long targetedSequencingId) throws IOException {
    List<TargetedSequencing> results = template.query(SELECT_BY_ID, new Object[] { targetedSequencingId },
        new TargetedSequencingMapper());
    TargetedSequencing e = results.size() > 0 ? (TargetedSequencing) results.get(0) : null;
    return e;
  }

  @Override
  public TargetedSequencing lazyGet(long targetedSequencingId) throws IOException {
    List<TargetedSequencing> results = template.query(SELECT_BY_ID, new Object[] { targetedSequencingId },
        new TargetedSequencingMapper(true));
    TargetedSequencing e = results.size() > 0 ? (TargetedSequencing) results.get(0) : null;
    return e;
  }

  @Override
  public Collection<TargetedSequencing> listAll() throws IOException {
    return template.query("SELECT * FROM TargetedSequencing", new TargetedSequencingMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM TargetedSequencing");
  }

  public class TargetedSequencingMapper extends CacheAwareRowMapper<TargetedSequencing> {
    public TargetedSequencingMapper() {
      super(TargetedSequencing.class);
    }

    public TargetedSequencingMapper(boolean lazy) {
      super(TargetedSequencing.class, lazy);
    }

    @Override
    public TargetedSequencing mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("targetedSequencingId");

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        Element element;
        if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
          log.debug(String.format("Cache hit on map for TargetedSequencing %d", id));
          return (TargetedSequencing) element.getObjectValue();
        }
      }

      TargetedSequencing targetedSequencing = dataObjectFactory.getTargetedSequencing();
      targetedSequencing.setTargetedSequencingId(id);
      targetedSequencing.setAlias(rs.getString("alias"));
      targetedSequencing.setDescription(rs.getString("description"));
      targetedSequencing.setArchived(rs.getBoolean("archived"));
      targetedSequencing.setCreationDate(rs.getDate("creationDate"));
      targetedSequencing.setLastUpdated(rs.getDate("lastUpdated"));

      try {
        targetedSequencing.setCreatedBy(securityDAO.getUserById(rs.getLong("createdBy")));
        targetedSequencing.setUpdatedBy(securityDAO.getUserById(rs.getLong("updatedBy")));
        targetedSequencing.setKitDescriptor(kitDescriptorStore.getKitDescriptorById(rs.getLong("kitDescriptorId")));
      } catch (IOException e1) {
        log.error("library dilution row mapper", e1);
      }

      if (isCacheEnabled() && lookupCache(cacheManager) != null) {
        lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), targetedSequencing));
      }

      return targetedSequencing;
    }
  }

}
