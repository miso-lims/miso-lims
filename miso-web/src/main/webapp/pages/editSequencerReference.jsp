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
<script type="text/javascript" src="<c:url value='/scripts/sample_validation.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">
  <div id="contentcolumn">
    <form:form action="/miso/stats/sequencer" method="POST" commandName="sequencerReference" autocomplete="off">
      <sessionConversation:insertSessionConversationId attributeName="sequencerReference"/>
      <h1>
        Edit Sequencer Reference
        <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
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
          <td><input type="text" id="serialNumber" name="serialNumber" value="${sequencerReference.serialNumber}"/></td>
        </tr>
        <tr>
          <td class="h">Name:</td>
          <td><form:input id="name" path="name"/><span id="nameCounter" class="counter"></span></td>
        </tr>
        <tr>
          <td>IP Address:</td>
          <td>
            <input type="text" id="ipAddress" name="ipAddress" value="${trimmedIpAddress}"/>
            <input type="hidden" value="on" name="_ipAddress"/>
          </td>
        </tr>
        <tr>
          <td class="h">Commissioned</td>
          <td><fmt:formatDate pattern="dd/MM/yyyy" value="${sequencerReference.dateCommissioned}"/></td>
        </tr>
        <tr>
          <td>Status</td>
          <td style="font-weight:bold">
            <c:choose>
              <c:when test="${sequencerReference.upgradedSequencerReference != null}">Upgraded</c:when>
              <c:when test="${sequencerReference.dateDecommissioned != null}">Retired</c:when>
              <c:otherwise>Production</c:otherwise>
            </c:choose>
          </td>
        </tr>
        <c:if test="${sequencerReference.dateDecommissioned != null}">
          <tr>
            <td class="h">Decommissioned</td>
            <td><fmt:formatDate pattern="dd/MM/yyyy" value="${sequencerReference.dateDecommissioned}"/></td>
          </tr>
        </c:if>
        <c:if test="${sequencerReference.upgradedSequencerReference != null}">
          <tr>
            <td class="h">Upgraded To</td>
            <td><a href="<c:url value='/miso/stats/sequencer/${sequencerReference.upgradedSequencerReference.id}'/>">${sequencerReference.upgradedSequencerReference.name}</a></td>
          </tr>
        </c:if>
      </table>
      <br/>
    </form:form>
    
    
    <script type="text/javascript">
	  jQuery(document).ready(function () {
	    jQuery('#runCountTotal').html(jQuery('#run_table>tbody>tr:visible').length.toString() + (jQuery('#run_table>tbody>tr:visible').length == 1 ? " Run" : " Runs"));
      });
    </script>
    <br/>
    <a id="runs"></a>
    <h1>
      <span id="runCountTotal"></span>
    </h1>
    
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