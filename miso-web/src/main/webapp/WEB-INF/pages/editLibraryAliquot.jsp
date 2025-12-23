<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  Edit Library Aliquot
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <span class="ui-button ui-state-default" onclick="Utils.page.pageRedirect(Urls.ui.libraryAliquots.qcHierarchy(${aliquot.id}));">QC Hierarchy</span>
  <span class="ui-button ui-state-default" onclick="Utils.printDialog('libraryaliquot', [${aliquot.id}]);">Print Barcode</span>
</div>

<div class="breadcrumbs">
  <ul>
    <li>
      <a href='<c:url value="/"/>'>Home</a>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/project/${aliquot.library.sample.project.id}"/>'>${aliquot.library.sample.project.title}</a>
        </div>
        <div class="breadcrumbspopup">
            ${aliquot.library.sample.project.name}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/sample/${aliquot.library.sample.id}"/>'>${aliquot.library.sample.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${aliquot.library.sample.name}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/library/${aliquot.library.id}"/>'>${aliquot.library.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${aliquot.library.name}
        </div>
      </div>
    </li>
  </ul>
</div>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A library aliquot is a portion of a library that has been prepared for pooling.</div>

<div id="warnings"></div>

<form:form id="libraryAliquotForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    Warning.generateHeaderWarnings('warnings', WarningTarget.libraryaliquot, ${aliquotDto});
    FormUtils.createForm('libraryAliquotForm', 'save', ${aliquotDto}, 'libraryaliquot', {
      detailedSample: Constants.isDetailedSample
    });
    Utils.ui.updateHelpLink(FormTarget.libraryaliquot.getUserManualUrl());
  });
</script>

<miso:qcs id="list_qcs" item="${aliquot}"/>
<miso:list-section id="list_transfer" name="Transfers" target="transfer" items="${aliquotTransfers}" config="{ libraryAliquotId: ${aliquot.id} }" alwaysShow="true"/>
<miso:list-section id="list_pool" name="Pools" target="pool" items="${aliquotPools}"/>
<miso:list-section id="list_run" name="Runs" target="run" items="${aliquotRuns}"/>
<miso:changelog item="${aliquot}"/>

</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
