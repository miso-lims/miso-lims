<%@ include file="../header.jsp" %>

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

<div id="maincontent">
<div id="contentcolumn">
<div id="tabs">
  <ul>
    <li><a href="#tab-1"><span>Tasks</span></a></li>
    <li><a href="#tab-2"><span>Pipelines</span></a></li>
  </ul>

  <div id="tab-1">
    <i>This page needs to be refreshed manually</i>

    <div id="runningDiv" class="panel panel-default padded-panel">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              Running Jobs
            </span>
         </div>
      </nav>

      <table id="runningTasks" class="table table-bordered table-striped">
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

    <div id="pendingDiv" class="panel panel-default padded-panel">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              Pending Jobs
            </span>
         </div>
      </nav>

      <table id="pendingTasks" class="table table-bordered table-striped">
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

    <div id="completedDiv" class="panel panel-default padded-panel">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              Completed Jobs
            </span>
         </div>
      </nav>

      <table id="completedTasks" class="table table-bordered table-striped">
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

    <div id="failedDiv" class="panel panel-default padded-panel">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              Failed Jobs
            </span>
         </div>
      </nav>

      <table id="failedTasks" class="table table-bordered table-striped">
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

  <div id="tab-2">
    <div id="pipelineDiv" class="panel panel-default padded-panel">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              Available Pipelines
            </span>
         </div>
      </nav>

      <table id="pipelines" class="table table-bordered table-striped">
        <thead>
        <tr>
          <th>Pipeline Name</th>
          <th>Processes</th>
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

      Tasks.ui.populatePipelines();
      Tasks.ui.populateRunningTasks();
      Tasks.ui.populatePendingTasks();
      Tasks.ui.populateFailedTasks();
      Tasks.ui.populateCompletedTasks();

      // update every 30 secs
//      window.setInterval(function () {
//        Tasks.ui.populateRunningTasks();
//        Tasks.ui.populatePendingTasks();
//        Tasks.ui.populateCompletedTasks();
//        Tasks.ui.populateFailedTasks();
//      }, 30000);
    });
  </script>
</div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>