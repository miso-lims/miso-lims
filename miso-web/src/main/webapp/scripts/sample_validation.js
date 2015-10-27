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

function validate_sample(form) {
  Fluxion.doAjax(
    'sampleControllerHelperService',
    'validateSampleAlias',
  {'alias':jQuery('#alias').val(), 'url':ajaxurl},
  {
    'doOnSuccess': function(json) { if (json.response === "OK") { process_validate_sample(form); }},
    'doOnError':function(json) { alert(json.error); }
  }
  );
}

function process_validate_sample(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery(':text.validateable').length > 0) {
    jQuery(':text.validateable').each(function() {
      var result = Utils.validation.validate_input_field(this, 'Sample', ok);
      ok = result.okstatus;
      error += result.errormsg;
    })
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
