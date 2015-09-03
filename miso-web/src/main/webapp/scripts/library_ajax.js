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

var Library = Library || {
  deleteLibrary: function (libraryId, successfunc) {
    if (confirm("Are you sure you really want to delete LIB" + libraryId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteLibrary',
        {'libraryId': libraryId, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          successfunc();
        }
        });
    }
  }
};

Library.tagbarcode = {
  populateAvailableBarcodesForStrategy: function (input) {
    var self = this;
    var strategy = jQuery(input).val();
    if (!Utils.validation.isNullCheck(strategy)) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'getTagBarcodesForStrategy',
        {'strategy': strategy, 'url': ajaxurl},
        {'doOnSuccess': self.processTagBarcodeStrategyChange}
      );
    }
  },

  processTagBarcodeStrategyChange: function (json) {
    jQuery('#tagBarcodesDiv').html(json.tagBarcodes);
  }
};

Library.qc = {
  insertLibraryQCRow: function (libraryId, includeId) {
    if (!jQuery('#libraryQcTable').attr("qcInProgress")) {
      jQuery('#libraryQcTable').attr("qcInProgress", "true");

      $('libraryQcTable').insertRow(1);
      //QCId  QCed By  	QC Date  	Method  	Results

      if (includeId) {
        var column1 = $('libraryQcTable').rows[1].insertCell(-1);
        column1.innerHTML = "<input type='hidden' id='libraryId' name='libraryId' value='" + libraryId + "'/>";
      }
      var column2 = $('libraryQcTable').rows[1].insertCell(-1);
      column2.innerHTML = "<input id='libraryQcUser' name='libraryQcUser' type='hidden' value='" + $('currentUser').innerHTML + "'/>" + $('currentUser').innerHTML;
      var column3 = $('libraryQcTable').rows[1].insertCell(-1);
      column3.innerHTML = "<input id='libraryQcDate' name='libraryQcDate' type='text'/>";
      var column4 = $('libraryQcTable').rows[1].insertCell(-1);
      column4.innerHTML = "<select id='libraryQcType' name='libraryQcType' onchange='Library.qc.changeLibraryQcUnits(this);'/>";
      var column5 = $('libraryQcTable').rows[1].insertCell(-1);
      column5.innerHTML = "<input id='libraryQcResults' name='libraryQcResults' type='text'/><span id='units'/>";
      var column6 = $('libraryQcTable').rows[1].insertCell(-1);
      column6.innerHTML = "<input id='libraryQcInsertSize' name='libraryQcInsertSize' type='text'/> bp";
      var column7 = $('libraryQcTable').rows[1].insertCell(-1);
      column7.innerHTML = "<a href='javascript:void(0);' onclick='Library.qc.addLibraryQC();'/>Add</a>";

      Utils.ui.addMaxDatePicker("libraryQcDate", 0);

      Fluxion.doAjax(
        'libraryControllerHelperService',
        'getLibraryQcTypes',
        {'url': ajaxurl},
        {'doOnSuccess': function (json) {
          jQuery('#libraryQcType').html(json.types);
          jQuery('#units').html(jQuery('#libraryQcType option:first').attr("units"));
        }
        }
      );
    }
    else {
      alert("Cannot add another QC when one is already in progress.")
    }
  },

  changeLibraryQcUnits: function (input) {
    jQuery('#units').html(jQuery('#libraryQcType').find(":selected").attr("units"));
  },

  addLibraryQC: function () {
    var f = Utils.mappifyForm("addQcForm");
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'addLibraryQC',
      {
        'libraryId': f.id,
        'qcCreator': f.libraryQcUser,
        'qcDate': f.libraryQcDate,
        'qcType': f.libraryQcType,
        'results': f.libraryQcResults,
        'insertSize': f.libraryQcInsertSize,
        'url': ajaxurl
      },
      {'updateElement': 'libraryQcTable',
      'doOnSuccess': function (json) {
        jQuery('#libraryQcTable').removeAttr("qcInProgress");
      }
    });
  },

  changeLibraryQCRow: function (qcId, libraryId) {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'changeLibraryQCRow',
      {
        'libraryId': libraryId,
        'qcId': qcId,
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#result' + qcId).html(json.results);
        jQuery('#insert' + qcId).html(json.insertSize);
        jQuery('#edit' + qcId).html(json.edit);
      }
    });
  },

  editLibraryQC: function (qcId, libraryId) {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'editLibraryQC',
      {
        'libraryId': libraryId,
        'qcId': qcId,
        'result': jQuery('#results' + qcId).val(),
        'insertSize': jQuery('#insertSize' + qcId).val(),
        'url': ajaxurl
      },
      {'doOnSuccess': Utils.page.pageReload
      }
    );
  }
};

Library.dilution = {
  insertLibraryDilutionRow: function (libraryId) {
    if (!jQuery('#libraryDilutionTable').attr("dilutionInProgress")) {
      jQuery('#libraryDilutionTable').attr("dilutionInProgress", "true");

      $('libraryDilutionTable').insertRow(1);
      //dilutionId    Done By   Dilution Date Barcode Results
      //var column1=$('libraryDilutionTable').rows[1].insertCell(-1);
      //column1.innerHTML="<input type='hidden' id='libraryId' name='libraryId' value='"+libraryId+"'/>";
      var column1 = $('libraryDilutionTable').rows[1].insertCell(-1);
      column1.innerHTML = "<input id='name' name='name' type='hidden' value='Unsaved '/>Unsaved";
      var column2 = $('libraryDilutionTable').rows[1].insertCell(-1);
      column2.innerHTML = "<input id='libraryDilutionCreator' name='libraryDilutionCreator' type='hidden' value='" + $('currentUser').innerHTML + "'/>" + $('currentUser').innerHTML;
      var column3 = $('libraryDilutionTable').rows[1].insertCell(-1);
      column3.innerHTML = "<input id='libraryDilutionDate' name='libraryDilutionDate' type='text'/>";
      //var column5=$('libraryDilutionTable').rows[1].insertCell(-1);
      //column5.innerHTML="<input id='libraryDilutionBarcode' name='libraryDilutionBarcode' type='text'/>";
      var column6 = $('libraryDilutionTable').rows[1].insertCell(-1);
      column6.innerHTML = "<input id='libraryDilutionResults' name='libraryDilutionResults' type='text'/>";
      var column7 = $('libraryDilutionTable').rows[1].insertCell(-1);
      column7.innerHTML = "<i>Generated on save</i>";
      var column8 = $('libraryDilutionTable').rows[1].insertCell(-1);
      column8.innerHTML = "<a href='javascript:void(0);' onclick='Library.dilution.addLibraryDilution();'/>Add</a>";

      Utils.ui.addMaxDatePicker("libraryDilutionDate", 0);
    }
    else {
      alert("Cannot add another dilution when one is already in progress.")
    }
  },

  addLibraryDilution: function () {
    var f = Utils.mappifyForm("addDilutionForm");
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'addLibraryDilution',
      {
        'libraryId': f.id,
        'dilutionCreator': f.libraryDilutionCreator,
        'dilutionDate': f.libraryDilutionDate,
        //'locationBarcode':f.libraryDilutionBarcode.value,
        'results': f.libraryDilutionResults,
        'url': ajaxurl},
      {'updateElement': 'libraryDilutionTable',
        'doOnSuccess': function (json) {
          jQuery('#libraryDilutionTable').removeAttr("dilutionInProgress");
        }
      }
    );
  },

  changeLibraryDilutionRow: function (dilutionId) {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'changeLibraryDilutionRow',
      {
        'dilutionId': dilutionId,
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#results' + dilutionId).html(json.results);
        jQuery('#edit' + dilutionId).html(json.edit);
      }
      }
    );
  },

  editLibraryDilution: function (dilutionId) {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'editLibraryDilution',
      {
        'dilutionId': dilutionId,
        'result': jQuery('#' + dilutionId).val(),
        'url': ajaxurl
      },
      {'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  deleteLibraryDilution: function (libraryDilutionId, successfunc) {
    if (confirm("Are you sure you really want to delete LDI" + libraryDilutionId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteLibraryDilution',
        {'libraryDilutionId': libraryDilutionId, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          successfunc();
        }
      });
    }
  }
};

Library.empcr = {
  insertEmPcrRow: function (dilutionId) {
    if (!jQuery('#emPcrTable').attr("pcrInProgress")) {
      jQuery('#emPcrTable').attr("pcrInProgress", "true");

      $('emPcrTable').insertRow(1);

      //var column1=$('emPcrTable').rows[1].insertCell(-1);
      //column1.innerHTML="<input type='hidden' id='dilutionId' name='dilutionId' value='"+dilutionId+"'/>";
      var column2 = $('emPcrTable').rows[1].insertCell(-1);
      column2.innerHTML = "" + dilutionId + " <input type='hidden' id='dilutionId' name='dilutionId' value='" + dilutionId + "'/>";
      var column3 = $('emPcrTable').rows[1].insertCell(-1);
      column3.innerHTML = "<input id='emPcrCreator' name='emPcrCreator' type='hidden' value='" + $('currentUser').innerHTML + "'/>" + $('currentUser').innerHTML;
      var column4 = $('emPcrTable').rows[1].insertCell(-1);
      column4.innerHTML = "<input id='emPcrDate' name='emPcrDate' type='text'/>";
      var column5 = $('emPcrTable').rows[1].insertCell(-1);
      column5.innerHTML = "<input id='emPcrResults' name='emPcrResults' type='text'/>";
      var column6 = $('emPcrTable').rows[1].insertCell(-1);
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Library.empcr.addEmPcr();'/>Add</a>";

      Utils.ui.addMaxDatePicker("emPcrDate", 0);
    }
    else {
      alert("Cannot add another emPCR when one is already in progress.")
    }
  },

  addEmPcr: function () {
    var f = Utils.mappifyForm("addEmPcrForm");
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'addEmPcr',
      {
        'dilutionId': f.dilutionId,
        'pcrCreator': f.emPcrCreator,
        'pcrDate': f.emPcrDate,
        'results': f.emPcrResults,
        'url': ajaxurl},
      {'updateElement': 'emPcrTable',
        'doOnSuccess': function (json) {
          jQuery('#emPcrTable').removeAttr("pcrInProgress");
        }
      }
    );
  },

  insertEmPcrDilutionRow: function (emPcrId) {
    if (!jQuery('#emPcrDilutionTable').attr("dilutionInProgress")) {
      jQuery('#emPcrDilutionTable').attr("dilutionInProgress", "true");

      $('emPcrDilutionTable').insertRow(1);

      //var column1=$('emPcrDilutionTable').rows[1].insertCell(-1);
      //column1.innerHTML="<input type='hidden' id='emPcrId' name='emPcrId' value='"+emPcrId+"'/>";
      var column2 = $('emPcrDilutionTable').rows[1].insertCell(-1);
      column2.innerHTML = "" + emPcrId + " <input type='hidden' id='emPcrId' name='emPcrId' value='" + emPcrId + "'/>";
      var column3 = $('emPcrDilutionTable').rows[1].insertCell(-1);
      column3.innerHTML = "<input id='emPcrDilutionCreator' name='emPcrDilutionCreator' type='hidden' value='" + $('currentUser').innerHTML + "'/>" + $('currentUser').innerHTML;
      var column4 = $('emPcrDilutionTable').rows[1].insertCell(-1);
      column4.innerHTML = "<input id='emPcrDilutionDate' name='emPcrDilutionDate' type='text'/>";
      //var column5=$('emPcrDilutionTable').rows[1].insertCell(-1);
      //column5.innerHTML="<input id='emPcrDilutionBarcode' name='emPcrDilutionBarcode' type='text'/>";
      var column6 = $('emPcrDilutionTable').rows[1].insertCell(-1);
      column6.innerHTML = "<input id='emPcrDilutionResults' name='emPcrDilutionResults' type='text'/>";
      var column7 = $('emPcrDilutionTable').rows[1].insertCell(-1);
      column7.innerHTML = "<a href='javascript:void(0);' onclick='Library.empcr.addEmPcrDilution();'/>Add</a>";

      Utils.ui.addMaxDatePicker("emPcrDilutionDate", 0);
    }
    else {
      alert("Cannot add another dilution when one is already in progress.")
    }
  },

  addEmPcrDilution: function (form) {
    var f = Utils.mappifyForm("addEmPcrDilutionForm");
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'addEmPcrDilution',
      {
        'pcrId': f.emPcrId,
        'pcrDilutionCreator': f.emPcrDilutionCreator,
        'pcrDilutionDate': f.emPcrDilutionDate,
        //'pcrDilutionBarcode':f.emPcrDilutionBarcode.value,
        'results': f.emPcrDilutionResults,
        'url': ajaxurl},
      {'updateElement': 'emPcrDilutionTable',
        'doOnSuccess': function (json) {
          jQuery('#emPcrDilutionTable').removeAttr("dilutionInProgress");
        }
      }
    );
  },

  deleteEmPCR: function (empcrId, successfunc) {
    if (confirm("Are you sure you really want to delete EmPCR " + empcrId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteEmPCR',
        {'empcrId': empcrId, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          successfunc();
        }
      });
    }
  },

  deleteEmPCRDilution: function (empcrDilutionId, successfunc) {
    if (confirm("Are you sure you really want to delete EmPCRDilution" + empcrDilutionId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteEmPCRDilution',
        {'empcrDilutionId': empcrDilutionId, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          successfunc();
        }
      });
    }
  }
};

Library.barcode = {
  printLibraryBarcodes: function () {
    var libraries = [];
    for (var i = 0; i < arguments.length; i++) {
      libraries[i] = {'libraryId': arguments[i]};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass': 'uk.ac.bbsrc.tgac.miso.core.data.Library',
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
                    'libraryControllerHelperService',
                    'printLibraryBarcodes',
                    {
                      'serviceName': jQuery('#serviceSelect').val(),
                      'libraries': libraries,
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
  },

  printDilutionBarcode: function (dilutionId, platform) {
    var dilutions = [];
    dilutions[0] = {'dilutionId': dilutionId};

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass': 'uk.ac.bbsrc.tgac.miso.core.data.Dilution',
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
                    'libraryControllerHelperService',
                    'printLibraryDilutionBarcodes',
                    {
                      'serviceName': jQuery('#serviceSelect').val(),
                      'dilutions': dilutions,
                      'platform': platform,
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
  },

  showLibraryLocationChangeDialog: function (libraryId) {
    var self = this;
    jQuery('#changeLibraryLocationDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<label for='notetext'>New Location:</label>" +
            "<input type='text' name='locationBarcodeInput' id='locationBarcodeInput' class='text ui-widget-content ui-corner-all'/>" +
            "</fieldset></form>");

    jQuery('#changeLibraryLocationDialog').dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function () {
          self.changeLibraryLocation(libraryId, jQuery('#locationBarcodeInput').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    }).dialog('open');
  },

  changeLibraryLocation: function (libraryId, barcode) {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'changeLibraryLocation',
      {'libraryId': libraryId, 'locationBarcode': barcode, 'url': ajaxurl},
      {'doOnSuccess': Utils.page.pageReload}
    );
  }
};

Library.ui = {
  changePlatformName: function (input) {
    var self = this;
    var platform = jQuery(input).val();
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'changePlatformName',
      {'platform': platform, 'url': ajaxurl},
      {'doOnSuccess': self.processPlatformChange}
    );
  },
  changePlatformNameWithLibraryType: function (input, librarytype) {
    var self = this;
    var platform = jQuery(input).val();
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'changePlatformName',
      {'platform': platform, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery('#libraryTypes').html(json.libraryTypes);
        jQuery('#libraryTypes').val(librarytype);
      }
      }
    );
  },
  changePlatformNameWithTagBarcodeStrategy: function (input, tagBarcodeStrategy) {
    var self = this;
    var platform = jQuery(input).val();
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'changePlatformName',
      {'platform': platform, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery('#tagBarcodeStrategies').html(json.tagBarcodeStrategies);
        jQuery('#tagBarcodeStrategies').val(tagBarcodeStrategy);
      }
      }
    );
  },

  processPlatformChange: function (json) {
    jQuery('#libraryTypes').html(json.libraryTypes);
    jQuery('#tagBarcodeStrategies').html(json.tagBarcodeStrategies);
  },

  fillDownTagBarcodeStrategySelects: function (tableselector, th) {
    DatatableUtils.collapseInputs(tableselector);
    var tableObj = jQuery(tableselector);
    var table = tableObj.dataTable();
    var header = jQuery(th);
    var headerName = header.attr("header");
    var firstSelectedRow = tableObj.find(".row_selected").first();
    if (firstSelectedRow.length > 0) {
      var td = firstSelectedRow.find("td[name=" + headerName + "]");
      var tdtext = td.html();

      var col = firstSelectedRow.children().index(td);

      var frId = 0;
      var aTrs = table.fnGetNodes();
      for (var i = 0; i < aTrs.length; i++) {
        if (jQuery(aTrs[i]).hasClass('row_selected')) {
          frId = i;
          break;
        }
      }

      tableObj.find("tr:gt(" + frId + ")").each(function () {
        table.fnUpdate(tdtext, table.fnGetPosition(this), col);
      });

      Fluxion.doAjax(
        'libraryControllerHelperService',
        'getBarcodesPositions',
        {'strategy': tdtext,
          'url': ajaxurl
        },
        {'doOnSuccess': function (json) {
          tableObj.find("tr:gt(" + frId + ")").each(function () {
            var c = this.cells[col + 1];
            jQuery(c).html("");
            for (var i = 0; i < json.numApplicableBarcodes; i++) {
              jQuery(c).append("<span class='tagBarcodeSelectDiv' position='" + (i + 1) + "' id='tagbarcodes" + (i + 1) + "'>- <i>Select...</i></span>");
              //jQuery(c).append("<td class='smallbox'><span class='tagBarcodeSelectDiv' position='"+(i+1)+"' id='tagbarcodes"+(i+1)+"'>X</span></td>");
              if (json.numApplicableBarcodes > 1 && i == 0) {
                jQuery(c).append("|");
              }
            }

            //bind editable to selects
            jQuery("#cinput .tagBarcodeSelectDiv").editable(function (value, settings) {
              return value;
            },
            {
              loadurl: '../../library/barcodesForPosition',
              loaddata: function (value, settings) {
                var ret = {};
                ret["position"] = jQuery(this).attr("position");
                if (!Utils.validation.isNullCheck(tdtext)) {
                  ret['barcodeStrategy'] = tdtext;
                }
                else {
                  ret['barcodeStrategy'] = '';
                }

                return ret;
              },
              type: 'select',
              onblur: 'submit',
              placeholder: '',
              style: 'inherit',
              submitdata: function (tvalue, tsettings) {
                return {
                  "row_id": this.parentNode.getAttribute('id'),
                  "column": table.fnGetPosition(this)[2]
                };
              }
            });
          });
        }
        });
    }
    else {
      alert("Please select a row to use as the Fill Down template by clicking in the Select column for that row.");
    }
  },

  fillDownTagBarcodeSelects: function (tableselector, th) {
    DatatableUtils.collapseInputs(tableselector);
    var tableObj = jQuery(tableselector);
    var table = tableObj.dataTable();
    var header = jQuery(th);
    var headerName = header.attr("header");
    var firstSelectedRow = tableObj.find(".row_selected").first();
    if (firstSelectedRow.length > 0) {
      var td = firstSelectedRow.find("td[name=" + headerName + "]");
      var tdtext = td.html();

      var col = firstSelectedRow.children().index(td);

      var frId = 0;
      var aTrs = table.fnGetNodes();
      for (var i = 0; i < aTrs.length; i++) {
        if (jQuery(aTrs[i]).hasClass('row_selected')) {
          frId = i;
          break;
        }
      }

      var firstSelText = jQuery(aTrs[frId].cells[col - 1]).text();

      tableObj.find("tr:gt(" + frId + ")").each(function () {
        var strat = this.cells[col - 1];
        var stratText = jQuery(strat).text();
        var cell = jQuery(this.cells[col]);

        if (stratText.trim()) {
          //no select means empty or already filled
          if (firstSelText.indexOf("Select") == 0) {
            //same strategy, just copy the cell
            if (firstSelText === stratText) {
              cell.html(tdtext);
            }
            else {
              Fluxion.doAjax(
                'libraryControllerHelperService',
                'getBarcodesPositions',
                {'strategy': stratText,
                  'url': ajaxurl
                },
                {'doOnSuccess': function (json) {
                  cell.html("");
                  for (var i = 0; i < json.numApplicableBarcodes; i++) {
                    cell.append("<span class='tagBarcodeSelectDiv' position='" + (i + 1) + "' id='tagbarcodes" + (i + 1) + "'>- <i>Select...</i></span>");
                    if (json.numApplicableBarcodes > 1 && i == 0) {
                      cell.append("|");
                    }
                  }
                }
              });
            }
          }
          else {
            //just copy select
            if (firstSelText === stratText) {
              cell.html(tdtext);
            }
          }
        }

        //bind editable to selects
        jQuery("#cinput .tagBarcodeSelectDiv").editable(function (value, settings) {
          return value;
        },
        {
          loadurl: '../../library/barcodesForPosition',
          loaddata: function (value, settings) {
            var ret = {};
            ret["position"] = jQuery(this).attr("position");
            if (!Utils.validation.isNullCheck(stratText)) {
              ret['barcodeStrategy'] = stratText;
            }
            else {
              ret['barcodeStrategy'] = '';
            }

            return ret;
          },
          type: 'select',
          onblur: 'submit',
          placeholder: '',
          style: 'inherit',
          submitdata: function (tvalue, tsettings) {
            return {
              "row_id": this.parentNode.getAttribute('id'),
              "column": table.fnGetPosition(this)[2]
            };
          }
        });
      });
    }
  },

  showLibraryNoteDialog: function (libraryId) {
    var self = this;
    jQuery('#addLibraryNoteDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<label for='internalOnly'>Internal Only?</label>" +
            "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
            "<br/>" +
            "<label for='notetext'>Text</label>" +
            "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery(function () {
      jQuery('#addLibraryNoteDialog').dialog({
        autoOpen: false,
        width: 400,
        modal: true,
        resizable: false,
        buttons: {
          "Add Note": function () {
            self.addLibraryNote(libraryId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          },
          "Cancel": function () {
            jQuery(this).dialog('close');
          }
        }
      });
    });
    jQuery('#addLibraryNoteDialog').dialog('open');
  },

  addLibraryNote: function (libraryId, internalOnly, text) {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'addLibraryNote',
      {'libraryId': libraryId, 'internalOnly': internalOnly, 'text': text, 'url': ajaxurl},
      {'doOnSuccess': Utils.page.pageReload}
    );
  },

  deleteLibraryNote: function (libraryId, noteId) {
    if (confirm("Are you sure you want to delete this note?")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteLibraryNote',
        {'libraryId': libraryId, 'noteId': noteId, 'url': ajaxurl},
        {'doOnSuccess': Utils.page.pageReload}
      );
    }
  },

  createListingLibrariesTable: function () {
    jQuery('#listingLibrariesTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    jQuery.fn.dataTableExt.oSort['no-lib-asc'] = function (x, y) {
      var a = parseInt(x.replace(/^LIB/i, ""));
      var b = parseInt(y.replace(/^LIB/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-lib-desc'] = function (x, y) {
      var a = parseInt(x.replace(/^LIB/i, ""));
      var b = parseInt(y.replace(/^LIB/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
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
      'libraryControllerHelperService',
      'listLibrariesDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#listingLibrariesTable').html('');
        jQuery('#listingLibrariesTable').dataTable({
          "aaData": json.array,
          "aoColumns": [
            { "sTitle": "Library Name", "sType": "no-lib"},
            { "sTitle": "Alias"},
            { "sTitle": "Type"},
            { "sTitle": "Sample Name", "sType": "no-sam"},
            { "sTitle": "QC Passed"},
            //{ "sTitle": "Edit"}
          ],
          "bJQueryUI": true,
          "iDisplayLength": 25,
          "aaSorting": [
            [0, "desc"]
          ]
        });
      }
      }
    );
  }
};
