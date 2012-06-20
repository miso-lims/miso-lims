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

function validate_run(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery("select[name=sequencer]").length > 0) {
    if (jQuery('select[name=sequencer] :selected').val() == 0) {
      ok = false;
      error += "You have not chosen a Sequencer for this Run.\n";
    }
  }

  if (jQuery("input[name=platformType]").length > 0) {
    if (!jQuery('input[name=platformType]').is(':checked')) {
      error += "You have not chosen the Platform Type of the Run.\n";
      ok = false;
    }
  }

  if (jQuery(':text.validateable').length > 0) {
    jQuery(':text.validateable').each(function() {
      var result = validate_input_field(this, 'Run', ok);
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

function checkForCompletionDate() {
  var statusVal = jQuery('input[name=status\\.health]:checked').val();
  if (!isNullCheck(statusVal)) {
    if (statusVal === "Failed" || statusVal === "Stopped") {
      alert("You are manually setting a run to Stopped or Failed. Please remember to enter a Completion Date!");
      if (jQuery("#completionDate input").length == 0) {
        jQuery("#completionDate").html("<input type='text' name='status.completionDate' id='status.completionDate' value='" + jQuery('#completionDate').html() + "'>");
        addDatePicker("status\\.completionDate");
      }
    }
    else {
      if (jQuery("#status\\.completionDate").length > 0) {
        jQuery("#completionDate").html(jQuery("#status\\.completionDate").val());
      }
    }
  }
}