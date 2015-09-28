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

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import uk.ac.bbsrc.tgac.miso.core.data.Assignable;
import uk.ac.bbsrc.tgac.miso.core.store.AssigneeStore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 09/05/14
 * @since 0.2.1-SNAPSHOT
 */
public class SQLAssigneeDAO implements AssigneeStore {
  private static final String ASSIGNEE_SELECT =
          "SELECT entityName, userId " +
          "FROM Assignee";

  private static final String ASSIGNEE_SELECT_BY_ENTITY_NAME =
          ASSIGNEE_SELECT + " WHERE entityName = ?";

  private static final String ASSIGNED_ENTITIES_BY_USER =
          ASSIGNEE_SELECT + " WHERE userId = ?";

  private static final String ASSIGNEE_DELETE_BY_USER_ID =
          "DELETE FROM Assignee WHERE entityName=:entityName AND userId=:userId";

  private static final String ASSIGNEE_DELETE =
          "DELETE FROM Assignee WHERE entityName=:entityName";

  protected static final Logger log = LoggerFactory.getLogger(SQLAssigneeDAO.class);

  @Autowired
  private SecurityManager securityManager;

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
  public User getAssigneeByEntityName(String entityName) throws IOException {
    List<User> eResults = template.query(ASSIGNEE_SELECT_BY_ENTITY_NAME, new Object[]{entityName}, new AssigneeMapper());
    return eResults.size() > 0 ? eResults.get(0) : null;
  }

  @Override
  public Collection<Assignable> getEntitiesByAssignee(User assignee) throws IOException {
    //return template.query(ASSIGNED_ENTITIES_BY_USER, new Object[]{assignee.getUserId()}, new AssigneeMapper());
    return null;
  }

  @Override
  public boolean removeAssignedEntity(Assignable assignable) throws IOException {
    MapSqlParameterSource eParams = new MapSqlParameterSource();
    eParams.addValue("entityName", assignable.getAssignableIdentifier());
    NamedParameterJdbcTemplate eNamedTemplate = new NamedParameterJdbcTemplate(template);
    return eNamedTemplate.update(ASSIGNEE_DELETE, eParams) == 1;
  }

  @Override
  public boolean removeAssignedEntityByUser(Assignable assignable, User user) throws IOException {
    if (user != null) {
      MapSqlParameterSource eParams = new MapSqlParameterSource();
      eParams.addValue("entityName", assignable.getAssignableIdentifier());
      eParams.addValue("userId", user.getUserId());

      log.debug("DAO removal of " + user.getUserId() + " from " + assignable.getAssignableIdentifier());

      NamedParameterJdbcTemplate eNamedTemplate = new NamedParameterJdbcTemplate(template);
      return eNamedTemplate.update(ASSIGNEE_DELETE_BY_USER_ID, eParams) == 1;
    }
    return false;
  }

  @Override
  public void saveAssignedEntityUser(Assignable assignable, User user) throws IOException {
    if (user != null) {
      SimpleJdbcInsert fInsert = new SimpleJdbcInsert(template).withTableName("Assignee");
      MapSqlParameterSource fcParams = new MapSqlParameterSource();
      fcParams.addValue("entityName", assignable.getAssignableIdentifier())
              .addValue("userId", user.getUserId());
      try {
        fInsert.execute(fcParams);
        log.debug("DAO insert of " + user.getUserId() + " on " + assignable.getAssignableIdentifier());
      }
      catch(DuplicateKeyException dke) {
        log.debug("This Assignable combination already exists - not inserting: " + dke.getMessage());
      }
    }
  }

  public class AssigneeMapper implements RowMapper<User> {
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      try {
        return securityManager.getUserById(rs.getLong("userId"));
      }
      catch (IOException e) {
        throw new SQLException(e);
      }
    }
  }
}
