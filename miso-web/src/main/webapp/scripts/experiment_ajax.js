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

var Experiment = Experiment || {
  validateExperiment: function () {
    Validate.cleanFields('#experiment-form');
    jQuery('#experiment-form').parsley().destroy();

    // Title input field validation
    jQuery('#title').attr('class', 'form-control');
    jQuery('#title').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    jQuery('#title').attr('data-parsley-required', 'true');
    jQuery('#title').attr('data-parsley-maxlength', '255');

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '100');

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    jQuery('#description').attr('data-parsley-maxlength', '255');

    jQuery('#experiment-form').parsley();
    jQuery('#experiment-form').parsley().validate();
    
    Validate.updateWarningOrSubmit('#experiment-form');
    return false;
  }
};

Experiment.ui = {
  wizardAddExperiment: function (form) {
    Fluxion.doAjax(
      'experimentWizardControllerHelperService',
      'addStudyExperiment',
      {'form': jQuery('#' + form).serializeArray(), 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        Utils.page.pageRedirect('/miso/study/' + json.studyId);
      }
    });
  },

  addExperimentForm: function (id) {
    Fluxion.doAjax(
      'experimentWizardControllerHelperService',
      'addExperimentForm',
      {'newid': id, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery('#experimentWizardForm').append(json.html);
      }
    });
  },

  createListingExperimentsTable: function () {
    jQuery('#listingExperimentsTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    jQuery.fn.dataTableExt.oSort['no-exp-asc'] = function (x, y) {
      var a = parseInt(x.replace(/^EXP/i, ""));
      var b = parseInt(y.replace(/^EXP/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-exp-desc'] = function (x, y) {
      var a = parseInt(x.replace(/^EXP/i, ""));
      var b = parseInt(y.replace(/^EXP/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'listExperimentsDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#listingExperimentsTable').html('');
        jQuery('#listingExperimentsTable').dataTable({
          "aaData": json.experimentsArray,
          "aoColumns": [
            { "sTitle": "Experiment Name", "sType": "no-exp"},
            { "sTitle": "Alias"},
            { "sTitle": "Description"},
            { "sTitle": "Platform"}
          ],
          "bJQueryUI": true,
          "iDisplayLength": 25,
          "sPaginationType": "full_numbers",
          "aaSorting": [
            [0, "desc"]
          ],
          "fnDrawCallback": function (oSettings) {
            jQuery('#listingExperimentsTable_paginate').find('.fg-button').removeClass('fg-button');
          }
        });
      }
      }
    );
  },

  confirmRemoveExperiment: function (id) {
    if (confirm("Are you sure you wish to remove this Experiment?")) {
      var obj = jQuery('#exp' + id);
      obj.remove();
    }
  }
};

Experiment.pool = {
  experimentSelectPool: function (p, newid) {
    var pool = jQuery(p);
    var id = '#selPool';
    if (!Utils.validation.isNullCheck(newid)) {
      id = '#selPool' + newid;
    }
    var poolSel = jQuery(id);
    if (poolSel.is(':empty')) {
      var newpool = pool.clone().appendTo(poolSel);
      newpool.append("<input type='hidden' id='pool" + pool.attr("bind") + "' name='pool" + newid + "' value='" + pool.attr("bind") + "'/>");
      newpool.remove('.pType');
      newpool.append("<span style='position: absolute; top: 0; right: 0;' onclick='Experiment.pool.confirmPoolRemove(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
      newpool.removeAttr("ondblclick");
      newpool.dblclick(function () {
        poolSel.html("");
      });
      poolSel.append(newpool);

      if (Utils.validation.isNullCheck(newid)) {
        poolSel.append("<input type='hidden' value='on' name='_pool'/>");
      }
    }
  },

  confirmPoolRemove: function (t) {
    if (confirm("Remove this pool?")) {
      jQuery(t).parent().remove();
    }
  },

  loadPoolsByPlatform: function (select, id) {
    if (Utils.validation.isNullCheck(id)) {
      jQuery('#poolList').html("<img src='/styles/images/ajax-loader.gif'/>");
    }
    else {
      jQuery('#poolList' + id).html("<img src='/styles/images/ajax-loader.gif'/>");
    }
    Fluxion.doAjax(
      'experimentWizardControllerHelperService',
      'loadPoolsByPlatform',
      {'platformId': jQuery(select).val(), 'newid': id, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        if (Utils.validation.isNullCheck(id)) {
          jQuery('#poolList').html("");
          jQuery('#poolList').html(json.html);
        }
        else {
          jQuery('#poolList' + id).html("");
          jQuery('#poolList' + id).html(json.html);
        }
      }
    });
  }
};

Experiment.kit = {
  lookupKitByIdentificationBarcode: function (barcode) {
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'lookupKitByIdentificationBarcode',
      {'barcode': barcode, 'url': ajaxurl},
      {'doOnSuccess': fillKitSelector} // ?? fillKitSelector is not defined anywhere in the project.
    );
  },

  lookupKitByLotNumber: function (lotNumber) {
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'lookupKitByLotNumber',
      {'lotNumber': lotNumber, 'url': ajaxurl},
      {'doOnSuccess': fillKitSelector} // ?? fillKitSelector is not defined anywhere in the project.
    );
  },

  lookupKitDescriptorByPartNumber: function (t) {
    var func = function () {
      Fluxion.doAjax(
        'experimentControllerHelperService',
        'lookupKitDescriptorByPartNumber',
        {'partNumber': t.value, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          jQuery("#kitDescriptor").val(json.id);
        }
      });
    };
    Utils.timer.timedFunc(func, 200);
  },

  kitDescriptorChange: function (s) {
    jQuery("#partNumber").val(jQuery("#" + s.id + " :selected").attr("partNumber"));
  },

  // library
  showLibraryKitDialog: function (experimentId, multiplexed) {
    var self = this;
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'getLibraryKitDescriptors',
      {'experimentId': experimentId, 'multiplexed': multiplexed, 'url': ajaxurl},
      {'doOnSuccess': self.processLibraryKitList}
    );
  },

  processLibraryKitList: function (json) {
    var self = this;

    var experimentId = json.experimentId;
    var multiplexed = json.multiplexed;

    var libraryKits = [];
    if (json.libraryKitDescriptors) {
      for (var i = 0; i < json.libraryKitDescriptors.length; i++) {
        libraryKits.push(json.libraryKitDescriptors[i]);
      }
    }

    var multiplexKits = [];
    if (multiplexed == "true" && json.multiplexKitDescriptors) {
      for (var j = 0; j < json.multiplexKitDescriptors.length; j++) {
        multiplexKits.push(json.multiplexKitDescriptors[j]);
      }
    }

    var dialogText = "<form id='addKitForm'>" +
                     "<fieldset class='dialog'>" +
                     "<label for='kitDescriptor'>Library Kit</label>" +
                     "<select name='kitDescriptor' id='kitDescriptor' class='text ui-widget-content ui-corner-all' onchange='Experiment.kit.kitDescriptorChange(this)'>";

    for (var k = 0; k < libraryKits.length; k++) {
      dialogText += "<option partNumber='" + libraryKits[k].partNumber + "' value='" + libraryKits[k].id + "'>" + libraryKits[k].name + "</option>";
    }

    dialogText += "</select><br/>" +
                  "<label for='partNumber'>Part Number / Barcode</label>" +
                  "<input type='text' name='partNumber' id='partNumber' onkeyup='Experiment.kit.lookupKitDescriptorByPartNumber(this);' class='text ui-widget-content ui-corner-all required' />";

    if (multiplexed == "true") {
      dialogText += "<label for='multiplexingKitType'>Multiplexing Kit</label>" +
                    "<select name='multiplexingKitType' id='multiplexingKitType' class='text ui-widget-content ui-corner-all'>";

      for (var m = 0; m < multiplexKits.length; m++) {
        dialogText += "<option partNumber='" + multiplexKits[m].partNumber + "' value='" + multiplexKits[m].id + "'>" + multiplexKits[m].name + "</option>";
      }
      dialogText += "</select><br/>" +
                    "<label for='multiplexingKitBarcode'>Multiplexing Kit Barcode</label>" +
                    "<input type='text' name='multiplexingKitBarcode' id='multiplexingKitBarcode' class='text ui-widget-content ui-corner-all required' />";
    }

    dialogText += "<br/>" +
                  "<label for='lotNumber'>Lot Number / Barcode</label>" +
                  "<input type='text' name='lotNumber' id='lotNumber' class='text ui-widget-content ui-corner-all required' />";

    dialogText += "</fieldset></form>";

    jQuery('#addKitDialog').html(dialogText);

    jQuery('#addKitDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Library Kit": function () {
          var b = this;
          if (jQuery("#addKitForm").validate().form()) {
            if (multiplexed == "true") {
              self.addLibraryKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), jQuery('#multiplexingKitType').val(), jQuery('#multiplexingKitBarcode').val(), jQuery('#lotNumber').val());
            }
            else {
              self.addLibraryKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), undefined, undefined, jQuery('#lotNumber').val());
            }
            jQuery(b).dialog('close');
          }
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
    jQuery('#addKitDialog').parent().css("width", "auto");
  },

  addLibraryKit: function (experimentId, kitDescriptorId, partNumber, mplexKitType, mplexKitBarcode, lotNumber) {
    if (mplexKitType === undefined || mplexKitBarcode === undefined) {
      Fluxion.doAjax(
        'experimentControllerHelperService',
        'addLibraryKit',
        {'experimentId': experimentId, 'kitDescriptor': kitDescriptorId, 'partNumber': partNumber, 'lotNumber': lotNumber, 'url': ajaxurl},
        {'doOnSuccess': function () {
          alert("Added");
        }
      });
    }
  },

  // empcr
  showEmPcrKitDialog: function (experimentId) {
    var self = this;
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'getEmPcrKitDescriptors',
      {'experimentId': experimentId, 'url': ajaxurl},
      {'doOnSuccess': self.processEmPcrKitList}
    );
  },

  processEmPcrKitList: function (json) {
    var self = this;

    var experimentId = json.experimentId;

    var kits = [];
    if (json.emPcrKitDescriptors) {
      for (var i = 0; i < json.emPcrKitDescriptors.length; i++) {
        kits.push(json.emPcrKitDescriptors[i]);
      }
    }

    var dialogText = "<form id='addKitForm'>" +
                     "<fieldset class='dialog'>" +
                     "<label for='kitDescriptor'>EmPCR Kit</label>" +
                     "<select name='kitDescriptor' id='kitDescriptor' class='text ui-widget-content ui-corner-all' onchange='Experiment.kit.kitDescriptorChange(this)'>";

    for (var j = 0; j < kits.length; j++) {
      dialogText += "<option partNumber='" + kits[j].partNumber + "' value='" + kits[j].id + "'>" + kits[j].name + "</option>";
    }

    dialogText += "</select><br/>" +
                  "<label for='partNumber'>Part Number / Barcode</label>" +
                  "<input type='text' name='partNumber' id='partNumber' onkeyup='Experiment.kit.lookupKitDescriptorByPartNumber(this);' class='text ui-widget-content ui-corner-all required' />";

    dialogText += "<br/>" +
                  "<label for='lotNumber'>Lot Number / Barcode</label>" +
                  "<input type='text' name='lotNumber' id='lotNumber' class='text ui-widget-content ui-corner-all required' />";

    dialogText += "</fieldset></form>";

    jQuery('#addKitDialog').html(dialogText);

    jQuery('#addKitDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add EmPCR Kit": function () {
          var b = this;
          if (jQuery("#addKitForm").validate().form()) {
            self.addEmPcrKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), jQuery('#lotNumber').val());
            jQuery(b).dialog('close');
          }
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  addEmPcrKit: function (experimentId, kitDescriptorId, partNumber, lotNumber) {
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'addEmPcrKit',
      {'experimentId': experimentId, 'kitDescriptor': kitDescriptorId, 'partNumber': partNumber, 'lotNumber': lotNumber, 'url': ajaxurl},
      {'doOnSuccess': function () {
        alert("Added");
      }
    });
  },

  //clustering
  showClusteringKitDialog: function (experimentId) {
    var self = this;
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'getClusteringKitDescriptors',
      {'experimentId': experimentId, 'url': ajaxurl},
      {'doOnSuccess': self.processClusteringKitList}
    );
  },

  processClusteringKitList: function (json) {
    var self = this;

    var experimentId = json.experimentId;

    var kits = [];
    if (json.clusteringKitDescriptors) {
      for (var i = 0; i < json.clusteringKitDescriptors.length; i++) {
        kits.push(json.clusteringKitDescriptors[i]);
      }
    }

    var dialogText = "<form id='addKitForm'>" +
                     "<fieldset class='dialog'>" +
                     "<label for='kitDescriptor'>Clustering Kit</label>" +
                     "<select name='kitDescriptor' id='kitDescriptor' class='text ui-widget-content ui-corner-all' onchange='Experiment.kit.kitDescriptorChange(this)'>";

    for (var j = 0; j < kits.length; j++) {
      dialogText += "<option partNumber='" + kits[j].partNumber + "' value='" + kits[j].id + "'>" + kits[j].name + "</option>";
    }

    dialogText += "</select><br/>" +
                  "<label for='partNumber'>Part Number / Barcode</label>" +
                  "<input type='text' name='partNumber' id='partNumber' onkeyup='Experiment.kit.lookupKitDescriptorByPartNumber(this);' class='text ui-widget-content ui-corner-all required' />";

    dialogText += "<br/>" +
                  "<label for='lotNumber'>Lot Number / Barcode</label>" +
                  "<input type='text' name='lotNumber' id='lotNumber' class='text ui-widget-content ui-corner-all required' />";

    dialogText += "</fieldset></form>";

    jQuery('#addKitDialog').html(dialogText);

    jQuery('#addKitDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Clustering Kit": function () {
          var b = this;
          if (jQuery("#addKitForm").validate().form()) {
            self.addClusteringKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), jQuery('#lotNumber').val());
            jQuery(b).dialog('close');
          }
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  addClusteringKit: function (experimentId, kitDescriptorId, partNumber, lotNumber) {
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'addClusteringKit',
      {'experimentId': experimentId, 'kitDescriptor': kitDescriptorId, 'partNumber': partNumber, 'lotNumber': lotNumber, 'url': ajaxurl},
      {'doOnSuccess': function () {
        alert("Added");
      }
    });
  },

  //sequencing
  showSequencingKitDialog: function (experimentId) {
    var self = this;
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'getSequencingKitDescriptors',
      {'experimentId': experimentId, 'url': ajaxurl},
      {'doOnSuccess': self.processSequencingKitList}
    );
  },

  processSequencingKitList: function (json) {
    var self = this;

    var experimentId = json.experimentId;

    var kits = [];
    if (json.sequencingKitDescriptors) {
      for (var i = 0; i < json.sequencingKitDescriptors.length; i++) {
        kits.push(json.sequencingKitDescriptors[i]);
      }
    }

    var dialogText = "<form id='addKitForm'>" +
                     "<fieldset class='dialog'>" +
                     "<label for='kitDescriptor'>Sequencing Kit</label>" +
                     "<select name='kitDescriptor' id='kitDescriptor' class='text ui-widget-content ui-corner-all' onchange='Experiment.kit.kitDescriptorChange(this)'>";

    for (var j = 0; j < kits.length; j++) {
      dialogText += "<option partNumber='" + kits[j].partNumber + "' value='" + kits[j].id + "'>" + kits[j].name + "</option>";
    }

    dialogText += "</select><br/>" +
                  "<label for='partNumber'>Part Number / Barcode</label>" +
                  "<input type='text' name='partNumber' id='partNumber' onkeyup='Experiment.kit.lookupKitDescriptorByPartNumber(this);' class='text ui-widget-content ui-corner-all required' />";

    dialogText += "<br/>" +
                  "<label for='lotNumber'>Lot Number / Barcode</label>" +
                  "<input type='text' name='lotNumber' id='lotNumber' class='text ui-widget-content ui-corner-all required' />";

    dialogText += "</fieldset></form>";

    jQuery('#addKitDialog').html(dialogText);

    jQuery('#addKitDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Sequencing Kit": function () {
          var b = this;

          if (jQuery("#addKitForm").validate().form()) {
            self.addSequencingKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), jQuery('#lotNumber').val());
            jQuery(b).dialog('close');
          }
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  addSequencingKit: function (experimentId, kitDescriptorId, partNumber, lotNumber) {
    Fluxion.doAjax(
      'experimentControllerHelperService',
      'addSequencingKit',
      {'experimentId': experimentId, 'kitDescriptor': kitDescriptorId, 'partNumber': partNumber, 'lotNumber': lotNumber, 'url': ajaxurl},
      {'doOnSuccess': function () {
        alert("Added");
      }
    });
  }
};
