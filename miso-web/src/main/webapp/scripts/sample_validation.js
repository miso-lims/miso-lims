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
  jQuery.listen('parsley:field:validate', function () {
    updateWarning();
  });
});

// Trim whitespace from input fields
function clean_sample_fields() {
  jQuery('#sample-form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#sample-form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_sample() {
  clean_sample_fields();
  // Have to manually add attributes to all form elements because form elements are dynamically generated :/
  //jQuery('#alias').attr('data-parsley-whitespace', 'trim');
  jQuery('#sample-form').parsley().destroy();

  // Alias input field validation
  jQuery('#alias').attr('class', 'form-control');
  jQuery('#alias').attr('data-parsley-required', 'true');
  jQuery('#alias').attr('data-parsley-maxlength', '100');

  // Description input field validation
  jQuery('#description').attr('class', 'form-control');
  jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
  jQuery('#description').attr('data-parsley-required', 'true');
  jQuery('#description').attr('data-parsley-maxlength', '100');

  // Checkbox validation: ensure a checkbox is selected (assumes there is a project 1, no other way to check because of dynamic
  // generation)
  jQuery('#project1').attr('data-parsley-mincheck', '1');
  jQuery('#project1').attr('required', 'true');
  jQuery('#project1').attr('data-parsley-error-message', 'You must select a project.');
  jQuery('#project1').attr('data-parsley-errors-container', '#projectError');
  jQuery('#project1').attr('data-parsley-class-handler', '#projectlist');

  // Date of Receipt validation: ensure date is of correct form
  jQuery('#receiveddatepicker').attr('class', 'form-control');
  jQuery('#receiveddatepicker').attr('data-date-format', 'DD/MM/YYYY');
  jQuery('#receiveddatepicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
  jQuery('#receiveddatepicker').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');

  // Scientific Name validation
  jQuery('#scientificName').attr('class', 'form-control');
  jQuery('#scientificName').attr('data-parsley-required', 'true');
  jQuery('#scientificName').attr('data-parsley-maxlength', '100');
  jQuery('#scientificName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

  Fluxion.doAjax(
    'sampleControllerHelperService',
    'getSampleAliasRegex',
    {
      'url': ajaxurl
    },
    {
      'doOnSuccess': function(json) {
        // very hacky TODO: fix Fluxion
        var regex = json.aliasRegex.split(' ').join('+');
        jQuery('#alias').attr('data-parsley-pattern', regex);
        // TODO: better error message than a regex..?
        //       perhaps save a description and examples with the regex
        jQuery('#alias').attr('data-parsley-error-message', 'Must match '+regex);
        jQuery('#sample-form').parsley();
        jQuery('#sample-form').parsley().validate();
        validate_backend();
      },
      'doOnError': function(json) {
        alert(json.error);
      }
    });
};

function validate_backend() {
  updateWarning();

  // if valid, then submit form
  if (true === jQuery('#sample-form').parsley().isValid()) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'validateSampleAlias',
      {
        'alias': jQuery('#alias').val(),
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          if (json.response === "OK") {
            old_crappy_validation(jQuery('#sample-form'));
          }
        },
        'doOnError': function(json) {
          alert(json.error);
        }
      });
  }
};

// TODO remove this after finishing backend validation
function old_crappy_validation(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery(':text.validateable').length > 0) {
    jQuery(':text.validateable').each(function() {
      var result = Utils.validation.validate_input_field(this, 'Sample', ok);
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

function validate_library_qcs(json) {
  var ok = true;
  for (var i = 0; i < json.length; i++) {
    if (!json[i].results.match(/[0-9\.]+/) ||
        !json[i].insertSize.match(/[0-9]+/) ||
        Utils.validation.isNullCheck(json[i].qcDate)) ok = false;
  }
  return ok;
}

function validate_library_dilutions(json) {
  var ok = true;
  for (var i = 0; i < json.length; i++) {
    if (!json[i].results.match(/[0-9\.]+/) ||
        Utils.validation.isNullCheck(json[i].dilutionDate)) ok = false;
  }
  return ok;
}

var validate_ncbi_taxon = function() {
  Fluxion.doAjax(
    'sampleControllerHelperService',
    'lookupNCBIScientificName',
  {'scientificName':jQuery('#scientificName').val(), 'url':ajaxurl},
  {
    'doOnSuccess':jQuery('#scientificName').removeClass().addClass("ok"),
    'doOnError':function(json) { jQuery('#scientificName').removeClass().addClass("error"); alert(json.error); }
  }
  );
}
