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

ListTarget.run = {
  name: "Runs",
  getUserManualUrl: function() {
    return Urls.external.userManual('sequencing_runs');
  },
  createUrl: function(config, projectId) {
    if (projectId) {
      return Urls.rest.runs.projectDatatable(projectId);
    } else if (config.sequencer) {
      return Urls.rest.runs.sequencerDatatable(config.sequencer);
    } else if (config.platformType) {
      return Urls.rest.runs.platformDatatable(config.platformType);
    } else {
      return Urls.rest.runs.datatable;
    }
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    return [ListUtils.createBulkDeleteAction("Runs", "runs", function(run) {
      return run.alias;
    })];
  },
  createStaticActions: function(config, projectId) {
    if (!projectId && config.platformType) {
      var platformKey = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.namePredicate(config.platformType),
          Constants.platformTypes), 'key');
      return [{
        name: "Add " + platformKey + " Run",
        handler: function() {
          Utils.ajaxWithDialog('Getting Sequencer', 'Get', Urls.rest.instruments.list, null, function(instruments) {
            var allowedModels = Constants.instrumentModels.filter(function(model) {
              return model.platformType === config.platformType && model.instrumentType === 'SEQUENCER';
            }).map(function(model) {
              return model.id;
            });
            Utils.showWizardDialog("Add " + platformKey + " Run", instruments.filter(function(instrument) {
              return allowedModels.indexOf(instrument.instrumentModelId) !== -1 && !instrument.dateDecommissioned;
            }).sort(Utils.sorting.standardSort('name')).map(function(sequencer) {
              return {
                name: sequencer.name + " (" + sequencer.instrumentModelAlias + ")",
                handler: function() {
                  window.location = Urls.ui.runs.create(sequencer.id);
                }
              };

            }));
          });
        }
      }];
    } else {
      return [];
    }
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", Urls.ui.runs.edit, "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", Urls.ui.runs.edit, Utils.array.getId, "alias", 0, true), {
          sTitle: "Projects",
          mData: "projectsLabel",
          include: true,
          bSortable: false
        }, {
          "sTitle": "Seq. Params.",
          "mData": "sequencingParametersName",
          "include": true,
          "bSortable": false,
          "mRender": function(data, type, full) {
            return data || "(None)";
          }
        }, {
          "sTitle": "Status",
          "mData": "status",
          "mRender": function(data, type, full) {
            return data || "";
          },
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Start Date",
          "mData": "startDate",
          "mRender": function(data, type, full) {
            return data || "";
          },
          "include": true,
          "iSortPriority": 2
        }, {
          "sTitle": "End Date",
          "mData": "endDate",
          "mRender": function(data, type, full) {
            return data || "";
          },
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Type",
          "mData": "platformType",
          "include": !config.platformType,
          "iSortPriority": 0,
          "sClass": config.poolId ? "noPrint" : undefined
        }, {
          "sTitle": "Last Modified",
          "mData": "lastModified",
          "include": Constants.isDetailedSample,
          "iSortPriority": 0,
          "sClass": config.poolId ? "noPrint" : undefined
        }];
  },
  searchTermSelector: function(searchTerms) {
    return [searchTerms['id'], searchTerms['runstatus'], searchTerms['created'], searchTerms['entered'], searchTerms['changed'],
        searchTerms['creator'], searchTerms['changedby'], searchTerms['platform'], searchTerms['index_name'], searchTerms['index_seq'],
        searchTerms['parameters']]
  }
};
