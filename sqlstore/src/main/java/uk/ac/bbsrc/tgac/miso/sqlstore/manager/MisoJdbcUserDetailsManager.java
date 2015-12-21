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

package uk.ac.bbsrc.tgac.miso.sqlstore.manager;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore.manager
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoJdbcUserDetailsManager extends JdbcUserDetailsManager {

  protected static final Logger log = LoggerFactory.getLogger(MisoJdbcUserDetailsManager.class);

  @Override
  protected List<GrantedAuthority> loadUserAuthorities(String username) {
    return (getJdbcTemplate().query(getAuthoritiesByUsernameQuery(), new String[] { username },
        new ResultSetExtractor<List<GrantedAuthority>>() {
          @Override
          public List<GrantedAuthority> extractData(ResultSet rs) throws SQLException {
            rs.next();
            List<GrantedAuthority> roleList = new ArrayList<GrantedAuthority>();
            Blob roleblob = rs.getBlob("authority");
            if (roleblob != null) {
              if (roleblob.length() > 0) {
                byte[] rbytes = roleblob.getBytes(1, (int) roleblob.length());
                String s1 = new String(rbytes);
                String[] roles = s1.split(",");
                for (String role : roles) {
                  log.info("Found role " + role + " for " + rs.getString("username"));
                  GrantedAuthorityImpl authority = new GrantedAuthorityImpl(role);
                  roleList.add(authority);
                }
              } else {
                log.error("Cannot process user login - cannot extract roles from database");
              }
            }

            try {
              if (rs.getBoolean("admin")) roleList.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
              if (rs.getBoolean("external")) roleList.add(new GrantedAuthorityImpl("ROLE_EXTERNAL"));
              if (rs.getBoolean("internal")) roleList.add(new GrantedAuthorityImpl("ROLE_INTERNAL"));
            } catch (SQLException e) {
              log.error("Couldn't retrieve a user property to convert to a role", e);
            }

            if (roleList.isEmpty()) {
              log.warn("User has null roles. This may affect their ability to access MISO.");
            }
            return roleList;
          }
        }));
  }
}
