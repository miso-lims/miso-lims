package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.security.MisoAuthority;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Concrete implementation of a User object, inheriting from the simlims core User
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "User")
public class UserImpl implements User {

  private static final long serialVersionUID = 1L;

  /**
   * Use this ID to indicate that a user has not yet been saved, and therefore does not yet have a
   * unique ID.
   */
  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long userId = UserImpl.UNSAVED_ID;
  private String fullName;
  private String loginName;
  private String email;
  private String password;
  private boolean internal = false;
  private boolean admin = false;
  private boolean active = true;

  @ElementCollection
  @CollectionTable(name = "User_FavouriteWorkflows",
      joinColumns = @JoinColumn(name = "userId", referencedColumnName = "userId"))
  @Column(name = "favouriteWorkflow")
  @Enumerated(EnumType.STRING)
  private Set<WorkflowName> favouriteWorkflows = new HashSet<>();

  @ManyToMany(targetEntity = Group.class)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(name = "User_Group", inverseJoinColumns = {@JoinColumn(name = "groups_groupId")}, joinColumns = {
      @JoinColumn(name = "users_userId")})
  private Set<Group> groups = new HashSet<>();

  @Lob
  private String roles = new String();

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public long getId() {
    return userId;
  }

  @Override
  public void setId(long userId) {
    this.userId = userId;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getFullName() {
    return fullName;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Set<Group> getGroups() {
    return groups;
  }

  @Override
  public String getLoginName() {
    return loginName;
  }

  @Override
  public String[] getRoles() {
    return roles == null ? new String[0] : roles.split(",");
  }

  @Override
  public Collection<GrantedAuthority> getRolesAsAuthorities() {
    List<GrantedAuthority> auths = new ArrayList<>();
    for (String s : getRoles()) {
      auths.add(new SimpleGrantedAuthority(s));
    }
    return auths;
  }

  @Override
  public Collection<GrantedAuthority> getPermissionsAsAuthorities() {
    List<GrantedAuthority> auths = new ArrayList<>();
    if (isAdmin()) {
      auths.add(MisoAuthority.ROLE_ADMIN);
    }

    if (isInternal()) {
      auths.add(MisoAuthority.ROLE_INTERNAL);
    }

    return auths;
  }

  @Override
  public boolean isAdmin() {
    return admin;
  }

  @Override
  public boolean isInternal() {
    return internal;
  }

  @Override
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  @Override
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public void setGroups(Set<Group> groups) {
    this.groups = groups;
  }

  @Override
  public void setInternal(boolean internal) {
    this.internal = internal;
  }

  @Override
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  @Override
  public void setRoles(String[] roles) {
    StringBuilder roleString = new StringBuilder();
    for (String role : roles) {
      if (!LimsUtils.isStringEmptyOrNull(role))
        roleString.append(role);
    }
    this.roles = roleString.toString();
  }

  @Override
  public Set<WorkflowName> getFavouriteWorkflows() {
    return favouriteWorkflows;
  }

  @Override
  public void setFavouriteWorkflows(Set<WorkflowName> favouriteWorkflows) {
    this.favouriteWorkflows = favouriteWorkflows;
  }

  /**
   * Users are equated by login name.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof User))
      return false;
    User them = (User) obj;
    if (!isSaved() || !them.isSaved()) {
      return this.getLoginName().equals(them.getLoginName());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (isSaved()) {
      return ((Long) getId()).intValue();
    } else {
      int hashcode = 1;
      if (getLoginName() != null)
        hashcode = 37 * hashcode + getLoginName().hashCode();
      if (getEmail() != null)
        hashcode = 37 * hashcode + getEmail().hashCode();
      return hashcode;
    }
  }

  /**
   * Equivalent to getLoginName().
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getLoginName());
    sb.append(":").append(getFullName());
    sb.append(":").append(getEmail());
    sb.append(":").append(isActive());
    sb.append(":").append(isAdmin());
    sb.append(":").append(isInternal());
    sb.append("[").append(String.join(",", getRoles())).append("]");
    return sb.toString();
  }

  @Override
  public int compareTo(User t) {
    if (getId() < t.getId())
      return -1;
    if (getId() > t.getId())
      return 1;
    return 0;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "User";
  }

  @Override
  public String getDeleteDescription() {
    return getFullName();
  }

  @Override
  public boolean isRunReviewer() {
    return getGroups().stream().anyMatch(group -> Group.RUN_REVIEWERS.equals(group.getName()));
  }

}
