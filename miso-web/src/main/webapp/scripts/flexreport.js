/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

function toggleCheckedItems(className, status) {
  jQuery("." + className).each(function() {
    jQuery(this).attr("checked", status);
  })
}

jQuery(function() {
  var dates = jQuery("#calendarfrom, #calendarto").datepicker({
                                                                defaultDate: "+1w",
                                                                changeMonth: true,
                                                                changeYear: true,
                                                                numberOfMonths: 1,
                                                                dateFormat: 'dd-mm-yy',
                                                                onSelect: function(selectedDate) {
                                                                  var option = this.id == "calendarfrom" ? "minDate" : "maxDate", instance = jQuery(this).data("datepicker"), date = jQuery.datepicker.parseDate(
                                                                          instance.settings.dateFormat ||
                                                                          jQuery.datepicker._defaults.dateFormat,
                                                                          selectedDate, instance.settings);
                                                                  dates.not(this).datepicker("option", option, date);
                                                                }
                                                              });
});

function initProjects() {
  disableButton('resetProjectsFlexReportButton');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'initProjects',
          {'url':ajaxurl},
          {
            "doOnSuccess":
                    function(json) {

                      jQuery('#generateProjectsFlexReportButton').fadeIn();
                      jQuery('#generateProjectsFlexReportForm').fadeIn();
                      jQuery('#projectStatusChart').html('');
                      jQuery('#projectOverviewCell').html('');
                      jQuery('#projectsFlexReport').html('');
                      createProjectFormTable(json.html);
                      jQuery('#projectProgress').html(json.progress);
                      reenableButton('resetProjectsFlexReportButton', 'Reset');
                      prepareTable();
                    }
          });
}

function initSamples() {
  disableButton('resetSamplesFlexReportButton');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'initSamples',
          {'url':ajaxurl},
          {
            "doOnSuccess":
                    function(json) {
                      jQuery('#generateSamplesFlexReportButton').fadeIn();
                      jQuery('#generateSamplesFlexReportForm').fadeIn();
                      jQuery('#sampleStatusChart').html('');
                      jQuery('#sampleOverviewCell').html('');
                      jQuery('#samplesFlexReport').html('');
                      createSampleFormTable(json.html);
                      jQuery('#sampleType').html(json.type);
                      reenableButton('resetSamplesFlexReportButton', 'Reset');
                      prepareTable();
                    }
          });
}

function initLibraries() {
  disableButton('resetLibrariesFlexReportButton');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'initLibraries',
          {'url':ajaxurl},
          {
            "doOnSuccess":
                    function(json) {
                      jQuery('#generateLibrariesFlexReportButton').fadeIn();
                      jQuery('#generateLibrariesFlexReportForm').fadeIn();
                      jQuery('#libraryStatusChart').html('');
                      jQuery('#libraryOverviewCell').html('');
                      jQuery('#librariesFlexReport').html('');
                      createLibraryFormTable(json.html);
                      jQuery('#libraryPlatform').html(json.platform);
                      reenableButton('resetLibrariesFlexReportButton', 'Reset');
                      prepareTable();
                    }
          });
}

function initRuns() {
  disableButton('resetRunsFlexReportButton');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'initRuns',
          {'url':ajaxurl},
          {
            "doOnSuccess":
                    function(json) {
                      jQuery('#generateRunsFlexReportButton').fadeIn();
                      jQuery('#generateRunsFlexReportForm').fadeIn();
                      jQuery('#runStatusChart').html('');
                      jQuery('#runOverviewCell').html('');
                      jQuery('#runsFlexReport').html('');
                      createRunFormTable(json.html);
                      jQuery('#runPlatform').html(json.platform);
                      jQuery('#runStatus').html(json.status);
                      reenableButton('resetRunsFlexReportButton', 'Reset');
                      prepareTable();
                    }
          });
}

function searchProjects() {
  disableButton('searchProjects');
  jQuery('#projectsResultTable').html('<img src=\"/styles/images/ajax-loader.gif\"/>');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'searchProjectsByCreationDateandString',
          {'str':jQuery('#projectReportSearchInput').val(),
            'from':jQuery('#from').val(),'to':jQuery('#to').val(),
            'progress':jQuery('#projectProgress option:selected').val(), 'url':ajaxurl},
          {
            "doOnSuccess":
                    function(json) {
                      createProjectFormTable(json.html);
                      reenableButton('searchProjects', 'Search');
                      prepareTable();
                    }
          });
}

function createProjectFormTable(array) {
  jQuery('#projectsResultTable').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="projectsResultFormTable"></table>');
  jQuery('#projectsResultFormTable').dataTable({
                                                 "aaData": array,
                                                 "aoColumns": [
                                                   {"sTitle":"<input class=\"chkbox1\" type=\"checkbox\" onclick=\"toggleCheckedItems('chkboxprojects',this.checked)\"/> All"},
                                                   { "sTitle": "Project Name"},
                                                   { "sTitle": "Alias"},
                                                   { "sTitle": "Description"},
                                                   { "sTitle": "Progress"}
                                                 ],
                                                 "bPaginate": false,
                                                 "bFilter": false,
                                                 "bSort": false,
                                                 "sDom":'<"top"i>',
                                                 "bAutoWidth": false
                                               });
}

function searchSamples() {
  disableButton('searchSamples');
  jQuery('#samplesResultTable').html('<img src=\"/styles/images/ajax-loader.gif\"/>');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'searchSamplesByCreationDateandString',
          {'str':jQuery('#sampleReportSearchInput').val(),
            'from':jQuery('#samplefrom').val(),'to':jQuery('#sampleto').val(),
            'type':jQuery('#sampleType option:selected').val(),
            'qc':jQuery('#sampleQC option:selected').val(),
            'url':ajaxurl},
          {
            "doOnSuccess":
                    function(json) {
                      createSampleFormTable(json.html);
                      reenableButton('searchSamples', 'Search');
                      prepareTable();
                    }
          });
}

function createSampleFormTable(array) {
  jQuery('#samplesResultTable').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="samplesResultFormTable"></table>');
  jQuery('#samplesResultFormTable').dataTable({
                                                "aaData": array,
                                                "aoColumns": [
                                                  {"sTitle":"<input class=\"chkbox1\" type=\"checkbox\" onclick=\"toggleCheckedItems('chkboxsamples',this.checked)\"/> All"},
                                                  { "sTitle": "Sample Name"},
                                                  { "sTitle": "Alias"},
                                                  { "sTitle": "Description"},
                                                  { "sTitle": "Sample Type"},
                                                  { "sTitle": "QC Passed"}
                                                ],
                                                "bPaginate": false,
                                                "bFilter": false,
                                                "bSort": false,
                                                "sDom":'<"top"i>',
                                                "bAutoWidth": false
                                              });
}

function searchLibraries() {
  disableButton('searchLibraries');
  jQuery('#librariesResultTable').html('<img src=\"/styles/images/ajax-loader.gif\"/>');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'searchLibrariesByCreationDateandString',
          {'str':jQuery('#libraryReportSearchInput').val(),
            'from':jQuery('#libraryfrom').val(),'to':jQuery('#libraryto').val(),
            'platform':jQuery('#libraryPlatform option:selected').val(),
            'qc':jQuery('#libraryQC option:selected').val(),
            'url':ajaxurl},
          {
            "doOnSuccess":
                    function(json) {
                      createLibraryFormTable(json.html);
                      reenableButton('searchLibraries', 'Search');
                      prepareTable();
                    }
          });
}

function createLibraryFormTable(array) {
  jQuery('#librariesResultTable').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="librariesResultFormTable"></table>');
  jQuery('#librariesResultFormTable').dataTable({
                                                  "aaData": array,
                                                  "aoColumns": [
                                                    {"sTitle":"<input class=\"chkbox1\" type=\"checkbox\" onclick=\"toggleCheckedItems('chkboxlibraries',this.checked)\"/> All"},
                                                    { "sTitle": "Library Name"},
                                                    { "sTitle": "Alias"},
                                                    { "sTitle": "Description"},
                                                    { "sTitle": "Platform"},
                                                    { "sTitle": "Library Type"},
                                                    { "sTitle": "QC Passed"}
                                                  ],
                                                  "bPaginate": false,
                                                  "bFilter": false,
                                                  "bSort": false,
                                                  "sDom":'<"top"i>',
                                                  "bAutoWidth": false
                                                });
}

function searchRuns() {
  disableButton('searchRuns');
  jQuery('#runsResultTable').html('<img src=\"/styles/images/ajax-loader.gif\"/>');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'searchRunsByCreationDateandString',
          {'str':jQuery('#runReportSearchInput').val(),
            'from':jQuery('#runfrom').val(),'to':jQuery('#runto').val(),
            'platform':jQuery('#runPlatform option:selected').val(),
            'status':jQuery('#runStatus option:selected').val(),
            'url':ajaxurl},
          {
            "doOnSuccess":
                    function(json) {
                      createRunFormTable(json.html);
                      reenableButton('searchRuns', 'Search');
                      prepareTable();
                    }
          });
}

function createRunFormTable(array) {
  jQuery('#runsResultTable').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="runsResultFormTable"></table>');
  jQuery('#runsResultFormTable').dataTable({
                                             "aaData": array,
                                             "aoColumns": [
                                               {"sTitle":"<input class=\"chkbox1\" type=\"checkbox\" onclick=\"toggleCheckedItems('chkboxruns',this.checked)\"/> All"},
                                               { "sTitle": "Run Name"},
                                               { "sTitle": "Alias"},
                                               { "sTitle": "Status"},
                                               { "sTitle": "Platform"}
                                             ],
                                             "bPaginate": false,
                                             "bFilter": false,
                                             "bSort": false,
                                             "sDom":'<"top"i>',
                                             "bAutoWidth": false
                                           });
}

function plotPieChart(datachart, div) {
  console.log(datachart.toString());
  var w = 300, h = 300, r = 100, color = d3.scale.category20c();
  var vis = d3.select("#" + div)
          .append("svg:svg")
          .data([datachart])
          .attr("width", w)
          .attr("height", h)
          .append("svg:g")
          .attr("transform", "translate(" + r + "," + r + ")");

  var arc = d3.svg.arc()
          .outerRadius(r);

  var pie = d3.layout.pie()
          .value(function(d) {
                   return d.value;
                 });
  var arcs = vis.selectAll("g.slice")
          .data(pie)
          .enter()
          .append("svg:g")
          .attr("class", "slice");

  arcs.append("svg:path")
          .attr("fill", function(d, i) {
                  return color(i);
                })
          .attr("d", arc)
          .append("svg:title")
          .text(function(d, i) {
                  return datachart[i].label;
                });

  arcs.append("svg:text")
          .attr("transform", function(d) {
                  d.innerRadius = 0;
                  d.outerRadius = r;
                  return "translate(" + arc.centroid(d) + ")";
                })
          .attr("text-anchor", "middle")
          .text(function(d, i) {
                  return datachart[i].label;
                });
}

function plotHollowPieChart(data, div) {
  var w = 400, h = 600, r = 150, labelr = r + 10,sum=0 // radius for label anchor
          color = d3.scale.category20c(), donut = d3.layout.pie(), arc = d3.svg.arc().innerRadius(r * .6).outerRadius(r);

  var vis = d3.select("#" + div)
          .append("svg:svg")
          .data([data])
          .attr("width", w + 150)
          .attr("height", h);

  var arcs = vis.selectAll("g.arc")
          .data(donut.value(function(d) {
    sum += parseInt(d.value);
    return d.value
  }))
          .enter().append("svg:g")
          .attr("class", "arc")
          .attr("transform", "translate(" + (r + 120) + "," + (r + 140) + ")");

  arcs.append("svg:path")
          .attr("fill", function(d, i) {
                  return color(i);
                })
          .attr("d", arc)
          .append("svg:title")
          .text(function(d, i) {
                  return data[i].label;
                });

  arcs.append("svg:text")
          .attr("transform", function(d, i) {
                  var c = arc.centroid(d), x = c[0], y = c[1], h = Math.sqrt(x * x + y * y);
                    return ((data[i].value*360/sum) < 10) ? "rotate (" + (c[0] - 90) + ")translate(" + (labelr)+","+((data[i].value*360/sum)*2)+")" : "translate(" +(x / h * labelr) + ',' +  (y / h * labelr)+")";
//                  return "translate(" + (x / h * labelr) + ',' +
//                         (y / h * labelr) + ")";
                })
          .attr("dy", ".35em")
          .attr("text-anchor", "middle")
          .text(function(d, i) {
                  return data[i].label;
                });
}


function generateProjectsFlexReport() {
  disableButton('generateProjectsFlexReportButton');
  jQuery('#generateProjectsFlexReportFormHeader').html('<img src=\"/styles/images/ajax-loader.gif\"/>');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'generateProjectsFlexReport',
          {'form':jQuery('#generateProjectsFlexReportForm').serializeArray(), 'url':ajaxurl},
          {'doOnSuccess':function(json) {
            reenableButton('generateProjectsFlexReportButton', 'Generate Report');

            jQuery('#generateProjectsFlexReportButton').fadeOut();
            jQuery('#generateProjectsFlexReportForm').fadeOut();

            //plotPieChart(json.graph, "projectStatusChart");
            if (jQuery('#projectProgress').val() == 'all' || json.graph.length > 1) {
              plotHollowPieChart(json.graph, "projectStatusChart");
            }
            createProjectOverviewTable(json.overviewTable);
            createProjectResultTable(json.reportTable);
          }
          }
  );
}

function createProjectOverviewTable(array) {
  jQuery('#projectOverviewCell').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="projectOverviewTable"></table>');
  jQuery('#projectOverviewTable').dataTable({
                                              "aaData": array,
                                              "aoColumns": [
                                                { "sTitle": "Status"},
                                                { "sTitle": "Amount"}
                                              ],
                                              "bJQueryUI": true
                                            });
}

function createProjectResultTable(array) {
  jQuery('#projectsFlexReport').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="projectsFlexReportTable"></table>');
  jQuery('#projectsFlexReportTable').dataTable({
                                                 "aaData": array,
                                                 "aoColumns": [
                                                   { "sTitle": "Project Name"},
                                                   { "sTitle": "Alias"},
                                                   { "sTitle": "Description"},
                                                   { "sTitle": "Progress"}
                                                 ],
                                                 "bJQueryUI": true
                                               });
}

function generateSamplesFlexReport() {
  disableButton('generateSamplesFlexReportButton');
  jQuery('#generateSamplesFlexReportFormHeader').html('<img src=\"/styles/images/ajax-loader.gif\"/>');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'generateSamplesFlexReport',
          {'form':jQuery('#generateSamplesFlexReportForm').serializeArray(), 'url':ajaxurl},
          {'doOnSuccess':function(json) {
            reenableButton('generateSamplesFlexReportButton', 'Generate Report');

            jQuery('#generateSamplesFlexReportButton').fadeOut();
            jQuery('#generateSamplesFlexReportForm').fadeOut();

            if (jQuery('#sampleType').val() == 'all' || json.graph.length > 1) {
              plotHollowPieChart(json.graph, "sampleStatusChart");
            }
            if (jQuery('#sampleQC').val() == 'all') {
              plotHollowPieChart(json.qcgraph, "sampleStatusChart");
            }
            createSampleOverviewRelationTable(json.overviewRelationTable);
//            createSampleOverviewTable(json.overviewTable);
            createSampleResultTable(json.reportTable);
          }
          }
  );
}

function createSampleOverviewRelationTable(array) {
  jQuery('#sampleOverviewCell').append('<table cellpadding="0" cellspacing="0" border="0" class="display" id="sampleOverviewRelationTable"></table>');
  jQuery('#sampleOverviewRelationTable').dataTable({
                                                     "aaData": array,
                                                     "aoColumns": [
                                                       { "sTitle": "Sample Type"},
                                                       { "sTitle": "Created"},
                                                       { "sTitle": "Received"},
                                                       { "sTitle": "QC Passed"},
                                                       { "sTitle": "QC Failed"}
                                                     ],
                                                     "bJQueryUI": true,
                                                     "bSort": false
                                                   });
}

function createSampleOverviewTable(array) {
  jQuery('#sampleOverviewCell').append('<br/><br/><table cellpadding="0" cellspacing="0" border="0" class="display" id="sampleOverviewTable"></table>');
  jQuery('#sampleOverviewTable').dataTable({
                                             "aaData": array,
                                             "aoColumns": [
                                               { "sTitle": "Status"},
                                               { "sTitle": "Amount"}
                                             ],
                                             "bJQueryUI": true
                                           });
}

function createSampleResultTable(array) {
  jQuery('#samplesFlexReport').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="samplesFlexReportTable"></table>');
  jQuery('#samplesFlexReportTable').dataTable({
                                                "aaData": array,
                                                "aoColumns": [
                                                  { "sTitle": "Sample Name"},
                                                  { "sTitle": "Alias"},
                                                  { "sTitle": "Description"},
                                                  { "sTitle": "Type"},
                                                  { "sTitle": "QC Passed"}
                                                ],
                                                "bJQueryUI": true
                                              });
}

function generateLibrariesFlexReport() {
  disableButton('generateLibrariesFlexReportButton');
  jQuery('#generateLibrariesFlexReportFormHeader').html('<img src=\"/styles/images/ajax-loader.gif\"/>');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'generateLibrariesFlexReport',
          {'form':jQuery('#generateLibrariesFlexReportForm').serializeArray(), 'url':ajaxurl},
          {'doOnSuccess':function(json) {
            reenableButton('generateLibrariesFlexReportButton', 'Generate Report');

            jQuery('#generateLibrariesFlexReportButton').fadeOut();
            jQuery('#generateLibrariesFlexReportForm').fadeOut();

            if (jQuery('#libraryPlatform').val() == 'all' || json.graph.length > 1) {
              plotHollowPieChart(json.graph, "libraryStatusChart");
            }
            if (jQuery('#libraryQC').val() == 'all') {
              plotHollowPieChart(json.qcgraph, "libraryStatusChart");
            }
            // plotHollowPieChart(json.typegraph, "libraryStatusChart");
            createLibraryOverviewRelationTable(json.overviewRelationTable);
//            createLibraryOverviewTable(json.overviewTable);
            createLibraryResultTable(json.reportTable);
          }
          }
  );
}

function createLibraryOverviewRelationTable(array) {
  jQuery('#libraryOverviewCell').append('<table cellpadding="0" cellspacing="0" border="0" class="display" id="libraryOverviewRelationTable"></table>');
  jQuery('#libraryOverviewRelationTable').dataTable({
                                                      "aaData": array,
                                                      "aoColumns": [
                                                        { "sTitle": "Library Type"},
                                                        { "sTitle": "Platform"},
                                                        { "sTitle": "QC Passed"},
                                                        { "sTitle": "QC Failed"},
                                                        { "sTitle": "Total"}
                                                      ],
                                                      "bJQueryUI": true,
                                                      "bSort": false
                                                    });
}

function createLibraryOverviewTable(array) {
  jQuery('#libraryOverviewCell').append('<br/><br/><table cellpadding="0" cellspacing="0" border="0" class="display" id="libraryOverviewTable"></table>');
  jQuery('#libraryOverviewTable').dataTable({
                                              "aaData": array,
                                              "aoColumns": [
                                                { "sTitle": "Status"},
                                                { "sTitle": "Amount"}
                                              ],
                                              "bJQueryUI": true
                                            });
}

function createLibraryResultTable(array) {
  jQuery('#librariesFlexReport').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="librariesFlexReportTable"></table>');
  jQuery('#librariesFlexReportTable').dataTable({
                                                  "aaData": array,
                                                  "aoColumns": [
                                                    { "sTitle": "Library Name"},
                                                    { "sTitle": "Alias"},
                                                    { "sTitle": "Description"},
                                                    { "sTitle": "Platform"},
                                                    { "sTitle": "Library Type"},
                                                    { "sTitle": "QC Passed"}
                                                  ],
                                                  "bJQueryUI": true
                                                });
}

function generateRunsFlexReport() {
  disableButton('generateRunsFlexReportButton');
  jQuery('#generateRunsFlexReportFormHeader').html('<img src=\"/styles/images/ajax-loader.gif\"/>');
  Fluxion.doAjax(
          'flexReportingControllerHelperService',
          'generateRunsFlexReport',
          {'form':jQuery('#generateRunsFlexReportForm').serializeArray(), 'url':ajaxurl},
          {'doOnSuccess':function(json) {
            reenableButton('generateRunsFlexReportButton', 'Generate Report');

            jQuery('#generateRunsFlexReportButton').fadeOut();
            jQuery('#generateRunsFlexReportForm').fadeOut();

            if (jQuery('#runStatus').val() == 'all' || json.graph.length > 1) {
              plotHollowPieChart(json.graph, "runStatusChart");
            }
            if (jQuery('#runPlatform').val() == 'all' || json.platformgraph.length > 1) {
              plotHollowPieChart(json.platformgraph, "runStatusChart");
            }
            createRunOverviewTable(json.overviewTable);
            createRunResultTable(json.reportTable);
          }
          }
  );
}

function createRunOverviewTable(array) {
  jQuery('#runOverviewCell').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="runOverviewTable"></table>');
  jQuery('#runOverviewTable').dataTable({
                                          "aaData": array,
                                          "aoColumns": [
                                            { "sTitle": "Status"},
                                            { "sTitle": "Amount"}
                                          ],
                                          "bJQueryUI": true
                                        });
}

function createRunResultTable(array) {
  jQuery('#runsFlexReport').html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="runsFlexReportTable"></table>');
  jQuery('#runsFlexReportTable').dataTable({
                                             "aaData": array,
                                             "aoColumns": [
                                               { "sTitle": "Run Name"},
                                               { "sTitle": "Alias"},
                                               { "sTitle": "Status"},
                                               { "sTitle": "Type"}
                                             ],
                                             "bJQueryUI": true
                                           });
}

function getSequencersList() {
  Fluxion.doAjax(
          'sequencerReferenceControllerHelperService',
          'listSequencers',
          {'url':ajaxurl},
          {'doOnSuccess':function(json) {
            list = "<select id='sequencers' name='sequencers' onchange='updateCalendar();'> <option value=0> All </option>" + json.sequencers + "</select>";
            jQuery('#sequencerslist').html("<b> Sequencer Machines: </b>" + list);
            generateColors();

            init(jQuery('#datepicking').val(), jQuery('#sequencers').val(), jQuery('#sequencers option:last').val());
            addRestrictedDatePicker("calendarfrom");
            addRestrictedDatePicker("calendarto");

            updateCalendar();
          }
          });
}

function updateCalendar() {

  if (jQuery('#sequencers').val() == 0) {
    jQuery("#listtoggle").fadeIn();
  }
  else {
    jQuery("#listtoggle").fadeOut();
  }

  if (jQuery('#datepicking').val() == "custom") {
    jQuery("#calendarfrom").val("");
    jQuery("#calendarto").val("");
    jQuery("#custom").show();
  }
  else {
    jQuery("#custom").hide();
    drawCalendar(jQuery('#datepicking').val(), jQuery('#sequencers').val(), jQuery('#sequencers option:last').val());
  }

}

//function updateDatePicker() {
//
//    editRestrictedDatePicker("calendarfrom");
//    editRestrictedDatePicker("calendarto");
//    drawCalendar(jQuery('input:radio[name=calendarYear]:checked').val(), jQuery('#sequencers').val(), jQuery('#sequencers option:last').val());
//}

function resetCalendar() {

  jQuery("#calendarfrom").val("");
  jQuery("#calendarto").val("");
  drawCalendar("cyear", jQuery('#sequencers').val(), jQuery('#sequencers option:last').val());
}

function generateColors() {
  var size = jQuery('#sequencers option:last').val();
  var color = "<table>";
  var tempcolour = d3.scale.category20();

  for (var i = 1; i < size; i++) {

    if ((i % 2) > 0) {
      color += "<tr>";
    }
    var label = jQuery("#sequencers option").eq(i).text();
    var temp = parseInt(i) * parseInt(i);
    color += "<td bgcolor=" + tempcolour(temp) + "> <font color='black'> <b> " + label + "</b> </font></td>";
    if ((i % 2) == 0) {
      color += "</tr>";
    }
  }
  color += "</table>";
  jQuery("#legendlist").html(color);
}