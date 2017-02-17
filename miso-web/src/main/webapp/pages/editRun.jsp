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

<div id="maincontent" class="${run.health.key}">
<div id="contentcolumn">
<form:form id="run-form" data-parsley-validate="" action="/miso/run" method="POST" modelAttribute="run" autocomplete="off">

<sessionConversation:insertSessionConversationId attributeName="run"/>

<h1>
  <c:choose>
    <c:when test="${run.id != 0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> Run
  <button type="button" onclick="return Run.validateRun();" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

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
<ul class="sddm" style="margin: 0px 8px 0 0;">
  <li>
    <a onmouseover="mopen('runMenu')" onmouseout="mclosetime()">Options
      <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
    </a>

    <div id="runMenu"
         onmouseover="mcancelclosetime()"
         onmouseout="mclosetime()">
      <c:choose>
        <c:when test="${not empty runMap[run.id]}">
          <a href='javascript:void(0);' onclick="Run.alert.unwatchRun(${run.id});">Stop watching</a>
        </c:when>
        <c:otherwise>
          <a href='javascript:void(0);' onclick="Run.alert.watchRun(${run.id});">Watch</a>
        </c:otherwise>
      </c:choose>
    </div>
  </li>
</ul>
<table class="in">
  <tr>
    <td class="h">Run ID:</td>
    <td>
      <c:choose>
        <c:when test="${run.id != 0}">
          <input type='hidden' id='runId' name='runId' value='${run.id}'/>${run.id}
        </c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
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
    <td>${platformType.key}</td>
  </tr>
  <tr>
    <td>Sequencer:</td>
    <td>
      <c:choose>
        <c:when test="${run.id == 0}">
          <miso:select id="sequencerReference" path="sequencerReference" items="${sequencerReferences}" itemLabel="name" itemValue="id" defaultLabel="SELECT" defaultValue=""/>
        </c:when>
        <c:otherwise>${run.sequencerReference.name} - ${run.sequencerReference.platform.instrumentModel}</c:otherwise>
      </c:choose>
    </td>
  </tr>
  <c:if test="${run.id != 0}">
    <tr>
      <td>Sequencing Parameters:</td>
      <td><miso:select id="sequencingParameters" path="sequencingParameters" items="${sequencingParameters}" itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="" /></td>
    </tr>
  </c:if>
  <tr>
    <td></td>
    <td>
      <div class="parsley-errors-list filled" id="platformError">
        <div class="parsley-required"></div>
      </div>
    </td>
  </tr>
  <tr>
    <td></td>
    <td>
      <div class="parsley-errors-list filled" id="sequencerReferenceError">
        <div class="parsley-required"></div>
      </div>
    </td>
  </tr>

  <tr>
    <td>Name:</td>
    <td>
      <c:choose>
        <c:when test="${run.id != 0}">${run.name}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Alias:*</td>
    <td><form:input path="alias" class="validateable"/><span id="aliascounter" class="counter"></span>
    </td>
  </tr>
  <tr>
    <td>Description:</td>
    <td>
      <c:choose>
        <c:when test="${run.health.key ne 'Unknown'}">
           <form:input path="description" disabled="disabled" class="validateable"/>
        </c:when>
        <c:otherwise><form:input path="description" class="validateable"/></c:otherwise>
      </c:choose>
      <span id="descriptioncounter" class="counter"></span>
    </td>
  </tr>
  <tr>
    <td>Run Path:*</td>
    <td>
      <c:choose>
        <c:when test="${not empty run.filePath}">${run.filePath}</c:when>
        <c:otherwise><form:input path="filePath"/></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <c:if test="${miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.PacBioRun')}">
    <c:if test="${pacBioDashboardUrl != null}">
      <tr>
        <td>PacBio Dashboard:</td>
        <td><span id="pbDashLink"></span></td>
        <script type="text/javascript">
          jQuery(document).ready(function() {
            Run.makePacBioUrl('${pacBioDashboardUrl}', '${run.alias}', '${run.startDate}', '${run.sequencerReference.name}');
          });
        </script>
      </tr>
    </c:if>
    <tr>
      <td>Movie Duration:</td>
      <td><form:input path="movieDuration" class="validateable"/></td>
    </tr>
    <tr>
      <td>Well:</td>
      <td><form:input path="wellName" class="validateable"/></td>
    </tr>
    <tr>
      <td>Creation Date:</td>
      <td>${run.creationDate}</td>
    </tr>
  </c:if>
  <c:if test="${miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun')}">
    <tr>
      <td>Number of Cycles:</td>
      <td><form:input path="numCycles" class="validateable"/></td>
    </tr>
    <tr>
      <td>Called Cycles:</td>
      <td><form:input path="callCycle" class="validateable"/></td>
    </tr>
    <tr>
      <td>Imaged Cycles:</td>
      <td><form:input path="imgCycle" class="validateable"/></td>
    </tr>
    <tr>
      <td>Scored Cycles:</td>
      <td><form:input path="scoreCycle" class="validateable"/></td>
    </tr>
  </c:if>
  <c:if test="${miso:instanceOf(run, 'uk.ac.bbsrc.tgac.miso.core.data.LS454Run')}">
    <tr>
      <td>Cycles:</td>
      <td><form:input path="cycles" class="validateable"/></td>
    </tr>
  </c:if>
  <tr>
    <td>Paired End:</td>
    <td>
      <c:choose>
        <c:when test="${run.health.key ne 'Unknown'}"><form:checkbox
            value="${run.pairedEnd}" path="pairedEnd" disabled="disabled"/></c:when>
        <c:otherwise><form:checkbox value="${run.pairedEnd}" path="pairedEnd"/></c:otherwise>
      </c:choose>
    </td>
  </tr>

  <tr>
    <td valign="top">Status:</td>
    <td>
      <form:radiobuttons id="health" path="health" items="${healthTypes}"
                         onchange="Run.checkForCompletionDate();"/><br/>
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
          <td><fmt:formatDate pattern="dd/MM/yyyy" value="${run.startDate}"/></td>
          <c:choose>
            <c:when test="${(run.health.isDone() and empty run.completionDate)}">
              <td><form:input path="completionDate"/></td>
              <script type="text/javascript">
                Utils.ui.addDatePicker("completionDate");
              </script>
            </c:when>
            <c:otherwise>
              <td id="completionDate">
                <fmt:formatDate pattern="dd/MM/yyyy" value="${run.completionDate}"/>
              </td>
            </c:otherwise>
          </c:choose>
          <td>
            <fmt:formatDate value="${run.lastUpdated}" dateStyle="long" pattern="dd/MM/yyyy HH:mm:ss"/>
          </td>
        </tr>
        </tbody>
      </table>
    </td>
  </tr>

</table>

<script type="text/javascript">
	jQuery(document).ready(function () {
	  // Attaches a Parsley form validator.
	  Validate.attachParsley('#run-form');
	});
</script>

<%@ include file="permissions.jsp" %>
<c:if test="${run.id != 0}">
  <c:if test="${statsAvailable}">
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#stats_arrowclick'), 'stats');">Statistics
      <div id="stats_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="stats">
      <h1>Statistics</h1>

      <div id="summarydiv"></div>
    </div>
  </c:if>

  <c:if test="${not empty run.metrics}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#metrix_arrowclick'), 'metrix');">InterOp Metrics
    <div id="metrix_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="metrix">
    <h1>InterOp Metrics</h1>

    <div id="metrixdiv"></div>
  </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      Stats.getInterOpMetrics('${run.alias}', '${run.metrics}');
    });
  </script>
  </c:if>

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
            <c:if test="${(note.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                            or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              <span style="color:#000000">
                <a href='#' onclick="Run.ui.deleteRunNote('${run.id}', '${note.noteId}');">
                  <span class="ui-icon ui-icon-trash" style="clear: both; position: relative; float: right; margin-top: -15px;"></span>
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
</c:if>

<c:if test="${run.health.key eq 'Completed'}">
  <h1>Run QC</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('qcmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="qcmenu"
           onmouseover="mcancelclosetime()"
           onmouseout="mclosetime()">
        <a href='javascript:void(0);' class="add"
           onclick="Run.qc.generateRunQCRow(${run.id}); return false;">Add Run QC</a>
        <c:if test="${operationsQcPassed}">
          <a href='<c:url value="/miso/analysis/new/run/${run.id}"/>' class="add">Initiate Analysis</a>
        </c:if>
      </div>
    </li>
  </ul>
<span style="clear:both">
  <div id="addRunQC"></div>
  <table class="list in" id="runQcTable">
    <thead>
    <tr>
      <th>QCed By</th>
      <th>QC Date</th>
      <th>Method</th>
      <th>Process Selection</th>
      <th>Info</th>
      <th>Do Not Process</th>
    </tr>
    </thead>
    <tbody>
    <c:if test="${not empty run.runQCs}">
      <c:forEach items="${run.runQCs}" var="qc">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${qc.qcCreator}</td>
          <td><fmt:formatDate value="${qc.qcDate}"/></td>
          <td>${qc.qcType.name}</td>
          <td>
            <c:forEach items="${run.sequencerPartitionContainers}" var="container" varStatus="fCount">
              <table class="containerSummary">
                <tr>
                  <c:forEach items="${container.partitions}" var="partition">
                    <c:if test="${not empty qc.partitionSelections and fn:length(qc.partitionSelections) > 0}">
                      <c:forEach items="${qc.partitionSelections}" var="selection">
                        <c:if test="${selection.partitionNumber eq partition.partitionNumber}">
                          <td id="${qc.id}_${run.id}_${container.id}_${partition.partitionNumber}"
                              class="smallbox partitionOccupied">${partition.partitionNumber}</td>
                        </c:if>
                      </c:forEach>
                    </c:if>
                  </c:forEach>
                </tr>
              </table>
              <c:if test="${fn:length(run.sequencerPartitionContainers) > 1
                    and fCount < fn:length(run.sequencerPartitionContainers)}">
                <br/>
              </c:if>
            </c:forEach>
          </td>
          <td>${qc.information}</td>
          <td>${qc.doNotProcess}</td>
        </tr>
      </c:forEach>
    </c:if>
    </tbody>
  </table>
</span>
</c:if>

<div id="runinfo">
<h1>Containers</h1>
<table width="100%">
<tbody>
<tr>
<td width="50%" valign="top">

<div id="runPartitions">
<c:choose>
<c:when test="${empty run.sequencerPartitionContainers}">
  <c:if test="${not empty run.sequencerReference}">
    Container:
     <c:forEach var="platformContainerCount" begin="1"
                end="${run.sequencerReference.platform.numContainers}" step="1"
                varStatus="platformContainer">
       <input id='container${platformContainerCount}select' name='containerselect'
              onchange="Run.container.changeContainer(this.value, '${platformType.key}', ${run.sequencerReference.id});"
              type='radio'
              value='${platformContainerCount}'/>${platformContainerCount}
     </c:forEach>
   </c:if>
  <br/>

  <div id='containerdiv' class="note ui-corner-all"></div>
</c:when>
<c:otherwise>
  <c:forEach items="${run.sequencerPartitionContainers}" var="container" varStatus="containerCount">
    <div class="note ui-corner-all">
      <h2>${platformType.containerName} ${containerCount.count}</h2>
      <c:if test="${not empty container.identificationBarcode}">
        <ul class="sddm">
          <li>
            <a onmouseover="mopen('containermenu')" onmouseout="mclosetime()">Options
              <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
            </a>

            <div class="run" id="containermenu"
                 onmouseover="mcancelclosetime()"
                 onmouseout="mclosetime()">
              <c:if test="${platformType.key eq 'Illumina'}">
                <a href="javascript:void(0);"
                   onclick="Run.container.generateCasava17DemultiplexCSV(${run.id}, ${container.id});">Demultiplex
                  CSV (pre-1.8)</a>
                <a href="javascript:void(0);"
                   onclick="Run.container.generateCasava18DemultiplexCSV(${run.id}, ${container.id});">Demultiplex
                  CSV (1.8+)</a>
              </c:if>
            </div>
          </li>
        </ul>
      </c:if>
      <div style="clear:both"></div>
      <table class="in">
        <tr>
          <c:choose>
            <c:when test="${empty container.identificationBarcode}">
              <td>Serial Number:</td>
              <td>
                <button id="pencil" onclick='Run.container.lookupContainer(this, ${containerCount.index});'
                        type='button' class='right-button ui-state-default ui-corner-all'>
                  Lookup
                </button>
                <div style='overflow:hidden'>
                  <form:input
                      path="sequencerPartitionContainers[${containerCount.index}].identificationBarcode"/>
                </div>
              </td>
            </c:when>
            <c:otherwise>
              <td>Serial Number:</td>
              <td>
                <span id="idBarcode"><a href='<c:url value="/miso/container/${container.id}"/>' title='idBarcode'>${container.identificationBarcode}</a></span>
                <a href="javascript:void(0);"
                   onclick="Run.ui.editContainerIdBarcode(jQuery('#idBarcode'), ${containerCount.index})">
                  <span id="pencil" class="fg-button ui-icon ui-icon-pencil"></span>
                </a>
              </td>
            </c:otherwise>
          </c:choose>
        </tr>
        <tr>
          <c:choose>
            <c:when test="${empty container.locationBarcode}">
              <td>Location:</td>
              <td><form:input
                  path="sequencerPartitionContainers[${containerCount.index}].locationBarcode"/></td>
            </c:when>
            <c:otherwise>
              <td>Location:</td>
              <td>
                <span id="locationBarcode">${container.locationBarcode}</span>
                <c:if test="${(container.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                                    or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <a href="javascript:void(0);"
                     onclick="Run.ui.editContainerLocationBarcode(jQuery('#locationBarcode'), ${containerCount.index})">
                    <span class="fg-button ui-icon ui-icon-pencil"></span>
                  </a>
                </c:if>
              </td>
            </c:otherwise>
          </c:choose>
        </tr>
        <tr>
          <c:choose>
            <c:when test="${empty container.validationBarcode}">
              <td>Validation:</td>
              <td><form:input
                  path="sequencerPartitionContainers[${containerCount.index}].validationBarcode"/></td>
            </c:when>
            <c:otherwise>
              <td>Validation:</td>
              <td>
                <span id="validationBarcode">${container.validationBarcode}</span>
                <c:if test="${(container.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                                    or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <a href="javascript:void(0);"
                     onclick="editContainerValidationBarcode(jQuery('#validationBarcode'), 0)">
                    <span class="fg-button ui-icon ui-icon-pencil"></span>
                  </a>
                </c:if>
              </td>
            </c:otherwise>
          </c:choose>
        </tr>
      </table>
      <div id='partitionErrorDiv' class="parsley-custom-error-message"></div>
      <div id="partitionDiv">
        <i class="italicInfo">Click in a ${platformType.partitionName} box to beep/type in barcodes, or double click a
          pool on the right to sequentially add pools to the ${platformType.containerName}</i>
        <table class="in">
          <tr>
	          <th>${platformType.partitionName} No.</th>
	          <th>Pool</th>
	          <c:if test="${statsAvailable}">
	            <th>Stats</th>
	          </c:if>
	        </tr>
          <c:forEach items="${container.partitions}" var="partition" varStatus="partitionCount">
            <tr>
              <td>${partition.partitionNumber}</td>
              <td width="90%">
                <c:choose>
                  <c:when test="${not empty partition.pool}">
                    <div class="dashboard">
                      <a href='<c:url value="/miso/pool/${partition.pool.id}"/>'>
                          ${partition.pool.name}
                        (${partition.pool.creationDate})
                      </a><br/>
                      <span style="font-size:8pt" id='partition_span_${partitionCount.index}'>
                        <c:choose>
                          <c:when test="${not empty partition.pool.experiments}">
                            <i><c:forEach items="${partition.pool.experiments}" var="experiment">
                              ${experiment.study.project.alias} (${experiment.name}: ${fn:length(partition.pool.poolableElements)} dilutions)<br/>
                            </c:forEach>
                            </i>
                            <script>
                              jQuery(document).ready(function () {
                                Run.container.checkPoolExperiment('#partition_span_${partitionCount.index}', ${partition.pool.id}, ${partitionCount.index});
                              });
                            </script>
                            <input type="hidden"
                                   name="sequencerPartitionContainers[${containerCount.index}].partitions[${partitionCount.index}].pool"
                                   id="pId${partitionCount.index}"
                                   value="${partition.pool.id}"/>
                          </c:when>
                          <c:otherwise>
                            <i>No experiment linked to this pool</i>
                          </c:otherwise>
                        </c:choose>
                      </span>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div id="p_div_${partitionCount.index}"
                         class="elementListDroppableDiv">
                      <div class="runPartitionDroppable"
                           bind="sequencerPartitionContainers[${containerCount.index}].partitions[${partitionCount.index}].pool"
                           partition="${containerCount.index}_${partitionCount.index}"
                           ondblclick='Run.container.populatePartition(this, ${containerCount.index}, ${partitionCount.index});'></div>
                    </div>
                  </c:otherwise>
                </c:choose>
              </td>
              <c:if test="${statsAvailable}">
                <td><img id="charttrigger" src="<c:url value='/styles/images/chart-bar-icon.png'/>"
                         border="0"
                         onclick="Stats.getPartitionStats(${run.id}, ${partition.partitionNumber}); checkstats(${run.id}, ${partition.partitionNumber}); ">
                </td>
              </c:if>
            </tr>
          </c:forEach>
        </table>
      </div>
      <input type="hidden" value="${container.id}"
             id="sequencerPartitionContainers${containerCount.count-1}"
             name="sequencerPartitionContainers"/>
    </div>
  </c:forEach>
</c:otherwise>
</c:choose>
  <%-- <form:hidden path="sequencerPartitionContainers"/> --%>
</div>
</td>
<td width="50%" valign="top">
  <h2>Available Pools</h2>
      <input id="showOnlyReady" type="checkbox" checked="true"
             onclick="Run.pool.toggleReadyToRunCheck(this, '${platformType.key}');"/>Only Ready to Run pools?
      <div align="right" style="margin-top: -23px; margin-bottom:3px">Filter:
        <input type="text" size="8" id="searchPools" name="searchPools"/></div>
      <script type="text/javascript">
        Utils.timer.typewatchFunc(jQuery('#searchPools'), function () {
          Run.pool.poolSearch(jQuery('#searchPools').val(), '${platformType.key}');
        }, 300, 2);
      </script>
  <div id='poolList' class="elementList ui-corner-all" style="height:500px">
  </div>
</td>
</tr>
</tbody>
</table>
</div>

<script type="text/javascript">
  jQuery("#charttrigger").colorbox({
    width: "90%",
    html: "<div style='display:none'> " +
          "<div id=\"graphpanel\"> " +
          "<div id=\"statresultgraph\">" +
          "<center><h2>Sample <span id=chartSample></span> Partition <span id=chartPartition></span> Statistics</h2></center> " +
          "<div id=\"statstable\"></div> " +
          "<div style=\"width: 45%; left: 10px; position: absolute;\"><h2>Quality Profile</h2>  " +
          "<div id=\"statschartqualityprofile\" style=\"width: 100%; height:650px; position: absolute; overflow-x: scroll; overflow-y: hidden; \"></div>" +
          "</div> " +
          "<div style=\"width: 45%; right: 10px; position: absolute;\"><h2>Per Base Content</h2>    " +
          "<div id=\"statschartperbasecontent\" style=\"width: 100%; right: 10px; position: absolute;\"></div>" +
          "</div>" +
          "<div id=\"statschartmessage\"></div> " +
          "</div>" +
          "</div>" +
          "</div>"});

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

     Run.pool.poolSearch("", '${platformType.key}');
     <c:if test="${run.id != 0 and metrixEnabled}">
       Stats.checkRunProgress('${run.alias}', '${platformType.key}');
     </c:if>

    <c:if test="${statsAvailable}">
      Stats.getRunStats(${run.id});
    </c:if>
  });

  function checkstats(runId, lane) {
    jQuery('#statschartmessage').html("");
    jQuery('#statschartqualityprofile').html('<img src="<c:url value="/styles/images/loading.gif"/>"/>');

    Fluxion.doAjax(
      'statsControllerHelperService',
      'getSummaryRunstatsDiagram',
      {'runId': runId, 'lane': lane, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery('#statschartqualityprofile').html("");
        readStats(json);
      }
      }
    );

    jQuery('#statschartmessage').html("");
    jQuery('#statschartperbasecontent').html('<img src="<c:url value="/styles/images/loading.gif"/>"/>');

    Fluxion.doAjax(
      'statsControllerHelperService',
      'getPerPositionBaseContentDiagram',
      {'runId': runId, 'lane': lane, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        readStatsperbasecontent(json);
      }
      }
    );
  }

  function readStats(json) {
    readStatsdb(json);
  }

  function checkstatsperbasecontent(runId, lane) {
    jQuery('#statschartmessage').html("");
    jQuery('#chartheader').html("Per Base Percentage");
    jQuery('#statschart').html('<img src="<c:url value="/styles/images/loading.gif"/>"/>');

    Fluxion.doAjax(
      'statsControllerHelperService',
      'getPerPositionBaseContentDiagram',
      {'runId': runId, 'lane': lane, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        readStatsperbasecontent(json);
      }
      }
    );
  }

  function readStatsperbasecontent(json) {
    readStatsdbperbasecontent(json);
  }
</script>

<br/>
</form:form>
<c:if test="${not empty run.changeLogs}">
  <br/>
  <h1>Changes</h1>
  <div style="clear:both">
    <table class="list" id="changelog_table">
      <thead>
      <tr>
        <th>Editor</th>
        <th>Summary</th>
        <th>Time</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${run.changeLogs}" var="change">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${change.user.fullName} (${change.user.loginName})</td>
          <td><b>${change.summary}</b></td>
          <td>${change.time}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
</c:if>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
