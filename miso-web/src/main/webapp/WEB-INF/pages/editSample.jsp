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
<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">
<link href="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.css'/>" rel="stylesheet" type="text/css" />
<script src="<c:url value='/scripts/handsontable/dist/pikaday/pikaday.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/handsontable/dist/moment/moment.js'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">

<input type="hidden" id="sampleCategory" name="sampleCategory" value="${sampleId eq 0 ? 'new' : sampleCategory}"/>
<h1>
  Edit Sample
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${sample.id != 0 && not empty sample.identificationBarcode}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('sample', [${sample.id}]);">Print Barcode</span></c:if>
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
            <a href='<c:url value="/miso/project/${sample.project.id}"/>'>${detailedSample ? sample.project.shortName : sample.project.alias}</a>
          </div>
          <div class="breadcrumbspopup">
              ${sample.project.name}
          </div>
        </div>
      </li>
    </ul>
    <c:if test="${not empty nextSample}">
      <span style="float:right; padding-top: 5px; padding-left: 6px">
        <a class='arrowright' href='<c:url value="/miso/sample/${nextSample.id}"/>'>Next Sample <b>${nextSample.name}</b></a>
      </span>
    </c:if>
    <c:if test="${not empty previousSample}">
      <span style="float:right; padding-top: 5px">
        <a class='arrowleft' href='<c:url value="/miso/sample/${previousSample.id}"/>'>Previous Sample <b>${previousSample.name}</b></a>
      </span>
    </c:if>
  </div>
</c:if>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Sample contains information about the material upon which the
  sequencing experiments are to be based. Samples can be used in any number of sequencing Experiments in the form
  of a Library that is often processed further into pooled Dilutions.
</div>

<div id="warnings"></div>

<form:form id="sampleForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    Warning.generateHeaderWarnings('warnings', WarningTarget.sample, ${sampleDto});
    
    var opts = {
      detailedSample: Constants.isDetailedSample,
      generateSampleAliases: Constants.automaticSampleAlias
    };
    <c:if test="${detailedSample}">
    opts.dnaseTreatable = ${sample.sampleClass.DNAseTreatable};
    </c:if>
    FormUtils.createForm('sampleForm', 'save', ${sampleDto}, 'sample', opts);
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

<c:if test="${ !detailedSample or detailedSample and sampleCategory eq 'Aliquot' }">
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
