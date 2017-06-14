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
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">
<link rel="stylesheet" href="<c:url value='/styles/progress.css'/>" type="text/css">

<div id="maincontent">
<div id="contentcolumn">
<form:form id="project-form" data-parsley-validate="" action="/miso/project" method="POST" commandName="project" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="project"/>
<h1>
<c:choose><c:when test="${project.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
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
      <c:when test="${not empty overviewMap[overview.id]}">
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
    <c:if test="${overview.allSampleQcPassed}">
      <c:set value="left-progress-done" var="cssSamQcClass"></c:set>
    </c:if>
    <c:if test="${overview.allSampleQcPassed and overview.libraryPreparationComplete}">
      <c:set value="mid-progress-done" var="cssSamQcClass"></c:set>
    </c:if>
    <div class="${cssSamQcClass}">
      <span>Sample QCs</span>
      <form:checkbox value="${overview.allSampleQcPassed}" path="overviews[${ov.count-1}].allSampleQcPassed"/>
    </div>
  </li>

  <li class="lib-prep-step">
    <c:if test="${overview.libraryPreparationComplete}">
      <c:set value="left-progress-done" var="cssLibClass"></c:set>
    </c:if>
    <c:if test="${overview.libraryPreparationComplete and overview.allLibrariesQcPassed}">
      <c:set value="mid-progress-done" var="cssLibClass"></c:set>
    </c:if>
    <div class="${cssLibClass}">
      <span>Libraries prepared</span>
      <form:checkbox value="${overview.libraryPreparationComplete}" path="overviews[${ov.count-1}].libraryPreparationComplete"/>
    </div>
  </li>

  <li class="lib-qc-step">
    <c:if test="${overview.allLibrariesQcPassed}">
      <c:set value="left-progress-done" var="cssLibQcClass"></c:set>
    </c:if>
    <c:if test="${overview.allLibrariesQcPassed and overview.allPoolsConstructed}">
      <c:set value="mid-progress-done" var="cssLibQcClass"></c:set>
    </c:if>
    <div class="${cssLibQcClass}">
      <span>Library QCs</span>
      <form:checkbox value="${overview.allLibrariesQcPassed}" path="overviews[${ov.count-1}].allLibrariesQcPassed"/>
    </div>
  </li>

  <li class="pools-step">
    <c:if test="${overview.allPoolsConstructed}">
      <c:set value="left-progress-done" var="cssPoolsClass"></c:set>
    </c:if>
    <c:if test="${overview.allPoolsConstructed and overview.allRunsCompleted}">
      <c:set value="mid-progress-done" var="cssPoolsClass"></c:set>
    </c:if>
    <div class="${cssPoolsClass}">
      <span>Pools Constructed</span>
      <form:checkbox value="${overview.allPoolsConstructed}" path="overviews[${ov.count-1}].allPoolsConstructed"/>
    </div>
  </li>

  <li class="runs-step">
    <c:if test="${overview.allRunsCompleted}">
      <c:set value="left-progress-done" var="cssRunsClass"></c:set>
    </c:if>
    <c:if test="${overview.allRunsCompleted and overview.primaryAnalysisCompleted}">
      <c:set value="mid-progress-done" var="cssRunsClass"></c:set>
    </c:if>
    <div class="${cssRunsClass}">
      <span>Runs Completed</span>
      <form:checkbox value="${overview.allRunsCompleted}" path="overviews[${ov.count-1}].allRunsCompleted"/>
    </div>
  </li>

  <li class="primary-analysis-step">
    <div class="right ${overview.primaryAnalysisCompleted ? 'mid-progress-done' : ''}">
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
  <div style="clear:both">
    <table class="list" id="overview_samplegroup_table_${overview.id}">
      <thead>
      <tr>
        <th>Sample Name</th>
        <th>Sample Alias*</th>
        <th class="fit">REMOVE</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${overview.samples}" var="sample">
        <tr sampleId="${sample.id}" onMouseOver="this.className='highlightrow'"
            onMouseOut="this.className='normalrow'">
          <td><b><a href="<c:url value='/miso/sample/${sample.id}'/>">${sample.name}</a></b></td>
          <td><a href="<c:url value='/miso/sample/${sample.id}'/>">${sample.alias}</a></td>
          <td class="misoicon" onclick="Sample.removeSampleFromOverview(${sample.id}, ${overview.id}, Utils.page.pageReload);">
            <span class="ui-icon ui-icon-trash"></span>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
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
                <span class="ui-icon ui-icon-trash"></span>
              </td>
            </sec:authorize>
          </tr>
        </table>
    </div>
    <a class="listbox" href="<c:url value='/miso/download/project/${project.id}/${file.key}'/>">

      <span onMouseOver="this.className='boxlistboxhighlight'" onMouseOut="this.className='boxlistbox'" class="boxlistbox" style='margin-left: 32px;'>
          ${file.value}
      </span>
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
  <div style="clear:both">
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
              <span class="ui-icon ui-icon-trash"></span>
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
  </div>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#samples_arrowclick'), 'samplesdiv');">
  Samples
  <div id="samples_arrowclick" class="toggleLeft"></div>
</div>
<div id="samplesdiv" style="display:none;">

  <div id="sampletabs">
    <h1>Samples</h1>
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
            <a href="javascript:void(0);" onclick="Project.ui.processSampleDeliveryForm(${project.id}, false);">Get Information Form (Tubes)</a>
            <a href="javascript:void(0);" onclick="Project.ui.processSampleDeliveryForm(${project.id}, true);">Get Information Form (Plate)</a>
            <a href="javascript:void(0);" onclick="Project.ui.uploadSampleDeliveryForm();">Import Information Form</a>
           <hr>
            <a href="javascript:void(0);" onclick="Project.ui.receiveSelectedSamples();">Receive Samples</a>
            <a href='<c:url value="/miso/importexport/importlibrarypoolsheet"/>'>Import Library Sheet</a>
            <a href="javascript:void(0);" onclick="Project.barcode.printSelectedSampleBarcodes();">Print Barcodes ...</a>
          </c:if>
        </div>
      </li>
    </ul>

    <div style="clear:both">
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

      <table class="list" id="sample_table"></table>
    </div>
  </div>
</div>
<script type="text/javascript">
  jQuery(document).ready(function () {
  Project.ui.createSampleTable(${project.id});
  });
</script>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#libraries_arrowclick'), 'librariesdiv');">
  Libraries
  <div id="libraries_arrowclick" class="toggleLeft"></div>
</div>

<div id="librariesdiv" style="display:none;">
  <h1>Libraries</h1>
  <table class="display no-border" id="listingLibrariesTable">
  </table>
</div>
<script type="text/javascript">
  jQuery(document).ready(function () {
    ListUtils.createTable('listingLibrariesTable', ListTarget.library, ${project.id}, {});
  });
</script>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#librarydils_arrowclick'), 'librarydilsdiv');">
  Library Dilutions
  <div id="librarydils_arrowclick" class="toggleLeft"></div>
</div>
<div id="librarydilsdiv" style="display:none;">
  <a id="librarydil"></a>

  <h1>Library Dilutions</h1>
  <table class="display no-border" id="listingDilutionsTable">
  </table>
</div>
<script type="text/javascript">
  jQuery(document).ready(function () {
    ListUtils.createTable('listingDilutionsTable', ListTarget.dilution, ${project.id}, {});
  });
</script>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#pools_arrowclick'), 'poolsdiv');">
  Pools
  <div id="pools_arrowclick" class="toggleLeft"></div>
</div>
<div id="poolsdiv" style="display:none;">
  <a id="pool"></a>

  <h1>Pools</h1>
  <table class="display no-border" id="listingPoolsTable">
  </table>
</div>
<script type="text/javascript">
  jQuery(document).ready(function () {
    Pool.ui.createListingPoolsTable('listingPoolsTable', '${poolConcentrationUnits}', '/miso/rest/pool/dt/project/${project.id}');
  });
</script>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#runs_arrowclick'), 'runsdiv');">
  Runs
  <div id="runs_arrowclick" class="toggleLeft"></div>
</div>
<div id="runsdiv" style="display:none;">
  <h1>Runs</h1>

  <table class="display no-border" id="listingRunsTable">
  </table>
</div>
<script type="text/javascript">
  jQuery(document).ready(function () {
    Run.ui.createListingRunsTable(${project.id});
  });
</script>
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

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
