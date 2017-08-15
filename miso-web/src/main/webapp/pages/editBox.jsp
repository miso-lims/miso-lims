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

<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">

<div id="tab-1">

<form:form id="box-form" data-parsley-validate="" action="/miso/box" method="POST" commandName="box" autocomplete="off" acceptCharset="utf-8">
<sessionConversation:insertSessionConversationId attributeName="box" />
<h1>
  <c:choose>
    <c:when test="${box.id !=0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> Box
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all" onclick="return Box.validateBox();">Save</button>
</h1>

<div class="sectionDivider"  onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Box is subdivided into rows and columns whoch hold Samples, Libraries, Dilutions, or Pools.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid</p>
</div>

<h2>Box Information</h2>

<div class="barcodes">
  <div class="barcodeArea ui-corner-all">
    <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Barcode</span>
    <c:if test="${box.id != 0}">
	    <ul class="barcode-ddm">
	      <li>
	        <a onmouseover="mopen('idBarcodeMenu')" onmouseout="mclosetime()">
	          <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
	          <span id="idBarcode" style="float:right"></span>
	        </a>
	
	        <div id="idBarcodeMenu"
	             onmouseover="mcancelclosetime()"
	             onmouseout="mclosetime()">
	          <a href="javascript:void(0);"
	             onclick="Box.barcode.printBoxBarcodes(${box.id});">Print</a>
	          <c:if test="${not autoGenerateIdBarcodes}">
	            <a href="javascript:void(0);" 
	               onclick="Box.ui.showBoxIdBarcodeChangeDialog(${box.id}, '${box.identificationBarcode}');">Assign New Barcode</a>
	          </c:if>
	        </div>
	      </li>
	    </ul>
	  </c:if>
    <div id="changeBoxIdBarcodeDialog" title="Assign New Barcode"></div>
	  <c:if test="${not empty box.identificationBarcode}">
	    <script type="text/javascript">
	      jQuery(document).ready(function () {
	        Fluxion.doAjax(
	          'boxControllerHelperService',
	          'getBoxBarcode',
	          {
	            'boxId':${box.id},
	            'url': ajaxurl
	          },
	          {'doOnSuccess': function (json) {
	            jQuery('#idBarcode').html("<img style='height:30px; border:0;' title='${box.identificationBarcode}' src='<c:url value='/temp/'/>" + json.img + "'/>");
	          }
	        });
	      });
	    </script>
    </c:if>
  </div>
  <div id="printServiceSelectDialog" title="Select a Printer"></div>
</div>
<div id="boxInfo">
  <table class="in">
    <tr>
      <td class="h">Box ID:</td>
      <td><span id="id">
        <c:choose>
          <c:when test="${box.id != 0}">${box.id}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </span></td>
    </tr>
    <tr>
      <td>Name:</td>
      <td>
        <c:choose>
          <c:when test="${box.id != 0}">${box.name}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td class="h">Alias:</td>
      <td><form:input id="alias" path="alias" class="validateable"/><span id="aliasCounter" class="counter"></span></td>
    </tr>
    <tr>
      <td class="h">Description:</td>
      <td><form:input id="description" path="description" class="validateable"/><span id="descriptionCounter" class="counter"></span></td>
    </tr>
    <tr>
      <td class="h">Box Use:</td>
      <td><miso:select id="boxUse" path="use" items="${boxUses}" itemLabel="alias" itemValue="id"/></td>
    </tr>
    <tr>
      <td>Box Size:</td>
      <c:choose>
        <c:when test="${box.id == 0}">
          <td>
            <c:choose>
              <c:when test="${scannerEnabled}">
                <miso:select id="boxSize" path="size" items="${boxSizes}" itemLabel="rowsByColumnsWithScan" itemValue="id"/>
                ("scannable" means this box can be scanned by the bulk scanner)
              </c:when>
             <c:otherwise>
               <miso:select id="boxSize" path="size" items="${boxSizes}" itemLabel="rowsByColumns" itemValue="id"/>
             </c:otherwise>
           </c:choose></td>
        </c:when>
        <c:otherwise><td><span id="boxSize">${box.size.getRowsByColumns()}</span> <c:choose><c:when test="${scannerEnabled}">(can ${box.size.scannable ? '':'not '}be scanned by your lab's bulk scanner)</c:when></c:choose></td></c:otherwise>
      </c:choose>
    </tr>
    <tr>
      <td>Location:</td>
      <td><form:input id="location" path="locationBarcode"/></td>
    </tr>
  </table>
</div>
</form:form>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    // Attaches form validation listener
    Validate.attachParsley('#box-form');
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
        <a onclick="Box.ui.discardEntireBox(${box.id});" href="javascript:void(0);" class="add">Discard All Tubes</a>
        <c:if test="${(scannerEnabled) && (box.size.scannable)}"><a onclick="Box.initScan();" href="javascript:void(0);">Scan Box</a></c:if>
        <a onclick="Box.fakeScan();" href="javascript:void(0);">Fill by Barcode Pattern</a>
      </div>
    </li>
  </ul>
</div>
<div id="boxContentsDiagram">
  <div id="boxContentsTable" class="unselectable" style="float:left;"></div>
  <div style="float:left;padding:20px;">
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
	      <td id="currentLocationText"></td><td><span id="currentLocation"></span></td>
	    </tr>
	    <tr>
	      <td>Barcode:</td><td><input type="text" id="selectedBarcode"/> <button id="lookupBarcode" class="ui-state-default" onclick="Box.lookupBoxableByBarcode();">Lookup</button></td>
	    </tr>
	    <tr>
	      <td><button id="updateSelected" class="ui-state-default" onclick="Box.ui.addItemToBox();">Update Position</button></td>
	    </tr>
	    <tr>
	      <td></td>
	    </tr>
	    <tr><td><button id="removeSelected" class="ui-state-default" onclick="Box.ui.removeOneItem()">Remove Tube</button>
	      <button id="emptySelected"  class="ui-state-default" onclick="Box.ui.discardOneItem();">Discard Tube</button>
	      </td>
	    </tr>
    </table>
    <p class="warning" id="warningMessages"></p>
    <p>Hold down Control (Windows, Linux) or Command (Mac) to select multiple positions.<br/>
       Click row or column header to select entire row or column.</p>
  </div>
</div>

<script type="text/javascript">
  Box.visual = new Box.Visual();
  jQuery(document).ready(function() {
    Box.boxJSON = ${boxJSON};
    Box.boxId = ${box.id};
    Box.visual.create({
      div: '#boxContentsTable',
      size: {
        rows: Box.boxJSON.rows,
        cols: Box.boxJSON.cols
      },
      data: Box.boxJSON.items
    });
    jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
    Box.ui.createListingBoxablesTable(Box.boxJSON);
  });

  jQuery('#selectedBarcode').keyup(function(event) {
    if(event.which == "13"){
      jQuery('#lookupBarcode').click();
    }
  });

  jQuery('#selectedBarcode').on('paste', function(e) {
    window.setTimeout(function() {
      jQuery('#lookupBarcode').click();
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
<div id="dialog"></div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>

