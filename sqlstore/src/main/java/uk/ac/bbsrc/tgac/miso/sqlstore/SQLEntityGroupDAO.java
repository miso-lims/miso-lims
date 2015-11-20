package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;

import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.EntityGroupImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.store.EntityGroupStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DaoLookup;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 23/10/13
 * @since 0.2.1-SNAPSHOT
 */
public class SQLEntityGroupDAO implements EntityGroupStore {
  private static final String TABLE_NAME = "EntityGroup";

  public static final String ENTITYGROUP_SELECT = "SELECT entityGroupId, parentId, parentType " + "FROM " + TABLE_NAME;

  public static final String ENTITYGROUP_SELECT_LIMIT = ENTITYGROUP_SELECT + " ORDER BY entityGroupId DESC LIMIT ?";

  public static final String ENTITYGROUP_SELECT_BY_ID = ENTITYGROUP_SELECT + " WHERE entityGroupId = ?";

  public static final String ENTITYGROUP_SELECT_BY_PARENT_TYPE_AND_ID = ENTITYGROUP_SELECT + " WHERE parentId = ? and parentType = ?";

  public static final String ENTITYGROUP_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE entityGroupId=:entityGroupId";

  public static final String ENTITYGROUP_ELEMENT_SELECT = "SELECT entityId, entityType FROM EntityGroup_Elements "
      + "WHERE entityGroup_entityGroupId = ? ORDER BY entityId";

  public static final String ENTITYGROUP_ELEMENT_DELETE_BY_GROUP_ID = "DELETE FROM EntityGroup_Elements "
      + "WHERE entityGroup_entityGroupId=:entityGroup_entityGroupId";

  protected static final Logger log = LoggerFactory.getLogger(SQLEntityGroupDAO.class);
  private JdbcTemplate template;
  private CascadeType cascadeType;

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Autowired
  private DaoLookup daoLookup;

  public void setDaoLookup(DaoLookup daoLookup) {
    this.daoLookup = daoLookup;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setCascadeType(CascadeType cascadeType) {
    this.cascadeType = cascadeType;
  }

  @Override
  public EntityGroup<? extends Nameable, ? extends Nameable> lazyGet(long groupId) throws IOException {
    List<EntityGroup<? extends Nameable, ? extends Nameable>> eResults = template.query(ENTITYGROUP_SELECT_BY_ID, new Object[] { groupId },
        new EntityGroupMapper(true));
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Collection<EntityGroup<? extends Nameable, ? extends Nameable>> listAllWithLimit(long limit) throws IOException {
    return null;
  }

  @Override
  public <T extends Nameable, S extends Nameable> EntityGroup<T, S> getEntityGroupByParentTypeAndId(Class<? extends T> parentType,
      long parentId) throws IOException, SQLException {
    List<Map<String, Object>> results = template.queryForList(ENTITYGROUP_SELECT_BY_PARENT_TYPE_AND_ID, parentId, parentType.getName());
    if (!results.isEmpty()) {
      EntityGroup osg = new EntityGroupImpl<T, S>();
      for (Map<String, Object> row : results) {
        Long groupId = (Long) row.get("entityGroupId");
        osg.setId(groupId);

        Store<? extends T> dao = daoLookup.lookup(parentType);
        if (dao != null) {
          if (parentType.equals(ProjectOverview.class)) {
            osg.setParent(((ProjectStore) dao).getProjectOverviewById(parentId));
          } else {
            osg.setParent(dao.get(parentId));
          }

          osg.setEntities(resolveEntityGroupElements(groupId));
        } else {
          throw new SQLException("No DAO found or more than one found.");
        }
      }
      return osg;
    }
    return null;
  }

  @Override
  public <T extends Nameable, S extends Nameable> EntityGroup<T, S> getEntityGroupByParent(T parent, Class<? extends T> parentClz)
      throws IOException, SQLException {
    String parentType = parentClz.getName();
    long parentId = parent.getId();

    List<Map<String, Object>> results = template.queryForList(ENTITYGROUP_SELECT_BY_PARENT_TYPE_AND_ID, parentId, parentType);
    if (!results.isEmpty()) {
      EntityGroup osg = new EntityGroupImpl<T, S>();
      for (Map<String, Object> row : results) {
        Long groupId = (Long) row.get("entityGroupId");
        osg.setId(groupId);
        osg.setParent(parent);
        osg.setEntities(resolveEntityGroupElements(groupId));
      }
      return osg;
    }
    return null;
  }

  @Override
  public boolean remove(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    if (entityGroup.isDeletable()
        && (namedTemplate.update(ENTITYGROUP_DELETE, new MapSqlParameterSource().addValue("entityGroupId", entityGroup.getId())) == 1)) {
      MapSqlParameterSource eparams = new MapSqlParameterSource();
      eparams.addValue("entityGroup_entityGroupId", entityGroup.getId());
      namedTemplate.update(ENTITYGROUP_ELEMENT_DELETE_BY_GROUP_ID, eparams);
      return true;
    }

    // explicit call to parent cache cleaning routines. a little more long winded than usual due to type erased parent
    // also can't call parentDao.save() as will probably end up in cyclical save operation
    Cache lazyCache = DbUtils.lookupCache(cacheManager, entityGroup.getParent().getClass(), true);
    Cache cache = DbUtils.lookupCache(cacheManager, entityGroup.getParent().getClass(), false);
    if (lazyCache != null) {
      DbUtils.updateCaches(lazyCache, entityGroup.getParent().getId());
    }
    if (cache != null) {
      DbUtils.updateCaches(cache, entityGroup.getParent().getId());
    }

    return false;
  }

  @Override
  public long save(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    // save group
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("parentId", entityGroup.getParent().getId());
    params.addValue("parentType", entityGroup.getParent().getClass().getName());

    if (entityGroup.getId() == EntityGroupImpl.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("entityGroupId");
      entityGroup.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

      Number newId = insert.executeAndReturnKey(params);
      if (newId.longValue() != entityGroup.getId()) {
        log.error("Expected EntityGroup ID doesn't match returned value from database insert: rolling back...");
        new NamedParameterJdbcTemplate(template).update(ENTITYGROUP_DELETE,
            new MapSqlParameterSource().addValue("entityGroupId", newId.longValue()));
        throw new IOException("Something bad happened. Expected EntityGroup ID doesn't match returned value from DB insert");
      }
    }

    if (entityGroup.getEntities() != null && !entityGroup.getEntities().isEmpty()) {
      MapSqlParameterSource eparams = new MapSqlParameterSource();
      eparams.addValue("entityGroup_entityGroupId", entityGroup.getId());

      NamedParameterJdbcTemplate nt = new NamedParameterJdbcTemplate(template);
      nt.update(ENTITYGROUP_ELEMENT_DELETE_BY_GROUP_ID, eparams);

      for (Nameable n : entityGroup.getEntities()) {
        if (n.getId() == 0) {
          Store<? super Nameable> dao = daoLookup.lookup(n.getClass());
          if (dao != null) {
            dao.save(n);
          } else {
            log.error("No dao class found for " + n.getClass().getName());
          }
        }

        SimpleJdbcInsert eInsert = new SimpleJdbcInsert(template).withTableName("EntityGroup_Elements");

        MapSqlParameterSource ltParams = new MapSqlParameterSource();
        Class<?> coreDatatype = daoLookup.getAssignableClassFromClass(n.getClass());
        if (coreDatatype == null) {
          coreDatatype = n.getClass();
        }
        ltParams.addValue("entityGroup_entityGroupId", entityGroup.getId());
        ltParams.addValue("entityType", coreDatatype.getName());
        ltParams.addValue("entityId", n.getId());

        eInsert.execute(ltParams);
      }
    }

    // explicit call to parent cache cleaning routines. a little more long winded than usual due to type erased parent
    // also can't call parentDao.save() as will probably end up in cyclical save operation
    Cache lazyCache = DbUtils.lookupCache(cacheManager, entityGroup.getParent().getClass(), true);
    Cache cache = DbUtils.lookupCache(cacheManager, entityGroup.getParent().getClass(), false);
    if (lazyCache != null) {
      DbUtils.updateCaches(lazyCache, entityGroup.getParent().getId());
    }
    if (cache != null) {
      DbUtils.updateCaches(cache, entityGroup.getParent().getId());
    }

    return entityGroup.getId();
  }

  @Override
  public EntityGroup<? extends Nameable, ? extends Nameable> get(long id) throws IOException {
    List<EntityGroup<? extends Nameable, ? extends Nameable>> eResults = template.query(ENTITYGROUP_SELECT_BY_ID, new Object[] { id },
        new EntityGroupMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Collection<EntityGroup<? extends Nameable, ? extends Nameable>> listAll() throws IOException {
    return template.query(ENTITYGROUP_SELECT, new EntityGroupMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM EntityGroup");
  }

  public class EntityGroupMapper extends CacheAwareRowMapper<EntityGroup<? extends Nameable, ? extends Nameable>> {
    public EntityGroupMapper() {
      super(
          (Class<EntityGroup<? extends Nameable, ? extends Nameable>>) ((ParameterizedType) new TypeReference<EntityGroup<? extends Nameable, ? extends Nameable>>() {
          }.getType()).getRawType());
    }

    public EntityGroupMapper(boolean lazy) {
      super(
          (Class<EntityGroup<? extends Nameable, ? extends Nameable>>) ((ParameterizedType) new TypeReference<EntityGroup<? extends Nameable, ? extends Nameable>>() {
          }.getType()).getRawType(), lazy);
    }

    @Override
    public EntityGroup<? extends Nameable, ? extends Nameable> mapRow(ResultSet rs, int rowNum) throws SQLException {
      // map parent
      Long groupId = rs.getLong("entityGroupId");
      Long parentId = rs.getLong("parentId");
      String parentType = rs.getString("parentType");

      EntityGroup<Nameable, Nameable> eg = new EntityGroupImpl<Nameable, Nameable>();
      eg.setId(groupId);

      try {
        Class<? extends Nameable> clz = Class.forName(parentType).asSubclass(Nameable.class);
        Store<? extends Nameable> dao = daoLookup.lookup(clz);
        if (dao != null) {
          // TODO this is horrific. split project overview stuff out of the project DAO
          // get on project dao returns a project, not a projectoverview! ARGH
          if (clz.equals(ProjectOverview.class)) {
            eg.setParent(((ProjectStore) dao).getProjectOverviewById(parentId));
          } else {
            eg.setParent(dao.get(parentId));
          }
        } else {
          throw new SQLException("No DAO found or more than one found.");
        }

        if (!isLazy()) {
          eg.setEntities(resolveEntityGroupElements(groupId));
        }

        return eg;
      } catch (ClassNotFoundException e) {
        throw new SQLException("Cannot resolve EntityGroup parent type to a valid class", e);
      } catch (IOException e) {
        throw new SQLException("Cannot retrieve EntityGroup [" + groupId + "] parent: [" + parentType + " ] " + parentId);
      }
    }
  }

  private Set<Nameable> resolveEntityGroupElements(long groupId) throws IOException, SQLException {
    try {
      Set<Nameable> elements = new HashSet<>();
      List<Map<String, Object>> rows = template.queryForList(ENTITYGROUP_ELEMENT_SELECT, groupId);
      for (Map<String, Object> map : rows) {
        Class<? extends Nameable> clz = Class.forName((String) map.get("entityType")).asSubclass(Nameable.class);
        Store<? extends Nameable> dao = daoLookup.lookup(clz);
        if (dao != null) {
          elements.add(dao.lazyGet((Long) map.get("entityId")));
        } else {
          throw new SQLException("No DAO found or more than one found.");
        }
      }
      return elements;
    } catch (ClassNotFoundException e) {
      throw new IOException(e);
    }
  }
}
