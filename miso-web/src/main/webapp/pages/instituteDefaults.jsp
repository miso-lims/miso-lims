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

<div id="maincontent">
<div id="contentcolumn">

  <h1>Institute Defaults</h1>
  
  <div class="corner-padding">
    <a href="#tissueorigin" onclick="document.getElementById('tissueorigin').click();">Tissue Origins</a><br/>
    <a href="#tissuetype" onclick="document.getElementById('tissuetype').click();">Tissue Types</a><br/>
    <a href="#tissuematerial" onclick="document.getElementById('tissuematerial').click();">Tissue Materials</a><br/>
    <a href="#samplepurpose" onclick="document.getElementById('samplepurpose').click();">Sample Purposes</a><br/>
    <a href="#qcpasseddetail" onclick="document.getElementById('qcpasseddetail').click();">QC Details</a><br/>
    <a href="#subproject" onclick="document.getElementById('subproject').click();">Subprojects</a><br/>
    <a href="#institute" onclick="document.getElementById('institute').click();">Institutes</a><br/>
    <a href="#lab" onclick="document.getElementById('lab').click();">Labs</a><br/>
    <a href="#sampleclass" onclick="document.getElementById('sampleclass').click();">Sample Classes</a><br/>
    <a href="#samplevalidrelationship" onclick="document.getElementById('samplevalidrelationship').click();">Relationships between Sample Classes</a><br/>
  </div>
  
  <div id="tissueorigin" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#origins_arrowclick'), 'originsdiv');">
    Tissue Origins
    <div id="origins_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="originsdiv" class="corner-padding" style="display:none;">
    <h2>Tissue Origins</h2>
	  <table id="allOriginsTable" class="clear default-table" data-sortable>
		  <thead>
			  <tr>
			    <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allOrigins" class="TO"></tbody>
	  </table>
  </div>
  
  
  <div id="tissuetype" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#types_arrowclick'), 'typesdiv');">
    Tissue Types
    <div id="types_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="typesdiv" class="corner-padding" style="display:none;">
    <h2>Tissue Types</h2>
	  <table id="allTypesTable" class="clear default-table" data-sortable>
	    <thead>
	      <tr>
	        <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allTypes" class="TT"></tbody>
	  </table>
	</div>
	
	<div id="tissuematerial" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#materials_arrowclick'), 'materialsdiv');">
    Tissue Materials
    <div id="materials_arrowclick" class="toggleLeft"></div>
  </div>
	<div id="materialsdiv" class="corner-padding" style="display:none;">
    <h2>Tissue Materials</h2>
    <table id="allMaterialsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allMaterials" class="TM"></tbody>
    </table>
  </div>
  
  <div id="samplepurpose" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#purposes_arrowclick'), 'purposesdiv');">
    Sample Purposes
    <div id="purposes_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="purposesdiv" class="corner-padding" style="display:none;">
    <h2>Sample Purposes</h2>
    <table id="allPurposesTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th><th>Description</th>
        </tr>
      </thead>
      <tbody id="allPurposes" class="SP"></tbody>
    </table>
  </div>
  
  <div id="qcpasseddetail" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#qcdetails_arrowclick'), 'qcdetailsdiv');">
    QC Details
    <div id="qcdetails_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="qcdetailsdiv" class="corner-padding" style="display:none;">
    <h2>QC Details</h2>
    <table id="allQcDetailsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Description</th><th>QC Passed</th><th>Note Required?</th>
        </tr>
      </thead>
      <tbody id="allQcDetails" class="QC"></tbody>
    </table>
  </div>
  
  <div id="subproject" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#subprojects_arrowclick'), 'subprojectsdiv');">
    Subprojects
    <div id="subprojects_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="subprojectsdiv" class="corner-padding" style="display:none;">
    <h2>Subprojects</h2>
    <table id="allSubprojectsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Project</th><th>Alias</th><th>Description</th><th>Priority</th><th>Reference Genome</th>
        </tr>
      </thead>
      <tbody id="allSubprojects" class="SubP"></tbody>
    </table>
  </div>
  
  <div id="institute" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#institutes_arrowclick'), 'institutesdiv');">
    Institutes
    <div id="institutes_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="institutesdiv" class="corner-padding" style="display:none;">
    <h2>Institutes</h2>
    <table id="allInstitutesTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th>
        </tr>
      </thead>
      <tbody id="allInstitutes" class="In"></tbody>
    </table>
  </div>
  
  <div id="labs" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#labs_arrowclick'), 'labsdiv');">
    Labs
    <div id="labs_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="labsdiv" class="corner-padding" style="display:none;">
    <h2>Labs</h2>
    <table id="allLabsTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th><th>Institute</th>
        </tr>
      </thead>
      <tbody id="allLabs" class="Lab"></tbody>
    </table>
  </div>
  
  <div id="sampleclass" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#classes_arrowclick'), 'classesdiv');">
    Sample Classes
    <div id="classes_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="classesdiv" class="corner-padding" style="display:none;">
    <h2>Sample Classes</h2>
    <table id="allClassesTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Class</th><th>Category</th>
        </tr>
      </thead>
      <tbody id="allClasses" class="Cl"></tbody>
    </table>
  </div>
  
  <div id="samplevalidrelationship" class="sectionDivider clear" onclick="Utils.ui.toggleLeftInfo(jQuery('#relationships_arrowclick'), 'relationshipsdiv');">
    Relationships between Sample Classes
    <div id="relationships_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="relationshipsdiv" class="corner-padding" style="display:none;">
    <h2 class="clear default-table" data-sortable>Relationships between Sample Classes</h2>
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