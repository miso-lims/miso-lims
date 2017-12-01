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

ListTarget.kit_consumable = {
  name: "Kits",
  createUrl: function(config, projectId) {
    throw "Must be provided statically";
  },
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return config.allowedDescriptors ? [{
      "name": "Add",
      "handler": function() {

        Utils.showDialog('Add Kit', 'Add', [{
          property: 'descriptor',
          type: 'select',
          label: 'Kit Type',
          values: config.allowedDescriptors,
          getLabel: Utils.array.getName
        }, {
          property: 'lotNumber',
          type: 'text',
          label: 'Lot Number',
          required: true
        }, {
          property: 'date',
          type: 'date',
          label: 'Date',
          required: true
        }], function(result) {

          Utils.ajaxWithDialog("Adding Kit", "POST", "/miso/rest/experiment/" + config.experimentId + "/addkit", result,
              Utils.page.pageReload);

        });
      }
    }] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Name",
      "mData": "descriptor.name",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Part Number",
      "mData": "descriptor.partNumber",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Lot Number",
      "mData": "lotNumber",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Date",
      "mData": "date",
      "include": true,
      "iSortPriority": 1
    }, ];
  }
};
