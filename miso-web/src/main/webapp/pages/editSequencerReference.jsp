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
<script type="text/javascript" src="<c:url value='/scripts/sequencer_reference_validation.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">
  <div id="contentcolumn">
    <form:form id="sequencer_reference_form" data-parsley-validate="" action="/miso/stats/sequencer" method="POST" commandName="sequencerReference" autocomplete="off">
      <sessionConversation:insertSessionConversationId attributeName="sequencerReference"/>
      <h1>
        Edit Sequencer Reference
        <button onclick="validate_sequencer_reference();" class="fg-button ui-state-default ui-corner-all">Save</button>
      </h1>
      <div class="breadcrumbs">
        <ul>
          <li>
            <a href="<c:url value='/miso/'/>">Home</a>
          </li>
          <li>
            <div class="breadcrumbsbubbleInfo">
              <div class="trigger">
                <a href='<c:url value="/miso/stats"/>'>Sequencer References</a>
              </div>
              <div class="breadcrumbspopup">
                All Sequencer References
              </div>
            </div>
          </li>
        </ul>
      </div>
      <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
        <div id="note_arrowclick" class="toggleLeft"></div>
      </div>
      <div id="notediv" class="note" style="display:none;">A Sequencer Reference represents a sequencer. This may be
        a machine physically attached to a sequencer itself, or more commonly, a cluster or storage machine that
        holds the run directories.
      </div>
      
      <div class="bs-callout bs-callout-warning hidden">
        <h2>Oh snap!</h2>
        <p>This form seems to be invalid!</p>
      </div>
      
      <h2>Sequencer Information</h2>

      <table class="in">
        <tr>
          <td class="h">Sequencer Reference ID:</td>
          <td>
            <c:choose>
              <c:when test="${sequencerReference.id != 0}">${sequencerReference.id}</c:when>
              <c:otherwise><i>Unsaved</i></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td class="h">Platform:</td>
          <td>${sequencerReference.platform.nameAndModel}</td>
        </tr>
        <tr>
          <td class="h">Serial Number:</td>
          <td><form:input path="serialNumber" id="serialNumber" name="serialNumber"/><span id="serialnumbercounter" class="counter"></span></td>
        </tr>
        <tr>
          <td class="h">Name:*</td>
          <td><form:input path="name" id="name" name="name"/><span id="nameCounter" class="counter"></span></td>
        </tr>
        <tr>
          <td>IP Address:*</td>
          <td>
            <input type="text" id="ipAddress" name="ipAddress" value="${trimmedIpAddress}"/>
            <input type="hidden" value="on" name="_ipAddress"/>
          </td>
        </tr>
        <tr>
          <td class="h">Commissioned:</td>
          <td>
            <form:input path="dateCommissioned" id="datecommissionedpicker" placeholder="DD/MM/YYYY"/>
            <script type="text/javascript">
              Utils.ui.addDatePicker("datecommissionedpicker");
            </script>
          </td>
        </tr>
        <tr>
          <td>Status:</td>
          <td>
            <input type="radio" name="status" value="production" onchange="Sequencer.ui.showStatusRows();" <c:if test="${sequencerReference.dateDecommissioned == null}">checked</c:if>/> Production
            <input type="radio" name="status" value="retired" onchange="Sequencer.ui.showStatusRows();" <c:if test="${sequencerReference.dateDecommissioned != null && sequencerReference.upgradedSequencerReference == null}">checked</c:if>/> Retired
            <input type="radio" name="status" value="upgraded" onchange="Sequencer.ui.showStatusRows();" <c:if test="${sequencerReference.dateDecommissioned != null && sequencerReference.upgradedSequencerReference != null}">checked</c:if>/> Upgraded
          </td>
        </tr>
        <tr id="decommissionedRow">
          <td class="h">Decommissioned:*</td>
          <td>
            <form:input path="dateDecommissioned" id="datedecommissionedpicker" placeholder="DD/MM/YYYY"/>
            <script type="text/javascript">
              Utils.ui.addDatePicker("datedecommissionedpicker");
            </script>
          </td>
        </tr>
        <tr id="upgradedReferenceRow">
          <td class="h">Upgraded To:*</td>
          <td>
            <form:select id="upgradedSequencerReference" path="upgradedSequencerReference" onchange="updateUpgradedSequencerReferenceLink();">
              <form:option value="0">(choose)</form:option>
              <form:options items="${otherSequencerReferences}" itemLabel="name" itemValue="id"/>
            </form:select>
            <span id="upgradedSequencerReferenceLink"></span>
          </td>
        </tr>
      </table>
      <script type="text/javascript">
        jQuery(document).ready(function() {
	      Sequencer.ui.showStatusRows();
	    });
      </script>
      <br/>
    </form:form>
    
    
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#records_arrowclick'), 'recordsdiv');">
      <c:choose>
        <c:when test="${fn:length(sequencerServiceRecords) == 1}">1 Service Record</c:when>
        <c:otherwise>${fn:length(sequencerServiceRecords)} Service Records</c:otherwise>
      </c:choose>
      <div id="records_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="recordsdiv" style="display:none;">
      <h1>Service Records</h1>
      <ul class="sddm">
        <li>
          <a onmouseover="mopen('recordmenu')" onmouseout="mclosetime()">
            Options
            <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
          </a>
          <div id="recordmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
            <c:choose>
              <c:when test="${sequencerReference.dateDecommissioned == null}">
                <a href='<c:url value="/miso/stats/sequencer/servicerecord/new/${sequencerReference.id}"/> '>Add new Service Record</a>
              </c:when>
              <c:otherwise>
                <a onclick="alert('Error: Cannot add service records to a retired sequencer')">Add new Service Record</a>
              </c:otherwise>
            </c:choose>
          </div>
        </li>
      </ul>
      <div style="clear:both">
        <table class="list" id="records_table">
          <thead>
            <tr>
              <th>Service Date</th>
              <th>Title</th>
              <th>Serviced By</th>
              <th>Phone</th>
              <th class="fit">Edit</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${sequencerServiceRecords}" var="record">
              <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                <td>${record.serviceDate}</td>
                <td>${record.title}</td>
                <td>${record.servicedByName}</td>
                <td>${record.phone}</td>
                <td class="misoicon"
                  onclick="window.location.href='<c:url value="/miso/stats/sequencer/servicerecord/${record.id}"/>'"><span
                  class="ui-icon ui-icon-pencil"></span></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#records_table').dataTable({
          "aaSorting": [
            [0, 'desc']
          ],
          "aoColumns": [
            { "sType": 'date' },
            { "sType": 'string' },
            { "sType": 'string' },
            { "sType": 'string' },
            { "bSortable": false }
          ],
          "iDisplayLength": 50,
          "bJQueryUI": true,
          "bRetrieve": true
        });
      });
    </script>
    
    
    <br/>
    <a id="runs"></a>
    <div class="sectionDivider">
      <c:choose>
        <c:when test="${fn:length(sequencerRuns) == 1}">1 Run</c:when>
        <c:otherwise>${fn:length(sequencerRuns)} Runs</c:otherwise>
      </c:choose>
    </div>
    <h1>Runs</h1>
    <div style="clear:both">
      <table class="list" id="run_table">
        <thead>
          <tr>
            <th>Run Alias</th>
            <th>Status</th>
            <th>Started At</th>
            <th>Completed At</th>
            <th class="fit">Edit</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${sequencerRuns}" var="run">
            <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
              <td>${run.alias}</td>
              <td>${run.status.health.key}</td>
              <td>${run.status.startDate}</td>
              <td>${run.status.completionDate}</td>
              <td class="misoicon"
                  onclick="window.location.href='<c:url value="/miso/run/${run.id}"/>'"><span
                  class="ui-icon ui-icon-pencil"></span></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#run_table').dataTable({
          "aaSorting": [
            [2, 'desc']
          ],
          "aoColumns": [
            { "sType": 'string' },
            { "sType": 'string' },
            { "sType": 'date' },
            { "sType": 'date' },
            { "bSortable": false }
          ],
          "iDisplayLength": 50,
          "bJQueryUI": true,
          "bRetrieve": true
        });
      });
    </script>
    
  </div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#name').simplyCountable({
      counter: '#nameCounter',
      countType: 'characters',
      maxCount: ${maxLengths['name']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>