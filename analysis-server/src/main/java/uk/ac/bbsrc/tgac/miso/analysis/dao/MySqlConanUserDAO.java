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

package uk.ac.bbsrc.tgac.miso.analysis.dao;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fgpt.conan.core.user.ConanUserWithPermissions;
import uk.ac.ebi.fgpt.conan.dao.DatabaseConanUserDAO;
import uk.ac.ebi.fgpt.conan.model.ConanUser;
/**
 * uk.ac.bbsrc.tgac.miso.analysis
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 07/11/11
 * @since 0.1.3
 */
public class MySqlConanUserDAO extends DatabaseConanUserDAO {
  protected static final Logger log = LoggerFactory.getLogger(MySqlConanUserDAO.class);
  private long getAutoIncrement(String tableName) throws IOException {
    final String q = "SHOW TABLE STATUS LIKE '" + tableName + "'";
    Map<String, Object> rs = getJdbcTemplate().queryForMap(q);
    Object ai = rs.get("Auto_increment");
    if (ai != null) {
      return new Long(ai.toString());
    } else {
      throw new IOException("Cannot resolve Auto_increment value from DBMS metadata tables");
    }
  }

  @Override
  public ConanUser saveUser(ConanUser user) {
    int userCheck = 0;

    if (user.getId() != null) {
      userCheck = getJdbcTemplate().queryForInt(USER_COUNT, user.getId());
    }

    // There is no such user in database
    if (userCheck == 0) {
      try {
        int userID = (int) getAutoIncrement("CONAN_USERS");
        getJdbcTemplate().update(USER_INSERT, userID, user.getUserName(), user.getFirstName(), user.getSurname(), user.getEmail(),
            user.getRestApiKey(), user.getPermissions().toString());
        if (user instanceof ConanUserWithPermissions) {
          ((ConanUserWithPermissions) user).setId(Integer.toString(userID));
        } else {
          getLog().warn("User acquired from database was of unexpected type " + user.getClass().getSimpleName() + ", cannot set user ID");
        }
      } catch (IOException e) {
        log.error("save user", e);
      }
    } else {
      getJdbcTemplate().update(USER_UPDATE, user.getUserName(), user.getFirstName(), user.getSurname(), user.getEmail(),
          user.getRestApiKey(), user.getPermissions().toString(), user.getId());
    }

    return user;
  }
}
