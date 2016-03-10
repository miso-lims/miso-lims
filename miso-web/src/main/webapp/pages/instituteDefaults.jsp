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

<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/sortable.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/instituteDefaults_ajax.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

<div id="maincontent">
<div id="contentcolumn">

  <h1>Institute Defaults</h1>
  
  <div class="corner-padding">
    <a href="#groupIds">Group IDs</a><br/>
    <a href="#subprojects">Subprojects</a><br/>
    <a href="#origins">Tissue Origins</a><br/>
    <a href="#types">Tissue Types</a><br/>
    <a href="#materials">Tissue Materials</a><br/>
    <a href="#purposes">Sample Purposes</a><br/>
    <a href="#qcDetails">QC Details</a><br/>
    <a href="#institutes">Institutes</a><br/>
    <a href="#labs">Labs</a><br/>
    <a href="#classes">Sample Classes</a><br/>
    <a href="#relationships">Relationships between Sample Classes</a><br/>
  </div>
  
    
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#groupids_arrowclick'), 'groupidsdiv');">
    Group IDs
    <div id="groupids_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="groupidsdiv" style="display:none;">
    <h2 id="groupids">Group IDs</h2>
    <table id="allGroupIdsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Project</th><th>Subproject</th><th>Group ID</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allGroupIds" class="GID"></tbody>
    </table>
  </div>
  
    
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#subprojects_arrowclick'), 'subprojectsdiv');">
    Subprojects
    <div id="subprojects_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="subprojectsdiv" style="display:none;">
    <h2 id="subprojects">Subprojects</h2>
    <table id="allSubprojectsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Project</th><th>Alias</th><th>Description</th><th>Priority</th>
        </tr>
      </thead>
      <tbody id="allSubprojects" class="SubP"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#origins_arrowclick'), 'originsdiv');">
    Tissue Origins
    <div id="origins_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="originsdiv" style="display:none;">
    <h2 id="origins">Tissue Origins</h2>
	  <table id="allOriginsTable" class="clear default-table" data-sortable>
		  <thead>
			  <tr>
			    <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allOrigins" class="TO"></tbody>
	  </table>
  </div>
  
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#types_arrowclick'), 'typesdiv');">
    Tissue Types
    <div id="types_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="typesdiv" style="display:none;">
    <h2 id="types">Tissue Types</h2>
	  <table id="allTypesTable" class="clear default-table" data-sortable>
	    <thead>
	      <tr>
	        <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allTypes" class="TT"></tbody>
	  </table>
	</div>
	
	<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#materials_arrowclick'), 'materialsdiv');">
    Tissue Materials
    <div id="materials_arrowclick" class="toggleLeft"></div>
  </div>
	<div class="corner-padding" id="materialsdiv" style="display:none;">
    <h2 id="materials">Tissue Materials</h2>
    <table id="allMaterialsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allMaterials" class="TM"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#purposes_arrowclick'), 'purposesdiv');">
    Sample Purposes
    <div id="purposes_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="purposesdiv" style="display:none;">
    <h2 id="purposes">Sample Purposes</h2>
    <table id="allPurposesTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allPurposes" class="SP"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#qcdetails_arrowclick'), 'qcdetailsdiv');">
    QC Details
    <div id="qcdetails_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="qcdetailsdiv" style="display:none;">
    <h2 id="qcDetails">QC Details</h2>
    <table id="allQcDetailsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Description</th><th>QC Passed</th><th>Note Required?</th>
        </tr>
      </thead>
      <tbody id="allQcDetails" class="QC"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#institutes_arrowclick'), 'institutesdiv');">
    Institutes
    <div id="institutes_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="institutesdiv" style="display:none;">
    <h2 id="institutes">Institutes</h2>
    <table id="allInstitutesTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th>
        </tr>
      </thead>
      <tbody id="allInstitutes" class="In"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#labs_arrowclick'), 'labsdiv');">
    Labs
    <div id="labs_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="labsdiv" style="display:none;">
    <h2 id="labs">Labs</h2>
    <table id="allLabsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th><th>Institute</th>
        </tr>
      </thead>
      <tbody id="allLabs" class="Lab"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#classes_arrowclick'), 'classesdiv');">
    Sample Classes
    <div id="classes_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="classesdiv" style="display:none;">
    <h2 id="classes">Sample Classes</h2>
    <table id="allClassesTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Class</th><th>Category</th>
        </tr>
      </thead>
      <tbody id="allClasses" class="Cl"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#relationships_arrowclick'), 'relationshipsdiv');">
    Relationships between Sample Classes
    <div id="relationships_arrowclick" class="toggleLeft"></div>
  </div>
  <div class="corner-padding" id="relationshipsdiv" style="display:none;">
    <h2 id="relationships" class="clear default-table" data-sortable>Relationships between Sample Classes</h2>
    <table id="allRelationshipsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Parent Category</th><th>Parent Class</th><th>Child Category</th><th>Child Class</th>
        </tr>
      </thead>
      <tbody id="allRelationships" class="Rel"></tbody>
    </table>
  </div>

</div>
</div>
  
<script type="text/javascript">
  jQuery(document).ready(function() {
    Defaults.getDefaults();
  });
</script>
  
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>