/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

// these variables are set on the editProject page if the project has samples/libraries
var projectId_sample, sampleQcTypesString, libraryQcTypesString, projectId_d3graph;

//Custom Parsley validator to validate Project shortName server-side
window.Parsley.addValidator('projectShortName', {
  validateString: function(value) {
    var deferred = new jQuery.Deferred();
    Fluxion.doAjax(
      'projectControllerHelperService',
      'validateProjectShortName',
      {
        'shortName': value,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          deferred.resolve();
        },
        'doOnError': function(json) {
          deferred.reject(json.error);
        }
      }
    );
    return deferred.promise();
  },
  messages: {
    en: 'Short name must conform to the naming scheme.'
  }
});

var Project = Project || {
  validateProject: function () {
    Validate.cleanFields('#project-form');
    jQuery('#project-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '100');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Short name input field validation
    jQuery('#shortName').attr('class', 'form-control');
    jQuery('#shortName').attr('data-parsley-maxlength', '5');
    jQuery('#shortName').attr('data-parsley-validate-if-empty', '');
    jQuery('#shortName').attr('data-parsley-project-short-name', '');
    jQuery('#shortName').attr('data-parsley-debounce', '500');

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Radio button validation: ensure a button is selected (assumes there is one progress button, no other way to check because of dynamic
    // generation)
    jQuery('#progress').attr('class', 'form-control');
    jQuery('#progress1').attr('required', 'true');
    jQuery('#progress').attr('data-parsley-error-message', 'You must select a progress status.');
    jQuery('#progress1').attr('data-parsley-errors-container', '#progressSelectError');
    jQuery('#progress').attr('data-parsley-class-handler', '#progressButtons');
    
    if (jQuery('#securityProfile_owner').length > 0) {
      jQuery('#securityProfile_owner').attr('class', 'form-control');
      jQuery('#securityProfile_owner').attr('required', 'true');
    }

    jQuery('#project-form').parsley();
    jQuery('#project-form').parsley().validate();

    Validate.updateWarningOrSubmit('#project-form');
  },
  
  validate_sample_qcs: function (json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if(!json[i].results.match(/[0-9\.]+/)) {
        ok = false;
      }
    }
    return ok;
  },
  
  validate_empcrs: function (json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if(!json[i].results.match(/[0-9\.]+/)) {
        ok = false;
      }
    }
    return ok;
  },
  
  validate_empcr_dilutions: function (json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if(!json[i].results.match(/[0-9\.]+/)) {
        ok = false;
      }
    }
    return ok;
  }
};

Project.ui = {
  editProjectTrafficLight: function (projectId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'editProjectTrafficLight',
      {
        'projectId': projectId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#pro' + projectId + 'traf').html(json.html);
        }
      }
    );
  },

  listProjectTrafficLight: function () {
    jQuery('.overviewstat').html("<img src='../styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'projectControllerHelperService',
      'listProjectTrafficLight',
      {
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery.each(json, function (i, val) {
            jQuery('#pro' + i + 'overview').html(val);
          });
        }
      }
    );
  },

  createListingProjectsTable: function () {
    jQuery('#listingProjectsTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    function projectIdFromColumn(x) {
      return parseInt(x.match(/PRO(\d+)/)[1]);
    }
    jQuery.fn.dataTableExt.oSort['no-pro-asc'] = function (x, y) {
      return projectIdFromColumn(x) - projectIdFromColumn(y);
    };
    jQuery.fn.dataTableExt.oSort['no-pro-desc'] = function (x, y) {
      return projectIdFromColumn(y) - projectIdFromColumn(x);
    };
    Fluxion.doAjax(
      'projectControllerHelperService',
      'listProjectsDataTable',
      {
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#listingProjectsTable').html('');
          jQuery('#listingProjectsTable').dataTable({
            "aaData": json.projectsArray,
            "aoColumns": [
              { "sTitle": "Project Name", "sType": "no-pro"},
              { "sTitle": "Short Name"},
              { "sTitle": "Alias"},
              { "sTitle": "Description"},
              { "sTitle": "Progress"},
              { "sTitle": "Overview"}
            ],
            "bJQueryUI": true,
            "iDisplayLength": 25,
            "aaSorting": [
              [0, "desc"]
            ],
            "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
            "fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
              Fluxion.doAjax(
                'projectControllerHelperService',
                'checkOverviewByProjectId',
                {
                  'projectId': aData[5],
                  'url': ajaxurl
                },
                {
                  'doOnSuccess': function (json) {
                    jQuery('td:eq(5)', nRow).html(json.response);
                  }
                }
              );
            }
          });
          jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
          jQuery("#toolbar").append("<button style=\"margin-left:5px;\" onclick=\"window.location.href='/miso/project/new';\" class=\"fg-button ui-state-default ui-corner-all\">Add Project</button>");
        }
      }
    );
  },

  processSampleDeliveryForm: function (tableName, projectId) {
    var table = jQuery(tableName).dataTable();
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
        'plate':jQuery('input:radio[name=plateinformationform]:checked').val(),
        'projectId': projectId,
        'samples': aReturn,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          Utils.page.pageRedirect('/miso/download/project/' + projectId + '/' + json.response);
        }
      }
    );
  },

  projectFileUploadSuccess: function () {
    jQuery('#statusdiv').html("Upload complete. Refresh to see the file.");
  },
  
  deleteFile: function (projectId, fileKey) {
	    if (confirm("Are you sure you want to delete this file?")) {
	      Fluxion.doAjax(
	        'projectControllerHelperService',
	        'deleteProjectFile',
	        {
	          'id': projectId,
	          'hashcode': fileKey,
	          'url': ajaxurl
	        },
	        {
	          'doOnSuccess': Utils.page.pageReload
	        }
	      );
	    }
	  },

  deliveryFormUploadSuccess: function () {
    jQuery('#deliveryform_statusdiv').html("Samples imported.");
  },

  uploadSampleDeliveryForm: function () {
    jQuery('#deliveryformdiv').css("display", "block");
  },

  cancelSampleDeliveryFormUpload: function () {
    jQuery('#deliveryformdiv').css("display", "none");
  },

  downloadBulkSampleInputForm: function (projectId, documentFormat) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'downloadBulkSampleInputForm',
      {
        'projectId': projectId,
        'documentFormat': documentFormat,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          Utils.page.pageRedirect('/miso/download/project/' + projectId + '/' + json.response);
        }
      }
    );
  },

  bulkSampleInputFormUploadSuccess: function () {
    jQuery('#inputform_statusdiv').html("");
    Fluxion.doAjax(
      'projectControllerHelperService',
      'visualiseBulkSampleInputForm',
      {
        'url': ajaxurl
      },
      {
        'updateElement': 'inputform_statusdiv'
      }
    );
  },

  uploadBulkSampleInputForm: function () {
    jQuery('#inputformdiv').css("display", "block");
  },

  cancelBulkSampleInputFormUpload: function () {
    jQuery('#inputformdiv').css("display", "none");
  },

  addPoolEmPCR: function (tableId) {
    alert("This function is not available at present");
  },

  saveBulkSampleQc: function (tableName) {
    var self = this;
    Utils.ui.disableButton('bulkSampleQcButton');
    DatatableUtils.collapseInputs(tableName);

    var table = jQuery(tableName).dataTable();
    var aReturn = [];

    var aTrs = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < aTrs.length; i++) {
      var obj = {};
      jQuery(aTrs[i]).find("td:gt(0)").each(function () {
        var at = jQuery(this).attr("name");
        obj[at] = jQuery(this).text();
      });
      obj.qcCreator = jQuery('#currentUser').text();
      obj.sampleId = obj.name.substring(3);
      aReturn.push(obj);
    }

    if (aReturn.length > 0) {
      if (Project.validate_sample_qcs(aReturn)) {
        Fluxion.doAjax(
          'sampleControllerHelperService',
          'bulkAddSampleQCs',
          {
            'qcs': aReturn,
            'url': ajaxurl
          },
          {
            'doOnSuccess': function(json) {
              self.processBulkSampleQcTable(tableName, json);
            }
          }
        );
      } else {
        alert("The results field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");
      }
    } else {
      alert("You have not selected any QC rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");
    }
  },

  processBulkSampleQcTable: function (tableName, json) {
    Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");
    var a = json.saved;
    for (var i = 0; i < a.length; i++) {
      jQuery(tableName).find("tr:gt(0)").each(function () {
        if (jQuery(this).attr("sampleId") === a[i].sampleId) {
          jQuery(this).removeClass('row_selected');
          jQuery(this).addClass('row_saved');
          jQuery(this).find("td").each(function () {
            jQuery(this).css('background', '#CCFF99');
            if (jQuery(this).hasClass('rowSelect')) {
              jQuery(this).removeClass('rowSelect');
              jQuery(this).removeAttr('name');
            }
          });
        }
      });
    }

    if (json.errors) {
      var errors = json.errors;
      var errorStr = "";
      for (var j = 0; j < errors.length; j++) {
        errorStr += errors[j].error + "\n";
        jQuery(tableName).find("tr:gt(0)").each(function () {
          if (jQuery(this).attr("sampleId") === errors[j].sampleId) {
            jQuery(this).find("td").each(function () {
              jQuery(this).css('background', '#EE9966');
            });
          }
        });
      }
      alert("There were errors in your bulk input. The green rows have been saved, please fix the red rows:\n\n" + errorStr);
    } else {
      Utils.timer.timedFunc(Utils.page.pageReload(), 1000);
    }
  },

  saveBulkEmPcrs: function () {
    var self = this;
    Utils.ui.disableButton('bulkEmPcrButton');
    DatatableUtils.collapseInputs('#librarydils_table');

    var table = jQuery('#librarydils_table').dataTable();
    var aReturn = [];
    var aTrs = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < aTrs.length; i++) {
      var obj = {};
      jQuery(aTrs[i]).find("td:gt(0)").each(function () {
        var at = jQuery(this).attr("name");
        obj[at] = jQuery(this).text();
      });
      obj.pcrCreator = jQuery('#currentUser').text();
      obj.dilutionId = obj.dilName.substring(3);
      aReturn.push(obj);
    }
    if (aReturn.length > 0) {
      if (Project.validate_empcrs(aReturn)) {
        Fluxion.doAjax(
          'libraryControllerHelperService',
          'bulkAddEmPcrs',
          {
            'pcrs': aReturn,
            'url': ajaxurl
          },
          {
            'doOnSuccess': self.processBulkEmPcrTable
          }
        );
      } else {
        alert("The results field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkEmPcrButton', "Save EmPCRs");
      }
    } else {
      alert("You have not selected any EmPCR rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkEmPcrButton', "Save EmPCRs");
    }
  },

  processBulkEmPcrTable: function (json) {
    Utils.ui.reenableButton('bulkEmPcrButton', "Save EmPCRs");

    var a = json.saved;
    for (var i = 0; i < a.length; i++) {
      jQuery('#librarydils_table').find("tr:gt(0)").each(function () {
        if (jQuery(this).attr("dilutionId") === a[i].dilutionId) {
          jQuery(this).removeClass('row_selected');
          jQuery(this).addClass('row_saved');
          jQuery(this).find("td").each(function () {
            jQuery(this).css('background', '#CCFF99');
            if (jQuery(this).hasClass('rowSelect')) {
              jQuery(this).removeClass('rowSelect');
              jQuery(this).removeAttr('name');
            }
          });
        }
      });
    }

    if (json.errors) {
      var errors = json.errors;
      var errorStr = "";
      for (var j = 0; j < errors.length; j++) {
        errorStr += errors[j].error + "\n";
        jQuery('#librarydils_table').find("tr:gt(0)").each(function () {
          if (jQuery(this).attr("dilutionId") === errors[j].dilutionId) {
            jQuery(this).find("td").each(function () {
              jQuery(this).css('background', '#EE9966');
            });
          }
        });
      }
      alert("There were errors in your bulk input. The green rows have been saved, please fix the red rows:\n\n" + errorStr);
    } else {
      Utils.timer.timedFunc(Utils.page.pageReload(), 1000);
    }
  },

  saveBulkEmPcrDilutions: function () {
    var self = this;
    Utils.ui.disableButton('bulkEmPcrDilutionButton');
    DatatableUtils.collapseInputs('#empcrs_table');

    var table = jQuery('#empcrs_table').dataTable();
    var aReturn = [];
    var aTrs = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < aTrs.length; i++) {
      var obj = {};
      jQuery(aTrs[i]).find("td:gt(0)").each(function () {
        var at = jQuery(this).attr("name");
        obj[at] = jQuery(this).text();
      });
      obj.pcrDilutionCreator= jQuery('#currentUser').text();
      obj.pcrId= obj.pcrName.substring(3);
      aReturn.push(obj);
    }

    if (aReturn.length > 0) {
      if (Project.validate_empcr_dilutions(aReturn)) {
        Fluxion.doAjax(
          'libraryControllerHelperService',
          'bulkAddEmPcrDilutions',
          {
            'dilutions': aReturn,
            'url': ajaxurl
          },
          {
            'doOnSuccess': self.processBulkEmPcrDilutionTable
          }
        );
      } else {
        alert("The results field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");
      }
    } else {
      alert("You have not selected any EmPCR Dilution rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");
    }
  },

  processBulkEmPcrDilutionTable: function (json) {
    Utils.ui.reenableButton('bulkEmPcrDilutionButton', "Save Dilutions");

    var a = json.saved;
    for (var i = 0; i < a.length; i++) {
      jQuery('#empcrs_table').find("tr:gt(0)").each(function () {
        if (jQuery(this).attr("pcrId") === a[i].pcrId) {
          jQuery(this).removeClass('row_selected');
          jQuery(this).addClass('row_saved');
          jQuery(this).find("td").each(function () {
            jQuery(this).css('background', '#CCFF99');
            if (jQuery(this).hasClass('rowSelect')) {
              jQuery(this).removeClass('rowSelect');
              jQuery(this).removeAttr('name');
            }
          });
        }
      });
    }

    if (json.errors) {
      var errors = json.errors;
      var errorStr = "";
      for (var j = 0; j < errors.length; j++) {
        errorStr += errors[j].error + "\n";
        jQuery('#empcrs_table').find("tr:gt(0)").each(function () {
          if (jQuery(this).attr("pcrId") === errors[j].pcrId) {
            jQuery(this).find("td").each(function () {
              jQuery(this).css('background', '#EE9966');
            });
          }
        });
      }
      alert("There were errors in your bulk input. The green rows have been saved, please fix the red rows:\n\n" + errorStr);
    } else {
      Utils.timer.timedFunc(Utils.page.pageReload(), 1000);
    }
  },

  receiveSamples: function (tableId) {
    if (!jQuery(tableId).hasClass("display")) {
      //destroy current table and recreate
      jQuery(tableId).dataTable().fnDestroy();
      //bug fix to reset table width
      jQuery(tableId).removeAttr("style");
      jQuery(tableId).addClass("display");

      jQuery(tableId + ' tbody').find("tr").each(function () {
          // remove received samples
          if (jQuery(this).find("td:eq(4)").html() === "") {
            jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
            jQuery(this).find("td:eq(4)").remove();
          } else {
            jQuery(this).remove();
          }
      });

      jQuery(tableId).find('tr:first th:eq(4)').remove();

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

      jQuery(tableId).find('.rowSelect').click(function () {
        if (jQuery(this).parent().hasClass('row_selected')) {
          jQuery(this).parent().removeClass('row_selected');
        } else {
          jQuery(this).parent().addClass('row_selected');
        }
      });

      jQuery("div.toolbar").html("<input type='button' value='Receive Selected' onclick=\"Project.ui.receiveSelectedSamples('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\"/>");
      jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
      jQuery("div.toolbar").removeClass("toolbar");
    }
  },

  receiveSelectedSamples: function (tableId) {
    if (confirm("Are you sure you want to receive selected samples?")) {
      var samples = [];
      var table = jQuery(tableId).dataTable();
      var nodes = DatatableUtils.fnGetSelected(table);
      for (var i = 0; i < nodes.length; i++) {
        samples[i] = {'sampleId': jQuery(nodes[i]).attr("sampleId")};
      }

      if (samples.length > 0) {
        Fluxion.doAjax(
          'sampleControllerHelperService',
          'setSampleReceivedDateByBarcode',
          {
            'samples': samples,
            'url': ajaxurl
          },
          {
            'doOnSuccess': function (json) {
              alert(json.result);
              Utils.page.pageReload();
            }
          }
        );
      } else {
        alert("No samples selected");
      }
    }
  }
};

Project.overview = {
  showProjectOverviewDialog: function (projectId) {
    var self = this;
    jQuery('#addProjectOverviewDialog')
        .html("<form>" +
              "<fieldset class='dialog'><label for='principalInvestigator'>Principal Investigator</label>" +
              "<input type='text' name='principalInvestigator' id='principalInvestigator' class='text ui-widget-content ui-corner-all' />" +
              "<label for='numProposedSamples'>No. Proposed Samples</label>" +
              "<input type='text' name='numProposedSamples' id='numProposedSamples' class='text ui-widget-content ui-corner-all' />" +
              "</fieldset></form>");

    jQuery('#addProjectOverviewDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Overview": function () {
          self.addProjectOverview(projectId, jQuery('#principalInvestigator').val(), jQuery('#numProposedSamples').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  addProjectOverview: function (projectId, pi, nsamples) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'addProjectOverview',
      {
        'projectId': projectId,
        'principalInvestigator': pi,
        'numProposedSamples': nsamples,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  showProjectOverviewNoteDialog: function (overviewId) {
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

    jQuery('#addProjectOverviewNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function () {
          self.addProjectOverviewNote(overviewId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  addProjectOverviewNote: function (overviewId, internalOnly, text) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'addProjectOverviewNote',
      {
        'overviewId': overviewId,
        'internalOnly': internalOnly,
        'text': text,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  deleteProjectOverviewNote: function (overviewId, noteId) {
    if (confirm("Are you sure you want to delete this note?")) {
      Fluxion.doAjax(
        'projectControllerHelperService',
        'deleteProjectOverviewNote',
        {
          'overviewId': overviewId,
          'noteId': noteId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload
        }
      );
    }
  },

  addSampleGroupTable: function (projectId, overviewId) {
    var tableDiv = "#sampleGroupTableDiv"+overviewId;
    var tableId = "#sampleGroupTable"+overviewId;

    jQuery(tableDiv).html("<table class='list display' id='sampleGroupTable"+overviewId+"'><thead><tr><th>Sample Name</th><th>Alias</th><th>Type</th><th>Description</th></tr></thead><tbody></tbody></table>");

    Fluxion.doAjax(
      'projectControllerHelperService',
      'listSamplesByProject',
      {
        'projectId': projectId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery(tableId).html('');
  
          var oTable = jQuery(tableId).dataTable({
            "aaData": json.array,
            "aoColumns": [
              { "mData":"id", "bVisible":"false"},
              { "sTitle": "Sample Name", "mData":"name"},
              { "sTitle": "Alias", "mData":"alias", "sType": "natural"},
              { "sTitle": "Type", "mData":"type"},
              { "sTitle": "Description", "mData":"description"}
            ],
            "aoColumnDefs": [
              {
                "bUseRendered": false,
                "aTargets": [ 0 ]
              }
            ],
            "aaSorting": [
              [1, 'asc']
            ],
            "bPaginate": false,
            "bInfo": false,
            "bJQueryUI": true,
            "bAutoWidth": true,
            "bFilter": false,
            "sDom": '<<"toolbar">f>r<t>ip>'
          });
  
          //bug fix to reset table width
          jQuery(tableId).removeAttr("style");
  
          jQuery(tableId).find("tr").each(function () {
            jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
          });
  
          jQuery(tableId).find("tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"" + tableId + "\", this);'></span></th>");
          jQuery(tableId).find("tr:gt(0)").prepend("<td class='rowSelect'></td>");
  
          jQuery(tableId).find('.rowSelect').click(function () {
            if (jQuery(this).parent().hasClass('row_selected')) {
              jQuery(this).parent().removeClass('row_selected');
            } else {
              jQuery(this).parent().addClass('row_selected');
            }
          });
  
          jQuery("div.toolbar").html("<input type='button' value='Group Selected' onclick=\"Project.overview.addSampleGroup('"+overviewId+"', '" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\"/>");
          jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
          jQuery("div.toolbar").removeClass("toolbar");
        }
      }
    );
  },

  addSampleGroup: function (overviewId, tableId) {
    var samples = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < nodes.length; i++) {
      var anSelectedData = table.fnGetData(nodes[i].cells[0]);
      samples[i] = {'sampleId': anSelectedData};
    }

    Fluxion.doAjax(
      'projectControllerHelperService',
      'addSampleGroup',
      {
        'overviewId': overviewId,
        'samples': samples,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  addSamplesToGroupTable: function (projectId, overviewId, groupId) {
    var tableDiv = "#sampleGroupTableDiv"+overviewId;
    var tableId = "#sampleGroupTable"+overviewId;

    jQuery(tableDiv).html("<table class='list display' id='sampleGroupTable"+overviewId+"'><thead><tr><th>Sample Name</th><th>Alias</th><th>Type</th><th>Description</th></tr></thead><tbody></tbody></table>");

    Fluxion.doAjax(
      'projectControllerHelperService',
      'listSamplesByProject',
      {
        'projectId': projectId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery(tableId).html('');
  
          var oTable = jQuery(tableId).dataTable({
            "aaData": json.array,
            "aoColumns": [
              { "mData":"id", "bVisible":"false"},
              { "sTitle": "Sample Name", "mData":"name"},
              { "sTitle": "Alias", "mData":"alias", "sType": "natural"},
              { "sTitle": "Type", "mData":"type"},
              { "sTitle": "Description", "mData":"description"}
            ],
            "aoColumnDefs": [
              {
                "bUseRendered": false,
                "aTargets": [ 0 ]
              }
            ],
            "aaSorting": [
              [1, 'asc']
            ],
            "bPaginate": false,
            "bInfo": false,
            "bJQueryUI": true,
            "bAutoWidth": true,
            "bFilter": false,
            "sDom": '<<"toolbar">f>r<t>ip>'
          });
  
          //bug fix to reset table width
          jQuery(tableId).removeAttr("style");
  
          jQuery(tableId).find("tr").each(function () {
            jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
          });
  
          jQuery(tableId).find("tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"" + tableId + "\", this);'></span></th>");
          jQuery(tableId).find("tr:gt(0)").prepend("<td class='rowSelect'></td>");
  
          jQuery(tableId).find('.rowSelect').click(function () {
            if (jQuery(this).parent().hasClass('row_selected')) {
              jQuery(this).parent().removeClass('row_selected');
            } else {
              jQuery(this).parent().addClass('row_selected');
            }
          });
  
          jQuery("div.toolbar").html("<input type='button' value='Add Selected' onclick=\"Project.overview.addSamplesToGroup('"+overviewId+"', '" + tableId + "', '"+groupId+"');\" class=\"fg-button ui-state-default ui-corner-all\">Add Selected</input>");
          jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</input>");
          jQuery("div.toolbar").removeClass("toolbar");
        }
      }
    );
  },

  addSamplesToGroup: function (overviewId, tableId, groupId) {
    var samples = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < nodes.length; i++) {
      var anSelectedData = table.fnGetData(nodes[i].cells[0]);
      samples[i] = {'sampleId': anSelectedData};
    }

    Fluxion.doAjax(
      'projectControllerHelperService',
      'addSamplesToGroup',
      {
        'overviewId': overviewId,
        'groupId': groupId,
        'samples': samples,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  unlockProjectOverview: function (overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'unlockProjectOverview',
      {
        'overviewId': overviewId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  lockProjectOverview: function (overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'lockProjectOverview',
      {
        'overviewId': overviewId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  }
};

Project.issues = {
  removeIssueBox: function (closespan) {
    if (confirm("Are you sure you want to unlink this issue from this project?")) {
      var boxId = jQuery(closespan).parent().attr("id");
      jQuery('#' + boxId).remove();
    }
  },

  importProjectFromIssue: function () {
    var self = this;
    var issue = jQuery('#previewKey').val();
    if (issue !== "undefined" && issue !== "") {
      Fluxion.doAjax(
        'projectControllerHelperService',
        'previewIssues',
        {
          'issues': [
            {
              "key": issue
            }
          ],
          'url': ajaxurl
        },
        {
          'doOnSuccess': self.importIssue
        }
      );
    } else {
      alert("Please enter a valid Issue Key, e.g. FOO-1");
    }
  },

  importIssue: function (json) {
    if (json.invalidIssues === "undefined" ||
        json.invalidIssues.length === 0 &&
        json.validIssues !== "undefined" &&
        json.validIssues.length > 0) {
      var key = json.validIssues[0].key;
      var issue = json.validIssues[0].fields;
      var issueurl = json.validIssues[0].url;
      jQuery('#alias').val(issue.summary);
      jQuery('#description').val(issue.description);

      jQuery('#issues').append("<div id='importbox" + 0 + "' class='simplebox backwhite'>");
      jQuery('#importbox' + 0).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='Project.issues.removeIssueBox(this);'>Unlink</button>");
      jQuery('#importbox' + 0).append("<h2 onclick=\"Utils.page.newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
      jQuery('#importbox' + 0).append("<b>Summary:</b> " + issue.summary + "<br/>");
      jQuery('#importbox' + 0).append("<b>Description:</b> " + issue.description + "<br/>");
      jQuery('#importbox' + 0).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.reporter.url + "');\">" + issue.reporter.displayName + "</a><br/>");
      jQuery('#importbox' + 0).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.assignee.url + "');\">" + issue.assignee.displayName + "</a><br/>");
      jQuery('#importbox' + 0).append("<b>Created:</b> " + issue.created + "<br/>");
      jQuery('#importbox' + 0).append("<b>Updated:</b> " + issue.updated + "<br/>");
      jQuery('#importbox' + 0).append("<input type='hidden' value='on' name='_issueKeys'/>");
      jQuery('#importbox' + 0).append("<input type='hidden' name='issueKeys' id='issueKeys0' value='" + key + "'><hr/>");
      jQuery('#issues').append("</div>");
    }
  },

  previewIssueKeys: function () {
    var self = this;
    var inKeys = jQuery('#previewKeys').val();
    if (inKeys !== "undefined" && inKeys !== "") {
      var issueKeys = inKeys.replace(/[\s]*/, "").split(",");
      var issues = [];
      for (var i = 0; i < issueKeys.length; i++) {
        issues[i] = {"key": issueKeys[i]};
      }

      Fluxion.doAjax(
        'projectControllerHelperService',
        'previewIssues',
        {
          'issues': issues,
          'url': ajaxurl
        },
        {
          'doOnSuccess': self.previewIssues
        }
      );
    } else {
      alert("Please enter a valid Issue Key, or list of keys, e.g. FOO-1,FOO-2,FOO-3");
    }
  },

  previewIssues: function (json) {
    if (json.validIssues !== "undefined" && json.validIssues.length > 0) {
      for (var i = 0; i < json.validIssues.length; i++) {
        var key = json.validIssues[i].key;
        var issueurl = json.validIssues[i].url;
        var issue = json.validIssues[i].fields;
        jQuery('#issues').append("<div id='previewbox" + i + "' class='simplebox backwhite'>");
        jQuery('#previewbox' + i).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='Project.issues.removeIssueBox(this);'>Unlink</button>");
        jQuery('#previewbox' + i).append("<h2 onclick=\"Utils.page.newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
        jQuery('#previewbox' + i).append("<b>Summary:</b> " + issue.summary + "<br/>");
        jQuery('#previewbox' + i).append("<b>Description:</b> " + issue.description + "<br/>");
        jQuery('#previewbox' + i).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.reporter.url + "');\">" + issue.reporter.displayName + "</a><br/>");
        jQuery('#previewbox' + i).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.assignee.url + "');\">" + issue.assignee.displayName + "</a><br/>");
        jQuery('#previewbox' + i).append("<b>Created:</b> " + issue.created + "<br/>");
        jQuery('#previewbox' + i).append("<b>Updated:</b> " + issue.updated + "<br/>");
        jQuery('#previewbox' + i).append("<input type='hidden' name='issueKeys' id='issueKeys" + i + "' value='" + key + "'><hr/>");
        jQuery('#issues').append("</div>");
      }
    }

    jQuery('#issues').append("<input type='hidden' value='on' name='_issueKeys'/>");
  },

  getProjectIssues: function (projectId) {
    var self = this;
    Fluxion.doAjax(
      'projectControllerHelperService',
      'getIssues',
      {
        'projectId': projectId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': self.processIssues
      }
    );
  },

  processIssues: function (json) {
    if (json.issues !== "undefined" && json.issues.length > 0) {
      jQuery('#issues').html("");
      for (var i = 0; i < json.issues.length; i++) {
        var key = json.issues[i].key;
        var issueurl = json.issues[i].url;
        var issue = json.issues[i].fields;
        jQuery('#issues').append("<div id='issuebox" + i + "' class='simplebox backwhite'>");
        jQuery('#issuebox' + i).append("<button type='button' style='float:right;' class='fg-button ui-state-default ui-corner-all' onclick='Project.issues.removeIssueBox(this);'>Remove</button>");
        jQuery('#issuebox' + i).append("<h2 onclick=\"Utils.page.newWindow('" + issueurl + "');\">Issue " + key + "</h2><br/>");
        jQuery('#issuebox' + i).append("<b>Summary:</b> " + issue.summary + "<br/>");
        jQuery('#issuebox' + i).append("<b>Description:</b> " + issue.description + "<br/>");
        jQuery('#issuebox' + i).append("<b>Reporter:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.reporter.url + "');\">" + issue.reporter.displayName + "</a><br/>");
        jQuery('#issuebox' + i).append("<b>Assignee:</b> <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + issue.assignee.url + "');\">" + issue.assignee.displayName + "</a><br/>");
        jQuery('#issuebox' + i).append("<b>Created:</b> " + issue.created + "<br/>");
        jQuery('#issuebox' + i).append("<b>Updated:</b> " + issue.updated + "<br/>");

        if (issue.issuelinks.length > 0) {
          jQuery('#issuebox' + i).append("<h4>Links</h4>");
          for (var j = 0; j < issue.issuelinks.length; j++) {
            var link = issue.issuelinks[j];
            jQuery('#issuebox' + i).append(link.type.outward + " <a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + link.url + "');\">" + link.url + "</a><br/>");
          }
        }

        if (issue["sub-tasks"].value.length > 0) {
          jQuery('#issuebox' + i).append("<h4>Subtasks</h4>");
          for (var k = 0; k < issue.subtasks.length; k++) {
            var subtask = issue.subtasks[k];
            jQuery('#issuebox' + i).append("<a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + subtask.url + "');\">" + subtask.url + "</a><br/>");
          }
        }

        jQuery('#issuebox' + i).append("<h4>Comments</h4>");
        for (var m = 0; m < issue.comment.comments.length; m++) {
          var comment = issue.comment.comments[m];
          jQuery('#issuebox' + i).append("<div id='commentbox" + i + "_" + m + "' class='simplebox backwhite' onclick=\"Utils.page.newWindow('" + comment.url + "');\">");
          jQuery('#commentbox' + i + "_" + m).append("<a href='javascript:void(0);' onclick=\"Utils.page.newWindow('" + comment.author.url + "');\">" + comment.author.displayName + "</a>");
          jQuery('#commentbox' + i + "_" + m).append(" at " + comment.created + "<br/>");
          jQuery('#commentbox' + i + "_" + m).append("<pre class='wrap'>" + comment.body + "</pre>");
        }

        jQuery('#issuebox' + i).append("<input type='hidden' name='issueKeys' id='issueKeys" + i + "' value='" + key + "'" + "><hr/>");
        jQuery('#issues').append("</div>");
      }
    }
    jQuery('#issues').append("<input type='hidden' value='on' name='_issueKeys'/>");
  }
};

Project.barcode = {
  printAllSampleBarcodes: function (projectId) {
    if (confirm("Are you sure you want to print all sample barcodes?")) {
      Fluxion.doAjax(
        'projectControllerHelperService',
        'printAllSampleBarcodes',
        {
          'projectId': projectId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            alert(json.response);
          }
        }
      );
    }
  },

  printAllLibraryBarcodes: function (projectId) {
    if (confirm("Are you sure you want to print all library barcodes?")) {
      Fluxion.doAjax(
        'projectControllerHelperService',
        'printAllLibraryBarcodes',
        {
          'projectId': projectId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            alert(json.response);
          }
        }
      );
    }
  },

  printAllLibraryDilutionBarcodes: function (projectId) {
    if (confirm("Are you sure you want to print all library dilution barcodes?")) {
      Fluxion.doAjax(
        'projectControllerHelperService',
        'printAllLibraryDilutionBarcodes',
        {
          'projectId': projectId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            alert(json.response);
          }
        }
      );
    }
  },

  selectSampleBarcodesToPrint: function (tableId) {
    if (!jQuery(tableId).hasClass("display")) {
      //destroy current table and recreate
      jQuery(tableId).dataTable().fnDestroy();
      //bug fix to reset table width
      jQuery(tableId).removeAttr("style");

      jQuery(tableId).addClass("display");

      jQuery(tableId).find('tr:first th:gt(6)').remove();
      jQuery(tableId).find("tr").each(function () {
        jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
        jQuery(this).find("td:gt(6)").remove();
      });

      jQuery(tableId).find("tr:first").prepend("<th width='5%'>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"" + tableId + "\", this);'></span></th>");
      jQuery(tableId).find("tr:gt(0)").prepend("<td width='5%' class='rowSelect'></td>");

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
        "aaSorting": [
          [2, 'asc']
        ],
        "bPaginate": false,
        "bInfo": false,
        "bJQueryUI": true,
        "bAutoWidth": true,
        "aoColumns": [
          {"bSortable": false},
          { "sType": 'natural' },
          { "sType": 'natural' },
          null,
          null,
          null,
          null,
          null
      ],
        "bFilter": false,
        "sDom": '<<"toolbar">f>r<t>ip>'
      });

      jQuery(tableId).find('.rowSelect').click(function () {
        if (jQuery(this).parent().hasClass('row_selected')) {
          jQuery(this).parent().removeClass('row_selected');
        } else {
          jQuery(this).parent().addClass('row_selected');
        }
      });

      jQuery("div.toolbar").html("<input type='button' value='Print Selected' type='button' onclick=\"Project.barcode.printSelectedSampleBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\"/>");
      jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
      jQuery("div.toolbar").removeClass("toolbar");
    }
  },

  printSelectedSampleBarcodes: function (tableId) {
    var samples = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < nodes.length; i++) {
      samples[i] = {'sampleId': jQuery(nodes[i]).attr("sampleId")};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#printServiceSelectDialog')
            .html("<form>" +
                  "<fieldset class='dialog'>" +
                  "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                  json.services +
                  "</select></fieldset></form>");

          jQuery('#printServiceSelectDialog').dialog({
            width: 400,
            modal: true,
            resizable: false,
            buttons: {
              "Print": function () {
                Fluxion.doAjax(
                  'projectControllerHelperService',
                  'printSelectedSampleBarcodes',
                  {
                    'printerId': jQuery('#serviceSelect').val(),
                    'samples': samples,
                    'url': ajaxurl
                  },
                  {
                    'doOnSuccess': function (json) {
                      alert(json.response);
                    }
                  });
                jQuery(this).dialog('close');
              },
              "Cancel": function () {
                jQuery(this).dialog('close');
              }
            }
          });
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  },

  selectLibraryBarcodesToPrint: function (tableId) {
    if (!jQuery(tableId).hasClass("display")) {
      //destroy current table and recreate
      jQuery(tableId).dataTable().fnDestroy();
      //bug fix to reset table width
      jQuery(tableId).removeAttr("style");

      jQuery(tableId).addClass("display");

      jQuery(tableId).find('tr:first th:gt(8)').remove();
      jQuery(tableId).find("tr").each(function () {
        jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
        jQuery(this).find("td:gt(8)").remove();
      });

      jQuery(tableId).find("tr:first").prepend("<th width='5%'>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"" + tableId + "\", this);'></span></th>");
      jQuery(tableId).find("tr:gt(0)").prepend("<td width='5%' class='rowSelect'></td>");
      //Library Name 	Library Alias 	Library Description 	Library Type 	Library Platform 	Index 	Insert Size
      var headers = ['rowsel',
                     'name',
                     'alias',
                     'date',
                     'description',
                     'libraryType',
                     'platform',
                     'index',
                     'insertSize'];

      var oTable = jQuery(tableId).dataTable({
        "aoColumnDefs": [
          {
            "bUseRendered": false,
            "aTargets": [ 0 ]
          }
        ],
        "aaSorting": [
          [2, 'asc']
        ],
        "bPaginate": false,
        "bInfo": false,
        "bJQueryUI": true,
        "bAutoWidth": true,

         "aoColumns": [
           {"bSortable": false},
           { "sType": 'natural' },
           { "sType": 'natural' },
           { "sType": 'natural' },
           null,
           null,
           null,
           null,
           null,
           null
         ],
        "bFilter": false,
        "sDom": '<<"toolbar">f>r<t>ip>'
      });

      jQuery(tableId).find('.rowSelect').click(function () {
        if (jQuery(this).parent().hasClass('row_selected')) {
          jQuery(this).parent().removeClass('row_selected');
        } else {
          jQuery(this).parent().addClass('row_selected');
        }
      });

      jQuery("div.toolbar").html("<input type='button' value='Print Selected' onclick=\"Project.barcode.printSelectedLibraryBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\"/>");
      jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
      jQuery("div.toolbar").removeClass("toolbar");
    }
  },

  printSelectedLibraryBarcodes: function (tableId) {
    var libraries = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < nodes.length; i++) {
      libraries[i] = {'libraryId': jQuery(nodes[i]).attr("libraryId")};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#printServiceSelectDialog')
            .html("<form>" +
                  "<fieldset class='dialog'>" +
                  "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                  json.services +
                  "</select></fieldset></form>");

          jQuery('#printServiceSelectDialog').dialog({
            width: 400,
            modal: true,
            resizable: false,
            buttons: {
              "Print": function () {
                Fluxion.doAjax(
                  'projectControllerHelperService',
                  'printSelectedLibraryBarcodes',
                  {
                    'printerId': jQuery('#serviceSelect').val(),
                    'libraries': libraries,
                    'url': ajaxurl
                  },
                  {
                    'doOnSuccess': function (json) {
                      alert(json.response);
                    }
                  }
                );
                jQuery(this).dialog('close');
              },
              "Cancel": function () {
                jQuery(this).dialog('close');
              }
            }
          });
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  },

  selectLibraryDilutionBarcodesToPrint: function (tableId) {
    if (!jQuery(tableId).hasClass("display")) {
      //destroy current table and recreate
      jQuery(tableId).dataTable().fnDestroy();
      //bug fix to reset table width
      jQuery(tableId).removeAttr("style");

      jQuery(tableId).addClass("display");

      jQuery(tableId).find('tr:first th:gt(5)').remove();
      jQuery(tableId).find("tr").each(function () {
        jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
        jQuery(this).find("td:gt(5)").remove();
      });

      jQuery(tableId).find("tr:first").prepend("<th width='5%'>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"" + tableId + "\", this);'></span></th>");
      jQuery(tableId).find("tr:gt(0)").prepend("<td width='5%' class='rowSelect'></td>");
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
        "aaSorting": [
           [2, 'asc']
        ],
        "bPaginate": false,
        "bInfo": false,
        "bJQueryUI": true,
        "bAutoWidth": true,
        "aoColumns": [
          {"bSortable": false},
          { "sType": 'natural' },
          { "sType": 'natural' },
          null,
          null,
          null,
          null
        ],
        "bFilter": false,
        "sDom": '<<"toolbar">f>r<t>ip>'
      });

      jQuery(tableId).find('.rowSelect').click(function () {
        if (jQuery(this).parent().hasClass('row_selected')) {
          jQuery(this).parent().removeClass('row_selected');
        } else {
          jQuery(this).parent().addClass('row_selected');
        }
      });

      jQuery("div.toolbar").html("<input type='button' value='Print Selected' onclick=\"Project.barcode.printSelectedLibraryDilutionBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\"/>");
      jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
      jQuery("div.toolbar").removeClass("toolbar");
    }
  },

  printSelectedLibraryDilutionBarcodes: function (tableId) {
    var dilutions = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < nodes.length; i++) {
      dilutions[i] = {'dilutionId': jQuery(nodes[i]).attr("dilutionId")};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass': 'uk.ac.bbsrc.tgac.miso.core.data.Dilution',
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#printServiceSelectDialog')
            .html("<form>" +
                  "<fieldset class='dialog'>" +
                  "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                  json.services +
                  "</select></fieldset></form>");

          jQuery('#printServiceSelectDialog').dialog({
            width: 400,
            modal: true,
            resizable: false,
            buttons: {
              "Print": function () {
                Fluxion.doAjax(
                  'projectControllerHelperService',
                  'printSelectedLibraryDilutionBarcodes',
                  {
                    'printerId': jQuery('#serviceSelect').val(),
                    'dilutions': dilutions,
                    'url': ajaxurl
                  },
                  {
                    'doOnSuccess': function (json) {
                      alert(json.response);
                    }
                  }
                );
                jQuery(this).dialog('close');
              },
              "Cancel": function () {
                jQuery(this).dialog('close');
              }
            }
          });
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  }
};

Project.alert = {
  watchOverview: function (overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'watchOverview',
      {
        'overviewId': overviewId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function () {
          Utils.page.pageReload();
        }
      }
    );
  },

  unwatchOverview: function (overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'unwatchOverview',
      {
        'overviewId': overviewId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function () {
          Utils.page.pageReload();
        }
      }
    );
  },

  listWatchOverview: function (overviewId) {
    Fluxion.doAjax(
      'projectControllerHelperService',
      'listWatchOverview',
      {
        'overviewId': overviewId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#watchersList' + overviewId).html(json.watchers);
        }
      }
    );
  }
};
