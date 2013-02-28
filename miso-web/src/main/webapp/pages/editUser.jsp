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
        <form:form action="/miso/admin/user" method="POST" commandName="user" autocomplete="off">
          <sessionConversation:insertSessionConversationId attributeName="user"/>
            <h1><c:choose><c:when
                    test="${not empty user.userId}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
                User
                <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
            </h1>
            <table class="in">
                <tr>
                    <td class="h">User ID:</td>
                    <td>${user.userId}</td>
                </tr>
                <tr>
                    <td>Full name:</td>
                    <td>
                        <sec:authorize access="hasRole('ROLE_ADMIN')">
                            <form:input path="fullName"/>
                        </sec:authorize>

                        <sec:authorize access="hasRole('ROLE_TECH')">
                            ${user.fullName}
                        </sec:authorize>
                    </td>
                </tr>
                <tr>
                    <td>Login name:</td>
                    <td>
                        <sec:authorize access="hasRole('ROLE_ADMIN')">
                            <form:input path="loginName"/>
                        </sec:authorize>

                        <sec:authorize access="hasRole('ROLE_TECH')">
                            ${user.loginName}
                        </sec:authorize>
                    </td>
                </tr>
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <tr>
                        <td>Password:</td>
                        <td><form:password path="password" showPassword="false"/></td>
                        <%-- <a href="/changePassword">Change</a></td> --%>
                    </tr>
                    <tr>
                        <td>Admin?:</td>
                        <td><form:checkbox path="admin"/></td>
                    </tr>
                    <tr>
                        <td>Internal?:</td>
                        <td><form:checkbox path="internal"/></td>
                    </tr>
                    <tr>
                        <td>External?:</td>
                        <td><form:checkbox path="external"/></td>
                    </tr>
                    <tr>
                        <td>Active?:</td>
                        <td><form:checkbox path="active"/></td>
                    </tr>
                </sec:authorize>
                <tr>
                    <td>Groups:</td>
                        <%-- <td><form:checkboxes items="${groups}" path="groups" itemValue="groupId" itemLabel="name"/></td> --%>
                    <td>
                        <div id="groups" class="checklist">
                            <form:checkboxes items="${groups}" path="groups"
                                             itemLabel="name"
                                             itemValue="groupId"/>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>Supplemental Roles:</td>
                        <%-- <td><form:checkboxes items="${roles}" path="roles"/></td> --%>
                    <td>
                        <div id="roles" class="checklist">
                            <form:checkboxes items="${roles}" path="roles"/>
                        </div>
                    </td>
                </tr>
            </table>
        </form:form>
    </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>