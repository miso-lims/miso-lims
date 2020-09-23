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
    </div>

    <h2>Misc</h2>
    <div class="menu">
      <a href="<c:url value="/miso/storagelocations"/>">Freezers &amp; Rooms</a>
      <a href="<c:url value="/miso/locationmap/list"/>">Location Maps</a>
      <a href="<c:url value="/miso/librarytemplates"/>">Library Templates</a>
      <a href="<c:url value="/miso/kitdescriptors"/>">Kits</a>
      <a href="<c:url value="/miso/indexfamily/list"/>">Index Families</a>
      <a href="<c:url value="/miso/qctype/list"/>">QC Types</a>
      <a href="<c:url value="/miso/attachmentcategories/list"/>">Attachment Categories</a>
      <a href="<c:url value="/miso/detailedqcstatus/list"/>">Detailed QC Statuses</a>
      <a href="<c:url value="/miso/sampletype/list"/>">Sample Types</a>
      <a href="<c:url value="/miso/sequencingcontroltype/list"/>">Sequencing Control Types</a>
      <a href="<c:url value="/miso/librarytype/list"/>">Library Types</a>
      <a href="<c:url value="/miso/libraryselection/list"/>">Library Selection Types</a>
      <a href="<c:url value="/miso/librarystrategy/list"/>">Library Strategy Types</a>
      <a href="<c:url value="/miso/libraryspikein/list"/>">Library Spike-Ins</a>
      <a href="<c:url value="/miso/targetedsequencing/list"/>">Targeted Sequencings</a>
      <a href="<c:url value="/miso/runpurpose/list"/>">Run Purposes</a>
      <a href="<c:url value="/miso/sequencingparameters/list"/>">Sequencing Parameters</a>
      <a href="<c:url value="/miso/containermodel/list"/>">Sequencing Container Models</a>
      <a href="<c:url value="/miso/instrumentmodel/list"/>">Instrument Models</a>
      <a href="<c:url value="/miso/boxsize/list"/>">Box Sizes</a>
      <a href="<c:url value="/miso/boxuse/list"/>">Box Uses</a>
      <a href="<c:url value="/miso/institute/list"/>">Institutes</a>
      <a href="<c:url value="/miso/lab/list"/>">Labs</a>
      <a href="<c:url value="/miso/arraymodel/list"/>">Array Models</a>
      <a href="<c:url value="/miso/partitionqctype/list"/>">Partition QC Types</a>
      <a href="<c:url value="/miso/scientificname/list"/>">Scientific Names</a>
      <a href="<c:url value="/miso/referencegenome/list"/>">Reference Genomes</a>
      <a href="<c:url value="/miso/studytype/list"/>">Study Types</a>
      <a href="<c:url value="/miso/workstation/list"/>">Workstations</a>
      <a href="<c:url value="/miso/sop/list"/>">SOPs</a>
      <a href="<c:url value="/miso/printers"/>">Printers</a>
      <a href="<c:url value="/miso/deletions"/>">Deletion Log</a>
    </div>

    <c:if test="${detailedSample}">
      <h2>Institute Defaults</h2>
      <div class="menu">
        <a href="<c:url value="/miso/sampleclass/list"/>">Sample Classes</a>
        <a href="<c:url value="/miso/tissuematerial/list"/>">Tissue Materials</a>
        <a href="<c:url value="/miso/tissueorigin/list"/>">Tissue Origins</a>
        <a href="<c:url value="/miso/tissuetype/list"/>">Tissue Types</a>
        <a href="<c:url value="/miso/tissuepiecetype/list"/>">Tissue Piece Types</a>
        <a href="<c:url value="/miso/samplepurpose/list"/>">Sample Purposes</a>
        <a href="<c:url value="/miso/subproject/list"/>">Subprojects</a>
        <a href="<c:url value="/miso/stain/list"/>">Stains</a>
        <a href="<c:url value="/miso/staincategory/list"/>">Stain Categories</a>
        <a href="<c:url value="/miso/librarydesigncode/list"/>">Library Design Codes</a>
        <a href="<c:url value="/miso/librarydesign/list"/>">Library Designs</a>
      </div>
    </c:if>
	
    <h2>ENA</h2>
    <div class="menu">
        <a href="<c:url value="/miso/studies"/>">Studies</a>
        <a href="<c:url value="/miso/experiments"/>">Experiments</a>
        <a href="<c:url value="/miso/submissions"/>">Submissions</a>
    </div>

	<c:if test="${miso:isAdmin()}">
	  <h2>User Administration</h2>
      <div class="menu">
        <a href="<c:url value="/miso/admin/users"/>">Users</a>
        <a href="<c:url value="/miso/admin/groups"/>">Groups</a>
      </div>
    </c:if>
</div>
