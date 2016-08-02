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
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      Unfulfilled Pool Orders
    </h1>

    <div class="dataTables_wrapper" role="grid">
    <table cellpadding="0" cellspacing="0" border="0" class="display dataTable" id="listingPoolOrderTable">
       <thead><tr><th></th><th>Alias</th><th></th><th></th><th></th><th>Completed</th><th>Remaining</th></tr></thead>
       <tbody>
      </tbody>
    </table>
    </div>
    <script type="text/javascript">
       jQuery('#listingPoolOrderTable').html('');
       jQuery('#listingPoolOrderTable').dataTable({
         "aaData": [
           <c:forEach items="${ordercompletions}" var="poolGroup">
            <c:forEach items="${poolGroup.value}" var="parameterGroup">[
              '<a href="/miso/pool/${poolGroup.key.id}">${poolGroup.key.name}</a>', 
              '<a href="/miso/pool/${poolGroup.key.id}">${poolGroup.key.alias}</a>',
              '${parameterGroup.key.platform.nameAndModel}',
              '${parameterGroup.key.name}',
              <c:forEach items="${ordercompletionheadings}" var="heading">
                '${parameterGroup.value[heading].numPartitions}',
              </c:forEach>
              '${parameterGroup.value.getRemaining()}'
          ],
           </c:forEach>
         </c:forEach>
         ],
         "aoColumns": [
           { "sTitle": "Pool Name"},
           { "sTitle": "Alias"},
           { "sTitle": "Platform"},
           { "sTitle": "Sequencing Parameters"},
           <c:forEach items="${ordercompletionheadings}" var="heading">
           { "sTitle": "${heading.key}"},
           </c:forEach>
           { "sTitle": "Remaining"}
         ],
         "bJQueryUI": true,
         "bAutoWidth": false,
         "iDisplayLength": 25,
         "sPaginationType": "full_numbers",
         "aaSorting": [
           [0, "desc"]
         ],
         "fnDrawCallback": function (oSettings) {
           jQuery('#listingPoolOrderTable_paginate').find('.fg-button').removeClass('fg-button');
         }
       });
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
