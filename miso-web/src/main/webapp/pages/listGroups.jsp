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
          <div>${fn:length(groups)} Groups</div>
        </span>
      </div>
      <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
        <ul class="nav navbar-nav navbar-right">
          <li id="pro-menu" class="dropdown">
            <a id="pro-drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="pro-drop1">
              <sec:authorize access="hasRole('ROLE_TECH')">
              <li role="presentation"><a href="<c:url value="/miso/tech/group/new"/>">Add Group</a></li>
              </sec:authorize>
              <sec:authorize access="hasRole('ROLE_ADMIN')">
              <li role="presentation"><a href="<c:url value="/miso/admin/group/new"/>">Add Group</a></li>
              </sec:authorize>
            </ul>
          </li>
        </ul>
      </div>
    </nav>

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