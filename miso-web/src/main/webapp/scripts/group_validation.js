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
  jQuery('#group-form').parsley();
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

function clean_group_fields() {
  jQuery('#group-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#group-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_group() {
  clean_group_fields();

  jQuery('#group-form').parsley().destroy();

  // Full name input field validation
  jQuery('#name').attr('class', 'form-control');
  jQuery('#name').attr('data-parsley-required', 'true');
  jQuery('#name').attr('data-parsley-maxlength', '100');
  jQuery('#name').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  // Login name input field validation
  jQuery('#description').attr('class', 'form-control');
  jQuery('#description').attr('data-parsley-required', 'true');
  jQuery('#description').attr('data-parsley-maxlength', '100');
  jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  jQuery('#group-form').parsley();
  jQuery('#group-form').parsley().validate();

  return validate_backend();
};

var validate_backend = function() {
  updateWarning();
  if (jQuery('#group-form').parsley().isValid() === true) {
    jQuery('#group-form').submit();
    return true;
  } else {
    return false;
  }
};
