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
<script type="text/javascript" src="<c:url value='/scripts/printer_ajax.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">
  <div id="contentcolumn">
    <c:choose>
      <c:when test="${not empty barcodePrinter}">
        <h1>Printer ${barcodePrinter.name}</h1>
        <table class="list" id="printerTable">
          <thead>
          <tr>
            <th>Job ID</th>
            <th>Date</th>
            <th>User</th>
            <th>Status</th>
            <th>Elements</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${printJobs}" var="job">
            <tr>
              <td>${job.jobId}</td>
              <td>${job.printDate}</td>
              <td>${job.printUser.loginName}</td>
              <td>${job.status}</td>
              <td><c:forEach items="${job.queuedElements}" var="element">
                ${element}<br/>
              </c:forEach>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>

        <script type="text/javascript">
          jQuery(document).ready(function () {
            jQuery("#printerTable").tablesorter({
              headers: {
                3: {
                  sorter: false
                },
                4: {
                  sorter: false
                }
              }
            });
          });
        </script>
      </c:when>
      <c:otherwise>
        <h1>Printers</h1>
        <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
          <a href='javascript:void(0);' class="add" onclick="Print.ui.getPrinterFormEntities(); return false;">Add
            Printer Service</a><br/>
        </c:if>

        <form id='addPrinterForm'>
          <table class="list" id="printerTable">
            <thead>
            <tr>
              <th>Printer Service</th>
              <th>Host</th>
              <th>Type</th>
              <th>Recent Print Jobs</th>
              <th>Enabled</th>
              <th>Schema</th>
              <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <th>Edit</th>
              </c:if>
            </tr>
            </thead>
            <tbody>

            <c:forEach items="${barcodePrinters}" var="service">
              <tr>
                <td>${service.name}</td>
                <td id='host-${service.name}'>${service.printContext.host}</td>
                <td>${service.printContext.name}</td>
                <td>
                  <a href='<c:url value="/miso/admin/configuration/printers/barcode/${service.name}"/>'>View</a>
                </td>

                <td>
                  <c:choose>
                    <c:when test="${service.enabled}">
                          <span class="miso-button ui-state-default ui-corner-all ui-icon ui-icon-check"
                                onclick="Print.service.disablePrintService('${service.name}');">Disable</span>
                    </c:when>
                    <c:otherwise>
                          <span class="miso-button ui-state-default ui-corner-all ui-icon ui-icon-closethick"
                                onclick="Print.service.enablePrintService('${service.name}');">Enable</span>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td>${service.barcodableSchema.name}</td>
                <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <td id='edit-${service.name}' class="misoicon"/>
                  <a href='javascript:void(0);'
                     onclick="Print.ui.changePrinterServiceRow('${service.name}');"><span
                      class="ui-icon ui-icon-pencil"/></a>
                  </td>
                </c:if>
              </tr>
            </c:forEach>

            </tbody>
          </table>
        </form>

        <script type="text/javascript">
          jQuery('.miso-button').hover(
            function () {
              jQuery(this).addClass('ui-state-hover');
            },
            function () {
              jQuery(this).removeClass('ui-state-hover');
            }
          );
        </script>
      </c:otherwise>
    </c:choose>

    <c:choose>
      <c:when test="${not empty userPrintJobs}">
        <h1>Print Jobs</h1>
        <table class="list" id="printerTable">
          <thead>
          <tr>
            <th>Job ID</th>
            <th>Printer Name</th>
            <th>Date</th>
            <th>Status</th>
            <th>Elements</th>
            <th>Reprint</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${userPrintJobs}" var="job">
            <tr>
              <td>${job.jobId}</td>
              <td>${job.printService.name}</td>
              <td>${job.printDate}</td>
              <td>${job.status}</td>
              <td><c:forEach items="${job.queuedElements}" var="element">
                ${element}<br/>
              </c:forEach>
              </td>
              <td><span class="miso-button ui-state-default ui-corner-all ui-icon ui-icon-print"
                        onclick="Print.service.reprintJob(${job.jobId});">Reprint</span></td>
            </tr>
          </c:forEach>
          </tbody>
        </table>

        <script type="text/javascript">
          jQuery(document).ready(function () {
            jQuery("#printerTable").tablesorter({
              headers: {
                4: {
                  sorter: false
                },
                5: {
                  sorter: false
                }
              }
            });
          });
        </script>
      </c:when>
      <c:otherwise>
        <h1>Print Jobs</h1>
        You have no print jobs.
      </c:otherwise>
    </c:choose>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>

