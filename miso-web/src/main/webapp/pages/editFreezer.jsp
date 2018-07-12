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
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">

<div id="tab-1">

<form:form id="freezer-form" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8">
<h1>
  <c:choose>
    <c:when test="${pageMode eq 'create'}">Create</c:when>
    <c:otherwise>Edit</c:otherwise>
  </c:choose> Freezer
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Freezer.validateAndSave()">Save</button>
</h1>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid</p>
  <div id="generalErrors"></div>
</div>
<br/>

<div id="freezerInfo">
  <table class="in">
    <tr>
      <td class="h">Location ID:</td>
      <td><span id="id">Unsaved</span></td>
    </tr>
    <tr>
      <td class="h">Room:</td>
      <td>
        <select id="room">
          <option value="">SELECT</option>
          <c:forEach items="${rooms}" var="room">
            <option value="${room.id}">${room.alias}</option>
          </c:forEach>
        </select>
      <div id="roomError" class="errorContainer"></div>
      </td>
    </tr>
    <tr>
      <td class="h">Alias:</td>
      <td>
        <input type="text" id="alias"/><span id="aliasCounter" class="counter"></span>
        <div id="aliasError" class="errorContainer"></div>
      </td>
    </tr>
    <tr>
      <td class="h">Barcode:</td>
      <td>
        <input type="text" id="identificationBarcode"/><span id="identificationBarcodeCounter" class="counter"></span>
        <div id="identificationBarcodeError" class="errorContainer"></div>
      </td>
    </tr>
  </table>
</div>

<script>
jQuery(document).ready(function() {
  jQuery('#alias').simplyCountable({
    counter: '#aliasCounter',
    countType: 'characters',
    maxCount: ${maxLengths['alias']},
    countDirection: 'down'
  });

  jQuery('#identificationBarcode').simplyCountable({
    counter: '#identificationBarcodeCounter',
    countType: 'characters',
    maxCount: ${maxLengths['identificationBarcode']},
    countDirection: 'down'
  });
});
</script>
</form:form>

<c:if test="${!empty freezerJson}">
  <h1>Layout</h1>
  <div id="layoutSection" style="overflow:auto">
  
    <div id="freezerLayoutContainer" class="storageComponentContainer unselectable">
      <span class="storageComponentLabel">Freezer</span>
      <button type="button" class="ui-state-default storageComponentButton" onclick="Freezer.addFreezerStorage()">Add Storage</button>
      <div class="clearfix"></div>
      <table id="freezerLayout" class="storageComponent"></table>
    </div>
    
    <div id="levelTwoStorageContainer" class="storageComponentContainer unselectable">
      <span id="levelTwoStorageAlias" class="storageComponentLabel"></span>
      <div class="clearfix"></div>
      <table id="levelTwoStorageLayout" class="storageComponent"></table>
    </div>
    
  </div>
  <script>
  jQuery(document).ready(function() {
    Freezer.setFreezerJson(${freezerJson});
  });
  </script>
</c:if>

<miso:list-section id="list_box" name="Boxes" target="box" items="${boxes}" config="{'showFreezerLocation':true, 'boxUse':false, 'showStorageLocation':false}"/>

<br/>
<h1>Changes</h1>
<table id='changelog' class='display no-border ui-widget-content'></table>

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>