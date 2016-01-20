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
  Validate.attachParsley('#group-form');
});

function validateGroup() {
  Validate.cleanFields('#group-form');
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

  Validate.updateWarningOrSubmit('#group-form');
  return false;
};
