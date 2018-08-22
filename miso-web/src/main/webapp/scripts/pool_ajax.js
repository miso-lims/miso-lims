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

var Pool = {

  savePool: function() {
    if (jQuery('#poolId').text().indexOf('Unsaved') > -1 && !Constants.automaticBarcodes && !jQuery('#identificationBarcode').val().length) {
      Utils.showConfirmDialog("Missing Barcode", "Save",
          ["Pools should usually have barcodes. Are you sure you wish to save without one?"], Pool.validatePool);
    } else {
      Pool.validatePool();
    }
  },

  validatePool: function() {
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
    jQuery('#concentration').attr('data-parsley-required', 'false');
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
  showPoolNoteDialog: function(poolId) {
    var self = this;
    jQuery('#addNoteDialog')
        .html(
            "<form>"
                + "<fieldset class='dialog'>"
                + "<label for='internalOnly'>Internal Only?</label>"
                + "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />"
                + "<br/>" + "<label for='notetext'>Text</label>"
                + "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' autofocus />"
                + "</fieldset></form>");

    jQuery('#addNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function() {
          if (jQuery('#notetext').val().length > 0) {
            self.addPoolNote(poolId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          } else {
            jQuery('#notetext').focus();
          }
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  addPoolNote: function(poolId, internalOnly, text) {
    Fluxion.doAjax('poolControllerHelperService', 'addPoolNote', {
      'poolId': poolId,
      'internalOnly': internalOnly,
      'text': text,
      'url': ajaxurl
    }, {
      'doOnSuccess': Utils.page.pageReload
    });
  },

  deletePoolNote: function(poolId, noteId) {
    var deleteIt = function() {
      Fluxion.doAjax('poolControllerHelperService', 'deletePoolNote', {
        'poolId': poolId,
        'noteId': noteId,
        'url': ajaxurl
      }, {
        'doOnSuccess': Utils.page.pageReload
      });
    }
    Utils.showConfirmDialog('Delete Note', 'Delete', ["Are you sure you want to delete this note?"], deleteIt);
  }
};
