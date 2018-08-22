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
            'Any associated orders will also be deleted.', 'Note: a pool may only be deleted by its creator or an admin.'];
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
    actions.push({
      name: "Merge",
      action: function(pools) {
        var fields = pools.map(function(pool) {
          return {
            type: 'int',
            label: pool.alias + " (" + pool.name + ")",
            value: 1,
            property: 'pool' + pool.id,
            required: true
          };
        });

        HotUtils.showDialogForBoxCreation("Merge Proportions", "Merge", fields, '/miso/pool/bulk/merge?', function(output) {
          var ids = pools.map(Utils.array.getId);
          var proportions = ids.map(function(id) {
            return output['pool' + id];
          });          
          return {
            ids: ids.join(','),
            proportions: proportions.join(',')
          };
        }, function(result){
          return 1;
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
          "mRender": WarningTarget.pool.tableWarnings,
          "include": true,
          "iSortPriority": 0,
          "bSortable": false
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
          "sTitle": "Concentration",
          "mData": "concentration",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Conc. Units",
          "mData": "concentrationUnits",
          "include": true,
          "iSortPriority": 0,
          "bSortable": false,
          "mRender": function(data, type, full){
            var units = Constants.concentrationUnits.find(function(unit){
              return unit.name == data;
            });
            return !!units ? units.units : '';
          }
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
