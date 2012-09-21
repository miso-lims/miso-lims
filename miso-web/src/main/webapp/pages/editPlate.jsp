<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/plate_ajax.js?ts=${timestamp.time}'/>"></script>

<script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/datatable.css'/>" type="text/css">

<div id="maincontent">
<div id="contentcolumn">
<form:form method="POST" commandName="plate" autocomplete="off" onsubmit="return validate_plate(this);">
  <sessionConversation:insertSessionConversationId attributeName="plate"/>
<h1>
    <c:choose>
        <c:when test="${plate.id != 0}">Edit</c:when>
        <c:otherwise>Create</c:otherwise>
    </c:choose> Plate
    <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<h2>Plate Information</h2>
<div class="barcodes">
    <div class="barcodeArea ui-corner-all">
        <c:choose>
            <c:when test="${empty plate.locationBarcode}">
                <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span><form:input
                    path="locationBarcode" size="8"/>
            </c:when>
            <c:otherwise>
                <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span>
                <ul class="barcode-ddm">
                    <li><a
                            onmouseover="mopen('locationBarcodeMenu')"
                            onmouseout="mclosetime()"><span style="float:right; margin-top:6px;"
                                                            class="ui-icon ui-icon-triangle-1-s"></span><span
                            id="locationBarcode"
                            style="float:right; margin-top:6px; padding-bottom: 11px;">${plate.locationBarcode}</span></a>

                        <div id="locationBarcodeMenu"
                             onmouseover="mcancelclosetime()"
                             onmouseout="mclosetime()">
                            <a href="javascript:void(0);" onclick="Plate.barcode.showPlateLocationChangeDialog(${plate.id});">Change
                                location</a>
                        </div>
                    </li>
                </ul>
                <div id="changePlateLocationDialog" title="Change Plate Location"></div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="barcodeArea ui-corner-all">
        <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">ID</span>
        <c:if test="${not empty plate.identificationBarcode}">
            <ul class="barcode-ddm">
                <li><a
                        onmouseover="mopen('idBarcodeMenu')"
                        onmouseout="mclosetime()"><span style="float:right; margin-top:6px;"
                                                        class="ui-icon ui-icon-triangle-1-s"></span><span id="idBarcode"
                                                                                                          style="float:right"></span></a>

                    <div id="idBarcodeMenu"
                         onmouseover="mcancelclosetime()"
                         onmouseout="mclosetime()">
                        <a href="javascript:void(0);" onclick="Plate.barcode.printPlateBarcodes(${plate.id});">Print</a>
                    </div>
                </li>
            </ul>
            <script type="text/javascript">
                jQuery(document).ready(function() {
                    Fluxion.doAjax(
                            'plateControllerHelperService',
                            'getPlateBarcode',
                            {'plateId':${plate.id},
                                'url':ajaxurl
                            },
                            {'doOnSuccess':function(json) {
                                jQuery('#idBarcode').html("<img style='border:0;' src='<c:url value='/temp/'/>" + json.img + "'/>");
                            }
                            });
                });
            </script>
        </c:if>
    </div>
    <div id="printServiceSelectDialog" title="Select a Printer"></div>
</div>
<div>
    <table class="in">
        <tr>
            <td class="h">Plate ID:</td>
            <td>
                <c:choose>
                    <c:when test="${plate.id != 0}">${plate.id}</c:when>
                    <c:otherwise><i>Unsaved</i></c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>Name:</td>
            <td>
                <c:choose>
                    <c:when test="${plate.id != 0}">${plate.name}</c:when>
                    <c:otherwise><i>Unsaved</i></c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>Description:</td>
            <td><form:input path="description"/><span id="descriptioncounter" class="counter"></span></td>
        </tr>
        <tr>
            <td>Creation Date:</td>
            <td>
                <form:input path="creationDate" id="creationdatepicker"/>
                <script type="text/javascript">
                    Utils.ui.addDatePicker("creationdatepicker");
                </script>
            </td>
        </tr>
        <tr>
            <td>Size:</td>
            <td>
                <form:input path="size" id="size"/>
            </td>
        </tr>
        <tr>
            <c:choose>
                <c:when test="${plate.id == 0 or empty plate.plateMaterialType}">
                    <td>Plate Material Type:</td>
                    <td>
                        <form:radiobuttons id="plateMaterialType" path="plateMaterialType" onchange="Plate.tagbarcode.getPlateBarcodesByMaterialType(this);"/>
                    </td>
                </c:when>
                <c:otherwise>
                    <td>Plate Material Type</td>
                    <td>${plate.plateMaterialType}</td>
                </c:otherwise>
            </c:choose>
        </tr>
        <tr>
          <td id="plateBarcodeSelect">
            <i>Please choose a material type above...</i>
          </td>
        </tr>
      <%--
        <tr>
            <c:choose>
                <c:when test="${empty plate.plateId or empty plate.tagBarcode}">
                    <td>Tag Barcode:</td>
                    <td>
                        <form:select id="tagBarcodes" path="tagBarcode">
                          <form:option value="" label="No Barcode"/>
                          <form:options items="${availableTagBarcodes}" itemLabel="name" itemValue="tagBarcodeId" />
                        </form:select>
                    </td>
                </c:when>
                <c:otherwise>
                    <td>Tag Barcode:</td>
                    <td>
                        <form:select id="tagBarcodes" path="tagBarcode">
                          <form:option value="" label="No Barcode"/>
                          <form:options items="${availableTagBarcodes}" itemLabel="name" itemValue="tagBarcodeId" />
                        </form:select>
                    </td>
                </c:otherwise>
            </c:choose>
        </tr>
        --%>
    </table>
</div>
</form:form>
</div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>