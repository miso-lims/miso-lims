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

<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      Pool Orders
    </h1>

    <div id="tabs"></div>
    <script type="text/javascript">
       var ordercompletions = ${ordercompletionJSON};
       var tabHeader = '<ul>' + ordercompletions.map(function(ocd) { return '<li><a href="#tab-' + ocd.htmlElement + '"><span>' + ocd.humanName + '</span></a></li>'; }).join('') + '</ul>';
       var tabs = ordercompletions.map(function(ocd) {
         return '<div id="tab-' + ocd.htmlElement + '"><h1><div>' + ocd.humanName + '</div></h1>' +
           '<div class="dataTables_wrapper" role="grid"><table cellpadding="0" cellspacing="0" border="0" class="display dataTable" id="listingPoolOrderTable' +
           ocd.htmlElement + '"><thead><tr><th></th><th>Alias</th><th></th><th></th><th></th><th>Completed</th><th>Remaining</th></tr></thead><tbody></tbody></table></div></div>';
       }).join('');
       document.getElementById('tabs').innerHTML = tabHeader + tabs;
       for (var i = 0; i < ordercompletions.length; i++) {
         jQuery('#listingPoolOrderTable' + ordercompletions[i].htmlElement).html('');
         jQuery('#listingPoolOrderTable' + ordercompletions[i].htmlElement).dataTable({
           "aaData": ordercompletions[i].data ,
           "aoColumns": [
             { "sTitle": "Pool Name"},
             { "sTitle": "Alias"},
             { "sTitle": "Platform"},
             { "sTitle": "Sequencing Parameters"}
           ].concat(ordercompletions[i].headings.map(function(name) { return { "sTitle": name }; })).concat([
             { "sTitle": "Remaining"},
             { "sTitle": "Last Updated"}
           ]),
           "bJQueryUI": true,
           "bAutoWidth": false,
           "iDisplayLength": 25,
           "sPaginationType": "full_numbers",
           "aaSorting": [
             [0, "desc"]
           ],
           "fnDrawCallback": function (oSettings) {
             jQuery('#listingPoolOrderTable' + ordercompletions[i].htmlElement + '_paginate').find('.fg-button').removeClass('fg-button');
           }
         });
       }
        jQuery(document).ready(function () {
          jQuery("#tabs").tabs();
        });
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
