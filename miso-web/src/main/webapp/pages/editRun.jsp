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
  Time: 15:09:13

--%>
<%@ include file="../header.jsp" %>
<!-- fileupload -->
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload-process.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload-jquery-ui.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.iframe-transport.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/vendor/jquery.ui.widget.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/fileupload/css/jquery.fileupload.css'/>" rel="stylesheet" type="text/css">
<link href="<c:url value='/scripts/jquery/fileupload/css/jquery.fileupload-ui.css'/>" rel="stylesheet" type="text/css">

<script src="<c:url value='/scripts/statsdb.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/statsdbperbasecontent.js'/>" type="text/javascript"></script>

<script type="text/javascript" src="<c:url value='/scripts/stats_ajax.js?ts=${timestamp.time}'/>"></script>

<c:choose>
  <c:when test="${not empty run.status}"><div id="maincontent" class="${run.status.health.key}"></c:when>
  <c:otherwise><div id="maincontent"></c:otherwise>
</c:choose>
<div id="contentcolumn">
<form:form action="/miso/run" method="POST" modelAttribute="run" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="run"/>
<nav class="navbar navbar-default" role="navigation">
   <div class="navbar-header">
      <span class="navbar-brand navbar-center">
        <c:choose>
          <c:when test="${run.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose> Run
      </span>
   </div>
   <div class="navbar-right container-fluid">
      <button type="button" class="btn btn-default navbar-btn" onclick="return validate_run(this.form);">Save</button>
   </div>
</nav>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Run contains the sequencing results from sequencing Experiments.
  Each run partition (lane/chamber) holds a Pool which is linked to a number of Experiments to facilitate multiplexing
  if required.
</div>
<h2>Run Information</h2>
<%--
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
</ul> --%>
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
        <%--<td><a href="void(0);" onclick="popup('help/runAccession.html');">Help</a></td>--%>
    </tr>
  </c:if>
  <tr>
    <c:choose>
      <c:when test="${run.id == 0}">
        <td>Platform:</td>
        <td>
          <c:choose>
            <c:when test="${not empty run.status and run.status.health.key ne 'Unknown'}"><form:radiobuttons
                id="platformTypes" path="platformType" items="${platformTypes}"
                onchange="Run.ui.changePlatformType(this);"
                disabled="disabled"/></c:when>
            <c:otherwise><form:radiobuttons id="platformTypes" path="platformType" items="${platformTypes}"
                                            onchange="Run.ui.changePlatformType(this);"/></c:otherwise>
          </c:choose>
        </td>
      </c:when>
      <c:otherwise>
        <td>Platform</td>
        <td>${run.platformType.key}</td>
      </c:otherwise>
    </c:choose>
  </tr>

  <tr>
    <c:choose>
      <c:when test="${run.id == 0}">
        <td>Sequencer:</td>
        <td id="sequencerReferenceSelect">
          <i>Please choose a platform above...</i>
        </td>
      </c:when>
      <c:otherwise>
        <td>Sequencer</td>
        <td>${run.sequencerReference.name} - ${run.sequencerReference.platform.instrumentModel}</td>
      </c:otherwise>
    </c:choose>
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
    <td class="h">Alias:</td>
    <td>
      <div class="input-group"><form:input path="alias" class="validateable form-control"/><span class="input-group-addon" id="aliascounter"></span></div></span>
    </td>
  </tr>
  <tr>
    <td>Description:</td>
    <td>
      <div class="input-group">
      <c:choose>
        <c:when test="${not empty run.status and run.status.health.key ne 'Unknown'}"><form:input
            path="description" disabled="disabled" class="validateable form-control"/></c:when>
        <c:otherwise><form:input path="description" class="validateable form-control"/></c:otherwise>
      </c:choose>
      <span id="descriptioncounter" class="input-group-addon"></span>
      </div>
    </td>
  </tr>
  <tr>
    <td>Run Path:</td>
    <td>
      <c:choose>
        <c:when test="${not empty run.filePath}">${run.filePath}</c:when>
        <c:otherwise><form:input path="filePath" class="form-control"/></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td>Paired End:</td>
    <td>
      <c:choose>
        <c:when test="${not empty run.status and run.status.health.key ne 'Unknown'}"><form:checkbox
            value="${run.pairedEnd}" path="pairedEnd" disabled="disabled"/></c:when>
        <c:otherwise><form:checkbox value="${run.pairedEnd}" path="pairedEnd"/></c:otherwise>
      </c:choose>
    </td>
  </tr>

  <tr>
    <td valign="top">Status:</td>
    <td>
      <div id="health-radio" class="btn-group" data-toggle="buttons">
      <form:radiobuttons id="status.health" path="status.health" items="${healthTypes}"
                         onchange="checkForCompletionDate();" element="label class='btn btn-default'"/>
      </div>
      <script>
        var c = jQuery('#health-radio :input:checked');
        c.parent('.btn').addClass('active');
        var inpv = c.val();
        if (inpv === "Completed") { c.parent('.btn').removeClass('btn-default').addClass("btn-success"); }
        if (inpv === "Failed") { c.parent('.btn').removeClass('btn-default').addClass("btn-danger"); }
      </script>

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
          <td><fmt:formatDate pattern="dd/MM/yyyy" value="${run.status.startDate}"/></td>
          <c:choose>
            <c:when test="${(run.status.health.key eq 'Completed' and empty run.status.completionDate)
                    or run.status.health.key eq 'Failed'
                    or run.status.health.key eq 'Stopped'}">
              <td><form:input path="status.completionDate" class="form-control"/></td>
              <script type="text/javascript">
                Utils.ui.addDatePicker("status\\.completionDate");
              </script>
            </c:when>
            <c:otherwise>
              <td id="completionDate">
                <fmt:formatDate pattern="dd/MM/yyyy" value="${run.status.completionDate}"/>
              </td>
            </c:otherwise>
          </c:choose>
          <td>
            <fmt:formatDate value="${run.status.lastUpdated}" dateStyle="long" pattern="dd/MM/yyyy HH:mm:ss"/>
          </td>
        </tr>
        </tbody>
      </table>

      <c:if test="${not empty run.status.xml}">
        ${statusXml}
      </c:if>
    </td>
  </tr>

</table>
<%@ include file="permissions.jsp" %>

<c:if test="${run.id != 0}">
  <div id="simplebox">
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#upload_arrowclick'), 'uploaddiv');">
      Run Files
      <div id="upload_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="uploaddiv" class="panel panel-default padded-panel" style="display:none;">
      <table class="in">
        <tr>
          <td>
            <span id="upload-area">
              <div class="fileupload-buttonbar">
                <div class="fileupload-buttonbar">
                  <div>
                    <span class="btn btn-success fileinput-button">
                      <i class="glyphicon glyphicon-plus"></i>
                      <span>Add files...</span>
                      <input id="ajax_upload_form" type="file" name="files[]" data-url="<c:url value="/miso/upload/run"/>" multiple/>
                    </span>
                  </div>
                </div>
              </div>
              <div id="progress" class="progress">
                <div class="progress-bar progress-bar-success"></div>
              </div>

              <div id="selectedFiles" class="files"></div>
            </span>

            <script>
              jQuery('#ajax_upload_form').fileupload({
                formData: {'runId': '${run.id}'},
                dataType: 'json',
                done: function (e, data) {
                  jQuery.each(data.result.files, function (index, file) {
                    var r = "<a href='"+file.url+"'><a class='listbox' href='"+file.url+"'><div onMouseOver='this.className=\"boxlistboxhighlight\"' onMouseOut='this.className=\"boxlistbox\"' class='boxlistbox'>"+file.name+"</div></a></a>";
                    jQuery(r).prependTo('#runfiles');
                  });

                  //reset progress
                  jQuery('#progress .progress-bar').css('width', '0%');
                },
                progress: function (e, data) {
                  var progress = parseInt(data.loaded / data.total * 100, 10);
                  jQuery('#progress .progress-bar').css('width', progress + '%');
                }
              }).prop('disabled', !jQuery.support.fileInput)
                .parent().addClass(jQuery.support.fileInput ? undefined : 'disabled');
            </script>
          </td>
        </tr>
      </table>

      <div id="runfiles">
        <c:forEach items="${runFiles}" var="file">
          <div id="file${file.key}">
            <div onMouseOver="this.className='boxlistboxhighlight'" onMouseOut="this.className='boxlistbox'" class="boxlistbox">
              <a href="<c:url value='/miso/download/run/${run.id}/${file.key}'/>">${file.value}</a>
              <c:if test="${(library.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                              or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              <a href='#' onclick="Run.ui.deleteRunFile('${run.id}', '${file.value}', '${file.key}');">
                <i class="fa fa-trash-o fa-lg fa-fw pull-right" style="padding-top:4px"></i>
              </a>
              </c:if>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>

  <c:if test="${statsAvailable}">
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#stats_arrowclick'), 'stats');">Statistics
      <div id="stats_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="stats">
      <nav id="navbar-stats" class="navbar navbar-default navbar-static" role="navigation">
        <div class="navbar-header">
          <span class="navbar-brand navbar-center">Stats</span>
        </div>
      </nav>

      <div id="summarydiv"></div>
    </div>
  </c:if>

  <c:if test="${run.status.health.key ne 'Failed' and run.status.health.key ne 'Stopped' and metrixEnabled and run.platformType.key eq 'Illumina'}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#metrix_arrowclick'), 'metrix');">InterOp Metrics
    <div id="metrix_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="metrix">
    <nav id="navbar-metrix" class="navbar navbar-default navbar-static" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">InterOp Metrics</span>
      </div>
    </nav>

    <div id="metrixdiv"></div>
  </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      Stats.getInterOpMetrics('${run.alias}', '${run.platformType.key}');
    });
  </script>
  </c:if>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
    <c:choose>
      <c:when test="${fn:length(sample.notes) > 0}">
        <div id="notes_arrowclick" class="toggleLeftDown"></div>
      </div>
      <div id="notes" class="panel panel-default padded-panel">
      </c:when>
      <c:otherwise>
        <div id="notes_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notes" class="panel panel-default padded-panel" style="display:none">
      </c:otherwise>
    </c:choose>

    <nav id="navbar-notes" class="navbar navbar-default navbar-static" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">Notes</span>
      </div>
      <div class="collapse navbar-collapse">
        <ul class="nav navbar-nav navbar-right">
          <%--<li><a href="#">Link</a></li>--%>
          <li id="notes-menu" class="dropdown">
            <a id="notedrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="notedrop1">
              <li role="presentation">
                <a role="menuitem" onclick="Run.ui.showRunNoteDialog(${run.id});" href="javascript:void(0);">Add Note</a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
    <c:if test="${fn:length(run.notes) > 0}">
      <div id="notelist" class="note" style="clear:both">
        <c:forEach items="${run.notes}" var="note" varStatus="n">
          <div class="exppreview" id="run-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
          <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
            <c:if test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                            or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <span style="color:#000000"><a href='#' onclick="Run.ui.deleteRunNote('${run.runId}', '${note.noteId}');">
              <i class="fa fa-trash-o fa-fw"></i></a></span>
            </c:if>
          </span>
          </div>
        </c:forEach>
      </div>
    </c:if>
    <div id="addRunNoteDialog" title="Create new Note"></div>
  </div>
</c:if>

<c:if test="${not empty run.status and run.status.health.key eq 'Completed'}">
  <nav id="navbar-runqc" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span id="qcsTotalCount" class="navbar-brand navbar-center">Run QCs</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="runqc-menu" class="dropdown">
          <a id="runqcdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="runqcdrop1">
            <li role="presentation">
              <a onclick="Run.qc.generateRunQCRow(${run.id});" href='javascript:void(0);'>Add Run QC</a>
            </li>
            <c:if test="${operationsQcPassed}">
              <li role="presentation"><a href='<c:url value="/miso/analysis/new/run/${run.id}"/>'>Initiate Analysis</a></li>
            </c:if>
          </ul>
        </li>
      </ul>
    </div>
  </nav>
<span style="clear:both">
  <div id="addRunQC"></div>
  <table class="table table-bordered table-striped in" id="runQcTable">
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
<table style="width:100%;">
<tbody>
<tr>
<td style="width:50%; vertical-align: top; padding-right:3px;">
<div id="container-panel" class="panel panel-default panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title">Container Parameters</h3>
  </div>
  <div class="panel-body padded-panel">
  <c:choose>
    <c:when test="${empty run.sequencerPartitionContainers}">
      <div id="runPartitions">
        Container:
        <c:choose>
          <c:when test="${not empty run.sequencerReference}">
            <c:forEach var="platformContainerCount" begin="1"
                       end="${run.sequencerReference.platform.numContainers}" step="1"
                       varStatus="platformContainer">
              <input id='container${platformContainerCount}select' name='containerselect'
                     onchange="Run.container.changeContainer(this.value, '${run.platformType.key}', ${run.sequencerReference.id});"
                     type='radio'
                     value='${platformContainerCount}'/>${platformContainerCount}
            </c:forEach>
          </c:when>
          <c:otherwise>
            <input id='container1select' name='containerselect'
                   onchange="Run.container.changeContainer(this.value, '${run.platformType.key}', ${run.sequencerReference.id});"
                   type='radio' value='1'/>1
          </c:otherwise>
        </c:choose>

        <div id='containerdiv' class="note ui-corner-all"></div>
      </div>
    </c:when>
    <c:otherwise>
      <div id="containerPartitions">
        <c:forEach items="${run.sequencerPartitionContainers}" var="container" varStatus="containerCount">
          <div class="panel panel-default">
            <nav id="navbar-cont-${containerCount.count}" class="navbar navbar-default navbar-static" role="navigation">
              <div class="navbar-header">
                <span class="navbar-brand navbar-center">Container ${containerCount.count}</span>
              </div>
              <c:if test="${not empty container.identificationBarcode}">
              <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
                <ul class="nav navbar-nav navbar-right">
                  <li id="cont-${containerCount.count}-menu" class="dropdown">
                    <a id="cont-${containerCount.count}-drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
                    <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="cont-${containerCount.count}-drop1">
                      <li role="presentation"><a href="javascript:void(0);" onclick="Run.container.generateCasava18DemultiplexCSV(${run.id}, ${container.id});">Demultiplex CSV (1.8+)</a></li>
                      <li role="presentation"><a href="javascript:void(0);" onclick="Run.container.generateCasava17DemultiplexCSV(${run.id}, ${container.id});">Demultiplex CSV (pre-1.8)</a></li>
                    </ul>
                  </li>
                </ul>
              </div>
              </c:if>
            </nav>

            <table class="in">
              <tr>
                <c:choose>
                  <c:when test="${empty container.identificationBarcode}">
                    <td>ID:</td>
                    <td>
                      <form:input path="sequencerPartitionContainers[${containerCount.index}].identificationBarcode" class="form-control"/>
                    </td>
                    <td>
                      <button onclick='Run.container.lookupContainer(this, ${containerCount.index});'
                              type='button' class='btn btn-default pull-right'>Lookup
                      </button>
                    </td>
                  </c:when>
                  <c:otherwise>
                    <td>ID:</td>
                    <td>
                      <span id="idBarcode">${container.identificationBarcode}</span>
                    </td>
                    <td>
                      <a href="javascript:void(0);"
                         onclick="Run.ui.editContainerIdBarcode(jQuery('#idBarcode'), ${containerCount.index})">
                        <span class="fa fa-pencil-square-o fa-lg"></span>
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
                        path="sequencerPartitionContainers[${containerCount.index}].locationBarcode" class="form-control"/></td>
                  </c:when>
                  <c:otherwise>
                    <td>Location:</td>
                    <td>
                      <span id="locationBarcode">${container.locationBarcode}</span>
                      <c:if test="${(container.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                                          or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                        <a href="javascript:void(0);"
                           onclick="Run.ui.editContainerLocationBarcode(jQuery('#locationBarcode'), ${containerCount.index})">
                          <span class="fa fa-pencil-square-o fa-lg"></span>
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
                        path="sequencerPartitionContainers[${containerCount.index}].validationBarcode" class="form-control"/></td>
                  </c:when>
                  <c:otherwise>
                    <td>Validation:</td>
                    <td>
                      <span id="validationBarcode">${container.validationBarcode}</span>
                      <c:if test="${(container.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                                          or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                        <a href="javascript:void(0);"
                           onclick="editContainerValidationBarcode(jQuery('#validationBarcode'), 0)">
                          <span class="fa fa-pencil-square-o fa-lg"></span>
                        </a>
                      </c:if>
                    </td>
                  </c:otherwise>
                </c:choose>
              </tr>
            </table>
            <div id='partitionErrorDiv'></div>
            <div id="partitionDiv">
              <i class="italicInfo">Click in a partition box to beep/type in barcodes, or double click a
                pool on the right to sequentially add pools to the container</i>
              <table class="in">
                <c:forEach items="${container.partitions}" var="partition" varStatus="partitionCount">
                  <tr>
                    <td class="partition-number">${partition.partitionNumber}</td>
                    <td>
                      <c:choose>
                        <c:when test="${not empty partition.pool}">
                          <div class="dashboard">
                              <%-- <a href='<c:url value="/miso/pool/${fn:toLowerCase(run.platformType.key)}/${partition.pool.id}"/>'> --%>
                            <a href='<c:url value="/miso/pool/${partition.pool.id}"/>'>
                                ${partition.pool.name}
                              (${partition.pool.creationDate})
                            </a><br/>
                            <span style="font-size:8pt" id='partition_span_${partitionCount.index}'>
                              <c:choose>
                                <c:when test="${not empty partition.pool.experiments}">
                                  <i><c:forEach items="${partition.pool.experiments}" var="experiment">
                                    ${experiment.study.project.alias} (${experiment.name}: ${fn:length(partition.pool.dilutions)} dilutions)<br/>
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
      </div>
    </c:otherwise>
  </c:choose>
  </div>
</div>
</td>
<td width="50%" valign="top">
  <div id="pools-panel" class="panel panel-default panel-primary">
    <div class="panel-heading">
      <h3 class="panel-title">Available Pools</h3>
      <div class="float-right" style="margin-top: -20px;">
        <c:choose>
          <c:when test="${not empty run.platformType}">
          <div class="float-left" style="padding-right: 10px;">
            <input id="showOnlyReady" type="checkbox" checked="true"
                   onclick="Run.pool.toggleReadyToRunCheck(this, '${run.platformType.key}');"/>Only Ready to Run pools?
          </div>
          <div style="margin-top: -3px; margin-bottom:3px; float:right; width: 165px;">
            <label for="searchPools" style="margin-top:5px; float:left">Filter:</label>
            <input type="text" size="8" id="searchPools" name="searchPools" class="form-control float-right" style="width:120px">
          </div>
          <script type="text/javascript">
            Utils.timer.typewatchFunc(jQuery('#searchPools'), function () {
              Run.pool.poolSearch(jQuery('#searchPools').val(), '${run.platformType.key}');
            }, 300, 2);
          </script>
        </c:when>
        <c:otherwise>
          <div class="float-left" style="padding-right: 10px;">
            <input id="showOnlyReady" type="checkbox" checked="true"
                   onclick="Run.pool.toggleReadyToRunCheck(this, jQuery('input[name=platformType]:checked').val());"/>Only Ready to Run pools?
          </div>
          <div style="margin-top: -3px; margin-bottom:3px; float:right; width: 165px;">
            <label for="searchPools" style="margin-top:5px; float:left">Filter:</label>
            <input type="text" size="8" id="searchPools" name="searchPools" class="form-control float-right" style="width:120px">
          </div>
          <script type="text/javascript">
            Utils.timer.typewatchFunc(jQuery('#searchPools'), function () {
              Run.pool.poolSearch(jQuery('#searchPools').val(), jQuery('input[name=platformType]:checked').val());
            }, 300, 2);
          </script>
        </c:otherwise>
      </c:choose>
      </div>
    </div>
    <div class="panel-body padded-panel">
      <div id='poolList' class="list-group" style="height:500px"></div>
    </div>
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

    <c:choose>
    <c:when test="${not empty run.platformType}">
      Run.pool.poolSearch("", '${run.platformType.key}');
      <c:if test="${run.id != 0}">
        Stats.checkRunProgress('${run.alias}', '${run.platformType.key}');
      </c:if>
    </c:when>
    <c:otherwise>
      Run.pool.poolSearch("", jQuery('input[name=platformType]:checked').val());
    </c:otherwise>
    </c:choose>

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
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>