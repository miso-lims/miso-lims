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

<div id="subcontent">
  <!--
	<h2>Sample Processing</h2>
	<ul class="bullets">
		<li><a href="<c:url value="/miso/sample/receipt"/>">Receive
				Samples</a></li>
		<li><a href="<c:url value="/miso/importexport"/>">Import &amp;
				Export</a></li>
	</ul>

	<h2>Sequencing</h2>
	<ul class="bullets">
		<li><a href="<c:url value="/miso/pools/ready"/>">Ready to Run</a></li>
	</ul>
  -->
    <h2>Tracking</h2>
    <ul class="bullets">
        <li><a href="<c:url value="/miso/samples"/>">Samples</a></li>
        <li><a href="<c:url value="/miso/libraries"/>">Libraries</a></li>
        <li><a href="<c:url value="/miso/pools"/>">Pools</a></li>
        <li><a href="<c:url value="/miso/poolorders"/>">Orders</a></li>
        <li><a href="<c:url value="/miso/containers"/>">Sequencing Containers</a></li>
        <li><a href="<c:url value="/miso/runs"/>">Runs</a></li>
        <li><a href="<c:url value="/miso/boxes"/>">Boxes</a></li>
        <li><a href="<c:url value="/miso/sequencers"/>">Sequencers</a></li>
        <li><a href="<c:url value="/miso/kitdescriptors"/>">Kits</a></li>
        <li><a href="<c:url value="/miso/indices"/>">Indices</a></li>
        <li><a href="<c:url value="/miso/experiments"/>">Experiments</a></li>
        <li><a href="<c:url value="/miso/studies"/>">Studies</a></li>
        <li><a href="<c:url value="/miso/printers"/>">Printers</a></li>
    </ul>
	
	<sec:authorize access="hasRole('ROLE_ADMIN')">
	  <h2>User Administration</h2>
      <ul class="bullets">
        <li><a href="<c:url value="/miso/admin/users"/>">Users</a></li>
        <li><a href="<c:url value="/miso/admin/groups"/>">Groups</a></li>
      </ul>
    </sec:authorize>
</div>
