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
  Time: 15:09:13

--%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent" class="${not empty run.health ? 'run.health.key' : ''}">

<div id="contentcolumn">
<form:form id="run-form" data-parsley-validate="" action="/miso/run" method="POST" modelAttribute="run" autocomplete="off">

<sessionConversation:insertSessionConversationId attributeName="run"/>

<h1>
  <c:choose>
    <c:when test="${run.id != 0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> Run
  <button type="button" id="save" onclick="return Run.validateRun();" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${run.id != 0}">
    <c:forEach items="<%=uk.ac.bbsrc.tgac.miso.core.util.SampleSheet.values()%>" var="sheet">
      <c:if test="${sheet.allowedFor(run)}">
        <a href="<c:url value='/miso/rest/run/${run.id}/samplesheet/${sheet.name()}'/>" class="ui-button ui-state-default">Download Sample Sheet (${sheet.alias()})</a>
      </c:if>
    </c:forEach>
    <span></span>
  </c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Run contains the sequencing results from sequencing Experiments.
  Each run partition (lane/chamber) holds a Pool which is linked to a number of Experiments to facilitate multiplexing
  if required.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid!</p>
</div>

<h2>Run Information</h2>
<table class="in">
  <tr>
    <td class="h">Run ID:</td>
    <td><span id="runId">
      <c:choose>
        <c:when test="${run.id != 0}">
          <input type='hidden' id='runId' name='runId' value='${run.id}'/>${run.id}
        </c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </span></td>
  </tr>
  <tr>
    <td class="h">Name:</td>
    <td><span id="name">
      <c:choose>
      <c:when test="${run.id != 0}">${run.name}</c:when>
      <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </span></td>
  </tr>
  <tr>
    <td class="h">Alias:*</td>
    <td><form:input id="alias" path="alias" class="validateable"/><span id="aliascounter" class="counter"></span>
    </td>
  </tr>
  <c:if test="${not empty run.accession}">
    <tr>
      <td class="h">Accession:</td>
      <td><a href="http://www.ebi.ac.uk/ena/data/view/${run.accession}" target="_blank">${run.accession}</a>
      </td>
    </tr>
  </c:if>
  <tr>
    <td>Platform:</td>
    <td><span id="platform">${run.sequencer.instrumentModel.platformType.key}</span></td>
  </tr>
  <tr>
    <td>Sequencer:</td>
    <td><a id="sequencer" href='<c:url value="/miso/instrument/${run.sequencer.id}"/>'>${run.sequencer.name} - ${run.sequencer.instrumentModel.alias}</a></td>
  </tr>
  <tr>
    <td></td>
    <td>
      <div class="parsley-errors-list filled" id="sequencerError">
        <div class="parsley-required"></div>
      </div>
    </td>
  </tr>
  <tr>
    <td>Sequencing Parameters:*</td>
    <td><miso:select id="sequencingParameters" path="sequencingParameters" items="${sequencingParameters}" itemLabel="name" itemValue="id" defaultLabel="SELECT" defaultValue="" /></td>
  </tr>
  <tr>
    <td></td>
    <td>
      <div class="parsley-errors-list filled" id="sequencingParametersError">
        <div class="parsley-required"></div>
      </div>
    </td>
  </tr>

  <tr>
    <td>Description:</td>
    <td>
      <c:choose>
        <c:when test="${run.health.key ne 'Unknown'}">
           <form:input id="description" path="description" disabled="disabled" class="validateable"/>
        </c:when>

        <c:otherwise><form:input id="description" path="description" class="validateable"/></c:otherwise>
      </c:choose>
      <span id="descriptioncounter" class="counter"></span>
    </td>
  </tr>
  <tr>
    <td>Run Path:*</td>
    <td>
      <c:choose>
        <c:when test="${empty run.filePath or miso:isAdmin()}"><form:input id="filePath" path="filePath"/></c:when>
        <c:otherwise><span id="filePath">${run.filePath}</span></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <c:if test="${miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun')}">
    <tr>
      <td>Number of Cycles:</td>
      <td><form:input id="numCycles" path="numCycles" class="validateable"/></td>
    </tr>
    <tr>
      <td>Called Cycles:</td>
      <td><form:input id="callCycle" path="callCycle" class="validateable"/></td>
    </tr>
    <tr>
      <td>Imaged Cycles:</td>
      <td><form:input id="imgCycle" path="imgCycle" class="validateable"/></td>
    </tr>
    <tr>
      <td>Scored Cycles:</td>
      <td><form:input id="scoreCycle" path="scoreCycle" class="validateable"/></td>
    </tr>
  </c:if>
  <c:if test="${miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.LS454Run')}">
    <tr>
      <td>Cycles:</td>
      <td><form:input id="cycles" path="cycles" class="validateable"/></td>
    </tr>

  </c:if>
  <c:if test="${miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun') or miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.SolidRun') or miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.LS454Run')}">
  <tr>
    <td><label for="pairedEnd">Paired End:</label></td>
    <td>
      <c:choose>
         <c:when test="${run.health.key ne 'Unknown'}"><form:checkbox
           value="${run.pairedEnd}" path="pairedEnd" id="pairedEnd" disabled="disabled"/></c:when>
        <c:otherwise><form:checkbox value="${run.pairedEnd}" path="pairedEnd" id="pairedEnd"/></c:otherwise>
      </c:choose>
    </td>
  </tr>
  </c:if>
  
  <c:if test="${miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.OxfordNanoporeRun')}">
  	<tr>
      <td>MinKNOW Version:</td>
      <td><form:input id="minKnowVersion" path="minKnowVersion" class="validateable"/></td>
    </tr>
    <tr>
      <td>Protocol Version:</td>
      <td><form:input id="protocolVersion" path="protocolVersion" class="validateable"/></td>
    </tr>
  </c:if>

  <tr>
    <td valign="top">Status:</td>
    <td>
      <form:radiobuttons id="health" path="health" items="${healthTypes}"
                         onchange="Run.checkForCompletionDate(true);"/><br/>
      <table class="list" id="runStatusTable">
        <thead>
        <tr>
          <th>Start Date</th>
          <th>Completion Date</th>
          <th>Last Updated</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td><form:input path="startDate" id="startDate" /></td>
          <td><form:input path="completionDate" id="completionDate" /></td>
          <td>
            <fmt:formatDate value="${run.lastModified}" dateStyle="long" pattern="yyyy-MM-dd HH:mm:ss"/>
          </td>
        </tr>
        </tbody>
      </table>
      <script type="text/javascript">
      Utils.ui.addDatePicker("startDate");
      Utils.ui.addDatePicker("completionDate");
      Run.userIsAdmin = ${miso:isAdmin()};
      var startDate = document.getElementById("startDate");
      startDate.disabled = startDate.value && !Run.userIsAdmin;
      Run.checkForCompletionDate(false);
      </script>
    </td>
  </tr>
  <c:if test="${run.id != 0 && !runReportLinks.isEmpty()}">
  <tr>
    <td>External Links:</td>
    <td>
    <c:forEach items="${runReportLinks}" var="runReportLink">
      <span><a href="<c:out value="${runReportLink.value}"/>">${runReportLink.key}</a></span><br/>
    </c:forEach>
    </td>
  </tr>
  </c:if>
</table>

<script type="text/javascript">
	jQuery(document).ready(function () {
	  // Attaches a Parsley form validator.
	  Validate.attachParsley('#run-form');
	});
</script>

<c:if test="${run.id != 0}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
    <div id="notes_arrowclick" class="toggleLeftDown"></div>
  </div>
  <div id="notes">
    <h1>Notes</h1>
    <ul class="sddm">
      <li>
        <a onmouseover="mopen('notesmenu')" onmouseout="mclosetime()">Options
          <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
        </a>

        <div id="notesmenu"
             onmouseover="mcancelclosetime()"
             onmouseout="mclosetime()">
          <a onclick="Run.ui.showRunNoteDialog(${run.id});" href="javascript:void(0);" class="add">Add
            Note</a>
        </div>
      </li>
    </ul>
    <c:if test="${fn:length(run.notes) > 0}">
      <div id="notelist" class="note" style="clear:both">
        <c:forEach items="${run.notes}" var="note" varStatus="n">
          <div class="exppreview" id="run-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
          <span class="float-right"
                style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
            <c:if test="${miso:isCurrentUser(note.owner.loginName) or miso:isAdmin()}">
              <span style="color:#000000">
                <a href='#' onclick="Run.ui.deleteRunNote('${run.id}', '${note.noteId}');">
                  <span class="ui-icon ui-icon-trash note-delete-icon"></span>
                </a>
              </span>
            </c:if>
          </span>
          </div>
        </c:forEach>
      </div>
    </c:if>
    <div id="addRunNoteDialog" title="Create new Note"></div>
  </div>
  <br/>
  
  <miso:attachments item="${run}"/>
  <div id="issues">
    <miso:list-section id="list_issue" name="Related Issues" target="issue" alwaysShow="true" items="${runIssues}" config="{}"/>
  </div>
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#metrix_arrowclick'), 'metrix');">Metrics
    <div id="metrix_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="metrix">
    <h1>Metrics</h1>
    <div id="metricsdiv"></div>
  </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      RunGraph.renderMetrics(${metrics}, ${partitionNames});
    });
  </script>
  <div id="containers">
    <miso:list-section id="list_container" name="${run.platformType.containerName}" target="run_position" items="${runPositions}" alwaysShow="true" config="${partitionConfig}"/>
  </div>
  <div id="partitions">
    <miso:list-section id="list_partition" name="${run.platformType.pluralPartitionName}" target="partition" items="${runPartitions}" config="${partitionConfig}"/>
  </div>
  <div id="experiments">
    <miso:list-section id="list_experiment" name="Experiments" target="experiment" alwaysShow="true" items="${experiments}" config="${experimentConfiguration}"/>
  </div>
</c:if>
<%@ include file="permissions.jsp" %>
</form:form>
<miso:changelog item="${run}"/>
</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
