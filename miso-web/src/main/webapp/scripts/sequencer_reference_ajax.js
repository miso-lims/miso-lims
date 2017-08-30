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

var Sequencer = Sequencer || {
  
  validateSequencerReference: function() {
    Validate.cleanFields('#sequencer_reference_form');
    
    jQuery('#sequencer_reference_form').parsley().destroy();
    
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
    
    jQuery('#sequencer_reference_form').parsley();
    jQuery('#sequencer_reference_form').parsley().validate();
    
    Validate.updateWarningOrSubmit('#sequencer_reference_form');
    return false;
  }
  
};

Sequencer.ui = {
  
  hideStatusRowsReadOnly : function(decommissioned, upgraded, upgradedId, upgradedName) {
    if (!decommissioned) {
      jQuery("#decommissionedRow").hide();
    }
    if (!upgraded) {
      jQuery("#upgradedReferenceRow").hide();
    }
    else {
      jQuery("#upgradedSequencerReferenceLink").empty();
      jQuery("#upgradedSequencerReferenceLink").append("<a href='/miso/sequencer/" + upgradedId + "'>" + upgradedName + "</a>");
    }
  },
  
  showStatusRows : function() {
    switch(jQuery('input[name="status"]:checked').val()) {
      case "production":
        Sequencer.ui.hideDecommissioned();
        Sequencer.ui.hideUpgradedSequencerReference();
        break;
      case "retired":
        Sequencer.ui.showDecommissioned();
        Sequencer.ui.hideUpgradedSequencerReference();
        break;
      case "upgraded":
        Sequencer.ui.showDecommissioned();
        Sequencer.ui.showUpgradedSequencerReference();
        break;
    }
  },
  
  hideDecommissioned : function() {
    jQuery("#decommissionedRow").hide();
    jQuery("#datedecommissionedpicker").val("");
  },
  
  showDecommissioned : function() {
    if (jQuery("#datedecommissionedpicker").val() == "") {
      jQuery("#datedecommissionedpicker").val(jQuery.datepicker.formatDate(Utils.ui.goodDateFormat, new Date()));
    }
    jQuery("#decommissionedRow").show();
  },
  
  hideUpgradedSequencerReference : function() {
    jQuery("#upgradedReferenceRow").hide();
    jQuery("#upgradedSequencerReference").val("");
  },
  
  showUpgradedSequencerReference : function() {
    jQuery("#upgradedReferenceRow").show();
    Sequencer.ui.updateUpgradedSequencerReferenceLink();
  },
  
  updateUpgradedSequencerReferenceLink : function() {
    jQuery("#upgradedSequencerReferenceLink").empty();
    if (jQuery("#upgradedSequencerReference").val() != "" && jQuery("#upgradedSequencerReference").val() != 0) {
      jQuery("#upgradedSequencerReferenceLink").append("<a href='/miso/sequencer/" + jQuery("#upgradedSequencerReference").val() + "'>View</a>");
    }
  },
  
  deleteServiceRecord : function(recordId, successfunc) {
    if (confirm("Are you sure you really want to delete service record "+recordId+"? This operation is permanent!")) {
      Fluxion.doAjax(
        'serviceRecordControllerHelperService',
        'deleteServiceRecord',
        {'recordId':recordId, 'url':ajaxurl},
        {'doOnSuccess':function(json) {
            successfunc();
          }
        }
      );
    }
  },
};
