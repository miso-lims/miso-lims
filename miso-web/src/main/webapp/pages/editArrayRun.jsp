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

<form:form id="arrayRun-form" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8">
<h1>
  <c:choose>
    <c:when test="${pageMode eq 'create'}">Create</c:when>
    <c:otherwise>Edit</c:otherwise>
  </c:choose> Array Run
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all" onclick="ArrayRun.validateAndSave()">Save</button>
</h1>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid</p>
  <div class="generalErrors"></div>
</div>
<br/>

<div id="arrayRunInfo">
  <table class="in">
    <tr>
      <td class="h">Array Run ID:</td>
      <td><span id="id">Unsaved</span></td>
    </tr>
    <tr>
      <td class="h">Instrument:*</td>
      <td>
      <c:choose>
        <c:when test="${pageMode eq 'create'}">
          <select id="instrument">
            <option value="">SELECT</option>
            <c:forEach items="${arrayScanners}" var="instrument">
              <option value="${instrument.id}">${instrument.name}</option>
            </c:forEach>
          </select>
        </c:when>
        <c:otherwise><span id="instrument"></span></c:otherwise>
      </c:choose>
      <div id="instrumentError" class="errorContainer"></div>
      </td>
    </tr>
    <tr>
      <td class="h">Alias:*</td>
      <td>
        <input type="text" id="alias"/><span id="aliasCounter" class="counter"></span>
        <div id="aliasError" class="errorContainer"></div>
      </td>
    </tr>
    <tr>
      <td class="h">Description:</td>
      <td>
        <input type="text" id="description"/><span id="descriptionCounter" class="counter"></span>
        <div id="descriptionError" class="errorContainer"></div>
      </td>
    </tr>
    <tr>
      <td class="h">Run Path:</td>
      <td>
        <input type="text" id="filePath"/><span id="filePathCounter" class="counter"></span>
        <div id="filePathError" class="errorContainer"></div>
      </td>
    </tr>
    <tr>
      <td class="h">Array:</td>
      <td>
        <input type="hidden" id="array"><input type="hidden" id="arrayAlias">
        <span id="arrayLabel">Not set</span>
      </td>
    </tr>
    <tr>
      <td class="h">Array Search:</td>
      <td id="arraySearchContainer">
        <button type="button" id="arraySearch" class="ui-state-default" onclick="ArrayRun.searchArrays()" style="float: right; padding: 3px; width: 100px;">Search</button>
        <img src="/styles/images/ajax-loader.gif" class="fg-button arraySearchLoader" style="float: right; visibility: hidden;">
        <div style="overflow: hidden;">
          <input id="arraySearchField" style="width: 100%; height: 100%; padding: 3px;" type="text">
        </div>
        
        <button type="button" id="arraySet" class="ui-state-default" onclick="ArrayRun.setArray()" style="float: right; padding: 3px; width: 100px;">Apply</button>
        <img src="/styles/images/ajax-loader.gif" class="fg-button arraySearchLoader" style="float: right; visibility: hidden;">
        <div style="overflow: hidden;">
          <select id="arraySearchResults" style="width: 100%; margin-right: 3px;">
            <option value="-1" selected="selected">None</option>
          </select>
        </div>
        <div id="arrayError" class="errorContainer"></div>
      </td>
    </tr>
    <tr>
      <td valign="top">Status:</td>
      <td>
        <c:forEach items="${healthTypes}" var="healthType">
          <label><input type="radio" name="health" onchange="ArrayRun.checkForCompletionDate()" data-parsley-multiple="health" value="${healthType}" <c:if test="${healthType eq 'Unknown'}">checked="checked"</c:if>>${healthType}</label>
        </c:forEach>
        <div id="statusError" class="errorContainer"></div>
        <table class="list" id="runStatusTable">
          <thead>
          <tr>
            <th>Start Date</th>
            <th>Completion Date</th>
            <th>Last Updated</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>
              <input type="text" id="startDate" />
              <div id="startDateError" class="errorContainer"></div>
            </td>
            <td>
              <input type="text" id="completionDate" />
              <div id="completionDateError" class="errorContainer"></div>
            </td>
            <td>
              <span id="lastModified"></span>
            </td>
          </tr>
          </tbody>
        </table>
        <script type="text/javascript">
        Utils.ui.addDatePicker("startDate");
        Utils.ui.addDatePicker("completionDate");
        </script>
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

  jQuery('#description').simplyCountable({
    counter: '#descriptionCounter',
    countType: 'characters',
    maxCount: ${maxLengths['description']},
    countDirection: 'down'
  });

  jQuery('#filePath').simplyCountable({
    counter: '#filePathCounter',
    countType: 'characters',
    maxCount: ${maxLengths['filePath']},
    countDirection: 'down'
  });
  
  ArrayRun.userIsAdmin = ${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')};
  ArrayRun.setRunJson(${pageMode eq 'create' ? null : arrayRunJson});
  
  jQuery('#arraySearchField').keyup(function(event) {
    if(event.which == "13"){ // enter key
      jQuery('#arraySearch').click();
    }
  });

  jQuery('#arraySearchField').on('paste', function(e) {
    window.setTimeout(function() {
      jQuery('#arraySearch').click();
    }, 100);
  });
});
</script>
</form:form>

<h1>Samples</h1>
<table id="listingSamplesTable" class="display"></table>

<c:if test="${pageMode eq 'edit'}">
  <br/>
  <h1>Changes</h1>
  <table id='changelog' class='display no-border ui-widget-content'></table>
</c:if>

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
