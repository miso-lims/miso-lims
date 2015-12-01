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
  jQuery('#project-form').parsley();
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

function clean_project_fields() {
  jQuery('#project-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#project-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_project() {
  clean_project_fields();

  jQuery('#project-form').parsley().destroy();

  // Alias input field validation
  jQuery('#alias').attr('class', 'form-control');
  jQuery('#alias').attr('data-parsley-required', 'true');
  jQuery('#alias').attr('data-parsley-maxlength', '100');
  jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  // Description input field validation
  jQuery('#description').attr('class', 'form-control');
  jQuery('#description').attr('data-parsley-required', 'true');
  jQuery('#description').attr('data-parsley-maxlength', '100');
  jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  // Radio button validation: ensure a button is selected (assumes there is one progress button, no other way to check because of dynamic
  // generation)
  jQuery('#progress1').attr('required', 'true');
  jQuery('#progress1').attr('data-parsley-error-message', 'You must select a progress.');
  jQuery('#progress1').attr('data-parsley-errors-container', '#progressSelectError');
  jQuery('#progress1').attr('data-parsley-class-handler', '#progressButtons');


  jQuery('#project-form').parsley();
  jQuery('#project-form').parsley().validate();

  validateFront();
 }

var validateFront = function() {
  if (jQuery('#project-form').parsley().isValid() === true) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');

    jQuery('#project-form').submit();
    return true;
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
    return false;
  }
};

function validate_sample_qcs(json) {
  var ok = true;
  for (var i = 0; i < json.length; i++) {
    if(!json[i].results.match(/[0-9\.]+/)) ok = false;
  }
  return ok;
}

function validate_empcrs(json) {
  var ok = true;
  for (var i = 0; i < json.length; i++) {
    if(!json[i].results.match(/[0-9\.]+/)) ok = false;
  }
  return ok;
}

function validate_empcr_dilutions(json) {
  var ok = true;
  for (var i = 0; i < json.length; i++) {
    if(!json[i].results.match(/[0-9\.]+/)) ok = false;
  }
  return ok;
}
