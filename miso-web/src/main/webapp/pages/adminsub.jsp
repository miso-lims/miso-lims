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
  <!--
	<h2>Sample Processing</h2>
	<ul class="bullets">
		<li><a href="<c:url value="/miso/sample/receipt"/>">Receive
				Samples</a></li>
		<li><a href="<c:url value="/miso/importexport"/>">Import &amp;
				Export</a></li>
	</ul>

  -->
    <h2>Tracking</h2>
    <ul class="bullets">
        <li><a href="<c:url value="/miso/samples"/>">Samples</a></li>
        <li><a href="<c:url value="/miso/libraries"/>">Libraries</a></li>
        <li><a href="<c:url value="/miso/dilutions"/>">Dilutions</a></li>
        <li><a href="<c:url value="/miso/worksets"/>">Worksets</a></li>
        <li><a href="<c:url value="/miso/pools"/>">Pools</a></li>
        <li><a href="<c:url value="/miso/poolorders/active"/>">Active Orders</a></li>
        <li><a href="<c:url value="/miso/poolorders/all"/>">All Orders</a></li>
        <li><a href="<c:url value="/miso/poolorders/pending"/>">Pending Orders</a></li>
        <li><a href="<c:url value="/miso/containers"/>">Sequencing Containers</a></li>
        <li><a href="<c:url value="/miso/runs"/>">Sequencer Runs</a></li>
        <li><a href="<c:url value="/miso/arrays"/>">Arrays</a></li>
        <li><a href="<c:url value="/miso/arrayruns"/>">Array Runs</a></li>
        <li><a href="<c:url value="/miso/instruments"/>">Instruments</a></li>
        <li><a href="<c:url value="/miso/boxes"/>">Boxes</a></li>
        <li><a href="<c:url value="/miso/storagelocations"/>">Freezers &amp; Rooms</a></li>
        <li><a href="<c:url value="/miso/librarytemplates"/>">Library Templates</a></li>
        <li><a href="<c:url value="/miso/kitdescriptors"/>">Kits</a></li>
        <li><a href="<c:url value="/miso/indices"/>">Indices</a></li>
        <li><a href="<c:url value="/miso/studies"/>">Studies</a></li>
        <li><a href="<c:url value="/miso/printers"/>">Printers</a></li>
        <li><a href="<c:url value="/miso/deletions"/>">Deletions</a></li>
    </ul>

    <h2>Tools</h2>
    <ul class="bullets">
        <li><a href="<c:url value="/miso/tools/indexdistance"/>">Index Distance</a></li>
        <c:if test="${detailedSample}">
        <li><a href="<c:url value="/miso/tools/identitysearch"/>">Identity Search</a></li>
        </c:if>
    </ul>

    <c:if test="${detailedSample}">
      <h2>Institute Defaults</h2>
      <ul class="bullets">
          <li><a href="<c:url value="/miso/tissuematerial/list"/>">Tissue Materials</a></li>
          <li><a href="<c:url value="/miso/tissueorigin/list"/>">Tissue Origins</a></li>
          <li><a href="<c:url value="/miso/samplepurpose/list"/>">Sample Purposes</a></li>
          <li><a href="<c:url value="/miso/qctype/list"/>">QC Types</a></li>
          <li><a href="<c:url value="/miso/subproject/list"/>">Subprojects</a></li>
          <li><a href="<c:url value="/miso/institute/list"/>">Institutes</a></li>
          <li><a href="<c:url value="/miso/lab/list"/>">Labs</a></li>
          <li><a href="<c:url value="/miso/attachmentcategories/list"/>">Attachment Categories</a></li>
      </ul>
    </c:if>
	
    <h2>ENA</h2>
    <ul class="bullets">
        <li><a href="<c:url value="/miso/experiments"/>">Experiments</a></li>
        <li><a href="<c:url value="/miso/submissions"/>">Submissions</a></li>
    </ul>

	<c:if test="${miso:isAdmin()}">
	  <h2>User Administration</h2>
      <ul class="bullets">
        <li><a href="<c:url value="/miso/admin/users"/>">Users</a></li>
        <li><a href="<c:url value="/miso/admin/groups"/>">Groups</a></li>
      </ul>
    </c:if>
</div>
