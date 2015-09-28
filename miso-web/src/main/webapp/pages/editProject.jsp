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

<%@ include file="../header.jsp" %>
<!-- fileupload -->
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload-process.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.fileupload-jquery-ui.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/jquery.iframe-transport.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/fileupload/js/vendor/jquery.ui.widget.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/fileupload/css/jquery.fileupload.css'/>" rel="stylesheet" type="text/css">
<link href="<c:url value='/scripts/jquery/fileupload/css/jquery.fileupload-ui.css'/>" rel="stylesheet" type="text/css">

<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.popup.js'/>"></script>

<link rel="stylesheet" href="<c:url value='/styles/progress.css'/>" type="text/css">

<div id="maincontent">
<div id="contentcolumn">

<form:form action="/miso/project" method="POST" commandName="project" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="project"/>
<nav class="navbar navbar-default" role="navigation">
   <div class="navbar-header">
      <span class="navbar-brand navbar-center">
        <c:choose>
          <c:when test="${project.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose> Project
      </span>
   </div>
   <div class="navbar-right container-fluid">
      <button type="button" class="btn btn-default navbar-btn" onclick="return validate_project(this.form);">Save</button>
   </div>
</nav>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Project contains information about a set of Studies that may
  comprise many different Samples, Experiments and Runs. Samples are attached to Projects as they are often
  processed into Dilutions, which are then Pooled and sequenced.<br/>Projects also have Overviews, which hold
  information about a Project proposal.
</div>

<c:if test="${project.id != 0}">
  <div id="trafdiv" class="ui-corner-all" onclick="location.href='#';">
    <div id="pro${project.id}traf"></div>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        Project.ui.editProjectTrafficLight(${project.id});

        jQuery("#trafdiv").colorbox({width: "90%", inline: true, href: "#trafpanel"});
      });
    </script>
  </div>
  <div style='display:none'>
    <div id="trafpanel">
      <div id="trafresultgraph">
        <div id="chart"></div>
      </div>
    </div>
  </div>

</c:if>

<h2>Project Information</h2>
<table class="in">
  <tr>
    <td class="h">Project ID:</td>
    <td>
      <c:choose>
        <c:when test="${project.id != 0}">${project.id}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Name:</td>
    <td>
      <c:choose>
        <c:when test="${project.id != 0}">${project.name}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Creation date:</td>
    <td><fmt:formatDate value="${project.creationDate}"/></td>
  </tr>
  <tr>
    <td class="h">Alias:</td>
    <td>
      <div class="input-group">
        <form:input path="alias" maxlength="${maxLengths['alias']}" class="validateable form-control"/>
        <span id="aliascounter" class="input-group-addon"></span>
      </div>
    </td>
  </tr>
  <tr>
    <td class="h">Description:</td>
    <td>
      <div class="input-group">
        <form:input path="description" maxlength="${maxLengths['description']}" class="validateable form-control"/>
        <span id="descriptioncounter" class="input-group-addon"></span>
      </div>
    </td>
  </tr>
  <tr>
    <td>Progress:</td>
    <td>
      <c:choose>
        <c:when test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
          <div id="progress-radio" class="btn-group" data-toggle="buttons">
          <form:radiobuttons id="progress" path="progress" element="label class='btn btn-default'"/>
          </div>
          <script>
            var c = jQuery('#progress-radio :input:checked');
            c.parent('.btn').addClass('active');
            var inpv = c.val();
            if (inpv === "ACTIVE" || inpv === "Active") { c.parent('.btn').removeClass('btn-default').addClass("btn-info"); }
            if (inpv === "COMPLETED" || inpv === "Active") { c.parent('.btn').removeClass('btn-default').addClass("btn-success"); }
            if (inpv === "CANCELLED" || inpv === "Inactive") { c.parent('.btn').removeClass('btn-default').addClass("btn-warning"); }
          </script>
        </c:when>
        <c:otherwise>
          ${project.progress}
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
</table>
<div id="printServiceSelectDialog" title="Select a Printer"></div>

<div id="projectoverviews" class="panel panel-default padded-panel">
<nav id="navbar-pro-over" class="navbar navbar-default navbar-static" role="navigation">
  <div class="navbar-header">
    <span class="navbar-brand navbar-center">Project Overviews</span>
  </div>
  <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
    <ul class="nav navbar-nav navbar-right">
      <li id="pro-over-menu" class="dropdown">
        <a id="pro-over-drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
        <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="pro-over-drop1">
        <c:if test="${project.id != 0}">
          <li role="presentation"><a href="javascript:void(0);" onclick="Project.overview.showProjectOverviewDialog(${project.id});">Add Overview</a></li>
        </c:if>
        </ul>
      </li>
    </ul>
  </div>
</nav>

<c:choose>
<c:when test="${not empty project.overviews}">
<c:forEach items="${project.overviews}" var="overview" varStatus="ov">
<div id="overviewdiv${overview.overviewId}" class="panel panel-default">
<nav id="navbar-pro-over${overview.overviewId}" class="navbar navbar-default navbar-static" role="navigation">
  <div class="navbar-header">
    <span class="navbar-brand navbar-center">Overview ${overview.overviewId}</span>
  </div>
  <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
    <ul class="nav navbar-nav navbar-right">
      <li class="dropdown">
        <a class="dropdown-toggle" data-toggle="dropdown" href="#">
          <i class="fa fa-eye fa-fw"></i> <i class="fa fa-caret-down"></i>
          <span id="watchersMenuDropdown"></span>
        </a>
        <ul class="dropdown-menu dropdown-watchers" id="watchersList${overview.overviewId}"></ul>
      </li>
      <li id="pro-over${overview.overviewId}-menu" class="dropdown">
        <a id="pro-over${overview.overviewId}-drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
        <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="pro-over${overview.overviewId}-drop1">
        <c:if test="${not overview.locked}">
          <li role="presentation"><a onclick="Project.overview.showProjectOverviewNoteDialog(${overview.overviewId});" href="javascript:void(0);">Add Note</a></li>
          <c:if test="${project.id != 0 && empty overview.sampleGroup}">
          <li role="presentation"><a href="javascript:void(0);" onclick="Project.overview.addSampleGroupTable(${project.id}, ${overview.id});">Add Sample Group</a></li>
          </c:if>
        </c:if>
        <li role="presentation" class="divider">
        <c:choose>
        <c:when test="${not empty overviewMap[overview.overviewId]}">
          <li role="presentation"><a href='javascript:void(0);' onclick="Project.alert.unwatchOverview(${overview.overviewId});">Stop watching</a></li>
        </c:when>
        <c:otherwise>
          <li role="presentation"><a href='javascript:void(0);' onclick="Project.alert.watchOverview(${overview.overviewId});">Watch</a></li>
        </c:otherwise>
        </c:choose>
        </ul>
      </li>
    </ul>
  </div>
</nav>

<script type="text/javascript">
  jQuery(document).ready(function () {
    //show watchers list
    Project.alert.listWatchOverview(${overview.overviewId});
  });
</script>

<table class="table table-striped table-bordered" id="overview">
  <thead>
  <tr>
    <c:if test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
      <th>Lock/Unlock</th>
    </c:if>
    <th>Principal Investigator</th>
    <th>Start Date</th>
    <th>End Date</th>
    <th># Proposed Samples</th>
    <th># QC Passed Samples</th>
    <th width="40%">Notes</th>
  </tr>
  </thead>
  <tbody>
  <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
    <c:if test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                      or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
      <c:choose>
        <c:when test="${overview.locked}">
          <td style="text-align:center;">
            <a href="javascript:void(0);" onclick="Project.overview.unlockProjectOverview(${overview.overviewId})">
              <img style="border:0;" alt="Unlock" title="Unlock this overview" src="<c:url value='/styles/images/lock_closed.png'/>"/>
            </a>
          </td>
        </c:when>
        <c:otherwise>
          <td style="text-align:center;">
            <a href="javascript:void(0);" onclick="Project.overview.lockProjectOverview(${overview.overviewId})">
              <img style="border:0;" alt="Lock" title="Lock this overview" src="<c:url value='/styles/images/lock_open.png'/>"/>
            </a>
          </td>
        </c:otherwise>
      </c:choose>
    </c:if>

    <td>${overview.principalInvestigator}</td>
    <td>
      <c:choose>
        <c:when test="${overview.locked eq false and ((project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                      or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN'))}">
          <form:input path="overviews['${ov.count-1}'].startDate" id="startdatepicker" class="form-control"/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("startdatepicker");
          </script>
        </c:when>
        <c:otherwise>
          ${overview.startDate}
        </c:otherwise>
      </c:choose>
    </td>
    <td>
      <c:choose>
        <c:when test="${overview.locked eq false and ((project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                      or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN'))}">
          <form:input path="overviews['${ov.count-1}'].endDate" id="enddatepicker" class="form-control"/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("enddatepicker");
          </script>
        </c:when>
        <c:otherwise>
          ${overview.endDate}
        </c:otherwise>
      </c:choose>
    </td>
    <td>
      <c:choose>
        <c:when test="${overview.locked eq false and ((project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                      or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN'))}">
          <form:input path="overviews['${ov.count-1}'].numProposedSamples" id="numProposedSamples${ov.count-1}" class="form-control"/>
        </c:when>
        <c:otherwise>
          ${overview.numProposedSamples}
        </c:otherwise>
      </c:choose>
    </td>
    <td>
      <div class="form-control-progress progress">
        <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${fn:length(overview.qcPassedSamples)}" aria-valuemin="0" aria-valuemax="100" style="width: ${fn:length(overview.qcPassedSamples) / overview.numProposedSamples * 100}%;">
          ${fn:length(overview.qcPassedSamples)} / ${overview.numProposedSamples}
        </div>
      </div>
    </td>
    <td>
      <c:forEach items="${overview.notes}" var="note" varStatus="n">
        <div class="exppreview" id="overview-notes-${n.count}">
          <b>${note.creationDate}</b>: ${note.text}
          <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
            <c:if test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                            or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <span style="color:#000000"><a href='#' onclick="Project.overview.deleteProjectOverviewNote('${overview.overviewId}', '${note.noteId}');">
              <i class="fa fa-trash-o fa-fw"></i></a></span>
            </c:if>
          </span>
        </div>
      </c:forEach>
    </td>
  </tr>
  </tbody>
</table>

<ol id="progress">
  <li class="sample-qc-step">
    <c:choose>
    <c:when test="${overview.allSampleQcPassed and overview.libraryPreparationComplete}">
    <div class="left mid-progress-done">
    </c:when>
    <c:when test="${overview.allSampleQcPassed}">
    <div class="left-progress-done">
    </c:when>
    <c:otherwise>
    <div class="left">
    </c:otherwise>
    </c:choose>
    <span>Sample QCs</span>
    <form:checkbox value="${overview.allSampleQcPassed}"
                   path="overviews[${ov.count-1}].allSampleQcPassed"/>
    </div>
  </li>

  <li class="lib-prep-step">
    <c:choose>
      <c:when test="${overview.libraryPreparationComplete and overview.allLibrariesQcPassed}">
        <div class="mid-progress-done">
      </c:when>
      <c:when test="${overview.libraryPreparationComplete}">
        <div class="left-progress-done">
      </c:when>
      <c:otherwise>
      <div>
      </c:otherwise>
    </c:choose>
      <span>Libraries prepared</span>
      <form:checkbox value="${overview.libraryPreparationComplete}" path="overviews[${ov.count-1}].libraryPreparationComplete"/>
    </div>
  </li>

  <li class="lib-qc-step">
    <c:choose>
      <c:when test="${overview.allLibrariesQcPassed and overview.allPoolsConstructed}">
        <div class="mid-progress-done">
      </c:when>
      <c:when test="${overview.allLibrariesQcPassed}">
        <div class="left-progress-done">
      </c:when>
      <c:otherwise>
        <div>
      </c:otherwise>
    </c:choose>
      <span>Library QCs</span>
      <form:checkbox value="${overview.allLibrariesQcPassed}" path="overviews[${ov.count-1}].allLibrariesQcPassed"/>
    </div>
  </li>

  <li class="pools-step">
    <c:choose>
      <c:when test="${overview.allPoolsConstructed and overview.allRunsCompleted}">
        <div class="mid-progress-done">
      </c:when>
      <c:when test="${overview.allPoolsConstructed}">
        <div class="left-progress-done">
      </c:when>
      <c:otherwise>
        <div>
      </c:otherwise>
    </c:choose>
      <span>Pools Constructed</span>
      <form:checkbox value="${overview.allPoolsConstructed}" path="overviews[${ov.count-1}].allPoolsConstructed"/>
      </div>
  </li>

  <li class="runs-step">
    <c:choose>
      <c:when test="${overview.allRunsCompleted and overview.primaryAnalysisCompleted}">
        <div class="mid-progress-done">
      </c:when>
      <c:when test="${overview.allRunsCompleted}">
        <div class="left-progress-done">
      </c:when>
      <c:otherwise>
        <div>
      </c:otherwise>
    </c:choose>
      <span>Runs Completed</span>
      <form:checkbox value="${overview.allRunsCompleted}" path="overviews[${ov.count-1}].allRunsCompleted"/>
    </div>
  </li>

  <li class="primary-analysis-step">
    <c:choose>
      <c:when test="${overview.primaryAnalysisCompleted}">
        <div class="right mid-progress-done">
      </c:when>
      <c:otherwise>
        <div class="right">
      </c:otherwise>
    </c:choose>
      <span>Primary Analysis</span>
      <form:checkbox value="${overview.primaryAnalysisCompleted}" path="overviews[${ov.count-1}].primaryAnalysisCompleted"/>
    </div>
  </li>
</ol>

<p style="clear:both"/>

<div id="overviewsamplegroups">
<c:if test="${project.id != 0}">
  <div id="sampleGroupTableDiv${overview.overviewId}"></div>
</c:if>
<span style="clear:both">
  <c:if test="${not empty overview.sampleGroup}">
  <nav id="navbar-samgroup" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">Sample Group ${overview.sampleGroup.id}</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="samgroup-menu" class="dropdown">
          <a id="samgroupdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="samgroupdrop1">
            <li role="presentation">
              <a href="javascript:void(0);" onclick="Project.overview.addSamplesToGroupTable(${project.id}, ${overview.id}, ${overview.sampleGroup.id});">Add more Samples</a>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <table class="table table-striped table-bordered" id="overview_samplegroup_table_${overview.sampleGroup.id}">
      <thead>
      <tr>
        <th>Sample Name</th>
        <th>Sample Alias</th>
        <th class="fit">Edit</th>
        <th class="fit">REMOVE</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${overview.sampleGroup.entities}" var="sample">
        <tr sampleId="${sample.id}" onMouseOver="this.className='highlightrow'"
            onMouseOut="this.className='normalrow'">
          <td><b>${sample.name}</b></td>
          <td>${sample.alias}</td>
          <td class="misoicon" onclick="window.location.href='<c:url value="/miso/sample/${sample.id}"/>'">
            <span class="fa fa-pencil-square-o fa-lg"/>
          </td>
          <td class="misoicon" onclick="Sample.removeSampleFromGroup(${sample.id}, ${overview.sampleGroup.id}, Utils.page.pageReload);">
            <span class="fa fa-trash-o fa-lg"/>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </span>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery('#overview_samplegroup_table_'+${overview.sampleGroup.id}).dataTable({
        "aaSorting": [
          [1, 'asc']
        ],
        "aoColumns": [
          null,
          { "sType": 'natural' },
          null,
          null
        ],
        "iDisplayLength": 50,
        "bJQueryUI": false,
        "bRetrieve": true
      });
    });
  </script>
  </c:if>
</span>
</div>
</div>
</div> <!-- end of panel -->
</c:forEach>
</c:when>
</c:choose>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#issues_arrowclick'), 'issuesdiv');">
  Tracked Issues
  <div id="issues_arrowclick" class="toggleLeft"></div>
</div>
<div id="issuesdiv" class="note" style="display:none;">
  <c:choose>
    <c:when test="${project.id != 0}">
      To link issues to this project please enter your issue keys here, separated by a single comma, e.g. FOO-1,FOO-2,FOO-3:<br/>
      <input type="text" id="previewKeys" name="previewKeys" class="form-control"/>
      <button type="button" class="br-button ui-state-default ui-corner-all" onclick="Project.issues.previewIssueKeys();">
        Preview Issues
      </button>
      <br/>
    </c:when>
    <c:otherwise>
      To import a project from an issue tracker, please enter an Issue Key to form the basis of this project.
      Enter a SINGLE key, e.g. FOO-1, and click Import to link this project to an external issue.<br/>
      <input type="text" id="previewKey" name="previewKey" class="form-control"/>
      <button type="button" class="br-button ui-state-default ui-corner-all" onclick="Project.issues.importProjectFromIssue();">
        Import
      </button>
    </c:otherwise>
  </c:choose>
  <div id="issues"></div>
</div>

<%@ include file="permissions.jsp" %>
<c:if test="${project.id == 0}">
  <script type="text/javascript">
    jQuery(document).ready(function () {
      //show import pane by default if project is unsaved
      jQuery("#issuesdiv").attr("style", "");
      jQuery("#issues_arrowclick").removeClass("toggleLeft").addClass("toggleLeftDown");

      //show permissions pane by default if project is unsaved
      jQuery("#permissions").attr("style", "");
      jQuery("#permissions_arrowclick").removeClass("toggleLeft").addClass("toggleLeftDown");
    });
  </script>
</c:if>
</form:form>

<c:choose>
<c:when test="${project.id != 0}">
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#upload_arrowclick'), 'uploaddiv');">
  Project Files
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
                  <input id="ajax_upload_form" type="file" name="files[]" data-url="<c:url value="/miso/upload/project"/>" multiple/>
                </span>
              </div>
            </div>
          </div>
          <div id="progressbar" class="progress">
            <div class="progress-bar progress-bar-success"></div>
          </div>

          <div id="selectedFiles" class="files"></div>
        </span>

        <script>
          jQuery('#ajax_upload_form').fileupload({
            formData: {'projectId': '${project.id}'},
            dataType: 'json',
            done: function (e, data) {
              jQuery.each(data.result.files, function (index, file) {
                var r = "<a href='"+file.url+"'><a class='listbox' href='"+file.url+"'><div onMouseOver='this.className=\"boxlistboxhighlight\"' onMouseOut='this.className=\"boxlistbox\"' class='boxlistbox'>"+file.name+"</div></a></a>";
                jQuery(r).prependTo('#projectfiles');
              });

              //reset progress
              jQuery('#progressbar .progress-bar').css('width', '0%');
            },
            progress: function (e, data) {
              var progress = parseInt(data.loaded / data.total * 100, 10);
              jQuery('#progressbar .progress-bar').css('width', progress + '%');
            }
          }).prop('disabled', !jQuery.support.fileInput)
            .parent().addClass(jQuery.support.fileInput ? undefined : 'disabled');
        </script>
      </td>
    </tr>
  </table>

  <div id="projectfiles">
    <c:forEach items="${projectFiles}" var="file">
      <div id="file${file.key}">
        <div onMouseOver="this.className='boxlistboxhighlight'" onMouseOut="this.className='boxlistbox'" class="boxlistbox">
          <a href="<c:url value='/miso/download/project/${project.id}/${file.key}'/>">${file.value}</a>
          <c:if test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                          or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
          <a href='#' onclick="Project.ui.deleteProjectFile('${project.id}', '${file.value}', '${file.key}');">
            <i class="fa fa-trash-o fa-lg fa-fw pull-right" style="padding-top:4px"></i>
          </a>
          </c:if>
        </div>
      </div>
    </c:forEach>
  </div>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#studies_arrowclick'), 'studiesdiv');">
  ${fn:length(project.studies)} Studies
  <div id="studies_arrowclick" class="toggleLeft"></div>
</div>
<div id="studiesdiv" class="panel panel-default padded-panel" style="display:none;">
  <nav id="navbar-stu" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(project.studies)} Studies</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="stu-menu" class="dropdown">
          <a id="studrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="studrop1">
            <li role="presentation"><a href='<c:url value="/miso/study/new/${project.id}"/>'>Add new Study</a></li>
            <li role="presentation"><a href='<c:url value="/miso/experimentwizard/new/${project.id}"/>'>Create Experiments</a></li>
            <li role="presentation"><a href='<c:url value="/miso/poolwizard/new/${project.id}"/>'>Create Pools</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <table class="table table-striped table-bordered" id="study_table">
      <thead>
      <tr>
        <th>Study Name</th>
        <th>Study Alias</th>
        <th class="fit">Edit</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${project.studies}" var="study">
        <tr studyId="${study.id}" onMouseOver="this.className='highlightrow'"
            onMouseOut="this.className='normalrow'">
          <td><b>${study.name}</b></td>
          <td>${study.alias}</td>
          <td class="misoicon" onclick="window.location.href='<c:url value="/miso/study/${study.id}"/>'">
            <span class="fa fa-pencil-square-o fa-lg"/>
          </td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Study.deleteStudy(${study.id}, Utils.page.pageReload);">
              <span class="fa fa-trash-o fa-lg"/>
            </td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#study_table').dataTable({
          "aaSorting": [
            [1, 'asc']
          ],
          "aoColumns": [
            null,
            { "sType": 'natural' },
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#samples_arrowclick'), 'samplesdiv');">
  ${fn:length(project.samples)} Samples
  <div id="samples_arrowclick" class="toggleLeft"></div>
</div>
<div id="samplesdiv" class="panel panel-default padded-panel" style="display:none;">

  <div id="sampletabs">
    <ul>
      <li><a href="#samtab-1"><span>All</span></a></li>
      <c:if test="${not empty project.overviews}">
        <li><a href="#samtab-2"><span>By Group</span></a></li>
      </c:if>
    </ul>

    <div id="samtab-1">
      <nav id="navbar-sam" class="navbar navbar-default navbar-static" role="navigation">
        <div class="navbar-header">
          <span class="navbar-brand navbar-center">${fn:length(project.samples)} Samples</span>
        </div>
        <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
          <ul class="nav navbar-nav navbar-right">
            <li id="sam-menu" class="dropdown">
              <a id="samdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
              <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="samdrop1">
                <li role="presentation"><a href='<c:url value="/miso/sample/new/${project.id}"/>'>Add Samples</a></li>
                <li role="presentation"><a href="javascript:void(0);" onclick="Project.ui.receiveSamples('#sample_table');">Receive Samples</a></li>
                <li role="presentation"><a href="javascript:void(0);" onclick="bulkSampleQcTable('#sample_table');">QC Samples</a></li>
                <li role="presentation"><a href="javascript:void(0);" onclick="Project.barcode.selectSampleBarcodesToPrint('#sample_table');">Print Barcodes</a></li>
                <li role="presentation" class="divider"></li>
                <li role="presentation"><a href="javascript:void(0);" onclick="getBulkSampleInputForm(${project.id});">Get Bulk Sample Input Form</a></li>
                <li role="presentation"><a href="javascript:void(0);" onclick="Project.ui.uploadBulkSampleInputForm();">Import Bulk Sample Input Form</a></li>
                <c:if test="${not empty project.samples}">
                  <li role="presentation" class="divider"></li>
                  <li role="presentation"><a href='<c:url value="/miso/importexport/exportsamplesheet"/>'>Export Sample QC Sheet</a></li>
                  <li role="presentation"><a href='<c:url value="/miso/importexport/importsamplesheet"/>'>Import Sample QC Sheet</a></li>
                  <li role="presentation" class="divider"></li>
                  <li role="presentation"><a href="javascript:void(0);" onclick="generateSampleDeliveryForm('#sample_table', ${project.id});">Get Information Form</a></li>
                  <li role="presentation"><a href="javascript:void(0);" onclick="Project.ui.uploadSampleDeliveryForm();">Import Information Form</a></li>
                  <li role="presentation" class="divider"></li>
                  <li role="presentation"><a href='<c:url value="/miso/library/new/${project.samples[0].id}#tab-2"/>'>Add Libraries</a></li>
                  <li role="presentation"><a href='<c:url value="/miso/importexport/importlibrarypoolsheet"/>'>Import Library Sheet</a></li>
                </c:if>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </nav>

      <span style="clear:both">
        <div id="deliveryformdiv" class="simplebox" style="display:none;">
          <table class="in">
            <tr>
              <td>
                <form method='post'
                      id='deliveryform_upload_form'
                      action='<c:url value="/miso/upload/project/sample-delivery-form"/>'
                      enctype="multipart/form-data"
                      target="deliveryform_target_upload"
                      onsubmit="Utils.fileUpload.fileUploadProgress('deliveryform_upload_form', 'deliveryform_statusdiv', Project.ui.deliveryFormUploadSuccess);">
                  <input type="hidden" name="projectId" value="${project.id}"/>
                  <input type="file" name="file"/>
                  <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                  <button type="button" class="br-button ui-state-default ui-corner-all" onclick="Project.ui.cancelSampleDeliveryFormUpload();">
                    Cancel
                  </button>
                </form>
                <iframe id='deliveryform_target_upload' name='deliveryform_target_upload' src='' style='display: none'></iframe>
                <div id="deliveryform_statusdiv"></div>
              </td>
            </tr>
          </table>
        </div>

        <div id="inputformdiv" class="simplebox" style="display:none;">
          <table class="in">
            <tr>
              <td>
                <form method='post'
                      id='inputform_upload_form'
                      action='<c:url value="/miso/upload/project/bulk-input-form"/>'
                      enctype="multipart/form-data"
                      target="inputform_target_upload"
                      onsubmit="Utils.fileUpload.fileUploadProgress('inputform_upload_form', 'inputform_statusdiv', Project.ui.bulkSampleInputFormUploadSuccess);">
                  <input type="hidden" name="projectId" value="${project.id}"/>
                  <input type="file" name="file"/>
                  <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                  <button type="button" class="br-button ui-state-default ui-corner-all" onclick="Project.ui.cancelBulkSampleInputFormUpload();">
                    Cancel
                  </button>
                </form>
                <iframe id='inputform_target_upload' name='inputform_target_upload' src='' style='display: none'></iframe>
                <div id="inputform_statusdiv"></div>
              </td>
            </tr>
          </table>
        </div>

        <div id="plateformdiv" class="simplebox" style="display:none;">
          <table class="in">
            <tr>
              <td>
                <form method='post'
                      id='plateform_upload_form'
                      action='<c:url value="/miso/upload/project/plate-form"/>'
                      enctype="multipart/form-data"
                      target="plateform_target_upload"
                      onsubmit="Utils.fileUpload.fileUploadProgress('plateform_upload_form', 'plateform_statusdiv', Project.ui.plateInputFormUploadSuccess);">
                  <input type="hidden" name="projectId" value="${project.id}"/>
                  <input type="file" name="file"/>
                  <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                  <button type="button" class="br-button ui-state-default ui-corner-all" onclick="Project.ui.cancelPlateInputFormUpload();">
                    Cancel
                  </button>
                </form>
                <iframe id='plateform_target_upload' name='plateform_target_upload' style='display: none'></iframe>
                <div id="plateform_statusdiv"></div>
                <div id="plateform_import"></div>
              </td>
            </tr>
          </table>
        </div>

        <table class="table table-striped table-bordered" id="sample_table">
          <thead>
          <tr>
            <th>Sample Name</th>
            <th>Sample Alias</th>
            <th>Sample Description</th>
            <th>Type</th>
            <th>Received Date</th>
            <th>QC Passed</th>
            <th>QC Result</th>
            <th class="fit">Edit</th>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <th class="fit">DELETE</th>
            </sec:authorize>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${project.samples}" var="sample">
            <tr sampleId="${sample.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td><b>${sample.name}</b></td>
              <td>${sample.alias}</td>
              <td>${sample.description}</td>
              <td>${sample.sampleType}</td>
              <td>${sample.receivedDate}</td>
              <td>${sample.qcPassed}</td>
              <td>${sample.id}</td>
              <td class="misoicon" onclick="window.location.href='<c:url value="/miso/sample/${sample.id}"/>'">
                <span class="fa fa-pencil-square-o fa-lg"/>
              </td>
              <sec:authorize access="hasRole('ROLE_ADMIN')">
                <td class="misoicon" onclick="Sample.deleteSample(${sample.id}, Utils.page.pageReload);">
                  <span class="fa fa-trash-o fa-lg"/>
                </td>
              </sec:authorize>
            </tr>
          </c:forEach>
          </tbody>
        </table>
        <script type="text/javascript">
          jQuery(document).ready(function () {
            jQuery('#sample_table').dataTable({
              "aaSorting": [
                [1, 'asc']
              ],
              "aoColumns": [
                null,
                { "sType": 'natural' },
                { "sType": 'natural' },
                null,
                null,
                null,
                { "sType": 'natural' },
                {"bSortable": false}
                <sec:authorize access="hasRole('ROLE_ADMIN')">, {"bSortable": false}</sec:authorize>
              ],
              "iDisplayLength": 50,
              "bJQueryUI": false,
              "bRetrieve": true,

              "fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                Fluxion.doAjax(
                  'sampleControllerHelperService',
                  'getSampleLastQCRequest',
                  {
                    'sampleId': aData[6],
                    'url': ajaxurl
                  },
                  {'doOnSuccess': function (json) {
                    jQuery('td:eq(6)', nRow).html(json.response);
                  }
                  }
                );
              }
            });
          });
        </script>
      </span>
    </div>

    <div id="samtab-2">
      <c:forEach items="${project.overviews}" var="overview" varStatus="ov">
        <c:if test="${not empty overview.sampleGroup}">
          <div id="overviewsamdiv${overview.overviewId}" class="ui-corner-all simplebox">
            <nav id="navbar-gsam" class="navbar navbar-default navbar-static" role="navigation">
              <div class="navbar-header">
                <span class="navbar-brand navbar-center">Group ${overview.sampleGroup.id} Samples</span>
              </div>
              <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
                <ul class="nav navbar-nav navbar-right">
                  <li id="gsam-menu" class="dropdown">
                    <c:if test="${not empty overview.sampleGroup.entities}">
                      <a id="gsamdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
                      <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="gsamdrop1">
                        <li role="presentation"><a href="javascript:void(0);" onclick="Project.ui.receiveSamples('#overview_samplegroup_table_'+${overview.id});">Receive Samples</a></li>
                        <li role="presentation"><a href="javascript:void(0);" onclick="bulkSampleQcTable('#overview_samplegroup_table_'+${overview.id});">QC Samples</a></li>
                        <li role="presentation"><a href="javascript:void(0);" onclick="Project.barcode.selectSampleBarcodesToPrint('#overview_samplegroup_table_'+${overview.id});">Print Barcodes</a></li>
                        <li role="presentation" class="divider"></li>
                        <li role="presentation"><a href='<c:url value="/miso/importexport/exportsamplesheet"/>'>Export Sample QC Sheet</a></li>
                        <li role="presentation"><a href='<c:url value="/miso/importexport/importsamplesheet"/>'>Import Sample QC Sheet</a></li>
                        <li role="presentation"><a href="javascript:void(0);" onclick="generateSampleDeliveryForm('#overview_samplegroup_table_'+${overview.id}, ${project.id});">Get Information Form</a></li>
                        <li role="presentation"><a href="javascript:void(0);" onclick="Project.ui.uploadSampleDeliveryForm();">Import Information Form</a></li>
                        <li role="presentation" class="divider"></li>
                        <li role="presentation"><a href='<c:url value="/miso/importexport/importlibrarypoolsheet"/>'>Import Library Sheet</a></li>
                      </ul>
                    </c:if>
                  </li>
                </ul>
              </div>
            </nav>

            <span style="clear:both">
              <table class="table table-striped table-bordered" id="overview_samplegroup_table_${overview.id}">
                <thead>
                <tr>
                  <th>Sample Name</th>
                  <th>Sample Alias</th>
                  <th>Sample Description</th>
                  <th>Type</th>
                  <th>Received Date</th>
                  <th>QC Passed</th>
                  <th>QC Result</th>
                  <th class="fit">Edit</th>
                  <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <th class="fit">DELETE</th>
                  </sec:authorize>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${overview.sampleGroup.entities}" var="sample">
                  <tr sampleId="${sample.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                    <td><b>${sample.name}</b></td>
                    <td>${sample.alias}</td>
                    <td>${sample.description}</td>
                    <td>${sample.sampleType}</td>
                    <td>${sample.receivedDate}</td>
                    <td>${sample.qcPassed}</td>
                    <td>${sample.id}</td>
                    <td class="misoicon" onclick="window.location.href='<c:url value="/miso/sample/${sample.id}"/>'">
                      <span class="fa fa-pencil-square-o fa-lg"/>
                    </td>
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                      <td class="misoicon" onclick="Sample.deleteSample(${sample.id}, Utils.page.pageReload);">
                        <span class="fa fa-trash-o fa-lg"/>
                      </td>
                    </sec:authorize>
                  </tr>
                </c:forEach>
                </tbody>
              </table>
              <script type="text/javascript">
                jQuery(document).ready(function () {
                  jQuery('#overview_samplegroup_table_'+${overview.id}).dataTable({
                    "aaSorting": [
                      [1, 'asc']
                    ],
                    "aoColumns": [
                      null,
                      { "sType": 'natural' },
                      { "sType": 'natural' },
                      null,
                      null,
                      null,
                      null,
                      null
                      <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
                    ],
                    "iDisplayLength": 50,
                    "bJQueryUI": false,
                    "bRetrieve": true,

                    "fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                      Fluxion.doAjax(
                        'sampleControllerHelperService',
                        'getSampleLastQCRequest',
                        {
                          'sampleId': aData[6],
                          'url': ajaxurl
                        },
                        {'doOnSuccess': function (json) {
                          jQuery('td:eq(6)', nRow).html(json.response);
                        }
                        }
                      );
                    }
                  });
                });
              </script>
            </span>
          </div>
        </c:if>
      </c:forEach>
    </div>
  </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#sampletabs").tabs();
    });
  </script>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#libraries_arrowclick'), 'librariesdiv');">
  ${fn:length(projectLibraries)} Libraries
  <div id="libraries_arrowclick" class="toggleLeft"></div>
</div>

<div id="librariesdiv" class="panel panel-default padded-panel" style="display:none;">
  <div id="librarytabs">
    <ul>
      <li><a href="#libtab-1"><span>All</span></a></li>
      <c:if test="${not empty project.overviews}">
        <li><a href="#libtab-2"><span>By Group</span></a></li>
      </c:if>
    </ul>

    <div id="libtab-1">
      <a name="library"></a>
      <nav id="navbar-lib" class="navbar navbar-default navbar-static" role="navigation">
        <div class="navbar-header">
          <span class="navbar-brand navbar-center">${fn:length(projectLibraries)} Libraries</span>
        </div>
        <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
          <ul class="nav navbar-nav navbar-right">
            <li id="lib-menu" class="dropdown">
              <a id="libdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
              <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="libdrop1">
                <c:if test="${not empty project.samples}">
                  <li role="presentation"><a href='<c:url value="/miso/library/new/${project.samples[0].id}#tab-2"/>'>Add Libraries</a></li>
                </c:if>

                <c:if test="${not empty projectLibraries}">
                  <li role="presentation"><a href="javascript:void(0);" onclick="bulkLibraryQcTable('#library_table');">QC these Libraries</a></li>
                  <li role="presentation"><a href="javascript:void(0);" onclick="bulkLibraryDilutionTable('#library_table');">Add Library Dilutions</a></li>
                  <li role="presentation"><a href="javascript:void(0);" onclick="Project.barcode.selectLibraryBarcodesToPrint('#library_table');">Print Barcodes</a></li>
                </c:if>
              </ul>
            </li>
          </ul>
        </div>
      </nav>

      <span style="clear:both">
        <table class="table table-striped table-bordered" id="library_table">
          <thead>
          <tr>
            <th>Library Name</th>
            <th>Library Alias</th>
            <th>Date</th>
            <th>Library Description</th>
            <th>Library Type</th>
            <th>Library Platform</th>
            <th>Tag Barcodes</th>
            <th>Insert Size</th>
            <th>QC Passed</th>
            <th class="fit">Edit</th>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <th class="fit">DELETE</th>
            </sec:authorize>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${projectLibraries}" var="library">
            <tr libraryId="${library.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td><b>${library.name}</b></td>
              <td>${library.alias}</td>
              <td>${library.creationDate}</td>
              <td>${library.description}</td>
              <td>${library.libraryType.description}</td>
              <td>${library.platformName}</td>
              <td><c:if test="${not empty library.tagBarcodes}">
                <c:forEach items="${library.tagBarcodes}" varStatus="status" var="barcodemap">
                  ${status.count}: ${barcodemap.value.name} (${barcodemap.value.sequence})
                  <c:if test="${status.count lt fn:length(library.tagBarcodes)}">
                    <br/>
                  </c:if>
                </c:forEach>
              </c:if></td>
              <td><c:forEach var="qc" items="${library.libraryQCs}" end="0">${qc.insertSize}</c:forEach></td>
              <td>${library.qcPassed}</td>
              <td class="misoicon" onclick="window.location.href='<c:url value="/miso/library/${library.id}"/>'">
                <span class="fa fa-pencil-square-o fa-lg"/>
              </td>
              <sec:authorize access="hasRole('ROLE_ADMIN')">
                <td class="misoicon" onclick="Library.deleteLibrary(${library.id}, Utils.page.pageReload);">
                  <span class="fa fa-trash-o fa-lg"/>
                </td>
              </sec:authorize>
            </tr>
          </c:forEach>
          </tbody>
        </table>
        <script type="text/javascript">
          jQuery(document).ready(function () {
            jQuery('#library_table').dataTable({
              "aaSorting": [
                [1, 'asc']
              ],
              "aoColumns": [
                null,
                { "sType": 'natural' },
                { "sType": 'natural' },
                { "sType": 'natural' },
                null,
                null,
                null,
                null,
                null,
                null
                <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
              ],
              "iDisplayLength": 50,
              "bJQueryUI": false,
              "bRetrieve": true
            });
          });
        </script>
      </span>
    </div>
    <div id="libtab-2">
      <c:forEach items="${project.overviews}" var="overview" varStatus="ov">
        <c:if test="${not empty overview.sampleGroup}">
        <div id="overviewlibdiv${overview.overviewId}" class="ui-corner-all simplebox">
          <nav id="navbar-glib" class="navbar navbar-default navbar-static" role="navigation">
            <div class="navbar-header">
              <span class="navbar-brand navbar-center">Group ${overview.sampleGroup.id} Libraries</span>
            </div>
            <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
              <ul class="nav navbar-nav navbar-right">
                <li id="glib-menu" class="dropdown">
                  <a id="glibdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
                  <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="glibdrop1">
                    <c:if test="${not empty project.samples}">
                      <li role="presentation"><a href='<c:url value="/miso/library/new/${project.samples[0].id}#tab-2"/>'>Add Libraries</a></li>
                    </c:if>

                    <c:if test="${not empty projectLibraries}">
                      <li role="presentation"><a href="javascript:void(0);" onclick="bulkLibraryQcTable('#overview_librarygroup_table_${overview.id}');">QC these Libraries</a></li>
                      <li role="presentation"><a href="javascript:void(0);" onclick="bulkLibraryDilutionTable('#overview_librarygroup_table_${overview.id}');">Add Library Dilutions</a></li>
                      <li role="presentation"><a href="javascript:void(0);" onclick="Project.barcode.selectLibraryBarcodesToPrint('#overview_librarygroup_table_${overview.id}');">Print Barcodes</a></li>
                    </c:if>
                  </ul>
                </li>
              </ul>
            </div>
          </nav>

          <span style="clear:both">
            <c:if test="${not empty libraryGroupMap[overview.id]}">
            <table class="table table-striped table-bordered" id="overview_librarygroup_table_${overview.id}">
              <thead>
              <tr>
                <th>Library Name</th>
                <th>Library Alias</th>
                <th>Date</th>
                <th>Library Description</th>
                <th>Library Type</th>
                <th>Library Platform</th>
                <th>Tag Barcodes</th>
                <th>Insert Size</th>
                <th>QC Passed</th>
                <th class="fit">Edit</th>
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                  <th class="fit">DELETE</th>
                </sec:authorize>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${libraryGroupMap[overview.id]}" var="grouplib" varStatus="lg">
                <tr libraryId="${grouplib.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                  <td><b>${grouplib.name}</b></td>
                  <td>${grouplib.alias}</td>
                  <td>${grouplib.creationDate}</td>
                  <td>${grouplib.description}</td>
                  <td>${grouplib.libraryType.description}</td>
                  <td>${grouplib.platformName}</td>
                  <td><c:if test="${not empty grouplib.tagBarcodes}">
                    <c:forEach items="${grouplib.tagBarcodes}" varStatus="status" var="barcodemap">
                      ${status.count}: ${barcodemap.value.name} (${barcodemap.value.sequence})
                      <c:if test="${status.count lt fn:length(grouplib.tagBarcodes)}">
                        <br/>
                      </c:if>
                    </c:forEach>
                  </c:if></td>
                  <td><c:forEach var="qc" items="${grouplib.libraryQCs}" end="0">${qc.insertSize}</c:forEach></td>
                  <td>${grouplib.qcPassed}</td>
                  <td class="misoicon" onclick="window.location.href='<c:url value="/miso/library/${grouplib.id}"/>'">
                    <span class="fa fa-pencil-square-o fa-lg"/>
                  </td>
                  <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <td class="misoicon" onclick="Library.deleteLibrary(${grouplib.id}, Utils.page.pageReload);">
                      <span class="fa fa-trash-o fa-lg"/>
                    </td>
                  </sec:authorize>
                </tr>
              </c:forEach>
              </tbody>
            </table>
            <script type="text/javascript">
              jQuery(document).ready(function () {
                jQuery('#overview_librarygroup_table_'+${overview.id}).dataTable({
                  "aaSorting": [
                    [1, 'asc']
                  ],
                  "aoColumns": [
                    null,
                    { "sType": 'natural' },
                    { "sType": 'natural' },
                    { "sType": 'natural' },
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                    <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
                  ],
                  "iDisplayLength": 50,
                  "bJQueryUI": false,
                  "bRetrieve": true
                });
              });
            </script>
            </c:if>
          </span>
        </div>
        </c:if>
      </c:forEach>
    </div>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery("#librarytabs").tabs();
      });
    </script>
  </div>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#librarydils_arrowclick'), 'librarydilsdiv');">
  ${fn:length(projectLibraryDilutions)} Library Dilutions
  <div id="librarydils_arrowclick" class="toggleLeft"></div>
</div>
<div id="librarydilsdiv" class="panel panel-default padded-panel" style="display:none;">
  <a name="librarydil"></a>
  <nav id="navbar-ldi" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(projectLibraryDilutions)} Library Dilutions</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="ldi-menu" class="dropdown">
          <a id="ldidrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="ldidrop1">
            <c:if test="${not empty projectLibraryDilutions}">
              <c:if test="${existsAnyEmPcrLibrary}">
              <li role="presentation"><a href='javascript:void(0);' onclick='bulkEmPcrTable();'>Add EmPCRs</a></li>
              </c:if>
              <li role="presentation"><a href="javascript:void(0);" onclick="Project.barcode.selectLibraryDilutionBarcodesToPrint('#librarydils_table');">Print Barcodes</a></li>
              <li role="presentation"><a href='<c:url value="/miso/poolwizard/new/${project.id}"/>'>Create Pools</a></li>
            </c:if>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <table class="table table-striped table-bordered" id="librarydils_table">
      <thead>
      <tr>
        <th>Dilution Name</th>
        <th>Parent Library</th>
        <th>Dilution Creator</th>
        <th>Dilution Creation Date</th>
        <th>Dilution Platform</th>
        <th>Dilution Concentration</th>
        <th class="fit">Edit</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectLibraryDilutions}" var="dil">
        <tr dilutionId="${dil.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b>${dil.name}</b></td>
          <td>${dil.library.alias}<c:if test="${not empty dil.library.tagBarcode}">
            (${dil.library.tagBarcode.name})</c:if></td>
          <td>${dil.dilutionCreator}</td>
          <td>${dil.creationDate}</td>
          <td>${dil.library.platformName}</td>
          <td>${dil.concentration}</td>
          <td class="misoicon" onclick="window.location.href='<c:url value="/miso/library/${dil.library.id}"/>'">
            <span class="fa fa-pencil-square-o fa-lg"/>
          </td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Library.dilution.deleteLibraryDilution(${dil.id}, Utils.page.pageReload);">
              <span class="fa fa-trash-o fa-lg"/>
            </td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#librarydils_table').dataTable({
          "aaSorting": [
            [1, 'asc'],
            [3, 'asc']
          ],
          "aoColumns": [
            null,
            { "sType": 'natural' },
            null,
            null,
            null,
            null,
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#pools_arrowclick'), 'poolsdiv');">
  ${fn:length(projectPools)} Pools
  <div id="pools_arrowclick" class="toggleLeft"></div>
</div>
<div id="poolsdiv" class="panel panel-default padded-panel" style="display:none;">
  <a name="pool"></a>
  <nav id="navbar-pool" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(projectPools)} Pools</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="pool-menu" class="dropdown">
          <a id="pooldrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="pooldrop1">
            <c:if test="${not empty projectPools}">
              <c:if test="${existsAnyEmPcrLibrary}">
                <li role="presentation"><a href='javascript:void(0);' onclick="Project.ui.addPoolEmPCR('#pools_table');">Add Pool EmPCR</a></li>
              </c:if>
                <li role="presentation"><a href="javascript:void(0);" onclick="Pool.barcode.selectPoolBarcodesToPrint('#pools_table');">Print Barcodes</a></li>
            </c:if>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <table class="table table-striped table-bordered" id="pools_table">
      <thead>
      <tr>
        <th>Pool Name</th>
        <th>Pool Alias</th>
        <th>Pool Platform</th>
        <th>Pool Creation Date</th>
        <th>Pool Concentration</th>
        <th class="fit">Edit</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectPools}" var="pool">
        <tr poolId="${pool.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b>${pool.name}</b></td>
          <td>${pool.alias}</td>
          <td>${pool.platformType.key}</td>
          <td>${pool.creationDate}</td>
          <td>${pool.concentration}</td>
            <%-- <td class="misoicon" onclick="window.location.href='<c:url value="/miso/pool/${fn:toLowerCase(pool.platformType.key)}/${pool.id}"/>'"><span class="fa fa-pencil-square-o fa-lg"/></td> --%>
          <td class="misoicon" onclick="window.location.href='<c:url value="/miso/pool/${pool.id}"/>'">
            <span class="fa fa-pencil-square-o fa-lg"/>
          </td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Pool.deletePool(${pool.id}, Utils.page.pageReload);">
              <span class="fa fa-trash-o fa-lg"/>
            </td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#pools_table').dataTable({
          "aaSorting": [
            [1, 'asc'],
            [3, 'asc']
          ],
          "aoColumns": [
            null,
            { "sType": 'natural' },
            null,
            null,
            null,
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });
      });
    </script>
  </span>
</div>

<%--
  TODO - only show these options if some of the libraries have the right platform!
   At the moment you can create emPCRs and EmPcrDilutions for Illumina libraries!
--%>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#empcrs_arrowclick'), 'empcrsdiv');">
  ${fn:length(projectEmPcrs)} EmPCRs
  <div id="empcrs_arrowclick" class="toggleLeft"></div>
</div>
<div id="empcrsdiv" class="panel panel-default padded-panel" style="display:none;">
  <a name="empcr"></a>
  <nav id="navbar-empcr" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(projectEmPcrs)} EmPCRs</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <c:if test="${not empty projectEmPcrs}">
          <li id="empcr-menu" class="dropdown">
            <a id="empcrdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="empcrdrop1">
              <li role="presentation"><a href='javascript:void(0);' onclick='bulkEmPcrDilutionTable();'>Add EmPCR Dilutions</a></li>
            </ul>
          </li>
        </c:if>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <table class="table table-striped table-bordered" id="empcrs_table">
      <thead>
      <tr>
        <th>EmPCR Name</th>
        <th>Library Dilution</th>
        <th>EmPCR Creator</th>
        <th>EmPCR Creation Date</th>
        <th>EmPCR Concentration</th>
        <th class="fit">Edit</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectEmPcrs}" var="pcr">
        <tr pcrId="${pcr.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b>${pcr.name}</b></td>
          <td>${pcr.libraryDilution.name}</td>
          <td>${pcr.pcrCreator}</td>
          <td>${pcr.creationDate}</td>
          <td>${pcr.concentration}</td>
          <td class="misoicon" onclick="window.location.href='<c:url value="/miso/library/${pcr.libraryDilution.library.id}"/>'">
            <span class="fa fa-pencil-square-o fa-lg"/>
          </td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Library.empcr.deleteEmPCR(${pcr.id}, Utils.page.pageReload);">
              <span class="fa fa-trash-o fa-lg"/>
            </td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#empcrs_table').dataTable({
          "aaSorting": [
            [1, 'asc'],
            [3, 'asc']
          ],
          "aoColumns": [
            { "sType": 'natural' },
            { "sType": 'natural' },
            null,
            null,
            null,
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#empcrdils_arrowclick'), 'empcrdilsdiv');">
  ${fn:length(projectEmPcrDilutions)} EmPCR Dilutions
  <div id="empcrdils_arrowclick" class="toggleLeft"></div>
</div>
<div id="empcrdilsdiv" class="panel panel-default padded-panel" style="display:none;">
  <a name="empcrdil"></a>
  <nav id="navbar-edi" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(projectEmPcrDilutions)} EmPCR Dilutions</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <c:if test="${not empty projectEmPcrDilutions}">
          <li id="edi-menu" class="dropdown">
            <a id="edidrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="edidrop1">
              <li role="presentation"><a href='<c:url value="/miso/poolwizard/new/${project.id}"/>'>Create Pools</a></li>
            </ul>
          </li>
        </c:if>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <table class="table table-striped table-bordered" id="empcrdils_table">
      <thead>
      <tr>
        <th>Dilution Name</th>
        <th>Dilution Creator</th>
        <th>Dilution Creation Date</th>
        <th>Dilution Concentration</th>
        <th class="fit">Edit</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectEmPcrDilutions}" var="dil">
        <tr dilutionId="${dil.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b>${dil.name}</b></td>
          <td>${dil.dilutionCreator}</td>
          <td>${dil.creationDate}</td>
          <td>${dil.concentration}</td>
          <td class="misoicon" onclick="window.location.href='<c:url value="/miso/library/${dil.library.id}"/>'">
            <span class="fa fa-pencil-square-o fa-lg"/>
          </td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Library.empcr.deleteEmPCRDilution(${dil.id}, Utils.page.pageReload);">
              <span class="fa fa-trash-o fa-lg"/>
            </td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#empcrdils_table').dataTable({
          "aaSorting": [
            [2, 'asc']
          ],
          "aoColumns": [
            null,
            null,
            null,
            null,
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#plates_arrowclick'), 'platesdiv');">
  ${fn:length(projectPlates)} Plates
  <div id="plates_arrowclick" class="toggleLeft"></div>
</div>
<div id="platesdiv" class="panel panel-default padded-panel" style="display:none;">
  <a name="plate"></a>
  <nav id="navbar-pla" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(projectPlates)} Plates</span>
    </div>
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="pla-menu" class="dropdown">
          <a id="pladrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="pladrop1">
            <li role="presentation"><a href="<c:url value="/miso/plate/import"/>">Import Plate Sheet</a></li>
            <c:if test="${not empty projectPlates}">
              <li role="presentation"><a href="javascript:void(0);" onclick="Plate.barcode.selectPlateBarcodesToPrint('#plates_table');">Print Barcodes</a></li>
            </c:if>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <span style="clear:both">
    <table class="table table-striped table-bordered" id="plates_table">
      <thead>
      <tr>
        <th>Plate Name</th>
        <th>Plate Size</th>
        <th>Plate Creation Date</th>
        <th class="fit">Edit</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectPlates}" var="plate">
        <tr poolId="${plate.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b>${plate.name}</b></td>
          <td>${plate.size}</td>
          <td>${plate.creationDate}</td>
            <%-- <td class="misoicon" onclick="window.location.href='<c:url value="/miso/pool/${fn:toLowerCase(pool.platformType.key)}/${pool.id}"/>'"><span class="fa fa-pencil-square-o fa-lg"/></td> --%>
          <td class="misoicon" onclick="window.location.href='<c:url value="/miso/plate/${plate.id}"/>'">
            <span class="fa fa-pencil-square-o fa-lg"/>
          </td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Plate.deletePlate(${plate.id}, Utils.page.pageReload);">
              <span class="fa fa-trash-o fa-lg"/>
            </td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#plates_table').dataTable({
          "aaSorting": [
            [0, 'asc'],
            [2, 'asc']
          ],
          "aoColumns": [
            null,
            null,
            null,
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": false,
          "bRetrieve": true
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#runs_arrowclick'), 'runsdiv');">
  ${fn:length(projectRuns)} Runs
  <div id="runs_arrowclick" class="toggleLeft"></div>
</div>
<div id="runsdiv" class="panel panel-default padded-panel" style="display:none;">
  <nav id="navbar-run" class="navbar navbar-default navbar-static" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">${fn:length(projectRuns)} Runs</span>
    </div>
    <%--
    <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li id="run-menu" class="dropdown">
          <a id="rundrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
          <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="rundrop1">
            <li role="presentation"></li>
          </ul>
        </li>
      </ul>
    </div>
    --%>
  </nav>

  <table class="table table-striped table-bordered" id="run_table">
    <thead>
    <tr>
      <th>Run Name</th>
      <th>Run Alias</th>
      <th>Partitions</th>
      <th class="fit">Edit</th>
      <sec:authorize access="hasRole('ROLE_ADMIN')">
        <th class="fit">DELETE</th>
      </sec:authorize>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${projectRuns}" var="run" varStatus="runCount">
      <tr runId="${run.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td><b>${run.name}</b></td>
        <td>${run.alias}</td>
        <td>
          <c:forEach items="${run.sequencerPartitionContainers}" var="container" varStatus="fCount">
            <table class="containerSummary">
              <tr>
                <c:forEach items="${container.partitions}" var="partition">
                  <td id="partition${runCount.count}_${fCount.count}_${partition.partitionNumber}"
                      class="smallbox">${partition.partitionNumber}</td>
                  <c:forEach items="${partition.pool.experiments}" var="experiment">
                    <c:if test="${experiment.study.project.id eq project.id}">
                      <script type="text/javascript">
                        jQuery(document).ready(function () {
                          jQuery('#partition${runCount.count}_${fCount.count}_${partition.partitionNumber}').addClass("partitionOccupied");
                        });
                      </script>
                    </c:if>
                  </c:forEach>
                </c:forEach>
              </tr>
            </table>
            <c:if test="${fn:length(run.sequencerPartitionContainers) > 1}">
              <br/>
            </c:if>
          </c:forEach>
        </td>
        <td class="misoicon" onclick="window.location.href='<c:url value="/miso/run/${run.id}"/>'">
          <span class="fa fa-pencil-square-o fa-lg"/>
        </td>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <td class="misoicon" onclick="Run.deleteRun(${run.id}, Utils.page.pageReload);">
            <span class="fa fa-trash-o fa-lg"/>
          </td>
        </sec:authorize>
      </tr>
    </c:forEach>
    </tbody>
  </table>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery('#run_table').dataTable({
        "aaSorting": [
          [0, 'asc'],
          [1, 'asc']
        ],
        "aoColumns": [
          null,
          null,
          null,
          null
          <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
        ],
        "iDisplayLength": 50,
        "bJQueryUI": false,
        "bRetrieve": true
      });
    });
  </script>
</div>
</c:when>
</c:choose>

<div id="addProjectOverviewDialog" title="Create new Overview"></div>
<div id="addProjectOverviewNoteDialog" title="Create new Note"></div>
<div id="getBulkSampleInputFormDialog" title="Get Bulk Sample Input Form"></div>
<div id="getPlateInputFormDialog" title="Get Plate Input Form"></div>

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

  <c:if test="${project.id != 0}">
  Project.issues.getProjectIssues(${project.id});
  </c:if>
});

</script>

<c:if test="${not empty project.samples}">
    <script type="text/javascript">
        var projectId_sample = ${project.id};
        var sampleQcTypesString = {${sampleQcTypesString}};
    </script>
    <script src="<c:url value='/scripts/editProject_sample.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
</c:if>

<c:if test="${not empty projectLibraries}">
    <script type="text/javascript">
        var libraryQcTypesString = {${libraryQcTypesString}};
    </script>
    <script src="<c:url value='/scripts/editProject_library.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
</c:if>

<c:if test="${existsAnyEmPcrLibrary and not empty projectLibraryDilutions}">
    <script src="<c:url value='/scripts/editProject_libraryDilution.js?ts=${timestamp.time}'/>"
            type="text/javascript"></script>
</c:if>

<c:if test="${not empty projectEmPcrs}">
    <script src="<c:url value='/scripts/editProject_empcr.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
</c:if>

<c:if test="${project.id != 0}">
    <script type="text/javascript">
        var projectId_d3graph = ${project.id};
    </script>
    <script src="<c:url value='/scripts/editProject_existing.js?ts=${timestamp.time}'/>"
            type="text/javascript"></script>
</c:if>

</div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>