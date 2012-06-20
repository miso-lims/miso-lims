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

package uk.ac.bbsrc.tgac.miso.core.util.jira;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * uk.ac.bbsrc.tgac.miso.core.util.jira
 * <p/>
 * Converts issue tracker JSON into JSON suitable for display in the MISO interface
 *
 * @author Rob Davey
 * @date 25-Jan-2011
 * @since 0.0.3
 */
public class IssueJsonConverter {
  public static JSONObject jiraToMiso(JSONObject json) {
    //convert all REST urls to relevant JIRA web interface URLs
    Pattern issuePattern = Pattern.compile("(http://[A-z0-9\\.]+)/rest/api/2.0.alpha1/issue/([A-Z]+-[\\d]+)");
    //Pattern votePattern = Pattern.compile("(http://[A-z0-9\\.]+)/rest/api/2.0.alpha1/issue/([A-Z]+-[\\d]+)/votes");
    Pattern userPattern = Pattern.compile("(http://[A-z0-9\\.]+)/rest/api/2.0.alpha1/user\\?username\\=([A-z0-9]+)");
    Pattern projectPattern = Pattern.compile("(http://[A-z0-9\\.]+)/rest/api/2.0.alpha1/project/([A-Z]+)");
    Pattern commentPattern = Pattern.compile("(http://[A-z0-9\\.]+)/rest/api/2.0.alpha1/comment/([0-9]+)");

    String issueKey = json.getString("key");

    if (json.getString("self") != null) {
      Matcher m = issuePattern.matcher(json.getString("self"));
      if (m.matches()) {
        json.put("url", m.group(1)+"/browse/"+m.group(2));
      }
    }

    JSONObject fields = json.getJSONObject("fields");
    if (fields != null) {
      /*
      JSONObject voteValue = fields.getJSONObject("votes").getJSONObject("value");
      Matcher m = votePattern.matcher(voteValue.getString("self"));
      if (m.matches()) {
        json.put("url", m.group(1)+"/browse/"+m.group(2));
      }
      */

      JSONObject assigneeValue = fields.getJSONObject("assignee").getJSONObject("value");
      Matcher m = userPattern.matcher(assigneeValue.getString("self"));
      if (m.matches()) {
        assigneeValue.put("url", m.group(1)+"/secure/ViewProfile.jspa?name="+m.group(2));
      }

      JSONObject reporterValue = fields.getJSONObject("reporter").getJSONObject("value");
      m = userPattern.matcher(reporterValue.getString("self"));
      if (m.matches()) {
        reporterValue.put("url", m.group(1)+"/secure/ViewProfile.jspa?name="+m.group(2));
      }

      JSONObject projectValue = fields.getJSONObject("project").getJSONObject("value");
      m = projectPattern.matcher(projectValue.getString("self"));
      if (m.matches()) {
        projectValue.put("url", m.group(1)+"/browse/"+m.group(2));
      }

      JSONArray links = fields.getJSONObject("links").getJSONArray("value");
      for (JSONObject link : (Iterable<JSONObject>)links) {
        m = issuePattern.matcher(link.getString("issue"));
        if (m.matches()) {
          link.put("url", m.group(1)+"/browse/"+m.group(2));
        }
      }

      JSONArray subtasks = fields.getJSONObject("sub-tasks").getJSONArray("value");
      for (JSONObject subtask : (Iterable<JSONObject>)subtasks) {
        m = issuePattern.matcher(subtask.getString("issue"));
        if (m.matches()) {
          subtask.put("url", m.group(1)+"/browse/"+m.group(2));
        }
      }

      JSONArray comments = fields.getJSONObject("comment").getJSONArray("value");
      for (JSONObject comment : (Iterable<JSONObject>)comments) {
        m = commentPattern.matcher(comment.getString("self"));
        if (m.matches()) {
          comment.put("url", m.group(1)+"/browse/"+issueKey+"?focusedCommentId="+m.group(2)+"&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-"+m.group(2));
        }

        JSONObject authorValue = comment.getJSONObject("author");
        m = commentPattern.matcher(authorValue.getString("self"));
        if (m.matches()) {
         authorValue.put("url", m.group(1)+"/secure/ViewProfile.jspa?name="+m.group(2));
        }
      }
    }

    return json;
  }
}
