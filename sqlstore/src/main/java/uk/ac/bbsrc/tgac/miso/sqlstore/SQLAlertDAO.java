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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.impl.DefaultAlert;
import uk.ac.bbsrc.tgac.miso.core.event.impl.SystemAlert;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;
import uk.ac.bbsrc.tgac.miso.core.store.AlertStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.cache.CacheAwareRowMapper;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 07/10/11
 * @since 0.1.2
 */
public class SQLAlertDAO implements AlertStore {
  private static final String TABLE_NAME = "Alert";

  private static final String ALERTS_SELECT = "SELECT alertId, title, text, userId, date, isRead, level " + "FROM " + TABLE_NAME;

  private static final String ALERT_SELECT_BY_ID = ALERTS_SELECT + " " + "WHERE alertId = ?";

  private static final String ALERT_UPDATE = "UPDATE " + TABLE_NAME
      + " SET title=:title, text=:text, userId=:userId, date=:date, isRead=:isRead, level=:level " + "WHERE alertId=:alertId";

  private static final String ALERT_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE alertId=:alertId";

  private static final String ALERTS_BY_USER = ALERTS_SELECT + " WHERE userId = ?";

  private static final String ALERTS_BY_USER_WITH_LIMIT = ALERTS_BY_USER + " ORDER BY date DESC LIMIT ?";

  private static final String ALERTS_BY_LEVEL = ALERTS_SELECT + " WHERE level = ?";

  private static final String UNREAD_ALERTS_BY_USER = ALERTS_SELECT + " WHERE userId = ? AND isRead = false";

  private static final String UNREAD_ALERTS_BY_LEVEL = ALERTS_SELECT + " WHERE level = ? AND isRead = false";

  protected static final Logger log = LoggerFactory.getLogger(SQLAlertDAO.class);

  @Autowired
  private CacheManager cacheManager;

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

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
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = "alertCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public boolean remove(Alert alert) throws IOException {
    return alert.isDeletable() && (template.update(ALERT_DELETE, alert.getAlertId()) == 1);
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  @TriggersRemove(cacheName = "alertCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public long save(Alert alert) throws IOException {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("title", alert.getAlertTitle());
    params.addValue("text", alert.getAlertText());
    params.addValue("date", alert.getAlertDate());
    params.addValue("isRead", alert.getAlertRead());
    params.addValue("level", alert.getAlertLevel().getKey());

    if (alert.getAlertUser() != null) {
      params.addValue("userId", alert.getAlertUser().getUserId());
    }

    if (alert.getAlertId() == DefaultAlert.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("alertId");
      Number newId = insert.executeAndReturnKey(params);
      alert.setAlertId(newId.longValue());
    } else {
      params.addValue("alertId", alert.getAlertId());
      NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
      namedTemplate.update(ALERT_UPDATE, params);
    }

    return alert.getAlertId();
  }

  @Override
  @Cacheable(cacheName = "alertCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = {
      @Property(name = "includeMethod", value = "false"), @Property(name = "includeParameterTypes", value = "false") }) )
  public Alert get(long alertId) throws IOException {
    List<Alert> eResults = template.query(ALERT_SELECT_BY_ID, new Object[] { alertId }, new AlertMapper());
    Alert e = eResults.size() > 0 ? eResults.get(0) : null;
    return e;
  }

  @Override
  public Alert lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<Alert> listAll() throws IOException {
    return template.query(ALERTS_SELECT, new AlertMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public Collection<Alert> listByUserId(long userId) throws IOException {
    return template.query(ALERTS_BY_USER, new Object[] { userId }, new AlertMapper());
  }

  @Override
  public Collection<Alert> listByUserId(long userId, long limit) throws IOException {
    return template.query(ALERTS_BY_USER_WITH_LIMIT, new Object[] { userId, limit }, new AlertMapper());
  }

  @Override
  public Collection<Alert> listByAlertLevel(AlertLevel alertLevel) throws IOException {
    return template.query(ALERTS_BY_LEVEL, new Object[] { alertLevel.getKey() }, new AlertMapper());
  }

  @Override
  public Collection<Alert> listUnreadByUserId(long userId) throws IOException {
    return template.query(UNREAD_ALERTS_BY_USER, new Object[] { userId }, new AlertMapper());
  }

  @Override
  public Collection<Alert> listUnreadByAlertLevel(AlertLevel alertLevel) throws IOException {
    return template.query(UNREAD_ALERTS_BY_LEVEL, new Object[] { alertLevel.getKey() }, new AlertMapper());
  }

  public class AlertMapper extends CacheAwareRowMapper<Alert> {
    public AlertMapper() {
      super(Alert.class);
    }

    public AlertMapper(boolean lazy) {
      super(Alert.class, lazy);
    }

    @Override
    public Alert mapRow(ResultSet rs, int rowNum) throws SQLException {
      long id = rs.getLong("alertId");
      Alert a = null;

      try {
        if (isCacheEnabled() && lookupCache(cacheManager) != null) {
          Element element;
          if ((element = lookupCache(cacheManager).get(DbUtils.hashCodeCacheKeyFor(id))) != null) {
            log.debug("Cache hit on map for Alert " + id);
            return (Alert) element.getObjectValue();
          }
        }

        try {
          if (rs.getLong("userId") == LimsUtils.SYSTEM_USER_ID) {
            a = new SystemAlert();
          } else {
            User u = securityManager.getUserById(rs.getLong("userId"));
            a = new DefaultAlert(u);
          }
          a.setAlertId(id);
          a.setAlertTitle(rs.getString("title"));
          a.setAlertText(rs.getString("text"));
          a.setAlertRead(rs.getBoolean("isRead"));
          a.setAlertLevel(AlertLevel.get(rs.getString("level")));
          a.setAlertDate(rs.getDate("date"));
        } catch (IOException e1) {
          log.error("alert row mapper", e1);
        }

        if (isCacheEnabled() && lookupCache(cacheManager) != null) {
          lookupCache(cacheManager).put(new Element(DbUtils.hashCodeCacheKeyFor(id), a));
        }
      } catch (CacheException ce) {
        log.error("alert row mapper", ce);
      } catch (UnsupportedOperationException uoe) {
        log.error("alert row mapper", uoe);
      }
      return a;
    }
  }
}
