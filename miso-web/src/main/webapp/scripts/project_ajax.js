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

function showProjectOverviewDialog(projectId) {
  jQuery('#addProjectOverviewDialog')
          .html("<form>" +
                "<fieldset class='dialog'><label for='principalInvestigator'>Principal Investigator</label>" +
                "<input type='text' name='principalInvestigator' id='principalInvestigator' class='text ui-widget-content ui-corner-all' />" +
                "<label for='numProposedSamples'>No. Proposed Samples</label>" +
                "<input type='text' name='numProposedSamples' id='numProposedSamples' class='text ui-widget-content ui-corner-all' />" +
                "</fieldset></form>");

  jQuery(function() {
    jQuery('#addProjectOverviewDialog').dialog({
                                                 autoOpen: false,
                                                 width: 400,
                                                 modal: true,
                                                 resizable: false,
                                                 buttons: {
                                                   "Add Overview": function() {
                                                     addProjectOverview(projectId, jQuery('#principalInvestigator').val(), jQuery('#numProposedSamples').val());
                                                     jQuery(this).dialog('close');
                                                   },
                                                   "Cancel": function() {
                                                     jQuery(this).dialog('close');
                                                   }
                                                 }
                                               });
  });
  jQuery('#addProjectOverviewDialog').dialog('open');
}

var addProjectOverview = function(projectId, pi, nsamples) {
  Fluxion.doAjax(
          'projectControllerHelperService',
          'addProjectOverview',
          {
            'projectId':projectId,
            'principalInvestigator':pi,
            'numProposedSamples':nsamples,
            'url':ajaxurl
          },
          {
            'doOnSuccess':pageReload
          }
  );
}

function showProjectOverviewNoteDialog(overviewId) {
  jQuery('#addProjectOverviewNoteDialog')
          .html("<form>" +
                "<fieldset class='dialog'>" +
                "<label for='internalOnly'>Internal Only?</label>" +
                "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
                "<br/>" +
                "<label for='notetext'>Text</label>" +
                "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' />" +
                "</fieldset></form>");

  jQuery(function() {
    jQuery('#addProjectOverviewNoteDialog').dialog({
                                                     autoOpen: false,
                                                     width: 400,
                                                     modal: true,
                                                     resizable: false,
                                                     buttons: {
                                                       "Add Note": function() {
                                                         addProjectOverviewNote(overviewId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
                                                         jQuery(this).dialog('close');
                                                       },
                                                       "Cancel": function() {
                                                         jQuery(this).dialog('close');
                                                       }
                                                     }
                                                   });
  });
  jQuery('#addProjectOverviewNoteDialog').dialog('open');
}

var addProjectOverviewNote = function(overviewId, internalOnly, text) {
  Fluxion.doAjax(
          'projectControllerHelperService',
          'addProjectOverviewNote',
          {
            'overviewId':overviewId,
            'internalOnly':internalOnly,
            'text':text,
            'url':ajaxurl
          },
          {
            'doOnSuccess':pageReload
          }
  );
};

function unlockProjectOverview(overviewId) {
  Fluxion.doAjax(
          'projectControllerHelperService',
          'unlockProjectOverview',
          {
            'overviewId':overviewId,
            'url':ajaxurl
          },
          {
            'doOnSuccess':pageReload
          }
  );
}

function lockProjectOverview(overviewId) {
  Fluxion.doAjax(
          'projectControllerHelperService',
          'lockProjectOverview',
          {
            'overviewId':overviewId,
            'url':ajaxurl
          },
          {
            'doOnSuccess':pageReload
          }
  );
}

function removeIssueBox(closespan) {
  if (confirm("Are you sure you want to unlink this issue from this project?")) {
    var boxId = jQuery(closespan).parent().attr("id");
    jQuery('#' + boxId).remove();
  }
}

function importProjectFromIssue() {
  var issue = jQuery('#previewKey').val();
  if (issue !== "undefined" && issue !== "") {
    Fluxion.doAjax(
            'projectControllerHelperService',
            'previewIssues',
            {
              'issues':[
                {
                  "key":issue
                }
              ],
              'url':ajaxurl
            },
            {
              'doOnSuccess':importIssue
            }
    );
  }
  else {
    alert("Please enter a valid Issue Key, e.g. FOO-1");
  }
}

var importIssue = function(json) {
  if (json.invalidIssues === "undefined" || json.invalidIssues.length == 0
                                                    && json.validIssues !== "undefined" && json.validIssues.length > 0) {
    var key = json.validIssues[0].key;
    var issue = json.validIssues[0].fields;
    var issueurl = json.validIssues[0].url;
    jQuery('#alias').val(issue.summary.value);
    jQuery('#description').val(issue.description.value);

    jQuery('#issues').append("<div id='importbox" + 0 + "' class='simplebox backwhite'>");
    jQuery('#importbox' + 0).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='removeIssueBox(this);'>Unlink</button>");
    jQuery('#importbox' + 0).append("<h2 onclick=\"newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
    jQuery('#importbox' + 0).append("<b>Summary:</b> " + issue.summary.value + "<br/>");
    jQuery('#importbox' + 0).append("<b>Description:</b> " + issue.description.value + "<br/>");
    jQuery('#importbox' + 0).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"newWindow('" + issue.reporter.value.url + "');\">" + issue.reporter.value.displayName + "</a><br/>");
    jQuery('#importbox' + 0).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"newWindow('" + issue.assignee.value.url + "');\">" + issue.assignee.value.displayName + "</a><br/>");
    jQuery('#importbox' + 0).append("<b>Created:</b> " + issue.created.value + "<br/>");
    jQuery('#importbox' + 0).append("<b>Updated:</b> " + issue.updated.value + "<br/>");
    jQuery('#importbox' + 0).append("<input type='hidden' value='on' name='_issueKeys'/>");
    jQuery('#importbox' + 0).append("<input type='hidden' name='issueKeys' id='issueKeys0' value='" + key + "'><hr/>");
    jQuery('#issues').append("</div>");
  }
};

function previewIssueKeys() {
  var inKeys = jQuery('#previewKeys').val();
  if (inKeys !== "undefined" && inKeys !== "") {
    var issueKeys = inKeys.replace(/[\s]*/, "").split(",");
    var issues = [];
    for (var i = 0; i < issueKeys.length; i++) {
      issues[i] = {"key":issueKeys[i]};
    }

    Fluxion.doAjax(
            'projectControllerHelperService',
            'previewIssues',
            {
              'issues':issues,
              'url':ajaxurl
            },
            {
              'doOnSuccess':previewIssues
            }
    );
  }
  else {
    alert("Please enter a valid Issue Key, or list of keys, e.g. FOO-1,FOO-2,FOO-3");
  }
}

var previewIssues = function(json) {
  if (json.validIssues !== "undefined" && json.validIssues.length > 0) {
    for (var i = 0; i < json.validIssues.length; i++) {
      var key = json.validIssues[i].key;
      var issueurl = json.validIssues[i].url;
      var issue = json.validIssues[i].fields;
      jQuery('#issues').append("<div id='previewbox" + i + "' class='simplebox backwhite'>");
      jQuery('#previewbox' + i).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='removeIssueBox(this);'>Unlink</button>");
      jQuery('#previewbox' + i).append("<h2 onclick=\"newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
      jQuery('#previewbox' + i).append("<b>Summary:</b> " + issue.summary.value + "<br/>");
      jQuery('#previewbox' + i).append("<b>Description:</b> " + issue.description.value + "<br/>");
      jQuery('#previewbox' + i).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"newWindow('" + issue.reporter.value.url + "');\">" + issue.reporter.value.displayName + "</a><br/>");
      jQuery('#previewbox' + i).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"newWindow('" + issue.assignee.value.url + "');\">" + issue.assignee.value.displayName + "</a><br/>");
      jQuery('#previewbox' + i).append("<b>Created:</b> " + issue.created.value + "<br/>");
      jQuery('#previewbox' + i).append("<b>Updated:</b> " + issue.updated.value + "<br/>");
      jQuery('#previewbox' + i).append("<input type='hidden' name='issueKeys' id='issueKeys" + i + "' value='" + key + "'><hr/>");
      jQuery('#issues').append("</div>");
    }
  }

  if (json.invalidIssues !== "undefined" && json.invalidIssues.length > 0) {
    for (var i = 0; i < json.invalidIssues.length; i++) {
      //var key = json.invalidIssues[i];

    }
  }

  jQuery('#issues').append("<input type='hidden' value='on' name='_issueKeys'/>");
};

function getProjectIssues(projectId) {
  Fluxion.doAjax(
          'projectControllerHelperService',
          'getIssues',
          {
            'projectId':projectId,
            'url':ajaxurl
          },
          {
            'doOnSuccess':processIssues
          }
  );
}

var processIssues = function(json) {
  if (json.issues !== "undefined" && json.issues.length > 0) {
    jQuery('#issues').html("");
    for (var i = 0; i < json.issues.length; i++) {
      var key = json.issues[i].key;
      var issueurl = json.issues[i].url;
      var issue = json.issues[i].fields;
      jQuery('#issues').append("<div id='issuebox" + i + "' class='simplebox backwhite'>");
      jQuery('#issuebox' + i).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='removeIssueBox(this);'>Remove</button>");
      jQuery('#issuebox' + i).append("<h2 onclick=\"newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
      jQuery('#issuebox' + i).append("<b>Summary:</b> " + issue.summary.value + "<br/>");
      jQuery('#issuebox' + i).append("<b>Description:</b> " + issue.description.value + "<br/>");
      jQuery('#issuebox' + i).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"newWindow('" + issue.reporter.value.url + "');\">" + issue.reporter.value.displayName + "</a><br/>");
      jQuery('#issuebox' + i).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"newWindow('" + issue.assignee.value.url + "');\">" + issue.assignee.value.displayName + "</a><br/>");
      jQuery('#issuebox' + i).append("<b>Created:</b> " + issue.created.value + "<br/>");
      jQuery('#issuebox' + i).append("<b>Updated:</b> " + issue.updated.value + "<br/>");

      if (issue["links"].value.length > 0) {
        jQuery('#issuebox' + i).append("<h4>Links</h4>");
        for (var j = 0; j < issue["links"].value.length; j++) {
          var link = issue["links"].value[j];
          jQuery('#issuebox' + i).append(link.type.description + " <a href='javascript:void(0);' onclick=\"newWindow('" + link.url + "');\">" + link.issueKey + "</a><br/>");
        }
      }

      if (issue["sub-tasks"].value.length > 0) {
        jQuery('#issuebox' + i).append("<h4>Subtasks</h4>");
        for (var j = 0; j < issue["sub-tasks"].value.length; j++) {
          var subtask = issue["sub-tasks"].value[j];
          jQuery('#issuebox' + i).append("<a href='javascript:void(0);' onclick=\"newWindow('" + subtask.url + "');\">" + subtask.url + "</a><br/>");
        }
      }

      jQuery('#issuebox' + i).append("<h4>Comments</h4>");
      for (var k = 0; k < issue["comment"].value.length; k++) {
        var comment = issue["comment"].value[k];
        jQuery('#issuebox' + i).append("<div id='commentbox" + i + "_" + k + "' class='simplebox backwhite' onclick=\"newWindow('" + comment.url + "');\">");
        jQuery('#commentbox' + i + "_" + k).append("<a href='javascript:void(0);' onclick=\"newWindow('" + comment.author.url + "');\">" + comment.author.displayName + "</a>");
        jQuery('#commentbox' + i + "_" + k).append(" at " + comment.created + "<br/>");
        jQuery('#commentbox' + i + "_" + k).append("<pre class='wrap'>" + comment.body + "</pre>");
      }

      jQuery('#issuebox' + i).append("<input type='hidden' name='issueKeys' id='issueKeys" + i + "' value='" + key + "'" + "><hr/>");
      jQuery('#issues').append("</div>");
    }
  }
  jQuery('#issues').append("<input type='hidden' value='on' name='_issueKeys'/>");
};

function saveBulkSampleQc() {
  disableButton('bulkSampleQcButton');
  //jQuery('#bulkSampleQcButton').attr('disabled', 'disabled');
  //jQuery('#bulkSampleQcButton').html("Processing...");

  collapseInputs('#sample_table');

  var table = jQuery('#sample_table').dataTable();
  var aReturn = [];

  var aTrs = fnGetSelected(table);
  for (var i = 0; i < aTrs.length; i++) {
    var obj = {};
    jQuery(aTrs[i]).find("td:gt(0)").each(function() {
      var at = jQuery(this).attr("name");
      obj[at] = jQuery(this).text();
    });
    obj["qcCreator"] = jQuery('#currentUser').text();
    obj["sampleId"] = obj["name"].substring(3);
    aReturn.push(obj);
  }

  if (aReturn.length > 0) {
    if (validate_sample_qcs(aReturn)) {
      Fluxion.doAjax(
              'sampleControllerHelperService',
              'bulkAddSampleQCs',
              {
                'qcs':aReturn,
                'url':ajaxurl
              },
              {
                'doOnSuccess':processBulkSampleQcTable
              }
      );
    }
    else {
      alert("The results field can only contain integers or decimals.");
      reenableButton('bulkSampleQcButton', "Save QCs");
      //jQuery('#bulkSampleQcButton').removeAttr('disabled');
      //jQuery('#bulkSampleQcButton').html("Save QCs");
    }
  }
  else {
    alert("You have not selected any QC rows to save!\nPlease click the Select column cells in the rows you wish to save.");
    reenableButton('bulkSampleQcButton', "Save QCs");
    //jQuery('#bulkSampleQcButton').removeAttr('disabled');
    //jQuery('#bulkSampleQcButton').html("Save QCs");
  }
}

var processBulkSampleQcTable = function(json) {
  reenableButton('bulkSampleQcButton', "Save QCs");
  //jQuery('#bulkSampleQcButton').removeAttr('disabled');
  //jQuery('#bulkSampleQcButton').html("Save QCs");

  var a = json.saved;
  for (var i = 0; i < a.length; i++) {
    jQuery('#sample_table').find("tr:gt(0)").each(function() {
      if (jQuery(this).attr("sampleId") === a[i].sampleId) {
        jQuery(this).find("td").each(function() {
          jQuery(this).css('background', '#CCFF99');
        });
      }
    });
  }

  //reload page after a second
  timedFunc(pageReload(), 1000);
};

function saveBulkEmPcrs() {
  disableButton('bulkEmPcrButton');
  //jQuery('#bulkEmPcrButton').attr('disabled', 'disabled');
  //jQuery('#bulkEmPcrButton').html("Processing...");

  collapseInputs('#librarydils_table');

  var table = jQuery('#librarydils_table').dataTable();
  var aReturn = [];
  var aTrs = fnGetSelected(table);
  for (var i = 0; i < aTrs.length; i++) {
    var obj = {};
    jQuery(aTrs[i]).find("td:gt(0)").each(function() {
      var at = jQuery(this).attr("name");
      obj[at] = jQuery(this).text();
    });
    obj["pcrCreator"] = jQuery('#currentUser').text();
    obj["dilutionId"] = obj["dilName"].substring(3);
    aReturn.push(obj);
  }
  if (aReturn.length > 0) {
    if (validate_empcrs(aReturn)) {
      Fluxion.doAjax(
              'libraryControllerHelperService',
              'bulkAddEmPcrs',
              {
                'pcrs':aReturn,
                'url':ajaxurl
              },
              {
                'doOnSuccess':processBulkEmPcrTable
              }
      );
    }
    else {
      alert("The results field can only contain integers or decimals.");
      reenableButton('bulkEmPcrButton', "Save EmPCRs");
      //jQuery('#bulkEmPcrButton').removeAttr('disabled');
      //jQuery('#bulkEmPcrButton').html("Save EmPCRs");
    }
  }
  else {
    alert("You have not selected any EmPCR rows to save!\nPlease click the Select column cells in the rows you wish to save.");
    reenableButton('bulkEmPcrButton', "Save EmPCRs");
    //jQuery('#bulkEmPcrButton').removeAttr('disabled');
    //jQuery('#bulkEmPcrButton').html("Save EmPCRs");
  }
}
var processBulkEmPcrTable = function(json) {
  reenableButton('bulkEmPcrButton', "Save EmPCRs");
  //jQuery('#bulkEmPcrButton').removeAttr('disabled');
  //jQuery('#bulkEmPcrButton').html("Save EmPCRs");

  var a = json.saved;
  for (var i = 0; i < a.length; i++) {
    jQuery('#librarydils_table').find("tr:gt(0)").each(function() {
      if (jQuery(this).attr("dilutionId") === a[i].dilutionId) {
        jQuery(this).find("td").each(function() {
          jQuery(this).css('background', '#CCFF99');
        });
      }
    });
  }

  //reload page after a second
  timedFunc(pageReload(), 1000);
};

function saveBulkEmPcrDilutions() {
  disableButton('bulkEmPcrDilutionButton');
  //jQuery('#bulkEmPcrDilutionButton').attr('disabled', 'disabled');
  //jQuery('#bulkEmPcrDilutionButton').html("Processing...");

  collapseInputs('#empcrs_table');

  var table = jQuery('#empcrs_table').dataTable();
  var aReturn = [];
  var aTrs = fnGetSelected(table);
  for (var i = 0; i < aTrs.length; i++) {
    //if (jQuery(aTrs[i]).hasClass('row_selected')) {
    var obj = {};
    jQuery(aTrs[i]).find("td:gt(0)").each(function() {
      var at = jQuery(this).attr("name");
      obj[at] = jQuery(this).text();
    });
    obj["pcrDilutionCreator"] = jQuery('#currentUser').text();
    obj["pcrId"] = obj["pcrName"].substring(3);
    aReturn.push(obj);
  }

  if (aReturn.length > 0) {
    if (validate_empcr_dilutions(aReturn)) {
      Fluxion.doAjax(
              'libraryControllerHelperService',
              'bulkAddEmPcrDilutions',
              {
                'dilutions':aReturn,
                'url':ajaxurl
              },
              {
                'doOnSuccess':processBulkEmPcrDilutionTable
              });
    }
    else {
      alert("The results field can only contain integers or decimals.");
      reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");
      //jQuery('#bulkEmPcrButton').removeAttr('disabled');
      //jQuery('#bulkEmPcrDilutionButton').html("Save Dilutions");
    }
  }
  else {
    alert("You have not selected any EmPCR Dilution rows to save!\nPlease click the Select column cells in the rows you wish to save.");
    reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");
    //jQuery('#bulkEmPcrDilutionButton').removeAttr('disabled');
    //jQuery('#bulkEmPcrDilutionButton').html("Save Dilutions");
  }
}

var processBulkEmPcrDilutionTable = function(json) {
  reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");
  //jQuery('#bulkEmPcrDilutionButton').removeAttr('disabled');
  //jQuery('#bulkEmPcrDilutionButton').html("Save Dilutions");

  var a = json.saved;
  for (var i = 0; i < a.length; i++) {
    jQuery('#empcrs_table').find("tr:gt(0)").each(function() {
      if (jQuery(this).attr("pcrId") === a[i].pcrId) {
        jQuery(this).find("td").each(function() {
          jQuery(this).css('background', '#CCFF99');
        });
      }
    });
  }

  //reload page after a second
  timedFunc(pageReload(), 1000);
};

function editProjectTrafficLight(projectId) {
  Fluxion.doAjax(
          'projectControllerHelperService',
          'editProjectTrafficLight',
          {
            'projectId':projectId,
            'url':ajaxurl
          },
          {
            'doOnSuccess': function(json) {
              jQuery('#pro' + projectId + 'traf').html(json.html);
            }
          });
}

//function editProjectTrafficLightResult(projectId) {
//
//  Fluxion.doAjax(
//          'projectControllerHelperService',
//          'editProjectTrafficLightResult',
//  {
//    'projectId':projectId,
//    'url':ajaxurl
//  },
//  {
//    'doOnSuccess':
//            function(json) {
//              //jQuery('#trafresult').html(json.html);
//              //editProjectGraphData = [json.totalSamples, json.passSamples, json.totalLibs,
//              //  json.passLibs,json.totalRuns,json.completedRuns];
//
//              //               projectTrafficlightBullets = [
//              //                {
//              //                  title: "Samples",
//              //                  subtitle: "No. Passed QC",
//              //                 // ranges: [150, 225, 300],
//              //                  measures: [json.passSamples],
//              //                  markers: [json.totalSamples]
//              //                },
//              //                {
//              //                  title: "Libraries",
//              //                  subtitle: "No. Passed QC",
//              //                //  ranges: [20, 25, 30],
//              //                  measures: [json.passLibs],
//              //                  markers: [json.totalLibs]
//              //                },
//              //                {
//              //                  title: "Runs",
//              //                  subtitle: "No. of Completed",
//              //                //  ranges: [350, 500, 600],
//              //                  measures: [json.completedRuns],
//              //                  markers: [json.totalRuns]
//              //                }
//              //              ];
//              projectTree = json;
//            }
//  }
//          );
//}

function listProjectTrafficLight() {
  jQuery('.overviewstat').html("<img src='../styles/images/ajax-loader.gif'/>");
  Fluxion.doAjax(
          'projectControllerHelperService',
          'listProjectTrafficLight',
          {
            'url':ajaxurl
          },
          { 'doOnSuccess': function(json) {
            jQuery.each(json, function(i, val) {
              jQuery('#pro' + i + 'overview').html(val)
            });

          }
          });
}

function printAllSampleBarcodes(projectId) {
  if (confirm("Are you sure you want to print all sample barcodes?")) {
    Fluxion.doAjax(
            'projectControllerHelperService',
            'printAllSampleBarcodes',
            {
              'projectId':projectId,
              'url':ajaxurl
            },
            {
              'doOnSuccess':function (json) {
                alert(json.response);
              }
            });
  }
}

function printAllLibraryBarcodes(projectId) {
  if (confirm("Are you sure you want to print all library barcodes?")) {
    Fluxion.doAjax(
            'projectControllerHelperService',
            'printAllLibraryBarcodes',
            {
              'projectId':projectId,
              'url':ajaxurl
            },
            {
              'doOnSuccess':function (json) {
                alert(json.response);
              }
            });
  }
}

function printAllLibraryDilutionBarcodes(projectId) {
  if (confirm("Are you sure you want to print all library dilution barcodes?")) {
    Fluxion.doAjax(
            'projectControllerHelperService',
            'printAllLibraryDilutionBarcodes',
            {
              'projectId':projectId,
              'url':ajaxurl
            },
            {
              'doOnSuccess':function (json) {
                alert(json.response);
              }
            });
  }
}

function processSampleDeliveryForm(projectId) {
  var table = jQuery('#sample_table').dataTable();
  var aReturn = [];
  var aTrs = fnGetSelected(table);
  for (var i = 0; i < aTrs.length; i++) {
    var obj = {};
    obj.sampleId = jQuery(aTrs[i]).attr("sampleId");
    aReturn.push(obj);
  }

  Fluxion.doAjax(
          'projectControllerHelperService',
          'generateSampleDeliveryForm',
          {
            'projectId':projectId,
            'samples':aReturn,
            'url':ajaxurl
          },
          {
            'doOnSuccess':function (json) {
              pageRedirect('/miso/download/project/' + projectId + '/' + json.response);
            }
          });
}

var deliveryFormUploadSuccess = function() {
  jQuery('#deliveryform_statusdiv').html("Samples imported.");
};

function uploadSampleDeliveryForm(projectId) {
  jQuery('#deliveryformdiv').css("display", "block");
}

function cancelSampleDeliveryFormUpload() {
  jQuery('#deliveryformdiv').css("display", "none");
}

function watchOverview(overviewId) {
  Fluxion.doAjax(
          'projectControllerHelperService',
          'watchOverview',
          {
            'overviewId':overviewId,
            'url':ajaxurl
          },
          {
            'doOnSuccess':function (json) {
              pageReload();
            }
          });
}

function unwatchOverview(overviewId) {
  Fluxion.doAjax(
          'projectControllerHelperService',
          'unwatchOverview',
          {
            'overviewId':overviewId,
            'url':ajaxurl
          },
          {
            'doOnSuccess':function (json) {
              pageReload();
            }
          });
}

function selectSampleBarcodesToPrint(tableId) {
  if (!jQuery(tableId).hasClass("display")) {
    jQuery(tableId).addClass("display");

    jQuery(tableId).find('tr:first th:eq(5)').remove();
    jQuery(tableId).find("tr").each(function() {
      jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
      jQuery(this).find("td:eq(5)").remove();
    });
    //Sample Name 	Sample Alias 	Sample Description 	Type 	QC Passed
    var headers = ['rowsel',
                   'name',
                   'alias',
                   'description',
                   'type',
                   'qcPassed'];

    var oTable = jQuery(tableId).dataTable({
                                             "aoColumnDefs": [
                                               {
                                                 "bUseRendered": false,
                                                 "aTargets": [ 0 ]
                                               }
                                             ],
                                             "bPaginate": false,
                                             "bInfo": false,
                                             "bJQueryUI": true,
                                             "bAutoWidth": true,
                                             "bSort": false,
                                             "bFilter": false,
                                             "sDom": '<<"toolbar">f>r<t>ip>'
                                           });

    jQuery(tableId).find("tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='toggleSelectAll(\"" + tableId + "\", this);'></span></th>");
    jQuery(tableId).find("tr:gt(0)").prepend("<td class='rowSelect'></td>");

    jQuery(tableId).find('.rowSelect').click(function() {
      if (jQuery(this).parent().hasClass('row_selected')) {
        jQuery(this).parent().removeClass('row_selected');
      }
      else {
        jQuery(this).parent().addClass('row_selected');
      }
    });

    jQuery("div.toolbar").html("<button onclick=\"printSelectedSampleBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\">Print Selected</button>");
    jQuery("div.toolbar").append("<button onclick=\"pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</button>");
    jQuery("div.toolbar").removeClass("toolbar");
  }
}

function printSelectedSampleBarcodes(tableId) {
  var samples = [];
  var table = jQuery(tableId).dataTable();
  var nodes = fnGetSelected(table);
  for (var i = 0; i < nodes.length; i++) {
    samples[i] = {'sampleId':jQuery(nodes[i]).attr("sampleId")};
  }

  Fluxion.doAjax(
          'printerControllerHelperService',
          'listAvailableServices',
          {
            'serviceClass':'uk.ac.bbsrc.tgac.miso.core.data.Sample',
            'url':ajaxurl
          },
          {
            'doOnSuccess':function (json) {
              jQuery('#printServiceSelectDialog')
                      .html("<form>" +
                            "<fieldset class='dialog'>" +
                            "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                            json.services +
                            "</select></fieldset></form>");

              jQuery(function() {
                jQuery('#printServiceSelectDialog').dialog({
                                                             autoOpen: false,
                                                             width: 400,
                                                             modal: true,
                                                             resizable: false,
                                                             buttons: {
                                                               "Print": function() {
                                                                 Fluxion.doAjax(
                                                                         'projectControllerHelperService',
                                                                         'printSelectedSampleBarcodes',
                                                                         {
                                                                           'serviceName':jQuery('#serviceSelect').val(),
                                                                           'samples':samples,
                                                                           'url':ajaxurl
                                                                         },
                                                                         {
                                                                           'doOnSuccess':function (json) {
                                                                             alert(json.response);
                                                                           }
                                                                         }
                                                                 );
                                                                 jQuery(this).dialog('close');
                                                               },
                                                               "Cancel": function() {
                                                                 jQuery(this).dialog('close');
                                                               }
                                                             }
                                                           });
              });
              jQuery('#printServiceSelectDialog').dialog('open');
            },
            'doOnError':function (json) {
              alert(json.error);
            }
          }
  );

  /*
   Fluxion.doAjax(
   'projectControllerHelperService',
   'printSelectedSampleBarcodes',
   {
   'samples':samples,
   'url':ajaxurl
   },
   {
   'doOnSuccess':function (json) { alert(json.response); }
   }
   );
   */
}

function selectLibraryBarcodesToPrint(tableId) {
  if (!jQuery(tableId).hasClass("display")) {
    jQuery(tableId).addClass("display");

    jQuery(tableId).find('tr:first th:eq(7)').remove();
    jQuery(tableId).find("tr").each(function() {
      jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
      jQuery(this).find("td:eq(7)").remove();
    });
    //Library Name 	Library Alias 	Library Description 	Library Type 	Library Platform 	Tag Barcode 	Insert Size
    var headers = ['rowsel',
                   'name',
                   'alias',
                   'description',
                   'libraryType',
                   'platform',
                   'tagBarcode',
                   'insertSize'];

    var oTable = jQuery(tableId).dataTable({
                                             "aoColumnDefs": [
                                               {
                                                 "bUseRendered": false,
                                                 "aTargets": [ 0 ]
                                               }
                                             ],
                                             "bPaginate": false,
                                             "bInfo": false,
                                             "bJQueryUI": true,
                                             "bAutoWidth": true,
                                             "bSort": false,
                                             "bFilter": false,
                                             "sDom": '<<"toolbar">f>r<t>ip>'
                                           });

    jQuery(tableId).find("tr:first").prepend("<th>Select</th>");
    jQuery(tableId).find("tr:gt(0)").prepend("<td class='rowSelect'></td>");

    jQuery(tableId).find('.rowSelect').click(function() {
      if (jQuery(this).parent().hasClass('row_selected')) {
        jQuery(this).parent().removeClass('row_selected');
      }
      else {
        jQuery(this).parent().addClass('row_selected');
      }
    });

    jQuery("div.toolbar").html("<button onclick=\"printSelectedLibraryBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\">Print Selected</button>");
    jQuery("div.toolbar").append("<button onclick=\"pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</button>");
    jQuery("div.toolbar").removeClass("toolbar");
  }
}

function printSelectedLibraryBarcodes(tableId) {
  var libraries = [];
  var table = jQuery(tableId).dataTable();
  var nodes = fnGetSelected(table);
  for (var i = 0; i < nodes.length; i++) {
    libraries[i] = {'libraryId':jQuery(nodes[i]).attr("libraryId")};
  }

  Fluxion.doAjax(
          'printerControllerHelperService',
          'listAvailableServices',
          {
            'serviceClass':'uk.ac.bbsrc.tgac.miso.core.data.Library',
            'url':ajaxurl
          },
          {
            'doOnSuccess':function (json) {
              jQuery('#printServiceSelectDialog')
                      .html("<form>" +
                            "<fieldset class='dialog'>" +
                            "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                            json.services +
                            "</select></fieldset></form>");

              jQuery(function() {
                jQuery('#printServiceSelectDialog').dialog({
                                                             autoOpen: false,
                                                             width: 400,
                                                             modal: true,
                                                             resizable: false,
                                                             buttons: {
                                                               "Print": function() {
                                                                 Fluxion.doAjax(
                                                                         'projectControllerHelperService',
                                                                         'printSelectedLibraryBarcodes',
                                                                         {
                                                                           'serviceName':jQuery('#serviceSelect').val(),
                                                                           'libraries':libraries,
                                                                           'url':ajaxurl
                                                                         },
                                                                         {
                                                                           'doOnSuccess':function (json) {
                                                                             alert(json.response);
                                                                           }
                                                                         }
                                                                 );
                                                                 jQuery(this).dialog('close');
                                                               },
                                                               "Cancel": function() {
                                                                 jQuery(this).dialog('close');
                                                               }
                                                             }
                                                           });
              });
              jQuery('#printServiceSelectDialog').dialog('open');
            },
            'doOnError':function (json) {
              alert(json.error);
            }
          }
  );

  /*
   Fluxion.doAjax(
   'projectControllerHelperService',
   'printSelectedLibraryBarcodes',
   {
   'libraries':libraries,
   'url':ajaxurl
   },
   {
   'doOnSuccess':function (json) { alert(json.response); }
   }
   );
   */
}

function selectLibraryDilutionBarcodesToPrint(tableId) {
  if (!jQuery(tableId).hasClass("display")) {
    jQuery(tableId).addClass("display");

    jQuery(tableId).find('tr:first th:eq(5)').remove();
    jQuery(tableId).find("tr").each(function() {
      jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
      jQuery(this).find("td:eq(5)").remove();
    });
    //Dilution Name 	Dilution Creator 	Dilution Creation Date 	Dilution Platform 	Dilution Concentration
    var headers = ['rowsel',
                   'name',
                   'creator',
                   'creationDate',
                   'platform',
                   'concentration'];

    var oTable = jQuery(tableId).dataTable({
                                             "aoColumnDefs": [
                                               {
                                                 "bUseRendered": false,
                                                 "aTargets": [ 0 ]
                                               }
                                             ],
                                             "bPaginate": false,
                                             "bInfo": false,
                                             "bJQueryUI": true,
                                             "bAutoWidth": true,
                                             "bSort": false,
                                             "bFilter": false,
                                             "sDom": '<<"toolbar">f>r<t>ip>'
                                           });

    jQuery(tableId).find("tr:first").prepend("<th>Select</th>");
    jQuery(tableId).find("tr:gt(0)").prepend("<td class='rowSelect'></td>");

    jQuery(tableId).find('.rowSelect').click(function() {
      if (jQuery(this).parent().hasClass('row_selected')) {
        jQuery(this).parent().removeClass('row_selected');
      }
      else {
        jQuery(this).parent().addClass('row_selected');
      }
    });

    jQuery("div.toolbar").html("<button onclick=\"printSelectedLibraryDilutionBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\">Print Selected</button>");
    jQuery("div.toolbar").append("<button onclick=\"pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</button>");
    jQuery("div.toolbar").removeClass("toolbar");
  }
}

function printSelectedLibraryDilutionBarcodes(tableId) {
  var dilutions = [];
  var table = jQuery(tableId).dataTable();
  var nodes = fnGetSelected(table);
  for (var i = 0; i < nodes.length; i++) {
    dilutions[i] = {'dilutionId':jQuery(nodes[i]).attr("dilutionId")};
  }

  Fluxion.doAjax(
          'printerControllerHelperService',
          'listAvailableServices',
          {
            'serviceClass':'uk.ac.bbsrc.tgac.miso.core.data.Dilution',
            'url':ajaxurl
          },
          {
            'doOnSuccess':function (json) {
              jQuery('#printServiceSelectDialog')
                      .html("<form>" +
                            "<fieldset class='dialog'>" +
                            "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                            json.services +
                            "</select></fieldset></form>");

              jQuery(function() {
                jQuery('#printServiceSelectDialog').dialog({
                                                             autoOpen: false,
                                                             width: 400,
                                                             modal: true,
                                                             resizable: false,
                                                             buttons: {
                                                               "Print": function() {
                                                                 Fluxion.doAjax(
                                                                         'projectControllerHelperService',
                                                                         'printSelectedLibraryDilutionBarcodes',
                                                                         {
                                                                           'serviceName':jQuery('#serviceSelect').val(),
                                                                           'dilutions':dilutions,
                                                                           'url':ajaxurl
                                                                         },
                                                                         {
                                                                           'doOnSuccess':function (json) {
                                                                             alert(json.response);
                                                                           }
                                                                         }
                                                                 );
                                                                 jQuery(this).dialog('close');
                                                               },
                                                               "Cancel": function() {
                                                                 jQuery(this).dialog('close');
                                                               }
                                                             }
                                                           });
              });
              jQuery('#printServiceSelectDialog').dialog('open');
            },
            'doOnError':function (json) {
              alert(json.error);
            }
          }
  );
  /*
   Fluxion.doAjax(
   'projectControllerHelperService',
   'printSelectedLibraryDilutionBarcodes',
   {
   'dilutions':dilutions,
   'url':ajaxurl
   },
   {
   'doOnSuccess':function (json) { alert(json.response); }
   }
   );
   */
}

function addPoolEmPCR(tableId) {
  alert("This function is not available at present");
}