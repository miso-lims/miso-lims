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
  deleteLibrary: function (libraryId) {
    if (confirm("Are you sure you really want to delete LIB" + libraryId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteLibrary',
        {'libraryId': libraryId, 'url': ajaxurl},
        {
          'doOnSuccess': function () {
            window.location.href = '/miso/libraries';
          }
        }
      );
    }
  },
  
  validateLibrary: function () {
    Validate.cleanFields('#library-form');
    jQuery('#library-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '100');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-required', 'true');
    jQuery('#description').attr('data-parsley-maxlength', '100');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    
    // Volume validation
    jQuery('#volume').attr('class', 'form-control');
    jQuery('#volume').attr('data-parsley-required', 'true');
    jQuery('#volume').attr('data-parsley-maxlength', '10');
    jQuery('#volume').attr('data-parsley-type', 'number');

    jQuery('#propagationRuleError').css('display', 'none');
    if (Library.propagationRules.length > 0 && jQuery('#libraryDesignTypes')[0].value != -1) {
      var selectionType = jQuery('#librarySelectionTypes')[0].value;
      var strategyType = jQuery('#libraryStrategyTypes')[0].value;
      var paired = jQuery('#paired')[0].value === "true";
      var platform = jQuery('#platformNames')[0].value.toUpperCase();
      if (!Library.propagationRules.some(function(rule) {
        return Library.isMatched(selectionType, rule.librarySelectionType)
          && Library.isMatched(strategyType, rule.libraryStrategyType)
          && Library.isMatched(paired, rule.paired)
          && (rule.platform === null && typeof rule.platform === 'object' || rule.platform.toUpperCase() == platform);
      })) {
        jQuery('#propagationRuleError').css('display', 'block');
      }
    }

    Fluxion.doAjax(
      'libraryControllerHelperService',
      'getLibraryAliasRegex',
      {
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          var regex = json.aliasRegex.split(' ').join('+');
          jQuery('#alias').attr('data-parsley-pattern', regex);
          // TODO: better error message than a regex..?
          //       perhaps save a description and examples with the regex
          jQuery('#alias').attr('data-parsley-error-message', 'Must match '+regex);
          jQuery('#library-form').parsley();
          jQuery('#library-form').parsley().validate();
          Validate.updateWarningOrSubmit('#library-form', Library.validateLibraryExtra);
          return false;
        },
        'doOnError': function(json) {
          alert(json.error);
        }
      }
    );
  },

  isMatched: function (value, reference) {
    return (reference === null && typeof reference === 'object') || value == reference;
  },
  
  validateLibraryExtra: function () {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'validateLibraryAlias',
      {
        'alias': jQuery('#alias').val(),
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          if (json.response === "OK") {
            jQuery('#library-form').submit();
          }
        },
        'doOnError': function(json) {
          alert(json.error);
        }
      }
    );
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

      jQuery('#libraryQcTable')[0].insertRow(1);
      //QCId  QCed By  	QC Date  	Method  	Results

      if (includeId) {
        var column1 = jQuery('#libraryQcTable')[0].rows[1].insertCell(-1);
        column1.innerHTML = "<input type='hidden' id='libraryId' name='libraryId' value='" + libraryId + "'/>";
      }
      var column2 = jQuery('#libraryQcTable')[0].rows[1].insertCell(-1);
      column2.innerHTML = "<input id='libraryQcUser' name='libraryQcUser' type='hidden' value='" + jQuery('#currentUser')[0].innerHTML + "'/>" + jQuery('#currentUser')[0].innerHTML;
      var column3 = jQuery('#libraryQcTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='libraryQcDate' name='libraryQcDate' type='text'/>";
      var column4 = jQuery('#libraryQcTable')[0].rows[1].insertCell(-1);
      column4.innerHTML = "<select id='libraryQcType' name='libraryQcType' onchange='Library.qc.changeLibraryQcUnits(this);'/>";
      var column5 = jQuery('#libraryQcTable')[0].rows[1].insertCell(-1);
      column5.innerHTML = "<input id='libraryQcResults' name='libraryQcResults' type='text'/><span id='units'/>";
      var column6 = jQuery('#libraryQcTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<input id='libraryQcInsertSize' name='libraryQcInsertSize' type='text'/> bp";
      var column7 = jQuery('#libraryQcTable')[0].rows[1].insertCell(-1);
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
      alert("Cannot add another QC when one is already in progress.");
    }
  },

  changeLibraryQcUnits: function () {
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
      'doOnSuccess': function () {
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
  insertLibraryDilutionRow: function (libraryId, libraryPrepKitId) {
    if (!jQuery('#libraryDilutionTable').attr("dilutionInProgress")) {
      jQuery('#libraryDilutionTable').attr("dilutionInProgress", "true");

      jQuery('#libraryDilutionTable')[0].insertRow(1);
      //dilutionId    Done By   Dilution Date Barcode Results
      var column1 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column1.innerHTML = "<input id='name' name='name' type='hidden' value='Unsaved '/>Unsaved";
      var column2 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column2.innerHTML = "<input id='libraryDilutionCreator' name='libraryDilutionCreator' type='hidden' value='" + jQuery('#currentUser')[0].innerHTML + "'/>" + jQuery('#currentUser')[0].innerHTML;
      var column3 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='libraryDilutionDate' name='libraryDilutionDate' type='text'/>";
      var column6 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<input id='libraryDilutionResults' name='libraryDilutionResults' type='text'/>";
      var column7 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column7.innerHTML = "<select id='libraryDilutionTargetedResequencing' name='libraryDilutionTargetedResequencing' onchange='Library.qc.changeLibraryQcUnits(this);'/>"
      var column8 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column8.innerHTML = "<i>Generated on save</i>";
      var column9 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column9.innerHTML = "<a href='javascript:void(0);' onclick='Library.dilution.addLibraryDilution();'/>Add</a>";

      Utils.ui.addMaxDatePicker("libraryDilutionDate", 0);
      
      Fluxion.doAjax(
    	        'libraryControllerHelperService',
    	        'getTargetedResequencingTypes',
    	        {
    	          'url': ajaxurl,
    	          'libraryPrepKitId': libraryPrepKitId
    	        },
    	        {'doOnSuccess': function (json) {
    	        	var selectElem = jQuery('#libraryDilutionTargetedResequencing');
    	        	if(json.targetedResequencings.length == 0) {
    	        		selectElem.append(new Option('NONE', 0));
    	        	} else {
    	        		selectElem.append(new Option('--- select'));
    	        		selectElem.append(new Option('NONE', 0));
                        json.targetedResequencings.sort(function(a, b) {
                           return (a.alias > b.alias) - (a.alias < b.alias);
                        });
        	        	jQuery.each(json.targetedResequencings, function(index, item) {
        	        		selectElem.append(new Option(item.alias, item.targetedSequencingId));
        	        		});
    	        	}
    	        }
    	        }
    	      );
    } else {
      alert("Cannot add another dilution when one is already in progress.");
    }
  },

  addLibraryDilution: function () {
	  
   jQuery('#libraryDilutionTargetedResequencing').attr('class', 'form-control');
   jQuery('#libraryDilutionTargetedResequencing').attr('data-parsley-required', 'true');
   jQuery('#libraryDilutionTargetedResequencing').attr('data-parsley-type', 'number');  
   
   jQuery('#addDilutionForm').parsley();
   if(!jQuery('#addDilutionForm').parsley().validate()) {
	   return;
   }
	  
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
        'libraryDilutionTargetedResequencing': f.libraryDilutionTargetedResequencing,
        'url': ajaxurl},
      {'updateElement': 'libraryDilutionTable',
        'doOnSuccess': function () {
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

  deleteLibraryDilution: function (libraryDilutionId) {
    if (confirm("Are you sure you really want to delete LDI" + libraryDilutionId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteLibraryDilution',
        {'libraryDilutionId': libraryDilutionId, 'url': ajaxurl},
        {'doOnSuccess': window.location.reload(true)
        }
      );
    }
  }
};

Library.empcr = {
  insertEmPcrRow: function (dilutionId) {
    if (!jQuery('#emPcrTable').attr("pcrInProgress")) {
      jQuery('#emPcrTable').attr("pcrInProgress", "true");

      jQuery('#emPcrTable')[0].insertRow(1);

      var column2 = jQuery('#emPcrTable')[0].rows[1].insertCell(-1);
      column2.innerHTML = "" + dilutionId + " <input type='hidden' id='dilutionId' name='dilutionId' value='" + dilutionId + "'/>";
      var column3 = jQuery('#emPcrTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='emPcrCreator' name='emPcrCreator' type='hidden' value='" + jQuery('#currentUser')[0].innerHTML + "'/>" + jQuery('#currentUser')[0].innerHTML;
      var column4 = jQuery('#emPcrTable')[0].rows[1].insertCell(-1);
      column4.innerHTML = "<input id='emPcrDate' name='emPcrDate' type='text'/>";
      var column5 = jQuery('#emPcrTable')[0].rows[1].insertCell(-1);
      column5.innerHTML = "<input id='emPcrResults' name='emPcrResults' type='text'/>";
      var column6 = jQuery('#emPcrTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Library.empcr.addEmPcr();'/>Add</a>";

      Utils.ui.addMaxDatePicker("emPcrDate", 0);
    }
    else {
      alert("Cannot add another emPCR when one is already in progress.");
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
        'doOnSuccess': function () {
          jQuery('#emPcrTable').removeAttr("pcrInProgress");
        }
      }
    );
  },

  insertEmPcrDilutionRow: function (emPcrId) {
    if (!jQuery('#emPcrDilutionTable').attr("dilutionInProgress")) {
      jQuery('#emPcrDilutionTable').attr("dilutionInProgress", "true");

      jQuery('#emPcrDilutionTable')[0].insertRow(1);

      var column2 = jQuery('#emPcrDilutionTable')[0].rows[1].insertCell(-1);
      column2.innerHTML = "" + emPcrId + " <input type='hidden' id='emPcrId' name='emPcrId' value='" + emPcrId + "'/>";
      var column3 = jQuery('#emPcrDilutionTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='emPcrDilutionCreator' name='emPcrDilutionCreator' type='hidden' value='" + jQuery('#currentUser')[0].innerHTML + "'/>" + jQuery('#currentUser')[0].innerHTML;
      var column4 = jQuery('#emPcrDilutionTable')[0].rows[1].insertCell(-1);
      column4.innerHTML = "<input id='emPcrDilutionDate' name='emPcrDilutionDate' type='text'/>";
      var column6 = jQuery('#emPcrDilutionTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<input id='emPcrDilutionResults' name='emPcrDilutionResults' type='text'/>";
      var column7 = jQuery('#emPcrDilutionTable')[0].rows[1].insertCell(-1);
      column7.innerHTML = "<a href='javascript:void(0);' onclick='Library.empcr.addEmPcrDilution();'/>Add</a>";

      Utils.ui.addMaxDatePicker("emPcrDilutionDate", 0);
    }
    else {
      alert("Cannot add another dilution when one is already in progress.");
    }
  },

  addEmPcrDilution: function () {
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
        'doOnSuccess': function () {
          jQuery('#emPcrDilutionTable').removeAttr("dilutionInProgress");
        }
      }
    );
  },

  deleteEmPCR: function (empcrId) {
    if (confirm("Are you sure you really want to delete EmPCR " + empcrId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteEmPCR',
        {'empcrId': empcrId, 'url': ajaxurl},
        {'doOnSuccess': window.location.reload(true)
        }
      );
    }
  },

  deleteEmPCRDilution: function (empcrDilutionId) {
    if (confirm("Are you sure you really want to delete EmPCRDilution" + empcrDilutionId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteEmPCRDilution',
        {'empcrDilutionId': empcrDilutionId, 'url': ajaxurl},
        {'doOnSuccess': window.location.reload(true)
        }
      );
    }
  }
};

Library.barcode = {
  editLibraryBarcode: function(span, id) {
    Fluxion.doAjax(
      'loggedActionService',
      'logAction',
      {
        'objectId': id,
        'objectType': 'Library',
        'action': 'editLibraryIdBarcode',
        'url': ajaxurl
      },
      {}
    );

    var v = span.find('a').text();
    if (v && v !== "") {
      span.html("<input type='text' value='" + v + "' name='identificationBarcode' id='identificationBarcode'>");
    }
  },

  showLibraryIdBarcodeChangeDialog: function (libraryId, libraryIdBarcode) {
    var self = this;
    jQuery('#changeLibraryIdBarcodeDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<strong><label>Current Barcode: </label></strong>" + libraryIdBarcode +
            "<br /><strong><label for='notetext'>New Barcode:</label></strong>" +
            "<input type='text' name='idBarcodeInput' id='idBarcodeInput' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery('#changeLibraryIdBarcodeDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function () {
          self.changeLibraryIdBarcode(libraryId, jQuery('#idBarcodeInput').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  changeLibraryIdBarcode: function (libraryId, idBarcode) {
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'changeLibraryIdBarcode',
      {
        'libraryId': libraryId,
        'identificationBarcode': idBarcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

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

          jQuery('#printServiceSelectDialog').dialog({
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

          jQuery('#printServiceSelectDialog').dialog({
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
    });
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
              if (json.numApplicableBarcodes > 1 && i === 0) {
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
          if (firstSelText.indexOf("Select") === 0) {
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
                    if (json.numApplicableBarcodes > 1 && i === 0) {
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

    jQuery('#addLibraryNoteDialog').dialog({
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
      {
        'doOnSuccess': function (json) {
          jQuery('#listingLibrariesTable').html('');
          jQuery('#listingLibrariesTable').dataTable({
            "aaData": json.array,
            "aoColumns": [
              { "sTitle": "Bulk Edit" },
              { "sTitle": "Library Name", "sType": "no-lib"},
              { "sTitle": "Alias"},
              { "sTitle": "Type"},
              { "sTitle": "Sample Name", "sType": "no-sam"},
              { "sTitle": "QC Passed"},
              { "sTitle": "ID", "bVisible": false}
            ],
            "bJQueryUI": true,
            "bAutoWidth": false,
            "iDisplayLength": 25,
            "aaSorting": [
              [1, "desc"]
            ]
          });
          jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
          jQuery("#toolbar").append("<button style=\"margin-left:5px;\" onclick=\"window.location.href='/miso/sample/new';\" class=\"fg-button ui-state-default ui-corner-all\">Add Sample</button>");
          
          jQuery("input[class='bulkCheckbox']").click(function () {
            if (jQuery(this).parent().parent().hasClass('row_selected')) {
              jQuery(this).parent().parent().removeClass('row_selected');
            } else if (!jQuery(this).parent().parent().hasClass('row_selected')) {
              jQuery(this).parent().parent().addClass('row_selected');
            }
          });
          
          var selectAll = '<label><input type="checkbox" onchange="Library.ui.checkAll(this)" id="checkAll">Select All</label>';
          document.getElementById('listingLibrariesTable').insertAdjacentHTML('beforebegin', selectAll);
          
          var actions = ['<select id="dropdownActions"><option value="">-- Bulk actions</option>'];
          actions.push('<option value="update">Update selected</option>');
          actions.push('<option value="dilutions">Make dilutions from selected</option>');
          actions.push('<option value="empty">Empty selected</option>');
          actions.push('<option value="archive">Archive selected</option>');
          actions.push('</select>');
          document.getElementById('listingLibrariesTable').insertAdjacentHTML('afterend', actions.join(''));
          var saveButton = '<button id="go" type="button" onclick="Library.ui.handleBulkAction();">Go</button>';
          document.getElementById('dropdownActions').insertAdjacentHTML('afterend', saveButton);
        }
      }
    );
  },
  
  checkAll: function (el) {
    var checkboxes = document.getElementsByClassName('bulkCheckbox');
    if (el.checked) {
      for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = true;
      }
    } else {
      for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].checked = false;
      }
    }
  },
  
  handleBulkAction: function () {
    var selectedValue = document.getElementById('dropdownActions').value;
    var options = {
      "update": Library.ui.updateSelectedItems,
      "dilutions": Library.ui.makeDilutionsFromSelectedItems,
      "empty": Library.ui.emptySelectedItems,
      "archive": Library.ui.archiveSelectedItems
    }
    var action = options[selectedValue];
    action();
  },
  
  // get array of selected IDs
  getSelectedIds: function () {
    return [].slice.call(document.getElementsByClassName('bulkCheckbox'))
             .filter(function (input) { return input.checked; })
             .map(function (input) { return input.value; });
  },
  
  updateSelectedItems: function () {
    var selectedIdsArray = Library.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Libraries to update.");
      return false;
    }
    alert("Check if controller methods have been implemented.");
    // window.location = "library/bulk/" + selectedIdsArray.join(',');
  },
  
  // TODO: finish this, and the one in sample_ajax.js
  makeDilutionsFromSelectedItems: function () {
    var selectedIdsArray = Library.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Libraries to dilute.");
      return false;
    }
    var cageDiv = '<div id="cageDialog"><span class="dialog">Look for this feature in the next release!<br>' 
      + '<img src="http://images.mentalfloss.com/sites/default/files/styles/insert_main_wide_image/public/uncanny_valley.jpg"/></span></div>';
    document.getElementById('go').insertAdjacentHTML('afterend', cageDiv);
    jQuery('#cageDialog').dialog({
      modal: true,
      width: 640,
      buttons: {
        "Ok": function () {
          jQuery(this).dialog("close");
        }
      }
    });
  },
  
  // TODO: finish this, and the one in sample_ajax.js
  emptySelectedItems: function () {
    var selectedIdsArray = Library.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Libraries to empty.");
      return false;
    }
    var cageDiv = '<div id="cageDialog"><span class="dialog">Look for this feature in the next release!<br>' 
      + '<img src="http://nicolascage.us/wp-content/uploads/2013/08/Comet-Cage1.jpg"/></span></div>';
    document.getElementById('go').insertAdjacentHTML('afterend', cageDiv);
    jQuery('#cageDialog').dialog({
      modal: true,
      width: 620,
      buttons: {
        "Ok": function () {
          jQuery(this).dialog("close");
        }
      }
    });
  },
  
  //TODO: finish this, and the one in sample_ajax.js
  archiveSelectedItems: function () {
    var selectedIdsArray = Library.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Libraries to archive.");
      return false;
    }
    var cageDiv = '<div id="cageDialog"><span class="dialog">Look for this feature in the next release!<br>' 
      + '<img src="http://i2.listal.com/image/1675309/600full-fast-times-at-ridgemont-high-screenshot.jpg"/></span></div>';
    document.getElementById('go').insertAdjacentHTML('afterend', cageDiv);
    jQuery('#cageDialog').dialog({
      modal: true,
      width: 620,
      buttons: {
        "Ok": function () {
          jQuery(this).dialog("close");
        }
      }
    });
  },

  changeDesign: function() {
    var designSelect = document.getElementById('libraryDesignTypes');
    var selection = document.getElementById('librarySelectionTypes');
    var strategy = document.getElementById('libraryStrategyTypes');
    var paired = document.getElementById('paired');
    var platform = document.getElementById('platformNames');
    if (designSelect.value == -1) {
      selection.disabled = false;
      strategy.disabled = false;
      paired.disabled = false;
      platform.disabled = false;
    } else {
      var matchedRules = Library.propagationRules.filter(function (rule) { return rule.id == designSelect.value; });
      if (matchedRules.length == 1) {
        selection.value = matchedRules[0].librarySelectionType;
        selection.disabled = true;
        strategy.value = matchedRules[0].libraryStrategyType;
        strategy.disabled = true;
        paired.checked = matchedRules[0].paired;
        paired.disabled = true;
        platform.checked = matchedRules[0].platform;
        platform.disabled = true;
      }
    }
  }
};
