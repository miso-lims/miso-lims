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

<script type="text/javascript" src="<c:url value='/scripts/parsley/parsley.min.js'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/jquery/timepicker/js/jquery-ui-timepicker-addon.min.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/timepicker/css/jquery-ui-timepicker-addon.min.css'/>"
        type="text/css">

<div id="maincontent">
  <div id="contentcolumn">
    <form:form id="serviceRecordForm" data-parsley-validate="" action="/miso/stats/sequencer/servicerecord" method="POST" commandName="serviceRecord" autocomplete="off">
      <sessionConversation:insertSessionConversationId attributeName="serviceRecord"/>
      <h1>
        <c:choose>
          <c:when test="${serviceRecord.id != 0}">
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              Edit
            </sec:authorize>
          </c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose>
        Service Record
        <sec:authorize access="hasRole('ROLE_ADMIN')">
          <button onclick="return ServiceRecord.validateServiceRecord();" class="fg-button ui-state-default ui-corner-all">Save</button>
        </sec:authorize>
      </h1>
      <div class="breadcrumbs">
        <ul>
          <li>
            <a href="<c:url value='/miso/'/>">Home</a>
          </li>
          <li>
            <a href='<c:url value="/miso/sequencers"/>'>Sequencer References</a>
          </li>
          <li>
            <a href='<c:url value="/miso/sequencer/${serviceRecord.sequencerReference.id}"/>'>${serviceRecord.sequencerReference.name}</a>
          </li>
        </ul>
      </div>
      <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#noteArrowClick'), 'noteDiv');">Quick Help
        <div id="noteArrowClick" class="toggleLeft"></div>
      </div>
      <div id="noteDiv" class="note" style="display:none;">A Service Record is a record of maintenance performed 
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
          <td><a href='<c:url value="/miso/sequencer/${serviceRecord.sequencerReference.id}"/>'>${serviceRecord.sequencerReference.name}</a></td>
        </tr>
        <tr>
          <td class="h">Title:*</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input id="title" path="title" name="title"/><span id="titleCounter" class="counter"></span>
              </c:when>
              <c:otherwise>${serviceRecord.title}</c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Details:</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:textarea id="details" path="details" name="details"/><span id="detailsCounter" class="counter"></span>
              </c:when>
              <c:otherwise>
                <form:textarea id="details" path="details" name="details" disabled="true" style="color: black;"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Serviced By:*</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input id="servicedByName" path="servicedByName" name="servicedByName"/><span id="servicedByNameCounter" class="counter"></span>
              </c:when>
              <c:otherwise>${serviceRecord.servicedByName}</c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Reference Number:</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input id="referenceNumber" path="referenceNumber" name="referenceNumber"/><span id="referenceNumberCounter" class="counter"></span>
              </c:when>
              <c:otherwise>${serviceRecord.referenceNumber}</c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Service Date:*</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input path="serviceDate" id="serviceDatePicker" placeholder="DD/MM/YYYY"/>
                <script type="text/javascript">
                  Utils.ui.addDatePicker("serviceDatePicker");
                </script>
              </c:when>
              <c:otherwise>
                <fmt:formatDate value="${serviceRecord.serviceDate}" pattern="dd/MM/yyyy"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Sequencer Shutdown Time:</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input path="shutdownTime" id="shutdownTime" placeholder="DD/MM/YYYY HH:mm"/>
                <script type="text/javascript">
                  Utils.ui.addDateTimePicker("shutdownTime");
                </script>
              </c:when>
              <c:otherwise>
                <fmt:formatDate value="${serviceRecord.shutdownTime}" pattern="dd/MM/yyyy HH:mm"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Sequencer Restored Time:</td>
          <td>
            <c:choose>
              <c:when test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <form:input path="restoredTime" id="restoredTime" placeholder="DD/MM/YYYY HH:mm"/>
                <script type="text/javascript">
                  Utils.ui.addDateTimePicker("restoredTime");
                </script>
              </c:when>
              <c:otherwise>
                <fmt:formatDate value="${serviceRecord.restoredTime}" pattern="dd/MM/yyyy HH:mm"/>
              </c:otherwise>
            </c:choose>
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
    
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#uploadArrowClick'), 'uploadDiv');">
      Attachments
      <div id="uploadArrowClick" class="toggleLeft"></div>
    </div>
    <div id="uploadDiv" class="simplebox" style="display:none;">
      <sec:authorize access="hasRole('ROLE_ADMIN')">
        <c:choose>
          <c:when test="${serviceRecord.id != 0}">
            <table class="in">
              <tr>
                <td>
                  <form method='post'
                      id='ajaxUploadForm'
                      action="<c:url value="/miso/upload/servicerecord"/>"
                      enctype="multipart/form-data"
                      target="targetUpload"
                      onsubmit="Utils.fileUpload.fileUploadProgress('ajaxUploadForm', 'statusDiv', ServiceRecord.ui.serviceRecordFileUploadSuccess);">
                    <input type="hidden" name="serviceRecordId" value="${serviceRecord.id}"/>
                    <input type="file" name="file"/>
                    <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                  </form>
                  <iframe id='targetUpload' name='targetUpload' src='' style='display: none'></iframe>
                  <div id="statusDiv"></div>
                </td>
              </tr>
            </table>
          </c:when>
          <c:otherwise>
            You may upload files after the service record has been saved.
          </c:otherwise>
        </c:choose>
      </sec:authorize>
    </div>
    
    <c:if test="${serviceRecord.id != 0}">
      <div id="serviceRecordFiles">
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

<sec:authorize access="hasRole('ROLE_ADMIN')">
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
</sec:authorize>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>