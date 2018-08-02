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

<form:form id="workset-form" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8">
<h1>
  <c:choose>
    <c:when test="${pageMode eq 'create'}">Create</c:when>
    <c:otherwise>Edit</c:otherwise>
  </c:choose> Workset
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Workset.validateAndSave()">Save</button>
</h1>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid</p>
  <div class="generalErrors"></div>
</div>
<br/>

<div id="worksetInfo">
  <table class="in">
    <tr>
      <td class="h">Workset ID:</td>
      <td><span id="id">Unsaved</span></td>
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
    <c:if test="${pageMode eq 'edit'}">
      <tr>
        <td class="h">Created By:</td>
        <td><span id="creator"></span></td>
      </tr>
    </c:if>
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
});
</script>
</form:form>

<c:if test="${pageMode eq 'edit'}">
  <miso:list-section id="list_samples" name="Samples" target="sample" items="${samples}" config="{worksetId: ${worksetId}}"/>
  <miso:list-section id="list_libraries" name="Libraries" target="library" items="${libraries}" config="{worksetId: ${worksetId}}"/>
  <miso:list-section id="list_dilutions" name="Dilutions" target="dilution" items="${dilutions}" config="{worksetId: ${worksetId}}"/>
  
  <script>
  jQuery(document).ready(function() {
    Workset.setWorksetJson(${worksetJson});
  });
  </script>
</c:if>

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
