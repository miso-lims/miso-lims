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
      <h1>Status</h1>
      <table>
        <tr><td>Uptime</td><td>${uptime}</td></tr>
        <tr><td>Is Configuration Good?</td><td>${isConfigurationGood}</td></tr>
        <tr><td>Last Configuration Read</td><td>${lastConfigurationRead}</td></tr>
        <tr><td>Scanning Enabled</td><td>${isScanningEnabled}</td></tr>
        <tr><td>Currently Scanning</td><td>${isScanningNow}</td></tr>
        <tr><td>Processed Runs</td><td>${finished}</td></tr>
        <tr><td>Waiting Runs</td><td>${scheduled}</td></tr>
      </table>

      <h1>Processors</h1>
      <table>
        <tr><th>Name</th><th>Platform</th></tr>
        <c:forEach items="${processors}" var="processor"><td>${processor.name}</td><td>${processor.platformType}</td></c:forEach>
      </table>

      <h1>Configuration</h1>
      <c:choose>
        <c:when test="${empty configurations}"><p>No configuration.</p></c:when>
        <c:otherwise>
          <table>
            <tr><th>Path</th><th>Processor Name</th><th>Platform</th><th>Time Zone</th><th>Valid?</th></tr>
            <c:forEach items="${configurations}" var="conf"><td>${conf.path}</td><td>${conf.processor.name}</td><td>${conf.processor.platformType}</td><td>${conf.timeZone.displayName}</td><td>${conf.valid}</td></c:forEach>
          </table>
        </c:otherwise>
      </c:choose>
    </div>
  </body>
</html>
