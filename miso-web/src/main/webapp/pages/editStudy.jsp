<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>

<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 15-Feb-2010
  Time: 15:08:42

--%>
<%@ include file="../header.jsp" %>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.popup.js'/>"></script>

<div id="maincontent">
  <div id="contentcolumn">
    <form:form action="/miso/study" method="POST" commandName="study" autocomplete="off"
               onsubmit="return validate_study(this);">
      <sessionConversation:insertSessionConversationId attributeName="study"/>
      <h1>
        <c:choose>
          <c:when test="${study.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose> Study
        <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
      </h1>
      <div class="breadcrumbs">
        <ul>
          <li>
            <a href="/">Home</a>
          </li>
          <li>
            <div class="breadcrumbsbubbleInfo">
              <div class="trigger">
                <a href='<c:url value="/miso/project/${study.project.id}"/>'>${study.project.name}</a>
              </div>
              <div class="breadcrumbspopup">
                  ${study.project.alias}
              </div>
            </div>
          </li>
        </ul>
      </div>
      <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
        <div id="note_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notediv" class="note" style="display:none;">A Study contains more fine-grained information about
        the sequencing Project. Studies can contain any number of sequencing Experiments and Analysis.
      </div>
      <h2>Study Information</h2>
      <table class="in">
      <tr>
        <td class="h">Study ID:</td>
        <td>
          <c:choose>
            <c:when test="${study.id != 0}">${study.id}</c:when>
            <c:otherwise><i>Unsaved</i></c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td class="h">Project ID:</td>
        <td>
          <input type="hidden" value="${study.project.id}" name="project" id="project"/>
          <a href='<c:url value="/miso/project/${study.project.id}"/>'>${study.project.name}</a>
        </td>
      </tr>
      <tr>
        <td>Name:</td>
        <td>
          <c:choose>
            <c:when test="${study.id != 0}">${study.name}</c:when>
            <c:otherwise><i>Unsaved</i></c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td class="h">Alias:</td>
        <td><form:input path="alias" class="validateable"/><span id="aliascounter" class="counter"></span></td>
          <%--<td><a href="void(0);" onclick="popup('help/studyAlias.html');">Help</a></td>--%>
      </tr>
      <tr>
        <td>Description:</td>
        <td><form:input path="description" class="validateable"/><span id="descriptioncounter" class="counter"></span>
        </td>
          <%--<td><a href="void(0);" onclick="popup('help/studyDescription.html');">Help</a></td>--%>
      </tr>
      <c:if test="${not empty study.accession}">
        <tr>
          <td class="h">Accession:</td>
          <td><a href="http://www.ebi.ac.uk/ena/data/view/${study.accession}"
                 target="_blank">${study.accession}</a>
          </td>
            <%--<td><a href="void(0);" onclick="popup('help/studyAccession.html');">Help</a></td>--%>
        </tr>
      </c:if>
      <tr>
        <td>Study Type:</td>
        <td><form:select items="${studyTypes}" path="studyType"/></td>
      </tr>
      <c:choose>
        <c:when test="${!empty project and study.securityProfile.profileId eq project.securityProfile.profileId}">
          <tr>
            <td>Permissions</td>
            <td><i>Inherited from project </i><a
                href='<c:url value="/miso/project/${project.id}"/>'>${project.name}</a>
              <input type="hidden" value="${project.securityProfile.profileId}"
                     name="securityProfile" id="securityProfile"/>
            </td>
          </tr>
          </table>
        </c:when>
        <c:otherwise>
          </table>
          <%@ include file="permissions.jsp" %>
        </c:otherwise>
      </c:choose>
      <br/>

      <c:choose>
        <c:when test="${study.id != 0}">
          <h1>
            <div id="totalCount">
            </div>
          </h1>
          <ul class="sddm">
            <li>
              <a onmouseover="mopen('expmenu')" onmouseout="mclosetime()">Options
                <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
              </a>

              <div id="expmenu"
                   onmouseover="mcancelclosetime()"
                   onmouseout="mclosetime()">
                <a href='<c:url value="/miso/experiment/new/${study.id}"/>' class="add">Add new
                  Experiment</a>
              </div>
            </li>
          </ul>
          <span style="clear:both">
            <table class="list" id="table">
              <thead>
              <tr>
                <th>Experiment Name</th>
                <th>Experiment Alias</th>
                <th>Description</th>
                <th>Platform</th>
                <th class="fit">Edit</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${study.experiments}" var="experiment">
                <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                  <td><b>${experiment.name}</b></td>
                  <td>${experiment.alias}</td>
                  <td>${experiment.description}</td>
                  <td>${experiment.platform.platformType.key}
                    - ${experiment.platform.instrumentModel}</td>
                  <td class="misoicon"
                      onclick="window.location.href='<c:url value="/miso/experiment/${experiment.id}"/>'">
                    <span class="ui-icon ui-icon-pencil"/></td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </span>
          <script type="text/javascript">
            jQuery(document).ready(function () {
              jQuery("#table").tablesorter({
                headers: {
                  4: {
                    sorter: false
                  }
                }
              });
            });
            jQuery(document).ready(function () {
              writeTotalNo();
            });
            function writeTotalNo() {
              jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Experiments");
            }
          </script>
        </c:when>
      </c:choose>
    </form:form>
  </div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#alias').simplyCountable({
      counter: '#aliascounter',
      countType: 'characters',
      maxCount: ${maxLengths['alias']},
      countDirection: 'down'
    });

    jQuery('#description').simplyCountable({
      counter: '#descriptioncounter',
      countType: 'characters',
      maxCount: ${maxLengths['description']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>