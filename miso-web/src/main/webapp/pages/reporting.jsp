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


<%--   Old Reporting Form
<form:form method="POST" autocomplete="off">
<h1>Reports</h1>

<div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
    <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">Reports can be generated using the selection criteria
    below, or by selecting a previously saved reporting query file.
</div>
<a href="javascript:void(0);" onclick='processQueryParameters()'>Process Query Parameters</a>
<h2>Select parameters</h2>

<div id="tablelist" class="checklist">
    <c:forEach items="${tables}" var="table">
        <input type="checkbox" id="table_${table}" value="${table}"
               onclick="toggleAddTableToQuerySet(this);"/>${table}<br/>
    </c:forEach>
</div>

<div id="querydiv" class="note">
    <h2>Selected query parameters</h2>

</div>

<br/>

<h2>Select a previously saved query</h2>
</form:form>    --%>
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
  checked = false;
  // check/uncheck all
  function checkAll(field) {
    if (checked == false) {
      checked = true
    }
    else {
      checked = false
    }

    for (i = 0; i < field.length; i++)
      field[i].checked = checked;
  }
  //shift check

  jQuery(document).ready(function() {
    prepareTable();
  });

</script>

<%@ include file="../footer.jsp" %>