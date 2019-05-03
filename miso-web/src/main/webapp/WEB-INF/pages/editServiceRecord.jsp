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
<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="<c:url value='/scripts/jquery/timepicker/js/jquery-ui-timepicker-addon.min.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/timepicker/css/jquery-ui-timepicker-addon.min.css'/>"
        type="text/css">

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      <c:choose>
        <c:when test="${serviceRecord.id != 0}">Edit</c:when>
        <c:otherwise>Create</c:otherwise>
      </c:choose>
      Service Record
      <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
    </h1>
    <div class="breadcrumbs">
      <ul>
        <li>
          <a href="<c:url value='/miso/'/>">Home</a>
        </li>
        <li>
          <a href='<c:url value="/miso/instruments"/>'>Instruments</a>
        </li>
        <li>
          <a href='<c:url value="/miso/instrument/${serviceRecord.instrument.id}"/>'>${serviceRecord.instrument.name}</a>
        </li>
      </ul>
    </div>
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#noteArrowClick'), 'noteDiv');">Quick Help
      <div id="noteArrowClick" class="toggleLeft"></div>
    </div>
    <div id="noteDiv" class="note" style="display:none;">A Service Record is a record of maintenance performed 
    on an instrument
    </div>
    
    <form:form id="serviceRecordForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        FormUtils.createForm('serviceRecordForm', 'save', ${serviceRecordDto}, 'servicerecord', {instrumentPositions: ${instrumentPositions}});
      });
    </script>
    <br>
    
    <c:choose>
      <c:when test="${serviceRecord.id != 0}">
        <miso:attachments item="${serviceRecord}"/>
      </c:when>
      <c:otherwise>
        You can attach files after the service record has been saved.
      </c:otherwise>
    </c:choose>
    
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>