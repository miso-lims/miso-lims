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

<script type="text/javascript" src="<c:url value='/scripts/jquery/sidr/jquery.sidr.min.js'/>"></script>
<link href="<c:url value='/scripts/jquery/sidr/stylesheets/jquery.sidr.light.css'/>" media="all" rel="stylesheet" type="text/css">

<script type="text/javascript">
  jQuery(document).ready(function() {
    jQuery('#sidebar').sidr({
      name: 'sidr-existing-content',
      source: '#sidebar-content',
      side: "left",
      displace: false
    });
  });

  function showNav() {
    jQuery('#sidebar-button').addClass('sidebar-content-left', 200);
  }

  function hideNav() {
    jQuery('#sidebar-button').removeClass('sidebar-content-left', 200);
  }

  function toggleNav() {
    if (jQuery('#sidr-existing-content').css('display') !== 'none') {
      return hideNav();
    } else {
      return showNav();
    }
  }
</script>

<style>
.nav-menu {
  position: absolute;
  left:0;
  top: 50%;
  background: #F0F0FF;
  font-weight:bold;
  font-size:1.1em;
  padding:5px 10px 5px 10px;
  border-bottom-right-radius:5px;
  border-bottom-left-radius:5px;
  cursor:pointer;
  -webkit-transform-origin: 0 0;
  -webkit-transform: rotate(270deg);
  -moz-transform-origin: 0 0;
  -moz-transform: rotate(270deg);
}

.nav-menu:hover {
  box-shadow: 0 0 3pt 2pt rgba(88, 151, 251, 1);
}

.content {
  padding: 10px 25px 10px 10px;
  position: fixed;
  top: 75px;
  bottom: 60px;
  left: 0;
  overflow: auto;
}

.sidebar-content-left {
  left: 260px;
}

#sidr-existing-content {
  border-right: 1px solid darkgrey;
}

.sidr h2 {
  font-weight: bold;
  background-image: linear-gradient(#FFFFFF,#F3F3F3);
}
.sidr ul {
  margin-bottom: 4px;
}

.sidr ul li {
  line-height: 25px;
}

.sidr ul li:hover,.sidr ul li.active,.sidr ul li.sidr-class-active {
  border-top:1px solid #fff;
  line-height: 25px;
}

.sidr ul li ul li {
  line-height: 25px;
}

.sidr ul li ul li:hover,.sidr ul li ul li.active,.sidr ul li ul li.sidr-class-active {
  border-top:1px solid #fff;
  line-height: 25px;
}
</style>

<div id="sidebar-content" style="display: none;">
  <h2>Sample Processing</h2>
  <ul class="bullets">
    <li><a href="<c:url value="/miso/samplegroup/new"/>">Create Sample Group</a></li>
    <li><a href="<c:url value="/miso/samplegroups"/>">List Sample Groups</a></li>
    <li><a href="<c:url value="/miso/sample/receipt"/>">Receive Samples</a></li>
    <li><a href="<c:url value="/miso/importexport"/>">Import & Export</a></li>
    <%--<li><a href="<c:url value="/miso/plate/import"/>">Import Plate Sheet</a></li>--%>
    <%--<li><a href="<c:url value="/miso/plate/export"/>">Export Plate Sheet</a></li>--%>
  </ul>

  <h2>Workflows</h2>
  <ul class="bullets">
    <sec:authorize access="hasRole('ROLE_ADMIN')">
    <li><a href="<c:url value="/miso/admin/workflows"/>">Workflow Admin</a></li>
    </sec:authorize>
    <li><a href="<c:url value="/miso/workflows"/>">My Workflows</a></li>
    <li><a href="<c:url value="/miso/workflow/manage"/>">Manage Workflows</a></li>
  </ul>

  <h2>Sequencing</h2>
  <ul class="bullets">
    <li><a href="<c:url value="/miso/pools/ready"/>">Pools Ready to Run</a></li>
    <li><a href="<c:url value="/miso/container/new"/>">Create New Partition Container</a></li>
    <li><a href="<c:url value="/miso/containers"/>">List Partition Containers</a></li>
    <li><a href="<c:url value="/miso/run/new"/>">Create New Run</a></li>
    <li><a href="<c:url value="/miso/runs"/>">List Runs</a></li>
  </ul>

  <h2>Tracking</h2>
  <ul class="bullets">
    <li><a href="<c:url value="/miso/projects"/>">List Projects</a></li>
    <li><a href="<c:url value="/miso/samples"/>">List Samples</a></li>
    <li><a href="<c:url value="/miso/libraries"/>">List Libraries</a></li>
    <li><a href="<c:url value="/miso/pools"/>">List Pools</a></li>
    <li><a href="<c:url value="/miso/plates"/>">List Plates</a></li>
    <li><a href="<c:url value="/miso/kitdescriptors"/>">List Consumables</a></li>
    <li><a href="<c:url value="/miso/studies"/>">List Studies</a></li>
    <li><a href="<c:url value="/miso/experiments"/>">List Experiments</a></li>
  </ul>

  <h2>Print Jobs</h2>
  <ul class="bullets">
    <li><a href="<c:url value="/miso/printjobs"/>">My Print Jobs</a></li>
    <li><a href="<c:url value="/miso/custombarcode"/>">Custom Barcode Printing</a></li>
    <sec:authorize access="hasRole('ROLE_ADMIN')">
    <li><a href="<c:url value="/miso/admin/configuration/printers"/>">Printer Admin</a></li>
    </sec:authorize>
  </ul>

  <sec:authorize access="hasRole('ROLE_ADMIN')">
  <h2>User Administration</h2>
  <ul class="bullets">
    <li><a href="<c:url value="/miso/admin/users"/>">List Users</a></li>
    <li><a href="<c:url value="/miso/admin/user/new"/>">Create User</a></li>
    <li><a href="<c:url value="/miso/admin/groups"/>">List Groups</a></li>
    <li><a href="<c:url value="/miso/admin/group/new"/>">Create Group</a></li>
  </ul>
  </sec:authorize>
</div>

<div id="sidebar-button" class="content">
  <div id="sidebar" class="nav-menu" onclick="javascript: toggleNav();">
    Navigation
  </div>
</div>
