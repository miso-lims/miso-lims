<%@ include file="../header.jsp" %>

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

<div id="maincontent">
<div id="contentcolumn">
<div id="tabs">
  <ul>
    <li><a href="#tab-1"><span>Tasks</span></a></li>
  </ul>

  <div id="tab-1">
    <i>This page needs to be refreshed manually</i>

    <h1>Running Jobs</h1>

    <div id="runningDiv">
      <table id="runningTasks" class="list">
        <thead>
        <tr>
          <th>Job ID</th>
          <th>Job Name</th>
          <th>Pipeline</th>
          <th>Info</th>
          <th>Start Date</th>
          <th>Processes</th>
        </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>

    <h1>Pending Jobs</h1>

    <div id="pendingDiv">
      <table id="pendingTasks" class="list">
        <thead>
        <tr>
          <th>Job ID</th>
          <th>Job Name</th>
          <th>Pipeline</th>
          <th>Info</th>
          <th>Start Date</th>
        </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>

    <h1>Completed Jobs</h1>

    <div id="completedDiv">
      <table id="completedTasks" class="list">
        <thead>
        <tr>
          <th>Job ID</th>
          <th>Job Name</th>
          <th>Pipeline</th>
          <th>Info</th>
          <th>Start Date</th>
          <th>End Date</th>
          <th>State</th>
        </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>

    <h1>Failed Jobs</h1>

    <div id="failedDiv">
      <table id="failedTasks" class="list">
        <thead>
        <tr>
          <th>Job ID</th>
          <th>Job Name</th>
          <th>Pipeline</th>
          <th>Info</th>
          <th>Start Date</th>
        </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>

  </div>

  <script type="text/javascript">
    jQuery(document).ready(function () {
      jQuery("#tabs").tabs();
      jQuery("#tabs").removeClass('ui-widget').removeClass('ui-widget-content');

      Tasks.ui.populateRunningTasks();
      Tasks.ui.populatePendingTasks();
      Tasks.ui.populateFailedTasks();
      Tasks.ui.populateCompletedTasks();
    });
  </script>
</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
