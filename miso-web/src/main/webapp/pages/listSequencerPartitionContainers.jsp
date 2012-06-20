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
<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      <div id="totalCount"></div>
    </h1>
    <a href="<c:url value='/miso/container/new'/>" class="add">Create Partition Container</a>

    <form id="filter-form">Filter: <input name="filter" id="filter" value="" maxlength="30" size="30" type="text">
    </form>
    <br/>
    <table class="list" id="table">
      <thead>
      <tr>
        <th>ID Barcode</th>
        <th>Platform</th>
        <th>Last Associated Run</th>
        <th>Last Sequencer Used</th>
        <%--<th>Populated Partitions</th>--%>
        <th class="fit">Edit</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${containers}" var="container" varStatus="fCount">
        <tr onMouseOver="this.className='highlightrow'" onMouseOut="this.className='normalrow'">
          <td>${container.identificationBarcode}</td>
          <td>${container.platformType.key}</td>
          <td>
            <c:if test="${not empty container.run}">
              <a href='<c:url value="/miso/run/${container.run.runId}"/>'>${container.run.alias}</a>
            </c:if>
          </td>
          <td>
            <c:if test="${not empty container.run and not empty container.run.sequencerReference}">
              <a href='<c:url value="/miso/sequencer/${container.run.sequencerReference.id}"/>'>${container.run.sequencerReference.platform.nameAndModel}</a>
            </c:if>
          </td>
          <td class="misoicon"
              onclick="window.location.href='<c:url value="/miso/container/${container.containerId}"/>'"><span class="ui-icon ui-icon-pencil"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function() {
        writeTotalNo();
        jQuery("#table").tablesorter({
           headers: {
             5: {
               sorter: false
             }
           }
         });
      });

      jQuery(function() {
        var theTable = jQuery("#table");

        jQuery("#filter").keyup(function() {
          jQuery.uiTableFilter(theTable, this.value);
          writeTotalNo();
        });

        jQuery('#filter-form').submit(
          function() {
            theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
            return false;
          }).focus(); //Give focus to input field
      });

      function writeTotalNo() {
        jQuery('#totalCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Partition Containers");
      }
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>