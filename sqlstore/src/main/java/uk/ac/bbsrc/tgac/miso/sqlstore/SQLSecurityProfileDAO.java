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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.CacheManager;

import uk.ac.bbsrc.tgac.miso.core.store.Store;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Transactional(rollbackFor = Exception.class)
public class SQLSecurityProfileDAO implements Store<SecurityProfile> {
  private static final String TABLE_NAME = "SecurityProfile";

  public static final String PROFILES_SELECT = "SELECT profileId, allowAllInternal, owner_userId " + "FROM " + TABLE_NAME;

  public static final String PROFILE_SELECT_BY_ID = PROFILES_SELECT + " WHERE profileId = ?";

  protected static final Logger log = LoggerFactory.getLogger(SQLSecurityProfileDAO.class);

  private SecurityManager securityManager;
  private JdbcTemplate template;
  private final int maxQueryParams = 500;

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

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
  @TriggersRemove(cacheName = { "securityProfileCache" }, keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public long save(SecurityProfile securityProfile) throws IOException {
    SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME);
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("allowAllInternal", securityProfile.isAllowAllInternal());

    if (securityProfile.getOwner() != null) {
      params.addValue("owner_userId", securityProfile.getOwner().getUserId());
    }

    // if a profile already exists then delete all the old rows first, and repopulate.
    // easier than trying to work out which rows need to be updated and which don't
    if (securityProfile.getProfileId() != SecurityProfile.UNSAVED_ID) {
      MapSqlParameterSource delparams = new MapSqlParameterSource();
      delparams.addValue("profileId", securityProfile.getProfileId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      for (String deleteQuery : new String[] { "DELETE FROM " + TABLE_NAME + " WHERE profileId=:profileId",
          "DELETE FROM SecurityProfile_ReadUser WHERE SecurityProfile_profileId=:profileId",
          "DELETE FROM SecurityProfile_WriteUser WHERE SecurityProfile_profileId=:profileId",
          "DELETE FROM SecurityProfile_ReadGroup WHERE SecurityProfile_profileId=:profileId",
          "DELETE FROM SecurityProfile_WriteGroup WHERE SecurityProfile_profileId=:profileId" }) {
        namedTemplate.update(deleteQuery, delparams);
      }

      List<SecurityProfile> results = template.query(PROFILE_SELECT_BY_ID, new Object[] { securityProfile.getProfileId() },
          new SecurityProfileMapper());
      if (results.size() > 0) {
        log.error("SecurityProfile users/group relationships deletion failed!");
      } else {
        params.addValue("profileId", securityProfile.getProfileId());
        insert.execute(params);
      }
    } else {
      insert.usingGeneratedKeyColumns("profileId");
      Number newId = insert.executeAndReturnKey(params);
      securityProfile.setProfileId(newId.longValue());
    }

    // profile read users
    if (securityProfile.getReadUsers() != null && !securityProfile.getReadUsers().isEmpty()) {
      SimpleJdbcInsert uInsert = new SimpleJdbcInsert(template).withTableName("SecurityProfile_ReadUser");

      for (User u : securityProfile.getReadUsers()) {
        MapSqlParameterSource uParams = new MapSqlParameterSource();
        uParams.addValue("SecurityProfile_profileId", securityProfile.getProfileId());
        uParams.addValue("readUser_userId", u.getUserId());
        uInsert.execute(uParams);
      }
    }

    // profile write users
    if (securityProfile.getWriteUsers() != null && !securityProfile.getWriteUsers().isEmpty()) {
      SimpleJdbcInsert uInsert = new SimpleJdbcInsert(template).withTableName("SecurityProfile_WriteUser");

      for (User u : securityProfile.getWriteUsers()) {
        MapSqlParameterSource uParams = new MapSqlParameterSource();
        uParams.addValue("SecurityProfile_profileId", securityProfile.getProfileId());
        uParams.addValue("writeUser_userId", u.getUserId());
        uInsert.execute(uParams);
      }
    }

    // profile read groups
    if (securityProfile.getReadGroups() != null && !securityProfile.getReadGroups().isEmpty()) {
      SimpleJdbcInsert uInsert = new SimpleJdbcInsert(template).withTableName("SecurityProfile_ReadGroup");

      for (Group g : securityProfile.getReadGroups()) {
        MapSqlParameterSource uParams = new MapSqlParameterSource();
        uParams.addValue("SecurityProfile_profileId", securityProfile.getProfileId());
        uParams.addValue("readGroup_groupId", g.getGroupId());
        uInsert.execute(uParams);
      }
    }

    // profile read groups
    if (securityProfile.getWriteGroups() != null && !securityProfile.getWriteGroups().isEmpty()) {
      SimpleJdbcInsert uInsert = new SimpleJdbcInsert(template).withTableName("SecurityProfile_WriteGroup");

      for (Group g : securityProfile.getWriteGroups()) {
        MapSqlParameterSource uParams = new MapSqlParameterSource();
        uParams.addValue("SecurityProfile_profileId", securityProfile.getProfileId());
        uParams.addValue("writeGroup_groupId", g.getGroupId());
        uInsert.execute(uParams);
      }
    }

    return securityProfile.getProfileId();
  }

  @Override
  public Collection<SecurityProfile> listAll() throws IOException {
    return template.query(PROFILES_SELECT, new SecurityProfileMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  @Cacheable(cacheName = "securityProfileCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }))
  public SecurityProfile get(long id) throws IOException {
    List<SecurityProfile> results = template.query(PROFILE_SELECT_BY_ID, new Object[] { id }, new SecurityProfileMapper());
    SecurityProfile sp = results.size() > 0 ? results.get(0) : null;

    if (sp != null) {
      fillOutSecurityProfile(sp);
    } else {
      sp = new SecurityProfile();
    }

    return sp;
  }

  @Override
  public SecurityProfile lazyGet(long id) throws IOException {
    return get(id);
  }

  private List<Long> getIds(long id, String query) {
    return template.queryForList(query, Long.class, id);
  }

  private void fillOutSecurityProfile(SecurityProfile sp) throws IOException {
    sp.getReadUsers().addAll(securityManager.listUsersByIds(
        getIds(sp.getProfileId(), "SELECT readUser_userId FROM SecurityProfile_ReadUser WHERE SecurityProfile_profileId=?")));
    sp.getWriteUsers().addAll(securityManager.listUsersByIds(
        getIds(sp.getProfileId(), "SELECT writeUser_userId FROM SecurityProfile_WriteUser WHERE SecurityProfile_profileId=?")));
    sp.getReadGroups().addAll(securityManager.listGroupsByIds(
        getIds(sp.getProfileId(), "SELECT readGroup_groupId FROM SecurityProfile_ReadGroup WHERE SecurityProfile_profileId=?")));
    sp.getWriteGroups().addAll(securityManager.listGroupsByIds(
        getIds(sp.getProfileId(), "SELECT writeGroup_groupId FROM SecurityProfile_WriteGroup WHERE SecurityProfile_profileId=?")));
  }

  public class SecurityProfileMapper implements RowMapper<SecurityProfile> {
    @Override
    public SecurityProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
      SecurityProfile sp = new SecurityProfile();
      sp.setProfileId(rs.getLong("profileId"));
      sp.setAllowAllInternal(rs.getBoolean("allowAllInternal"));

      try {
        sp.setOwner(securityManager.getUserById(rs.getLong("owner_userId")));
      } catch (IOException e) {
        log.error("security profile mapper", e);
      }

      return sp;
    }
  }
}
