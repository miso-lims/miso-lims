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

<div id="maincontent">
<div id="contentcolumn">
<div id="tab-1">


<h1>Identity Search Tool</h1>

<p>
  Find which external names are already associated with an identity in MISO, and which samples are
  derived from them. Use the 'Filter Below' action to display all children of the selected identities,
  or the 'Children' action to navigate the hierarchy and perform actions on the children. 
</p>
<br/>

<div style="display:flex; width:100%;">
  <div style="flex: 25%; margin-right: 8px;">
    <h1>Criteria</h1>
    <label>External Names<br/>
      <textarea id="externalNames" rows="15" cols="40" style="width:98%;"></textarea>
    </label>
    <label>Project<br/>
      <input id="projectAlias" style="width:98%;"/>
    </label>
    <button class="ui-button ui-state-default" onclick="IdentitySearch.lookup(true);"
      style="width:100%; margin-top:8px;">Search for Exact Match</button><br/>
    <button class="ui-button ui-state-default" onclick="IdentitySearch.lookup(false);"
      style="width:100%; margin-top:8px;">Search for Partial Match</button><br/>
    <button class="ui-button ui-state-default" onclick="IdentitySearch.clearForm();"
      style="width:100%; margin-top:8px">Clear</button>
    <div id="ajaxLoaderDiv"></div>
  </div>
  <div style="flex: 75%;">
    <h1>Identity Results</h1>
    <div id="listResults"></div>
  </div>
</div>

<script type="text/javascript">
jQuery(document).ready(function () {
  Utils.ui.updateHelpLink(Urls.external.userManual('other_miso_tools', 'identity-search'));
  IdentitySearch.clearResults();
});
</script>

<miso:list-section-ajax id="list_samples" name="Samples" target="sample" config="{}"/>

</div>
</div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>