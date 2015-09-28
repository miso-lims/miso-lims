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

function validate_pool(form) {
  var ok = true;
  var error = "Please correct the following errors:\n";
  if (jQuery('input[name=concentration]').length > 0) {
    if (jQuery('input[name=concentration]').val().length == 0) {
      error += "You have not entered the Concentration of the Pool.\n";
      ok = false;
    }
  }

  var numberExp = /[.*0-9]/;
  if (!numberExp.test(jQuery('input[name=concentration]').val())) {
    error += "The Concentration of the Pool can only be numbers.\n";
    ok = false;
  }

  if (jQuery('input[name=creationDate]').length > 0) {
    if (jQuery('input[name=creationDate]').val().length == 0) {
      error += "You have not entered the Creation Date of the Pool.\n";
      ok = false;
    }
  }

  if (!jQuery('#dillist').html().trim()) {
    error += "You have selected no dilutions for this Pool.\n";
    ok = false;
  }

  if (!ok) {
    alert(error);
  }
  else {
    form.submit();
  }

  return ok;
}