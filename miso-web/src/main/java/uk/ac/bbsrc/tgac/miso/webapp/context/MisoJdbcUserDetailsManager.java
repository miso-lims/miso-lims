package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import uk.ac.bbsrc.tgac.miso.core.security.MisoAuthority;

public class MisoJdbcUserDetailsManager extends JdbcUserDetailsManager {

  protected static final Logger log = LoggerFactory.getLogger(MisoJdbcUserDetailsManager.class);

  @Override
  protected List<GrantedAuthority> loadUserAuthorities(String username) {
    return (getJdbcTemplate().query(getAuthoritiesByUsernameQuery(), new String[] {username},
        new int[] {java.sql.Types.VARCHAR},
        new ResultSetExtractor<List<GrantedAuthority>>() {
          @Override
          public List<GrantedAuthority> extractData(ResultSet rs) throws SQLException {
            rs.next();
            List<GrantedAuthority> roleList = new ArrayList<>();
            Blob roleblob = rs.getBlob("authority");
            if (roleblob != null) {
              if (roleblob.length() > 0) {
                byte[] rbytes = roleblob.getBytes(1, (int) roleblob.length());
                String s1 = new String(rbytes);
                String[] roles = s1.split(",");
                for (String role : roles) {
                  log.debug("Found role " + role + " for " + rs.getString("username"));
                  SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                  roleList.add(authority);
                }
              } else {
                log.error("Cannot process user login - cannot extract roles from database");
              }
            }

            try {
              if (rs.getBoolean("admin"))
                roleList.add(MisoAuthority.ROLE_ADMIN);
              if (rs.getBoolean("internal"))
                roleList.add(MisoAuthority.ROLE_INTERNAL);
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
