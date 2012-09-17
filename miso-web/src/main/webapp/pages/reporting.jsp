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

<h1>
  <div id="title">Reports</div>
</h1>
<div id="reportTable">
  <br/>
  <input type="radio" id="reportTypeProject" onchange="changeReportType(this);" name="reportType" value="Project"/>
  Project

  <input type="radio" id="reportTypeSample" onchange="changeReportType(this);" name="reportType" value="Sample"/>
  Sample

  <input type="radio" id="reportTypeRun" onchange="changeReportType(this);" name="reportType" value="Run"/>Run

  <hr/>

  Please select the type of report above to begin.
</div>


<script type="text/javascript">
  jQuery(document).ready(function() {
    Reports.ui.prepareTable();
  });
</script>

<%@ include file="../footer.jsp" %>