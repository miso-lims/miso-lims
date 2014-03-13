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

<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 05-Jul-2011
  Time: 16:48:27

--%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">
<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery("#tabs").tabs();
  });
</script>
<h1>Pools</h1>

<div id="tabs">
<ul>
  <c:forEach items="${platformTypes}" var="pt" varStatus="c">
    <li><a href="#tab-${c.count}"><span>${pt} Pools</span></a></li>
  </c:forEach>
</ul>

<c:forEach items="${platformTypes}" var="pt" varStatus="c">
  <div id="tab-${c.count}">
    <h1>
      <div id="${pt}totalCount">${pt} Pools</div>
    </h1>

    <form id="filter-form${c.count}">Filter:
      <input name="filter${c.count}" id="filter${c.count}" value="" maxlength="30" size="30" type="text">
    </form>
    <br/>
    <table class="list" id="${pt}_table">
      <thead>
      <tr>
        <th>Name</th>
        <th>Alias</th>
        <th>Barcode</th>
        <th>Date Created</th>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${pools[pt]}" var="ipool">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${ipool.name}</td>
          <td>${ipool.alias}</td>
          <td>${ipool.identificationBarcode}</td>
          <td>${ipool.creationDate}</td>
          <td class="misoicon"
              onclick="window.location.href='<c:url value="/miso/pool/${ipool.id}"/>'"><span
              class="ui-icon ui-icon-pencil"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#ipu_arrowclick'), 'ipudiv');"
         style="font: bold 80% Helvetica;">Used pools
      <div id="ipu_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="ipudiv" class="simplebox ui-corner-all" style="display:none;">
      <table class="list" id="${pt}_used_table">
        <thead>
        <tr>
          <th>Name</th>
          <th>Alias</th>
          <th>Barcode</th>
          <th>Date Created</th>
          <th class="fit">Edit</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${usedpools[pt]}" var="ipool">
          <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
            <td>${ipool.name}</td>
            <td>${ipool.alias}</td>
            <td>${ipool.identificationBarcode}</td>
            <td>${ipool.creationDate}</td>
            <td class="misoicon"
                onclick="window.location.href='<c:url value="/miso/pool/${ipool.id}"/>'"><span
                class="ui-icon ui-icon-pencil"/></td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>

    <script type="text/javascript">
      jQuery(document).ready(function () {
        jQuery("#${pt}_table").tablesorter({
          headers: {
            1: {
              sorter: false
            }
          }
        });
      });

      jQuery(function () {
        var theTable = jQuery("#${pt}_table");

        jQuery("#filter${c.count}").keyup(function () {
          jQuery.uiTableFilter(theTable, this.value);
        });

        jQuery('#filter-form${c.count}').submit(function () {
          theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
          return false;
        }).focus(); //Give focus to input field
      });
    </script>
    <br/><br/>
  </div>
</c:forEach>

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>