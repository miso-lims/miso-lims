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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.store.AttachableStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DaoLookup;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Persistence implementation for mapping Attachables
 *
 * @author Rob Davey
 * @date 26/08/14
 * @since 0.2.1-SNAPSHOT
 */
public class SQLAttachableDAO implements AttachableStore {
  private static final String ATTACHABLE_SELECT =
          "SELECT attachableId, attachableEntityType, attachedId, attachedEntityType " +
          "FROM Attached_Elements";

  private static final String SELECT_BY_ATTACHABLE_ENTITY_TYPE =
          ATTACHABLE_SELECT + " WHERE attachableEntityType = ?";

  private static final String ATTACHED_SELECT_BY_ATTACHABLE =
          ATTACHABLE_SELECT + " WHERE attachableId = ? AND attachableEntityType = ?";

  private static final String ATTACHABLE_SELECT_BY_ATTACHED =
          ATTACHABLE_SELECT + " WHERE attachedId = ? AND attachedEntityType = ?";

  private static final String UNATTACH =
          "DELETE FROM Attached_Elements WHERE attachableEntityType=:attachableEntityType AND attachableId=:attachableId";

  private static final String UNATTACH_BY_ATTACHED_ID =
          "DELETE FROM Attached_Elements WHERE attachableId=:attachableId AND attachedId=:attachedId "+
          "AND attachableEntityType=:attachableEntityType AND attachedEntityType=:attachedEntityType";

  protected static final Logger log = LoggerFactory.getLogger(SQLAttachableDAO.class);

  private JdbcTemplate template;

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Autowired
  private DaoLookup daoLookup;

  public void setDaoLookup(DaoLookup daoLookup) {
    this.daoLookup = daoLookup;
  }

  @Override
  public boolean unattachAll(Attachable attachable) throws IOException {
    MapSqlParameterSource eParams = new MapSqlParameterSource();
    eParams.addValue("attachableId", attachable.getId())
           .addValue("attachableEntityType", attachable.getClass().getSimpleName());
    NamedParameterJdbcTemplate eNamedTemplate = new NamedParameterJdbcTemplate(template);
    return eNamedTemplate.update(UNATTACH, eParams) == 1;
  }

  @Override
  public boolean unattachByAttachedId(Attachable attachable, Nameable attached) throws IOException {
    MapSqlParameterSource eParams = new MapSqlParameterSource();
    eParams.addValue("attachableId", attachable.getId())
           .addValue("attachableEntityType", attachable.getClass().getSimpleName())
           .addValue("attachedId", attached.getId())
           .addValue("attachedEntityType", attached.getClass().getSimpleName());
    NamedParameterJdbcTemplate eNamedTemplate = new NamedParameterJdbcTemplate(template);
    return eNamedTemplate.update(UNATTACH_BY_ATTACHED_ID, eParams) == 1;
  }

  public List<? extends Nameable> listAttachedByAttachableType(Attachable<? extends Nameable> attachable) throws IOException {
    return template.query(SELECT_BY_ATTACHABLE_ENTITY_TYPE, new Object[]{attachable.getClass().getSimpleName()}, new AttachedMapper());
  }

  public List<Attachable<? extends Nameable>> listAttachableByAttached(Nameable attached) throws IOException {
    return template.query(ATTACHABLE_SELECT_BY_ATTACHED, new Object[]{attached.getId(), attached.getClass().getSimpleName()}, new AttachableMapper());
  }

  public List<? extends Nameable> listAttachedByAttachable(Attachable<? extends Nameable> attachable) throws IOException {
    return template.query(ATTACHED_SELECT_BY_ATTACHABLE, new Object[]{attachable.getId(), attachable.getClass().getSimpleName()}, new AttachedMapper());
  }

  public long save(Attachable<? extends Nameable> attachable) throws IOException {
    if (attachable != null) {
      SimpleJdbcInsert fInsert = new SimpleJdbcInsert(template).withTableName("Attached_Elements");
      for (Nameable n : attachable.getAttached()) {
        MapSqlParameterSource fcParams = new MapSqlParameterSource();
        fcParams.addValue("attachableId", attachable.getId())
                .addValue("attachableEntityType", attachable.getClass().getSimpleName())
                .addValue("attachedId", n.getId())
                .addValue("attachedEntityType", n.getClass().getSimpleName());
        try {
          log.debug("DAO insert of " + attachable.getId() + " on " + attachable.getName());
          return fInsert.execute(fcParams);
        }
        catch(DuplicateKeyException dke) {
          log.debug("This Assignable combination already exists - not inserting: " + dke.getMessage());
        }
      }
    }
    return 0L;
  }

  public class AttachableMapper implements RowMapper<Attachable<? extends Nameable>> {
    public Attachable<? extends Nameable> mapRow(ResultSet rs, int rowNum) throws SQLException {
      Long attachableId = rs.getLong("attachableId");
      String attachableEntityType = rs.getString("attachableEntityType");

      try {
        Class<? extends Attachable> attachableClz = Class.forName(attachableEntityType).asSubclass(Attachable.class);
        Store<? extends Attachable> attachableDao = daoLookup.lookup(attachableClz);

        if (attachableDao != null) {
          //log.debug("Mapping poolable -> " + poolId + " : " + type + " : " + elementId);
          Attachable attachable = attachableDao.get(attachableId);

          if (attachable != null) {
            log.debug("\\_ " + attachable.getId() + " [" + attachable.getName() + "]");
            for (Nameable n : listAttachedByAttachable(attachable)) {
              //attach
              attachable.attach(n);
            }
          }
          else {
            log.debug("\\_ got null");
          }
          return attachable;
        }
        else {
          throw new SQLException("No DAO found or more than one found.");
        }
      }
      catch (ClassNotFoundException e) {
        throw new SQLException("Cannot resolve element type to a valid class", e);
      }
      catch (IOException e) {
        throw new SQLException("Cannot retrieve attachable element: [" + attachableEntityType + " ] " + attachableId);
      }
    }
  }

  public class AttachedMapper implements RowMapper<Nameable> {
    public Nameable mapRow(ResultSet rs, int rowNum) throws SQLException {
      Long attachedId = rs.getLong("attachedId");
      String attachedEntityType = rs.getString("attachedEntityType");

      try {
        Class<? extends Nameable> attachedClz = Class.forName(attachedEntityType).asSubclass(Nameable.class);
        Store<? extends Nameable> attachedDao = daoLookup.lookup(attachedClz);

        if (attachedDao != null) {
          //log.debug("Mapping poolable -> " + poolId + " : " + type + " : " + elementId);
          Nameable attached = attachedDao.lazyGet(attachedId);

          if (attached != null) {
            log.debug("\\_ " + attached.getId() + " [" + attached.getName() + "]");
          }
          else {
            log.debug("\\_ got null");
          }
          return attached;
        }
        else {
          throw new SQLException("No DAO found or more than one found.");
        }
      }
      catch (ClassNotFoundException e) {
        throw new SQLException("Cannot resolve element type to a valid class", e);
      }
      catch (IOException e) {
        throw new SQLException("Cannot retrieve poolable element: [" + attachedEntityType + " ] " + attachedId);
      }
    }
  }
}
