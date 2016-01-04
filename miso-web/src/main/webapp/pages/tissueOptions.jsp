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

<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css" />

<script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/tissueOptions_ajax.js'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">

  <h1>Tissue Options</h1>
  
  <h2 id="origins">Tissue Origins</h2>
  <div>
	  <table id="allOriginsTable" class="tissueOptionsTable clear">
		  <thead>
			  <tr>
			    <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allOrigins"></tbody>
	  </table>
  </div>
  
  
  <div class="sectionDivider"></div>
  <h2 id="conditions">Tissue Conditions</h2>
  <div>
	  <table id="allConditionsTable" class="tissueOptionsTable clear">
	    <thead>
	      <tr>
	        <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allConditions"></tbody>
	  </table>
	</div>
	
	<div class="sectionDivider"></div>
  <h2 id="materials">Tissue Materials</h2>
  <div>
    <table id="allMaterialsTable" class="tissueOptionsTable clear">
      <thead>
        <tr>
          <th>Alias</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allMaterials"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider"></div>
  <h2 id="purposes">Sample Purposes</h2>
  <div>
    <table id="allPurposesTable" class="tissueOptionsTable clear">
      <thead>
        <tr>
          <th>Alias</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allPurposes"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider"></div>
  <h2 id="qcDetails">QC Details</h2>
  <div>
    <table id="allQcDetailsTable" class="tissueOptionsTable clear">
      <thead>
        <tr>
          <th>QC</th><th>Passed</th><th>Description</th><th>Note Required?</th>
        </tr>
      </thead>
      <tbody id="allQcDetails"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider"></div>
  <h2 id="subprojects">Subprojects</h2>
  <div>
    <table id="allSubprojectsTable" class="tissueOptionsTable clear">
      <thead>
        <tr>
          <th>Alias Project Description Priority</th>
        </tr>
      </thead>
      <tbody id="allSubprojects"></tbody>
    </table>
  </div>

</div>
</div>
  
<script type="text/javascript">
  jQuery(document).ready(function() {
    Tissue.getTissueOrigins();
    Tissue.getTissueConditions();
    Tissue.getTissueMaterials();
    Tissue.getSamplePurposes();
    //QC.getQcDetails();
    Subproject.getSubprojects();
  });
</script>
  
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>