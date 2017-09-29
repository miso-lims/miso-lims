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

ListTarget.qc = function(qcTarget) {
  return {
    name: qcTarget + " QCs",
    createUrl: function(config, projectId) {
      throw "QCs can only be generated statically";
    },
    createBulkActions: function(config, projectId) {
      return HotTarget.qc(qcTarget).bulkActions;
    },
    createStaticActions: function(config, projectId) {
      return config.entityId ? [{
        name: 'Add QCs',
        handler: function() {
          Utils.showDialog('Add QCs', 'Add', [{
            property: 'copies',
            type: 'int',
            label: 'QCs per ' + qcTarget,
            value: 1
          }, ], function(result) {
            window.location = window.location.origin + '/miso/qc/bulk/addFrom/' + qcTarget + '?' + jQuery.param({
              entityIds: config.entityId,
              copies: result.copies
            });
          });
        }
      }] : [];
    },
    createColumns: function(config, projectId) {
      return [{
        "sTitle": "Method",
        "mData": "type.name",
        "include": true,
        "iSortPriority": 0
      }, {
        "sTitle": "Creator",
        "mData": "creator",
        "include": true,
        "iSortPriority": 0
      }, {
        "sTitle": "Date",
        "mData": "date",
        "include": true,
        "iSortPriority": 1
      }, {
        "sTitle": "Results",
        "mData": "results",
        "include": true,
        "iSortPriority": 0,
        "mRender": function(data, type, full) {
          return data + " " + full.type.units;
        }
      }, ];
    }
  };
};
