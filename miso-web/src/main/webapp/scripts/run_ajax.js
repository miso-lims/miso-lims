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

var Run = Run || {
  userIsAdmin: false,

  deleteRun: function(runId, successfunc) {
    if (confirm("Are you sure you really want to delete RUN" + runId + "? This operation is permanent!")) {
      Fluxion.doAjax('runControllerHelperService', 'deleteRun', {
        'runId': runId,
        'url': ajaxurl
      }, {
        'doOnSuccess': function(json) {
          successfunc();
        }
      });
    }
  },

  // Validate methods can be found in parsley_form_validations.js
  validateRun: function() {
    Validate.cleanFields('#run-form');
    jQuery('#run-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '255');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    jQuery('#wellName').attr('class', 'form-control');
    jQuery('#wellName').attr('data-parsley-maxlength', '255');
    jQuery('#wellName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    jQuery('#movieDuration').attr('class', 'form-control');
    jQuery('#movieDuration').attr('data-parsley-maxlength', '10');
    jQuery('#movieDuration').attr('data-parsley-type', 'number');

    jQuery('#numCycles').attr('class', 'form-control');
    jQuery('#numCycles').attr('data-parsley-maxlength', '10');
    jQuery('#numCycles').attr('data-parsley-type', 'number');

    jQuery('#callCycle').attr('class', 'form-control');
    jQuery('#callCycle').attr('data-parsley-maxlength', '10');
    jQuery('#callCycle').attr('data-parsley-type', 'number');

    jQuery('#imgCycle').attr('class', 'form-control');
    jQuery('#imgCycle').attr('data-parsley-maxlength', '10');
    jQuery('#imgCycle').attr('data-parsley-type', 'number');

    jQuery('#scoreCycles').attr('class', 'form-control');
    jQuery('#scoreCycles').attr('data-parsley-maxlength', '10');
    jQuery('#scoreCycles').attr('data-parsley-type', 'number');

    jQuery('#cycles').attr('class', 'form-control');
    jQuery('#cycles').attr('data-parsley-maxlength', '10');
    jQuery('#cycles').attr('data-parsley-type', 'number');

    jQuery('#minKnowVersion').attr('class', 'form-control');
    jQuery('#minKnowVersion').attr('data-parsley-maxlength', '100');
    jQuery('#minKnowVersion').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    jQuery('#protocolVersion').attr('class', 'form-control');
    jQuery('#protocolVersion').attr('data-parsley-maxlength', '100');
    jQuery('#protocolVersion').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    jQuery('#startDate').attr('class', 'form-control');
    jQuery('#startDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#startDate').attr('data-date-format', 'YYYY-MM-DD');
    jQuery('#startDate').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');
    jQuery('#startDate').attr('required', 'true');

    if (!document.getElementById('completionDate').disabled) {
      jQuery('#completionDate').attr('class', 'form-control');
      jQuery('#completionDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
      jQuery('#completionDate').attr('data-date-format', 'YYYY-MM-DD');
      jQuery('#completionDate').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');
      jQuery('#completionDate').attr('required', 'true');
    } else {
      jQuery('#completionDate').removeAttr('required');
    }

    // Radio button validation: ensure a platform is selected
    jQuery('#platformType').attr('class', 'form-control');
    jQuery('#platformTypes1').attr('required', 'true');
    jQuery('#platformType').attr('data-parsley-error-message', 'You must select a Platform.');
    jQuery('#platformTypes1').attr('data-parsley-errors-container', '#platformError');
    jQuery('#platformType').attr('data-parsley-class-handler', '#platformButtons');

    // Sequencer select field validation
    jQuery('#sequencer').attr('class', 'form-control');
    jQuery('#sequencer').attr('required', 'true');
    jQuery('#sequencer').attr('data-parsley-min', '1');
    jQuery('#sequencer').attr('data-parsley-error-message', 'You must select a Sequencer.');
    jQuery('#sequencer').attr('data-parsley-errors-container', '#sequencerError');

    jQuery('#sequencingParameters').attr('class', 'form-control');
    jQuery('#sequencingParameters').attr('required', 'true');
    jQuery('#sequencingParameters').attr('data-parsley-min', '1');
    jQuery('#sequencingParameters').attr('data-parsley-error-message', 'You must select the sequencing parameters.');
    jQuery('#sequencingParameters').attr('data-parsley-errors-container', '#sequencingParametersError');

    // Run path input field validation
    jQuery('#filePath').attr('class', 'form-control');
    jQuery('#filePath').attr('data-parsley-required', 'true');
    jQuery('#filePath').attr('data-parsley-maxlength', '100');

    jQuery('#run-form').parsley();
    jQuery('#run-form').parsley().validate();
    jQuery('#run-form').submit();
  },

  checkForCompletionDate: function(showDialog) {
    var statusVal = jQuery('input[name=health]:checked').val();
    if (Utils.validation.isNullCheck(statusVal)) {
      return;
    }
    var completionDate = document.getElementById("completionDate");
    if (!completionDate) {
      return;
    }
    var allowModification = ((statusVal === "Failed" || statusVal === "Completed") && (!completionDate.value || Run.userIsAdmin));
    completionDate.disabled = !allowModification;
  },
};

Run.ui = {
  showRunNoteDialog: function(runId) {
    var self = this;
    jQuery('#addRunNoteDialog')
        .html(
            "<form>"
                + "<fieldset class='dialog'>"
                + "<label for='internalOnly'>Internal Only?</label>"
                + "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />"
                + "<br/>" + "<label for='notetext'>Text</label>"
                + "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' autofocus />"
                + "</fieldset></form>");

    jQuery('#addRunNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function() {
          if (jQuery('#notetext').val().length > 0) {
            Utils.notes.addNote('run', runId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          } else {
            jQuery('#notetext').focus();
          }
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  deleteRunNote: function(runId, noteId) {
    if (confirm("Are you sure you want to delete this note?")) {
      Utils.notes.deleteNote('run', runId, noteId);
    }
  },
};

Run.alert = {
  watchRun: function(runId) {
    Fluxion.doAjax('runControllerHelperService', 'watchRun', {
      'runId': runId,
      'url': ajaxurl
    }, {
      'doOnSuccess': function() {
        Utils.page.pageReload();
      }
    });
  },

  unwatchRun: function(runId) {
    Fluxion.doAjax('runControllerHelperService', 'unwatchRun', {
      'runId': runId,
      'url': ajaxurl
    }, {
      'doOnSuccess': function() {
        Utils.page.pageReload();
      }
    });
  }
};
