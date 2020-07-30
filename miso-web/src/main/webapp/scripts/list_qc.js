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
      throw new Error("QCs can only be generated statically");
    },
    getQueryUrl: null,
    createBulkActions: function(config, projectId) {
      return BulkTarget.qc.getBulkActions({
        qcTarget: qcTarget
      });
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
          }, {
            property: 'controls',
            type: 'int',
            label: 'Controls per QC',
            value: 1
          }], function(result) {
            if (!Number.isInteger(result.copies) || result.copies < 1) {
              Utils.showOkDialog('Error', ['Invalid number of QCs entered']);
            } else if (!Number.isInteger(result.controls) || result.controls < 0) {
              Utils.showOkDialog('Error', ['Invalid number of controls entered']);
            } else {
              window.location = Urls.ui.qcs.bulkAddFrom(qcTarget) + '?' + jQuery.param({
                entityIds: config.entityId,
                copies: result.copies,
                controls: result.controls
              });
            }
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
          if (type === 'display') {
            if (full.type.precisionAfterDecimal < 0) {
              return ListUtils.render.booleanChecks(!!data, type, full);
            } else {
              return data + " " + full.type.units;
            }
          } else {
            return data;
          }
        }
      }, {
        "sTitle": "Description",
        "mData": "description",
        "include": true,
        "iSortPriority": 0,
      }, ];
    }
  };
};
