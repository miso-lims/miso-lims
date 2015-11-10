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

<div id="maincontent">
  <div id="contentcolumn">
    <form:form commandName="plate" autocomplete="off">
    <sessionConversation:insertSessionConversationId attributeName="plate"/>
    <nav class="navbar navbar-default" role="navigation">
       <div class="navbar-header">
          <span class="navbar-brand navbar-center">
            Import Plate
          </span>
       </div>
    </nav>

    <h2>Plate Information</h2>

    <div class="barcodes">
      <div class="barcodeArea ui-corner-all">
        <c:choose>
          <c:when test="${empty plate.locationBarcode}">
            <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span>
            <form:input path="locationBarcode" size="8" class="form-control"/>
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
          <td>
            <div class="input-group">
              <form:input path="description" class="form-control"/><span id="descriptioncounter" class="input-group-addon"></span>
            </div>
          </td>
        </tr>
        <tr>
          <td>Creation Date:</td>
          <td>
            <form:input path="creationDate" id="creationdatepicker" class="form-control"/>
            <script type="text/javascript">
              Utils.ui.addMaxDatePicker("creationdatepicker", 0);
            </script>
          </td>
        </tr>
        <tr>
          <td>Size:</td>
          <td>
            <form:input path="size" id="size" class="form-control"/>
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

      <nav id="navbar-el" class="navbar navbar-default navbar-static" role="navigation">
        <div class="navbar-header">
          <span id="totalCount" class="navbar-brand navbar-center">Elements</span>
        </div>
        <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
          <ul class="nav navbar-nav navbar-right">
            <li id="el-menu" class="dropdown">
              <a id="eldrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
              <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="eldrop1">
                <li role="presentation">
                  <a href="/miso/plate/export">Get Plate Input Form</a>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </nav>

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
          <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered display" id="plateElementsTable"></table>
        </span>
      <br/>
    </div>
  </div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>