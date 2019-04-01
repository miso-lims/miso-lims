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
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">
<div id="maincontent">
    <div id="contentcolumn">
        <h1>Dashboard</h1>

        <script type="text/javascript">
            favouriteWorkflows = ${favouriteWorkflows};
        </script>

        <miso:tiles-ajax id="tiles_barcode" target="barcode"/>
        <miso:tiles-ajax id="tiles_instrument_status" target="instrument_status"/>
        <miso:tiles-ajax id="tiles_workflow" target="workflow"/>
        <miso:tiles-ajax id="tiles_project" target="project"/>
        <miso:tiles-ajax id="tiles_run" target="run"/>
    </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
