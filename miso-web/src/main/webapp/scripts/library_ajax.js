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

//Custom Parsley validator to validate Library alias server-side
window.Parsley.addValidator('libraryAlias', {
  validateString: function(value) {
    var deferred = new jQuery.Deferred();
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'validateLibraryAlias',
      {
        'alias': value,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          deferred.resolve();
        },
        'doOnError': function(json) {
          deferred.reject(json.error);
        }
      }
    );
    return deferred.promise();
  },
  messages: {
    en: 'Alias must conform to the naming scheme.'
  }
});

var Library = Library || {
  deleteLibrary: function (libraryId) {
    if (confirm("Are you sure you really want to delete LIB" + libraryId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteLibrary',
        {
          'libraryId': libraryId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function () {
            window.location.href = '/miso/libraries';
          }
        }
      );
    }
  },
  
  validateLibrary: function (skipAliasValidation) {
    Validate.cleanFields('#library-form');
    jQuery('#library-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-maxlength', '100');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    if (skipAliasValidation) {
      jQuery('#alias').attr('data-parsley-required', 'true');
    } else {
      jQuery('#alias').attr('data-parsley-library-alias', '');
      jQuery('#alias').attr('data-parsley-debounce', '500');
    }

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    
    // Library size validation
    jQuery('#dnaSize').attr('class', 'form-control');
    jQuery('#dnaSize').attr('data-parsley-maxlength', '10');
    jQuery('#dnaSize').attr('data-parsley-type', 'number');

    // Volume validation
    jQuery('#volume').attr('class', 'form-control');
    jQuery('#volume').attr('data-parsley-maxlength', '10');
    jQuery('#volume').attr('data-parsley-type', 'number');

    if (Constants.isDetailedSample) {
      var generatingAlias = Constants.automaticLibraryAlias == true && jQuery('#alias').val().length === 0;
      var selectedPlatform = jQuery('#platformTypes option:selected').text();
      
      jQuery('#dnaSize').attr('data-parsley-required', generatingAlias && selectedPlatform === 'Illumina');
      
      // Prep Kit validation
      jQuery('#libraryKit').attr('class', 'form-control');
      jQuery('#libraryKit').attr('data-parsley-required', 'true');
      
      jQuery('#libraryDesignCodes').attr('class', 'form-control');
      jQuery('#libraryDesignCodes').attr('data-parsley-required', 'true');
      jQuery('#libraryDesignCodes').attr('data-parsley-min', 1);
      
      // Concentration validation
      jQuery('#initialConcentration').attr('class', 'form-control');
      jQuery('#initialConcentration').attr('data-parsley-maxlength', '10');
      jQuery('#initialConcentration').attr('data-parsley-type', 'number');
      jQuery('#initialConcentration').attr('data-parsley-required', generatingAlias && selectedPlatform === 'PacBio');
    }

    jQuery('#library-form').parsley();
    jQuery('#library-form').parsley().validate();
    
    Validate.updateWarningOrSubmit('#library-form');
  },

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
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Library.qc.addLibraryQC();'/>Add</a>";

      Utils.ui.addMaxDatePicker("libraryQcDate", 0);

      Fluxion.doAjax(
        'libraryControllerHelperService',
        'getLibraryQcTypes',
        {
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
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
        'url': ajaxurl
      },
      {
        'updateElement': 'libraryQcTable',
        'doOnSuccess': function () {
          jQuery('#libraryQcTable').removeAttr("qcInProgress");
        }
      }
    );
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
      {
        'doOnSuccess': function (json) {
          jQuery('#result' + qcId).html(json.results);
          jQuery('#insert' + qcId).html(json.insertSize);
          jQuery('#edit' + qcId).html(json.edit);
        }
      }
    );
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
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  }
};

Library.dilution = {
  insertLibraryDilutionRow: function (libraryId, libraryPrepKitId, autoGenerateIdBarcodes) {
    if (!jQuery('#libraryDilutionTable').attr("dilutionInProgress")) {
      jQuery('#libraryDilutionTable').attr("dilutionInProgress", "true");

      jQuery('#libraryDilutionTable')[0].insertRow(1);
      //dilutionId    Done By   Dilution Date Results
      var column1 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column1.innerHTML = "<input id='name' name='name' type='hidden' value='Unsaved '/>Unsaved";
      var column2 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column2.innerHTML = "<input id='libraryDilutionCreator' name='libraryDilutionCreator' type='hidden' value='" + jQuery('#currentUser')[0].innerHTML + "'/>" + jQuery('#currentUser')[0].innerHTML;
      var column3 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='libraryDilutionDate' name='libraryDilutionDate' type='text'/>";
      var column6 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<input id='libraryDilutionResults' name='libraryDilutionResults' type='text' onchange='Library.qc.changeLibraryQcUnits(this);'/>";
      if (Constants.isDetailedSample) {
        var column7 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
        column7.innerHTML = "<select id='libraryDilutionTargetedSequencing' name='libraryDilutionTargetedSequencing'/>"
      }
      var column8 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      if (autoGenerateIdBarcodes) {
        column8.innerHTML = "<i>Generated on save</i>";
      } else {
        column8.innerHTML = "<input id='libraryDilutionIdBarcode' name='libraryDilutionIdBarcode' type='text'/>";
      }
      var column9 = jQuery('#libraryDilutionTable')[0].rows[1].insertCell(-1);
      column9.innerHTML = "<a href='javascript:void(0);' onclick='Library.dilution.addLibraryDilution(" + autoGenerateIdBarcodes + ");'/>Add</a>";

      Utils.ui.addMaxDatePicker("libraryDilutionDate", 0);
      
      if (Constants.isDetailedSample) {
        Fluxion.doAjax(
          'libraryControllerHelperService',
          'getTargetedSequencingTypes',
          {
            'url': ajaxurl,
            'libraryPrepKitId': libraryPrepKitId
          },
          {
            'doOnSuccess': function (json) {
  	        	var selectElem = jQuery('#libraryDilutionTargetedSequencing');
  	        	selectElem.append(new Option('NONE', 0));
  	        	if (json.targetedSequencings.length > 0) {
                json.targetedSequencings.sort(function(a, b) {
                   return a.alias.localeCompare(b.alias);
                });
               jQuery.each(json.targetedSequencings, function(index, item) {
               	 selectElem.append(new Option(item.alias, item.targetedSequencingId));
               });
  	        	}
            }
          }
        );
      }
    } else {
      alert("Cannot add another dilution when one is already in progress.");
    }
  },

  addLibraryDilution: function (autoGenerateIdBarcodes) {
    var f = Utils.mappifyForm("addDilutionForm");
    var params = {
      'libraryId': f.id,
      'dilutionCreator': f.libraryDilutionCreator,
      'dilutionDate': f.libraryDilutionDate,
      'results': f.libraryDilutionResults,
      'autoGenerateIdBarcodes': autoGenerateIdBarcodes,
      'detailedSample': Constants.isDetailedSample,
      'url': ajaxurl
    };
 	  if (Constants.isDetailedSample) {
      jQuery('#libraryDilutionTargetedSequencing').attr('class', 'form-control');
      jQuery('#libraryDilutionTargetedSequencing').attr('data-parsley-required', 'true');
      jQuery('#libraryDilutionTargetedSequencing').attr('data-parsley-type', 'number');  
  
      jQuery('#addDilutionForm').parsley();
      if (!jQuery('#addDilutionForm').parsley().validate()) {
  	    return;
      }
      params['targetedSequencing'] = f.libraryDilutionTargetedSequencing;
	  }
 	  if (!autoGenerateIdBarcodes) {
 	    params['idBarcode'] = f.libraryDilutionIdBarcode;
 	  }

    Fluxion.doAjax(
      'libraryControllerHelperService',
      'addLibraryDilution',
      params,
      {
        'updateElement': 'libraryDilutionTable',
        'doOnSuccess': function () {
          jQuery('#libraryDilutionTable').removeAttr("dilutionInProgress");
        }
      }
    );
  },

  changeLibraryDilutionRow: function (dilutionId, autoGenerateIdBarcodes, detailedSample) {
    if (jQuery('#tarSeq' + dilutionId).length > 0) {
      var targetedSequencingAlias = jQuery.trim(jQuery('#tarSeq' + dilutionId).text());
    }
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'changeLibraryDilutionRow',
      {
        'dilutionId': dilutionId,
        'autoGenerateIdBarcodes': autoGenerateIdBarcodes,
        'detailedSample': detailedSample,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#results' + dilutionId).html(json.results);
          jQuery('#edit' + dilutionId).html(json.edit);
          if (detailedSample) {
            jQuery('#tarSeq' + dilutionId).html(Library.dilution.makeTargetedSequencingsDropdown(json.targetedSequencings, dilutionId, targetedSequencingAlias));
          }
          if (!autoGenerateIdBarcodes) jQuery('#idBarcode' + dilutionId).html(json.idBarcode);
        }
      }
    );
  },

  editLibraryDilution: function (dilutionId, autoGenerateIdBarcodes, detailedSample) {
    var params = {
        'dilutionId': dilutionId,
        'result': jQuery('#' + dilutionId).val(),
        'url': ajaxurl
    };
    if (detailedSample) params['targetedSequencing'] = jQuery('#targetedSequencing' + dilutionId).val();
    if (!autoGenerateIdBarcodes) params['idBarcode'] = jQuery('#idBarcodeValue' + dilutionId).val();
    
    Fluxion.doAjax(
      'libraryControllerHelperService',
      'editLibraryDilution',
      params,
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  deleteLibraryDilution: function (libraryDilutionId) {
    if (confirm("Are you sure you really want to delete LDI" + libraryDilutionId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteLibraryDilution',
        {
          'libraryDilutionId': libraryDilutionId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': window.location.reload(true)
        }
      );
    }
  },
  
  makeTargetedSequencingsDropdown: function (targetedSequencingsMap, dilutionId, currentAlias) {
    var dropdown = ['<select id="targetedSequencing' + dilutionId + '">'];
    dropdown.push('<option value="">NONE</option>');
    dropdown.push(targetedSequencingsMap.map(function (tarseq) { return '<option value="' + tarseq.targetedSequencingId + '" ' + (currentAlias == tarseq.alias ? 'selected="selected"' : '') + '>' + tarseq.alias + '</option>'; }));
    dropdown.push('</select>');
    return dropdown.join('');
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
                    'printerId': jQuery('#serviceSelect').val(),
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
                    'printerId': jQuery('#serviceSelect').val(),
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
      {
        'libraryId': libraryId,
        'locationBarcode': barcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  }
};

Library.ui = {
  updateConcentrationUnits: function() {
    var platformType = Library.ui.getSelectedPlatformType();
    // default to 'nM' before platform is chosen
    var units = platformType == null ? 'nM' : platformType.libraryConcentrationUnits;
    jQuery('#concentrationUnits').text(units);
  },
    
  changePlatformType: function (originalLibraryTypeId, callback) {
    var platformType = Library.ui.getSelectedPlatformType();

    var indexFamilySelect = jQuery('#indexFamily').empty()[0];
    Constants.indexFamilies.filter(function(family) { return !family.platformType || family.platformType == platformType.name; }).sort(function(a, b) {
      if (a.id == b.id) return 0;
      if (a.id == 0) return -1;
      if (b.id == 0) return 1;
      return a.name.localeCompare(b.name);
    }).map(function(family) {
      var option = document.createElement("option");
      option.value = family.id;
      option.text = family.name;
      return option;
    }).forEach(function(o) { indexFamilySelect.appendChild(o); });

    var libraryTypesSelect = jQuery('#libraryTypes').empty()[0];
    Constants.libraryTypes.filter(function(type) { return type.platform == platformType.name && (!type.archived || type.id == originalLibraryTypeId); }).sort(function(a, b) {
      return a.alias.localeCompare(b.alias);
    }).map(function(type) {
      var option = document.createElement("option");
      option.value = type.id;
      option.text = type.alias;
      return option;
    }).forEach(function(o) { libraryTypesSelect.appendChild(o); });
    Library.ui.updateIndices();
    Library.ui.updateConcentrationUnits();
    if (callback) {
      callback();
    }
  },
  
  getSelectedPlatformType: function() {
    var platformTypeKey = jQuery('#platformTypes').val();
    return Constants.platformTypes.filter(function(pt) { return pt.key == platformTypeKey; })[0];
  },

  updateIndices: function () {
    jQuery('#indicesDiv').empty();
    Library.lastIndexPosition = 0;
    Library.ui.createIndexNextBox();
  },

  createIndexBox: function(id) {
    if (typeof id == 'undefined') return;
    var selectedIndex = Library.ui.getCurrentIndexFamily().indices.filter(function(index) { return index.id == id; })[0];
    Library.ui.createIndexSelect(selectedIndex.position, id);
  },

  maxOfArray: function(array) {
    return Math.max(0, Math.max.apply(Math, array));
  },

  maxIndexPositionInFamily: function(family) {
    return Library.ui.maxOfArray(family.indices.map(function(index) { return index.position; }));
  },

  createIndexNextBox: function() {
    var family = Library.ui.getCurrentIndexFamily();
    var max = Library.ui.maxIndexPositionInFamily(family);
    if (Library.lastIndexPosition < max) {
      Library.ui.createIndexSelect(max, 0);
    } else if (Library.lastIndexPosition == 0) {
      var container = jQuery('#indicesDiv');
      if (container.children().length == 0) {
        container.text("No indices available.");
      }
    }
    var container = document.getElementById('indicesDiv');
    // If this index family requires fewer indices than previously selected, we need to null them out in the form input or Spring will create an array with a mix of new and old indices.
    var biggestMax = Library.ui.maxOfArray(Constants.indexFamilies.map(Library.ui.maxIndexPositionInFamily));
    for (var j = Library.lastIndexPosition; j < biggestMax; j++) {
       var nullInput = document.createElement("input");
       nullInput.type = "hidden";
       nullInput.value = "";
       nullInput.name = "indices[" + j + "]";
       container.appendChild(nullInput);
    }
  },

  getCurrentIndexFamily: function() {
    var familyId = jQuery('#indexFamily').val();
    var families = Constants.indexFamilies.filter(function(family) { return family.id == familyId; });
    if (families.length == 0) {
      return { id : 0, indices :  [] };
    } else {
      return families[0];
    }
  },

  createIndexSelect: function(newPosition, selectedId) {
    var container = document.getElementById('indicesDiv');
    for (var position = Library.lastIndexPosition + 1; position <= newPosition; position++) {
      var widget = document.createElement("select");
      widget.id = "index" + position;
      widget.name = "indices[" + (position - 1) + "]";
      if (position > 1) {
        var nullOption = document.createElement("option");
        nullOption.value = "";
        nullOption.text = "(None)";
        widget.appendChild(nullOption);
      }
      var indices = Library.ui.getCurrentIndexFamily().indices.filter(function(index) { return index.position == position; });
      for (var i = 0; i < indices.length; i++) {
        var option = document.createElement("option");
        option.value = indices[i].id;
        option.text = indices[i].name + " (" + indices[i].sequence + ")";
        widget.appendChild(option);
      }
      if (position == newPosition) {
        widget.value = selectedId;
      }
      container.appendChild(widget);
    }
    Library.lastIndexPosition = newPosition;
  },

  fillDownIndexFamilySelects: function (tableselector, th) {
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

      jQuery.get('../../library/indexPositionsJson', {indexFamily: tdtext}, {
        success: function (json) {
          tableObj.find("tr:gt(" + frId + ")").each(function () {
            var c = this.cells[col + 1];
            jQuery(c).html("");
            for (var i = 0; i < json.numApplicableIndices; i++) {
              jQuery(c).append("<span class='indexSelectDiv' position='" + (i + 1) + "' id='indices" + (i + 1) + "'>- <i>Select...</i></span>");
              if (json.numApplicableIndices > 1 && i === 0) {
                jQuery(c).append("|");
              }
            }

            //bind editable to selects
            jQuery("#cinput .indexSelectDiv").editable(function (value, settings) {
              return value;
            },
            {
              loadurl: '../../library/indicesForPosition',
              loaddata: function (value, settings) {
                var ret = {};
                ret["position"] = jQuery(this).attr("position");
                if (!Utils.validation.isNullCheck(tdtext)) {
                  ret['indexFamily'] = tdtext;
                } else {
                  ret['indexFamily'] = '';
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

  fillDownIndexSelects: function (tableselector, th) {
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
        var ifam = this.cells[col - 1];
        var ifamText = jQuery(ifam).text();
        var cell = jQuery(this.cells[col]);

        if (ifamText.trim()) {
          //no select means empty or already filled
          if (firstSelText.indexOf("Select") === 0) {
            //same family, just copy the cell
            if (firstSelText === ifamText) {
              cell.html(tdtext);
            } else {
              jQuery.get('../../library/indexPositionsJson', {indexFamily: ifamText}, {
                success: function (json) {
                  cell.html("");
                  for (var i = 0; i < json.numApplicableIndices; i++) {
                    cell.append("<span class='indexSelectDiv' position='" + (i + 1) + "' id='indices" + (i + 1) + "'>- <i>Select...</i></span>");
                    if (json.numApplicableIndices > 1 && i === 0) {
                      cell.append("|");
                    }
                  }
                }
              });
            }
          }
          else {
            //just copy select
            if (firstSelText === ifamText) {
              cell.html(tdtext);
            }
          }
        }

        //bind editable to selects
        jQuery("#cinput .indexSelectDiv").editable(function (value, settings) {
          return value;
        },
        {
          loadurl: '../../library/indicesForPosition',
          loaddata: function (value, settings) {
            var ret = {};
            ret["position"] = jQuery(this).attr("position");
            if (!Utils.validation.isNullCheck(ifamText)) {
              ret['indexFamily'] = ifamText;
            }
            else {
              ret['indexFamily'] = '';
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
            "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' autofocus />" +
            "</fieldset></form>");

    jQuery('#addLibraryNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function () {
          if (jQuery('#notetext').val().length > 0) {
            self.addLibraryNote(libraryId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          } else {
            jQuery('#notetext').focus();
          }
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
      {
        'libraryId': libraryId,
        'internalOnly': internalOnly,
        'text': text,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  deleteLibraryNote: function (libraryId, noteId) {
    if (confirm("Are you sure you want to delete this note?")) {
      Fluxion.doAjax(
        'libraryControllerHelperService',
        'deleteLibraryNote',
        {
          'libraryId': libraryId,
          'noteId': noteId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload
        }
      );
    }
  },

  changeDesign: function(callback) {
    var designSelect = document.getElementById('libraryDesignTypes');
    var selection = document.getElementById('librarySelectionTypes');
    var strategy = document.getElementById('libraryStrategyTypes');
    var code = document.getElementById('libraryDesignCodes');
    if (designSelect == null || designSelect.value == -1) {
      selection.disabled = false;
      strategy.disabled = false;
      if (code) { code.disabled = false; }
      if (typeof callback == 'function') callback();
    } else {
      var matchedDesigns = Constants.libraryDesigns.filter(function (rule) { return rule.id == designSelect.value; });
      if (matchedDesigns.length == 1) {
        selection.value = matchedDesigns[0].selectionId;
        selection.disabled = true;
        strategy.value = matchedDesigns[0].strategyId;
        strategy.disabled = true;
        if (code) {
          code.value = matchedDesigns[0].designCodeId;
          code.disabled = true;
        }
      }
    }
  }
};
