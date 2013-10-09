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

<h1>Activities</h1>

<p>This list only shows manual activities for which you have the rights to work with.</p>

<br/><br/>
<table class="list">
  <c:forEach items="${activities}" var="activity">
    <tr>
      <td>
        <b>${activity.name}</b>
      </td>
      <td class="fit"><a href='<c:url value="/miso/activity/process/${activity.uniqueIdentifier}"/>'>Edit</a></td>
    </tr>
  </c:forEach>
</table>

<%@ include file="../footer.jsp" %>