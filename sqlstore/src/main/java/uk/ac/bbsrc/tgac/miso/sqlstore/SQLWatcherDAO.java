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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Watchable;
import uk.ac.bbsrc.tgac.miso.core.store.WatcherStore;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 07/10/11
 * @since 0.1.3
 */
public class SQLWatcherDAO implements WatcherStore {
  private static final String WATCHER_SELECT = "SELECT entityName, userId " + "FROM Watcher";

  private static final String WATCHERS_SELECT_BY_ENTITY_NAME = WATCHER_SELECT + " WHERE entityName = ?";

  private static final String WATCHED_ENTITIES_BY_USER = WATCHER_SELECT + " WHERE userId = ?";

  private static final String WATCHER_DELETE_BY_USER_ID = "DELETE FROM Watcher WHERE entityName=:entityName AND userId=:userId";

  private static final String WATCHER_DELETE = "DELETE FROM Watcher WHERE entityName=:entityName";

  protected static final Logger log = LoggerFactory.getLogger(SQLWatcherDAO.class);

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  private JdbcTemplate template;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public Collection<User> getWatchersByWatcherGroup(String groupName) throws IOException {
    return securityManager.listUsersByGroupName(groupName);
  }

  @Override
  public Collection<User> getWatchersByEntityName(String entityName) throws IOException {
    return template.query(WATCHERS_SELECT_BY_ENTITY_NAME, new Object[] { entityName }, new WatcherMapper());
  }

  @Override
  public boolean removeWatchedEntity(Watchable watchable) throws IOException {
    MapSqlParameterSource eParams = new MapSqlParameterSource();
    eParams.addValue("entityName", watchable.getWatchableIdentifier());
    NamedParameterJdbcTemplate eNamedTemplate = new NamedParameterJdbcTemplate(template);
    return eNamedTemplate.update(WATCHER_DELETE, eParams) == 1;
  }

  @Override
  public boolean removeWatchedEntityByUser(Watchable watchable, User user) throws IOException {
    if (user != null) {
      MapSqlParameterSource eParams = new MapSqlParameterSource();
      eParams.addValue("entityName", watchable.getWatchableIdentifier());
      eParams.addValue("userId", user.getUserId());

      log.debug("DAO removal of " + user.getUserId() + " from " + watchable.getWatchableIdentifier());

      NamedParameterJdbcTemplate eNamedTemplate = new NamedParameterJdbcTemplate(template);
      return eNamedTemplate.update(WATCHER_DELETE_BY_USER_ID, eParams) == 1;
    }
    return false;
  }

  @Override
  public void saveWatchedEntityUser(Watchable watchable, User user) throws IOException {
    if (user != null) {
      SimpleJdbcInsert fInsert = new SimpleJdbcInsert(template).withTableName("Watcher");
      MapSqlParameterSource fcParams = new MapSqlParameterSource();
      fcParams.addValue("entityName", watchable.getWatchableIdentifier());
      fcParams.addValue("userId", user.getUserId());
      try {
        fInsert.execute(fcParams);
        log.debug("DAO insert of " + user.getUserId() + " on " + watchable.getWatchableIdentifier());
      } catch (DuplicateKeyException dke) {
        log.error("This Watcher combination already exists - not inserting", dke);
      }
    }
  }

  public class WatcherMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      try {
        return securityManager.getUserById(rs.getLong("userId"));
      } catch (IOException e) {
        throw new SQLException(e);
      }
    }
  }
}
