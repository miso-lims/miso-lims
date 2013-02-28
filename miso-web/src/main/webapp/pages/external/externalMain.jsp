<%@ include file="externalHeader.jsp" %>

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

<link rel="stylesheet" href="<c:url value='/styles/progress.css'/>" type="text/css">
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">
<div id="maincontent">
    <div id="contentcolumn">
        <h1>Your Project Status</h1>

        <div id="externalProjectStatus">
            <p>Please select a project to view.</p>
        </div>
        <div id="externalSampleStatusWrapper">
            <table cellpadding="0" cellspacing="0" border="0" class="display" id="externalSampleStatus">
            </table>
        </div>
        <div id="externalSampleQcStatus">
        </div>
        <div id="externalRunStatusWrapper">
            <table cellpadding="0" cellspacing="0" border="0" class="display" id="externalRunStatus">
            </table>
        </div>
    </div>
</div>

<div id="subcontent">
    <p>List of Available Projects</p>

    <div id="externalProjectsListing">Loading....</div>
</div>

<script type="text/javascript">
    jQuery(document).ready(function () {
        externalProjectsListing();
    });

    function externalProjectsListing() {
        jQuery('#externalProjectsListing').html("<img src='../styles/images/ajax-loader.gif'/>");
        Fluxion.doAjax(
                'externalSectionControllerHelperService',
                'listProjects',
                {'url': ajaxurl},
                {
                    "doOnSuccess": function (json) {
                        jQuery('#externalProjectsListing').html(json.html);
                    }
                });
    }

    function showProjectStatus(projectId) {
        createListingSamplesTable(projectId);
        createListingRunsTable(projectId);
        Fluxion.doAjax(
                'externalSectionControllerHelperService',
                'projectStatus',
                {'projectId': projectId, 'url': ajaxurl},
                {
                    "doOnSuccess": function (json) {
                        jQuery('#externalProjectStatus').html(json.projectJson);
                        jQuery('#externalSampleQcStatus').html(json.sampleQcJson);
                    }
                });
    }

    function createListingSamplesTable(projectId) {
        jQuery('#externalSampleStatusWrapper').html("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\" id=\"externalSampleStatus\"></table>");
        jQuery('#externalSampleStatus').html("<img src='../styles/images/ajax-loader.gif'/>");

        Fluxion.doAjax(
                'externalSectionControllerHelperService',
                'listSamplesDataTable',
                {
                    'projectId': projectId, 'url': ajaxurl
                },
                {'doOnSuccess': function (json) {
                    jQuery('#externalSampleStatus').html('');
                    jQuery('#externalSampleStatus').dataTable({
                                                                  "aaData": json.array,
                                                                  "aoColumns": [
                                                                      { "sTitle": "Sample Alias"},
                                                                      { "sTitle": "Type"},
                                                                      { "sTitle": "QC Passed"},
                                                                      { "sTitle": "Qubit Concentration"},
                                                                      { "sTitle": "Received"}
                                                                  ],
                                                                  "bJQueryUI": true
                                                              });
                }
                }
        );
    }

    function createListingRunsTable(projectId) {
        jQuery('#externalRunStatusWrapper').html("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\" id=\"externalRunStatus\"></table>");
        jQuery('#externalRunStatus').html("<img src='../styles/images/ajax-loader.gif'/>");

        jQuery.fn.dataTableExt.oSort['no-run-asc'] = function (x, y) {
          var a = parseInt(x.replace(/^RUN/i, ""));
          var b = parseInt(y.replace(/^RUN/i, ""));
          return ((a < b) ? -1 : ((a > b) ? 1 : 0));
        };
        jQuery.fn.dataTableExt.oSort['no-run-desc'] = function (x, y) {
          var a = parseInt(x.replace(/^RUN/i, ""));
          var b = parseInt(y.replace(/^RUN/i, ""));
          return ((a < b) ? 1 : ((a > b) ? -1 : 0));
        };

        Fluxion.doAjax(
                'externalSectionControllerHelperService',
                'listRunsDataTable',
                {
                    'projectId': projectId, 'url': ajaxurl
                },
                {'doOnSuccess': function (json) {
                    jQuery('#externalRunStatus').html('');
                    jQuery('#externalRunStatus').dataTable({
                                                               "aaData": json.array,
                                                               "aoColumns": [
                                                                   { "sTitle": "Run Name", "sType": "no-run"},
                                                                   { "sTitle": "Status"},
                                                                   { "sTitle": "Start Date"},
                                                                   { "sTitle": "End Date"},
                                                                   { "sTitle": "Type"},
                                                                   { "sTitle": "Samples"}
                                                               ],
                                                               "bJQueryUI": true
                                                           });
                }
                }
        );
    }

</script>

<%@ include file="externalFooter.jsp" %>