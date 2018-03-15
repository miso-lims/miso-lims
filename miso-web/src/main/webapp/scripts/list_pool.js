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

ListTarget.pool = {
  name: "Pools",
  createUrl: function(config, projectId) {
    if (projectId) {
      return "/miso/rest/pool/dt/project/" + projectId;
    } else {
      return "/miso/rest/pool/dt/platform/" + config.platformType;
    }
  },
  queryUrl: "/miso/rest/pool/query",
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.pool.getBulkActions(config);
    actions.push({
      name: "Delete",
      action: function(items) {
        var lines = ['Are you sure you wish to delete the following pools? This cannot be undone.',
            'Note: a pool may only be deleted by its creator or an admin.'];
        var ids = [];
        jQuery.each(items, function(index, pool) {
          lines.push('* ' + pool.name + ' (' + pool.alias + ')');
          ids.push(pool.id);
        });
        Utils.showConfirmDialog('Delete Pools', 'Delete', lines, function() {
          Utils.ajaxWithDialog('Deleting Pools', 'POST', '/miso/rest/pool/bulk-delete', ids, function() {
            window.location = window.location.origin + '/miso/pools';
          });
        });
      }
    });
    return actions;
  },
  createStaticActions: function(config, prodjectId) {
    return [{
      name: "Add",
      handler: function() {
        window.location = "/miso/pool/new";
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", "pool", "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "pool", Utils.array.getId, "alias", 0, true), {
          "sTitle": "Description",
          "mData": "description",
          "mRender": function(data, type, full) {
            var html = data ? data : ""
            if (full.duplicateIndices) {
              html += " <span class='parsley-custom-error-message'><strong>(DUPLICATE INDICES)</strong></span>"
            } else if (full.nearDuplicateIndices) {
              html += " <span class='parsley-custom-error-message'><strong>(NEAR-DUPLICATE INDICES)</strong></span>"
            }
            return html;
          },
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Date Created",
          "mData": "creationDate",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Dilutions",
          "mData": "dilutionCount",
          "include": true,
          "iSortPriority": 0,
          "bSortable": false,
        }, {
          "sTitle": "Conc. (" + Constants.poolConcentrationUnits + ")",
          "mData": "concentration",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Location",
          "mData": "locationLabel",
          "bSortable": false,
          "mRender": function(data, type, full) {
            return full.boxId ? "<a href='/miso/box/" + full.boxId + "'>" + data + "</a>" : data;
          },
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Average Insert Size",
          "mData": "insertSize",
          "bSortable": false,
          "mRender": function(data, type, full) {
            return data ? Math.round(data) : "N/A";
          },
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Last Modified",
          "mData": "lastModified",
          "include": Constants.isDetailedSample,
          "iSortPriority": 2
        }];
  },
  searchTermSelector: function(searchTerms) {
    return [searchTerms['fulfilled'], searchTerms['active'], searchTerms['created'], searchTerms['changed'], searchTerms['creator'],
        searchTerms['changedby'], searchTerms['platform'], searchTerms['index_name'], searchTerms['index_seq'], searchTerms['box']]
  }
};
