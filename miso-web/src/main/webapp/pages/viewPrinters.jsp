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
     <h1>Printers</h1>
     <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
       <a href='javascript:void(0);' class="add" onclick="Print.ui.showAddPrinter(); return false;">Add
         Printer</a><br/>
     </c:if>

     <table class="list" id="printerTable">
       <thead>
       <tr>
         <th>Printer</th>
         <th>Driver</th>
         <th>Backend</th>
         <sec:authorize access="hasRole('ROLE_INTERNAL')">
           <th>Availability</th>
         </sec:authorize>
         <sec:authorize access="hasRole('ROLE_ADMIN')">
           <th>Delete</th>
         </sec:authorize>
       </tr>
       </thead>
       <tbody>

       <c:forEach items="${printers}" var="printer">
         <tr>
           <td>${printer.name}</td>
           <td>${printer.driver}</td>
           <td>${printer.backend}</td>

           <sec:authorize access="hasRole('ROLE_INTERNAL')">
             <td>
               <a href='javascript:void(0);' onclick="Print.service.setPrinterState(${printer.id}, !${printer.enabled});">
                 <c:choose>
                   <c:when test="${printer.enabled}">Disable</c:when>
                   <c:otherwise>Enable</c:otherwise>
                 </c:choose>
               </a>
             </td>
           </sec:authorize>
           <sec:authorize access="hasRole('ROLE_ADMIN')">
             <td id='edit-${service.name}' class="misoicon">
             <a href='javascript:void(0);'
                onclick="if (confirm('Delete printer ${printer.name}?')) { Print.service.deletePrinter(${printer.id}); } return false;"><span class="ui-icon ui-icon-circle-close"></span></a>
             </td>
           </sec:authorize>
         </tr>
       </c:forEach>

       </tbody>
     </table>

<div id="add-printer-dialog" title="Add Printer" hidden="true">
Name: <input type="text" id="addName" /><br/>
Driver: <select id="driver"><c:forEach items="${drivers}" var="driver"><option value="${driver.ordinal()}">${driver.name()}</option></c:forEach></select><br/>
Backend: <select id="backend" onchange="Print.ui.changeBackend()"><c:forEach items="${backends}" var="backend"><option value="${backend.ordinal()}">${backend.name()}</option></c:forEach></select><br/>
<div id="backendConfiguration">
</div>
</div>

     <script type="text/javascript">
       Print.backends = ${backendsJSON};
       jQuery('.miso-button').hover(
         function () {
           jQuery(this).addClass('ui-state-hover');
         },
         function () {
           jQuery(this).removeClass('ui-state-hover');
         }
       );
     </script>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>

