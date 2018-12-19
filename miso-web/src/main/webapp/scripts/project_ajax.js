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

// these variables are set on the editProject page if the project has samples/libraries
var projectId_sample, sampleQcTypesString, libraryQcTypesString;

// Custom Parsley validator to validate Project shortName server-side
window.Parsley.addValidator('projectShortName', {
  validateString: function(value) {
    var deferred = new jQuery.Deferred();
    jQuery.ajax({
      url: '/miso/rest/project/validate-short-name',
      type: 'POST',
      contentType: 'application/json; charset=utf8',
      data: JSON.stringify({
        shortName: value
      })
    }).success(function(json) {
      deferred.resolve();
    }).fail(function(response, textStatus, serverStatus) {
      deferred.reject(response); // need to upgrade Parsley to get custom error messages
    });
    return deferred.promise();
  },
  messages: {
    en: 'Short name must conform to the naming scheme.'
  }
});

var Project = Project || {
  validateProject: function() {
    Validate.cleanFields('#project-form');
    jQuery('#project-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '100');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Short name input field validation
    jQuery('#shortName').attr('class', 'form-control');
    jQuery('#shortName').attr('data-parsley-maxlength', '5');
    jQuery('#shortName').attr('data-parsley-validate-if-empty', '');
    jQuery('#shortName').attr('data-parsley-project-short-name', '');
    jQuery('#shortName').attr('data-parsley-debounce', '500');

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Radio button validation: ensure a button is selected (assumes there is one progress button, no other way to check because of dynamic
    // generation)
    jQuery('#progress').attr('class', 'form-control');
    jQuery('#progress1').attr('required', 'true');
    jQuery('#progress').attr('data-parsley-error-message', 'You must select a progress status.');
    jQuery('#progress1').attr('data-parsley-errors-container', '#progressSelectError');
    jQuery('#progress').attr('data-parsley-class-handler', '#progressButtons');

    if (jQuery('#securityProfile_owner').length > 0) {
      jQuery('#securityProfile_owner').attr('class', 'form-control');
      jQuery('#securityProfile_owner').attr('required', 'true');
    }

    jQuery('#project-form').parsley();
    jQuery('#project-form').parsley().validate();

    Validate.updateWarningOrSubmit('#project-form');
  }
};
