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
function clean_sequencer_reference_fields() {
  jQuery('#sequencer_reference_form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#sequencer_reference_form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_sequencer_reference() {
  clean_sequencer_reference_fields();
  
  jQuery('#sequencer_reference_form').parsley().destroy();
  
  jQuery('#serialNumber').attr('data-parsley-maxlength', '30');
  
  jQuery('#name').attr('required', 'true');
  jQuery('#name').attr('data-parsley-maxlength', '30');
  
  jQuery('#ipAddress').attr('required', 'true');
  
  jQuery('#datecommissionedpicker').attr('data-date-format', 'DD/MM/YYYY');
  jQuery('#datecommissionedpicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
  jQuery('#datecommissionedpicker').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');
  
  jQuery('#datedecommissionedpicker').attr('data-date-format', 'DD/MM/YYYY');
  jQuery('#datedecommissionedpicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
  jQuery('#datedecommissionedpicker').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');
  
  jQuery('#upgradedSequencerReference').attr('type', 'number');
  jQuery('#upgradedSequencerReference').attr('data-parsley-error-message', 'Upgrade must refer to an existing sequencer.');
  
  if (jQuery('input[name="status"]:checked').val() != "production") {
    jQuery('#datedecommissionedpicker').attr('required', 'true');
  }
  else {
    jQuery('#datedecommissionedpicker').removeAttr('required');
  }
  
  if (jQuery('input[name="status"]:checked').val() === "upgraded") {
    jQuery('#upgradedSequencerReference').attr('required', 'true');
    jQuery('#upgradedSequencerReference').attr('min', '1');
  }
  else {
    jQuery('#upgradedSequencerReference').removeAttr('required');
    jQuery('#upgradedSequencerReference').removeAttr('min');
  }
  
  updateWarning();
  if (jQuery('#sequencer_reference_form').parsley().isValid() === true) {
    jQuery('#sequencer_reference_form').submit();
  }
}
