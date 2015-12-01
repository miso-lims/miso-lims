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
  jQuery('#pool-form').parsley();
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

function clean_pool_fields() {
  jQuery('#pool-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#pool-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_pool(form) {
  clean_pool_fields();

  jQuery('#pool-form').parsley().destroy();

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

  // Concentration input field validation
  jQuery('#concentration').attr('class', 'form-control');
  jQuery('#concentration').attr('data-parsley-required', 'true');
  jQuery('#concentration').attr('data-parsley-maxlength', '100');
  jQuery('#concentration').attr('data-parsley-type', 'number');

  // Creation Date input field validation
  jQuery('creationDate').attr('data-parsley-required', 'true');
  jQuery('creationDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
  jQuery('creationDate').attr('data-date-format', 'DD/MM/YYYY');
  jQuery('creationDate').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');

  jQuery('#pool-form').parsley();
  jQuery('#pool-form').parsley().validate();

  validate_backend();
 }

var validate_backend = function() {
  if (jQuery('#pool-form').parsley().isValid() === true) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');

    jQuery('#pool-form').submit();
    return true;
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
    return false;
  }
};
