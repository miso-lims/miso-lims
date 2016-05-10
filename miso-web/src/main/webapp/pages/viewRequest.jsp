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

<h1>Request ${request.name}</h1>

<p>Some stuff here. Current execution complete, etc.</p>

<ul>
  <c:forEach var="execCount" begin="1" end="${request.executionCount}" step="1">
    <li><a href='<c:url value="/miso/request/view/${request.id}/${execCount}"/>'>View results for
      execution ${execCount}</a></li>
  </c:forEach>
</ul>

<h2>Notes</h2>
<ul>
  <c:forEach items="${request.notes}" var="note">
    <c:choose>
      <c:when test="${not note.internalOnly}">
        <li><fmt:formatDate value="${note.creationDate}"/>, ${note.owner.fullName}: ${note.text}</li>
      </c:when>
      <c:otherwise>
        <sec:authorize access="hasRole('ROLE_INTERNAL')">
          <li><fmt:formatDate value="${note.creationDate}"/>, ${note.owner.fullName}: ${note.text}</li>
        </sec:authorize>
      </c:otherwise>
    </c:choose>
  </c:forEach>
</ul>

<h2>Create New Note</h2>

<form action='<c:url value="/miso/note"/>' method="POST">
  <table>
    <tr>
      <td>Note:</td>
      <td>
        <input type="hidden" name="requestId" value="${request.id}"/>
        <textarea name="text">Type note in here</textarea>
      </td>
    </tr>
    <sec:authorize access="hasRole('ROLE_INTERNAL')">
      <tr>
        <td>Internal only?:</td>
        <td><input type="checkbox" name="internalOnly" value="true"/></td>
      </tr>
    </sec:authorize>
    <tr>
      <td></td>
      <td><input type="submit"/></td>
    </tr>
  </table>
</form>

<%@ include file="../footer.jsp" %>
