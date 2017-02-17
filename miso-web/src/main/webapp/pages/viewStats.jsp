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

<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 19-Jul-2010
  Time: 16:13:12
  
--%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">

    <c:if test="${not empty clusterStatus}">
      <h1>${referenceName} Cluster Status</h1>
      ${clusterStatus}
    </c:if>

    <c:if test="${not empty stats}">
      <h1>${referenceName} Runs</h1>

      <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
      </form>
      <br/>

      <table class="list" id="runStatusTable">
        <thead>
        <tr>
          <th>Run Name</th>
          <th>Alias</th>
          <th>Instrument Name</th>
          <th>Health</th>
          <th>Start Date</th>
          <th>Last Updated</th>
          <th class="fit">View Stats</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${runs}" var="run">
          <tr>
            <td>${run.name}</td>
            <td>${run.alias}</td>
            <td>${run.sequencerReference.name}</td>
            <td>${run.health.key}</td>
            <td>${run.startDate}</td>
            <td><fmt:formatDate value="${run.lastUpdated}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
            <td class="misoicon"
                onclick="window.location.href='<c:url value="/miso/run/${run.id}"/>'">
              <span class="ui-icon ui-icon-pencil"/></td>
          </tr>
        </c:forEach>
        </tbody>
      </table>

      <script type="text/javascript">
        jQuery(document).ready(function() {
          jQuery("#runStatusTable").tablesorter({
            headers: { }
          });
        });

        jQuery(function() {
          var theTable = jQuery("#runStatusTable");

          jQuery("#filter").keyup(function() {
            jQuery.uiTableFilter(theTable, this.value);
          });

          jQuery('#filter-form').submit(
            function() {
              theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
              return false;
            }).focus(); //Give focus to input field
        });
      </script>
    </c:if>

    <c:if test="${not empty incompleteRuns}">
      <h1>${referenceName} Incomplete Runs</h1>
      <c:forEach items="${incompleteRuns}" var="incomp">
        <a href="<c:url value="/miso/stats/${fn:toLowerCase(platformtype)}/${referenceId}/${incomp}"/>">${incomp}</a><br/>
      </c:forEach>
      <br/>
    </c:if>

    <c:if test="${empty referenceId}">
      <h1>${platformtype} Sequencers</h1>
      <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
        <a href='javascript:void(0);' class="add" onclick="Sequencer.ui.insertSequencerReferenceRow(); return false;">Add
          Sequencer</a><br/>
      </c:if>

      <div id="addSequencerReference"></div>
      <form id='addReferenceForm'>
        <table class="list" id="sequencerReferenceTable">
          <thead>
          <tr>
            <th class="fit">ID</th>
            <th>Name</th>
            <th>Platform</th>
            <th>Hostname</th>
            <th>View Stats</th>
            <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
              <th class="fit">Edit</th>
              <th class="fit">DELETE</th>
            </c:if>
          </tr>
          </thead>
          <tbody>
          <c:if test="${not empty sequencerReferences}">
            <c:forEach items="${sequencerReferences}" var="ref">
              <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
                <td class="fit">${ref.id}</td>
                <td>${ref.name}</td>
                <td>${ref.platform.nameAndModel}</td>
                <td>${ref.FQDN}</td>
                <c:choose>
                <c:when test="${ref.platform.platformType.key eq 'Illumina'}">
                  <td><a href="<c:url value="/miso/stats/illumina/${ref.id}"/>">View</a></td>
                </c:when>
                <c:when test="${ref.platform.platformType.key eq 'LS454'}">
                  <td><a href="<c:url value="/miso/stats/ls454/${ref.id}"/>">View</a></td>
                </c:when>
                <c:when test="${ref.platform.platformType.key eq 'Solid'}">
                  <td><a href="<c:url value="/miso/stats/solid/${ref.id}"/>">View</a> (<a href="http://${ref.FQDN}${path} }">SETS</a>)</td>
                </c:when>
                <c:when test="${ref.platform.platformType.key eq 'PacBio'}">
                  <td><a href="<c:url value="/miso/stats/pacbio/${ref.id}"/>">View</a></td>
                </c:when>
                <c:otherwise>
                  <td><a href="http://${ref.FQDN}${path}">View</a></td>
                </c:otherwise>
                </c:choose>
                <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <td class="misoicon"
                      onclick="window.location.href='<c:url value="/miso/stats/sequencer/${ref.id}"/>'"><span
                          class="ui-icon ui-icon-pencil"/></td>
                  <td class="misoicon" onclick="Sequencer.ui.deleteSequencerReference(${ref.id}, Utils.page.pageReload);"><span
                          class="ui-icon ui-icon-trash"/></td>
                </c:if>
              </tr>
            </c:forEach>
          </c:if>
          </tbody>
        </table>
      </form>
    </c:if>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>

