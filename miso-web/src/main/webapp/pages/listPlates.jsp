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
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.popup.js'/>"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      <div id="totalCount"></div>
    </h1>
    <a href="<c:url value='/miso/plate/new'/>" class="add">Create Plate</a>

    <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
    </form>
    <br/>
    <table class="list" id="table">
      <thead>
      <tr>
        <th>Name</th>
        <th>Description</th>
        <th>Creation Date</th>
        <th>Size</th>
        <th>Material Type</th>
        <th>Tag Barcode</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${plates}" var="plate">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td><a href='<c:url value="/miso/plate/${plate.id}"/>'>${plate.name}</a></td>
          <td>${plate.description}</td>
          <td>${plate.creationDate}</td>
          <td>${plate.size}</td>
          <td>${plate.plateMaterialType}</td>
          <td>${plate.tagBarcode.sequence}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        writeTotalNo();
        jQuery("#table").tablesorter({
          headers: {
            5: {
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
        jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Plates");
      }
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>