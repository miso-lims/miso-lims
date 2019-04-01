<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey @ TGAC
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
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <form:form id="study-form" data-parsley-validate="" action="/miso/study" method="POST" commandName="study" autocomplete="off">
      <sessionConversation:insertSessionConversationId attributeName="study"/>
      <h1>
        <c:choose>
          <c:when test="${study.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose> Study
        <button id="save" type="submit" class="fg-button ui-state-default ui-corner-all" onclick="return Study.validateStudy();">Save</button>
      </h1>
      <div class="breadcrumbs">
        <ul>
          <li>
            <a href="/">Home</a>
          </li>
          <li>
            <div class="breadcrumbsbubbleInfo">
              <div class="trigger">
                <a href='<c:url value="/miso/project/${study.project.id}"/>'>${study.project.alias}</a>
              </div>
              <div class="breadcrumbspopup">
                  ${study.project.name}
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

      <div class="bs-callout bs-callout-warning hidden">
        <h2>Oh snap!</h2>
        <p>This form seems to be invalid</p>
      </div>

      <h2>Study Information</h2>
      <table class="in">
      <tr>
        <td class="h">Study ID:</td>
        <td><span id="studyId">
          <c:choose>
            <c:when test="${study.id != 0}">${study.id}</c:when>
            <c:otherwise><i>Unsaved</i></c:otherwise>
          </c:choose>
        </span></td>
      </tr>
      <tr>
        <td class="h">Project Name:</td>
        <td>
        <!-- When opening a new study without a known project ID (i.e., from the Studies page), dropdown should display project name when
        in plain sample mode, shortname when in detailed sample mode. ${projects} will come in sorted based on these attributes from
        EditStudyController. -->
        <c:choose>
          <c:when test="${not empty study.project}">
            <input type="hidden" value="${study.project.id}" name="project" id="project"/>
            <a href='<c:url value="/miso/project/${study.project.id}"/>'><span id="projectName">${study.project.name}</span></a>
          </c:when>
          <c:otherwise>
            <miso:select id="project" path="project" items="${projects}" itemLabel="${detailedSample? \"shortName\" : \"name\" }" 
              itemValue="id"/>
          </c:otherwise>
        </c:choose>
        </td>
      </tr>
      <tr>
        <td>Name:</td>
        <td><span id="name">
          <c:choose>
            <c:when test="${study.id != 0}">${study.name}</c:when>
            <c:otherwise><i>Unsaved</i></c:otherwise>
          </c:choose>
        </span></td>
      </tr>
      <tr>
        <td class="h">Alias:</td>
        <td><form:input id="alias" path="alias" class="validateable"/><span id="aliascounter" class="counter"></span></td>
      </tr>
      <tr>
        <td>Description:</td>
        <td><form:input id="description" path="description" class="validateable"/><span id="descriptioncounter" class="counter"></span>
        </td>
      </tr>
      <c:if test="${not empty study.accession}">
        <tr>
          <td class="h">Accession:</td>
          <td><a href="http://www.ebi.ac.uk/ena/data/view/${study.accession}"
                 target="_blank">${study.accession}</a>
          </td>
        </tr>
      </c:if>
      <tr>
        <td>Study Type:</td>
        <td><miso:select id="studyType" path="studyType" items="${studyTypes}" itemLabel="name" itemValue="id" /></td>
      </tr>
      </table>
      <br/>
      
      <script type="text/javascript">
        jQuery(document).ready(function () {
          // Attaches a Parsley form validator. 
          Validate.attachParsley('#study-form');
        });
      </script>
    </form:form>

    <c:if test="${study.id != 0}">
        <miso:list-section id="list_experiments" alwaysShow="true" name="Experiments" target="experiment" items="${experiments}" config="{ studyId : ${study.id} }"/>
    </c:if>
    <miso:changelog item="${study}"/>
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
