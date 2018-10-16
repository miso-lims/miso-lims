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

ListTarget.qctype = {
  name: "QC Types",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return HotTarget.qctype.getBulkActions(config);
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [{
      "name": "Add",
      "handler": function() {

        Utils.showDialog('Create QC Types', 'Create', [{
          property: 'quantity',
          type: 'int',
          label: 'Quantity',
          value: 1
        }], function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Create QC Types', ["That's a peculiar number of qctypes to create."]);
            return;
          }
          window.location = '/miso/qctype/bulk/new?' + jQuery.param({
            quantity: result.quantity,
          });
        });
      }
    }] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Name",
      "mData": "name",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Description",
      "mData": "description",
      "include": true,
      "iSortPriority": 0,
      "bSortable": false
    }, {
      "sTitle": "Target",
      "mData": "qcTarget",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Units",
      "mData": "units",
      "include": true,
      "iSortPriority": 0,
      "bSortable": false
    }, {
      "sTitle": "Corresponding Field",
      "mData": "correspondingField",
      "include": true,
      "iSortPriority": 0,
      "bSortable": true
    }, {
      "sTitle": "Auto Update Field",
      "mData": "autoUpdateField",
      "include": true,
      "iSortPriority": 0,
      "bSortable": true
    }, ];
  }
};
