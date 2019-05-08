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

var Submission = {
  validateSubmission: function() {
    Validate.cleanFields('#submission-form');
    jQuery('#submission-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '255');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Title input field validation
    jQuery('#title').attr('class', 'form-control');
    jQuery('#title').attr('data-parsley-maxlength', '255');
    jQuery('#title').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    jQuery('#submission-form').parsley();
    jQuery('#submission-form').parsley().validate();
    jQuery('#submission-form').submit();
  },
  download: function(id) {
    Utils.showDialog("Download XML", "Download", [{
      type: 'select',
      required: true,
      label: "Action",
      values: Constants.submissionAction,
      property: "action"
    }, {
      type: 'text',
      required: 'true',
      label: 'Centre Name',
      property: 'centerName'
    }, ], function(results) {
      window.location = window.location.origin + '/miso/rest/submissions/' + id + '/download?' + jQuery.param(results);

    });
  }
}
