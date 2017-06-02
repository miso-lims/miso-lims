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
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>MISO Run Scanning Server</title>
    <link type="text/css" rel="stylesheet" href="<c:url value="/resources/runscanner.css"/>">
  </head>

  <body>
    <nav>
      <a href="<c:url value="/"/>">Status</a>
      <c:forEach items="${collections}" var="collection"><a href="<c:url value="/list/${collection}"/>">${collection}</a></c:forEach>
    </nav>
    <div>
    <h1>${collection}</h1>
      <c:choose>
        <c:when test="${empty runs}"><p>No directories.</p></c:when>
        <c:otherwise>
          <table>
            <tr><th>Name</th><th>Path</th></tr>
            <c:forEach items="${runs}" var="run"><tr><td>${run.name}</td><td>${run.path}</td></tr></c:forEach>
          </table>
        </c:otherwise>
      </c:choose>
    <div>
  </body>
</html>
