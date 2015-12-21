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
  jQuery('#library-form').parsley();
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

// Trim whitespace from input fields
function clean_library_fields() {
  jQuery('#library-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#library-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_library() {
  clean_library_fields();
  jQuery('#library-form').parsley().destroy();

  // Alias input field validation
  jQuery('#alias').attr('class', 'form-control');
  jQuery('#alias').attr('data-parsley-required', 'true');
  jQuery('#alias').attr('data-parsley-maxlength', '100');

  // Description input field validation
  jQuery('#description').attr('class', 'form-control');
  jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
  jQuery('#description').attr('data-parsley-required', 'true');
  jQuery('#description').attr('data-parsley-maxlength', '100');

  Fluxion.doAjax(
    'libraryControllerHelperService',
    'getLibraryAliasRegex',
    {
      'url': ajaxurl
    },
    {
      'doOnSuccess': function(json) {
        // very hacky much bad. TODO: fix Fluxion
        var regex = json.aliasRegex.split(' ').join('+');
        jQuery('#alias').attr('data-parsley-pattern', regex);
        // TODO: better error message than a regex..?
        //       perhaps save a description and examples with the regex
        jQuery('#alias').attr('data-parsley-error-message', 'Must match '+regex);
        jQuery('#library-form').parsley();
        jQuery('#library-form').parsley().validate();
        validate_backend();
      },
      'doOnError': function(json) {
        alert(json.error);
      }
    });
};

function validate_backend() {
  updateWarning();

  if (true === jQuery('#library-form').parsley().isValid()) {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'validateLibraryAlias',
      {
        'alias': jQuery('#alias').val(),
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          if (json.response === "OK") {
            old_crappy_validation(jQuery('#library-form'));
          }
        },
        'doOnError': function(json) {
          alert(json.error);
        }
      });
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

// TODO remove this after finishing backend validation
function old_crappy_validation(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery(':text.validateable').length > 0) {
    jQuery(':text.validateable').each(function() {
      var result = Utils.validation.validate_input_field(this,'Library',ok);
      ok = result.okstatus;
      error += result.errormsg;
    });
  }

  if (!ok) {
    alert(error);
  }
  else {
    form.submit();
  }

  return ok;
}
