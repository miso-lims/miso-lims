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
  Time: 15:08:52

--%>
<%@ include file="../header.jsp" %>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">
<h1>
  <c:choose><c:when test="${experiment.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Experiment
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
          <a href='<c:url value="/miso/project/${experiment.study.project.id}"/>'>${experiment.study.project.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${experiment.study.project.name}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/study/${experiment.study.id}"/>'>${experiment.study.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${experiment.study.name}
        </div>
      </div>
    </li>
  </ul>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">An experiment contains design information about the
  sequencing experiment. Experiments are associated with Runs which contain the actual sequencing results.
  A Pool is attached to an Experiment which is then assigned to an instrument partition (lane/chamber).
</div>

<form:form id="experimentForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    FormUtils.createForm('experimentForm', 'save', ${experimentDto}, 'experiment', {});
  });
</script>

<miso:list-section id="list_part_lane" alwaysShow="true" name="${experiment.instrumentModel.platformType.pluralPartitionName}" target="experiment_run_partition" items="${runPartitions}" config="{}"/>
<miso:list-section id="list_consumable" alwaysShow="true" name="Consumables" target="kit_consumable" items="${consumables}" config="${consumableConfig}"/>
<miso:changelog item="${experiment}"/>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
