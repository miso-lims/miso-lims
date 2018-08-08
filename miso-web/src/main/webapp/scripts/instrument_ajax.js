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

var Instrument = Instrument || {

  validateInstrument: function() {
    Validate.cleanFields('#instrument_form');

    jQuery('#instrument_form').parsley().destroy();

    jQuery('#serialNumber').attr('data-parsley-maxlength', '30');

    jQuery('#name').attr('required', 'true');
    jQuery('#name').attr('data-parsley-maxlength', '30');

    jQuery('#ipAddress').attr('required', 'true');

    jQuery('#datecommissionedpicker').attr('data-date-format', 'YYYY-MM-DD');
    jQuery('#datecommissionedpicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#datecommissionedpicker').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

    jQuery('#datedecommissionedpicker').attr('data-date-format', 'YYYY-MM-DD');
    jQuery('#datedecommissionedpicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#datedecommissionedpicker').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

    jQuery('#upgradedInstrument').attr('type', 'number');
    jQuery('#upgradedInstrument').attr('data-parsley-error-message', 'Upgrade must refer to an existing instrument.');

    if (jQuery('input[name="status"]:checked').val() != "production") {
      jQuery('#datedecommissionedpicker').attr('required', 'true');
    } else {
      jQuery('#datedecommissionedpicker').removeAttr('required');
    }

    if (jQuery('input[name="status"]:checked').val() === "upgraded") {
      jQuery('#upgradedInstrument').attr('required', 'true');
      jQuery('#upgradedInstrument').attr('min', '1');
    } else {
      jQuery('#upgradedInstrument').removeAttr('required');
      jQuery('#upgradedInstrument').removeAttr('min');
    }

    jQuery('#instrument_form').parsley();
    jQuery('#instrument_form').parsley().validate();

    Validate.updateWarningOrSubmit('#instrument_form');
    return false;
  }

};

Instrument.ui = {

  hideStatusRowsReadOnly: function(decommissioned, upgraded, upgradedId, upgradedName) {
    if (!decommissioned) {
      jQuery("#decommissionedRow").hide();
    }
    if (!upgraded) {
      jQuery("#upgradedInstrumentRow").hide();
    } else {
      jQuery("#upgradedInstrumentLink").empty();
      jQuery("#upgradedInstrumentLink").append("<a href='/miso/instrument/" + upgradedId + "'>" + upgradedName + "</a>");
    }
  },

  showStatusRows: function() {
    switch (jQuery('input[name="status"]:checked').val()) {
    case "production":
      Instrument.ui.hideDecommissioned();
      Instrument.ui.hideUpgradedInstrument();
      break;
    case "retired":
      Instrument.ui.showDecommissioned();
      Instrument.ui.hideUpgradedInstrument();
      break;
    case "upgraded":
      Instrument.ui.showDecommissioned();
      Instrument.ui.showUpgradedInstrument();
      break;
    }
  },

  hideDecommissioned: function() {
    jQuery("#decommissionedRow").hide();
    jQuery("#datedecommissionedpicker").val("");
  },

  showDecommissioned: function() {
    if (jQuery("#datedecommissionedpicker").val() == "") {
      jQuery("#datedecommissionedpicker").val(jQuery.datepicker.formatDate(Utils.ui.goodDateFormat, new Date()));
    }
    jQuery("#decommissionedRow").show();
  },

  hideUpgradedInstrument: function() {
    jQuery("#upgradedInstrumentRow").hide();
    jQuery("#upgradedInstrument").val("");
  },

  showUpgradedInstrument: function() {
    jQuery("#upgradedInstrumentRow").show();
    Instrument.ui.updateUpgradedInstrumentLink();
  },

  updateUpgradedInstrumentLink: function() {
    jQuery("#upgradedInstrumentLink").empty();
    if (jQuery("#upgradedInstrument").val() != "" && jQuery("#upgradedInstrument").val() != 0) {
      jQuery("#upgradedInstrumentLink").append("<a href='/miso/instrument/" + jQuery("#upgradedInstrument").val() + "'>View</a>");
    }
  }
};
