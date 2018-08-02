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

<%--
  Created by IntelliJ IDEA.
  User: bian
  Date: 19-Apr-2010
  Time: 13:38:56

--%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>" type="text/css">

<div id="maincontent">
<div id="contentcolumn">
<form:form id="pool-form" data-parsley-validate="" action="/miso/pool" method="POST" commandName="pool" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="pool"/>
<h1 class="noPrint"><c:choose><c:when
    test="${pool.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Pool
  <button id="save" type="button" onclick="return Pool.savePool();" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${pool.id != 0 && not empty pool.identificationBarcode}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('pool', [${pool.id}]);">Print Barcode</span></c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Pool contains <b>one or more</b> Dilutions that are
  to be placed, as part of an Experiment, in a sequencer instrument Run partition (lane/chamber/cell). Pools
  with more than one Dilution are said to be multiplexed.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid!</p>
</div>

<h2>Pool Information</h2>
<table class="in">
  <tr>
    <td class="h">Pool ID:</td>
    <td id="poolId">
      <c:choose>
        <c:when test="${pool.id != 0}">${pool.id}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <c:if test="${pool.id != 0}">
      <td colspan="2" id="warnings">
        <script>
            jQuery(document).ready(function() {
                jQuery('#warnings').append(WarningTarget.pool.headerWarnings(${poolDto}));
            });
        </script>
      </td>
    </c:if>
  </tr>
  <tr>
    <td class="h">Name:</td>
    <td id="name">
      <c:choose>
        <c:when test="${pool.id != 0}">${pool.name}</c:when>
        <c:otherwise><i>Unsaved</i></c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Alias:*</td>
    <td><form:input id="alias" path="alias"/><span id="aliasCounter" class="counter"></span></td>
  </tr>
  <tr>
    <td class="h">Description:</td>
    <td><form:input id="description" path="description"/><span id="descriptionCounter" class="counter"></span></td>
  </tr>
  <c:if test="${not autoGenerateIdBarcodes}">
    <tr>
      <td class="h">Matrix Barcode:</td>
      <td><form:input id="identificationBarcode" path="identificationBarcode" name="identificationBarcode"/></td>
    </tr>
  </c:if>
  <tr>
    <td>Platform Type:</td>
    <td>
      <c:choose>
        <c:when test="${pool.id != 0}">
          <span id="platformType">${pool.platformType.key}</span>
        </c:when>
        <c:otherwise>
          <form:select id="platformType" path="platformType" items="${platformTypes}"/>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
  <tr>
    <td class="h">Desired Concentration:</td>
    <td><form:input id="concentration" path="concentration"/></td>
  </tr>
  <tr>
    <td>Concentration Units:</td>
    <td><form:select id="concentrationUnits" path="concentrationUnits">
    <option value>(None)</option>
      <c:forEach var="concentrationUnit" items="${concentrationUnits}">
        <option value="${concentrationUnit}" <c:if test="${pool.concentrationUnits == concentrationUnit}">selected="selected"</c:if>>
          ${concentrationUnit.units}
        </option>
      </c:forEach>
    </form:select></td>
  </tr>
  <tr>
    <td class="h">Creation Date:*</td>
    <td><c:choose>
      <c:when test="${pool.id != 0}">
        <span id="creationDate">
          <fmt:formatDate pattern="yyyy-MM-dd" type="both" value="${pool.creationDate}"/>
        </span>
      </c:when>
      <c:otherwise>
        <form:input id="creationDate" path="creationDate"/>
        <script type="text/javascript">
          Utils.ui.addMaxDatePicker("creationDate", 0);
        </script>
      </c:otherwise>
    </c:choose>
    </td>
  </tr>
  <tr bgcolor="yellow">
    <td>QC Passed:</td>
    <td>
      <form:radiobutton path="qcPassed" value="" label="Unknown"/>
      <form:radiobutton path="qcPassed" value="true" label="True"/>
      <form:radiobutton path="qcPassed" value="false" label="False"/>
    </td>
  </tr>
  
  <tr>
    <td>Volume:</td>
    <td><form:input id="volume" path="volume"/></td>
  </tr>
  <tr>
    <td>Volume Units:</td>
    <td><form:select id="volumeUnits" path="volumeUnits">
    <option value>(None)</option>
      <c:forEach var="volumeUnit" items="${volumeUnits}">
        <option value="${volumeUnit}" <c:if test="${pool.volumeUnits == volumeUnit}">selected="selected"</c:if>>
          ${volumeUnit.units}
        </option>
      </c:forEach>
    </form:select></td>
  </tr>
  <tr>
    <td><label for="discarded">Discarded:</label></td>
    <td><form:checkbox id="discarded" path="discarded"/></td>
  </tr>
  <tr>
    <td class="h">Location:</td>
    <td id="location">
      <c:if test="${!empty pool.box.locationBarcode}">${pool.box.locationBarcode},</c:if>
      <c:if test="${!empty pool.boxPosition}"><a href='<c:url value="/miso/box/${pool.box.id}"/>'>${pool.box.alias}, ${pool.boxPosition}</a></c:if>
    </td>
  </tr>
</table>

<%@ include file="volumeControl.jspf" %>

<br/>

<script type="text/javascript">
  jQuery(document).ready(function () {
    // Attach Parsley form validator
    Validate.attachParsley('#pool-form');
  });
</script>

<!--notes start -->
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
            <a onclick="Pool.ui.showPoolNoteDialog(${pool.id});" href="javascript:void(0);" class="add">Add Note</a>
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
                <span style="color:#000000"><a href='#' onclick="Pool.ui.deletePoolNote('${pool.id}', '${note.noteId}');">
                  <span class="ui-icon ui-icon-trash note-delete-icon"></span></a></span>
                </c:if>
              </span>
          </div>
        </c:forEach>
        </div>
      </c:if>
      <div id="addNoteDialog" title="Create new Note"></div>
    </div>
    <br/>
</c:if>
<!-- notes end -->

<c:if test="${pool.id != 0}">
  <miso:attachments item="${pool}"/>
  <miso:qcs id="list_qcs" item="${pool}"/>
  <miso:list-section id="list_order" name="Requested Orders" target="order" alwaysShow="true" items="${orders}" config="{ pool: ${poolDto}, platformType: '${pool.platformType.name()}' }"/>
  <miso:list-section-ajax id="list_completion" name="Order Status" target="completion" config="{ poolId: ${pool.id} }"/>
  <miso:list-section id="list_run" name="Runs" target="run" items="${runs}" config="{ poolId: ${pool.id} }"/>
  <miso:list-section id="list_partition" name="${pool.platformType.pluralPartitionName}" target="partition" items="${partitions}" config="{'showContainer': true, 'showPool': false}"/>
  <miso:list-section id="list_included" name="Included Dilutions" target="poolelement" alwaysShow="true" items="${includedDilutions}" config="{ poolId: ${pool.id}, add: false, duplicateIndicesSequences: ${duplicateIndicesSequences}, nearDuplicateIndicesSequences: ${nearDuplicateIndicesSequences}}"/>
  <div class="noPrint">
    <miso:list-section-ajax id="list_available" name="Available Dilutions" target="poolelement" config="{ poolId: ${pool.id}, add: true }"/>
  </div>
</c:if>
<div class="noPrint">
  <%@ include file="permissions.jsp" %>
  <miso:changelog item="${pool}"/>
</div>
</form:form>

</div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#alias').simplyCountable({
      counter: '#aliasCounter',
      countType: 'characters',
      maxCount: ${maxLengths['alias']},
      countDirection: 'down'
    });
    jQuery('#description').simplyCountable({
      counter: '#descriptionCounter',
      countType: 'characters',
      maxCount: ${maxLengths['description']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
