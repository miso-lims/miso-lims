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
    
    jQuery('#sequencer_reference_form').parsley();
    jQuery('#sequencer_reference_form').parsley().validate();
    
    Validate.updateWarningOrSubmit('#sequencer_reference_form');
    return false;
  }
  
};

Sequencer.ui = {
  insertSequencerReferenceRow : function() {
    var self = this;
    Fluxion.doAjax(
      'sequencerReferenceControllerHelperService',
      'listPlatforms',
      {'url':ajaxurl},
      {'doOnSuccess':self.processSequencerReferenceRow}
    );
  },

  processSequencerReferenceRow : function(json) {
    jQuery('#sequencerReferenceTable')[0].insertRow(1);

    var column1 = jQuery('#sequencerReferenceTable')[0].rows[1].insertCell(-1);
    column1.innerHTML = "<i>Unsaved</i>";
    var column2 = jQuery('#sequencerReferenceTable')[0].rows[1].insertCell(-1);
    column2.innerHTML = "<input id='sequencername' name='sequencername' type='text'/>";
    var column3 = jQuery('#sequencerReferenceTable')[0].rows[1].insertCell(-1);
    column3.innerHTML = "<select id='platforms' name='platform'>" +json.platforms+ "</select>";
    var column4 = jQuery('#sequencerReferenceTable')[0].rows[1].insertCell(-1);
    column4.innerHTML = "<input id='server' name='server' type='text' onkeyup='Sequencer.ui.validateServer(this)'/>";
    var column5 = jQuery('#sequencerReferenceTable')[0].rows[1].insertCell(-1);
    column5.innerHTML = "<div id='available'></div>";
    var column6 = jQuery('#sequencerReferenceTable')[0].rows[1].insertCell(-1);
    column6.id = "addTd";
    column6.innerHTML = "Add";
  },

  validateServer : function(t) {
    jQuery('#available')[0].innerHTML="<div align='center'><img src='../../styles/images/ajax-loader.gif'/></div>";

    if (t.value != t.lastValue) {
      if (t.timer) {
        clearTimeout(t.timer);
      }

      t.timer = setTimeout(function () {
        Fluxion.doAjax(
          'sequencerReferenceControllerHelperService',
          'checkServerAvailability',
          {'server':t.value, 'url':ajaxurl},
          {"doOnSuccess": function(json) {
            jQuery('#available')[0].innerHTML = json.html;
            if (json.html == "OK") {
              jQuery('#available')[0].setAttribute("style", "background-color:green");
              jQuery('#addTd')[0].innerHTML = "<a href='javascript:void(0);' onclick='Sequencer.ui.addSequencerReference();'/>Add</a>";
            }
            else {
              jQuery('#available')[0].setAttribute("style", "background-color:red");
            }
          }
        });
      }, 200);
      t.lastValue = t.value;
    }
  },

  addSequencerReference : function() {
    var f = Utils.mappifyForm("addReferenceForm");
    Fluxion.doAjax(
      'sequencerReferenceControllerHelperService',
      'addSequencerReference',
      {
        'name':f.sequencername,
        'platform':f.platform,
        'server':f.server,
        'url':ajaxurl},
      {'doOnSuccess':Utils.page.pageReload}
    );
  },

  deleteSequencerReference : function(refId, successfunc) {
    if (confirm("Are you sure you really want to delete sequencer reference "+refId+"? This operation is permanent!")) {
      Fluxion.doAjax(
        'sequencerReferenceControllerHelperService',
        'deleteSequencerReference',
        {'refId':refId, 'url':ajaxurl},
        {'doOnSuccess':function(json) {
            successfunc();
          }
        }
      );
    }
  },
  
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
      jQuery("#datedecommissionedpicker").val(jQuery.datepicker.formatDate('dd/mm/yy', new Date()));
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
  
  createListingSequencersTable : function() {
    jQuery('#listingSequencersTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    
    Fluxion.doAjax(
      'sequencerReferenceControllerHelperService',
      'listSequencersDataTable',
      {'url': ajaxurl},
      {
        'doOnSuccess': function (json) {
          jQuery('#listingSequencersTable').html('');
          jQuery('#listingSequencersTable').dataTable({
            "aaData": json.array,
            "aoColumns": [
              { "sTitle": "Sequencer Name"},
              { "sTitle": "Platform"},
              { "sTitle": "Model"},
              { "sTitle": "Status"},
              { "sTitle": "Last Serviced"},
              { "sTitle": "Active", "bVisible": false}
            ],
            "bJQueryUI": true,
            "bAutoWidth": false,
            "iDisplayLength": 25,
            "aaSorting": [
              [0, "asc"]
            ]
          }).fnFilter(true, 5);
          // filter for active sequencers
        }
      }
    )
  }  
};
