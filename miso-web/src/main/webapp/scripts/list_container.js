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
  createUrl: function(config, projectId) {
    return "/miso/rest/container/dt" + (config.platformType ? "/platform/" + config.platformType : "");
  },
  createBulkActions: function(config, projectId) {
    var actions = HotUtils.makeQcActions('Container');
    if (config.runId) {
      actions.push({
        name: "Remove",
        action: function(containers) {
          Utils.ajaxWithDialog('Removing', 'POST', '/miso/rest/run/' + config.runId + '/remove', containers.map(Utils.array.getId),
              Utils.page.pageReload);

        }
      });
    } else {
      actions.push(HotUtils.printAction('container'));
    }
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
            Utils.ajaxWithDialog('Adding ' + platformType.containerName, 'POST', '/miso/rest/run/' + config.runId + '/add?'
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
        var models = Constants.platforms.filter(function(p) {
          return p.platformType == config.platformType && p.active;
        }).sort(Utils.sorting.standardSort('instrumentModel')).map(function(platform) {
          return {
            name: platform.instrumentModel,
            handler: function() {
              var sizes = platform.partitionSizes.map(function(size) {

                return {
                  name: size + " " + (size == 1 ? platformType.partitionName : platformType.pluralPartitionName),
                  handler: function() {
                    window.location = "/miso/container/new/" + platform.id + "?count=" + size;
                  }
                };

              });
              if (sizes.length == 1) {
                sizes[0].handler();
              } else {
                Utils.showWizardDialog("Add " + platform.instrumentModel + " " + platformType.containerName, sizes);
              }

            }
          };
        });
        if (models.length == 1) {
          models[0].handler();
        } else {
          Utils.showWizardDialog("Add " + platformType.containerName, models);
        }

      }
    }, ];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.labelHyperlinkColumn("Serial Number", "container", Utils.array.getId, "identificationBarcode", 1, true), {
      "sTitle": "Platform",
      "mData": "platform",
      "include": !config.platformType,
      "iSortPriority": 0
    }, ListUtils.idHyperlinkColumn("Last Run Name", "run", "lastRunId", function(container) {
      return "RUN" + container.lastRunId;
    }, -1, true), ListUtils.labelHyperlinkColumn("Last Run Alias", "run", function(container) {
      return container.lastRunId;
    }, "lastRunAlias", -1, true), ListUtils.labelHyperlinkColumn("Last Sequencer Used", "sequencer", function(container) {
      return container.lastSequencerId;
    }, "lastSequencerName", -1, true), {
      "sTitle": "Last Modified",
      "mData": "lastModified",
      "include": true,
      "iSortPriority": 0
    }];
  }
};
