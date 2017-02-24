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

<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/natural_sort.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>

<script src="<c:url value='/scripts/jquery/datatables/js/TableTools.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/dataTables.tableTools.css'/>" rel="stylesheet" type="text/css">

<script src="<c:url value='/scripts/jquery/datatables/js/ZeroClipboard.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/runCalendar.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>" rel="stylesheet" type="text/css">

<div id="maincontent">
<div id="contentcolumn">
<div id="tabs">
<ul>
  <li><a href="#tabProjects"><span>Projects Report</span></a></li>
  <li><a href="#tabProjectRunLane"><span>Project - Run - Lane Report</span></a></li>
  <li><a href="#tabSamples"><span>Samples Report</span></a></li>
  <li><a href="#tabLibraries"><span>Libraries Report</span></a></li>
  <li><a href="#tabRuns"><span>Runs Report</span></a></li>
  <li><a href="#tabResource"><span>Resource Calendar</span></a></li>
</ul>
<div id="tabProjects">
  <h1>
    <span id="tab1_title">Projects Report</span>
  </h1>

  <fieldset>
    <legend> Filter Projects</legend>
    <table class="no-border">
      <tr>
        <td>Name, Alias, Description Contains:</td>
        <td><input type="text" id="projectReportSearchInput" size="20"/></td>
      </tr>
      <tr>
        <td>Created From :</td>
        <td>
          <input id="from" name="from" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("from");
          </script>
          To :
          <input id="to" name="to" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("to");
          </script>
        </td>
      </tr>
      <tr>
        <td>Project Progress:</td>
        <td>
          <select id="projectProgress" name="projectProgress">
          </select></td>
      </tr>
      <tr>
        <td class="full-width">
          <button class="fg-button ui-state-default ui-corner-all"
                  id="generateProjectsFlexReportButton"
                  onclick="Reports.generateProjectsFlexReport();">
            Generate Report
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="resetProjectsFlexReportButton"
                  onclick="Reports.ui.initProjects();">
            Reset
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="searchProjects"
                  onclick="Reports.search.searchProjects();">
            Search
          </button>
        </td>
      </tr>
    </table>
  </fieldset>

  <form name="generateProjectsFlexReportForm" id="generateProjectsFlexReportForm">
    <div id="projectsResultTable"><i>Please search for projects to be reported...</i></div>
  </form>

  <br/>
  <table id="projectOverviewFlexReportTable" class="full-width no-border" style="visibility: hidden">
    <tr>
      <td valign="top" id="projectOverviewCell" class="ui-widget ui-widget-content ui-corner-all half-width">
      </td>
      <td align="center" class="half-width">
        <div id="projectStatusChartWrapper">
          <div id="projectStatusChart" class="ui-widget ui-widget-content ui-corner-all"></div>
        </div>
      </td>
    </tr>
  </table>
  <br/>

  <div id="projectsFlexReport" class="ui-widget ui-widget-content ui-corner-all" style="visibility: hidden"></div>
  <div id="projectsDetailReport" class="ui-widget ui-widget-content ui-corner-all" style="visibility: hidden"></div>
</div>

<div id="tabProjectRunLane">
  <h1>
    <span id="tabProjectRunLane_title">Project - Run - Lane Report</span>
  </h1>

  <fieldset>
    <legend>Filter Projects</legend>
    <table class="no-border">
      <tr>
        <td><b>Project</b> Name, Alias, Description Contains:</td>
        <td><input type="text" id="projectRunLaneReportSearchInput" size="20"/></td>
      </tr>
      <tr>
        <td><b>Run</b> Completed From :</td>
        <td>
          <input id="projectRunLanefrom" name="from" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("projectRunLanefrom");
          </script>
          To :
          <input id="projectRunLaneto" name="to" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("projectRunLaneto");
          </script>
        </td>
      </tr>
      <tr>
        <td class="full-width">
          <button class="fg-button ui-state-default ui-corner-all"
                  id="generateSampleRelationReportButton"
                  onclick="Reports.generateSampleRelationReport();">
            Generate Report
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="searchProjectRunLane"
                  onclick="Reports.search.searchProjectRunLane();">
            Search
          </button>
        </td>
      </tr>
    </table>
  </fieldset>

  <form name="generateProjectsFlexReportForm" id="generateProjectRunLaneFlexReportForm">
    <div id="projectRunLaneResultTable"><i>Please search for projects to be reported...</i></div>
  </form>

  <div id="projectRunLaneFlexReport" class="ui-widget ui-widget-content ui-corner-all" style="visibility: hidden"></div>
</div>

<div id="tabSamples">
  <h1>
    <span id="tabSamples_title">Samples Report</span>
  </h1>

  <fieldset>
    <legend> Filter Samples</legend>
    <table class="no-border">
      <tr>
        <td>Name, Alias, Description Contains:</td>
        <td><input type="text" id="sampleReportSearchInput" size="20"/></td>
      </tr>

      <tr>
        <td>Created From :</td>
        <td>
          <input id="samplefrom" name="from" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("samplefrom");
          </script>
          To :
          <input id="sampleto" name="to" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("sampleto");
          </script>
        </td>
      </tr>
      <tr>
        <td>Sample Type:</td>
        <td>
          <select id="sampleType" name="sampleType">
          </select></td>
      </tr>
      <tr>
        <td>Sample QC:</td>
        <td>
          <select id="sampleQC" name="sampleQC">
            <option value="all">all</option>
            <option value="true">Passed</option>
            <option value="false">Not Passed</option>
            <option value="unknown">Unknown</option>
          </select></td>
      </tr>
      <tr>
        <td class="full-width">
          <button class="fg-button ui-state-default ui-corner-all"
                  id="generateSamplesFlexReportButton"
                  onclick="Reports.generateSamplesFlexReport();">
            Generate Report
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="resetSamplesFlexReportButton"
                  onclick="Reports.ui.initSamples();">
            Reset
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="searchSamples"
                  onclick="Reports.search.searchSamples();">
            Search
          </button>
        </td>
      </tr>
    </table>
  </fieldset>

  <form name="generateSamplesFlexReportForm" id="generateSamplesFlexReportForm" method="POST">
    <div id="samplesResultTable"><i>Please search for samples to be reported...</i></div>
  </form>

  <br/>

  <table id="sampleOverviewFlexReportTable" class="full-width no-border" style="visibility: hidden">
    <tr>
      <td valign="top" id="sampleOverviewCell" class="ui-widget ui-widget-content ui-corner-all half-width">
      </td>
      <td align="center" class="half-width">
        <div class="ui-widget ui-widget-content ui-corner-all">
          <div id="sampleTypesChartWrapper">
            <div id="sampleTypesChart"></div>
          </div>
          <div id="sampleQcChartWrapper">
            <div id="sampleQcChart"></div>
          </div>
        </div>
      </td>
    </tr>
  </table>
  <br/>

  <div id="samplesFlexReport" class="ui-widget ui-widget-content  ui-corner-all" style='visibility: hidden'></div>
</div>

<div id="tabLibraries">
  <h1>
    <span id="tabLibraries_title">Libraries Report</span>
  </h1>

  <fieldset>
    <legend> Filter Libraries</legend>
    <table class="no-border">
      <tr>
        <td>Name, Alias, Description Contains:</td>
        <td><input type="text" id="libraryReportSearchInput" size="20"/></td>
      </tr>

      <tr>
        <td>Created From :</td>
        <td>
          <input id="libraryfrom" name="from" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("libraryfrom");
          </script>
          To :
          <input id="libraryto" name="to" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("libraryto");
          </script>
        </td>
      </tr>
      <tr>
        <td>Platform:</td>
        <td>
          <select id="libraryPlatform" name="libraryPlatform">

          </select></td>
      </tr>
      <tr>
        <td>Library QC:</td>
        <td>
          <select id="libraryQC" name="libraryQC">
            <option value="all">all</option>
            <option value="true">Passed</option>
            <option value="false">Not Passed</option>
          </select></td>
      </tr>
      <tr>
        <td class="full-width">
          <button class="fg-button ui-state-default ui-corner-all"
                  id="generateLibrariesFlexReportButton"
                  onclick="Reports.generateLibrariesFlexReport();">
            Generate Report
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="resetLibrariesFlexReportButton"
                  onclick="Reports.ui.initLibraries();">
            Reset
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="searchLibraries"
                  onclick="Reports.search.searchLibraries();">
            Search
          </button>
        </td>
      </tr>
    </table>
  </fieldset>

  <form name="generateLibrariesFlexReportForm" id="generateLibrariesFlexReportForm" method="POST">
    <div id="librariesResultTable"><i>Please search for libraries to be reported...</i></div>
  </form>

  <br/>
  <table class="full-width no-border" id='libraryOverviewFlexReportTable' style="visibility: hidden">
    <tr>
      <td valign="top" id="libraryOverviewCell" class="ui-widget ui-widget-content ui-corner-all half-width">
      </td>
      <td align="center" class="half-width">

        <div class="ui-widget ui-widget-content  ui-corner-all">
          <div id="libraryPlatformChartWrapper">
            <div id="libraryPlatformChart"></div>
          </div>
          <div id="libraryQcChartWrapper">
            <div id="libraryQcChart"></div>
          </div>
        </div>
      </td>
    </tr>
  </table>
  <br/>

  <div id="librariesFlexReport" class="ui-widget ui-widget-content  ui-corner-all" style="visibility: hidden"></div>
  <div id="librariesRelationQC" class="ui-widget ui-widget-content  ui-corner-all" style="visibility: hidden"></div>
</div>

<div id="tabRuns">
  <h1>
    <span id="tab2_title">Runs Report</span>
  </h1>

  <fieldset>
    <legend>Filter Runs</legend>
    <table class="no-border">
      <tr>
        <td>Name, Alias, Description Contains:</td>
        <td><input type="text" id="runReportSearchInput" size="20"/></td>
      </tr>
        <tr>
            <td>Started From :</td>
            <td>
                <input id="runStartedFrom" name="runStartedFrom" type="text" value=""/>
                <script type="text/javascript">
                    Utils.ui.addDatePicker("runStartedFrom");
                </script>
                To :
                <input id="runStartedTo" name="runStartedFrom" type="text" value=""/>
                <script type="text/javascript">
                    Utils.ui.addDatePicker("runStartedTo");
                </script>
            </td>
        </tr>
      <tr>
        <td>Completed From :</td>
        <td>
          <input id="runfrom" name="from" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("runfrom");
          </script>
          To :
          <input id="runto" name="to" type="text" value=""/>
          <script type="text/javascript">
            Utils.ui.addDatePicker("runto");
          </script>
        </td>
      </tr>
      <tr>
        <td>Run Status:</td>
        <td>
          <select id="runStatus" name="runStatus">
          </select></td>
      </tr>
      <tr>
        <td>Run Platform:</td>
        <td>
          <select id="runPlatform" name="runPlatform">
          </select></td>
      </tr>
      <tr>
        <td class="full-width">
          <button class="fg-button ui-state-default ui-corner-all" id="generateRunsFlexReportButton"
                  onclick="Reports.generateRunsFlexReport()">
            Generate Report
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="resetRunsFlexReportButton"
                  onclick="Reports.ui.initRuns();">
            Reset
          </button>
          <button class="fg-button ui-state-default ui-corner-all" id="searchRuns"
                  onclick="Reports.search.searchRuns();">
            Search
          </button>
        </td>
      </tr>
    </table>
  </fieldset>
  <form name="generateRunsFlexReportForm" id="generateRunsFlexReportForm" method="POST">
    <div id="runsResultTable"><i>Please search for runs to be reported...</i></div>
  </form>

  <br/>
  <table class="full-width no-border" id="runOverviewFlexReportTable" style="visibility: hidden">
    <tr>
      <td valign="top" id="runOverviewCell" class="ui-widget ui-widget-content  ui-corner-all half-width">
      </td>
      <td align="center" class="half-width">

        <div class="ui-widget ui-widget-content  ui-corner-all">

          <div id="runStatusChartWrapper">
            <div id="runStatusChart"></div>
          </div>
          <div id="runPlatformChartWrapper">
            <div id="runPlatformChart"></div>
          </div>
        </div>
      </td>
    </tr>
  </table>
  <br/>

  <div id="runsFlexReport" class="ui-widget ui-widget-content  ui-corner-all" style='visibility: hidden'></div>
  <div id="runsPartitionReport" class="ui-widget ui-widget-content  ui-corner-all" style='visibility: hidden'></div>
</div>

<div style="height:1000px;" id="tabResource">
  <div>
    <table class="full-width">
      <tr valign="top">
        <td>
          <div id="sequencerslist"></div>
        </td>
        <td rowspan="2" align="right">
          <div style="position: relative; left: 0">
            <fieldset style="text-align:right;">
              <legend><b>Showing Date Range</b></legend>
              <div id="daterange"></div>
            </fieldset>
          </div>
        </td>
        <%--<td id="legendlist" rowspan="2">--%>
      </tr>
      <tr valign="top">
        <td colspan=2>
          <b> Show me: </b> &nbsp;
          <select id='datepicking' name='datepicking' onchange='Reports.ui.updateCalendar();'>
            <option value=cyear selected="selected"> Current Year</option>
            <option value=lweek> Last week</option>
            <option value=lmonth> Last Month</option>
            <option value=l3month> Last Three Month</option>
            <option value=l6month> Last Six Month</option>
            <option value=lyear> Last Year</option>
            <option value=custom> Custom</option>
          </select>
          <br/><br/>

          <div id="custom" style="display: none;">
            <b>
              From :
              <input id="calendarfrom" name="from" type="text" value=""/>
              To :
              <input id="calendarto" name="to" type="text" value=""/>
            </b>

            <button id="Update"
                    onclick="drawCalendar(jQuery('#datepicking').val(), jQuery('#sequencers').val(), jQuery('#sequencers option:last').val());">
              Update Calendar
            </button>

            <button id="Reset" onclick="Reports.ui.resetCalendar()">Reset Calendar</button>
          </div>
        </td>
      </tr>
    </table>
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#legendlist_arrowclick'), 'legendlist');">
      Colour Schema
      <div id="legendlist_arrowclick" class="toggleLeft"></div>
    </div>
    <div id="legendlist" style="display:none;"></div>
  </div>
  <div style="margin-top:10px;" id="statusmenu" class="menuD3Calendar">
  </div>
  <div style="margin-top:50px;" class='gallery' id='chartD3Calendar'>
    <%--Chart will come here--%>
  </div>
</div>
</div>

<script type="text/javascript">
  jQuery("#tabs").tabs();

  jQuery(document).ready(function () {
    Reports.ui.prepareTable();
    Reports.ui.initProjects();
    Reports.ui.initSamples();
    Reports.ui.initLibraries();
    Reports.ui.initRuns();
    Reports.ui.getSequencersList();
  });
</script>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
