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
    <nav class="navbar navbar-default" role="navigation">
       <div class="navbar-header">
          <span class="navbar-brand navbar-center">
            <div id="totalCount"> Groups</div>
          </span>
       </div>
    </nav>
    <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
    </form>
    <br/>

    <sec:authorize access="hasRole('ROLE_TECH')">
      <a href="<c:url value="/miso/tech/group/new"/>" class="add">Add Group</a>
    </sec:authorize>

    <sec:authorize access="hasRole('ROLE_ADMIN')">
      <a href="<c:url value="/miso/admin/group/new"/>" class="add">Add Group</a>
    </sec:authorize>

    <table class="table table-bordered table-striped display" id="table">
      <thead>
      <tr>
        <th class="fit">Group ID</th>
        <th>Group Name</th>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${groups}" var="group">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td class="fit">
              ${group.groupId}
          </td>
          <td>
            <b>${group.name}</b>
          </td>
          <td class="fit">
            <sec:authorize access="hasRole('ROLE_TECH')">
              <a href='<c:url value="/miso/tech/group/${group.groupId}"/>'><span class="fa fa-pencil-square-o fa-lg"></span></a>
            </sec:authorize>

            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <a href='<c:url value="/miso/admin/group/${group.groupId}"/>'><span class="fa fa-pencil-square-o fa-lg"></span></a>
            </sec:authorize>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery('#table').dataTable();
      });
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>