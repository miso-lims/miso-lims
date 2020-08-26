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
  Created by Eclipse.
  User: harmstrong
  Date: 19-Oct-2015
  Time: 15:09:06
--%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">

<div id="tab-1">

<h1>
  <c:choose>
    <c:when test="${pageMode eq 'create'}">Create</c:when>
    <c:otherwise>Edit</c:otherwise>
  </c:choose> Box
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${box.id != 0}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('box', [${box.id}]);">Print Barcode</span></c:if>
</div>

<div class="sectionDivider"  onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">
  A Box is subdivided into rows and columns which hold Samples, Libraries, Library Aliquots, or Pools.
</div>

<form:form id="boxForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    FormUtils.createForm('boxForm', 'save', ${boxJSON}, 'box', {isNew: ${pageMode eq 'create'}});
    Utils.ui.updateHelpLink(FormTarget.box.getUserManualUrl());
  });
</script>

<c:if test="${box.id != 0}">
<div id="boxOptions">
  <h1>Contents</h1>
  <ul class="sddm">
    <li>
      <a onmouseover="mopen('actionsmenu')" onmouseout="mclosetime()">Options
        <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
      </a>
      <div id="actionsmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
        <a onclick="Box.ui.exportBox(${box.id});" href="javascript:void(0);" class="add">Export Box to Excel</a>
        <c:if test="${fragmentAnalyserCompatible}">
          <a onclick="Box.ui.exportFragmentAnalyser(${box.id});" href="javascript:void(0);">Export Fragment Analyser Sheet</a>
        </c:if>
        <a onclick="Box.ui.discardAllContents(${box.id});" href="javascript:void(0);" class="add">Discard All Contents</a>
        <c:if test="${(scannerEnabled) && (box.size.scannable)}">
          <c:forEach items="${scannerNames}" var="scannerName">
            <a onclick="Box.initScan('${scannerName}');" href="javascript:void(0);">Scan with ${scannerName}</a>
          </c:forEach>
        </c:if>
        <a onclick="Box.fakeScan();" href="javascript:void(0);">Fill by Barcode Pattern</a>
      </div>
    </li>
  </ul>
</div>
<div id="boxContentsDiagram">
  <div class="note">
    Hold down Ctrl (Windows, Linux) or Command (Mac) to select multiple positions.
    Click a row or column header to select the entire row or column.
  </div>
  <div style="float:left;margin:20px;">
    <div id="boxContentsTable" class="unselectable" style="margin-bottom:10px;"></div>
    <div>
      <button class="ui-state-default" onclick="Box.visual.selectAll()">Select All</button>
      <button class="ui-state-default" onclick="Box.visual.selectOddColumns()">Select Odd Columns</button>
      <button class="ui-state-default" onclick="Box.visual.selectEvenColumns()">Select Even Columns</button>
    </div>
  </div>
  <div id="singlePositionControls" style="float:left;padding:20px;">
    <table id="selectedPositionInfo">
	    <tr>
	      <td>Selected Position:</td><td><span id="selectedPosition"></span></td>
	    </tr>
	    <tr>
	      <td>Name:</td><td><span id="selectedName"></span></td>
	    </tr>
	    <tr>
	      <td>Alias:</td><td><span id="selectedAlias"></span></td>
	    </tr>
        <tr>
          <td>Barcode:</td><td><span id="selectedBarcode"></span></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <button id="removeSelected" class="ui-state-default" onclick="Box.ui.removeOneItem()">Remove Item</button>
            <button id="emptySelected"  class="ui-state-default" onclick="Box.ui.discardOneItem();">Discard Item</button>
          </td>
        </tr>
        <tr>
          <td>Search:</td>
          <td>
            <input id="searchField" type="text"/>
            <button id="search" class="ui-state-default" onclick="Box.ui.searchBoxables();">Lookup</button>
          </td>
        </tr>
        <tr>
          <td>Results:</td>
          <td>
            <img id="ajaxLoader" src="/styles/images/ajax-loader.gif" class="fg-button hidden"/>
            <select id="resultSelect"/></select>
            <button id="updateSelected" class="ui-state-default" onclick="Box.ui.addItemToBox();">Update Position</button>
          </td>
        </tr>
    </table>
    <p class="warning" id="warningMessages"></p>
  </div>
  <div id="bulkPositionControls" style="float:left; padding:10px; margin:20px; border:1px solid darkgrey; max-height:340px; overflow-y:scroll;">
    <button class="ui-state-default" onclick="Box.ui.bulkRemoveItems();">Remove Selected</button>
    <button class="ui-state-default" onclick="Box.ui.bulkDiscardItems();">Discard Selected</button>
    <br/><br/>
    <table id="bulkUpdateTable">
      <thead>
        <tr>
          <th>Position</th>
          <th>Search</th>
        </tr>
      </thead>
      <tbody>
        <!-- contents added via js -->
      </tbody>
      <tfoot>
        <tr>
          <td></td>
          <td>
            <button id="bulkUpdate" class="ui-state-default" onclick="Box.ui.bulkUpdatePositions();">Update</button>
          </td>
        </tr>
      </tfoot>
    </table>
  </div>
  <br/>
</div>

<script type="text/javascript">
  Box.visual = new Box.Visual(Box.ui.onSelectionChanged);
  jQuery(document).ready(function() {
    Box.boxJSON = ${boxJSON};
    Box.boxId = ${box.id};
    Box.ui.update();
    Utils.ui.setDisabled('#updateSelected, #removeSelected, #emptySelected, #resultSelect, #search, #searchField', true);
  });

  jQuery('#searchField').keyup(function(event) {
    if(event.which == "13"){
      jQuery('#search').click();
    }
  });

  jQuery('#searchField').on('paste', function(e) {
    window.setTimeout(function() {
      jQuery('#search').click();
    }, 100);
  });
</script>

<div id="boxContentsList" style="clear:both;">
  <table id="listingBoxablesTable" class="display"></table>
</div>
    <miso:changelog item="${box}"/>
</c:if>

<div id='dialogDialog' title='Scan' hidden='true'>
  <div id='dialogInfoAbove'></div>
  <div id="dialogVisual" style="text-align:center;"></div>
  <div id="dialogInfoBelow"></div>
</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>

