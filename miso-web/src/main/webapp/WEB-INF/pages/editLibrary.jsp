<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  Edit Library
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <span class="ui-button ui-state-default" onclick="Utils.page.pageRedirect(Urls.ui.libraries.qcHierarchy(${library.id}));">QC Hierarchy</span>
  <span class="ui-button ui-state-default" onclick="Utils.printDialog('library', [${library.id}]);">Print Barcode</span>
</div>
<div class="breadcrumbs">
  <ul>
    <li>
      <a href='<c:url value="/"/>'>Home</a>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/project/${library.sample.project.id}"/>'>${library.sample.project.title}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.project.name}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/sample/${library.sample.id}"/>'>${library.sample.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.name}
        </div>
      </div>
    </li>
  </ul>
  <c:if test="${not empty nextLibrary}">
    <span style="float:right; padding-top: 5px; padding-left: 6px">
      <a class='arrowright' href='<c:url value="/library/${nextLibrary.id}"/>'>Next Library <b>${nextLibrary.label}</b></a>
    </span>
  </c:if>
  <c:if test="${not empty previousLibrary}">
    <span style="float:right; padding-top: 5px">
      <a class='arrowleft' href='<c:url value="/library/${previousLibrary.id}"/>'>Previous Library <b>${previousLibrary.label}</b></a>
    </span>
  </c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Library is the first step in constructing sequenceable
  material from an initial Sample. A Library is then diluted down to a library aliquot, and put in a Pool.
</div>

<div id="warnings"></div>

<form:form id="libraryForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    Warning.generateHeaderWarnings('warnings', WarningTarget.library, ${libraryDto});
    FormUtils.createForm('libraryForm', 'save', ${libraryDto}, 'library', ${formConfig});
    Utils.ui.updateHelpLink(FormTarget.library.getUserManualUrl());
  });
</script>

<c:if test="${library.id != 0}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
    <div id="notes_arrowclick" class="toggleLeftDown"></div>
  </div>
  <div id="notes">
    <miso:list-section id="list_note" name="Notes" target="note" items="${notes}" config="{ entityId: ${library.id}, entityType: 'Library' }" alwaysShow="true"/>
  </div>
</c:if>

<br/>
<c:if test="${library.id != 0}">
  <miso:attachments item="${library}" projectId="${library.sample.project.id}"/>
  <miso:qcs id="list_qcs" item="${library}"/>
  <miso:list-section id="list_transfer" name="Transfers" target="transfer" items="${libraryTransfers}" config="{ libraryId: ${library.id} }" alwaysShow="true"/>
  <miso:list-section id="list_aliquot" name="Library Aliquots" target="libraryaliquot" items="${libraryAliquots}" config="${libraryAliquotsConfig}"/>
  <miso:list-section id="list_pool" name="Pools" target="pool" items="${libraryPools}"/>
  <miso:list-section id="list_run" name="Runs" target="run" items="${libraryRuns}"/>
  <miso:list-section id="list_experiment" name="Experiments" target="experiment" items="${experiments}" config="{ libraryId: ${library.id} }"/>
  <miso:changelog item="${library}"/>
</c:if>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
