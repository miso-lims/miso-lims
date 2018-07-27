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

ListTarget.library_template = {
  name: "Library Templates",
  createUrl: function(config, projectId) {
    return "/miso/rest/librarytemplate/dt/project/" + projectId;
  },
  queryUrl: "/miso/rest/librarytemplate/query",
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.libraryTemplate.getBulkActions(config);
    actions.push({
      name: "Delete",
      action: function(items) {
        var lines = ['Are you sure you wish to delete the following library templates? This cannot be undone.',
          'Note: a library template may only be deleted by the project owner or an admin.'];
        var ids = [];
        jQuery.each(items, function(index, librarytemplate) {
          lines.push('* ' + librarytemplate.alias);
          ids.push(librarytemplate.id);
        });
        Utils.showConfirmDialog('Delete Library Templates', 'Delete', lines, function() {
          Utils.ajaxWithDialog('Deleting Library Templates', 'POST', '/miso/rest/librarytemplate/bulk-delete', ids, Utils.page.pageReload);
        });
      }
    });
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return [{
      name: "Add",
      handler: function() {
        var fields = [{
          property: 'quantity',
          type: 'int',
          label: 'Quantity',
          value: 1
        }];

        Utils.showDialog('Create Library Templates', 'Create', fields, function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Create Library Templates', ["That's a peculiar number of library templates to create."]);
            return;
          }
          window.location = '/miso/librarytemplate/bulk/new?' + jQuery.param({
            quantity: result.quantity,
            projectId: projectId,
          });
        });
      }
    }];
  },
  createColumns: function(config, projectId) {
    
    var stringIdPredicate = function(id){
      return function(item) {
        return !Utils.validation.isEmpty(id) && item.id == id;
      };
    }
    
    return [{
          "sTitle": "Alias",
          "mData": "alias",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Library Design",
          "mData": "designId",
          "include": Constants.isDetailedSample,
          "iSortPriority": 0,
          "mRender": function(data, type, full){
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.libraryDesigns), 'name') || '';
          },
          "bSortable": false
        }, {
          "sTitle": "Library Code",
          "mData": "designCodeId",
          "include": Constants.isDetailedSample,
          "iSortPriority": 0,
          "mRender": function(data, type, full){
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.libraryDesignCodes), 'code') || '';
          },
          "bSortable": false
        }, {
          "sTitle": "Library Type",
          "mData": "libraryTypeId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full){
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.libraryTypes), 'alias') || '';
          },
          "bSortable": false
        }, {
          "sTitle": "Library Selection Type",
          "mData": "selectionTypeId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full){
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.librarySelections), 'name') || '';
          },
          "bSortable": false
        }, {
          "sTitle": "Library Strategy Type",
          "mData": "strategyTypeId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full){
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.libraryStrategies), 'name') || '';
          },
          "bSortable": false
        }, {
          "sTitle": "Kit",
          "mData": "kitDescriptorId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full){
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.kitDescriptors), 'name') || '';
          },
          "bSortable": false
        }, {
          "sTitle": "Index Family",
          "mData": "indexFamilyId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full){
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.indexFamilies), 'name') || '';
          },
          "bSortable": false
        }, {
          "sTitle": "Platform Type",
          "mData": "platformType",
          "include": true,
          "iSortPriority": 0,
          "bSortable": false,
          "mRender": function(data, type, full){
            return data || '';
          }
        }, {
          "sTitle": "Default Volume",
          "mData": "defaultVolume",
          "include": true,
          "iSortPriority": 0,
          "bSortable": false,
          "mRender": function(data, type, full){
            return data || '';
          }
        }];
  },
  searchTermSelector: function(searchTerms) {
    return [searchTerms['fulfilled'], searchTerms['active'], searchTerms['created'], searchTerms['changed'], searchTerms['creator'],
        searchTerms['changedby'], searchTerms['platform'], searchTerms['index_name'], searchTerms['index_seq'], searchTerms['box']]
  }
};
