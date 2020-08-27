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
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>" type="text/css">

<div id="maincontent">
<div id="contentcolumn">
<h1 class="noPrint"><c:choose><c:when
    test="${pool.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Pool
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${pool.id != 0}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('pool', [${pool.id}]);">Print Barcode</span></c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Pool contains <b>one or more</b> library aliquots that are
  to be placed, as part of an Experiment, in a sequencer instrument Run partition (lane/chamber/cell). Pools
  with more than one library aliquot are said to be multiplexed.
</div>

<div id="warnings"></div>

<form:form id="poolForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<c:if test="${pool.id != 0}">
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
      <div id="notes_arrowclick" class="toggleLeftDown"></div>
    </div>
    <div id="notes">
      <h1>Notes</h1>
      <ul class="sddm">
        <li>
          <a id="notesMenuHandle" onmouseover="mopen('notesmenu')" onmouseout="mclosetime()">Options
            <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
          </a>

          <div id="notesmenu"
               onmouseover="mcancelclosetime()"
               onmouseout="mclosetime()">
            <a onclick="Utils.notes.showNoteDialog('pool', ${pool.id});" href="javascript:void(0);" class="add">Add Note</a>
          </div>
        </li>
      </ul>
      <c:if test="${fn:length(pool.notes) > 0}">
        <div class="note" style="clear:both">
          <c:forEach items="${pool.notes}" var="note" varStatus="n">
          <div class="exppreview" id="pool-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
              <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
                <c:if test="${miso:isCurrentUser(note.owner.loginName) or miso:isAdmin()}">
                  <span style="color:#000000">
                    <a href='#' onclick="Utils.notes.deleteNote('pool', '${pool.id}', '${note.id}'); return false;">
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
    <br/>

  <miso:attachments item="${pool}"/>
  <miso:qcs id="list_qcs" item="${pool}"/>
  <miso:list-section id="list_transfer" name="Transfers" target="transfer" items="${poolTransfers}" config="{ poolId: ${pool.id} }" alwaysShow="true"/>
  <miso:list-section id="list_pool_order" name="Linked Pool Orders" target="linkedpoolorder" alwaysShow="false" items="${poolorders}" config="{}" />
  <miso:list-section id="list_order" name="Requested Sequencing Orders" target="sequencingorder" alwaysShow="true" items="${orders}" config="{ pool: ${poolDto}, platformType: '${pool.platformType.name()}' }"/>
  <miso:list-section-ajax id="list_completion" name="Sequencing Order Status" target="sequencingordercompletion" config="{ poolId: ${pool.id} }"/>
  <miso:list-section id="list_run" name="Runs" target="run" items="${runs}" config="{ poolId: ${pool.id} }"/>
  <miso:list-section id="list_partition" name="${pool.platformType.pluralPartitionName}" target="partition" items="${partitions}" config="{'showContainer': true, 'showPool': false}"/>
</c:if>

<br/>
<h1>Library Aliquots</h1>
<div id="poolForm_poolElementsError" class="errorContainer"></div>
<div id="listAliquotsContainer"></div>

<div class="noPrint">
  <miso:changelog item="${pool}"/>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var dto = ${poolDto};
    if (dto.id) {
      Warning.generateHeaderWarnings('warnings', WarningTarget.pool, dto);
    }
    var form = FormUtils.createForm('poolForm', 'save', dto, 'pool', {});
    Pool.setForm(form);
    Pool.setAliquots(dto.pooledElements, dto.duplicateIndicesSequences, dto.nearDuplicateIndicesSequences);
    Utils.ui.updateHelpLink(FormTarget.pool.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
