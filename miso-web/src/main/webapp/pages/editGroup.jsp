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
    <form:form action="/miso/admin/group" method="POST" commandName="group" autocomplete="off">

      <sessionConversation:insertSessionConversationId attributeName="group"/>

      <h1><c:choose><c:when
          test="${not empty group.groupId}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose>
        Group
        <button type="submit" class="fg-button ui-state-default ui-corner-all">Save</button>
      </h1>
      <table class="in">
        <tr>
          <td class="h">Group ID:</td>
          <td>${group.groupId}</td>
        </tr>
        <tr>
          <td class="h">Name:</td>
          <td><form:input path="name"/></td>
        </tr>
        <tr>
          <td class="h">Description:</td>
          <td><form:input path="description"/></td>
        </tr>
        <tr>
          <td class="h">Users:</td>
            <%--<td><c:forEach items="${group.users}" var="user">${user.loginName}</c:forEach></td>--%>
          <td>
            <div id="users" class="checklist">
              <form:checkboxes items="${users}" path="users"
                               itemLabel="fullName"
                               itemValue="userId"/>
            </div>
          </td>
        </tr>
      </table>
    </form:form>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>