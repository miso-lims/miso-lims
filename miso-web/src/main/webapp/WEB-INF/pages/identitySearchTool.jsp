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


<h1>Identity Search Tool</h1>

<p>Find which external names are already associated with an identity in MISO, and which samples are derived from them.</p>
<br/>

<div style="display:table; width:100%;">
  <div style="display:table-row;">
    <div style="display:table-cell; width:1px;">
      <label>External Names<br/>
        <textarea id="externalNames" rows="25" cols="40"></textarea>
      </label>
    </div>
    <div style="display: table-cell; width: 100px; vertical-align: bottom; padding-left: 12px; padding-right: 12px;">
      <div id="ajaxLoaderDiv"></div><br/>
      <label>Project<br/>
        <input id="projectAlias" style="width:inherit;"/>
      </label>
      <button class="ui-button ui-state-default" onclick="IdentitySearch.lookup(true);"
        style="width:100%; margin-top:6px; margin-bottom:6px;">Search for Exact Match</button><br/>
      <button class="ui-button ui-state-default" onclick="IdentitySearch.lookup(false);"
        style="width:100%; margin-top:6px; margin-bottom:6px;">Search for Partial Match</button><br/>
      <button class="ui-button ui-state-default" onclick="IdentitySearch.clearForm();" style="width:100%;">Clear</button>
    </div>
    <div style="display:table-cell;">
      <label>Results</label>
        <table class="dataTable">
          <thead>
            <tr><th>External Name</th><th>Identity Alias</th></tr>
          </thead>
          <tbody id="externalNameResults"></tbody>
        </table>
    </div>
  </div>
</div>

<script type="text/javascript">
jQuery(document).ready(function () {
  Utils.ui.updateHelpLink(Urls.external.userManual('other_miso_tools', 'identity-search'));
});
</script>

<miso:list-section-ajax id="list_samples" name="Samples" target="sample" config="{}"/>

</div>
</div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>