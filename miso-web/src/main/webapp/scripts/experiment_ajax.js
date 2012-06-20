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

function experimentSelectPool(p) {
  var pool = jQuery(p);
  if (jQuery('#selPool').is(':empty')) {
    var newpool = pool.clone().appendTo(jQuery(jQuery('#selPool')));
    newpool.append("<input type='hidden' id='pool"+pool.attr("bind")+"' name='pool' value='"+pool.attr("bind")+"'/>");
    newpool.removeAttr("ondblclick");
    newpool.dblclick(function () {
      jQuery('#selPool').html("");
    });
    jQuery('#selPool').append(newpool);
    jQuery('#selPool').append("<input type='hidden' value='on' name='_pool'/>");
  }
}

function listPoolsByPlatformType(form) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'listPoolsByPlatformType',
  {'platformtype':form.value, 'url':ajaxurl},
  {'doOnSuccess':
          function(json) {
            jQuery('#list_1').html(json.pools);
          }
  });
}

function lookupKitByIdentificationBarcode(barcode) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'lookupKitByIdentificationBarcode',
  {'barcode':barcode, 'url':ajaxurl},
  {'doOnSuccess':fillKitSelector}
          );
}

function lookupKitByLotNumber(barcode) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'lookupKitByLotNumber',
  {'lotNumber':lotNumber, 'url':ajaxurl},
  {'doOnSuccess':fillKitSelector}
          );
}

function lookupKitDescriptorByPartNumber(t) {
  var func = function () {
    Fluxion.doAjax(
            'experimentControllerHelperService',
            'lookupKitDescriptorByPartNumber',
    {'partNumber':t.value, 'url':ajaxurl},
    {'doOnSuccess':function(json) {
      jQuery("#kitDescriptor").val(json.id);
    }
    });
  };
  timedFunc(func, 200);
}

function kitDescriptorChange(s) {
  jQuery("#partNumber").val(jQuery("#" + s.id + " :selected").attr("partNumber"));
}

// library
function showLibraryKitDialog(experimentId, multiplexed) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'getLibraryKitDescriptors',
  {'experimentId':experimentId, 'multiplexed':multiplexed, 'url':ajaxurl},
  {'doOnSuccess':processLibraryKitList}
          );
}

var processLibraryKitList = function(json) {
  var experimentId = json.experimentId;
  var multiplexed = json.multiplexed;

  var libraryKits = new Array();
  if (json.libraryKitDescriptors) {
    for (var i = 0; i < json.libraryKitDescriptors.length; i++) {
      libraryKits.push(json.libraryKitDescriptors[i]);
    }
  }

  var multiplexKits = new Array();
  if (multiplexed == "true" && json.multiplexKitDescriptors) {
    for (var i = 0; i < json.multiplexKitDescriptors.length; i++) {
      multiplexKits.push(json.multiplexKitDescriptors[i]);
    }
  }

  var dialogText = "<form id='addKitForm'>" +
                   "<fieldset class='dialog'>" +
                   "<label for='kitDescriptor'>Library Kit</label>" +
                   "<select name='kitDescriptor' id='kitDescriptor' class='text ui-widget-content ui-corner-all' onchange='kitDescriptorChange(this)'>";

  for (var i = 0; i < libraryKits.length; i++) {
    dialogText += "<option partNumber='" + libraryKits[i].partNumber + "' value='" + libraryKits[i].id + "'>" + libraryKits[i].name + "</option>";
  }

  dialogText += "</select><br/>" +
                "<label for='partNumber'>Part Number / Barcode</label>" +
                "<input type='text' name='partNumber' id='partNumber' onkeyup='lookupKitDescriptorByPartNumber(this);' class='text ui-widget-content ui-corner-all required' />";

  if (multiplexed == "true") {
    dialogText += "<label for='multiplexingKitType'>Multiplexing Kit</label>" +
                  "<select name='multiplexingKitType' id='multiplexingKitType' class='text ui-widget-content ui-corner-all'>";

    for (var i = 0; i < multiplexKits.length; i++) {
      dialogText += "<option partNumber='" + multiplexKits[i].partNumber + "' value='" + multiplexKits[i].id + "'>" + multiplexKits[i].name + "</option>";
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

  jQuery(function() {
    jQuery('#addKitDialog').dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Library Kit": function() {
          var b = this;

          if (jQuery("#addKitForm").validate().form()) {
            if (multiplexed == "true") {
              addLibraryKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), jQuery('#multiplexingKitType').val(), jQuery('#multiplexingKitBarcode').val(), jQuery('#lotNumber').val());
            }
            else {
              addLibraryKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), undefined, undefined, jQuery('#lotNumber').val());
            }
            jQuery(b).dialog('close');
          }
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  });
  jQuery('#addKitDialog').dialog('open');
  jQuery('#addKitDialog').parent().css("width", "auto");
};

function addLibraryKit(experimentId, kitDescriptorId, partNumber, mplexKitType, mplexKitBarcode, lotNumber) {
  if (mplexKitType === undefined || mplexKitBarcode === undefined) {
    Fluxion.doAjax(
            'experimentControllerHelperService',
            'addLibraryKit',
    {'experimentId':experimentId, 'kitDescriptor':kitDescriptorId, 'partNumber':partNumber, 'lotNumber':lotNumber, 'url':ajaxurl},
    {'doOnSuccess':function(json) {
      alert("added");
    }
    });
  }
  else {

  }
}

// empcr
function showEmPcrKitDialog(experimentId) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'getEmPcrKitDescriptors',
  {'experimentId':experimentId, 'url':ajaxurl},
  {'doOnSuccess':processEmPcrKitList}
          );
}

var processEmPcrKitList = function(json) {
  var experimentId = json.experimentId;

  var kits = new Array();
  if (json.emPcrKitDescriptors) {
    for (var i = 0; i < json.emPcrKitDescriptors.length; i++) {
      kits.push(json.emPcrKitDescriptors[i]);
    }
  }

  var dialogText = "<form id='addKitForm'>" +
                   "<fieldset class='dialog'>" +
                   "<label for='kitDescriptor'>EmPCR Kit</label>" +
                   "<select name='kitDescriptor' id='kitDescriptor' class='text ui-widget-content ui-corner-all' onchange='kitDescriptorChange(this)'>";

  for (var i = 0; i < kits.length; i++) {
    dialogText += "<option partNumber='" + kits[i].partNumber + "' value='" + kits[i].id + "'>" + kits[i].name + "</option>";
  }

  dialogText += "</select><br/>" +
                "<label for='partNumber'>Part Number / Barcode</label>" +
                "<input type='text' name='partNumber' id='partNumber' onkeyup='lookupKitDescriptorByPartNumber(this);' class='text ui-widget-content ui-corner-all required' />";

  dialogText += "<br/>" +
                "<label for='lotNumber'>Lot Number / Barcode</label>" +
                "<input type='text' name='lotNumber' id='lotNumber' class='text ui-widget-content ui-corner-all required' />";

  dialogText += "</fieldset></form>";

  jQuery('#addKitDialog').html(dialogText);

  jQuery(function() {
    jQuery('#addKitDialog').dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add EmPCR Kit": function() {
          var b = this;

          if (jQuery("#addKitForm").validate().form()) {
            addEmPcrKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), jQuery('#lotNumber').val());
            jQuery(b).dialog('close');
          }
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  });
  jQuery('#addKitDialog').dialog('open');
};

function addEmPcrKit(experimentId, kitDescriptorId, partNumber, lotNumber) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'addEmPcrKit',
  {'experimentId':experimentId, 'kitDescriptor':kitDescriptorId, 'partNumber':partNumber, 'lotNumber':lotNumber, 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    alert("added");
  }
  });
}

//clustering
function showClusteringKitDialog(experimentId) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'getClusteringKitDescriptors',
  {'experimentId':experimentId, 'url':ajaxurl},
  {'doOnSuccess':processClusteringKitList}
          );
}

var processClusteringKitList = function(json) {
  var experimentId = json.experimentId;

  var kits = new Array();
  if (json.clusteringKitDescriptors) {
    for (var i = 0; i < json.clusteringKitDescriptors.length; i++) {
      kits.push(json.clusteringKitDescriptors[i]);
    }
  }

  var dialogText = "<form id='addKitForm'>" +
                   "<fieldset class='dialog'>" +
                   "<label for='kitDescriptor'>Clustering Kit</label>" +
                   "<select name='kitDescriptor' id='kitDescriptor' class='text ui-widget-content ui-corner-all' onchange='kitDescriptorChange(this)'>";

  for (var i = 0; i < kits.length; i++) {
    dialogText += "<option partNumber='" + kits[i].partNumber + "' value='" + kits[i].id + "'>" + kits[i].name + "</option>";
  }

  dialogText += "</select><br/>" +
                "<label for='partNumber'>Part Number / Barcode</label>" +
                "<input type='text' name='partNumber' id='partNumber' onkeyup='lookupKitDescriptorByPartNumber(this);' class='text ui-widget-content ui-corner-all required' />";

  dialogText += "<br/>" +
                "<label for='lotNumber'>Lot Number / Barcode</label>" +
                "<input type='text' name='lotNumber' id='lotNumber' class='text ui-widget-content ui-corner-all required' />";

  dialogText += "</fieldset></form>";

  jQuery('#addKitDialog').html(dialogText);

  jQuery(function() {
    jQuery('#addKitDialog').dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Clustering Kit": function() {
          var b = this;

          if (jQuery("#addKitForm").validate().form()) {
            addClusteringKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), jQuery('#lotNumber').val());
            jQuery(b).dialog('close');
          }
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  });
  jQuery('#addKitDialog').dialog('open');
};

function addClusteringKit(experimentId, kitDescriptorId, partNumber, lotNumber) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'addClusteringKit',
  {'experimentId':experimentId, 'kitDescriptor':kitDescriptorId, 'partNumber':partNumber, 'lotNumber':lotNumber, 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    alert("added");
  }
  });
}

//sequencing
function showSequencingKitDialog(experimentId) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'getSequencingKitDescriptors',
  {'experimentId':experimentId, 'url':ajaxurl},
  {'doOnSuccess':processSequencingKitList}
          );
}

var processSequencingKitList = function(json) {
  var experimentId = json.experimentId;

  var kits = new Array();
  if (json.sequencingKitDescriptors) {
    for (var i = 0; i < json.sequencingKitDescriptors.length; i++) {
      kits.push(json.sequencingKitDescriptors[i]);
    }
  }

  var dialogText = "<form id='addKitForm'>" +
                   "<fieldset class='dialog'>" +
                   "<label for='kitDescriptor'>Sequencing Kit</label>" +
                   "<select name='kitDescriptor' id='kitDescriptor' class='text ui-widget-content ui-corner-all' onchange='kitDescriptorChange(this)'>";

  for (var i = 0; i < kits.length; i++) {
    dialogText += "<option partNumber='" + kits[i].partNumber + "' value='" + kits[i].id + "'>" + kits[i].name + "</option>";
  }

  dialogText += "</select><br/>" +
                "<label for='partNumber'>Part Number / Barcode</label>" +
                "<input type='text' name='partNumber' id='partNumber' onkeyup='lookupKitDescriptorByPartNumber(this);' class='text ui-widget-content ui-corner-all required' />";

  dialogText += "<br/>" +
                "<label for='lotNumber'>Lot Number / Barcode</label>" +
                "<input type='text' name='lotNumber' id='lotNumber' class='text ui-widget-content ui-corner-all required' />";

  dialogText += "</fieldset></form>";

  jQuery('#addKitDialog').html(dialogText);

  jQuery(function() {
    jQuery('#addKitDialog').dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Sequencing Kit": function() {
          var b = this;

          if (jQuery("#addKitForm").validate().form()) {
            addSequencingKit(experimentId, jQuery('#kitDescriptor').val(), jQuery('#partNumber').val(), jQuery('#lotNumber').val());
            jQuery(b).dialog('close');
          }
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  });
  jQuery('#addKitDialog').dialog('open');
};

function addSequencingKit(experimentId, kitDescriptorId, partNumber, lotNumber) {
  Fluxion.doAjax(
          'experimentControllerHelperService',
          'addSequencingKit',
  {'experimentId':experimentId, 'kitDescriptor':kitDescriptorId, 'partNumber':partNumber, 'lotNumber':lotNumber, 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    alert("added");
  }
  });
}

function wizardAddExperiment(form) {
  Fluxion.doAjax(
          'experimentWizardControllerHelperService',
          'addStudyExperiment',
  {'form':jQuery('#' + form).serializeArray(), 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    pageRedirect('/miso/study/' + json.studyId);
  }
  });
}

function addExperimentForm(id) {
  Fluxion.doAjax(
          'experimentWizardControllerHelperService',
          'addExperimentForm',
  {'newid':id, 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    $('new' + id).innerHTML = json.html;
  }
  });
}

function loadPoolsbyPlatform(form, id) {
  Fluxion.doAjax(
          'experimentWizardControllerHelperService',
          'loadPoolsbyPlatform',
  {'platformId':form.value, 'newid':id, 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    $('pools' + id).innerHTML = json.html;
  }
  });
}

function editloadPoolsbyPlatform(form) {
  Fluxion.doAjax(
          'experimentWizardControllerHelperService',
          'editloadPoolsbyPlatform',
  {'platformId':form.value, 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    $('pool').innerHTML = json.html;
  }
  });
}

function poolSearchType(t, type) {
  Fluxion.doAjax(
          'experimentWizardControllerHelperService',
          'poolSearchType',
  {'str':t.value, 'id':t.id, 'type':type, 'url':ajaxurl},
  {'doOnSuccess': function(json) {
    $('poolresult' + t.id).style.visibility = 'visible';
    $('poolresult' + t.id).innerHTML = json.html;
  }
  });
}

function editpoolSearchType(t, type) {
  Fluxion.doAjax(
          'experimentWizardControllerHelperService',
          'editpoolSearchType',
  {'str':t.value, 'type':type, 'url':ajaxurl},
  {'doOnSuccess': function(json) {
    $('poolresult').style.visibility = 'visible';
    $('poolresult').innerHTML = json.html;
  }
  });
}

function insertPoolResult(id, v) {
  var i = $(id);
  i.value = v;
  $('poolresult' + id).style.visibility = 'hidden';
}

function editinsertPoolResult(poolId, poolBarcode) {
  $('poolid').value = poolId;
  $('poolinput').value = poolBarcode;
  $('poolresult').style.visibility = 'hidden';
}