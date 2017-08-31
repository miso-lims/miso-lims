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

var Pool = Pool || {

  validatePool: function () {
    Validate.cleanFields('#pool-form');
    jQuery('#pool-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '100');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Platform Type input select validation
    jQuery('#platformType').attr('class', 'form-control');
    jQuery('#platformType').attr('required', 'true');
    jQuery('#sampleTypes').attr('data-parsley-error-message', 'You must select a Platform');

    // Concentration input field validation
    jQuery('#concentration').attr('class', 'form-control');
    jQuery('#concentration').attr('data-parsley-required', 'true');
    jQuery('#concentration').attr('data-parsley-maxlength', '10');
    jQuery('#concentration').attr('data-parsley-type', 'number');

    // Creation Date input field validation
    jQuery('#creationDate').attr('class', 'form-control');
    jQuery('#creationDate').attr('required', 'true');
    jQuery('#creationDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#creationDate').attr('data-date-format', 'YYYY-MM-DD');
    jQuery('#creationDate').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

    // Volume validation
    jQuery('#volume').attr('class', 'form-control');
    jQuery('#volume').attr('data-parsley-maxlength', '10');
    jQuery('#volume').attr('data-parsley-type', 'number');

    jQuery('#pool-form').parsley();
    jQuery('#pool-form').parsley().validate();

    Validate.updateWarningOrSubmit('#pool-form');
    return false;
  }
};

Pool.ui = {
  selectElementsByBarcodes : function(codes) {
    if (codes === "") {
      alert("Please input at least one barcode...");
    } else {
      Fluxion.doAjax(
        'poolControllerHelperService',
        'selectElementsByBarcodeList',
        {
          'barcodes':codes,
          'url':ajaxurl
        },
        {
          'updateElement':'importlist'
        }
      );
    }
  },

  dilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {
        'url':ajaxurl
      },
      {
        'statusElement':'statusdiv',
        'progressElement':'trash',
        'doOnSuccess':self.dilutionFileUploadSuccessFunc
      },
      {'':''}
    );
  },

  dilutionFileUploadSuccessFunc : function() {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectDilutionsByBarcodeFile',
      {
        'url':ajaxurl
      },
      {
        'updateElement':'dilimportfile'
      }
    );
  },

  /** Deprecated */
  libraryDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {
        'url':ajaxurl
      },
      {
        'statusElement':'statusdiv',
        'progressElement':'trash',
        'doOnSuccess':self.libraryDilutionFileUploadSuccessFunc
      },
      {'':''}
    );
  },

  /** Deprecated */
  libraryDilutionFileUploadSuccessFunc : function() {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectLibraryDilutionsByBarcodeFile',
      {
        'url':ajaxurl
      },
      {
        'updateElement':'dilimportfile'
      }
    );
  },

  /** Deprecated */
  ls454EmPcrDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {
        'url':ajaxurl
      },
      {
        'statusElement':'statusdiv',
        'progressElement':'trash',
        'doOnSuccess':self.ls454EmPcrDilutionFileUploadSuccessFunc
      },
      {'':''}
    );
  },

  /** Deprecated */
  ls454EmPcrDilutionFileUploadSuccessFunc : function() {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'select454EmPCRDilutionsByBarcodeFile',
      {
        'url':ajaxurl
      },
      {
        'updateElement':'dilimportfile'
      }
    );
  },

  /** Deprecated */
  solidEmPcrDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {
        'url':ajaxurl
      },
      {
        'statusElement':'statusdiv',
        'progressElement':'trash',
        'doOnSuccess':self.solidEmPcrDilutionFileUploadSuccessFunc
      },
      {'':''}
    );
  },

  /** Deprecated */
  solidEmPcrDilutionFileUploadSuccessFunc : function() {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectSolidEmPCRDilutionsByBarcodeFile',
      {
        'url':ajaxurl
      },
      {
        'updateElement':'dilimportfile'
      }
    );
  },

  showPoolNoteDialog: function (poolId) {
    var self = this;
    jQuery('#addNoteDialog')
      .html("<form>" +
        "<fieldset class='dialog'>" +
        "<label for='internalOnly'>Internal Only?</label>" +
        "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
        "<br/>" +
        "<label for='notetext'>Text</label>" +
        "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' autofocus />" +
        "</fieldset></form>");

    jQuery('#addNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function () {
          if (jQuery('#notetext').val().length > 0) {
            self.addPoolNote(poolId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
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

  addPoolNote: function (poolId, internalOnly, text) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'addPoolNote',
      {
        'poolId': poolId,
        'internalOnly': internalOnly,
        'text': text,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  deletePoolNote: function (poolId, noteId) {
    var deleteIt = function() {
      Fluxion.doAjax(
        'poolControllerHelperService',
        'deletePoolNote',
        {
          'poolId': poolId,
          'noteId': noteId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload
        }
      );
    }
    Utils.showConfirmDialog('Delete Note', 'Delete',
      ["Are you sure you want to delete this note?"],
      deleteIt
    );
  }
};

Pool.search = {
  poolSearchExperiments : function(input, platform) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'poolSearchExperiments',
      {
        'str':jQuery(input).val(),
        'platform':platform,
        'id':input.id,
        'url':ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          jQuery('#exptresult').css('visibility', 'visible');
          jQuery('#exptresult').html(json.html);
          jQuery(input).blur(function() {
            jQuery('#exptresult :first-child').hide();
          });
        }
      }
    );
  },

  poolSearchSelectExperiment : function(experimentId, experimentName) {
    if (jQuery("#experiments" + experimentId).length > 0) {
      alert("Experiment " + experimentName + " is already associated with this pool.");
    } else {
      var div = "<div onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>";
      div += "<span class='float-left'><input type='hidden' id='experiment" + experimentId + "' value='" + experimentId + "' name='experiments'/>";
      div += "<b>Experiment: " + experimentName + "</b></span>";
      div += "<span onclick='Utils.ui.confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span></div>";
      jQuery('#exptlist').append(div);
    }
    jQuery('#exptresult').css('visibility', 'hidden');
  },

  poolSearchLibraryDilution : function(input, platform) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'poolSearchLibraryDilution',
      {
        'str':jQuery(input).val(),
        'platform':platform,
        'id':input.id,
        'url':ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          jQuery('#searchDilutionResult').css('visibility', 'visible');
          jQuery('#searchDilutionResult').html(json.html);
        }
      }
    );
  }
};

Pool.barcode = {
  editPoolIdBarcode: function (span, id) {
    var v = span.find('a').text();
    if (v && v !== "") {
      span.html("<input type='text' value='" + v + "' name='identificationBarcode' id='identificationBarcode'>");
    }
  },

  showPoolIdBarcodeChangeDialog: function (poolId, poolIdBarcode) {
    var self = this;
    jQuery('#changeIdBarcodeDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<strong><label>Current Barcode: </label></strong><span id='idBarcodeCurrent'>" + poolIdBarcode +
            "</span><br /><strong><label for='notetext'>New Barcode:</label></strong>" +
            "<input type='text' name='idBarcodeInput' id='idBarcodeInput' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery('#changeIdBarcodeDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function () {
          self.changePoolIdBarcode(poolId, jQuery('#idBarcodeInput').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  changePoolIdBarcode: function (poolId, idBarcode) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'changePoolIdBarcode',
      {
        'poolId': poolId,
        'identificationBarcode': idBarcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },
};
