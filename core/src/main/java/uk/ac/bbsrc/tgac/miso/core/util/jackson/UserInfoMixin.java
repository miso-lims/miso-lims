package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import com.eaglegenomics.simlims.core.Group;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import uk.ac.bbsrc.tgac.miso.core.data.Library;

import java.util.Collection;

/**
 * Jackson Mixin class to filter sensitive user info
 *
 * @author Rob Davey
 * @date 18/08/15
 * @since 0.2.1-SNAPSHOT
 */
public abstract class UserInfoMixin {
  @JsonIgnore()
  abstract String getPassword();

  @JsonIgnore()
  abstract String getLoginName();

  @JsonIgnore()
  abstract Collection<Group> getGroups();

  @JsonIgnore()
  abstract String[] getRoles();

  @JsonIgnore()
  abstract Collection<GrantedAuthority> getRolesAsAuthorities();

  @JsonIgnore()
  abstract Collection<GrantedAuthority> getPermissionsAsAuthorities();

  @JsonIgnore()
  abstract boolean isAdmin();

  @JsonIgnore()
  abstract boolean isInternal();

  @JsonIgnore()
  abstract boolean isExternal();
}