<%@ include file="../header.jsp" %>

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
              <a href='<c:url value="/project/${study.project.id}"/>'>${study.project.title}</a>
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
