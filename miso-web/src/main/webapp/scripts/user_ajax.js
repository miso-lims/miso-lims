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

var User = User || {
  validateUser: function() {
    Validate.cleanFields('#user-form');
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
    jQuery('#email').attr('data-parsley-type', 'email');

    // Current Password input field validation
    jQuery('#currentPassword').attr('class', 'form-control');
    jQuery('#currentPassword').attr('data-parsley-maxlength', '100');
    jQuery('#currentPassword').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // New Password input field validation
    jQuery('#newpassword').attr('class', 'form-control');
    jQuery('#newpassword').attr('data-parsley-maxlength', '100');
    jQuery('#newpassword').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Confirm password input field validation
    jQuery('#confirmpassword').attr('class', 'form-control');
    jQuery('#confirmpassword').attr('data-parsley-maxlength', '100');
    jQuery('#confirmpassword').attr('data-parsley-equalto', '#newpassword');
    jQuery('#confirmpassword').attr('data-parsley-error-message', 'Password does not match!');
    jQuery('#confirmpassword').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    if (jQuery('#userId').text() === '0') {
      // new user
      jQuery('#newpassword').attr('data-parsley-required', 'true');
      jQuery('#confirmpassword').attr('data-parsley-required', 'true');
    } else {
      // edit user
      if (jQuery('#newpassword').val().length) {
        jQuery('#currentPassword').attr('data-parsley-required', 'true');
        jQuery('#confirmpassword').attr('data-parsley-required', 'true');
      }
    }

    jQuery('#user-form').parsley();
    jQuery('#user-form').parsley().validate();

    Validate.updateWarningOrSubmit('#user-form');
  }
};