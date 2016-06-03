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
        {
          'sampleId': sampleId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            successfunc();
          }
        }
      );
    }
  },

  removeSampleFromGroup: function(sampleId, sampleGroupId, successfunc) {
    if (confirm("Are you sure you really want to remove SAM" + sampleId + " from Sample group "+sampleGroupId+"?")) {
      Fluxion.doAjax(
        'sampleControllerHelperService',
        'removeSampleFromGroup',
        {
          'sampleId': sampleId,
          'sampleGroupId':sampleGroupId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            successfunc();
          }
        }
      );
    }
  },
  
  validateSample: function (isDetailedSample, isNewSample) {
    Validate.cleanFields('#sample-form');
    jQuery('#sample-form').parsley().destroy();

    // Alias input field validation
    // 'data-parsley-required' attribute is set in JSP based on whether alias generation is enabled
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-maxlength', '100');

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-required', 'true');
    jQuery('#description').attr('data-parsley-maxlength', '100');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Project validation
    jQuery('#project').attr('class', 'form-control');
    jQuery('#project').attr('data-parsley-required', 'true');
    jQuery('#project').attr('data-parsley-error-message', 'You must select a project.');

    // Date of Receipt validation: ensure date is of correct form
    jQuery('#receiveddatepicker').attr('class', 'form-control');
    jQuery('#receiveddatepicker').attr('data-date-format', 'DD/MM/YYYY');
    jQuery('#receiveddatepicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#receiveddatepicker').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');

    // Sample Type validation
    jQuery('#sampleTypes').attr('class', 'form-control');
    jQuery('#sampleTypes').attr('required', 'true');
    jQuery('#sampleTypes').attr('data-parsley-error-message', 'You must select a Sample Type');
    jQuery('#sampleTypes').attr('data-parsley-errors-container', '#sampleTypesError');
    
    // Scientific Name validation
    jQuery('#scientificName').attr('class', 'form-control');
    jQuery('#scientificName').attr('data-parsley-required', 'true');
    jQuery('#scientificName').attr('data-parsley-maxlength', '100');
    jQuery('#scientificName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Volume validation
    jQuery('#volume').attr('class', 'form-control');
    jQuery('#volume').attr('data-parsley-required', 'true');
    jQuery('#volume').attr('data-parsley-maxlength', '10');
    jQuery('#volume').attr('data-parsley-type', 'number');

    if (isDetailedSample) {
      
      if (isNewSample) {
        // External Name validation
        jQuery('#externalName').attr('class', 'form-control');
        jQuery('#externalName').attr('data-parsley-required', 'true');
        jQuery('#externalName').attr('data-parsley-maxlength', '255');
        jQuery('#externalName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
      }
      
      // SampleClass validation
      jQuery('#sampleClass').attr('class', 'form-control');
      jQuery('#sampleClass').attr('data-parsley-required', 'true');
      
      // TissueOrigin validation
      jQuery('#tissueOrigin').attr('class', 'form-control');
      jQuery('#tissueOrigin').attr('data-parsley-required', 'true');
      
      // TissueType validation
      jQuery('#tissueType').attr('class', 'form-control');
      jQuery('#tissueType').attr('data-parsley-required', 'true');
      
      // External Institute Identifier validation
      jQuery('#externalInstituteIdentifier').attr('class', 'form-control');
      jQuery('#externalInstituteIdentifier').attr('data-parsley-maxlength', '255');
      jQuery('#externalInstituteIdentifier').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
      
      // PassageNumber validation
      jQuery('#passageNumber').attr('class', 'form-control');
      jQuery('#passageNumber').attr('data-parsley-type', 'integer');
      
      // TimesReceived validation
      jQuery('#timesReceived').attr('class', 'form-control');
      jQuery('#timesReceived').attr('data-parsley-required', 'true');
      jQuery('#timesReceived').attr('data-parsley-type', 'integer');
      
      // TubeNumber validation
      jQuery('#tubeNumber').attr('class', 'form-control');
      jQuery('#tubeNumber').attr('data-parsley-required', 'true');
      jQuery('#tubeNumber').attr('data-parsley-type', 'integer');
      
      // Concentration validation
      jQuery('#concentration').attr('class', 'form-control');
      jQuery('#concentration').attr('data-parsley-type', 'number');
        
      // Group ID validation
      jQuery('#groupId').attr('class', 'form-control');
      jQuery('#groupId').attr('data-parsley-type', 'integer');
      
      // Group Description validation
      jQuery('#groupDescription').attr('class', 'form-control');
      jQuery('#groupDescription').attr('data-parsley-maxlength', '255');
      jQuery('#groupDescription').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
      
      var selectedId = jQuery('#sampleClass option:selected').val();
      var sampleCategory = Sample.options.getSampleCategoryByClassId(selectedId);
      switch (sampleCategory) {
      case 'Tissue':
        // Cellularity validation
        jQuery('#cellularity').attr('class', 'form-control');
        jQuery('#cellularity').attr('data-parsley-type', 'integer');
        break;
      case 'Analyte':
        // Region validation
        jQuery('#region').attr('class', 'form-control');
        jQuery('#region').attr('data-parsley-maxlength', '255');
        jQuery('#region').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
        
        // TubeId validation
        jQuery('#tubeId').attr('class', 'form-control');
        jQuery('#tubeId').attr('data-parsley-maxlength', '255');
        jQuery('#tubeId').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
        break;
      }
    }
    
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'getSampleAliasRegex',
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
          jQuery('#sample-form').parsley();
          jQuery('#sample-form').parsley().validate();
          Validate.updateWarningOrSubmit('#sample-form', Sample.validateSampleAlias);
          return false;
        },
        'doOnError': function(json) {
          alert(json.error);
        }
      }
    );
  },
  
  validateSampleAlias: function () {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'validateSampleAlias',
      {
        'alias': jQuery('#alias').val(),
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          if (json.response === "OK") {
            jQuery('#sample-form').submit();
          }
        },
        'doOnError': function(json) {
          alert(json.error);
        }
      }
    );
  },
  
  validateNCBITaxon: function () {
    Fluxion.doAjax(
      'sampleControllerHelperService',
      'lookupNCBIScientificName',
      {
        'scientificName':jQuery('#scientificName').val(), 
        'url':ajaxurl
      },
      {
        'doOnSuccess': jQuery('#scientificName').removeClass().addClass("ok"),
        'doOnError': function(json) { 
          jQuery('#scientificName').removeClass().addClass("error"); alert(json.error); 
        }
      }
    );
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
      {
        'doOnSuccess': self.insertSampleQCRow
      }
    );
  },

  insertSampleQCRow: function (json, includeId) {
    if (!jQuery('#sampleQcTable').attr("qcInProgress")) {
      jQuery('#sampleQcTable').attr("qcInProgress", "true");

      jQuery('#sampleQcTable')[0].insertRow(1);
      //QCId  QCed By  	QC Date  	Method  	Results

      if (includeId) {
        var column1 = jQuery('#sampleQcTable')[0].rows[1].insertCell(-1);
        column1.innerHTML = "<input type='hidden' id='sampleId' name='sampleId' value='" + json.sampleId + "'/>";
      }

      var column2 = jQuery('#sampleQcTable')[0].rows[1].insertCell(-1);
      column2.innerHTML = "<select id='sampleQcUser' name='sampleQcUser'>" + json.qcUserOptions + "</select>";
      var column3 = jQuery('#sampleQcTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='sampleQcDate' name='sampleQcDate' type='text'/>";
      var column4 = jQuery('#sampleQcTable')[0].rows[1].insertCell(-1);
      column4.innerHTML = "<select id='sampleQcType' name='sampleQcType' onchange='Sample.qc.changeSampleQcUnits(this);'/>";
      var column5 = jQuery('#sampleQcTable')[0].rows[1].insertCell(-1);
      column5.innerHTML = "<input id='sampleQcResults' name='sampleQcResults' type='text'/><span id='units'/>";
      var column6 = jQuery('#sampleQcTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Sample.qc.addSampleQC();'/>Add</a>";

      Utils.ui.addMaxDatePicker("sampleQcDate", 0);

      Fluxion.doAjax(
        'sampleControllerHelperService',
        'getSampleQcTypes',
        {
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            jQuery('#sampleQcType').html(json.types);
            jQuery('#units').html(jQuery('#sampleQcType option:first').attr("units"));
          }
        }
      );
    } else {
      alert("Cannot add another QC when one is already in progress.");
    }
  },

  changeSampleQcUnits: function () {
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
        'url': ajaxurl
      },
      {
        'updateElement': 'sampleQcTable',
        'doOnSuccess': function () {
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
      {
        'doOnSuccess': Utils.page.pageReload
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
      obj.qcCreator = jQuery('#currentUser').text();
      obj.libraryId = obj.name.substring(3);
      aReturn.push(obj);
    }

    if (aReturn.length > 0) {
      if (Sample.library.validateLibraryQcs(aReturn)) {
        Fluxion.doAjax(
          'libraryControllerHelperService',
          'bulkAddLibraryQCs',
          {
            'qcs': aReturn,
            'url': ajaxurl
          },
          {
            'doOnSuccess': function(json) {
              Sample.library.processBulkLibraryQcTable(tableName, json);
            }
          }
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
      for (var j = 0; j < errors.length; j++) {
        errorStr += errors[j].error + "\n";
        jQuery(tableName).find("tr:gt(0)").each(function () {
          if (jQuery(this).attr("libraryId") === errors[j].libraryId) {
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
        obj.dilutionCreator = jQuery('#currentUser').text();
        obj.libraryId = obj.name.substring(3);
        aReturn.push(obj);
      }
    }

    if (aReturn.length > 0) {
      if (Sample.library.validateLibraryDilutions(aReturn)) {
        Fluxion.doAjax(
          'libraryControllerHelperService',
          'bulkAddLibraryDilutions',
          {
            'dilutions': aReturn,
            'url': ajaxurl
          },
          {
            'doOnSuccess': function(json) {
              self.processBulkLibraryDilutionTable(tableName, json);
            }
          }
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
      for (var j = 0; j < errors.length; j++) {
        errorStr += errors[j].error + "\n";
        jQuery(tableName).find("tr:gt(0)").each(function () {
          if (jQuery(this).attr("libraryId") === errors[j].libraryId) {
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
  
  validateLibraryQcs: function (json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if (!json[i].results.match(/[0-9\.]+/) ||
          !json[i].insertSize.match(/[0-9]+/) ||
          Utils.validation.isNullCheck(json[i].qcDate)) ok = false;
    }
    return ok;
  },
  
  validateLibraryDilutions: function (json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if (!json[i].results.match(/[0-9\.]+/) ||
          Utils.validation.isNullCheck(json[i].dilutionDate)) ok = false;
    }
    return ok;
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

          jQuery('#printServiceSelectDialog').dialog({
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
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  }
};

Sample.options = {
  
  all: null,
  
  getSampleGroupsBySubProjectId: function(subProjectId) {
    return Sample.options.all.sampleGroupsDtos.filter(function (sampleGroup) {
      return sampleGroup.subprojectId == subProjectId;
    });
  },
  
  getSampleGroupsByProjectId: function(projectId) {
    return Sample.options.all.sampleGroupsDtos.filter(function (sampleGroup) {
      return sampleGroup.projectId == projectId && !sampleGroup.subprojectId;
    });
  },
  
  getSubProjectsByProjectId: function(projectId) {
    return Sample.options.all.subprojectsDtos.filter(function (subProject) {
      return subProject.parentProjectId == projectId;
    });
  },
  
  getSampleCategoryByClassId: function(sampleClassId) {
    var results = Sample.options.all.sampleClassesDtos.filter(function (sampleClass) {
      return sampleClass.id == sampleClassId;
    });
    return results.length > 0 ? results[0].sampleCategory : null;
  }
  
};

Sample.ui = {
  
  filterSampleGroupOptions: function() {
    var validSampleGroups = [];
    var subProjectId = Sample.ui.getSelectedSubprojectId();
    if (subProjectId) {
      validSampleGroups = Sample.options.getSampleGroupsBySubProjectId(subProjectId);
    } else {
      var projectId = Sample.ui.getSelectedProjectId();
      if (projectId) {
        validSampleGroups = Sample.options.getSampleGroupsByProjectId(projectId);
      }
    }
    
    jQuery('#sampleGroup').empty()
        .append('<option value = "">None</option>');
    for (var i = 0, l = validSampleGroups.length; i < l; i++) {
      jQuery('#sampleGroup').append('<option value = "' + validSampleGroups[i].id + '">' + validSampleGroups[i].groupId + '</option>');
    }
  },
  
  projectChanged: function() {
    Sample.ui.filterSubProjectOptions();
    Sample.ui.filterSampleGroupOptions();
  },
  
  subProjectChanged: function() {
    Sample.ui.filterSampleGroupOptions();
  },
  
  filterSubProjectOptions: function() {
    var projectId = Sample.ui.getSelectedProjectId();
    var subProjects = Sample.options.getSubProjectsByProjectId(projectId);
    jQuery('#subProject').empty()
        .append('<option value = "">None</option>');
    for (var i = 0, l = subProjects.length; i < l; i++) {
      jQuery('#subProject').append('<option value = "' + subProjects[i].id + '">' + subProjects[i].alias + '</option>');
    }
  },
  
  getSelectedProjectId: function() {
    return jQuery('#project option:selected').val() || jQuery('#project').val();
  },
  
  getSelectedSubprojectId: function() {
    return jQuery('#subProject option:selected').val() || jQuery('#subProject').val();
  },
  
  sampleClassChanged: function() {
    var selectedId = jQuery('#sampleClass option:selected').val();
    var sampleCategory = Sample.options.getSampleCategoryByClassId(selectedId);
    switch (sampleCategory) {
    case 'Tissue':
      Sample.ui.setUpForTissue();
      break;
    case 'Analyte':
      Sample.ui.setUpForAnalyte();
      break;
    default:
      // Identity (can't create), Tissue Processing (no additional fields), or no SampleClass selected 
      Sample.ui.hideTissueFields();
      Sample.ui.hideAnalyteFields();
      break;
    }
  },
  
  hideTissueFields: function() {
    jQuery('#detailedSampleTissue').find(':input').each(function() {
      jQuery(this).val('');
    });
    jQuery('#detailedSampleTissue').hide();
  },
  
  hideAnalyteFields: function() {
    jQuery('#detailedSampleAnalyte').find(':input').each(function() {
      jQuery(this).val('');
    });
    jQuery('#detailedSampleAnalyte').hide();
  },
  
  setUpForTissue: function() {
    Sample.ui.hideAnalyteFields();
    jQuery('#detailedSampleTissue').show();
  },
  
  setUpForAnalyte: function() {
    Sample.ui.hideTissueFields();
    jQuery('#detailedSampleAnalyte').show();
  },
  
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


    jQuery('#changeSampleIdBarcodeDialog').dialog({
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

    jQuery('#changeSampleLocationDialog').dialog({
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

    jQuery('#addSampleNoteDialog').dialog({
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
        {
          'sampleId': sampleId,
          'noteId': noteId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload
        }
      );
    }
  },

  receiveSample: function (input) {
    var barcode = jQuery(input).val();
    if (!Utils.validation.isNullCheck(barcode)) {

      Fluxion.doAjax(
        'sampleControllerHelperService',
        'getSampleByBarcode',
        {
          'barcode': barcode,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            var sample_desc = "<div id='" + json.id + "' class='dashboard'><table width=100%><tr><td>Sample Name: " + json.name + "<br> Sample ID: " + json.id + "<br>Desc: " + json.desc + "<br>Sample Type:" + json.type + "</td><td style='position: absolute;' align='right'><span class='float-right ui-icon ui-icon-circle-close' onclick='Sample.ui.removeSample(" + json.id + ");' style='position: absolute; top: 0; right: 0;'></span></td></tr></table> </div>";
            if (jQuery("#" + json.id).length === 0) {
              jQuery("#sample_pan").append(sample_desc);
              jQuery('#msgspan').html("");
            } else {
              jQuery('#msgspan').html("<i>This sample has already been scanned</i>");
            }
  
            //unbind to stop change error happening every time
  
            //clear and focus
            jQuery(input).val("");
            jQuery(input).focus();
          },
          'doOnError': function (json) {
            jQuery('#msgspan').html("<i>" + json.error + "</i>");
          }
        }
      );
    } else {
      jQuery('#msgspan').html("");
    }
  },

  removeSample: function (sample) {
    jQuery("#" + sample).remove();
  },

  setSampleReceiveDate: function (sampleList) {
    var samples = [];
    jQuery(sampleList).children('div').each(function () {
      var sdiv = jQuery(this);
      samples.push({'sampleId': sdiv.attr("id")});
    });

    if (samples.length > 0) {
      Fluxion.doAjax(
        'sampleControllerHelperService',
        'setSampleReceivedDateByBarcode',
        {
          'samples': samples,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            alert(json.result);
          }
        }
      );
    } else {
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
      {
        'doOnSuccess': function (json) {
          jQuery('#listingSamplesTable').html('');
          jQuery('#listingSamplesTable').dataTable({
            "aaData": json.array,
            "aoColumns": [
              { "sTitle": "Bulk Edit"},
              { "sTitle": "Sample Name", "sType": "no-sam"},
              { "sTitle": "Alias"},
              { "sTitle": "Type"},
              { "sTitle": "QC Passed"},
              { "sTitle": "QC Result"},
              { "sTitle": "ID", "bVisible": false}
            ],
            "bJQueryUI": true,
            "bAutoWidth": false,
            "iDisplayLength": 25,
            "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
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
  
          var selectAll = '<label><input type="checkbox" onchange="Sample.ui.checkAll(this)" id="checkAll">Select All</label>';
          document.getElementById('listingSamplesTable').insertAdjacentHTML('beforebegin', selectAll);
          
          var actions = ['<select id="dropdownActions" onchange="Sample.ui.checkForPropagate(this);"><option value="">-- Bulk actions</option>'];
          actions.push('<option value="update">Update selected</option>');
          actions.push('<option value="propagateSams">Propagate (sample) selected</option>');
          actions.push('<option value="propagateLibs">Propagate (library) selected</option>');
          actions.push('<option value="empty">Empty selected</option>');
          actions.push('<option value="archive">Archive selected</option>');
          actions.push('</select>');
          document.getElementById('listingSamplesTable').insertAdjacentHTML('afterend', actions.join(''));
          var goButton = '<div id="classOptions"></div><button id="go" type="button" onclick="Sample.ui.handleBulkAction();">Go</button>';
          document.getElementById('dropdownActions').insertAdjacentHTML('afterend', goButton);
          if (Sample.detailedSample) Sample.ui.getSampleClasses();
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
  
  checkForPropagate: function (el) {
    var selectedValue = el.options[el.selectedIndex].value;
    if (selectedValue == 'propagateSams') {
      Sample.ui.insertSampleClassesDropdown();
    } else if (selectedValue == 'propagateLibs') {
      Sample.ui.insertLibraryDesignDropdown();
    } else {
      document.getElementById('classOptions').innerHTML = '';
    }
  },
  
  handleBulkAction: function () {
    var selectedValue = document.getElementById('dropdownActions').value;
    var options = {
      "update": Sample.ui.updateSelectedItems,
      "propagateSams": Sample.ui.propagateSamSelectedItems,
      "propagateLibs": Sample.ui.propagateLibSelectedItems,
      "empty": Sample.ui.emptySelectedItems,
      "archive": Sample.ui.archiveSelectedItems
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
    var selectedIdsArray = Sample.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Samples to update.");
      return false;
    }
    window.location="sample/bulk/edit/" + selectedIdsArray.join(',');
  },

  // TODO: fix this up to work with plain samples
  // TODO: add some logic in here for SampleValidRelationships
  propagateSamSelectedItems: function () {
    var selectedClassId = document.getElementById('classDropdown').value;
    var selectedIdsArray = Sample.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Samples to propagate.");
      return false;
    }
    window.location="sample/bulk/create/" + selectedIdsArray.join(',') + "&scid=" + selectedClassId;
  },
  
  propagateLibSelectedItems: function () {
    var selectedIdsArray = Sample.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Samples to propagate.");
      return false;
    }
    window.location="library/bulk/propagate/" + selectedIdsArray.join(',');
  },
  
  // TODO: finish this, and the one in library_ajax.js
  emptySelectedItems: function () {
    var selectedIdsArray = Sample.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Samples to empty.");
      return false;
    }
    var cageDiv = '<div id="cageDialog"><span class="dialog">Look for this feature in the next release!<br>' 
                  + '<img src="http://images.mentalfloss.com/sites/default/files/styles/insert_main_wide_image/public/tumblr_m3fc1bghyt1rq84v4o1_1280.png"/></span></div>';
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
  
  // TODO: finish this, and the one in library_ajax.js
  archiveSelectedItems: function () {
    var selectedIdsArray = Sample.ui.getSelectedIds();
    if (selectedIdsArray.length === 0) {
      alert("Please select one or more Samples to archive.");
      return false;
    }
    var cageDiv = '<div id="cageDialog"><span class="dialog">Look for this feature in the next release!<br>' 
      + '<img src="http://dorkshelf.com/wordpress/wp-content/uploads//2012/02/Raising-Arizona-Nicolas-Cage-2.jpg"/></span></div>';
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
  
  getSampleClasses: function () {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        xhr.status == 200 ? (Sample.sampleClasses = JSON.parse(xhr.responseText)) : console.log(xhr);
      }
    };
    xhr.open('GET', '/miso/rest/sampleclasses');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
  },
  
  insertSampleClassesDropdown: function () {
    var classes = Sample.sampleClasses.sort(function(a, b) {
        return a.id > b.id ? 1 : ((b.id > a.id) ? -1 : 0);
      });
    var select = [];
    select.push('<select id="classDropdown">');
    select.push('<option value="">-- Select child class</option>');
    for (var i=0; i<classes.length; i++) {
      if (classes[i].alias == "Identity") continue;
      select.push('<option value="'+ classes[i].id +'">'+ classes[i].alias +'</option>');
    }
    select.push('</select>');
    document.getElementById('classOptions').innerHTML = select.join('');
  },
  
  // TODO: add library propagation rule-checking here
  insertLibraryDesignDropdown: function () {
    
  }
};

window.addEventListener('error', function (e) {
  var error = e.error;
  console.log(error);
});
