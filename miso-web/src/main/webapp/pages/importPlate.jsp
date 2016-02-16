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

<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">

<div id="maincontent">
  <div id="contentcolumn">
    <form:form commandName="plate" autocomplete="off">
    <sessionConversation:insertSessionConversationId attributeName="plate"/>
    <h1>Import Plate</h1>

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
              <li>
                <a onmouseover="mopen('locationBarcodeMenu')" onmouseout="mclosetime()">
                  <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
                  <span id="locationBarcode" style="float:right; margin-top:6px; padding-bottom: 11px;">${plate.locationBarcode}</span>
                </a>
              </li>
            </ul>
            <div id="changePlateLocationDialog" title="Change Plate Location"></div>
          </c:otherwise>
        </c:choose>
      </div>
      <div id="printServiceSelectDialog" title="Select a Printer"></div>
    </div>
    <div>
      <table class="in">
        <tr>
          <td class="h">Plate ID:</td>
          <td>
            <i>Unsaved</i>
          </td>
        </tr>
        <tr>
          <td>Name:</td>
          <td>
            <i>Unsaved</i>
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
              Utils.ui.addMaxDatePicker("creationdatepicker", 0);
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
            <c:when test="${empty plate.plateMaterialType}">
              <td>Plate Material Type:</td>
              <td>
                <form:radiobuttons id="plateMaterialType" path="plateMaterialType"
                                   onchange="Plate.tagbarcode.getPlateBarcodesByMaterialType(this);"/>
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
      </table>
      </form:form>

      <a name="plate_elements"></a>

      <h1>
        Elements
      </h1>
      <ul class="sddm">
        <li>
          <a onmouseover="mopen('qcmenu')" onmouseout="mclosetime()">Options
            <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
          </a>

          <div id="qcmenu"
               onmouseover="mcancelclosetime()"
               onmouseout="mclosetime()">
            <a href="/miso/plate/export">Get Plate Input Form</a>
          </div>
        </li>
      </ul>
        <span style="clear:both">
          <div id="plateformdiv" class="simplebox">
            <table class="in">
              <tr>
                <td>
                  <form method='post'
                        id='plateform_upload_form'
                        action='<c:url value="/miso/upload/plate/plate-form"/>'
                        enctype="multipart/form-data"
                        target="plateform_target_upload"
                        onsubmit="Utils.fileUpload.fileUploadProgress('plateform_upload_form', 'plateform_statusdiv', Plate.ui.plateInputFormUploadSuccess);">
                    <input type="file" name="file"/>
                    <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                    <button type="button" class="br-button ui-state-default ui-corner-all"
                            onclick="Plate.ui.cancelPlateInputFormUpload();">Cancel
                    </button>
                  </form>
                  <iframe id='plateform_target_upload' name='plateform_target_upload' style='display: none'></iframe>
                  <div id="plateform_statusdiv"></div>
                  <div id="plateform_import"></div>
                </td>
              </tr>
            </table>
          </div>

          <div id="importPlateElements"></div>
          <table cellpadding="0" cellspacing="0" border="0" class="display" id="plateElementsTable"></table>
        </span>
      <br/>
    </div>
  </div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>