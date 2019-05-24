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
        <a href="<c:url value="/miso/dilutions"/>">Dilutions</a>
        <a href="<c:url value="/miso/worksets"/>">Worksets</a>
        <a href="<c:url value="/miso/pools"/>">Pools</a>
        <a href="<c:url value="/miso/boxes"/>">Boxes</a>
    </div>

    <h2>Instrument Runs</h2>
    <div class="menu">
        <a href="<c:url value="/miso/poolorders/active"/>">Orders</a>
          <a class="submenu" href="<c:url value="/miso/poolorders/all"/>">All</a>
          <a class="submenu" href="<c:url value="/miso/poolorders/active"/>">Active</a>
          <a class="submenu" href="<c:url value="/miso/poolorders/pending"/>">Pending</a>
        
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
      <a href="<c:url value="/miso/librarytemplates"/>">Library Templates</a>
      <a href="<c:url value="/miso/kitdescriptors"/>">Kits</a>
      <a href="<c:url value="/miso/indices"/>">Indices</a>
      <a href="<c:url value="/miso/qctype/list"/>">QC Types</a>
      <a href="<c:url value="/miso/attachmentcategories/list"/>">Attachment Categories</a>
      <a href="<c:url value="/miso/sampletype/list"/>">Sample Types</a>
      <a href="<c:url value="/miso/printers"/>">Printers</a>
      <a href="<c:url value="/miso/deletions"/>">Deletion Log</a>
    </div>

    <c:if test="${detailedSample}">
      <h2>Institute Defaults</h2>
      <div class="menu">
        <a href="<c:url value="/miso/tissuematerial/list"/>">Tissue Materials</a>
        <a href="<c:url value="/miso/tissueorigin/list"/>">Tissue Origins</a>
        <a href="<c:url value="/miso/samplepurpose/list"/>">Sample Purposes</a>
        <a href="<c:url value="/miso/subproject/list"/>">Subprojects</a>
        <a href="<c:url value="/miso/institute/list"/>">Institutes</a>
        <a href="<c:url value="/miso/lab/list"/>">Labs</a>
        <a href="<c:url value="/miso/stain/list"/>">Stains</a>
        <a href="<c:url value="/miso/staincategory/list"/>">Stain Categories</a>
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
