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

<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.radio.js'/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.popup.js'/>"></script>

<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/styles/progress.css'/>" type="text/css">

<div id="maincontent">
<div id="contentcolumn">
<form:form id="project-form" data-parsley-validate="" action="/miso/project" method="POST" commandName="project" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="project"/>
<h1><c:choose><c:when
    test="${project.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
  Project
  <button type="button" class="fg-button ui-state-default ui-corner-all" onclick="Project.validateProject();">
    Save
  </button>
</h1>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Project contains information about a set of Studies that may
  comprise many different Samples, Experiments and Runs. Samples are attached to Projects as they are often
  processed into Dilutions, which are then Pooled and sequenced.<br/>Projects also have Overviews, which hold
  information about a Project proposal.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid!</p>
</div>

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
    <td><form:input id="alias" path="alias" maxlength="${maxLengths['alias']}" class="validateable"/>
      <span id="aliascounter" class="counter"></span>
    </td>
  </tr>
  <tr>
    <td class="h">Short Name:</td>
    <td><form:input id="shortName" path="shortName" maxlength="${maxLengths['shortName']}" class="validateable"/>
      <span id="shortNamecounter" class="counter"></span>
    </td>
  </tr>
  <tr>
    <td class="h">Description:</td>
    <td><form:input id="description" path="description" maxlength="${maxLengths['description']}" class="validateable"/>
      <span id="descriptioncounter" class="counter"></span></td>
  </tr>
  <tr>
    <td>Progress:*</td>
    <td>
      <c:choose>
        <c:when test="${(project.securityProfile.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
          <div id="progressButtons">
            <form:radiobuttons id="progress" path="progress"/>
          </div>
        </c:when>
        <c:otherwise>
          ${project.progress}
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
      <td></td>
      <td>
        <div class="parsley-errors-list filled" id="progressSelectError">
          <div class="parsley-required"></div>
        </div>
      </td>
  </tr>
  <tr>
    <td>Reference Genome :*</td>
    <td>
        <form:select id="referenceGenome" path="referenceGenome">
            <form:options items="${referenceGenome}" itemValue="id" itemLabel="alias"/>
        </form:select>
    </td>
  </tr>
</table>

<script type="text/javascript">
  jQuery(document).ready(function () {
    // Attaches a Parsley form validator
    Validate.attachParsley('#project-form');
  });
</script>

<div id="printServiceSelectDialog" title="Select a Printer"></div>

<div id="projectoverviews">
<c:if test="${project.id != 0}">
  <a class="add" href="javascript:void(0);" onclick="Project.overview.showProjectOverviewDialog(${project.id});">Add
    Overview</a><br/>
</c:if>
<c:choose>
<c:when test="${not empty project.overviews}">

<c:forEach items="${project.overviews}" var="overview" varStatus="ov">
<div id="overviewdiv${overview.id}" class="ui-corner-all simplebox">

<script type="text/javascript">
  jQuery(document).ready(function () {
    //show watchers list
    Project.alert.listWatchOverview(${overview.id});
  });
</script>

<div style="float:right;margin:5px;">
  <div class="breadcrumbsbubbleInfo">
    <div class="trigger"><c:choose>
      <c:when test="${not empty overviewMap[overview.overviewId]}">
        <a href='javascript:void(0);' onclick="Project.alert.unwatchOverview(${overview.id});">Stop
          watching</a>
      </c:when>
      <c:otherwise>
        <a href='javascript:void(0);' onclick="Project.alert.watchOverview(${overview.id});">Watch</a>
      </c:otherwise>
    </c:choose>
      |
      (Watchers)
    </div>
    <div class="breadcrumbspopup">
      <div id="watchersList${overview.id}"></div>
    </div>
  </div>
</div>
<table class="list" id="overview">
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
            <a href="javascript:void(0);" onclick="Project.overview.unlockProjectOverview(${overview.id})">
              <img style="border:0;" alt="Unlock" title="Unlock this overview" src="<c:url value='/styles/images/lock_closed.png'/>"/>
            </a>
          </td>
        </c:when>
        <c:otherwise>
          <td style="text-align:center;">
            <a href="javascript:void(0);" onclick="Project.overview.lockProjectOverview(${overview.id})">
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
          <form:input path="overviews['${ov.count-1}'].startDate" id="startdatepicker"/>
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
          <form:input path="overviews['${ov.count-1}'].endDate" id="enddatepicker"/>
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
          <form:input path="overviews['${ov.count-1}'].numProposedSamples"
                      id="numProposedSamples${ov.count-1}"/>
        </c:when>
        <c:otherwise>
          ${overview.numProposedSamples}
        </c:otherwise>
      </c:choose>
    </td>
    <td>
        ${fn:length(overview.qcPassedSamples)} / ${overview.numProposedSamples}
      <div id="progressbar${overview.id}"></div>
      <script type="text/javascript">
        jQuery("#progressbar${overview.id}").progressbar({ value: ${fn:length(overview.qcPassedSamples) / overview.numProposedSamples * 100} });
      </script>
    </td>
    <td>
      <c:if test="${not overview.locked}">
        <a onclick="Project.overview.showProjectOverviewNoteDialog(${overview.id});"
           href="javascript:void(0);" class="add">Add Note</a><br/>
      </c:if>
      <c:forEach items="${overview.notes}" var="note" varStatus="n">
        <div class="exppreview" id="overview-notes-${n.count}">
          <b>${note.creationDate}</b>: ${note.text}
          <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
            <c:if test="${(note.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                            or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              <span style="color:#000000">
                <a href='#' onclick="Project.overview.deleteProjectOverviewNote('${overview.id}', '${note.noteId}');">
                  <span class="ui-icon ui-icon-trash" style="clear: both; position: relative; float: right; margin-top: -15px;"></span>
                </a>
              </span>
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
  <c:if test="${empty overview.sampleGroup}">
    <a class="add" href="javascript:void(0);" onclick="Project.overview.addSampleGroupTable(${project.id}, ${overview.id});">Add Sample Group</a><br/>
  </c:if>
  <div id="sampleGroupTableDiv${overview.id}"></div>
</c:if>
<span style="clear:both">
  <c:if test="${not empty overview.sampleGroup}">
  <h1>Sample Group ${overview.sampleGroup.id}</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('samplegroupmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="samplegroupmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
        <a href="javascript:void(0);" onclick="Project.overview.addSamplesToGroupTable(${project.id}, ${overview.id}, ${overview.sampleGroup.id});">Add more Samples</a>
      </div>
    </li>
  </ul>
  <span style="clear:both">
    <table class="list" id="overview_samplegroup_table_${overview.sampleGroup.id}">
      <thead>
      <tr>
        <th>Sample Name</th>
        <th>Sample Alias*</th>
        <th class="fit">REMOVE</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${overview.sampleGroup.entities}" var="sample">
        <tr sampleId="${sample.id}" onMouseOver="this.className='highlightrow'"
            onMouseOut="this.className='normalrow'">
          <td><b><a href="<c:url value='/miso/sample/${sample.id}'/>">${sample.name}</a></b></td>
          <td><a href="<c:url value='/miso/sample/${sample.id}'/>">${sample.alias}</a></td>
          <td class="misoicon" onclick="Sample.removeSampleFromGroup(${sample.id}, ${overview.sampleGroup.id}, Utils.page.pageReload);">
            <span class="ui-icon ui-icon-trash"/>
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
          null
        ],
        "iDisplayLength": 50,
        "bJQueryUI": true,
        "bRetrieve": true,
        "sPaginationType": "full_numbers",
        "fnDrawCallback": function (oSettings) {
          jQuery('#overview_samplegroup_table_'+${overview.sampleGroup.id}+'_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
        }
      });
    });
  </script>
  </c:if>
</span>
</div>
</div>

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
      <input type="text" id="previewKeys" name="previewKeys"/>
      <button type="button" class="br-button ui-state-default ui-corner-all" onclick="Project.issues.previewIssueKeys();">
        Preview Issues
      </button>
      <br/>
    </c:when>
    <c:otherwise>
      To import a project from an issue tracker, please enter an Issue Key to form the basis of this project.
      Enter a SINGLE key, e.g. FOO-1, and click Import to link this project to an external issue.<br/>
      <input type="text" id="previewKey" name="previewKey"/>
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
<div id="simplebox">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#upload_arrowclick'), 'uploaddiv');">
    Project Files
    <div id="upload_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="uploaddiv" class="simplebox" style="display:none;">
    <table class="in">
      <tr>
        <td>
          <form method='post'
                id='ajax_upload_form'
                action="<c:url value="/miso/upload/project"/>"
                enctype="multipart/form-data"
                target="target_upload"
                onsubmit="Utils.fileUpload.fileUploadProgress('ajax_upload_form', 'statusdiv', Project.ui.projectFileUploadSuccess);">
            <input type="hidden" name="projectId" value="${project.id}"/>
            <input type="file" name="file"/>
            <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
          </form>
          <iframe id='target_upload' name='target_upload' src='' style='display: none'></iframe>
          <div id="statusdiv"></div>
        </td>
      </tr>
    </table>
  </div>

  <div id="projectfiles">
    <c:forEach items="${projectFiles}" var="file">
      <div id='btnPanel' style='float: left; width: 32px;'>
        <table>
          <tr>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <td class="misoicon" onclick="Project.ui.deleteFile(${project.id}, ${file.key});">
                <span class="ui-icon ui-icon-trash" />
              </td>
            </sec:authorize>
          </tr>
        </table>
    </div>
    <a class="listbox" href="<c:url value='/miso/download/project/${project.id}/${file.key}'/>">

      <div onMouseOver="this.className='boxlistboxhighlight'" onMouseOut="this.className='boxlistbox'" class="boxlistbox" style='margin-left: 32px;'>
          ${file.value}
      </div>
    </a>
    </c:forEach>
  </div>
</div>
<br/>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#studies_arrowclick'), 'studiesdiv');">
  ${fn:length(project.studies)} Studies
  <div id="studies_arrowclick" class="toggleLeft"></div>
</div>
<div id="studiesdiv" style="display:none;">
  <h1>${fn:length(project.studies)} Studies</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('studymenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="studymenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
        <a href='<c:url value="/miso/study/new/${project.id}"/> '>Add new Study</a>
        <a href='<c:url value="/miso/experimentwizard/new/${project.id}"/> '>Create Experiments</a>
        <a href='<c:url value="/miso/poolwizard/new/${project.id}"/> '>Create Pools</a>
      </div>
    </li>
  </ul>
  <span style="clear:both">
    <table class="list" id="study_table">
      <thead>
      <tr>
        <th>Study Name</th>
        <th>Study Alias</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${project.studies}" var="study">
        <tr studyId="${study.id}" onMouseOver="this.className='highlightrow'"
            onMouseOut="this.className='normalrow'">
          <td><b><a href="<c:url value='/miso/study/${study.id}'/>">${study.name}</a></b></td>
          <td><a href="<c:url value='/miso/study/${study.id}'/>">${study.alias}</a></td>

          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Study.deleteStudy(${study.id}, Utils.page.pageReload);">
              <span class="ui-icon ui-icon-trash"/>
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
            { "sType": 'natural' }
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": true,
          "bRetrieve": true,
          "sPaginationType": "full_numbers",
          "fnDrawCallback": function (oSettings) {
            jQuery('#study_table_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
          }
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#samples_arrowclick'), 'samplesdiv');">
  ${fn:length(project.samples)} Samples
  <div id="samples_arrowclick" class="toggleLeft"></div>
</div>
<div id="samplesdiv" style="display:none;">

  <div id="sampletabs">
    <ul>
      <li><a href="#samtab-1"><span>All</span></a></li>
      <c:if test="${not empty project.overviews}">
        <li><a href="#samtab-2"><span>By Group</span></a></li>
      </c:if>
    </ul>

    <div id="samtab-1">
      <h1>${fn:length(project.samples)} Samples</h1>
      <ul class="sddm">
        <li>
          <a onmouseover="mopen('samplemenu')" onmouseout="mclosetime()">Options
            <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
          </a>

          <div id="samplemenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
            <a href='<c:url value="/miso/sample/new/${project.id}#tab-2"/>'>Add Samples</a>
            <a href="javascript:void(0);" onclick="getBulkSampleInputForm(${project.id});">Get Bulk Sample Input Form</a>
            <a href="javascript:void(0);" onclick="Project.ui.uploadBulkSampleInputForm();">Import Bulk Sample Input Form</a>
            <c:if test="${not empty project.samples}">
              <hr>
              <a href='<c:url value="/miso/importexport/exportsamplesheet"/>'>Export Sample QC Sheet</a>
              <a href='<c:url value="/miso/importexport/importsamplesheet"/>'>Import Sample QC Sheet</a>
              <hr>
              <a href="javascript:void(0);" onclick="generateSampleDeliveryForm('#sample_table', ${project.id});">Get Information Form</a>
              <a href="javascript:void(0);" onclick="Project.ui.uploadSampleDeliveryForm();">Import Information Form</a>
             <hr>
              <a href="javascript:void(0);" onclick="Project.ui.receiveSamples('#sample_table');">Receive Samples</a>
              <a href="javascript:void(0);" onclick="bulkSampleQcTable('#sample_table');">QC Samples</a>
              <a href='<c:url value="/miso/library/new/${project.samples[0].id}#tab-2"/>'>Add Libraries</a>
              <a href='<c:url value="/miso/importexport/importlibrarypoolsheet"/>'>Import Library Sheet</a>
              <a href="javascript:void(0);" onclick="Project.barcode.selectSampleBarcodesToPrint('#sample_table');">Print Barcodes ...</a>
            </c:if>
          </div>
        </li>
      </ul>

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

        <table class="list" id="sample_table">
          <thead>
          <tr>
            <th>Sample Name</th>
            <th>Sample Alias</th>
            <th>Sample Description</th>
            <th>Type</th>
            <th>Received Date</th>
            <th>QC Passed</th>
            <th>QC Result</th>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <th class="fit">DELETE</th>
            </sec:authorize>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${project.samples}" var="sample">
            <tr sampleId="${sample.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td><b><a href="<c:url value='/miso/sample/${sample.id}'/>">${sample.name}</a></b></td>
              <td><a href="<c:url value='/miso/sample/${sample.id}'/>">${sample.alias}</a></td>
              <td>${sample.description}</td>
              <td>${sample.sampleType}</td>
              <td>${sample.receivedDate}</td>
              <td>${sample.qcPassed}</td>
              <td>${sample.id}</td>
              <sec:authorize access="hasRole('ROLE_ADMIN')">
                <td class="misoicon" onclick="Sample.deleteSample(${sample.id}, Utils.page.pageReload);">
                  <span class="ui-icon ui-icon-trash"/>
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
                null
                <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
              ],
              "iDisplayLength": 50,
              "bJQueryUI": true,
              "bRetrieve": true,
              "sPaginationType": "full_numbers",
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
              },
              "fnDrawCallback": function (oSettings) {
                jQuery('#sample_table_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
              }
            });
          });
        </script>
      </span>
    </div>

    <div id="samtab-2">
      <c:forEach items="${project.overviews}" var="overview" varStatus="ov">
        <c:if test="${not empty overview.sampleGroup}">
          <div id="overviewsamdiv${overview.id}" class="ui-corner-all simplebox">
            <h1>Group ${overview.sampleGroup.id} Samples</h1>
            <ul class="sddm">
              <li>
                <a onmouseover="mopen('samplegroupmenu${overview.sampleGroup.id}')" onmouseout="mclosetime()">Options
                  <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
                </a>

                <div id="samplegroupmenu${overview.sampleGroup.id}" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
                  <c:if test="${not empty overview.sampleGroup.entities}">
                    <hr>
                    <a href='<c:url value="/miso/importexport/exportsamplesheet"/>'>Export Sample QC Sheet</a>
                    <a href='<c:url value="/miso/importexport/importsamplesheet"/>'>Import Sample QC Sheet</a>
                    <hr>
                    <a href="javascript:void(0);" onclick="generateSampleDeliveryForm('#overview_samplegroup_table_'+${overview.id}, ${project.id});">Get Information Form</a>
                    <a href="javascript:void(0);" onclick="Project.ui.uploadSampleDeliveryForm();">Import Information Form</a>
                    <hr>
                    <a href="javascript:void(0);" onclick="Project.ui.receiveSamples('#overview_samplegroup_table_'+${overview.id});">Receive Samples</a>
                    <a href="javascript:void(0);" onclick="bulkSampleQcTable('#overview_samplegroup_table_'+${overview.id});">QC Samples</a>
                    <%-- <a href='<c:url value="/miso/library/new/${overview.sampleGroup.entities.id}#tab-2"/>'>Add Libraries</a> --%>
                    <a href='<c:url value="/miso/importexport/importlibrarypoolsheet"/>'>Import Library Sheet</a>
                    <a href="javascript:void(0);" onclick="Project.barcode.selectSampleBarcodesToPrint('#overview_samplegroup_table_'+${overview.id});">Print Barcodes ...</a>
                  </c:if>
                </div>
              </li>
            </ul>

            <span style="clear:both">
              <table class="list" id="overview_samplegroup_table_${overview.id}">
                <thead>
                <tr>
                  <th>Sample Name</th>
                  <th>Sample Alias</th>
                  <th>Sample Description</th>
                  <th>Type</th>
                  <th>Received Date</th>
                  <th>QC Passed</th>
                  <th>QC Result</th>
                  <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <th class="fit">DELETE</th>
                  </sec:authorize>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${overview.sampleGroup.entities}" var="sample">
                  <tr sampleId="${sample.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                    <td><b><a href="<c:url value='/miso/sample/${sample.id}'/>">${sample.name}</a></b></td>
                    <td><a href="<c:url value='/miso/sample/${sample.id}'/>">${sample.alias}</a></td>
                    <td>${sample.description}</td>
                    <td>${sample.sampleType}</td>
                    <td>${sample.receivedDate}</td>
                    <td>${sample.qcPassed}</td>
                    <td>${sample.id}</td>
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                      <td class="misoicon" onclick="Sample.deleteSample(${sample.id}, Utils.page.pageReload);">
                        <span class="ui-icon ui-icon-trash"/>
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
                      null
                      <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
                    ],
                    "iDisplayLength": 50,
                    "bJQueryUI": true,
                    "bRetrieve": true,
                    "sPaginationType": "full_numbers",
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
                    },
                    "fnDrawCallback": function (oSettings) {
                      jQuery('#overview_samplegroup_table_'+${overview.id}+'_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
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

<div id="librariesdiv" style="display:none;">
  <div id="librarytabs">
    <ul>
      <li><a href="#libtab-1"><span>All</span></a></li>
      <c:if test="${not empty project.overviews}">
        <li><a href="#libtab-2"><span>By Group</span></a></li>
      </c:if>
    </ul>

    <div id="libtab-1">
      <a name="library"></a>

      <h1>${fn:length(projectLibraries)} Libraries</h1>
      <ul class="sddm">
        <li>
          <a onmouseover="mopen('librarymenu')" onmouseout="mclosetime()">Options
            <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
          </a>

          <div id="librarymenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
            <c:if test="${not empty project.samples}">
              <a href='<c:url value="/miso/library/new/${project.samples[0].id}#tab-2"/>'>Add Libraries</a>
            </c:if>

            <c:if test="${not empty projectLibraries}">
              <a href="javascript:void(0);" onclick="bulkLibraryQcTable('#library_table');" class="add">QC these Libraries</a>
              <a href="javascript:void(0);" onclick="bulkLibraryDilutionTable('#library_table', '${libraryDilutionUnits}');" class="add">Add Library Dilutions</a>
              <a href="javascript:void(0);" onclick="Project.barcode.selectLibraryBarcodesToPrint('#library_table');">Print Barcodes ...</a>
            </c:if>
          </div>
        </li>
      </ul>

      <span style="clear:both">
        <table class="list" id="library_table">
          <thead>
          <tr>
            <th>Library Name</th>
            <th>Library Alias</th>
            <th>Date</th>
            <th>Library Description</th>
            <th>Library Type</th>
            <th>Library Platform</th>
            <th>Indices</th>
            <th>Insert Size</th>
            <th>QC Passed</th>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <th class="fit">DELETE</th>
            </sec:authorize>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${projectLibraries}" var="library">
            <tr libraryId="${library.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td><b><a href="<c:url value='/miso/library/${library.id}'/>">${library.name}</a></b></td>
              <td><a href="<c:url value='/miso/library/${library.id}'/>">${library.alias}</a></td>
              <td>${library.creationDate}</td>
              <td>${library.description}</td>
              <td>${library.libraryType.description}</td>
              <td>${library.platformName}</td>
              <td><c:if test="${not empty library.indices}">
                <c:forEach items="${library.indices}" varStatus="status" var="index">
                  <c:if test="${status.index gt 0}"><br/></c:if>
                  ${status.count}: ${index.name} (${index.sequence})
                </c:forEach>
              </c:if></td>
              <td><c:forEach var="qc" items="${library.libraryQCs}" end="0">${qc.insertSize}</c:forEach></td>
              <td>${library.qcPassed}</td>
              <sec:authorize access="hasRole('ROLE_ADMIN')">
                <td class="misoicon" onclick="Library.deleteLibrary(${library.id}, Utils.page.pageReload);">
                  <span class="ui-icon ui-icon-trash"/>
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
                null
                <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
              ],
              "iDisplayLength": 50,
              "bJQueryUI": true,
              "bRetrieve": true,
              "sPaginationType": "full_numbers",
              "fnDrawCallback": function (oSettings) {
                jQuery('#library_table_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
              }
            });
          });
        </script>
      </span>
    </div>
    <div id="libtab-2">
      <c:forEach items="${project.overviews}" var="overview" varStatus="ov">
        <c:if test="${not empty overview.sampleGroup}">
        <div id="overviewlibdiv${overview.id}" class="ui-corner-all simplebox">
          <h1>Group ${overview.sampleGroup.id} Libraries</h1>
          <ul class="sddm">
            <li>
              <a onmouseover="mopen('librarygroupmenu${overview.sampleGroup.id}')" onmouseout="mclosetime()">Options
                <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
              </a>

              <div id="librarygroupmenu${overview.sampleGroup.id}" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
                <c:if test="${not empty project.samples}">
                  <a href='<c:url value="/miso/library/new/${project.samples[0].id}#tab-2"/>'>Add Libraries</a>
                </c:if>

                <c:if test="${not empty projectLibraries}">
                  <a href="javascript:void(0);" onclick="bulkLibraryQcTable('#overview_librarygroup_table_${overview.id}');" class="add">QC these Libraries</a>
                  <a href="javascript:void(0);" onclick="bulkLibraryDilutionTable('#overview_librarygroup_table_${overview.id}', '${libraryDilutionUnits}');" class="add">Add Library Dilutions</a>
                  <a href="javascript:void(0);" onclick="Project.barcode.selectLibraryBarcodesToPrint('#overview_librarygroup_table_${overview.id}');">Print Barcodes ...</a>
                </c:if>
              </div>
            </li>
          </ul>

          <span style="clear:both">
            <c:if test="${not empty libraryGroupMap[overview.id]}">
            <table class="list" id="overview_librarygroup_table_${overview.id}">
              <thead>
              <tr>
                <th>Library Name</th>
                <th>Library Alias</th>
                <th>Date</th>
                <th>Library Description</th>
                <th>Library Type</th>
                <th>Library Platform</th>
                <th>Indices</th>
                <th>Insert Size</th>
                <th>QC Passed</th>
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                  <th class="fit">DELETE</th>
                </sec:authorize>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${libraryGroupMap[overview.id]}" var="grouplib" varStatus="lg">
                <tr libraryId="${grouplib.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                  <td><b><a href="<c:url value='/miso/library/${grouplib.id}'/>">${grouplib.name}</a></b></td>
                  <td><a href="<c:url value='/miso/library/${grouplib.id}'/>">${grouplib.alias}</a></td>
                  <td>${grouplib.creationDate}</td>
                  <td>${grouplib.description}</td>
                  <td>${grouplib.libraryType.description}</td>
                  <td>${grouplib.platformName}</td>
                  <td><c:if test="${not empty grouplib.indices}">
                    <c:forEach items="${grouplib.indices}" varStatus="status" var="index">
                       <c:if test="${status.index gt 0}"><br/></c:if>
                       ${status.count}: ${index.name} (${index.sequence})
                    </c:forEach>
                  </c:if></td>
                  <td><c:forEach var="qc" items="${grouplib.libraryQCs}" end="0">${qc.insertSize}</c:forEach></td>
                  <td>${grouplib.qcPassed}</td>
                  <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <td class="misoicon" onclick="Library.deleteLibrary(${grouplib.id}, Utils.page.pageReload);">
                      <span class="ui-icon ui-icon-trash"/>
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
                    null
                    <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
                  ],
                  "iDisplayLength": 50,
                  "bJQueryUI": true,
                  "bRetrieve": true,
                  "sPaginationType": "full_numbers",
                  "fnDrawCallback": function (oSettings) {
                    jQuery('#overview_librarygroup_table_'+${overview.id}+'_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
                  }
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
<div id="librarydilsdiv" style="display:none;">
  <a name="librarydil"></a>

  <h1>${fn:length(projectLibraryDilutions)} Library Dilutions</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('librarydilsmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="librarydilsmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
        <c:if test="${not empty projectLibraryDilutions}">
          <c:if test="${existsAnyEmPcrLibrary}">
            <a href='javascript:void(0);' onclick='bulkEmPcrTable();' class="add">Add EmPCRs</a>
          </c:if>
          <a href="javascript:void(0);" onclick="Project.barcode.selectLibraryDilutionBarcodesToPrint('#librarydils_table');">Print Barcodes ...</a>
          <a href='<c:url value="/miso/poolwizard/new/${project.id}"/>'>Create Pools</a>
        </c:if>
      </div>
    </li>
  </ul>
  <span style="clear:both">
    <table cell-padding="0" width="100%" cellspacing="0" border="0" class="display" id="librarydils_table">
      <thead>
      <tr>
        <th>Dilution Name</th>
        <th>Parent Library</th>
        <th>Dilution Creator</th>
        <th>Dilution Creation Date</th>
        <th>Dilution Platform</th>
        <th>Dilution Concentration (${libraryDilutionUnits})</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectLibraryDilutions}" var="dil">
        <tr dilutionId="${dil.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b><a href="<c:url value='/miso/library/${dil.library.id}'/>">${dil.name}</a></b></td>
          <td><a href="<c:url value='/miso/library/${dil.library.id}'/>">${dil.library.alias}</a>
            <c:if test="${not empty dil.library.indices}">(<c:forEach items="${dil.library.indices}" varStatus="status" var="index"><c:if test="${status.index gt 0}">, </c:if>${index.name}</c:forEach>)</c:if>
          </td>
          <td>${dil.dilutionCreator}</td>
          <td>${dil.creationDate}</td>
          <td>${dil.library.platformName}</td>
          <td>${dil.concentration}</td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Library.dilution.deleteLibraryDilution(${dil.id}, Utils.page.pageReload);">
              <span class="ui-icon ui-icon-trash"/>
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
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": true,
          "bRetrieve": true,
          "sPaginationType": "full_numbers",
          "fnDrawCallback": function (oSettings) {
            jQuery('#librarydils_table_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
          }
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#pools_arrowclick'), 'poolsdiv');">
  ${fn:length(projectPools)} Pools
  <div id="pools_arrowclick" class="toggleLeft"></div>
</div>
<div id="poolsdiv" style="display:none;">
  <a name="pool"></a>

  <h1>${fn:length(projectPools)} Pools</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('poolsmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="poolsmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
        <c:if test="${not empty projectPools}">
          <c:if test="${existsAnyEmPcrLibrary}">
            <a href='javascript:void(0);' onclick="Project.ui.addPoolEmPCR('#pools_table');" class="add">Add Pool EmPCR</a>
          </c:if>
          <a href="javascript:void(0);" onclick="Pool.barcode.selectPoolBarcodesToPrint('#pools_table');">Print Barcodes ...</a>
        </c:if>
      </div>
    </li>
  </ul>
  <span style="clear:both">
    <table class="list" id="pools_table">
      <thead>
      <tr>
        <th>Pool Name</th>
        <th>Pool Alias</th>
        <th>Pool Platform</th>
        <th>Pool Creation Date</th>
        <th>Pool Concentration (${poolConcentrationUnits})</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectPools}" var="pool">
        <tr poolId="${pool.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b><a href="<c:url value='/miso/pool/${pool.id}'/>">${pool.name}</a></b></td>
          <td><a href="<c:url value='/miso/pool/${pool.id}'/>">${pool.alias}</a></td>
          <td>${pool.platformType.key}</td>
          <td>${pool.creationDate}</td>
          <td>${pool.concentration}</td>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Pool.deletePool(${pool.id}, Utils.page.pageReload);">
              <span class="ui-icon ui-icon-trash"/>
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
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": true,
          "bRetrieve": true,
          "sPaginationType": "full_numbers",
          "fnDrawCallback": function (oSettings) {
            jQuery('#pools_table_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
          }
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
<div id="empcrsdiv" style="display:none;">
  <a name="empcr"></a>

  <h1>${fn:length(projectEmPcrs)} EmPCRs</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('empcrsmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="empcrsmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
        <c:if test="${not empty projectEmPcrs}">
          <a href='javascript:void(0);' onclick='bulkEmPcrDilutionTable();' class="add">Add EmPCR Dilutions</a>
        </c:if>
      </div>
    </li>
  </ul>
  <span style="clear:both">
    <table class="list" id="empcrs_table">
      <thead>
      <tr>
        <th>EmPCR Name</th>
        <th>Library Dilution</th>
        <th>EmPCR Creator</th>
        <th>EmPCR Creation Date</th>
        <th>EmPCR Concentration</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectEmPcrs}" var="pcr">
        <tr pcrId="${pcr.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b><a href="<c:url value='/miso/library/${pcr.libraryDilution.library.id}'/>">${pcr.name}</a></b></td>
          <td><a href="<c:url value='/miso/library/${pcr.libraryDilution.library.id}'/>">${pcr.libraryDilution.name}</a></td>
          <td>${pcr.pcrCreator}</td>
          <td>${pcr.creationDate}</td>
          <td>${pcr.concentration}</td>

          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Library.empcr.deleteEmPCR(${pcr.id}, Utils.page.pageReload);">
              <span class="ui-icon ui-icon-trash"/>
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
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": true,
          "bRetrieve": true,
          "sPaginationType": "full_numbers",
          "fnDrawCallback": function (oSettings) {
            jQuery('#empcrs_table_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
          }
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#empcrdils_arrowclick'), 'empcrdilsdiv');">
  ${fn:length(projectEmPcrDilutions)} EmPCR Dilutions
  <div id="empcrdils_arrowclick" class="toggleLeft"></div>
</div>
<div id="empcrdilsdiv" style="display:none;">
  <a name="empcrdil"></a>

  <h1>${fn:length(projectEmPcrDilutions)} EmPCR Dilutions</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('empcrdilsmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>

      <div id="empcrdilsmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
        <c:if test="${not empty projectEmPcrDilutions}">
          <a href='<c:url value="/miso/poolwizard/new/${project.id}"/>'>Create Pools</a>
        </c:if>
      </div>
    </li>
  </ul>
  <span style="clear:both">
    <table class="list" id="empcrdils_table">
      <thead>
      <tr>
        <th>Dilution Name</th>
        <th>Dilution Creator</th>
        <th>Dilution Creation Date</th>
        <th>Dilution Concentration</th>
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <th class="fit">DELETE</th>
        </sec:authorize>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${projectEmPcrDilutions}" var="dil">
        <tr dilutionId="${dil.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><b><a href="<c:url value='/miso/library/${dil.library.id}'/>">${dil.name}</a></b></td>
          <td>${dil.dilutionCreator}</td>
          <td>${dil.creationDate}</td>
          <td>${dil.concentration}</td>

          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <td class="misoicon" onclick="Library.empcr.deleteEmPCRDilution(${dil.id}, Utils.page.pageReload);">
              <span class="ui-icon ui-icon-trash"/>
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
            null
            <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
          ],
          "iDisplayLength": 50,
          "bJQueryUI": true,
          "bRetrieve": true,
          "sPaginationType": "full_numbers",
          "fnDrawCallback": function (oSettings) {
            jQuery('#empcrdils_table_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
          }
        });
      });
    </script>
  </span>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#runs_arrowclick'), 'runsdiv');">
  ${fn:length(projectRuns)} Runs
  <div id="runs_arrowclick" class="toggleLeft"></div>
</div>
<div id="runsdiv" style="display:none;">
  <h1>${fn:length(projectRuns)} Runs</h1>

  <table class="list" id="run_table">
    <thead>
    <tr>
      <th>Run Name</th>
      <th>Run Alias</th>
      <th>Partitions</th>
      <sec:authorize access="hasRole('ROLE_ADMIN')">
        <th class="fit">DELETE</th>
      </sec:authorize>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${projectRuns}" var="run" varStatus="runCount">
      <tr runId="${run.id}" onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
        <td><a href="<c:url value='/miso/run/${run.id}'/>"><b>${run.name}</b></a></td>
        <td><a href="<c:url value='/miso/run/${run.id}'/>">${run.alias}</a></td>
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
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <td class="misoicon" onclick="Run.deleteRun(${run.id}, Utils.page.pageReload);">
            <span class="ui-icon ui-icon-trash"/>
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
          null
          <sec:authorize access="hasRole('ROLE_ADMIN')">, null</sec:authorize>
        ],
        "iDisplayLength": 50,
        "bJQueryUI": true,
        "bRetrieve": true,
        "sPaginationType": "full_numbers",
        "fnDrawCallback": function (oSettings) {
          jQuery('#run_table_paginate').find('.fg-button').addClass('dataTables_paginate_numbers').removeClass('fg-button ui-button');
        }
      });
    });
  </script>
</div>
</c:when>
</c:choose>

<div id="addProjectOverviewDialog" title="Create new Overview"></div>
<div id="addProjectOverviewNoteDialog" title="Create new Note"></div>
<div id="getBulkSampleInputFormDialog" title="Get Bulk Sample Input Form"></div>

<script type="text/javascript">
jQuery(document).ready(function () {
  jQuery('#alias').simplyCountable({
    counter: '#aliascounter',
    countType: 'characters',
    maxCount: ${maxLengths['alias']},
    countDirection: 'down'
  });

  jQuery('#shortName').simplyCountable({
    counter: '#shortNamecounter',
    countType: 'characters',
    maxCount: ${maxLengths['shortName']},
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
      projectId_sample = ${project.id};
      sampleQcTypesString = {${sampleQcTypesString}};
    </script>
</c:if>

<c:if test="${not empty projectLibraries}">
    <script type="text/javascript">
      libraryQcTypesString = {${libraryQcTypesString}};
    </script>
</c:if>

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
