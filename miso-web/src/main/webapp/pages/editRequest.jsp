<%@ include file="../header.jsp" %>

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

<div id="maincontent">
    <div id="contentcolumn">
        <form:form  action="/miso/request" method="POST" commandName="request" autocomplete="off">
            <h1><c:choose><c:when
                    test="${not empty request.requestId}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
                Request
                <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
            </h1>
            <table class="in">
                <tr>
                    <td>Request ID:</td>
                    <td>${request.requestId}</td>
                </tr>
                <tr>
                    <td>Project:</td>
                    <td>${request.project.name}</td>
                </tr>
                <tr>
                    <td>Creation date:</td>
                    <td><fmt:formatDate value="${request.creationDate}"/></td>
                </tr>
                <tr>
                    <td>Last execution date:</td>
                    <td><fmt:formatDate value="${request.lastExecutionDate}"/></td>
                </tr>
                <tr>
                    <td>Execution count:</td>
                    <td>${request.executionCount}</td>
                </tr>
                <tr>
                    <td>Name:</td>
                    <td><form:input path="name"/></td>
                </tr>
                <tr>
                    <td>Description:</td>
                    <td><form:input path="description"/></td>
                </tr>
                <tr>
                    <td>Protocol:</td>
                    <td>
                        <c:choose>
                            <c:when test="${empty request.protocolUniqueIdentifier}">
                                <form:select items="${protocols}" path="protocolUniqueIdentifier"
                                             itemValue="uniqueIdentifier"
                                             itemLabel="uniqueIdentifier"/>
                            </c:when>
                            <c:otherwise>${request.protocolUniqueIdentifier}</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
            <%@ include file="permissions.jsp" %>
        </form:form>
    </div>
</div>


<%@ include file="../footer.jsp" %>