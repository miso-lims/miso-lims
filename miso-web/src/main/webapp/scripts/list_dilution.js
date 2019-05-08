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

ListTarget.dilution = {
  name: "Library Dilutions",
  createUrl: function(config, projectId) {
    return "/miso/rest/librarydilutions/dt" + (projectId ? "/project/" + projectId : "");
  },
  queryUrl: "/miso/rest/librarydilutions/query",
  createBulkActions: function(config, projectId) {
    var actions = config.library ? HotTarget.dilution.getBulkActions(config).filter(function(action) {
      return action.allowOnLibraryPage;
    }) : HotTarget.dilution.getBulkActions(config);

    actions.push({
      name: "Delete",
      action: function(items) {
        var lines = ['Are you sure you wish to delete the following library dilutions? This cannot be undone.',
            'Note: a dilution may only be deleted by its creator or an admin.'];
        var ids = [];
        jQuery.each(items, function(index, dilution) {
          lines.push('* ' + dilution.name + ' (' + dilution.library.alias + ')');
          ids.push(dilution.id);
        });
        Utils.showConfirmDialog('Delete Library Dilutions', 'Delete', lines, function() {
          Utils.ajaxWithDialog('Deleting Library Dilutions', 'POST', '/miso/rest/librarydilutions/bulk-delete', ids, function() {
            Utils.page.pageReload();
          });
        });
      }
    });

    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.library ? [{
      "name": "Create",
      "handler": function() {
        HotUtils.warnIfConsentRevoked([config.library], function() {
          var fields = [ListUtils.createBoxField];
          Utils.showDialog('Make Dilution', 'Create', fields, function(result) {
            var params = {
              ids: config.library.id
            }
            var loadPage = function() {
              window.location = window.location.origin + '/miso/dilutions/bulk/propagate?' + jQuery.param(params);
            }
            if (result.createBox) {
              Utils.createBoxDialog(result, function(result) {
                return 1;
              }, function(newBox) {
                params['boxId'] = newBox.id;
                loadPage();
              });
            } else {
              loadPage();
            }
          });
        });
      }
    }] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Name",
      "mData": "id", // for sorting purposes (numerical order instead of string)
      "include": true,
      "iSortPriority": 1,
      "mRender": function(data, type, full) {
        if (type === 'display') {
          return "<a href=\"/miso/dilutions/" + data + "\">" + full.name + "</a>";
        }
        return data;
      }
    }, {
      "sTitle": "Warnings",
      "mData": null,
      "mRender": Warning.tableWarningRenderer(WarningTarget.dilution),
      "include": true,
      "iSortPriority": 0,
      "bVisible": true,
      "bSortable": false
    }, ListUtils.idHyperlinkColumn("Library Name", "library", "library.id", function(dilution) {
      return dilution.library.name;
    }, 0, !config.library), ListUtils.labelHyperlinkColumn("Library Alias", "library", function(dilution) {
      return dilution.library.id;
    }, "library.alias", 0, !config.library), {
      "sTitle": "Platform",
      "mData": "library.platformType",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Targeted Sequencing",
      "mData": "targetedSequencingId",
      "include": Constants.isDetailedSample,
      "mRender": ListUtils.render.textFromId(Constants.targetedSequencings, 'alias', '(None)'),
      "iSortPriority": 0,
      "bSortable": false
    }, {
      "sTitle": "Volume",
      "mData": "volume",
      "include": true,
      "iSortPriority": 0,
      "mRender": function(data, type, full) {
        if (type === 'display' && !!data) {
          var units = Constants.volumeUnits.find(function(unit) {
            return unit.name == full.volumeUnits;
          });
          if (!!units) {
            return data + ' ' + units.units;
          }
        }
        return data;
      }
    }, {
      "sTitle": "Concentration",
      "mData": "concentration",
      "include": true,
      "iSortPriority": 0,
      "mRender": function(data, type, full) {
        if (type === 'display' && !!data) {
          var units = Constants.concentrationUnits.find(function(unit) {
            return unit.name == full.concentrationUnits;
          });
          if (!!units) {
            return data + ' ' + units.units;
          }
        }
        return data;
      }
    }, {
      "sTitle": "ng Lib. Used",
      "mData": "ngUsed",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Vol. Lib. Used",
      "mData": "volumeUsed",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Matrix Barcode",
      "mData": "identificationBarcode",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Creator",
      "mData": "dilutionUserName",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Creation Date",
      "mData": "creationDate",
      "include": true,
      "iSortPriority": 0
    }];
  },
  searchTermSelector: function(searchTerms) {
    return [searchTerms['created'], searchTerms['changed'], searchTerms['creator'], searchTerms['changedby'], searchTerms['platform'],
        searchTerms['index_name'], searchTerms['index_seq'], searchTerms['box']]
  }
};
