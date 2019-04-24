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
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">

<h1>
  Edit Library
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${library.id != 0 && not empty library.identificationBarcode}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('library', [${library.id}]);">Print Barcode</span></c:if>
</div>
<div class="breadcrumbs">
  <ul>
    <li>
      <a href='<c:url value="/"/>'>Home</a>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/project/${library.sample.project.id}"/>'>${library.sample.project.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.project.name}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.name}
        </div>
      </div>
    </li>
  </ul>
  <c:if test="${not empty nextLibrary}">
    <span style="float:right; padding-top: 5px; padding-left: 6px">
      <a class='arrowright' href='<c:url value="/miso/library/${nextLibrary.id}"/>'>Next Library <b>${nextLibrary.alias}</b></a>
    </span>
  </c:if>
  <c:if test="${not empty previousLibrary}">
    <span style="float:right; padding-top: 5px">
      <a class='arrowleft' href='<c:url value="/miso/library/${previousLibrary.id}"/>'>Previous Library <b>${previousLibrary.alias}</b></a>
    </span>
  </c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Library is the first step in constructing sequenceable
  material from an initial Sample. A Library is then diluted down to a Dilution, and put in a Pool.
</div>

<div id="warnings"></div>

<form:form id="libraryForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    Warning.generateHeaderWarnings('warnings', WarningTarget.library, ${libraryDto});
    
    var opts = {
      detailedSample: Constants.isDetailedSample,
      generateLibraryAliases: Constants.automaticLibraryAlias
    };
    var dto = ${libraryDto};
    if (Constants.isDetailedSample) {
      dto.effectiveExternalNames = '${effectiveExternalNames}';
    }
    FormUtils.createForm('libraryForm', 'save', dto, 'library', opts);
  });
</script>

<c:if test="${library.id != 0}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
    <div id="notes_arrowclick" class="toggleLeftDown"></div>
  </div>
  <div id="notes">
    <h1>Notes</h1>
    <ul class="sddm">
      <li>
        <a id="notesMenuHandle" onmouseover="mopen('notesMenu')" onmouseout="mclosetime()">Options
          <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
        </a>

        <div id="notesMenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
          <a onclick="Library.ui.showLibraryNoteDialog(${library.id});" href="javascript:void(0);" class="add">Add Note</a>
        </div>
      </li>
    </ul>
    <c:if test="${fn:length(library.notes) > 0}">
      <div class="note" style="clear:both">
        <c:forEach items="${library.notes}" var="note" varStatus="n">
          <div class="exppreview" id="library-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
              <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
                <c:if test="${miso:isCurrentUser(note.owner.loginName) or miso:isAdmin()}">
                <span style="color:#000000"><a href='#' onclick="Library.ui.deleteLibraryNote('${library.id}', '${note.noteId}');">
                  <span class="ui-icon ui-icon-trash note-delete-icon"></span></a></span>
                </c:if>
              </span>
          </div>
        </c:forEach>
      </div>
    </c:if>
    <div id="addNoteDialog" title="Create new Note"></div>
  </div>
</c:if>

<br/>
<c:if test="${library.id != 0}">
  <miso:attachments item="${library}" projectId="${library.sample.project.id}"/>
  <miso:qcs id="list_qcs" item="${library}"/>
  <miso:list-section id="list_dilution" name="Dilutions" target="dilution" items="${libraryDilutions}" config="${libraryDilutionsConfig}"/>
  <miso:list-section id="list_pool" name="Pools" target="pool" items="${libraryPools}"/>
  <miso:list-section id="list_run" name="Runs" target="run" items="${libraryRuns}"/>
  <miso:list-section id="list_experiment" name="Experiments" target="experiment" items="${experiments}" config="{ libraryId: ${library.id} }"/>
  <miso:changelog item="${library}"/>
</c:if>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
