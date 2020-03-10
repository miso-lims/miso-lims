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

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Array
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="arrayForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    var dto = ${empty arrayJson ? '{}' : arrayJson};
    var config = {};
    <c:if test="${pageMode eq 'create'}">
      config.arrayModels = ${arrayModels};
    </c:if>
    FormUtils.createForm('arrayForm', 'save', dto, 'array', config);
    Utils.ui.updateHelpLink(FormTarget.array.getUserManualUrl());
  });
</script>

<c:if test="${pageMode eq 'edit'}">
  <br/>
  <h1>Samples</h1>
  <div id="arraySamplesSection">
    <div id="arraySamplesVisual" class="unselectable" style="float:left;margin:20px;"></div>
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
            <td>Barcode:</td><td><span id="selectedBarcode"></span></td>
          </tr>
          <tr>
            <td></td>
            <td>
              <button id="removeSelected" class="ui-state-default" onclick="SampleArray.removeSelected()">Remove Sample</button>
            </td>
          </tr>
          <tr>
            <td>Search:</td>
            <td>
              <input id="searchField" type="text"/>
              <button id="search" class="ui-state-default" onclick="SampleArray.searchSamples()">Lookup</button>
            </td>
          </tr>
          <tr>
            <td>Results:</td>
            <td>
              <img id="ajaxLoader" src="/styles/images/ajax-loader.gif" class="fg-button hidden"/>
              <select id="resultSelect"/></select>
              <button id="updateSelected" class="ui-state-default" onclick="SampleArray.updatePosition()">Update Position</button>
            </td>
          </tr>
      </table>
      <ul class="warning" id="warningMessages"></ul>
    </div>
  </div>
  
  <div style="clear:both;">
    <table id="listingSamplesTable" class="display"></table>
  </div>
  
  <miso:list-section id="list_arrayruns" name="Array Runs" target="arrayrun" items="${arrayRuns}"/>
  
  <br/>
  <h1>Changes</h1>
  <table id='changelog' class='display no-border ui-widget-content'></table>

  <script>
  jQuery('#searchField').keyup(function(event) {
    if(event.which == "13"){ // enter key
      jQuery('#search').click();
    }
  });

  jQuery('#searchField').on('paste', function(e) {
    window.setTimeout(function() {
      jQuery('#search').click();
    }, 100);
  });
  
  jQuery(document).ready(function() {
    SampleArray.setArrayJson(${arrayJson});
  });
  </script>
</c:if>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
