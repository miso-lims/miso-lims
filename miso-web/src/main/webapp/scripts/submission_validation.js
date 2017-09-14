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

function validate_submission(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery('#title').val() === "") {
    ok = false;
    error += "You have not entered a title for the Submission.\n";
  }

  if (jQuery('#alias').val() === "") {
    ok = false;
    error += "You have not entered an alias for the Submission.\n";
  }

  if (jQuery('#description').val() === "") {
    ok = false;
    error += "You have not entered a description of the Submission.\n";
  }

  if (jQuery("input[type='text']").length > 0) {
    jQuery("input[type='text']").each(function() {
      if (jQuery(this).val().indexOf("'") > -1 || jQuery(this).val().indexOf("\"") > -1) {
        ok = false;
        error += "You cannot use single or double quotes in the " + jQuery(this).attr("id") + " field and it cannot end with a space.\n";
      }
    });
  }

  if (!ok) {
    alert(error);
  }

  return ok;
}