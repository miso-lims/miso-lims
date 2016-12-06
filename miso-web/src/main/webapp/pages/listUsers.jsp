<%@ include file="../header.jsp" %>

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

<div id="maincontent">
  <div id="contentcolumn">
    <sec:authorize access="hasRole('ROLE_ADMIN')">
      <h1>${total} Logged-in Users</h1>
      <table class="list">
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

    <h1>
      <div id="totalCount">
      </div>
    </h1>
    <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
    </form>
    <br/>
    <sec:authorize access="hasRole('ROLE_ADMIN')">
      <a href="<c:url value="/miso/admin/user/new"/>" class="add">Add User</a>
    </sec:authorize>
    <br/>
    <table class="list" id="table">
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
            <a href='<c:url value="/miso/tech/user/${user.userId}"/>'>Edit</a></td>
          </sec:authorize>

          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <a href='<c:url value="/miso/admin/user/${user.userId}"/>'>Edit</a></td>
          </sec:authorize>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        writeTotalNo();
        jQuery("#table").tablesorter({
          headers: {
            2: {
              sorter: false
            }
          }
        });
      });

      jQuery(function () {
        var theTable = jQuery("#table");

        jQuery("#filter").keyup(function () {
          jQuery.uiTableFilter(theTable, this.value);
          writeTotalNo();
        });

        jQuery('#filter-form').submit(function () {
          theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
          return false;
        }).focus(); //Give focus to input field
      });

      function writeTotalNo() {
        jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Users");
      }
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>