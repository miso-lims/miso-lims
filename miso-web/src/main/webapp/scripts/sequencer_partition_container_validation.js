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

function validate_container(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (isNullCheck(jQuery('#identificationBarcode').val())) {
    ok = false;
    error += "You have not entered an ID barcode for the Container.\n";
  }

  if (jQuery(':text.validateable').length > 0) {
    jQuery(':text.validateable').each(function() {
      var result = validate_input_field(this, 'Container', ok);
      ok = result.okstatus;
      error += result.errormsg;
    })
  }

  if (jQuery('div[id^="studySelectDiv"]').length > 0) {
    if (!confirm("You haven't selected a study for one or more pools. Are you sure you still want to save?")) {
      ok = false;
      error += "Please select studies for all the pools added.\n"
    }
  }

  if (!ok) {
    alert(error);
  }

  return ok;
}
