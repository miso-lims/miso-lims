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

function generateSampleQCRow(sampleId) {
  Fluxion.doAjax(
          'sampleControllerHelperService',
          'getSampleQCUsers',
          {
            'sampleId':sampleId,
            'url':ajaxurl
          },
          {'doOnSuccess':insertSampleQCRow}
  );
}

var insertSampleQCRow = function(json, includeId) {
  if (!jQuery('#sampleQcTable').attr("qcInProgress")) {
    jQuery('#sampleQcTable').attr("qcInProgress", "true");

    $('sampleQcTable').insertRow(1);
    //QCId  QCed By  	QC Date  	Method  	Results

    if (includeId) {
      var column1 = $('sampleQcTable').rows[1].insertCell(-1);
      column1.innerHTML = "<input type='hidden' id='sampleId' name='sampleId' value='" + json.sampleId + "'/>";
    }

    var column2 = $('sampleQcTable').rows[1].insertCell(-1);
    column2.innerHTML = "<select id='sampleQcUser' name='sampleQcUser'>" + json.qcUserOptions + "</select>";
    var column3 = $('sampleQcTable').rows[1].insertCell(-1);
    column3.innerHTML = "<input id='sampleQcDate' name='sampleQcDate' type='text'/>";
    var column4 = $('sampleQcTable').rows[1].insertCell(-1);
    column4.innerHTML = "<select id='sampleQcType' name='sampleQcType' onchange='changeSampleQcUnits(this);'/>";
    var column5 = $('sampleQcTable').rows[1].insertCell(-1);
    column5.innerHTML = "<input id='sampleQcResults' name='sampleQcResults' type='text'/><span id='units'/>";
    var column6 = $('sampleQcTable').rows[1].insertCell(-1);
    column6.innerHTML = "<a href='javascript:void(0);' onclick='addSampleQC(\"addQcForm\");'/>Add</a>";

    addMaxDatePicker("sampleQcDate", 0);

    Fluxion.doAjax(
            'sampleControllerHelperService',
            'getSampleQcTypes',
            {'url':ajaxurl},
            {'doOnSuccess':function(json) {
              jQuery('#sampleQcType').html(json.types);
              jQuery('#units').html(jQuery('#sampleQcType option:first').attr("units"));
            }
            }
    );
  }
  else {
    alert("Cannot add another QC when one is already in progress.")
  }
};

function changeSampleQcUnits(input) {
  jQuery('#units').html(jQuery('#sampleQcType').find(":selected").attr("units"));
}

function addSampleQC(form) {
  var f = $(form);
  var uindex = f.sampleQcUser.selectedIndex;
  var tindex = f.sampleQcType.selectedIndex;
  Fluxion.doAjax(
          'sampleControllerHelperService',
          'addSampleQC',
          {
            'sampleId':f.sampleId.value,
            'qcCreator':f.sampleQcUser.options[uindex].value,
            'qcDate':f.sampleQcDate.value,
            'qcType':f.sampleQcType.options[tindex].value,
            'results':f.sampleQcResults.value,
            'url':ajaxurl},
          {'updateElement':'sampleQcTable',
            'doOnSuccess':function(json) {
              jQuery('#sampleQcTable').removeAttr("qcInProgress");
            }
          }
  );
}

function changeSampleQCRow(qcId) {
    Fluxion.doAjax(
            'sampleControllerHelperService',
            'changeSampleQCRow',
            {
              'qcId':qcId,
              'url':ajaxurl
            },
            {'doOnSuccess':function(json) {
              jQuery('#results' + qcId).html(json.results);
              jQuery('#edit' + qcId).html(json.edit);
            }
            }
    );
}

function editSampleQC(qcId) {
  Fluxion.doAjax(
          'sampleControllerHelperService',
          'editSampleQC',
          {
            'qcId':qcId,
            'result':jQuery('#' + qcId).val(),
            'url':ajaxurl
          },
          {'doOnSuccess':pageReload
          }
  );
}

function saveBulkLibraryQc() {
  disableButton('bulkLibraryQcButton');
  //jQuery('#bulkLibraryQcButton').attr('disabled', 'disabled');
  //jQuery('#bulkLibraryQcButton').html("Processing...");

  collapseInputs('#library_table');

  var table = jQuery('#library_table').dataTable();
  var aReturn = [];

  var nodes = fnGetSelected(table);
  for (var i = 0; i < nodes.length; i++) {
    var obj = {};
    jQuery(nodes[i]).find("td:gt(0)").each(function() {
      var at = jQuery(this).attr("name");
      obj[at] = jQuery(this).text();
    });
    obj["qcCreator"] = jQuery('#currentUser').text();
    obj["libraryId"] = obj["name"].substring(3);
    aReturn.push(obj);
  }

  if (aReturn.length > 0) {
    if (validate_library_qcs(aReturn)) {
      Fluxion.doAjax(
              'libraryControllerHelperService',
              'bulkAddLibraryQCs',
              {
                'qcs':aReturn,
                'url':ajaxurl
              },
              {'doOnSuccess':processBulkLibraryQcTable}
      );
    }
    else {
      alert("The insertSize field can only contain integers, and the result field can only contain integers or decimals.");
      reenableButton('bulkLibraryQcButton', "Save QCs");
      //jQuery('#bulkLibraryQcButton').removeAttr('disabled');
      //jQuery('#bulkLibraryQcButton').html("Save QCs");
    }
  }
  else {
    alert("You have not selected any QC rows to save!\nPlease click the Select column cells in the rows you wish to save.");
    reenableButton('bulkLibraryQcButton', "Save QCs");
    //jQuery('#bulkLibraryQcButton').removeAttr('disabled');
    //jQuery('#bulkLibraryQcButton').html("Save QCs");
  }
}

var processBulkLibraryQcTable = function(json) {
  reenableButton('bulkLibraryQcButton', "Save QCs");
  //jQuery('#bulkLibraryQcButton').removeAttr('disabled');
  //jQuery('#bulkLibraryQcButton').html("Save QCs");

  var a = json.saved;
  for (var i = 0; i < a.length; i++) {
    jQuery('#library_table').find("tr:gt(0)").each(function() {
      if (jQuery(this).attr("libraryId") === a[i].sampleId) {
        jQuery(this).find("td").each(function() {
          jQuery(this).css('background', '#CCFF99');
        });
      }
    });
  }
  location.reload(true);
};

function saveBulkLibraryDilutions() {
  disableButton('bulkLibraryDilutionButton');
  //jQuery('#bulkLibraryDilutionButton').attr('disabled', 'disabled');
  //jQuery('#bulkLibraryDilutionButton').html("Processing...");

  collapseInputs('#library_table');

  var table = jQuery('#library_table').dataTable();
  var aReturn = [];
  var aTrs = table.fnGetNodes();
  for (var i = 0; i < aTrs.length; i++) {
    if (jQuery(aTrs[i]).hasClass('row_selected')) {
      var obj = {};
      jQuery(aTrs[i]).find("td:gt(0)").each(function() {
        var at = jQuery(this).attr("name");
        obj[at] = jQuery(this).text();
      });
      obj["dilutionCreator"] = jQuery('#currentUser').text();
      obj["libraryId"] = obj["name"].substring(3);
      aReturn.push(obj);
    }
  }

  if (aReturn.length > 0) {
    if (validate_library_dilutions(aReturn)) {
      Fluxion.doAjax(
              'libraryControllerHelperService',
              'bulkAddLibraryDilutions',
              {
                'dilutions':aReturn,
                'url':ajaxurl
              },
              {'doOnSuccess':processBulkLibraryDilutionTable}
      );
    }
    else {
      alert("The insertSize field can only contain integers, and the result field can only contain integers or decimals.");
      reenableButton('bulkLibraryDilutionButton', "Save QCs");
      //jQuery('#bulkLibraryDilutionButton').removeAttr('disabled');
      //jQuery('#bulkLibraryDilutionButton').html("Save QCs");
    }
  }
  else {
    alert("You have not selected any Dilution rows to save!\nPlease click the Select column cells in the rows you wish to save.");
    reenableButton('bulkLibraryDilutionButton', "Save QCs");
    //jQuery('#bulkLibraryDilutionButton').removeAttr('disabled');
    //jQuery('#bulkLibraryDilutionButton').html("Save QCs");
  }
}

var processBulkLibraryDilutionTable = function(json) {
  reenableButton('bulkLibraryDilutionButton', "Save QCs");
  //jQuery('#bulkLibraryDilutionButton').removeAttr('disabled');
  //jQuery('#bulkLibraryDilutionButton').html("Save Dilutions");

  var a = json.saved;
  for (var i = 0; i < a.length; i++) {
    jQuery('#library_table').find("tr:gt(0)").each(function() {
      if (jQuery(this).attr("libraryId") == a[i].libraryId) {
        jQuery(this).find("td").each(function() {
          jQuery(this).css('background', '#CCFF99');
        });
      }
    });
  }
  location.reload(true);
};

function editSampleIdBarcode(span, id) {
  Fluxion.doAjax(
    'loggedActionService',
    'logAction',
    {
      'objectId':id,
      'objectType':'Sample',
      'action':'editSampleIdBarcode',
      'url':ajaxurl
    },
    {}
  );

  var v = span.find('a').text();
  if (v && v !== "") {
    span.html("<input type='text' value='" + v + "' name='identificationBarcode' id='identificationBarcode'>");
  }
}

function editSampleLocationBarcode(span) {
  var v = span.find('a').text();
  span.html("<input type='text' value='" + v + "' name='locationBarcode' id='locationBarcode'>");
}

function printSampleBarcodes() {
  var samples = [];
  for (var i = 0; i < arguments.length; i++) {
    samples[i] = {'sampleId':arguments[i]};
  }

  Fluxion.doAjax(
    'printerControllerHelperService',
    'listAvailableServices',
    {
      'serviceClass':'uk.ac.bbsrc.tgac.miso.core.data.Sample',
      'url':ajaxurl
    },
    {
      'doOnSuccess':function (json) {
        jQuery('#printServiceSelectDialog')
                .html("<form>" +
                      "<fieldset class='dialog'>" +
                      "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                      json.services +
                      "</select></fieldset></form>");

        jQuery(function() {
          jQuery('#printServiceSelectDialog').dialog({
            autoOpen: false,
            width: 400,
            modal: true,
            resizable: false,
            buttons: {
              "Print": function() {
                Fluxion.doAjax(
                  'sampleControllerHelperService',
                  'printSampleBarcodes',
                  {
                    'serviceName':jQuery('#serviceSelect').val(),
                    'samples':samples,
                    'url':ajaxurl
                  },
                  {
                    'doOnSuccess':function (json) {
                      alert(json.response);
                    }
                  }
                );
                jQuery(this).dialog('close');
              },
              "Cancel": function() {
                jQuery(this).dialog('close');
              }
            }
          });
        });
        jQuery('#printServiceSelectDialog').dialog('open');
      },
      'doOnError':function (json) { alert(json.error); }
    }
  );
}

function showSampleNoteDialog(sampleId) {
  jQuery('#addSampleNoteDialog')
          .html("<form>" +
                "<fieldset class='dialog'>" +
                "<label for='internalOnly'>Internal Only?</label>" +
                "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
                "<br/>" +
                "<label for='notetext'>Text</label>" +
                "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' />" +
                "</fieldset></form>");

  jQuery(function() {
    jQuery('#addSampleNoteDialog').dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function() {
          addSampleNote(sampleId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  });
  jQuery('#addSampleNoteDialog').dialog('open');
}

var addSampleNote = function(sampleId, internalOnly, text) {
  Fluxion.doAjax(
    'sampleControllerHelperService',
    'addSampleNote',
    {
      'sampleId':sampleId,
      'internalOnly':internalOnly,
      'text':text,
      'url':ajaxurl
    },
    {
      'doOnSuccess':pageReload
    }
  );
};

function showSampleLocationChangeDialog(sampleId) {
  jQuery('#changeSampleLocationDialog')
          .html("<form>" +
                "<fieldset class='dialog'>" +
                "<label for='notetext'>New Location:</label>" +
                "<input type='text' name='locationBarcodeInput' id='locationBarcodeInput' class='text ui-widget-content ui-corner-all'/>" +
                "</fieldset></form>");

  jQuery(function() {
    jQuery('#changeSampleLocationDialog').dialog({
       autoOpen: false,
       width: 400,
       modal: true,
       resizable: false,
       buttons: {
         "Save": function() {
           changeSampleLocation(sampleId, jQuery('#locationBarcodeInput').val());
           jQuery(this).dialog('close');
         },
         "Cancel": function() {
           jQuery(this).dialog('close');
         }
       }
     });
  });
  jQuery('#changeSampleLocationDialog').dialog('open');
}

var changeSampleLocation = function(sampleId, barcode) {
  Fluxion.doAjax(
    'sampleControllerHelperService',
    'changeSampleLocation',
    {
      'sampleId':sampleId,
      'locationBarcode':barcode,
      'url':ajaxurl
    },
    {
      'doOnSuccess':pageReload
    }
  );
};

function receiveSample(input) {
  var barcode = jQuery(input).val();
  if (!isNullCheck(barcode)) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'getSampleByBarcode',
      {'barcode':barcode, 'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          var sample_desc = "<div id='" + json.id + "' class='dashboard'><table width=100%><tr><td>Sample Name: " + json.name + "<br> Sample ID: " + json.id + "<br>Desc: " + json.desc + "<br>Sample Type:" + json.type + "</td><td style='position: absolute;' align='right'><span class='float-right ui-icon ui-icon-circle-close' onclick='removeSample("+json.id+");' style='position: absolute; top: 0; right: 0;'></span></td></tr></table> </div>";
          if (jQuery("#" + json.id).length == 0) {
            jQuery("#sample_pan").append(sample_desc);
            jQuery('#msgspan').html("");
          }
          else {
            jQuery('#msgspan').html("<i>This sample has already been scanned</i>");
          }

          //unbind to stop change error happening every time
          jQuery(input).unbind('keyup');

          //clear and focus
          jQuery(input).val("");
          jQuery(input).focus();

          //rebind after setting focus
          jQuery(input).keyup(timedFunc(receiveSample(this), 400));
        },
        'doOnError':
        function(json) {
          jQuery('#msgspan').html("<i>"+json.error+"</i>");
        }
      }
    );
  }
  else {
    jQuery('#msgspan').html("")
  }
}

function setSampleReceiveDate(sampleList) {
  var samples = [];
  jQuery(sampleList).children('div').each(function(e) {
    var sdiv = jQuery(this);
    samples.push({'sampleId':sdiv.attr("id")});
  });

  if (samples.length > 0) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'setSampleReceivedDateByBarcode',
      {'samples':samples, 'url':ajaxurl},
      {'doOnSuccess':function(json) {
        alert(json.result);
      }
    });
  }
  else {
    alert("No samples scanned");
  }
}

function removeSample(sample) {
  jQuery("#" + sample).remove();
}

function deleteSample(sampleId, successfunc) {
  if (confirm("Are you sure you really want to delete SAM"+sampleId+"? This operation is permanent!")) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'deleteSample',
      {'sampleId':sampleId, 'url':ajaxurl},
      {'doOnSuccess':function(json) {
        successfunc();
      }
    });
  }
}