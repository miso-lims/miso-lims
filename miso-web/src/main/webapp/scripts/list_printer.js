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

ListTarget.printer = {
  name: "Printers",
  getUserManualUrl: function() {
    return Urls.external.userManual('barcode_label_printers');
  },
  createUrl: function(config, projectId) {
    return Urls.rest.printers.datatable;
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    return !config.isAdmin ? [] : [{
      "name": "Enable",
      "action": function(items) {
        Utils.ajaxWithDialog('Enabling Printer', 'PUT', Urls.rest.printers.enable, items.map(Utils.array.getId), Utils.page.pageReload);

      }
    }, {
      "name": "Disable",
      "action": function(items) {
        Utils.ajaxWithDialog('Disabling Printer', 'PUT', Urls.rest.printers.disable, items.map(Utils.array.getId), Utils.page.pageReload);

      }
    }, ListUtils.createBulkDeleteAction("Printers", 'printers', Utils.array.getName)];
  },
  createStaticActions: function(config, projectId) {
    if (config.isAdmin) {
      return [{
        "name": "Add",
        "handler": function() {
          Utils.showDialog('Add Printer', 'Next', [{
            type: "text",
            label: "Name",
            property: "name"
          }, {
            type: "select",
            label: "Driver",
            property: "driver",
            values: Constants.printerDrivers.map(Utils.array.getName)
          }, {
            type: "text",
            label: "Width (mm)",
            property: "width",
          }, {
            type: "text",
            label: "Height (mm)",
            property: "height",
          }, {
            type: "text",
            label: "Layout",
            property: "layout",
          }, {
            type: "select",
            label: "Backend",
            property: "backend",
            values: Constants.printerBackends,
            getLabel: function(backend) {
              return backend.name;
            }
          }], function(printer) {
            if (!printer.name) {
              Utils.showOkDialog('Create Printer', ['A printer needs a name.']);
              return;
            }
            var layout;
            try {
              layout = JSON.parse(printer.layout);
            } catch (e) {
              Utils.showOkDialog('Create Printer', ['Layout must be valid JSON.']);
              return;
            }
            var save = function(printerConfig) {

              Utils.ajaxWithDialog('Saving Printer', 'POST', Urls.rest.printers.create, {
                "id": 0,
                "available": true,
                "backend": printer.backend.name,
                "configuration": printerConfig,
                "driver": printer.driver,
                "height": printer.height,
                "layout": layout,
                "name": printer.name,
                "width": printer.width,
              }, Utils.page.pageReload);
            }

            if (printer.backend.configurationKeys.length == 0) {
              save({});
            } else {
              Utils.showDialog('Add Printer', 'Save', printer.backend.configurationKeys.map(function(key) {
                return {
                  type: "text",
                  label: key,
                  property: key
                };
              }), save);
            }
          });

        }
      }

      ];
    } else {
      return [];
    }
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Printer",
      "include": true,
      "iSortPriority": 1,
      "mData": "name"
    }, {
      "sTitle": "Driver",
      "include": true,
      "iSortPriority": 0,
      "mData": "driver"
    }, {
      "sTitle": "Backend",
      "include": true,
      "iSortPriority": 0,
      "mData": "backend"
    }, {
      "sTitle": "Available",
      "include": true,
      "iSortPriority": 0,
      "mData": "available",
      "mRender": ListUtils.render.booleanChecks
    }];
  }
};
