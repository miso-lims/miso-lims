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
  createUrl: function(config, projectId) {
    if (projectId) {
      return "/miso/rest/run/dt/project/" + projectId;
    } else if (config.sequencer) {
      return "/miso/rest/run/dt/sequencer/" + config.sequencer;
    } else if (config.platformType) {
      return "/miso/rest/run/dt/platform/" + config.platformType;
    } else {
      return "/miso/rest/run/dt";
    }
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    if (!projectId && config.platformType) {
      var platformKey = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.namePredicate(config.platformType),
          Constants.platformTypes), 'key');
      return [{
        name: "Add " + platformKey + " Run",
        handler: function() {
          Utils.ajaxWithDialog('Getting Sequencer', 'Get', '/miso/rest/instrument', null, function(instruments) {

            Utils.showWizardDialog("Add " + platformKey + " Run", instruments.filter(
                function(instrument) {
                  return instrument.instrumentModel.platformType == config.platformType && !instrument.dateDecommissioned
                      && instrument.instrumentModel.instrumentType === 'SEQUENCER';
                }).sort(Utils.sorting.standardSort('name')).map(function(sequencer) {
              return {
                name: sequencer.name + " (" + sequencer.instrumentModel.alias + ")",
                handler: function() {
                  window.location = '/miso/run/new/' + sequencer.id;
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
    return [ListUtils.idHyperlinkColumn("Name", "run", "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "run", Utils.array.getId, "alias", 0, true), {
          "sTitle": "Seq. Params.",
          "mData": "parameters.name",
          "include": true,
          "bSortable": false
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
    return [searchTerms['runstatus'], searchTerms['created'], searchTerms['changed'], searchTerms['creator'], searchTerms['changedby'],
        searchTerms['platform'], searchTerms['index_name'], searchTerms['index_seq'], searchTerms['parameters']]
  }
};
