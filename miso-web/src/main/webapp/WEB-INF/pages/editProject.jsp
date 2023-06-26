<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">
<h1>
<c:choose><c:when test="${project.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
  Project
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">
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
  processed into library aliquots, which are then Pooled and sequenced.
</div>

<div id="warnings"></div>

<form:form id="projectForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<c:if test="${project.id != 0 && (detailedSample || not empty projectReportLinks)}">
  <table class="in">
    <c:if test="${detailedSample}">
      <tr>
        <td class="h">Subprojects:</td>
        <td>
        <c:forEach items="${subprojects}" var="subproject">
          <button class="ui-state-default ui-state-hover small-gap-right clickable-non-link"
            onclick="Subproject.filterSamples('samples_section_arrowclick', 'project_samples', '${subproject.alias}'); return false;">${subproject.alias}</button>
        </c:forEach>
        <br/><a href="<c:url value='/miso/subproject/bulk/new?quantity=1'/>">Add new subproject</a>
        </td>
      </tr>
    </c:if>
    <c:if test="${not empty projectReportLinks}">
      <tr>
      	<td class="h">External Links:</td>
      	<td>
      	<c:forEach items="${projectReportLinks}" var="projectReportLink">
      	  <span><a href="<c:out value="${projectReportLink.value}"/>">${projectReportLink.key}</a></span><br/>
      	</c:forEach>
      	</td>
      </tr>
    </c:if>
  </table>
</c:if>

<c:if test="${project.id != 0}">
  <button id="collapse_all" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Utils.ui.collapseClass('expandable_section')">
      Collapse all
  </button>
  <br/>
  <br/>

  <c:if test="${issueTrackerEnabled}">
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#issues_arrowclick'), 'issuesdiv');">
      Tracked Issues
      <div id="issues_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="issuesdiv" class="expandable_section" style="display:none;">
      <div class="note">Tag an issue with the project's code (${project.code}) to have it show up here.</div>
      <c:choose>
        <c:when test="${issueLookupError}">
      	  <p class="big big-error">Error retrieving issues</p>
        </c:when>
        <c:otherwise>
          <miso:list-section id="list_issue" name="Related Issues" target="issue" items="${issues}" config="{}"/>
        </c:otherwise>
      </c:choose>
    </div>
  </c:if>
  
  <div id="simplebox">
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#upload_arrowclick'), 'uploaddiv');">
      Project Files
      <div id="upload_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="uploaddiv" class="expandable_section" style="display:none;">
      <miso:attachments item="${project}"/>
    </div>
  </div>
  <!-- <br/> -->
</c:if>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#assays_section_arrowclick'), 'assays_section');">
  Assays
<div id="assays_section_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="projectForm_assaysError"></div>
<div id="assays_section" class="expandable_section">
  <h1>Assays</h1>
</div>

<c:if test="${project.id != 0}">
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
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#libraryAliquots_section_arrowclick'), 'libraryAliquots_section');">
    Library Aliquots
  <div id="libraryAliquots_section_arrowclick" class="toggleLeftDown"></div>
  </div>
  <div id="libraryAliquots_section" class="expandable_section">
    <miso:list-section-ajax id="project_libraryAliquots" name="Library Aliquots" target="libraryaliquot" project="${project}" config="{}"/>
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
  
  <miso:changelog item="${project}"/>
  
  <br/>
  
  <button id="collapse_all" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Utils.ui.collapseClass('expandable_section')">
      Collapse all
  </button>
</c:if>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var projectDto = ${projectDto};
    var config = ${formConfig};

    var assays = [];
    for (var i = 0; i < projectDto.assayIds.length; i++) {
      var holder = Constants.assays.find(function (x) {
        return x.id === projectDto.assayIds[i];
      });
      assays.push(holder);
    }

    Warning.generateHeaderWarnings('warnings', WarningTarget.project, projectDto);
    var form = FormUtils.createForm('projectForm', 'save', projectDto, 'project', config);

    Project.setForm(form);
    Project.setListConfig({projectId: projectDto.id});
    Project.setAssays(assays);
    Utils.ui.updateHelpLink(FormTarget.project.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
