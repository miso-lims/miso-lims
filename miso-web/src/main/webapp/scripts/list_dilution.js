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
    return "/miso/rest/librarydilution/dt" + (projectId ? "/project/" + projectId : "");
  },
  queryUrl: "/miso/rest/librarydilution/query",
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
          Utils.ajaxWithDialog('Deleting Library Dilutions', 'POST', '/miso/rest/librarydilution/bulk-delete', ids, function() {
            window.location = window.location.origin + '/miso/dilutions';
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
        var fields = [{
          property: "identificationBarcode",
          type: "text",
          label: "Matrix Barcode",
          required: false
        }, {
          property: "concentration",
          type: "float",
          label: "Conc. (" + Constants.libraryDilutionConcentrationUnits + ")",
          required: true
        }, {
          property: "creationDate",
          type: "date",
          label: "Creation Date",
          required: true
        }];
        if (Constants.isDetailedSample) {
          fields.push({
            property: "targetedSequencing",
            type: "select",
            values: [{
              "alias": "(None)",
              "id": 0
            }].concat(Constants.targetedSequencings.filter(function(targetedSequencing) {
              return targetedSequencing.kitDescriptorIds.indexOf(config.library.kitDescriptorId) != -1;
            }).sort(function(a, b) {
              return a.alias.localeCompare(b.alias);
            })),
            getLabel: Utils.array.getAlias,
            label: "Targeted Sequencing",
            required: true
          });
        }

        Utils.showDialog("Create Dilution", "Create", fields, function(dil) {
          // check barcode
          Utils.ajaxWithDialog('Saving Dilution', 'POST', '/miso/rest/librarydilution', {
            "library": config.library,
            "name": dil.name,
            "identificationBarcode": dil.identificationBarcode,
            "concentration": dil.concentration,
            "creationDate": dil.creationDate,
            "targetedSequencingId": dil.targetedSequencing && dil.targetedSequencing.id != 0 ? dil.targetedSequencing.id : null

          }, Utils.page.pageReload);

        });

      }
    }] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Name",
      "mData": "id",
      "include": true,
      "iSortPriority": 1,
      "mRender": function(data, type, full) {
        return "<a href=\"/miso/library/" + full.library.id + "\">" + full.name + "</a>";
      }
    }, ListUtils.idHyperlinkColumn("Library Name", "library", "library.id", function(dilution) {
      return dilution.library.name;
    }, 0, !config.library), ListUtils.labelHyperlinkColumn("Library Alias", "library", function(dilution) {
      return dilution.library.id;
    }, "library.alias", 0, !config.library), {
      "sTitle": "Matrix Barcode",
      "mData": "identificationBarcode",
      "include": true,
      "iSortPriority": 0
    }, {
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
      "sTitle": "Concentration",
      "mData": "concentration",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Volume",
      "mData": "volume",
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
        searchTerms['index_name'], searchTerms['index_seq'], searchTerms['institute'], searchTerms['external'], searchTerms['box']]
  }
};
