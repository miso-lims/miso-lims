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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.InetOrgPerson;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Xingdong Bian
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class DashboardHelperService {
  protected static final Logger log = LoggerFactory.getLogger(DashboardHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  public JSONObject checkUser(HttpSession session, JSONObject json) {
    String username = json.getString("username");
    if (isStringEmptyOrNull(username)) {
      if (SecurityContextHolder.getContext().getAuthentication().getName().equals(username)) {
        try {
          User user = securityManager.getUserByLoginName(username);
          if (user == null) {
            // user is authed, but doesn't exist in the LIMS DB. Save that user!
            User u = new UserImpl();
            Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (o instanceof UserDetails) {
              UserDetails details = (UserDetails) o;
              u.setLoginName(details.getUsername());
              u.setFullName(details.getUsername());
              u.setPassword(details.getPassword());
              u.setActive(true);

              if (details.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_ADMIN"))) {
                u.setAdmin(true);
              }

              if (details.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_INTERNAL"))) {
                u.setInternal(true);
                u.setRoles(new String[] { "ROLE_INTERNAL" });
              } else if (details.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_EXTERNAL"))) {
                u.setExternal(true);
                u.setRoles(new String[] { "ROLE_EXTERNAL" });
              } else {
                log.warn("Unrecognised roles");
              }

              if (details instanceof InetOrgPerson) {
                u.setFullName(((InetOrgPerson) details).getDisplayName());
                u.setEmail(((InetOrgPerson) details).getMail());
              }

              securityManager.saveUser(u);
            } else {
              return JSONUtils.SimpleJSONError(
                  "The UserDetailsService specified in the Spring config cannot support mapping of usernames to UserDetails objects");
            }
          } else {
            // the user isn't null, but LDAP is "newer" than the LIMS, i.e. LDAP pass changed but LIMS still the same
            Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (o instanceof UserDetails) {
              UserDetails details = (UserDetails) o;
              if (!user.getPassword().equals(details.getPassword())) {
                user.setPassword(details.getPassword());
                securityManager.saveUser(user);
              }
            }
          }
          return null;
        } catch (IOException e) {
          log.error("check user", e);
          return JSONUtils.SimpleJSONError("Something went wrong trying to get user information from the database: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("Cannot check LIMS user database table if you are not authenticated as that user!");
      }
    }
    return JSONUtils.SimpleJSONError("Please supply a valid username to check");
  }

  public JSONObject searchProject(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Project> projects;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        projects = new ArrayList<>(requestManager.listAllProjectsBySearch(searchStr));
      } else {
        projects = new ArrayList<>(requestManager.listAllProjectsWithLimit(50));
      }

      if (projects.size() > 0) {
        Collections.sort(projects);
        Collections.reverse(projects);
        for (Project p : projects) {
          b.append("<a class=\"dashboardresult\" href=\"/miso/project/" + p.getProjectId()
              + "\"><div onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          b.append("Name: <b>" + p.getName() + "</b><br/>");
          b.append("Alias: <b>" + p.getAlias() + "</b><br/>");
          b.append("</div></a>");
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());

    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchStudy(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Study> studies;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        studies = new ArrayList<Study>(requestManager.listAllStudiesBySearch(searchStr));
      } else {
        studies = new ArrayList<Study>(requestManager.listAllStudiesWithLimit(50));
      }

      if (studies.size() > 0) {
        Collections.sort(studies);
        Collections.reverse(studies);
        for (Study s : studies) {
          b.append("<a class=\"dashboardresult\" href=\"/miso/study/" + s.getId()
              + "\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          b.append("Name: <b>" + s.getName() + "</b><br/>");
          b.append("Alias: <b>" + s.getAlias() + "</b><br/>");
          b.append("</div></a>");
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchExperiment(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Experiment> experiments;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        experiments = new ArrayList<Experiment>(requestManager.listAllExperimentsBySearch(searchStr));
      } else {
        experiments = new ArrayList<Experiment>(requestManager.listAllExperimentsWithLimit(50));
      }

      if (experiments.size() > 0) {
        Collections.sort(experiments);
        Collections.reverse(experiments);
        for (Experiment e : experiments) {
          b.append("<a class=\"dashboardresult\" href=\"/miso/experiment/" + e.getId()
              + "\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          b.append("Name: <b>" + e.getName() + "</b><br/>");
          b.append("Alias: <b>" + e.getAlias() + "</b><br/>");
          b.append("</div></a>");
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchRun(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Run> runs;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        runs = new ArrayList<Run>(requestManager.listAllRunsBySearch(searchStr));
      } else {
        runs = new ArrayList<Run>(requestManager.listAllRunsWithLimit(50));
      }

      if (runs.size() > 0) {
        Collections.sort(runs);
        Collections.reverse(runs);
        for (Run r : runs) {
          b.append("<a class=\"dashboardresult\" href=\"/miso/run/" + r.getId()
              + "\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          b.append("Name: <b>" + r.getName() + "</b><br/>");
          b.append("Alias: <b>" + r.getAlias() + "</b><br/>");
          b.append("</div></a>");
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchLibraryDilution(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<LibraryDilution> libraryDilutions;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        libraryDilutions = new ArrayList<LibraryDilution>(requestManager.listAllLibraryDilutionsBySearchOnly(searchStr));
      } else {
        libraryDilutions = new ArrayList<LibraryDilution>(requestManager.listAllLibraryDilutionsWithLimit(50));
      }

      if (libraryDilutions.size() > 0) {
        Collections.sort(libraryDilutions);
        Collections.reverse(libraryDilutions);
        for (LibraryDilution ld : libraryDilutions) {
          if (ld != null) {
            if (ld.getLibrary() != null) {
              b.append("<a class=\"dashboardresult\" href=\"/miso/library/" + ld.getLibrary().getId()
                  + "\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
              b.append("Name: <b>" + ld.getName() + "</b><br/>");
              b.append("From Library: <b>" + ld.getLibrary().getAlias() + "(" + ld.getLibrary().getName() + ")</b><br/>");
              b.append("</div></a>");
            }
          }
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchLibrary(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Library> libraries;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        if (LimsUtils.isBase64String(searchStr)) {
          // Base64-encoded string, most likely a barcode image beeped in. decode and search
          searchStr = new String(Base64.decodeBase64(searchStr));
        }
        libraries = new ArrayList<Library>(requestManager.listAllLibrariesBySearch(searchStr));
      } else {
        libraries = new ArrayList<Library>(requestManager.listAllLibrariesWithLimit(50));
      }

      if (libraries.size() > 0) {
        Collections.sort(libraries);
        Collections.reverse(libraries);
        for (Library l : libraries) {
          b.append("<a class=\"dashboardresult\" href=\"/miso/library/" + l.getId()
              + "\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          b.append("Name: <b>" + l.getName() + "</b><br/>");
          b.append("Alias: <b>" + l.getAlias() + "</b><br/>");
          b.append("</div></a>");
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchSample(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Sample> samples;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        if (LimsUtils.isBase64String(searchStr)) {
          // Base64-encoded string, most likely a barcode image beeped in. decode and search
          searchStr = new String(Base64.decodeBase64(searchStr));
        }
        samples = new ArrayList<>(requestManager.listAllSamplesBySearch(searchStr));
      } else {
        samples = new ArrayList<>(requestManager.listAllSamplesWithLimit(50));
      }

      if (samples.size() > 0) {
        Collections.sort(samples);
        Collections.reverse(samples);
        for (Sample s : samples) {
          b.append("<a class=\"dashboardresult\" href=\"/miso/sample/" + s.getId()
              + "\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          b.append("Name: <b>" + s.getName() + "</b><br/>");
          b.append("Alias: <b>" + s.getAlias() + "</b><br/>");
          b.append("</div></a>");
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject checkAlerts(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!requestManager.listUnreadAlertsByUserId(user.getUserId()).isEmpty()) {
        response.put("newAlerts", true);
      }
    } catch (IOException e) {
      log.error("check alerts", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
    return response;
  }

  public JSONObject getAlerts(HttpSession session, JSONObject json) {
    StringBuilder b = new StringBuilder();

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      List<Alert> alerts;
      if (json.has("showReadAlerts") && json.getBoolean("showReadAlerts")) {
        alerts = new ArrayList<>(requestManager.listAlertsByUserId(user.getUserId()));
      } else {
        alerts = new ArrayList<>(requestManager.listUnreadAlertsByUserId(user.getUserId()));
      }
      Collections.sort(alerts);
      for (Alert a : alerts) {
        if (a.getAlertLevel().equals(AlertLevel.CRITICAL) || a.getAlertLevel().equals(AlertLevel.HIGH)) {
          b.append("<div alertId='" + a.getAlertId() + "' class=\"dashboard error\">");
        } else {
          b.append("<div alertId='" + a.getAlertId() + "' class=\"dashboard\">");
        }

        b.append(a.getAlertDate() + " <b>" + a.getAlertTitle() + "</b><br/>");
        b.append(a.getAlertText() + "<br/>");
        if (!a.getAlertRead()) {
          b.append("<span onclick='Utils.alert.confirmAlertRead(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
        }
        b.append("</div>");
      }
    } catch (IOException e) {
      log.error("get alerts", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }

    return JSONUtils.JSONObjectResponse("html", b.toString());
  }

  public JSONObject getSystemAlerts(HttpSession session, JSONObject json) {
    StringBuilder b = new StringBuilder();

    long limit = 20;
    if (json.has("limit")) limit = json.getLong("limit");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (user.isAdmin()) {
        List<Alert> alerts = new ArrayList<Alert>(requestManager.listAlertsByUserId(0L, limit));
        Collections.sort(alerts);
        Collections.reverse(alerts);
        for (Alert a : alerts) {
          if (a.getAlertLevel().equals(AlertLevel.CRITICAL) || a.getAlertLevel().equals(AlertLevel.HIGH)) {
            b.append("<div alertId='" + a.getAlertId() + "' class=\"dashboard error\">");
          } else {
            b.append("<div alertId='" + a.getAlertId() + "' class=\"dashboard\">");
          }

          b.append(a.getAlertDate() + " <b>" + a.getAlertTitle() + "</b><br/>");
          b.append(a.getAlertText() + "<br/>");
          b.append("</div>");
        }
      } else {
        return JSONUtils.SimpleJSONError("Failed: You do not have access to view system level alerts");
      }
    } catch (IOException e) {
      log.error("get system alerts", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }

    return JSONUtils.JSONObjectResponse("html", b.toString());
  }

  public JSONObject setAlertAsRead(HttpSession session, JSONObject json) {
    Long alertId = json.getLong("alertId");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Alert a = requestManager.getAlertById(alertId);
      if (a.getAlertUser().equals(user)) {
        a.setAlertRead(true);
        requestManager.saveAlert(a);
      } else {
        return JSONUtils.SimpleJSONError("You do not have the rights to set this alert as read");
      }
    } catch (IOException e) {
      log.error("set alert as read", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject setAllAlertsAsRead(HttpSession session, JSONObject json) {

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      List<Alert> alerts = new ArrayList<Alert>(requestManager.listUnreadAlertsByUserId(user.getUserId()));
      for (Alert a : alerts) {
        if (a.getAlertUser().equals(user)) {
          a.setAlertRead(true);
          requestManager.saveAlert(a);
        } else {
          return JSONUtils.SimpleJSONError("You do not have the rights to set this alert as read");
        }
      }
    } catch (IOException e) {
      log.error("set all alerts as read", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject showLatestReceivedSamples(HttpSession session, JSONObject json) {
    try {
      StringBuilder b = new StringBuilder();
      Collection<Sample> samples = requestManager.listAllSamplesByReceivedDate(100);

      if (samples.size() > 0) {
        for (Sample s : samples) {
          if (s.getReceivedDate() != null) {
            b.append("<a class=\"dashboardresult\" href=\"/miso/project/" + s.getProject().getId()
                + "\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
            b.append("Name: <b>" + s.getProject().getName() + "</b><br/>");
            b.append("Alias: <b>" + s.getProject().getAlias() + "</b><br/>");
            b.append("Last Received: <b>" + LimsUtils.getDateAsString(s.getReceivedDate()) + "</b><br/>");
            b.append("</div>");
          }
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
}
