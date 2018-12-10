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

// Custom Parsley validator to validate Project shortName server-side
window.Parsley.addValidator('projectShortName', {
  validateString: function(value) {
    var deferred = new jQuery.Deferred();
    Fluxion.doAjax('projectControllerHelperService', 'validateProjectShortName', {
      'shortName': value,
      'url': ajaxurl
    }, {
      'doOnSuccess': function(json) {
        deferred.resolve();
      },
      'doOnError': function(json) {
        deferred.reject(json.error);
      }
    });
    return deferred.promise();
  },
  messages: {
    en: 'Short name must conform to the naming scheme.'
  }
});

var Project = Project || {
  validateProject: function() {
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

  validate_sample_qcs: function(json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if (!json[i].results.match(/[0-9\.]+/)) {
        ok = false;
      }
    }
    return ok;
  },

  validate_empcrs: function(json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if (!json[i].results.match(/[0-9\.]+/)) {
        ok = false;
      }
    }
    return ok;
  },

  validate_empcr_dilutions: function(json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if (!json[i].results.match(/[0-9\.]+/)) {
        ok = false;
      }
    }
    return ok;
  }
};

Project.ui = {
  processSampleDeliveryForm: function(projectId, plate, ids) {
    Fluxion.doAjax('projectControllerHelperService', 'generateSampleDeliveryForm', {
      'plate': plate,
      'projectId': projectId,
      'samples': ids,
      'url': ajaxurl
    }, {
      'doOnSuccess': function(json) {
        Utils.page.pageRedirect('/miso/download/project/' + projectId + '/' + json.response);
      }
    });
  },

  projectFileUploadSuccess: function() {
    jQuery('#statusdiv').html("Upload complete. Refresh to see the file.");
  },

  deleteFile: function(projectId, fileKey) {
    if (confirm("Are you sure you want to delete this file?")) {
      Fluxion.doAjax('projectControllerHelperService', 'deleteProjectFile', {
        'id': projectId,
        'hashcode': fileKey,
        'url': ajaxurl
      }, {
        'doOnSuccess': Utils.page.pageReload
      });
    }
  },

  deliveryFormUploadSuccess: function() {
    jQuery('#deliveryform_statusdiv').html("Samples imported.");
  },

  uploadSampleDeliveryForm: function() {
    jQuery('#deliveryformdiv').css("display", "block");
  },

  cancelSampleDeliveryFormUpload: function() {
    jQuery('#deliveryformdiv').css("display", "none");
  },

  downloadBulkSampleInputForm: function(projectId, documentFormat) {
    Fluxion.doAjax('projectControllerHelperService', 'downloadBulkSampleInputForm', {
      'projectId': projectId,
      'documentFormat': documentFormat,
      'url': ajaxurl
    }, {
      'doOnSuccess': function(json) {
        Utils.page.pageRedirect('/miso/download/project/' + projectId + '/' + json.response);
      }
    });
  },

  bulkSampleInputFormUploadSuccess: function() {
    jQuery('#inputform_statusdiv').html("");
    Fluxion.doAjax('projectControllerHelperService', 'visualiseBulkSampleInputForm', {
      'url': ajaxurl
    }, {
      'updateElement': 'inputform_statusdiv'
    });
  },

  uploadBulkSampleInputForm: function() {
    jQuery('#inputformdiv').css("display", "block");
  },

  cancelBulkSampleInputFormUpload: function() {
    jQuery('#inputformdiv').css("display", "none");
  },

  saveBulkSampleQc: function(tableName) {
    var self = this;
    Utils.ui.disableButton('bulkSampleQcButton');
    DatatableUtils.collapseInputs(tableName);

    var table = jQuery(tableName).dataTable();
    var aReturn = [];

    var aTrs = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < aTrs.length; i++) {
      var obj = {};
      jQuery(aTrs[i]).find("td:gt(0)").each(function() {
        var at = jQuery(this).attr("name");
        obj[at] = jQuery(this).text();
      });
      obj.qcCreator = jQuery('#currentUser').text();
      obj.sampleId = obj.name.substring(3);
      aReturn.push(obj);
    }

    if (aReturn.length > 0) {
      if (Project.validate_sample_qcs(aReturn)) {
        Fluxion.doAjax('sampleControllerHelperService', 'bulkAddSampleQCs', {
          'qcs': aReturn,
          'url': ajaxurl
        }, {
          'doOnSuccess': function(json) {
            self.processBulkSampleQcTable(tableName, json);
          }
        });
      } else {
        alert("The results field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");
      }
    } else {
      alert("You have not selected any QC rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");
    }
  },

  processBulkSampleQcTable: function(tableName, json) {
    Utils.ui.reenableButton('bulkSampleQcButton', "Save QCs");
    var a = json.saved;
    for (var i = 0; i < a.length; i++) {
      jQuery(tableName).find("tr:gt(0)").each(function() {
        if (jQuery(this).attr("sampleId") === a[i].sampleId) {
          jQuery(this).removeClass('row_selected');
          jQuery(this).addClass('row_saved');
          jQuery(this).find("td").each(function() {
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
        jQuery(tableName).find("tr:gt(0)").each(function() {
          if (jQuery(this).attr("sampleId") === errors[j].sampleId) {
            jQuery(this).find("td").each(function() {
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

  receiveSelectedSamples: function(ids) {
    if (confirm("Are you sure you want to receive selected samples?")) {
      Fluxion.doAjax('sampleControllerHelperService', 'setSampleReceivedDateByBarcode', {
        'samples': ids,
        'url': ajaxurl
      }, {
        'doOnSuccess': function(json) {
          alert(json.result);
          Utils.page.pageReload();
        }
      });
    }
  },
};

