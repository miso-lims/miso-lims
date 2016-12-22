/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitComponentDescriptorImpl;
import uk.ac.bbsrc.tgac.miso.core.store.KitComponentDescriptorStore;
import uk.ac.bbsrc.tgac.miso.core.store.KitDescriptorStore;

/**
 * @author Michal Zak
 */
public class SQLKitComponentDescriptorDAO implements KitComponentDescriptorStore {
  private static final String TABLE_NAME = "KitComponentDescriptor";

  public static final String KIT_COMPONENT_DESCRIPTOR_SELECT = "SELECT kitComponentDescriptorId, name, referenceNumber, kitDescriptorId " +
      "FROM " + TABLE_NAME;

  public static final String KIT_COMPONENT_DESCRIPTOR_SELECT_BY_ID = KIT_COMPONENT_DESCRIPTOR_SELECT
      + " WHERE kitComponentDescriptorId = ?";

  public static final String KIT_COMPONENT_DESCRIPTOR_SELECT_BY_REFERENCE_NUMBER = KIT_COMPONENT_DESCRIPTOR_SELECT
      + " WHERE referenceNumber = ?";

  public static final String KIT_COMPONENT_DESCRIPTOR_SELECT_BY_KIT_DESCRIPTOR_ID = KIT_COMPONENT_DESCRIPTOR_SELECT
      + " WHERE kitDescriptorId = ?";

  public static final String KIT_COMPONENT_DESCRIPTOR_UPDATE = "UPDATE KitComponentDescriptor " +
      "SET name=:name, referenceNumber=:referenceNumber, kitDescriptorId=:kitDescriptorId " +
      "WHERE kitComponentDescriptorId=:kitComponentDescriptorId";

  protected static final Logger log = LoggerFactory.getLogger(SQLKitComponentDescriptorDAO.class);
  private JdbcTemplate jdbcTemplate;
  private KitDescriptorStore kitDescriptorDAO;

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.jdbcTemplate = template;
  }

  public void setKitDescriptorDAO(KitDescriptorStore kitDescriptorDAO) {
    this.kitDescriptorDAO = kitDescriptorDAO;
  }

  @Override
  public KitComponentDescriptor get(long id) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = jdbcTemplate.query(KIT_COMPONENT_DESCRIPTOR_SELECT_BY_ID, new Object[] { id }, new KitComponentDescriptorMapper());
    return eResults.size() > 0 ? (KitComponentDescriptor) eResults.get(0) : null;
  }

  @Override
  public KitComponentDescriptor lazyGet(long id) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = jdbcTemplate.query(KIT_COMPONENT_DESCRIPTOR_SELECT_BY_ID, new Object[] { id }, new KitComponentDescriptorMapper(true));
    return eResults.size() > 0 ? (KitComponentDescriptor) eResults.get(0) : null;
  }

  @Override
  public KitComponentDescriptor getKitComponentDescriptorById(long id) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = jdbcTemplate.query(KIT_COMPONENT_DESCRIPTOR_SELECT_BY_ID, new Object[] { id }, new KitComponentDescriptorMapper());
    return eResults.size() > 0 ? (KitComponentDescriptor) eResults.get(0) : null;
  }

  @Override
  public KitComponentDescriptor getKitComponentDescriptorByReferenceNumber(String referenceNumber) throws IOException {
    @SuppressWarnings("rawtypes")
    List eResults = jdbcTemplate.query(KIT_COMPONENT_DESCRIPTOR_SELECT_BY_REFERENCE_NUMBER, new Object[] { referenceNumber },
        new KitComponentDescriptorMapper());
    return eResults.size() > 0 ? (KitComponentDescriptor) eResults.get(0) : null;
  }

  @Override
  public List<KitComponentDescriptor> listKitComponentDescriptorsByKitDescriptorId(long kitDescriptorId) throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_DESCRIPTOR_SELECT_BY_KIT_DESCRIPTOR_ID, new Object[] { kitDescriptorId },
        new KitComponentDescriptorMapper());
  }

  @Override
  public Collection<KitComponentDescriptor> listAll() throws IOException {
    return jdbcTemplate.query(KIT_COMPONENT_DESCRIPTOR_SELECT, new KitComponentDescriptorMapper());
  }

  @Override
  public int count() throws IOException {
    return jdbcTemplate.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  public long saveKitComponentDescriptor(KitComponentDescriptor kcd) throws IOException {
    return save(kcd);
  }

  @Override
  public long save(KitComponentDescriptor kcd) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();

    params.addValue("name", kcd.getName())
        .addValue("referenceNumber", kcd.getReferenceNumber())
        .addValue("kitDescriptorId", kcd.getKitDescriptor().getId());

    if (kcd.getId() == KitComponentDescriptorImpl.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
          .withTableName("KitComponentDescriptor")
          .usingGeneratedKeyColumns("kitComponentDescriptorId");
      Number newId = insert.executeAndReturnKey(params);
      kcd.setId(newId.longValue());
    } else {
      params.addValue("kitDescriptorId", kcd.getId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
      namedTemplate.update(KIT_COMPONENT_DESCRIPTOR_UPDATE, params);
    }

    return kcd.getId();
  }

  public class KitComponentDescriptorMapper implements RowMapper<KitComponentDescriptor> {
    private boolean lazy = false;

    public KitComponentDescriptorMapper() {
      super();
    }

    public KitComponentDescriptorMapper(boolean lazy) {
      this.lazy = lazy;
    }

    @Override
    public KitComponentDescriptor mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("kitComponentDescriptorId");

      KitComponentDescriptor kitComponentDescriptor = new KitComponentDescriptorImpl();
      try {
        kitComponentDescriptor.setId(id);
        kitComponentDescriptor.setName(rs.getString("name"));
        kitComponentDescriptor.setReferenceNumber((rs.getString("referenceNumber")));

        if (!lazy) {
          kitComponentDescriptor.setKitDescriptor(kitDescriptorDAO.getKitDescriptorById(rs.getLong("kitDescriptorId")));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return kitComponentDescriptor;
    }
  }
}