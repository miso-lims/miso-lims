<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<input type="hidden" id="sampleCategory" name="sampleCategory" value="${sampleCategory}"/>
<h1>
  Edit Sample
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <span class="ui-button ui-state-default" onclick="Utils.page.pageRedirect(Urls.ui.samples.qcHierarchy(${sample.id}));">QC Hierarchy</span>
  <span class="ui-button ui-state-default" onclick="Utils.printDialog('sample', [${sample.id}]);">Print Barcode</span>
</div>

<c:if test="${not empty sample.project}">
  <div class="breadcrumbs">
    <ul>
      <li>
        <a href="/">Home</a>
      </li>
      <li>
        <div class="breadcrumbsbubbleInfo">
          <div class="trigger">
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${detailedSample ? sample.project.code : sample.project.title}</a>
          </div>
          <div class="breadcrumbspopup">
              ${sample.project.name}
          </div>
        </div>
      </li>
    </ul>
    <c:if test="${not empty nextSample}">
      <span style="float:right; padding-top: 5px; padding-left: 6px">
        <a class='arrowright' href='<c:url value="/miso/sample/${nextSample.id}"/>'>Next Sample <b>${nextSample.label}</b></a>
      </span>
    </c:if>
    <c:if test="${not empty previousSample}">
      <span style="float:right; padding-top: 5px">
        <a class='arrowleft' href='<c:url value="/miso/sample/${previousSample.id}"/>'>Previous Sample <b>${previousSample.label}</b></a>
      </span>
    </c:if>
  </div>
</c:if>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Sample contains information about the material upon which the
  sequencing experiments are to be based. Samples can be used in any number of sequencing Experiments in the form
  of a Library that is often processed further into pooled library aliquots.
</div>

<div id="warnings"></div>

<form:form id="sampleForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    Warning.generateHeaderWarnings('warnings', WarningTarget.sample, ${sampleDto});
    FormUtils.createForm('sampleForm', 'save', ${sampleDto}, 'sample', ${formConfig});
    Utils.ui.updateHelpLink(FormTarget.sample.getUserManualUrl());
  });
</script>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
  <div id="notes_arrowclick" class="toggleLeftDown"></div>
</div>
<div id="notes">
  <h1>Notes</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('notesmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>
      <div id="notesmenu"
           onmouseover="mcancelclosetime()"
           onmouseout="mclosetime()">
        <a onclick="Utils.notes.showNoteDialog('sample', ${sample.id});" href="javascript:void(0);" class="add">Add Note</a>
      </div>
    </li>
  </ul>
  <c:if test="${fn:length(sample.notes) > 0}">
    <div class="note" style="clear:both">
      <c:forEach items="${sample.notes}" var="note" varStatus="n">
        <div class="exppreview" id="sample-notes-${n.count}">
          <b>${note.creationDate}</b>: ${note.text}
          <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
            <c:if test="${miso:isCurrentUser(note.owner.loginName) or miso:isAdmin()}">
              <span style="color:#000000">
                <a href='#' onclick="Utils.notes.deleteNote('sample', '${sample.id}', '${note.id}'); return false;">
                  <span class="ui-icon ui-icon-trash note-delete-icon"></span>
                </a>
              </span>
            </c:if>
          </span>
        </div>
      </c:forEach>
    </div>
  </c:if>
  <div id="addSampleNoteDialog" title="Create new Note"></div>
</div>
<br/>

<miso:attachments item="${sample}" projectId="${sample.project.id}"/>
<miso:qcs id="list_qc" item="${sample}"/>

<c:if test="${!detailedSample or sampleCategory ne 'Identity'}">
  <miso:list-section id="list_transfer" name="Transfers" target="transfer" items="${sampleTransfers}" config="{ sampleId: ${sample.id} }" alwaysShow="true"/>
</c:if>

<c:if test="${ !detailedSample or sampleCategory eq 'Aliquot' }">
  <miso:list-section id="list_library" name="Libraries" target="library" items="${sampleLibraries}"/>
</c:if>

<c:if test="${detailedSample}">
  <miso:list-section id="list_relation" name="Relationships" target="sample" items="${sampleRelations}"/>
</c:if>

<miso:list-section id="list_pool" name="Pools" target="pool" items="${samplePools}"/>
<miso:list-section id="list_run" name="Runs" target="run" items="${sampleRuns}"/>

<miso:list-section id="list_array" name="Arrays" target="array" items="${sampleArrays}"/>
<miso:list-section id="list_arrayrun" name="Array Runs" target="arrayrun" items="${sampleArrayRuns}"/>

<miso:changelog item="${sample}"/>
<div id="dialog"></div>
</div>

</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
