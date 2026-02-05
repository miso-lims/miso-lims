<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="miso" uri="http://miso.tgac.bbsrc.ac.uk/tags/form" %>

<div id="subcontent">
	<h2>Preparation</h2>
    <div class="menu">
        <a href="<c:url value="/projects"/>">Projects</a>
        <a href="<c:url value="/samples"/>">Samples</a>
        <a href="<c:url value="/libraries"/>">Libraries</a>
        <a href="<c:url value="/libraryaliquots"/>">Library Aliquots</a>
        <a href="<c:url value="/poolorders"/>">Pool Orders</a>
        <a href="<c:url value="/pools"/>">Pools</a>
        <a href="<c:url value="/worksets"/>">Worksets</a>
        <a href="<c:url value="/boxes"/>">Boxes</a>
        <a href="<c:url value="/requisition/list"/>">Requisitions</a>
        <a href="<c:url value="/transfer/list"/>"><c:choose><c:when test="${pendingTransfers > 0}"><strong>Transfers (${pendingTransfers})</strong></c:when><c:otherwise>Transfers</c:otherwise></c:choose></a>
    </div>

    <h2>Instrument Runs</h2>
    <div class="menu">
        <a href="<c:url value="/sequencingorders/outstanding"/>">Sequencing Orders</a>
          <a class="submenu" href="<c:url value="/sequencingorders/all"/>">All</a>
          <a class="submenu" href="<c:url value="/sequencingorders/outstanding"/>">Outstanding</a>
          <a class="submenu" href="<c:url value="/sequencingorders/in-progress"/>">In-Progress</a>
        
        <a href="<c:url value="/runs"/>">Sequencing</a>
          <a class="submenu" href="<c:url value="/containers"/>">Containers</a>
          <a class="submenu" href="<c:url value="/runs"/>">Runs</a>
        
        <a href="<c:url value="/arrayruns"/>">Array Scanning</a>
            <a class="submenu" href="<c:url value="/arrays"/>">Arrays</a>
            <a class="submenu" href="<c:url value="/arrayruns"/>">Runs</a>
        
        <a href="<c:url value="/instruments"/>">Instruments</a>
    </div>

    <h2>Tools</h2>
    <div class="menu">
        <a href="<c:url value="/tools/indexdistance"/>">Index Distance</a>
        <a href="<c:url value="/tools/indexsearch"/>">Index Search</a>
        <c:if test="${detailedSample}">
         <a href="<c:url value="/tools/identitysearch"/>">Identity Search</a>
       </c:if>
        <a href="<c:url value="/deletions"/>">Deletion Log</a>
    </div>
    
    <h2>Misc</h2>
    <div class="menu">
      <a href="<c:url value="/contact/list"/>">Contacts</a>
      <a href="<c:url value="/storagelocations"/>">Freezers &amp; Rooms</a>
      <a href="<c:url value="/librarytemplates"/>">Library Templates</a>
      <a href="<c:url value="/printers"/>">Printers</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/subproject/list"/>">Subprojects</a>
      </c:if>
      <a href="<c:url value="/workstation/list"/>">Workstations</a>
    </div>
	
    <h2>ENA</h2>
    <div class="menu">
        <a href="<c:url value="/studies"/>">Studies</a>
        <a href="<c:url value="/experiments"/>">Experiments</a>
        <a href="<c:url value="/submissions"/>">Submissions</a>
    </div>

    <h2 class="menuDropdownTitle" onclick="Utils.ui.toggleElement('configurationMenu');">
      Configuration<span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
    </h2>
    <div id="configurationMenu" class="menu" style="display: none;">
      <a href="<c:url value="/arraymodel/list"/>">Array Models</a>
      <a href="<c:url value="/assay/list"/>">Assays</a>
      <a href="<c:url value="/assaytest/list"/>">Assay Tests</a>
      <a href="<c:url value="/attachmentcategories/list"/>">Attachment Categories</a>
      <a href="<c:url value="/boxsize/list"/>">Box Sizes</a>
      <a href="<c:url value="/boxuse/list"/>">Box Uses</a>
      <a href="<c:url value="/contactrole/list"/>">Contact Roles</a>
      <a href="<c:url value="/deliverable/list"/>">Deliverables</a>
      <a href="<c:url value="/deliverablecategory/list"/>">Deliverable Categories</a>
      <a href="<c:url value="/detailedqcstatus/list"/>">Detailed QC Statuses</a>
      <a href="<c:url value="/instrumentmodel/list"/>">Instrument Models</a>
      <a href="<c:url value="/kitdescriptors"/>">Kits</a>
      <a href="<c:url value="/lab/list"/>">Labs</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/librarydesign/list"/>">Library Designs</a>
        <a href="<c:url value="/librarydesigncode/list"/>">Library Design Codes</a>
      </c:if>
      <a href="<c:url value="/libraryindexfamily/list"/>">Library Index Families</a>
      <a href="<c:url value="/libraryselection/list"/>">Library Selection Types</a>
      <a href="<c:url value="/libraryspikein/list"/>">Library Spike-Ins</a>
      <a href="<c:url value="/librarystrategy/list"/>">Library Strategy Types</a>
      <a href="<c:url value="/librarytype/list"/>">Library Types</a>
      <a href="<c:url value="/locationmap/list"/>">Location Maps</a>
      <a href="<c:url value="/metric/list"/>">Metrics</a>
      <a href="<c:url value="/metricsubcategory/list"/>">Metric Subcategories</a>
      <a href="<c:url value="/partitionqctype/list"/>">Partition QC Types</a>
      <a href="<c:url value="/pipeline/list"/>">Pipelines</a>
      <a href="<c:url value="/qctype/list"/>">QC Types</a>
      <a href="<c:url value="/referencegenome/list"/>">Reference Genomes</a>
      <a href="<c:url value="/RunItemQcStatus/list"/>">Run-Library QC Statuses</a>
      <a href="<c:url value="/runpurpose/list"/>">Run Purposes</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/sampleclass/list"/>">Sample Classes</a>
        <a href="<c:url value="/sampleindexfamily/list"/>">Sample Index Families</a>
        <a href="<c:url value="/samplepurpose/list"/>">Sample Purposes</a>
      </c:if>
      <a href="<c:url value="/sampletype/list"/>">Sample Types</a>
      <a href="<c:url value="/scientificname/list"/>">Scientific Names</a>
      <a href="<c:url value="/containermodel/list"/>">Sequencing Container Models</a>
      <a href="<c:url value="/sequencingcontroltype/list"/>">Sequencing Control Types</a>
      <a href="<c:url value="/sequencingparameters/list"/>">Sequencing Parameters</a>
      <a href="<c:url value="/sop/list"/>">SOPs</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/stain/list"/>">Stains</a>
        <a href="<c:url value="/staincategory/list"/>">Stain Categories</a>
      </c:if>
      <a href="<c:url value="/storagelabel/list"/>">Storage Labels</a>
      <a href="<c:url value="/studytype/list"/>">Study Types</a>
      <a href="<c:url value="/targetedsequencing/list"/>">Targeted Sequencings</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/tissuematerial/list"/>">Tissue Materials</a>
        <a href="<c:url value="/tissueorigin/list"/>">Tissue Origins</a>
        <a href="<c:url value="/tissuepiecetype/list"/>">Tissue Piece Types</a>
        <a href="<c:url value="/tissuetype/list"/>">Tissue Types</a>
      </c:if>
      <a href="<c:url value="/worksetcategory/list"/>">Workset Categories</a>
      <a href="<c:url value="/worksetstage/list"/>">Workset Stages</a>
    </div>

	  <c:if test="${miso:isAdmin()}">
	    <h2>User Administration</h2>
      <div class="menu">
        <a href="<c:url value="/admin/users"/>">Users</a>
        <a href="<c:url value="/admin/groups"/>">Groups</a>
      </div>
    </c:if>
</div>
