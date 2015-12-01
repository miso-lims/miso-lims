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
  jQuery('#user-form').parsley();
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

function clean_user_fields() {
  jQuery('#user-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#user-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_user() {
  clean_user_fields();

  jQuery('#user-form').parsley().destroy();

  // Full name input field validation
  jQuery('#fullName').attr('class', 'form-control');
  jQuery('#fullName').attr('data-parsley-required', 'true');
  jQuery('#fullName').attr('data-parsley-maxlength', '100');
  jQuery('#fullName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  // Login name input field validation
  jQuery('#loginName').attr('class', 'form-control');
  jQuery('#loginName').attr('data-parsley-required', 'true');
  jQuery('#loginName').attr('data-parsley-maxlength', '100');
  jQuery('#loginName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);


  // Login name input field validation
  jQuery('#email').attr('class', 'form-control');
  jQuery('#email').attr('data-parsley-required', 'true');
  jQuery('#email').attr('data-parsley-maxlength', '100');
  //jQuery('#email').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
  jQuery('#email').attr('data-parsley-type', 'email');


  // Current Password input field validation
  jQuery('#password').attr('class', 'form-control');
  jQuery('#password').attr('data-parsley-required', 'true');
  jQuery('#password').attr('data-parsley-maxlength', '100');
  jQuery('#password').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  // New Password input field validation
  jQuery('#newpassword').attr('class', 'form-control');
  jQuery('#newpassword').attr('data-parsley-required', 'true');
  jQuery('#newpassword').attr('data-parsley-maxlength', '100');
  jQuery('#newpassword').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  // Confirm password input field validation
  jQuery('#confirmpassword').attr('class', 'form-control');
  jQuery('#confirmpassword').attr('data-parsley-required', 'true');
  jQuery('#confirmpassword').attr('data-parsley-maxlength', '100');
  jQuery('#confirmpassword').attr('data-parsley-equalto', '#newpassword');
  jQuery('#confirmpassword').attr('data-parsley-error-message', 'Password does not match!');
  jQuery('#confirmpassword').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  jQuery('#user-form').parsley();
  jQuery('#user-form').parsley().validate();

  return validate_backend();
};

var validate_backend = function() {
  updateWarning();
  if (jQuery('#user-form').parsley().isValid() === true) {
    jQuery('#user-form').submit();
    return true;
  } else {
    return false;
  }
};



