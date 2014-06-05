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
<script type="text/javascript" src="<c:url value='/scripts/dashboard.js?ts=${timestamp.time}'/>"></script>

<script type="text/javascript" src="<c:url value='/scripts/jquery/dateslider/jQAllRangeSliders-withRuler-min.js?ts=${timestamp.time}'/>"></script>
<link href="<c:url value='/scripts/jquery/dateslider/iThing.css'/>" rel="stylesheet" type="text/css">

<div id="maincontent">
    <div id="contentcolumn">
      <nav class="navbar navbar-default" role="navigation">
         <div class="navbar-header">
            <span class="navbar-brand navbar-center">
              Dashboard
            </span>
         </div>
      </nav>
        <%--
          <div id="alertbox">
            <fieldset class="alertwidget">
              <legend>Alerts</legend>
             <div id="alertList" class="elementList"><i style="color: gray">No unread alerts</i></div>
            </fieldset>
          </div>

          <div class="col-sm-6 col-md-6 col-lg-6 small-pad"> <!-- right hand side -->
            <div class="panel panel-primary panel-dashboard">
              <div class="panel-heading">
                <h3 class="panel-title">Projects with recently received samples</h3>
              </div>
              <div class="panel-body">
                <div id="latestSamplesList" class="list-group" align="right"></div>
              </div>
            </div>
          </div>
        </div>

        <div class="row-fluid">
          <h3 class="page-header header-margin">Workflows
            <i class="fa fa-puzzle-piece fa-2x header-icon pull-left"></i>
          </h3>
          <div class="col-sm-12 col-md-12 col-lg-12 small-pad">
            <div class="panel panel-primary panel-dashboard">
              <nav class="navbar navbar-default navbar-panel-heading" role="navigation">
                <h3 id="workflow-widget-title" class="navbar-header panel-title">Incomplete Workflows</h3>
                <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
                  <ul class="nav navbar-nav navbar-right">
                    <li id="workflow-menu" class="dropdown">
                      <a id="wdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
                      <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="wdrop1">
                        <li role="presentation"><a href="javascript:void(0);" onclick="Workflow.ui.newWorkflow();">New Workflow</a></li>
                      </ul>
                    </li>
                  </ul>
                </div>
              </nav>
              <div class="panel-body">
                <div id="workflow-div">
                  <table cellpadding="0" cellspacing="0" border="0" class="display" id="workflowListTable">
                  </table>
                  <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                  <script type="text/javascript">
                    jQuery(document).ready(function () {
                      Workflow.ui.createListingIncompleteWorkflowsTable('workflowListTable');
                    });
                  </script>
                  </c:if>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="row-fluid">
          <h3 class="page-header header-margin">Reports
            <i class="fa fa-sliders fa-2x header-icon pull-left"></i>
          </h3>
          <div class="col-sm-12 col-md-12 col-lg-12 small-pad">
            <div class="panel panel-primary panel-dashboard">
              <div class="panel-heading">
                <h3 class="panel-title">Quick Report</h3>
              </div>
              <div class="panel-body">
                <div id="report-div">

                  <div id='dateSlider'></div>
                  <script>
                    var now = new Date();
                    var minusMonth = new Date().setMonth(now.getMonth() - 1);
                    var minusYear = new Date().setFullYear(now.getFullYear() - 1);
                    var months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"];

                    jQuery("#dateSlider").dateRangeSlider(
                      {
                        valueLabels: "change",
                        delayOut: 2000,
                        defaultValues: {
                          min: minusMonth,
                          max: now
                        },
                        bounds: {
                          min: minusYear,
                          max: now
                        },
                        scales: [{
                          first: function(value){ return value; },
                          end: function(value) {return value; },
                          next: function(value){
                            var next = new Date(value);
                            return new Date(next.setMonth(value.getMonth() + 1));
                          },
                          label: function(value){
                            return months[value.getMonth()];
                          }
                        }]
                      }
                    );
                    jQuery("#dateSlider").bind("valuesChanged", function(e, data){
                      //console.log("Replotting based on min: " + data.values.min + " max: " + data.values.max);
                    });
                  </script>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-sm-12 col-md-6 col-lg-6 small-pad"> <!-- right hand side -->
        <h3 class="page-header header-margin">Tracking
          <i class="fa fa-flask fa-2x header-icon pull-left"></i>
        </h3>
        <div class="row-fluid">
          <div class="col-sm-12 col-md-6 col-lg-6 small-pad">
            <div class="panel panel-primary panel-dashboard">
              <div class="panel-heading">
                <h3 class="panel-title float-left">Runs</h3>
                <input type="text" size="20" id="searchRun" name="searchRun" class="form-control float-right"/>
              </div>
              <div class="panel-body">
                <div id="searchRunresult" class="list-group"></div>
              </div>
            </div>
          </div>

          <div class="col-sm-12 col-md-6 col-lg-6 small-pad">
            <div class="panel panel-primary panel-dashboard">
              <div class="panel-heading">
                <h3 class="panel-title float-left">Samples</h3>
                <input type="text" size="20" id="searchSample" name="searchSample" class="form-control float-right"/>
              </div>
              <div class="panel-body">
                <div id="searchSampleresult" class="list-group"></div>
              </div>
            </div>
          </div>

          <div class="col-sm-12 col-md-6 col-lg-6 small-pad">
            <div class="panel panel-primary panel-dashboard">
              <div class="panel-heading">
                <h3 class="panel-title float-left">Libraries</h3>
                <input type="text" size="20" id="searchLibrary" name="searchLibrary" class="form-control float-right"/>
              </div>
              <div class="panel-body">
                <div id="searchLibraryresult" class="list-group"></div>
              </div>
            </div>
          </div>

          <div class="col-sm-12 col-md-6 col-lg-6 small-pad">
            <div class="panel panel-primary panel-dashboard">
              <div class="panel-heading">
                <h3 class="panel-title float-left">Library Dilutions</h3>
                <input type="text" size="20" id="searchLibraryDilution" name="searchLibraryDilution" class="form-control float-right"/>
              </div>
              <div class="panel-body">
                <div id="searchLibraryDilutionresult" class="list-group"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
  jQuery(document).ready(function() {
    Dashboard.showLatestReceivedtSamples();

    Utils.timer.typewatchFunc(jQuery('#searchProject'), function () {
        Search.dashboardSearch(jQuery('#searchProject'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchRun'), function () {
        Search.dashboardSearch(jQuery('#searchRun'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchSample'), function () {
        Search.dashboardSearch(jQuery('#searchSample'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchLibrary'), function () {
        Search.dashboardSearch(jQuery('#searchLibrary'))
    }, 300, 2);
    Utils.timer.typewatchFunc(jQuery('#searchLibraryDilution'), function () {
        Search.dashboardSearch(jQuery('#searchLibraryDilution'))
    }, 300, 2);

    Search.dashboardSearch(jQuery('#searchProject'), true);
    Search.dashboardSearch(jQuery('#searchRun'), true);
    Search.dashboardSearch(jQuery('#searchLibrary'), true);
    Search.dashboardSearch(jQuery('#searchSample'), true);
    Search.dashboardSearch(jQuery('#searchLibraryDilution'), true);
  });
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>