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

<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.popup.js'/>"></script>
<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">
<h1>
  <span id="totalProjectCount"> Projects
  </span>
</h1>

<table cellpadding="0" cellspacing="0" border="0" class="display" id="listingProjectsTable">
</table>
</div>

<script type="text/javascript">
var fun, state = "tree";

jQuery(document).ready(function () {
  jQuery("#traftrigger").colorbox({width: "90%", inline: true, href: "#trafpanel"});
  Project.ui.createListingProjectsTable();
});


jQuery(function () {
  var theTable = jQuery("#table");

  jQuery("#filter").keyup(function () {
    jQuery.uiTableFilter(theTable, this.value + ' ' + jQuery('#progressFilter').val());
    writeTotalNo();
    jQuery('table.overviewSummary tr').show();
  });

  jQuery("#progressFilter").change(function () {
    jQuery.uiTableFilter(theTable, this.value + ' ' + jQuery('#filter').val());
    writeTotalNo();
    jQuery('table.overviewSummary tr').show();
  });

  jQuery('#filter-form').submit(
      function () {
        theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
        return false;
      }).focus(); //Give focus to input field
});

function writeTotalNo() {
  jQuery('#totalProjectCount').html(jQuery('#table>tbody>tr:visible').length.toString() + " Projects");
}

</script>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
