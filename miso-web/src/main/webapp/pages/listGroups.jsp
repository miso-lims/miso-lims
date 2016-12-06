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
    <h1>
      <div id="totalCount">
      </div>
    </h1>
    <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
    </form>
    <br/>

    <sec:authorize access="hasRole('ROLE_TECH')">
      <a href="<c:url value="/miso/tech/group/new"/>" class="add">Add Group</a>
    </sec:authorize>

    <sec:authorize access="hasRole('ROLE_ADMIN')">
      <a href="<c:url value="/miso/admin/group/new"/>" class="add">Add Group</a>
    </sec:authorize>

    <br/>
    <table class="list" id="table">
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
              <a href='<c:url value="/miso/tech/group/${group.groupId}"/>'>Edit</a>
            </sec:authorize>

            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <a href='<c:url value="/miso/admin/group/${group.groupId}"/>'>Edit</a>
            </sec:authorize>
          </td>
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
        jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Groups");
      }
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>