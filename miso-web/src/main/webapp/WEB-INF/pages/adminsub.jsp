<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="miso" uri="http://miso.tgac.bbsrc.ac.uk/tags/form" %>

<div id="subcontent">
	<h2>Preparation</h2>
    <div class="menu">
        <a href="<c:url value="/miso/projects"/>">Projects</a>
        <a href="<c:url value="/miso/samples"/>">Samples</a>
        <a href="<c:url value="/miso/libraries"/>">Libraries</a>
        <a href="<c:url value="/miso/libraryaliquots"/>">Library Aliquots</a>
        <a href="<c:url value="/miso/poolorders"/>">Pool Orders</a>
        <a href="<c:url value="/miso/pools"/>">Pools</a>
        <a href="<c:url value="/miso/worksets"/>">Worksets</a>
        <a href="<c:url value="/miso/boxes"/>">Boxes</a>
        <a href="<c:url value="/miso/requisition/list"/>">Requisitions</a>
        <a href="<c:url value="/miso/transfer/list"/>"><c:choose><c:when test="${pendingTransfers > 0}"><strong>Transfers (${pendingTransfers})</strong></c:when><c:otherwise>Transfers</c:otherwise></c:choose></a>
    </div>

    <h2>Instrument Runs</h2>
    <div class="menu">
        <a href="<c:url value="/miso/sequencingorders/outstanding"/>">Sequencing Orders</a>
          <a class="submenu" href="<c:url value="/miso/sequencingorders/all"/>">All</a>
          <a class="submenu" href="<c:url value="/miso/sequencingorders/outstanding"/>">Outstanding</a>
          <a class="submenu" href="<c:url value="/miso/sequencingorders/in-progress"/>">In-Progress</a>
        
        <a href="<c:url value="/miso/runs"/>">Sequencing</a>
          <a class="submenu" href="<c:url value="/miso/containers"/>">Containers</a>
          <a class="submenu" href="<c:url value="/miso/runs"/>">Runs</a>
        
        <a href="<c:url value="/miso/arrayruns"/>">Array Scanning</a>
            <a class="submenu" href="<c:url value="/miso/arrays"/>">Arrays</a>
            <a class="submenu" href="<c:url value="/miso/arrayruns"/>">Runs</a>
        
        <a href="<c:url value="/miso/instruments"/>">Instruments</a>
    </div>

    <h2>Tools</h2>
    <div class="menu">
        <a href="<c:url value="/miso/tools/indexdistance"/>">Index Distance</a>
        <a href="<c:url value="/miso/tools/indexsearch"/>">Index Search</a>
        <c:if test="${detailedSample}">
         <a href="<c:url value="/miso/tools/identitysearch"/>">Identity Search</a>
       </c:if>
        <a href="<c:url value="/miso/deletions"/>">Deletion Log</a>
    </div>
    
    <h2>Misc</h2>
    <div class="menu">
      <a href="<c:url value="/miso/contact/list"/>">Contacts</a>
      <a href="<c:url value="/miso/storagelocations"/>">Freezers &amp; Rooms</a>
      <a href="<c:url value="/miso/librarytemplates"/>">Library Templates</a>
      <a href="<c:url value="/miso/printers"/>">Printers</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/miso/subproject/list"/>">Subprojects</a>
      </c:if>
      <a href="<c:url value="/miso/workstation/list"/>">Workstations</a>
    </div>
	
    <h2>ENA</h2>
    <div class="menu">
        <a href="<c:url value="/miso/studies"/>">Studies</a>
        <a href="<c:url value="/miso/experiments"/>">Experiments</a>
        <a href="<c:url value="/miso/submissions"/>">Submissions</a>
    </div>

    <h2 class="menuDropdownTitle" onclick="Utils.ui.toggleElement('configurationMenu');">
      Configuration<span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
    </h2>
    <div id="configurationMenu" class="menu" style="display: none;">
      <a href="<c:url value="/miso/arraymodel/list"/>">Array Models</a>
      <a href="<c:url value="/miso/assay/list"/>">Assays</a>
      <a href="<c:url value="/miso/assaytest/list"/>">Assay Tests</a>
      <a href="<c:url value="/miso/attachmentcategories/list"/>">Attachment Categories</a>
      <a href="<c:url value="/miso/boxsize/list"/>">Box Sizes</a>
      <a href="<c:url value="/miso/boxuse/list"/>">Box Uses</a>
      <a href="<c:url value="/miso/contactrole/list"/>">Contact Roles</a>
      <a href="<c:url value="/miso/deliverable/list"/>">Deliverables</a>
      <a href="<c:url value="/miso/detailedqcstatus/list"/>">Detailed QC Statuses</a>
      <a href="<c:url value="/miso/indexfamily/list"/>">Index Families</a>
      <a href="<c:url value="/miso/instrumentmodel/list"/>">Instrument Models</a>
      <a href="<c:url value="/miso/kitdescriptors"/>">Kits</a>
      <a href="<c:url value="/miso/lab/list"/>">Labs</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/miso/librarydesign/list"/>">Library Designs</a>
        <a href="<c:url value="/miso/librarydesigncode/list"/>">Library Design Codes</a>
      </c:if>
      <a href="<c:url value="/miso/libraryselection/list"/>">Library Selection Types</a>
      <a href="<c:url value="/miso/libraryspikein/list"/>">Library Spike-Ins</a>
      <a href="<c:url value="/miso/librarystrategy/list"/>">Library Strategy Types</a>
      <a href="<c:url value="/miso/librarytype/list"/>">Library Types</a>
      <a href="<c:url value="/miso/locationmap/list"/>">Location Maps</a>
      <a href="<c:url value="/miso/metric/list"/>">Metrics</a>
      <a href="<c:url value="/miso/metricsubcategory/list"/>">Metric Subcategories</a>
      <a href="<c:url value="/miso/partitionqctype/list"/>">Partition QC Types</a>
      <a href="<c:url value="/miso/pipeline/list"/>">Pipelines</a>
      <a href="<c:url value="/miso/qctype/list"/>">QC Types</a>
      <a href="<c:url value="/miso/referencegenome/list"/>">Reference Genomes</a>
      <a href="<c:url value="/miso/runlibraryqcstatus/list"/>">Run-Library QC Statuses</a>
      <a href="<c:url value="/miso/runpurpose/list"/>">Run Purposes</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/miso/sampleclass/list"/>">Sample Classes</a>
        <a href="<c:url value="/miso/samplepurpose/list"/>">Sample Purposes</a>
      </c:if>
      <a href="<c:url value="/miso/sampletype/list"/>">Sample Types</a>
      <a href="<c:url value="/miso/scientificname/list"/>">Scientific Names</a>
      <a href="<c:url value="/miso/containermodel/list"/>">Sequencing Container Models</a>
      <a href="<c:url value="/miso/sequencingcontroltype/list"/>">Sequencing Control Types</a>
      <a href="<c:url value="/miso/sequencingparameters/list"/>">Sequencing Parameters</a>
      <a href="<c:url value="/miso/sop/list"/>">SOPs</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/miso/stain/list"/>">Stains</a>
        <a href="<c:url value="/miso/staincategory/list"/>">Stain Categories</a>
      </c:if>
      <a href="<c:url value="/miso/storagelabel/list"/>">Storage Labels</a>
      <a href="<c:url value="/miso/studytype/list"/>">Study Types</a>
      <a href="<c:url value="/miso/targetedsequencing/list"/>">Targeted Sequencings</a>
      <c:if test="${detailedSample}">
        <a href="<c:url value="/miso/tissuematerial/list"/>">Tissue Materials</a>
        <a href="<c:url value="/miso/tissueorigin/list"/>">Tissue Origins</a>
        <a href="<c:url value="/miso/tissuepiecetype/list"/>">Tissue Piece Types</a>
        <a href="<c:url value="/miso/tissuetype/list"/>">Tissue Types</a>
      </c:if>
      <a href="<c:url value="/miso/worksetcategory/list"/>">Workset Categories</a>
      <a href="<c:url value="/miso/worksetstage/list"/>">Workset Stages</a>
    </div>

	  <c:if test="${miso:isAdmin()}">
	    <h2>User Administration</h2>
      <div class="menu">
        <a href="<c:url value="/miso/admin/users"/>">Users</a>
        <a href="<c:url value="/miso/admin/groups"/>">Groups</a>
      </div>
    </c:if>
</div>
