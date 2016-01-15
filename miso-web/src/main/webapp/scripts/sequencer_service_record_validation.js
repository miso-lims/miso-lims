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
function clean_service_record_fields() {
  jQuery('#service_record_form').find('input:text').each(function() {
    Utils.validation.clean_input_field(jQuery(this));
  });
};

// update warning message
function updateWarning() {
  if (true === jQuery('#service_record_form').parsley().isValid()) {
    jQuery('.bs-callout-info').removeClass('hidden');
    jQuery('.bs-callout-warning').addClass('hidden');
  } else {
    jQuery('.bs-callout-info').addClass('hidden');
    jQuery('.bs-callout-warning').removeClass('hidden');
  }
};

function validate_service_record() {
  clean_service_record_fields();
  
  jQuery('#service_record_form').parsley().destroy();
  
  jQuery('#title').attr('required', 'true');
  jQuery('#title').attr('data-parsley-maxlength', '50');
  
  jQuery('#details').attr('data-parsley-maxlength', '2000');
  
  jQuery('#servicedByName').attr('required', 'true');
  jQuery('#servicedByName').attr('data-parsley-maxlength', '30');
  
  jQuery('#phone').attr('data-parsley-maxlength', '12');
  
  jQuery('#servicedatepicker').attr('required', 'true');
  jQuery('#servicedatepicker').attr('data-date-format', 'DD/MM/YYYY');
  jQuery('#servicedatepicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
  jQuery('#servicedatepicker').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');
  
  jQuery('#shutdownTime').attr('data-date-format', 'DD/MM/YYYY hh:mm');
  jQuery('#shutdownTime').attr('data-parsley-pattern', Utils.validation.dateTimeRegex);
  jQuery('#shutdownTime').attr('data-parsley-error-message', 'Time must be of form DD/MM/YYYY hh:mm');
  
  jQuery('#restoredTime').attr('data-date-format', 'DD/MM/YYYY hh:mm');
  jQuery('#restoredTime').attr('data-parsley-pattern', Utils.validation.dateTimeRegex);
  jQuery('#restoredTime').attr('data-parsley-error-message', 'Time must be of form DD/MM/YYYY hh:mm');
  
  updateWarning();
  if (jQuery('#service_record_form').parsley().isValid() === true) {
    jQuery('#service_record_form').submit();
  }
}
