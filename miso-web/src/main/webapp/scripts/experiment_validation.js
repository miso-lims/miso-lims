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
  jQuery('#experiment-form').parsley();
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

// Trim whitespace from input fields
function clean_experiment_fields() {
  jQuery('#experiment-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#experiment-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_experiment() {
  clean_experiment_fields();

  jQuery('#experiment-form').parsley().destroy();

  // Title input field validation
  jQuery('#title').attr('class', 'form-control');
  jQuery('#title').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
  jQuery('#title').attr('data-parsley-required', 'true');
  jQuery('#title').attr('data-parsley-maxlength', '255');

  // Alias input field validation
  jQuery('#alias').attr('class', 'form-control');
  jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
  jQuery('#alias').attr('data-parsley-required', 'true');
  jQuery('#alias').attr('data-parsley-maxlength', '100');

  // Description input field validation
  jQuery('#description').attr('class', 'form-control');
  jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
  jQuery('#description').attr('data-parsley-required', 'true');
  jQuery('#description').attr('data-parsley-maxlength', '255');

  jQuery('#experiment-form').parsley();
  jQuery('#experiment-form').parsley().validate();
  validate_backend();
};

function validate_backend() {
  updateWarning();

  // if valid, then submit form
  if (true === jQuery('#experiment-form').parsley().isValid()) {
    jQuery('#experiment-form').submit();
  }
};

/*
function validate_experiment(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery('#title').val() == "") {
    ok = false;
    error += "You have not entered a title for the Experiment.\n";
  }

  if (jQuery(':text.validateable').length > 0) {
    jQuery(':text.validateable').each(function() {
      var result = Utils.validation.validate_input_field(this, 'Experiment', ok);
      ok = result.okstatus;
      error += result.errormsg;
    });
  }

  if (!ok) {
    alert(error);
  }

  return ok;
}
*/
