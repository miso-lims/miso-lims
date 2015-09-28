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
    <sec:authorize access="hasRole('ROLE_ADMIN')">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              ${total} Logged-in Users
            </span>
         </div>
      </nav>

      <table class="table table-bordered table-striped">
        <thead>
        <tr>
          <th>Username</th>
          <th>IsAccountNonExpired</th>
          <th>IsCredentialsNonExpired</th>
          <th>IsAccountNonLocked</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${loggedInUsers}" var="liuser">
          <tr>
            <td><c:out value="${liuser.username}"/></td>
            <td><c:out value="${liuser.accountNonExpired}"/></td>
            <td><c:out value="${liuser.credentialsNonExpired}"/></td>
            <td><c:out value="${liuser.accountNonLocked}"/></td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </sec:authorize>

    <nav class="navbar navbar-default" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">
          <div>${fn:length(users)} Users</div>
        </span>
      </div>
      <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
        <ul class="nav navbar-nav navbar-right">
          <li id="pro-menu" class="dropdown">
            <a id="pro-drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
            <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="pro-drop1">
              <sec:authorize access="hasRole('ROLE_ADMIN')">
              <li role="presentation"><a href="<c:url value="/miso/admin/user/new"/>">Add User</a></li>
              </sec:authorize>
            </ul>
          </li>
        </ul>
      </div>
    </nav>

    <table class="table table-bordered table-striped display" id="table">
      <thead>
      <tr>
        <th class="fit">User ID</th>
        <th>User Name</th>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${users}" var="user">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td class="fit">
              ${user.userId}
          </td>
          <td>
            <b>${user.fullName}</b>
          </td>
          <td class="fit">
            <sec:authorize access="hasRole('ROLE_TECH')">
            <a href='<c:url value="/miso/tech/user/${user.userId}"/>'><span class="fa fa-pencil-square-o fa-lg"></span></a></td>
          </sec:authorize>

          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <a href='<c:url value="/miso/admin/user/${user.userId}"/>'><span class="fa fa-pencil-square-o fa-lg"></span></a></td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery("#table").dataTable();
      });
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>