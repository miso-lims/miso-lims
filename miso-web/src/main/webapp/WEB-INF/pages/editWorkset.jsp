<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose>
    <c:when test="${pageMode eq 'create'}">Create</c:when>
    <c:otherwise>Edit</c:otherwise>
  </c:choose> Workset
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="worksetForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    FormUtils.createForm('worksetForm', 'save', ${empty worksetJson ? '{}' : worksetJson}, 'workset', {});
    Utils.ui.updateHelpLink(FormTarget.workset.getUserManualUrl());
  });
</script>

<c:if test="${pageMode eq 'edit'}">
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
          <a onclick="Utils.notes.showNoteDialog('workset', ${workset.id});" href="javascript:void(0);" class="add">Add Note</a>
        </div>
      </li>
    </ul>
    <c:if test="${fn:length(workset.notes) > 0}">
      <div id="notelist" class="note" style="clear:both">
        <c:forEach items="${workset.notes}" var="note" varStatus="n">
          <div class="exppreview" id="workset-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
          <span class="float-right"
                style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
            <c:if test="${miso:isCurrentUser(note.owner.loginName) or miso:isAdmin()}">
              <span style="color:#000000">
                <a href='#' onclick="Utils.notes.deleteNote('workset', '${workset.id}', '${note.id}'); return false;">
                  <span class="ui-icon ui-icon-trash note-delete-icon"></span>
                </a>
              </span>
            </c:if>
          </span>
          </div>
        </c:forEach>
      </div>
    </c:if>
  </div>
  <br>
 </c:if>

<c:if test="${pageMode eq 'edit'}">
  <miso:list-section-ajax id="list_samples" name="Samples" target="sample" config="{worksetId: ${worksetId}}"/>
  <miso:list-section-ajax id="list_libraries" name="Libraries" target="library" config="{worksetId: ${worksetId}}"/>
  <miso:list-section-ajax id="list_libraryAliquots" name="Library Aliquots" target="libraryaliquot" config="{worksetId: ${worksetId}}"/>
  <miso:changelog item="${workset}"/>
</c:if>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
