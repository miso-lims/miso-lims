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
    <form:form id="serviceRecordForm" data-parsley-validate="" action="/miso/instrument/servicerecord" method="POST" commandName="serviceRecord" autocomplete="off">
      <sessionConversation:insertSessionConversationId attributeName="serviceRecord"/>
      <h1>
        <c:choose>
          <c:when test="${serviceRecord.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose>
        Service Record
        <button id="save" onclick="return ServiceRecord.validateServiceRecord();" class="fg-button ui-state-default ui-corner-all">Save</button>
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
      
      <div class="bs-callout bs-callout-warning hidden">
        <h2>Oh snap!</h2>
        <p>This form seems to be invalid!</p>
      </div>
      
      <h2>Service Record Information</h2>
      
      <br/>
      <table class="in">
        <tr>
          <td class="h">Service Record ID:</td>
          <td><span id="serviceRecordId">
            <c:choose>
              <c:when test="${serviceRecord.id != 0}">${serviceRecord.id}</c:when>
              <c:otherwise><i>Unsaved</i></c:otherwise>
            </c:choose>
          </span></td>
        </tr>
        <tr>
          <td class="h">Instrument:</td>
          <td><a href='<c:url value="/miso/instrument/${serviceRecord.instrument.id}"/>' id="instrumentName">${serviceRecord.instrument.name}</a></td>
        </tr>
        <tr>
          <td class="h">Title:*</td>
          <td>
            <form:input id="title" path="title" name="title"/><span id="titleCounter" class="counter"></span>
          </td>
        </tr>
        <tr>
          <td class="h">Details:</td>
          <td>
            <form:textarea id="details" path="details" name="details"/><span id="detailsCounter" class="counter"></span>
          </td>
        </tr>
        <tr>
          <td class="h">Serviced By:</td>
          <td>
            <form:input id="servicedByName" path="servicedByName" name="servicedByName"/><span id="servicedByNameCounter" class="counter"></span>
          </td>
        </tr>
        <tr>
          <td class="h">Reference Number:</td>
          <td>
            <form:input id="referenceNumber" path="referenceNumber" name="referenceNumber"/><span id="referenceNumberCounter" class="counter"></span>
          </td>
        </tr>
        <tr>
          <td class="h">Service Date:*</td>
          <td>
            <form:input path="serviceDate" id="serviceDatePicker" placeholder="YYYY-MM-DD"/>
            <script type="text/javascript">
              Utils.ui.addDatePicker("serviceDatePicker");
            </script>
          </td>
        </tr>
        <tr>
          <td class="h">Issue Start Time:</td>
          <td>
            <form:input path="startTime" id="startTime" placeholder="YYYY-MM-DD HH:mm"/>
            <script type="text/javascript">
              Utils.ui.addDateTimePicker("startTime");
            </script>
          </td>
        </tr>
        <tr>
          <td class="h">Instrument out of service?</td>
          <td><form:checkbox id="outOfService" path="outOfService"/></td>
        </tr>
        <tr>
          <td class="h">Issue End Time:</td>
          <td>
            <form:input path="endTime" id="endTime" placeholder="YYYY-MM-DD HH:mm"/>
            <script type="text/javascript">
              Utils.ui.addDateTimePicker("endTime");
            </script>
          </td>
        </tr>
      </table>
      <br/>
    </form:form>
    
    <script type="text/javascript">
      jQuery(document).ready(function () {
        // Attaches a Parsley form validator.
        Validate.attachParsley('#serviceRecordForm');
      });
	</script>

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

<c:if test="${miso:isAdmin()}">
  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery('#title').simplyCountable({
        counter: '#titleCounter',
        countType: 'characters',
        maxCount: ${maxLengths['title']},
        countDirection: 'down'
      });

      jQuery('#details').simplyCountable({
        counter: '#detailsCounter',
        countType: 'characters',
        maxCount: ${maxLengths['details']},
        countDirection: 'down'
      });

      jQuery('#servicedByName').simplyCountable({
        counter: '#servicedByNameCounter',
        countType: 'characters',
        maxCount: ${maxLengths['servicedBy']},
        countDirection: 'down'
      });

      jQuery('#referenceNumber').simplyCountable({
        counter: '#referenceNumberCounter',
        countType: 'characters',
        maxCount: ${maxLengths['referenceNumber']},
        countDirection: 'down'
      });
    });
  </script>
</c:if>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>