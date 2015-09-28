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

<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <nav class="navbar navbar-default" role="navigation">
       <div class="navbar-header">
          <span class="navbar-brand navbar-center">
            <div id="totalCount"> Submissions</div>
          </span>
       </div>
    </nav>
    <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text" class="form-control">
    </form>
    <br/>
    <a href="<c:url value="/miso/submission/new"/>" class="add">Create Submission</a>
    <table class="table table-bordered table-striped" id="table">
      <thead>
      <tr>
        <th class="fit">Submission ID</th>
        <th class="fit">Alias</th>
        <th>Created Date</th>
        <th>Submitted Date</th>
        <th class="fit">Verified</th>
        <th class="fit">Completed</th>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${submissions}" var="submission">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td class="fit">
              ${submission.id}
          </td>
          <td class="fit">
              ${submission.alias}
          </td>
          <td class="fit">
              ${submission.creationDate}
          </td>
          <td class="fit">
              ${submission.submissionDate}
          </td>
          <c:choose>
          <c:when test="${submission.verified}">
          <td class="fit yes_green">
            </c:when>
            <c:otherwise>
          <td class="fit no_red">
            </c:otherwise>
            </c:choose>
              ${submission.verified}
          </td>
          <c:choose>
          <c:when test="${submission.completed}">
          <td class="fit yes_green">
            </c:when>
            <c:otherwise>
          <td class="fit no_red">
            </c:otherwise>
            </c:choose>
              ${submission.completed}
          </td>
          <td class="misoicon"
              onclick="window.location.href='<c:url value="/miso/submission/${submission.id}"/>'"><span
              class="fa fa-pencil-square-o fa-lg"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
</div>
<script type="text/javascript">
  jQuery(document).ready(function () {
    writeTotalNo();
    jQuery("#table").tablesorter({
      headers: {}
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
    jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Submissions");
  }
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>