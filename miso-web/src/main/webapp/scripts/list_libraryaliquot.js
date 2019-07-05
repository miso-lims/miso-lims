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

ListTarget.libraryaliquot = {
  name: "Library Aliquots",
  createUrl: function(config, projectId) {
    return projectId ? Urls.rest.libraryAliquots.projectDatatable(projectId) : Urls.rest.libraryAliquots.datatable;
  },
  getQueryUrl: function() {
    return Urls.rest.libraryAliquots.query;
  },
  createBulkActions: function(config, projectId) {
    var actions = config.library ? HotTarget.libraryaliquot.getBulkActions(config).filter(function(action) {
      return action.allowOnLibraryPage;
    }) : HotTarget.libraryaliquot.getBulkActions(config);

    actions.push({
      name: "Delete",
      action: function(items) {
        var lines = ['Are you sure you wish to delete the following library aliquots? This cannot be undone.',
            'Note: a library aliquot may only be deleted by its creator or an admin.'];
        var ids = [];
        jQuery.each(items, function(index, aliquot) {
          lines.push('* ' + aliquot.name + ' (' + aliquot.alias + ')');
          ids.push(aliquot.id);
        });
        Utils.showConfirmDialog('Delete Library Aliquots', 'Delete', lines, function() {
          Utils.ajaxWithDialog('Deleting Library Aliquots', 'POST', Urls.rest.libraryAliquots.bulkDelete, ids, function() {
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
          Utils.showDialog('Make Library Aliquot', 'Create', fields, function(result) {
            var params = {
              ids: config.library.id
            }
            var loadPage = function() {
              window.location = Urls.ui.libraryAliquots.bulkPropagate + '?' + jQuery.param(params);
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
          return '<a href="' + Urls.ui.libraryAliquots.edit(data) + '">' + full.name + '</a>';
        }
        return data;
      }
    }, ListUtils.labelHyperlinkColumn("Alias", Urls.ui.libraryAliquots.edit, Utils.array.getId, "alias", 0, true), {
      "sTitle": "Warnings",
      "mData": null,
      "mRender": Warning.tableWarningRenderer(WarningTarget.libraryaliquot),
      "include": true,
      "iSortPriority": 0,
      "bVisible": true,
      "bSortable": false
    }, ListUtils.idHyperlinkColumn("Library Name", Urls.ui.libraries.edit, "libraryId", function(aliquot) {
      return aliquot.libraryName;
    }, 0, !config.library), ListUtils.labelHyperlinkColumn("Library Alias", Urls.ui.libraries.edit, function(aliquot) {
      return aliquot.libraryId;
    }, "libraryAlias", 0, !config.library), {
      "sTitle": "Platform",
      "mData": "libraryPlatformType",
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
      "mData": "creatorName",
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
