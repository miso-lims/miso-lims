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
    <a href="#origins">Tissue Origins</a><br/>
    <a href="#conditions">Tissue Conditions</a><br/>
    <a href="#materials">Tissue Materials</a><br/>
    <a href="#purposes">Sample Purposes</a><br/>
    <a href="#qcDetails">QC Details</a><br/>
    <a href="#subprojects">Subprojects</a><br/>
    <a href="#institutes">Institutes</a><br/>
    <a href="#classes">Sample Classes</a><br/>
    <a href="#relationships">Relationships between Sample Classes</a><br/>
  </div>
  
  <div class="sectionDivider"></div>
  <div class="corner-padding">
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
  
  
  <div class="sectionDivider"></div>
  <div class="corner-padding">
    <h2 id="conditions">Tissue Conditions</h2>
	  <table id="allConditionsTable" class="clear default-table" data-sortable>
	    <thead>
	      <tr>
	        <th>Alias</th><th>Description</th>
	      </tr>
	    </thead>
	    <tbody id="allConditions" class="TC"></tbody>
	  </table>
	</div>
	
	<div class="sectionDivider"></div>
	<div class="corner-padding">
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
  
  <div class="sectionDivider"></div>
  <div class="corner-padding">
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
  
  <div class="sectionDivider"></div>
  <div class="corner-padding">
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
  
  <div class="sectionDivider"></div>
  <div class="corner-padding">
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
  
  <div class="sectionDivider"></div>
  <div class="corner-padding">
    <h2 id="institutes">Institutes</h2>
    <table id="allInstitutesTable" class="clear default-table" data-sortable>
      <thead>
        <tr>
          <th>Alias</th><th>Lab</th>
        </tr>
      </thead>
      <tbody id="allInstitutes" class="In"></tbody>
    </table>
  </div>
  
  <div class="sectionDivider"></div>
  <div class="corner-padding">
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
  
  <div class="sectionDivider"></div>
  <div class="corner-padding">
    <h2 id="relationships">Relationships between Sample Classes</h2>
  </div>

</div>
</div>
  
<script type="text/javascript">
  jQuery(document).ready(function() {
    Tissue.getTissueOrigins();
    Tissue.getTissueConditions();
    Tissue.getTissueMaterials();
    Tissue.getSamplePurposes();
    QC.getQcDetails();
    Subproject.getProjects();
    Institute.getInstitutes();
    Hierarchy.getSampleCategories(); // callback within this creates Relationships table as well
  });
</script>
  
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>