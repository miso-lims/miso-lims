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
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Project.validateProject();">
    Save
  </button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
</div>
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
    <td><span id="projectId">
      <c:choose>
        <c:when test="${project.id != 0}">${project.id}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </span></td>
  </tr>
  <tr>
    <td class="h">Name:</td>
    <td><span id="name">
      <c:choose>
        <c:when test="${project.id != 0}">${project.name}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </span></td>
  </tr>
  <tr>
    <td class="h">Creation date:</td>
    <td><span id="creationDate"><fmt:formatDate pattern="yyyy-MM-dd" value="${project.creationDate}"/></span></td>
  </tr>
  <tr>
    <td class="h">Alias:*</td>
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
        <c:when test="${miso:isCurrentUser(project.securityProfile.owner.loginName) or miso:isAdmin()}">
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
        <div class="parsley-errors-list filled" id="progressSelectError"></div>
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
  <tr>
    <td>Default Targeted Sequencing :*</td>
    <td>
        <form:select id="defaultTargetedSequencing" path="defaultTargetedSequencing">
            <form:options items="${targetedSequencing}" itemValue="id" itemLabel="alias"/>
        </form:select>
    </td>
  </tr>
  <c:if test="${project.id != 0}">
  <c:if test="${detailedSample}">
  <tr>
    <td>Subprojects:</td>
    <td>
    <c:forEach items="${subprojects}" var="subproject">
      <button class="ui-state-default ui-state-hover small-gap-right clickable-non-link"
        onclick="Subproject.filterSamples('samples_section_arrowclick', 'project_samples', '${subproject.alias}'); return false;">${subproject.alias}</button>
    </c:forEach>
    <br/><a href="<c:url value='/miso/subproject/bulk/new?quantity=1'/>">Add new subproject</a>
    </td>
  </tr>
  </c:if>
  <c:if test="${!projectReportLinks.isEmpty()}">
  <tr>
  	<td>External Links:</td>
  	<td>
  	<c:forEach items="${projectReportLinks}" var="projectReportLink">
  	  <span><a href="<c:out value="${projectReportLink.value}"/>">${projectReportLink.key}</a></span><br/>
  	</c:forEach>
  	</td>
  </tr>
  </c:if>
  </c:if>
</table>

<script type="text/javascript">
  jQuery(document).ready(function () {
    // Attaches a Parsley form validator
    Validate.attachParsley('#project-form');
  });
</script>

<button id="collapse_all" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Utils.ui.collapseClass('expandable_section')">
    Collapse all
</button>
<br/>
<br/>


<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#issues_arrowclick'), 'issuesdiv');">
  Tracked Issues
  <div id="issues_arrowclick" class="toggleLeft"></div>
</div>
<div id="issuesdiv" class="expandable_section" style="display:none;">
  <div class="note">Tag an issue with the project's short name (${project.shortName}) to have it show up here.</div>
  <miso:list-section id="list_issue" name="Related Issues" target="issue" items="${projectIssues}" config="{}"/>
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
  <div id="uploaddiv" class="expandable_section" style="display:none;">
    <miso:attachments item="${project}"/>
  </div>
</div>
<br/>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#studies_section_arrowclick'), 'studies_section');">
  Studies
<div id="studies_section_arrowclick" class="toggleLeft"></div>
</div>
<div id="studies_section" class="expandable_section" style="display:none;">
  <miso:list-section-ajax id="project_studies" name="Studies" target="study" project="${project}" config="{ isAdmin : ${miso:isAdmin()} }"/>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#samples_section_arrowclick'), 'samples_section');">
  Samples
<div id="samples_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="samples_section" class="expandable_section">
  <miso:list-section-ajax id="project_samples" name="Samples" target="sample" project="${project}" config="{}"/>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#libraries_section_arrowclick'), 'libraries_section');">
  Libraries
<div id="libraries_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="libraries_section" class="expandable_section">
  <miso:list-section-ajax id="project_libraries" name="Libraries" target="library" project="${project}" config="{}"/>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#librarytemplates_section_arrowclick'), 'librarytemplates_section');">
  Library Templates
<div id="librarytemplates_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="librarytemplates_section" class="expandable_section">
  <miso:list-section-ajax id="project_librarytemplates" name="Library Templates" target="library_template" project="${project}" config="{}"/>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#dilutions_section_arrowclick'), 'dilutions_section');">
  Dilutions
<div id="dilutions_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="dilutions_section" class="expandable_section">
  <miso:list-section-ajax id="project_dilutions" name="Dilutions" target="dilution" project="${project}" config="{}"/>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#pools_section_arrowclick'), 'pools_section');">
  Pools
<div id="pools_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="pools_section" class="expandable_section">
  <miso:list-section-ajax id="project_pools" name="Pools" target="pool" project="${project}" config="{}"/>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#runs_section_arrowclick'), 'runs_section');">
  Runs
<div id="runs_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="runs_section" class="expandable_section">
  <miso:list-section-ajax id="project_runs" name="Runs" target="run" project="${project}" config="{}"/>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#arrayruns_section_arrowclick'), 'arrayruns_section');">
  Array Runs
<div id="arrayruns_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="arrayruns_section" class="expandable_section">
  <miso:list-section-ajax id="project_arrayruns" name="Array Runs" target="arrayrun" project="${project}" config="{}"/>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#arraysamples_section_arrowclick'), 'arraysamples_section');">
  Arrayed Samples
<div id="arraysamples_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="arraysamples_section" class="expandable_section">
  <miso:list-section-ajax id="project_arraysamples" name="Arrayed Samples" target="sample" project="${project}" config="{arrayed: true}"/>
</div>

</c:when>
</c:choose>
<br/>

<button id="collapse_all" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Utils.ui.collapseClass('expandable_section')">
    Collapse all
</button>

<div id="addProjectOverviewNoteDialog" title="Create new Note"></div>

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
});

</script>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
