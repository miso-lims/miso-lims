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

jQuery(document).ready(function () {
  jQuery('#run-form').parsley();
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

function clean_run_fields() {
  jQuery('#run-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#run-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_run() {
  clean_run_fields();

  jQuery('#run-form').parsley().destroy();

  // Alias input field validation
  jQuery('#alias').attr('class', 'form-control');
  jQuery('#alias').attr('data-parsley-required', 'true');
  jQuery('#alias').attr('data-parsley-maxlength', '255');
  jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  // Description input field validation
  jQuery('#description').attr('class', 'form-control');
  jQuery('#description').attr('data-parsley-required', 'true');
  jQuery('#description').attr('data-parsley-maxlength', '255');
  jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  // Radio button validation: ensure a button is selected (assumes there is one progress button, no other way to check because of dynamic
  // generation)
  jQuery('#platformTypes1').attr('required', 'true');
  jQuery('#platformTypes1').attr('data-parsley-error-message', 'You must select a Platform.');
  jQuery('#platformTypes1').attr('data-parsley-errors-container', '#platformError');
  jQuery('#platformTypes1').attr('data-parsley-class-handler', '#platformButtons');

  // Run path input field validation
  jQuery('#filePath').attr('class', 'form-control');
  jQuery('#filePath').attr('data-parsley-required', 'true');
  jQuery('#filePath').attr('data-parsley-maxlength', '100');


  jQuery('#run-form').parsley();
  jQuery('#run-form').parsley().validate();

  validate_backend();
 }

var validate_backend = function() {
  updateWarning();
  if (jQuery('#run-form').parsley().isValid() === true) {
    old_crappy_validation(jQuery('#run-form'));
  }
};

function old_crappy_validation(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery("select[name=sequencer]").length > 0) {
    if (jQuery('select[name=sequencer] :selected').val() == 0) {
      ok = false;
      error += "You have not chosen a Sequencer for this Run.\n";
    }
  }

  if (jQuery("input[name=platformType]").length > 0) {
    if (!jQuery('input[name=platformType]').is(':checked')) {
      error += "You have not chosen the Platform Type of the Run.\n";
      ok = false;
    }
  }

  if (jQuery(':text.validateable').length > 0) {
    jQuery(':text.validateable').each(function() {
      var result = Utils.validation.validate_input_field(this, 'Run', ok);
      ok = result.okstatus;
      error += result.errormsg;
    });
  }

  if (jQuery('div[id^="studySelectDiv"]').length > 0) {
    if (!confirm("You haven't selected a study for one or more pools. Are you sure you still want to save?")) {
      ok = false;
      error += "Please select studies for all the pools added.\n";
    }
  }

  if (!ok) {
    alert(error);
    return ok;
  }

  jQuery('#run-form').submit();
  return ok;
};

function checkForCompletionDate() {
  var statusVal = jQuery('input[name=status\\.health]:checked').val();
  if (!Utils.validation.isNullCheck(statusVal)) {
    if (statusVal === "Failed" || statusVal === "Stopped") {
      alert("You are manually setting a run to Stopped or Failed. Please remember to enter a Completion Date!");
      if (jQuery("#completionDate input").length == 0) {
        jQuery("#completionDate").html("<input type='text' name='status.completionDate' id='status.completionDate' value='" + jQuery('#completionDate').html() + "'>");
        Utils.ui.addDatePicker("status\\.completionDate");
      }
    }
    else {
      if (jQuery("#status\\.completionDate").length > 0) {
        jQuery("#completionDate").html(jQuery("#status\\.completionDate").val());
      }
    }
  }
}
