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
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">
<div id="tab-1">


<h1>Index Search Tool</h1>

<p>Find index families containing some or all of the index sequences you enter.</p>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#toolhelp_arrowclick'), 'toolHelp');">Quick Help
  <div id="toolhelp_arrowclick" class="toggleLeft"></div>
</div>
<div id="toolHelp" class="note" style="display:none;">
  <p>Enter a series of index 1 and/or index 2 sequences and click the Search button. Index families containing any of these indices
  will be listed in the Results pane, along with the number of matching indices in each.</p>
</div>
<br>

<div style="display: table; width:100%;">
  <div style="display: table-row;">
    <div style="display: table-cell; width: 1px;">
      <label>Index 1 Sequences<br>
        <textarea id="indices1" rows="25" cols="30"></textarea>
      </label>
    </div>
    <div style="display: table-cell; width: 1px; padding-left: 6px;">
      <label>Index 2 Sequences<br>
        <textarea id="indices2" rows="25" cols="30"></textarea>
      </label>
    </div>
    <div style="display: table-cell; width: 100px; vertical-align: bottom; padding-left: 12px; padding-right: 12px;">
      <button class="ui-button ui-state-default" onclick="IndexSearch.search()"
          style="width: 100%; margin-top: 6px; margin-bottom: 6px;">Search</button><br>
      <button class="ui-button ui-state-default" onclick="IndexSearch.clearForm()" style="width: 100%;">Clear</button>
    </div>
    <div style="display: table-cell;">
      <label>Results<br>
        <textarea id="results" rows="25" readonly="readonly" style="width: 100%; box-sizing: border-box; color: red;"></textarea>
      </label>
    </div>
  </div>
</div>

<script type="text/javascript">
jQuery(document).ready(function () {
  Utils.ui.updateHelpLink(Urls.external.userManual('other_miso_tools', 'index-search'));
});
</script>

</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
