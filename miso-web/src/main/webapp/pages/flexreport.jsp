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

<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/flexreport.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/datatables_utils.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value='/scripts/runCalendar.js?ts=${timestamp.time}'/>"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>"
      type="text/css">


<div id="tabs">
<ul>
    <li><a href="#tabProjects"><span>Projects Report</span></a></li>
    <li><a href="#tabSamples"><span>Samples Report</span></a></li>
    <li><a href="#tabLibraries"><span>Libraries Report</span></a></li>
    <li><a href="#tabRuns"><span>Runs Report</span></a></li>
    <li><a href="#tabResource"><span>Resource Calendar</span></a></li>
</ul>
<div id="tabProjects">

    <h1>
        <div id="tab1_title">Projects Report</div>
    </h1>

    <fieldset>
        <legend> Filter Projects</legend>
        <table border="0">
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
        </table>
    </fieldset>

    <form name="generateProjectsFlexReportForm" id="generateProjectsFlexReportForm">
        <div id="projectsResultTable"><img src="<c:url value='/styles/images/ajax-loader.gif'/>"/></div>
    </form>

    <br/>
    <hr/>
    <table width="100%" border="0">
        <tr>
            <td valign="top" width="50%" id="projectOverviewCell" class="ui-widget ui-widget-content  ui-corner-all">
            </td>
            <td align="center" width="50%">
                <div id="projectStatusChart" class="ui-widget ui-widget-content  ui-corner-all"></div>
            </td>
        </tr>
    </table>
    <br/>

    <div id="projectsFlexReport" class="ui-widget ui-widget-content  ui-corner-all"></div>


</div>

<div id="tabSamples">

    <h1>
        <div id="tabSamples_title">Samples Report</div>
    </h1>

    <fieldset>
        <legend> Filter Samples</legend>
        <table border="0">
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
        </table>
    </fieldset>

    <form name="generateSamplesFlexReportForm" id="generateSamplesFlexReportForm" method="POST">
        <div id="samplesResultTable"><img src="<c:url value='/styles/images/ajax-loader.gif'/>"/></div>
    </form>

    <br/>
    <hr/>
    <table width="100%" border="0">
        <tr>
            <td valign="top" width="50%" id="sampleOverviewCell" class="ui-widget ui-widget-content  ui-corner-all">
            </td>
            <td align="center" width="50%">
                <div id="sampleStatusChart" class="ui-widget ui-widget-content  ui-corner-all"></div>
            </td>
        </tr>
    </table>
    <br/>

    <div id="samplesFlexReport" class="ui-widget ui-widget-content  ui-corner-all"></div>
</div>

<div id="tabLibraries">

    <h1>
        <div id="tabLibraries_title">Libraries Report</div>
    </h1>

    <fieldset>
        <legend> Filter Libraries</legend>
        <table border="0">
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
        </table>
    </fieldset>

    <form name="generateLibrariesFlexReportForm" id="generateLibrariesFlexReportForm" method="POST">
        <div id="librariesResultTable"><img src="<c:url value='/styles/images/ajax-loader.gif'/>"/></div>
    </form>

    <br/>
    <hr/>
    <table width="100%" border="0">
        <tr>
            <td valign="top" width="50%" id="libraryOverviewCell" class="ui-widget ui-widget-content  ui-corner-all">
            </td>
            <td align="center" width="50%">
                <div id="libraryStatusChart" class="ui-widget ui-widget-content  ui-corner-all"></div>
            </td>
        </tr>
    </table>
    <br/>

    <div id="librariesFlexReport" class="ui-widget ui-widget-content  ui-corner-all"></div>
</div>

<div id="tabRuns">
    <h1>
        <div id="tab2_title">Runs Report</div>
    </h1>

    <fieldset>
        <legend>Filter Runs</legend>
        <table border="0">
            <tr>
                <td>Name, Alias, Description Contains:</td>
                <td><input type="text" id="runReportSearchInput" size="20"/></td>
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
        </table>
    </fieldset>
    <form name="generateRunsFlexReportForm" id="generateRunsFlexReportForm" method="POST">
        <div id="runsResultTable"><img src="<c:url value='/styles/images/ajax-loader.gif'/>"/></div>
    </form>

    <br/>
    <hr/>
    <table width="100%" border="0">
        <tr>
            <td valign="top" width="50%" id="runOverviewCell" class="ui-widget ui-widget-content  ui-corner-all">
            </td>
            <td align="center" wiPrevdth="50%">
                <div id="runStatusChart" class="ui-widget ui-widget-content  ui-corner-all"></div>
            </td>
        </tr>
    </table>
    <br/>

    <div id="runsFlexReport" class="ui-widget ui-widget-content  ui-corner-all"></div>

</div>

<div style="height:1000px;" id="tabResource">

    <div>

        <table width="100%">
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
                        <option value=cyear selected=""> Current Year</option>
                        <option value=lweek> Last week</option>
                        <option value=lmonth> Last Month</option>
                        <option value=l3month> Last Three Month</option>
                        <option value=l6month> Last Six Month</option>
                        <option value=lyear> Last Year</option>
                        <option value=custom> Custom</option>
                    </select>

                    <br> <br>

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
         <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#legendlist_arrowclick'), 'legendlist');">Colour Schema
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

  jQuery(document).ready(function() {
    Reports.ui.prepareTable();
    Reports.ui.initProjects();
    Reports.ui.initSamples();
    Reports.ui.initLibraries();
    Reports.ui.initRuns();
    Reports.ui.getSequencersList();
  });
</script>


<%@ include file="../footer.jsp" %>