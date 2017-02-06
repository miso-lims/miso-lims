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

var Print = Print || {};

Print.ui = {
  changeBackend: function() {
    var backend = parseInt(document.getElementById('backend').value);
    jQuery('#backendConfiguration').html(
      Print.backends.filter(function(b) { return b.id == backend; })[0].configurationKeys.map(function (k) {
        return k + ": <input id='cfg" + k + "'/>";
      }).join("<br/>"));
  },

  showAddPrinter: function() {
    Print.ui.dialog = jQuery('#add-printer-dialog').dialog({
        autoOpen: true,
        height: 400,
        width: 350,
        modal: true,
        buttons: {
          "Save": function() {
            var backend = parseInt(document.getElementById('backend').value);
            var configuration = {};
            Print.backends.filter(function(b) { return b.id == backend; })[0].configurationKeys.forEach(function (k) {
             configuration[k] = document.getElementById('cfg' + k).value;
            });

            Print.service.addPrinter(
              document.getElementById('addName').value,
              document.getElementById('driver').value,
              backend,
              configuration);
          },
          "Cancel": function() {
            Pool.orders.dialog.dialog( "close" );
          }
        }
      });
  }
};

Print.service = {
  addPrinter: function(name, driver, backend, configuration) {
    Fluxion.doAjax(
      'printerControllerHelperService',
      'addPrinter',
      {
        'name':name,
        'driver':driver,
        'backend':backend,
        'configuration':configuration,
        'url':ajaxurl},
      {'doOnSuccess':Utils.page.pageReload,
       'doOnError':function(json) {
         alert(json.error);
       }
      }
    );
  },

  setPrinterState: function(printerId, state) {
    Fluxion.doAjax(
    'printerControllerHelperService',
    'setPrinterState',
    {
      'printerId':printerId,
      'state':state,
      'url':ajaxurl},
    {
      'doOnSuccess':Utils.page.pageReload
    });
  },

  deletePrinter: function(printerId) {
    Fluxion.doAjax(
    'printerControllerHelperService',
    'deletePrinter',
    {
      'printerId':printerId,
      'url':ajaxurl},
    {
      'doOnSuccess':Utils.page.pageReload
    });
  }
};
