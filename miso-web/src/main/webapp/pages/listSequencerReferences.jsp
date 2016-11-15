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
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css" />
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>" type="text/css" />

<div id="maincontent">
  <div id="contentcolumn">
    
    <h1>Sequencers</h1>
    
    <div id="tabs">
      <ul>
        <li>
          <a href="#tab-1"><span>Current</span></a>
        </li>
        <li>
          <a href="#tab-2"><span>Retired</span></a>
        </li>
      </ul>
      
      <div id="tab">
        <div id="tab-1"></div>
        <div id="tab-2"></div>
        <table cellpadding="0" cellspacing="0" border="0" class="display" id="listingSequencersTable"></table>      
      </div>
    </div>
    
  </div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery("#tabs").tabs({
      select: function (event, ui) {
        // filter by active sequencers (active in tab-1, inactive in tab-2)
        jQuery('#listingSequencersTable').dataTable().fnFilter((ui.index == 0 ? true : false), 5);
      }
    });
    Sequencer.ui.createListingSequencersTable();
  });
</script>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>