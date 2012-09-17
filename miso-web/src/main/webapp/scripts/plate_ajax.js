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

var Plate = Plate || {};

Plate.barcode = {
  printPlateBarcodes : function() {
    var plates = [];
    for (var i = 0; i < arguments.length; i++) {
      plates[i] = {'plateId':plates[i]};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass':'uk.ac.bbsrc.tgac.miso.core.data.Plate',
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
                    'plateControllerHelperService',
                    'printPlateBarcodes',
                    {
                      'serviceName':jQuery('#serviceSelect').val(),
                      'plates':plates,
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

    /*
    Fluxion.doAjax(
          'plateControllerHelperService',
          'printPlateBarcodes',
     {
          'plates':plates,
          'url':ajaxurl
      },
      {
          'doOnSuccess':function (json) { alert(json.response); }
      }
    );
    */
  },

  showPlateLocationChangeDialog : function(plateId) {
    var self = this;
    jQuery('#changePlateLocationDialog')
            .html("<form>" +
                  "<fieldset class='dialog'>" +
                  "<label for='notetext'>New Location:</label>" +
                  "<input type='text' name='locationBarcode' id='locationBarcode' class='text ui-widget-content ui-corner-all'/>" +
                  "</fieldset></form>");

    jQuery(function() {
        jQuery('#changePlateLocationDialog').dialog({
            autoOpen: false,
            width: 400,
            modal: true,
            resizable: false,
            buttons: {
                "Save": function() {
                    self.changePlateLocation(plateId, jQuery('#locationBarcode').val());
                    jQuery(this).dialog('close');
                },
                "Cancel": function() {
                    jQuery(this).dialog('close');
                }
            }
        });
    });
    jQuery('#changePlateLocationDialog').dialog('open');
  },

  changePlateLocation : function(plateId, barcode) {
    Fluxion.doAjax(
      'plateControllerHelperService',
      'changePlateLocation',
      {
        'plateId':plateId,
        'locationBarcode':barcode,
        'url':ajaxurl
      },
      {
        'doOnSuccess':Utils.page.pageReload
      }
    );
  }
};

Plate.tagbarcode = {
  getPlateBarcodesByMaterialType : function(form) {
    Fluxion.doAjax(
      'plateControllerHelperService',
      'getPlateBarcodesByMaterialType',
      {'materialType':form.value, 'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          jQuery('#plateBarcodeSelect').html(json.plateBarcodes);
        }
      }
    );
  }
};