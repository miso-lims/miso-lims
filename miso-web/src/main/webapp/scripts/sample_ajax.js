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

var Sample = Sample || {
  deleteSample: function (sampleId, successfunc) {
    if (confirm("Are you sure you really want to delete SAM" + sampleId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'sampleControllerHelperService',
        'deleteSample',
        {'sampleId': sampleId, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          successfunc();
        }
      });
    }
  },

  removeSampleFromGroup: function(sampleId, sampleGroupId, successfunc) {
    if (confirm("Are you sure you really want to remove SAM" + sampleId + " from Sample group "+sampleGroupId+"?")) {
      Fluxion.doAjax(
        'sampleControllerHelperService',
        'removeSampleFromGroup',
        {'sampleId': sampleId, 'sampleGroupId':sampleGroupId, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          successfunc();
        }
      });
    }
  }
};

Sample.qc = {
  generateSampleQCRow: function (sampleId) {
    var self = this;
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'getSampleQCUsers',
      {
        'sampleId': sampleId,
        'url': ajaxurl
      },
      {'doOnSuccess': self.insertSampleQCRow}
    );
  },

  insertSampleQCRow: function (json, includeId) {
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
      column4.innerHTML = "<select id='sampleQcType' name='sampleQcType' onchange='Sample.qc.changeSampleQcUnits(this);'/>";
      var column5 = $('sampleQcTable').rows[1].insertCell(-1);
      column5.innerHTML = "<input id='sampleQcResults' name='sampleQcResults' type='text'/><span id='units'/>";
      var column6 = $('sampleQcTable').rows[1].insertCell(-1);
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Sample.qc.addSampleQC();'/>Add</a>";

      Utils.ui.addMaxDatePicker("sampleQcDate", 0);

      Fluxion.doAjax(
        'sampleControllerHelperService',
        'getSampleQcTypes',
        {'url': ajaxurl},
        {
          'doOnSuccess': function (json) {
            jQuery('#sampleQcType').html(json.types);
            jQuery('#units').html(jQuery('#sampleQcType option:first').attr("units"));
          }
        }
      );
    }
    else {
      alert("Cannot add another QC when one is already in progress.")
    }
  },

  changeSampleQcUnits: function (input) {
    jQuery('#units').html(jQuery('#sampleQcType').find(":selected").attr("units"));
  },

  addSampleQC: function () {
    var f = Utils.mappifyForm("addQcForm");
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'addSampleQC',
      {
        'sampleId': f.id,
        'qcCreator': f.sampleQcUser,
        'qcDate': f.sampleQcDate,
        'qcType': f.sampleQcType,
        'results': f.sampleQcResults,
        'url': ajaxurl},
      {'updateElement': 'sampleQcTable',
        'doOnSuccess': function (json) {
          jQuery('#sampleQcTable').removeAttr("qcInProgress");
        }
      }
    );
  },

  changeSampleQCRow: function (qcId) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'changeSampleQCRow',
      {
        'qcId': qcId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#results' + qcId).html(json.results);
          jQuery('#edit' + qcId).html(json.edit);
        }
      }
    );
  },

  editSampleQC: function (qcId) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'editSampleQC',
      {
        'qcId': qcId,
        'result': jQuery('#' + qcId).val(),
        'url': ajaxurl
      },
      {'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  saveBulkLibraryQc: function (tableName) {
    var lTable = jQuery(tableName);
    Utils.ui.disableButton('bulkLibraryQcButton');
    DatatableUtils.collapseInputs(tableName);

    var table = lTable.dataTable();
    var aReturn = [];

    var nodes = DatatableUtils.fnGetSelected(table);
    for (var i = 0; i < nodes.length; i++) {
      var obj = {};
      jQuery(nodes[i]).find("td:gt(0)").each(function () {
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
            'qcs': aReturn,
            'url': ajaxurl
          },
          {'doOnSuccess': function(json) {
            Sample.library.processBulkLibraryQcTable(tableName, json);
          }}
        );
      }
      else {
        alert("The insertSize and Concentration field can only contain integers, and the result field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkLibraryQcButton', "Save QCs");
      }
    }
    else {
      alert("You have not selected any QC rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkLibraryQcButton', "Save QCs");
    }
  }
};

Sample.library = {
  processBulkLibraryQcTable: function (tableName, json) {
    Utils.ui.reenableButton('bulkLibraryQcButton', "Save QCs");

    var a = json.saved;
    for (var i = 0; i < a.length; i++) {
      jQuery(tableName).find("tr:gt(0)").each(function () {
        if (jQuery(this).attr("libraryId") === a[i].libraryId) {
          jQuery(this).removeClass('row_selected');
          jQuery(this).addClass('row_saved');
          jQuery(this).find("td").each(function () {
            jQuery(this).css('background', '#CCFF99');
            if (jQuery(this).hasClass('rowSelect')) {
              jQuery(this).removeClass('rowSelect');
              jQuery(this).removeAttr('name');
            }
          });
        }
      });
    }

    if (json.errors) {
      var errors = json.errors;
      var errorStr = "";
      for (var i = 0; i < errors.length; i++) {
        errorStr += errors[i].error + "\n";
        jQuery(tableName).find("tr:gt(0)").each(function () {
          if (jQuery(this).attr("libraryId") === errors[i].libraryId) {
            jQuery(this).find("td").each(function () {
              jQuery(this).css('background', '#EE9966');
            });
          }
        });
      }
      alert("There were errors in your bulk input. The green rows have been saved, please fix the red rows:\n\n" + errorStr);
    }
    else {
      location.reload(true);
    }
  },

  saveBulkLibraryDilutions: function (tableName) {
    var self = this;
    Utils.ui.disableButton('bulkLibraryDilutionButton');
    DatatableUtils.collapseInputs(tableName);

    var table = jQuery(tableName).dataTable();
    var aReturn = [];
    var aTrs = table.fnGetNodes();
    for (var i = 0; i < aTrs.length; i++) {
      if (jQuery(aTrs[i]).hasClass('row_selected')) {
        var obj = {};
        jQuery(aTrs[i]).find("td:gt(0)").each(function () {
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
            'dilutions': aReturn,
            'url': ajaxurl
          },
          {'doOnSuccess': function(json) {
            self.processBulkLibraryDilutionTable(tableName, json);
          }}
        );
      }
      else {
        alert("The insertSize field can only contain integers, and the result field can only contain integers or decimals.");
        Utils.ui.reenableButton('bulkLibraryDilutionButton', "Save Dilutions");
      }
    }
    else {
      alert("You have not selected any Dilution rows to save!\nPlease click the Select column cells in the rows you wish to save.");
      Utils.ui.reenableButton('bulkLibraryDilutionButton', "Save Dilutions");
    }
  },

  processBulkLibraryDilutionTable: function (tableName, json) {
    Utils.ui.reenableButton('bulkLibraryDilutionButton', "Save Dilutions");

    var a = json.saved;
    for (var i = 0; i < a.length; i++) {
      jQuery(tableName).find("tr:gt(0)").each(function () {
        if (jQuery(this).attr("libraryId") == a[i].libraryId) {
          jQuery(this).removeClass('row_selected');
          jQuery(this).addClass('row_saved');
          jQuery(this).find("td").each(function () {
            jQuery(this).css('background', '#CCFF99');
            if (jQuery(this).hasClass('rowSelect')) {
              jQuery(this).removeClass('rowSelect');
              jQuery(this).removeAttr('name');
            }
          });
        }
      });
    }

    if (json.errors) {
      var errors = json.errors;
      var errorStr = "";
      for (var i = 0; i < errors.length; i++) {
        errorStr += errors[i].error + "\n";
        jQuery(tableName).find("tr:gt(0)").each(function () {
          if (jQuery(this).attr("libraryId") === errors[i].libraryId) {
            jQuery(this).find("td").each(function () {
              jQuery(this).css('background', '#EE9966');
            });
          }
        });
      }
      alert("There were errors in your bulk input. The green rows have been saved, please fix the red rows:\n\n" + errorStr);
    }
    else {
      location.reload(true);
    }
  }
};

Sample.barcode = {
  printSampleBarcodes: function () {
    var samples = [];
    for (var i = 0; i < arguments.length; i++) {
      samples[i] = {'sampleId': arguments[i]};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass': 'uk.ac.bbsrc.tgac.miso.core.data.Sample',
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#printServiceSelectDialog')
            .html("<form>" +
                  "<fieldset class='dialog'>" +
                  "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                  json.services +
                  "</select></fieldset></form>");

          jQuery(function () {
            jQuery('#printServiceSelectDialog').dialog({
              autoOpen: false,
              width: 400,
              modal: true,
              resizable: false,
              buttons: {
                "Print": function () {
                  Fluxion.doAjax(
                    'sampleControllerHelperService',
                    'printSampleBarcodes',
                    {
                      'serviceName': jQuery('#serviceSelect').val(),
                      'samples': samples,
                      'url': ajaxurl
                    },
                    {
                      'doOnSuccess': function (json) {
                        alert(json.response);
                      }
                    }
                  );
                  jQuery(this).dialog('close');
                },
                "Cancel": function () {
                  jQuery(this).dialog('close');
                }
              }
            });
          });
          jQuery('#printServiceSelectDialog').dialog('open');
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  }
};

Sample.ui = {
  editSampleIdBarcode: function (span, id) {
    Fluxion.doAjax(
      'loggedActionService',
      'logAction',
      {
        'objectId': id,
        'objectType': 'Sample',
        'action': 'editSampleIdBarcode',
        'url': ajaxurl
      },
      {}
    );

    var v = span.find('a').text();
    if (v && v !== "") {
      span.html("<input type='text' value='" + v + "' name='identificationBarcode' id='identificationBarcode'>");
    }
  },

  showSampleIdBarcodeChangeDialog: function (sampleId, sampleIdBarcode) {
    var self = this;
    jQuery('#changeSampleIdBarcodeDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<strong><label>Current Barcode: </label></strong>" + sampleIdBarcode +
            "<br /><strong><label for='notetext'>New Barcode:</label></strong>" +
            "<input type='text' name='idBarcodeInput' id='idBarcodeInput' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery(function () {
      jQuery('#changeSampleIdBarcodeDialog').dialog({
        autoOpen: false,
        width: 400,
        modal: true,
        resizable: false,
        buttons: {
          "Save": function () {
            self.changeSampleIdBarcode(sampleId, jQuery('#idBarcodeInput').val());
            jQuery(this).dialog('close');
          },
          "Cancel": function () {
            jQuery(this).dialog('close');
          }
        }
      });
    });
    jQuery('#changeSampleIdBarcodeDialog').dialog('open');
  },

  changeSampleIdBarcode: function (sampleId, idBarcode) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'changeSampleIdBarcode',
      {
        'sampleId': sampleId,
        'identificationBarcode': idBarcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  editSampleLocationBarcode: function (span) {
    var v = span.find('a').text();
    span.html("<input type='text' value='" + v + "' name='locationBarcode' id='locationBarcode'>");
  },

  showSampleLocationChangeDialog: function (sampleId) {
    var self = this;
    jQuery('#changeSampleLocationDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<label for='notetext'>New Location:</label>" +
            "<input type='text' name='locationBarcodeInput' id='locationBarcodeInput' class='text ui-widget-content ui-corner-all'/>" +
            "</fieldset></form>");

    jQuery(function () {
      jQuery('#changeSampleLocationDialog').dialog({
        autoOpen: false,
        width: 400,
        modal: true,
        resizable: false,
        buttons: {
          "Save": function () {
            self.changeSampleLocation(sampleId, jQuery('#locationBarcodeInput').val());
            jQuery(this).dialog('close');
          },
          "Cancel": function () {
            jQuery(this).dialog('close');
          }
        }
      });
    });
    jQuery('#changeSampleLocationDialog').dialog('open');
  },

  changeSampleLocation: function (sampleId, barcode) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'changeSampleLocation',
      {
        'sampleId': sampleId,
        'locationBarcode': barcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  showSampleNoteDialog: function (sampleId) {
    var self = this;
    jQuery('#addSampleNoteDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<label for='internalOnly'>Internal Only?</label>" +
            "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
            "<br/>" +
            "<label for='notetext'>Text</label>" +
            "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery(function () {
      jQuery('#addSampleNoteDialog').dialog({
        autoOpen: false,
        width: 400,
        modal: true,
        resizable: false,
        buttons: {
          "Add Note": function () {
            self.addSampleNote(sampleId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          },
          "Cancel": function () {
            jQuery(this).dialog('close');
          }
        }
      });
    });
    jQuery('#addSampleNoteDialog').dialog('open');
  },

  addSampleNote: function (sampleId, internalOnly, text) {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'addSampleNote',
      {
        'sampleId': sampleId,
        'internalOnly': internalOnly,
        'text': text,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  deleteSampleNote: function (sampleId, noteId) {
    if (confirm("Are you sure you want to delete this note?")) {
      Fluxion.doAjax(
        'sampleControllerHelperService',
        'deleteSampleNote',
        {'sampleId': sampleId, 'noteId': noteId, 'url': ajaxurl},
        {'doOnSuccess': Utils.page.pageReload}
      );
    }
  },

  receiveSample: function (input) {
    var barcode = jQuery(input).val();
    if (!Utils.validation.isNullCheck(barcode)) {
      barcode = Utils.validation.base64Check(barcode);
      jQuery(input).val(barcode);

      Fluxion.doAjax(
        'sampleControllerHelperService',
        'getSampleByBarcode',
        {'barcode': barcode, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          var sample_desc = "<div id='" + json.id + "' class='dashboard'><table width=100%><tr><td>Sample Name: " + json.name + "<br> Sample ID: " + json.id + "<br>Desc: " + json.desc + "<br>Sample Type:" + json.type + "</td><td style='position: absolute;' align='right'><span class='float-right ui-icon ui-icon-circle-close' onclick='Sample.ui.removeSample(" + json.id + ");' style='position: absolute; top: 0; right: 0;'></span></td></tr></table> </div>";
          if (jQuery("#" + json.id).length == 0) {
            jQuery("#sample_pan").append(sample_desc);
            jQuery('#msgspan').html("");
          }
          else {
            jQuery('#msgspan').html("<i>This sample has already been scanned</i>");
          }

          //unbind to stop change error happening every time
          //jQuery(input).unbind('keyup');

          //clear and focus
          jQuery(input).val("");
          jQuery(input).focus();

          //rebind after setting focus
          // Fixed for MISO-353 commented
          // Utils.timer.typewatchFunc(jQuery('#searchSampleByBarcode'), function() {
          // Sample.ui.receiveSample(jQuery('#searchSampleByBarcode'));
          // }, 100, 4);
          },
          'doOnError': function (json) {
            jQuery('#msgspan').html("<i>" + json.error + "</i>");
          }
        }
      );
    }
    else {
      jQuery('#msgspan').html("")
    }
  },

  removeSample: function (sample) {
    jQuery("#" + sample).remove();
  },

  setSampleReceiveDate: function (sampleList) {
    var samples = [];
    jQuery(sampleList).children('div').each(function (e) {
      var sdiv = jQuery(this);
      samples.push({'sampleId': sdiv.attr("id")});
    });

    if (samples.length > 0) {
      Fluxion.doAjax(
        'sampleControllerHelperService',
        'setSampleReceivedDateByBarcode',
        {'samples': samples, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          alert(json.result);
        }
      });
    }
    else {
      alert("No samples scanned");
    }
  },

  createListingSamplesTable: function () {
    jQuery('#listingSamplesTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    jQuery.fn.dataTableExt.oSort['no-sam-asc'] = function (x, y) {
      var a = parseInt(x.replace(/^SAM/i, ""));
      var b = parseInt(y.replace(/^SAM/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-sam-desc'] = function (x, y) {
      var a = parseInt(x.replace(/^SAM/i, ""));
      var b = parseInt(y.replace(/^SAM/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'listSamplesDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#listingSamplesTable').html('');
        jQuery('#listingSamplesTable').dataTable({
          "aaData": json.array,
          "aoColumns": [
            { "sTitle": "Sample Name", "sType": "no-sam"},
            { "sTitle": "Alias"},
            { "sTitle": "Type"},
            { "sTitle": "QC Passed"},
            { "sTitle": "QC Result"},
            { "sTitle": "Edit"},
            { "sTitle": "ID", "bVisible": false}
          ],
          "bJQueryUI": true,
          "iDisplayLength": 25,
          "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
          "aaSorting": [
            [0, "desc"]
          ]
        });
        jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
        jQuery("#toolbar").append("<button style=\"margin-left:5px;\" onclick=\"window.location.href='/miso/sample/new';\" class=\"fg-button ui-state-default ui-corner-all\">Add Sample</button>");
      }
      }
    );
  }
};
