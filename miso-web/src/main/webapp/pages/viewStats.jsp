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
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              ${referenceName} Cluster Status<br/>
              ${clusterStatus}
            </span>
         </div>
      </nav>
    </c:if>

    <c:if test="${not empty stats}">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              ${referenceName} Runs
            </span>
         </div>
      </nav>

      <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text" class="form-control">
      </form>
      <br/>

      <table class="table table-bordered table-striped" id="runStatusTable">
        <thead>
        <tr>
          <th>Run Name</th>
          <th>Instrument Name</th>
          <th>Health</th>
          <th>Start Date</th>
          <th>Last Updated</th>
          <th class="fit">View Stats</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${stats}" var="stat">
          <tr>
            <td>${stat.runName}</td>
            <td>${stat.instrumentName}</td>
            <td>${stat.health.key}</td>
            <td>${stat.startDate}</td>
            <td><fmt:formatDate value="${stat.lastUpdated}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
            <td class="misoicon"
                onclick="window.location.href='<c:url value="/miso/stats/${fn:toLowerCase(platformtype)}/${referenceId}/${stat.runName}"/>'">
              <span class="fa fa-pencil-square-o fa-lg"/></td>
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
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              ${referenceName} Incomplete Runs
            </span>
         </div>
      </nav>

      <c:forEach items="${incompleteRuns}" var="incomp">
        <a href="<c:url value="/miso/stats/${fn:toLowerCase(platformtype)}/${referenceId}/${incomp}"/>">${incomp}</a><br/>
      </c:forEach>
      <br/>
    </c:if>

    <c:choose>
      <c:when test="${not empty runStatus}">
        <nav class="navbar navbar-default" role="navigation">
           <div class="navbar-header">
              <span class="navbar-brand navbar-center">
                ${runName}
              </span>
           </div>
        </nav>

        <table class="table table-bordered table-striped" id="runStatusTable">
          <thead>
          <tr>
            <th>Run Name</th>
            <th>Health</th>
            <th>Start Date</th>
            <th>Last Updated</th>
            <th class="fit">View Run</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>${runStatus.runName}</td>
            <td>${runStatus.health.key}</td>
            <td>${runStatus.startDate}</td>
            <td><fmt:formatDate value="${runStatus.lastUpdated}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
            <td class="misoicon" onclick="window.location.href='<c:url value="/miso/run/${runId}"/>'"><span
                    class="fa fa-pencil-square-o fa-lg"/></td>
          </tr>

          </tbody>
        </table>

        <c:if test="${not empty runStatus.xml}">
          ${statusXml}
        </c:if>

      </c:when>
      <c:otherwise>
        <c:if test="${empty referenceId}">
          <nav class="navbar navbar-default" role="navigation">
             <div class="navbar-header">
                <span class="navbar-brand navbar-center">
                  ${platformtype} Sequencer References
                </span>
             </div>
          </nav>

          <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
            <a href='javascript:void(0);' class="add" onclick="Sequencer.ui.insertSequencerReferenceRow(); return false;">Add
              Sequencer Reference</a><br/>
          </c:if>

          <div id="addSequencerReference"></div>
          <form id='addReferenceForm'>
            <table class="table table-bordered table-striped" id="sequencerReferenceTable">
              <thead>
              <tr>
                <th class="fit">ID</th>
                <th>Name</th>
                <th>Platform</th>
                <th>Hostname</th>
                <th>Available</th>
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
                      <c:when test="${ref.available}">
                        <td style="background-color:green"></td>
                        <c:choose>
                          <c:when test="${ref.platform.platformType.key eq 'Illumina'}">
                            <td><a href="<c:url value="/miso/stats/illumina/${ref.id}"/>">View</a></td>
                          </c:when>
                          <c:when test="${ref.platform.platformType.key eq 'LS454'}">
                            <td><a href="<c:url value="/miso/stats/ls454/${ref.id}"/>">View</a></td>
                          </c:when>
                          <c:when test="${ref.platform.platformType.key eq 'Solid'}">
                            <td><a href="<c:url value="/miso/stats/solid/${ref.id}"/>">View</a> (<a
                                    href="http://${ref.FQDN}${path}">SETS</a>)
                            </td>
                          </c:when>
                          <c:otherwise>
                            <td><a href="http://${ref.FQDN}${path}">View</a></td>
                          </c:otherwise>
                        </c:choose>
                      </c:when>
                      <c:otherwise>
                        <td style="background-color:red"></td>
                        <td><i>Unavailable</i></td>
                      </c:otherwise>
                    </c:choose>
                    <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                      <td class="misoicon"
                          onclick="window.location.href='<c:url value="/miso/stats/sequencer/${ref.id}"/>'"><span
                              class="fa fa-pencil-square-o fa-lg"/></td>
                      <td class="misoicon" onclick="Sequencer.ui.deleteSequencerReference(${ref.id}, pageReload);"><span
                              class="fa fa-trash-o fa-lg"/></td>
                    </c:if>
                  </tr>
                </c:forEach>
              </c:if>
              </tbody>
            </table>
          </form>
        </c:if>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>

