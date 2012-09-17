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

var Project = Project || {

};

Project.ui = {
  editProjectTrafficLight : function(projectId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'editProjectTrafficLight',
      {
        'projectId':projectId,
        'url':ajaxurl
      },
      {'doOnSuccess': function(json) {
          jQuery('#pro' + projectId + 'traf').html(json.html);
        }
      }
    );
  },

  listProjectTrafficLight : function() {
    jQuery('.overviewstat').html("<img src='../styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'projectControllerHelperService',
      'listProjectTrafficLight',
      {
        'url':ajaxurl
      },
      {'doOnSuccess': function(json) {
          jQuery.each(json, function(i, val) {
            jQuery('#pro' + i + 'overview').html(val)
          });
        }
      }
    );
  },

  processSampleDeliveryForm : function(projectId) {
    var table = jQuery('#sample_table').dataTable();
    var aReturn = [];
    var aTrs = DatatableUtils.fnGetSelected(table);
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
      {'doOnSuccess':function (json) {
          Utils.page.pageRedirect('/miso/download/project/' + projectId + '/' + json.response);
        }
      }
    );
  },

  deliveryFormUploadSuccess : function() {
    jQuery('#deliveryform_statusdiv').html("Samples imported.");
  },

  uploadSampleDeliveryForm : function(projectId) {
    jQuery('#deliveryformdiv').css("display", "block");
  },

  cancelSampleDeliveryFormUpload : function() {
    jQuery('#deliveryformdiv').css("display", "none");
  },

  addPoolEmPCR : function(tableId) {
    alert("This function is not available at present");
  },

  saveBulkSampleQc : function() {
    var self = this;
    Utils.ui.disableButton('bulkSampleQcButton');
    DatatableUtils.collapseInputs('#sample_table');

    var table = jQuery('#sample_table').dataTable();
    var aReturn = [];

    var aTrs = DatatableUtils.fnGetSelected(table);
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
            'doOnSuccess':self.processBulkSampleQcTable
          }
        );
      }
      else {
        alert("The results field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");
      }
    }
    else {
      alert("You have not selected any QC rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");
    }
  },

  processBulkSampleQcTable : function(json) {
    Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");

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

    Utils.timer.timedFunc(Utils.page.pageReload(), 1000);
  },

  saveBulkEmPcrs : function() {
    var self = this;
    Utils.ui.disableButton('bulkEmPcrButton');
    DatatableUtils.collapseInputs('#librarydils_table');

    var table = jQuery('#librarydils_table').dataTable();
    var aReturn = [];
    var aTrs = DatatableUtils.fnGetSelected(table);
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
            'doOnSuccess':self.processBulkEmPcrTable
          }
        );
      }
      else {
        alert("The results field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkEmPcrButton', "Save EmPCRs");
      }
    }
    else {
      alert("You have not selected any EmPCR rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkEmPcrButton', "Save EmPCRs");
    }
  },

  processBulkEmPcrTable : function(json) {
    Utils.ui.reenableButton('bulkEmPcrButton', "Save EmPCRs");

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

    Utils.timer.timedFunc(Utils.page.pageReload(), 1000);
  },

  saveBulkEmPcrDilutions : function() {
    var self = this;
    Utils.ui.disableButton('bulkEmPcrDilutionButton');
    DatatableUtils.collapseInputs('#empcrs_table');

    var table = jQuery('#empcrs_table').dataTable();
    var aReturn = [];
    var aTrs = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < aTrs.length; i++) {
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
            'doOnSuccess':self.processBulkEmPcrDilutionTable
          });
      }
      else {
        alert("The results field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");
      }
    }
    else {
      alert("You have not selected any EmPCR Dilution rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");
    }
  },

  processBulkEmPcrDilutionTable : function(json) {
    Utils.ui.reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");

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
    Utils.timer.timedFunc(Utils.page.pageReload(), 1000);
  }
};

Project.overview = {
  showProjectOverviewDialog : function(projectId) {
    var self = this;
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
            self.addProjectOverview(projectId, jQuery('#principalInvestigator').val(), jQuery('#numProposedSamples').val());
            jQuery(this).dialog('close');
          },
          "Cancel": function() {
            jQuery(this).dialog('close');
          }
        }
      });
    });
    jQuery('#addProjectOverviewDialog').dialog('open');
  },

  addProjectOverview : function(projectId, pi, nsamples) {
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
        'doOnSuccess':Utils.page.pageReload
      }
    );
  },

  showProjectOverviewNoteDialog : function(overviewId) {
    var self = this;
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
            self.addProjectOverviewNote(overviewId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          },
          "Cancel": function() {
            jQuery(this).dialog('close');
          }
        }
      });
    });
    jQuery('#addProjectOverviewNoteDialog').dialog('open');
  },

  addProjectOverviewNote : function(overviewId, internalOnly, text) {
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
        'doOnSuccess':Utils.page.pageReload
      }
    );
  },

  unlockProjectOverview : function(overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'unlockProjectOverview',
      {
        'overviewId':overviewId,
        'url':ajaxurl
      },
      {
        'doOnSuccess':Utils.page.pageReload
      }
    );
  },

  lockProjectOverview : function(overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'lockProjectOverview',
      {
        'overviewId':overviewId,
        'url':ajaxurl
      },
      {
        'doOnSuccess':Utils.page.pageReload
      }
    );
  }
};

Project.issues = {
  removeIssueBox : function(closespan) {
    if (confirm("Are you sure you want to unlink this issue from this project?")) {
      var boxId = jQuery(closespan).parent().attr("id");
      jQuery('#' + boxId).remove();
    }
  },

  importProjectFromIssue : function() {
    var self = this;
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
          'doOnSuccess':self.importIssue
        }
      );
    }
    else {
      alert("Please enter a valid Issue Key, e.g. FOO-1");
    }
  },

  importIssue : function(json) {
    if (json.invalidIssues === "undefined" || json.invalidIssues.length == 0
          && json.validIssues !== "undefined" && json.validIssues.length > 0) {
      var key = json.validIssues[0].key;
      var issue = json.validIssues[0].fields;
      var issueurl = json.validIssues[0].url;
      jQuery('#alias').val(issue.summary.value);
      jQuery('#description').val(issue.description.value);

      jQuery('#issues').append("<div id='importbox" + 0 + "' class='simplebox backwhite'>");
      jQuery('#importbox' + 0).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='Project.issues.removeIssueBox(this);'>Unlink</button>");
      jQuery('#importbox' + 0).append("<h2 onclick=\"Utils.page.newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
      jQuery('#importbox' + 0).append("<b>Summary:</b> " + issue.summary.value + "<br/>");
      jQuery('#importbox' + 0).append("<b>Description:</b> " + issue.description.value + "<br/>");
      jQuery('#importbox' + 0).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.reporter.value.url + "');\">" + issue.reporter.value.displayName + "</a><br/>");
      jQuery('#importbox' + 0).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.assignee.value.url + "');\">" + issue.assignee.value.displayName + "</a><br/>");
      jQuery('#importbox' + 0).append("<b>Created:</b> " + issue.created.value + "<br/>");
      jQuery('#importbox' + 0).append("<b>Updated:</b> " + issue.updated.value + "<br/>");
      jQuery('#importbox' + 0).append("<input type='hidden' value='on' name='_issueKeys'/>");
      jQuery('#importbox' + 0).append("<input type='hidden' name='issueKeys' id='issueKeys0' value='" + key + "'><hr/>");
      jQuery('#issues').append("</div>");
    }
  },

  previewIssueKeys : function() {
    var self = this;
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
          'doOnSuccess':self.previewIssues
        }
      );
    }
    else {
      alert("Please enter a valid Issue Key, or list of keys, e.g. FOO-1,FOO-2,FOO-3");
    }
  },

  previewIssues : function(json) {
    if (json.validIssues !== "undefined" && json.validIssues.length > 0) {
      for (var i = 0; i < json.validIssues.length; i++) {
        var key = json.validIssues[i].key;
        var issueurl = json.validIssues[i].url;
        var issue = json.validIssues[i].fields;
        jQuery('#issues').append("<div id='previewbox" + i + "' class='simplebox backwhite'>");
        jQuery('#previewbox' + i).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='Project.issues.removeIssueBox(this);'>Unlink</button>");
        jQuery('#previewbox' + i).append("<h2 onclick=\"Utils.page.newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
        jQuery('#previewbox' + i).append("<b>Summary:</b> " + issue.summary.value + "<br/>");
        jQuery('#previewbox' + i).append("<b>Description:</b> " + issue.description.value + "<br/>");
        jQuery('#previewbox' + i).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.reporter.value.url + "');\">" + issue.reporter.value.displayName + "</a><br/>");
        jQuery('#previewbox' + i).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.assignee.value.url + "');\">" + issue.assignee.value.displayName + "</a><br/>");
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
  },

  getProjectIssues : function(projectId) {
    var self = this;
    Fluxion.doAjax(
      'projectControllerHelperService',
      'getIssues',
      {
        'projectId':projectId,
        'url':ajaxurl
      },
      {
        'doOnSuccess':self.processIssues
      }
    );
  },

  processIssues : function(json) {
    if (json.issues !== "undefined" && json.issues.length > 0) {
      jQuery('#issues').html("");
      for (var i = 0; i < json.issues.length; i++) {
        var key = json.issues[i].key;
        var issueurl = json.issues[i].url;
        var issue = json.issues[i].fields;
        jQuery('#issues').append("<div id='issuebox" + i + "' class='simplebox backwhite'>");
        jQuery('#issuebox' + i).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='Project.issues.removeIssueBox(this);'>Remove</button>");
        jQuery('#issuebox' + i).append("<h2 onclick=\"Utils.page.newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
        jQuery('#issuebox' + i).append("<b>Summary:</b> " + issue.summary.value + "<br/>");
        jQuery('#issuebox' + i).append("<b>Description:</b> " + issue.description.value + "<br/>");
        jQuery('#issuebox' + i).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.reporter.value.url + "');\">" + issue.reporter.value.displayName + "</a><br/>");
        jQuery('#issuebox' + i).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.assignee.value.url + "');\">" + issue.assignee.value.displayName + "</a><br/>");
        jQuery('#issuebox' + i).append("<b>Created:</b> " + issue.created.value + "<br/>");
        jQuery('#issuebox' + i).append("<b>Updated:</b> " + issue.updated.value + "<br/>");

        if (issue["links"].value.length > 0) {
          jQuery('#issuebox' + i).append("<h4>Links</h4>");
          for (var j = 0; j < issue["links"].value.length; j++) {
            var link = issue["links"].value[j];
            jQuery('#issuebox' + i).append(link.type.description + " <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + link.url + "');\">" + link.issueKey + "</a><br/>");
          }
        }

        if (issue["sub-tasks"].value.length > 0) {
          jQuery('#issuebox' + i).append("<h4>Subtasks</h4>");
          for (var j = 0; j < issue["sub-tasks"].value.length; j++) {
            var subtask = issue["sub-tasks"].value[j];
            jQuery('#issuebox' + i).append("<a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + subtask.url + "');\">" + subtask.url + "</a><br/>");
          }
        }

        jQuery('#issuebox' + i).append("<h4>Comments</h4>");
        for (var k = 0; k < issue["comment"].value.length; k++) {
          var comment = issue["comment"].value[k];
          jQuery('#issuebox' + i).append("<div id='commentbox" + i + "_" + k + "' class='simplebox backwhite' onclick=\"Utils.page.newWindow('" + comment.url + "');\">");
          jQuery('#commentbox' + i + "_" + k).append("<a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + comment.author.url + "');\">" + comment.author.displayName + "</a>");
          jQuery('#commentbox' + i + "_" + k).append(" at " + comment.created + "<br/>");
          jQuery('#commentbox' + i + "_" + k).append("<pre class='wrap'>" + comment.body + "</pre>");
        }

        jQuery('#issuebox' + i).append("<input type='hidden' name='issueKeys' id='issueKeys" + i + "' value='" + key + "'" + "><hr/>");
        jQuery('#issues').append("</div>");
      }
    }
    jQuery('#issues').append("<input type='hidden' value='on' name='_issueKeys'/>");
  }
};

Project.barcode = {
  printAllSampleBarcodes : function(projectId) {
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
        }
      );
    }
  },

  printAllLibraryBarcodes : function(projectId) {
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
        }
      );
    }
  },

  printAllLibraryDilutionBarcodes : function(projectId) {
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
        }
      );
    }
  },

  selectSampleBarcodesToPrint : function(tableId) {
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

      jQuery(tableId).find("tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"" + tableId + "\", this);'></span></th>");
      jQuery(tableId).find("tr:gt(0)").prepend("<td class='rowSelect'></td>");

      jQuery(tableId).find('.rowSelect').click(function() {
        if (jQuery(this).parent().hasClass('row_selected')) {
          jQuery(this).parent().removeClass('row_selected');
        }
        else {
          jQuery(this).parent().addClass('row_selected');
        }
      });

      jQuery("div.toolbar").html("<button onclick=\"Project.barcode.printSelectedSampleBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\">Print Selected</button>");
      jQuery("div.toolbar").append("<button onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</button>");
      jQuery("div.toolbar").removeClass("toolbar");
    }
  },

  printSelectedSampleBarcodes : function(tableId) {
    var samples = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
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
                  });
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
  },

  selectLibraryBarcodesToPrint : function(tableId) {
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

      jQuery("div.toolbar").html("<button onclick=\"Project.barcode.printSelectedLibraryBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\">Print Selected</button>");
      jQuery("div.toolbar").append("<button onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</button>");
      jQuery("div.toolbar").removeClass("toolbar");
    }
  },

  printSelectedLibraryBarcodes : function(tableId) {
    var libraries = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
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
  },

  selectLibraryDilutionBarcodesToPrint : function(tableId) {
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

      jQuery("div.toolbar").html("<button onclick=\"Project.barcode.printSelectedLibraryDilutionBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\">Print Selected</button>");
      jQuery("div.toolbar").append("<button onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</button>");
      jQuery("div.toolbar").removeClass("toolbar");
    }
  },

  printSelectedLibraryDilutionBarcodes : function(tableId) {
    var dilutions = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
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
  }
};

Project.alert = {
  watchOverview : function(overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'watchOverview',
      {
        'overviewId':overviewId,
        'url':ajaxurl
      },
      {'doOnSuccess':function (json) {
          Utils.page.pageReload();
        }
      }
    );
  },

  unwatchOverview : function(overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'unwatchOverview',
      {
        'overviewId':overviewId,
        'url':ajaxurl
      },
      {'doOnSuccess':function (json) {
          Utils.page.pageReload();
        }
      }
    );
  }
};