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

ListTarget.container = {
  name: "Sequencing Containers",
  getUserManualUrl: function() {
    return Urls.external.userManual('sequencing_containers');
  },
  createUrl: function(config, projectId) {
    if (config.platformType) {
      return Urls.rest.containers.platformDatatable(config.platformType);
    } else {
      return Urls.rest.containers.datatable;
    }
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = BulkUtils.actions.qc('Container');
    if (config.runId) {
      actions.push({
        name: "Remove",
        action: function(containers) {
          Utils.ajaxWithDialog('Removing', 'POST', Urls.rest.runs.removeContainers(config.runId), containers.map(Utils.array.getId),
              Utils.page.pageReload);
        }
      });
    } else {
      actions.push(HotUtils.printAction('container'));
    }
    actions.push(HotUtils.spreadsheetAction(Urls.rest.containers.spreadsheet, Constants.partitionSpreadsheets, function(containers,
        spreadsheet) {
      return [];
    }));
    actions.push({
      name: 'Delete',
      action: function(containers) {
        var lines = ['Are you sure you wish to delete the following containers? This cannot be undone.',
            'Note: a Container may only be deleted by its creator or an admin.'];
        var ids = [];
        jQuery.each(containers, function(index, container) {
          lines.push('* ' + container.id + ' (' + container.identificationBarcode + ')');
          ids.push(container.id);
        });
        Utils.showConfirmDialog('Delete Containers', 'Delete', lines, function() {
          Utils.ajaxWithDialog('Deleting Containers', 'POST', Urls.rest.containers.bulkDelete, ids, function() {
            Utils.page.pageReload();
          });
        });
      }
    });
    return actions;
  },
  createStaticActions: function(config, projectId) {
    var platformType = Utils.array.findFirstOrNull(function(pt) {
      return pt.name == config.platformType;
    }, Constants.platformTypes);
    if (config.runId) {
      return [{
        "name": "Add " + platformType.containerName,
        "handler": function() {
          if (config.isFull) {
            Utils.showOkDialog("Run Full", ["Cannot add another " + platformType.containerName
                + " to this run as it is full. Try removing one first."]);
            return;
          }
          Utils.showDialog('Add ' + platformType.containerName, 'Add', [{
            type: "text",
            label: "Serial Number",
            property: "barcode"
          }, ], function(results) {
            Utils.ajaxWithDialog('Adding ' + platformType.containerName, 'POST', Urls.rest.runs.addContainer(config.runId) + '?'
                + jQuery.param({
                  barcode: results.barcode
                }), null, Utils.page.pageReload);
          });

        }
      }

      ];

    }

    return [{
      "name": "Add " + platformType.containerName,
      "handler": function() {
        var instrumentModels = Constants.instrumentModels.filter(function(p) {
          return p.platformType === config.platformType && p.instrumentType === 'SEQUENCER' && p.active;
        }).sort(Utils.sorting.standardSort('alias')).map(function(instrumentModel) {
          return {
            name: instrumentModel.alias,
            handler: function() {
              var models = Constants.containerModels.filter(function(m) {
                return (m.instrumentModelIds.indexOf(instrumentModel.id) !== -1 && !m.archived);
              }).sort(Utils.sorting.standardSort('alias')).map(function(model) {
                return {
                  name: model.alias,
                  handler: function() {
                    window.location = Urls.ui.containers.create(model.id);
                  }
                }
              });
              Utils.showWizardDialog("Add " + platformType.containerName, models);
            }
          }
        });
        Utils.showWizardDialog("Add " + platformType.containerName, instrumentModels);
      }
    }, ];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.labelHyperlinkColumn("ID", Urls.ui.containers.edit, Utils.array.getId, "id", 1, true),
        ListUtils.labelHyperlinkColumn("Serial Number", Urls.ui.containers.edit, Utils.array.getId, "identificationBarcode", 1, true), {
          "sTitle": "Platform",
          "mData": "platform",
          "include": !config.platformType,
          "iSortPriority": 0
        }, ListUtils.idHyperlinkColumn("Last Run Name", Urls.ui.runs.edit, "lastRunId", function(container) {
          return "RUN" + container.lastRunId;
        }, -1, true), ListUtils.labelHyperlinkColumn("Last Run Alias", Urls.ui.runs.edit, function(container) {
          return container.lastRunId;
        }, "lastRunAlias", -1, true), ListUtils.labelHyperlinkColumn("Last Sequencer Used", Urls.ui.instruments.edit, function(container) {
          return container.lastSequencerId;
        }, "lastSequencerName", -1, true), {
          "sTitle": "Last Modified",
          "mData": "lastModified",
          "include": true,
          "iSortPriority": 2
        }];
  },
  searchTermSelector: function(searchTerms) {
    return [searchTerms['id'], searchTerms['entered'], searchTerms['changed'], searchTerms['creator'], searchTerms['changedby'],
        searchTerms['platform'], searchTerms['index_name'], searchTerms['index_seq'], searchTerms['kitname']]
  }
};
