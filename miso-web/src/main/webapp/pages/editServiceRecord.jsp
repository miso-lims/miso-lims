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
<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css" />

<script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/stats_ajax.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

<script type="text/javascript" src="<c:url value='/scripts/parsley/parsley.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/sequencer_service_record_validation.js?ts=${timestamp.time}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/jquery/timepicker/js/jquery-ui-timepicker-addon.min.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/timepicker/css/jquery-ui-timepicker-addon.min.css'/>"
        type="text/css">

<div id="maincontent">
  <div id="contentcolumn">
    <form:form id="service_record_form" data-parsley-validate="" action="/miso/stats/sequencer/servicerecord" method="POST" commandName="serviceRecord" autocomplete="off">
      <sessionConversation:insertSessionConversationId attributeName="serviceRecord"/>
      <h1>
        <c:choose>
          <c:when test="${serviceRecord.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose>
        Service Record
        <button onclick="validate_service_record();" class="fg-button ui-state-default ui-corner-all">Save</button>
      </h1>
      <div class="breadcrumbs">
        <ul>
          <li>
            <a href="<c:url value='/miso/'/>">Home</a>
          </li>
          <li>
            <a href='<c:url value="/miso/stats"/>'>Sequencer References</a>
          </li>
          <li>
            <a href='<c:url value="/miso/stats/sequencer/${serviceRecord.sequencerReference.id}"/>'>${serviceRecord.sequencerReference.name}</a>
          </li>
        </ul>
      </div>
      <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
        <div id="note_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notediv" class="note" style="display:none;">A Service Record is a record of maintenance performed 
      on a sequencer
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
          <td>
            <c:choose>
              <c:when test="${serviceRecord.id != 0}">${serviceRecord.id}</c:when>
              <c:otherwise><i>Unsaved</i></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Sequencer Reference:</td>
          <td><a href='<c:url value="/miso/stats/sequencer/${serviceRecord.sequencerReference.id}"/>'>${serviceRecord.sequencerReference.name}</a></td>
        </tr>
        <tr>
          <td class="h">Title:*</td>
          <td><form:input id="title" path="title" name="title"/><span id="titlecounter" class="counter"></span></td>
        </tr>
        <tr>
          <td class="h">Details:</td>
          <td><form:input id="details" path="details" name="details"/><span id="detailscounter" class="counter"></span></td>
        </tr>
        <tr>
          <td class="h">Serviced By:*</td>
          <td><form:input id="servicedByName" path="servicedByName" name="servicedByName"/><span id="servicedbynamecounter" class="counter"></span></td>
        </tr>
        <tr>
          <td class="h">Phone:</td>
          <td><form:input id="phone" path="phone" name="phone"/><span id="phonecounter" class="counter"></span></td>
        </tr>
        <tr>
          <td class="h">Service Date:*</td>
          <td>
            <form:input path="serviceDate" id="servicedatepicker" placeholder="DD/MM/YYYY"/>
            <script type="text/javascript">
              Utils.ui.addDatePicker("servicedatepicker");
            </script>
          </td>
        </tr>
        <tr>
          <td class="h">Sequencer Shutdown Time:</td>
          <td>
            <form:input path="shutdownTime" id="shutdownTime" placeholder="DD/MM/YYYY HH:mm"/>
            <script type="text/javascript">
              Utils.ui.addDateTimePicker("shutdownTime");
            </script>
          </td>
        </tr>
        <tr>
          <td class="h">Sequencer Restored Time:</td>
          <td>
            <form:input path="restoredTime" id="restoredTime" placeholder="DD/MM/YYYY HH:mm"/>
            <script type="text/javascript">
              Utils.ui.addDateTimePicker("restoredTime");
            </script>
          </td>
        </tr>
      </table>
      <br/>
    </form:form>
    
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#upload_arrowclick'), 'uploaddiv');">
      Attachments
      <div id="upload_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="uploaddiv" class="simplebox" style="display:none;">
      <c:choose>
        <c:when test="${serviceRecord.id != 0}">
          <table class="in">
            <tr>
              <td>
                <form method='post'
                    id='ajax_upload_form'
                    action="<c:url value="/miso/upload/servicerecord"/>"
                    enctype="multipart/form-data"
                    target="target_upload"
                    onsubmit="Utils.fileUpload.fileUploadProgress('ajax_upload_form', 'statusdiv', ServiceRecord.ui.serviceRecordFileUploadSuccess);">
                  <input type="hidden" name="serviceRecordId" value="${serviceRecord.id}"/>
                  <input type="file" name="file"/>
                  <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                </form>
                <iframe id='target_upload' name='target_upload' src='' style='display: none'></iframe>
                <div id="statusdiv"></div>
              </td>
            </tr>
          </table>
        </c:when>
        <c:otherwise>
          You may upload files after the service record has been saved.
        </c:otherwise>
      </c:choose>
    </div>
    
    <c:if test="${serviceRecord.id != 0}">
      <div id="servicerecordfiles">
        <c:forEach items="${serviceRecordFiles}" var="file">
          <div id='btnPanel' style='float: left; width: 32px;'>
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <table>
                <tr>
                  <td class="misoicon" onclick="ServiceRecord.ui.deleteFile(${serviceRecord.id}, ${file.key});">
                    <span class="ui-icon ui-icon-trash"></span>
                  </td>
                </tr>
              </table>
            </sec:authorize>
          </div>
          <a class="listbox" href="<c:url value='/miso/download/servicerecord/${serviceRecord.id}/${file.key}'/>">
            <div onMouseOver="this.className='boxlistboxhighlight'" onMouseOut="this.className='boxlistbox'" class="boxlistbox" style='margin-left: 32px;'>
              ${file.value}
            </div>
          </a>
        </c:forEach>
      </div>
    </c:if>
    
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>