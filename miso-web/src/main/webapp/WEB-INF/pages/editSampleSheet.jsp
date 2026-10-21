<%@ include file="../header.jsp" %>

  <div id="maincontent">
    <div id="contentcolumn">

      <h1>
        <c:choose>
          <c:when test="${pageMode eq 'create'}">Create</c:when>
          <c:otherwise>Edit</c:otherwise>
        </c:choose> Sample Sheet
        <c:if test="${isAdmin}">
          <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
        </c:if>
      </h1>

      <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
        <div id="note_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notediv" class="note" style="display:none;">
        This sample sheet defines a format that can be printed from either the Pools list or the Edit Run page.
        Parameters allow the user to provide input when generating the sample sheet. Sections are the areas of the
        sample sheet output that contain configuration or data, broken into fields.
      </div>

      <form:form id="sampleSheetForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

      <br />
      <h1>Parameters</h1>
      <div id="sampleSheetForm_parametersError"></div>
      <div id="listParameters"></div>

      <br />
      <h1>Sections</h1>
      <div id="sampleSheetForm_sectionsError"></div>
      <div id="listSections"></div>
      <br />

      <div id="sectionFieldLists"></div>

      <script type="text/javascript">
        jQuery(document).ready(function () {
          var sampleSheet = ${sampleSheetDto};
          var sampleSheetFieldCommonSources = ${sampleSheetFieldCommonSources};
          var form = FormUtils.createForm('sampleSheetForm', 'save', sampleSheet, 'samplesheet', {});
          SampleSheet.setup(form, ${isAdmin}, sampleSheetFieldCommonSources);
          SampleSheet.setParameters(sampleSheet.parameters);
          SampleSheet.setSections(sampleSheet.sections);
        });
      </script>

    </div>
  </div>

  <%@ include file="adminsub.jsp" %>
  <%@ include file="../footer.jsp" %>