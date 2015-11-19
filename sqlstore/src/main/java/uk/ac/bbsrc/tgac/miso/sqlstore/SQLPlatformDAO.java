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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLPlatformDAO implements PlatformStore {
  private static final String TABLE_NAME = "Platform";

  public static final String PLATFORMS_SELECT = "SELECT platformId, name, instrumentModel, description, numContainers " + "FROM "
      + TABLE_NAME;

  public static final String PLATFORM_NAMES_SELECT_DISTINCT = "SELECT DISTINCT name FROM " + TABLE_NAME;

  public static final String PLATFORM_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET name=:name, instrumentModel=:instrumentModel, description=:description, numContainers=:numContainers "
      + "WHERE platformId=:platformId";

  public static final String PLATFORM_SELECT_BY_ID = PLATFORMS_SELECT + " " + "WHERE platformId = ?";

  public static final String PLATFORMS_SELECT_BY_NAME = PLATFORMS_SELECT + " " + "WHERE name = ?";

  public static final String PLATFORM_SELECT_BY_MODEL = PLATFORMS_SELECT + " " + "WHERE instrumentModel = ?";

  private JdbcTemplate template;

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

  @Override
  public long save(Platform platform) throws IOException {
    // execute this procedure...
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("name", platform.getPlatformType().getKey());
    params.addValue("instrumentModel", platform.getInstrumentModel());
    params.addValue("description", platform.getDescription());
    params.addValue("numContainers", platform.getNumContainers());

    if (platform.getPlatformId() == null) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("platformId");
      Number newId = insert.executeAndReturnKey(params);
      platform.setPlatformId(newId.longValue());
    } else {
      params.addValue("platformId", platform.getPlatformId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(PLATFORM_UPDATE, params);
    }

    return platform.getPlatformId();
  }

  @Override
  public List<Platform> listAll() {
    return template.query(PLATFORMS_SELECT, new PlatformMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<String> listDistinctPlatformNames() {
    return template.queryForList(PLATFORM_NAMES_SELECT_DISTINCT, String.class);
  }

  @Override
  public List<Platform> listByName() {
    List results = template.query(PLATFORMS_SELECT_BY_NAME, new PlatformMapper());
    return results;
  }

  @Override
  public Platform getByModel(String model) {
    List eResults = template.query(PLATFORM_SELECT_BY_MODEL, new Object[] { model }, new PlatformMapper());
    Platform e = eResults.size() > 0 ? (Platform) eResults.get(0) : null;
    return e;
  }

  @Override
  public Platform get(long platformId) throws IOException {
    List eResults = template.query(PLATFORM_SELECT_BY_ID, new Object[] { platformId }, new PlatformMapper());
    Platform e = eResults.size() > 0 ? (Platform) eResults.get(0) : null;
    return e;
  }

  @Override
  public Platform lazyGet(long id) throws IOException {
    return get(id);
  }

  public class PlatformMapper implements RowMapper<Platform> {
    @Override
    public Platform mapRow(ResultSet rs, int rowNum) throws SQLException {
      Platform p = new PlatformImpl();
      p.setPlatformId(rs.getLong("platformId"));
      p.setPlatformType(PlatformType.get(rs.getString("name")));
      p.setDescription(rs.getString("description"));
      p.setInstrumentModel(rs.getString("instrumentModel"));
      p.setNumContainers(rs.getInt("numContainers"));
      return p;
    }
  }
}
