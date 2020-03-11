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
    <h1>
      <c:choose><c:when test="${study.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Study
      <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
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
      the sequencing Project. Studies can contain any number of sequencing Experiments and Analyses.
    </div>
    
    <form:form id="studyForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        FormUtils.createForm('studyForm', 'save', ${studyDto}, 'study', {
          detailedSample: Constants.isDetailedSample,
          projects: ${projects}
        });
        Utils.ui.updateHelpLink(FormTarget.study.getUserManualUrl());
      });
    </script>

    <c:if test="${study.id != 0}">
        <miso:list-section id="list_experiments" alwaysShow="true" name="Experiments" target="experiment" items="${experiments}" config="{ studyId : ${study.id} }"/>
    </c:if>
    <miso:changelog item="${study}"/>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
