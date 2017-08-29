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
  name : "Runs",
  createUrl : function(config, projectId) {
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
  createBulkActions : function(config, projectId) {
    return [];
  },
  createStaticActions : function(config, projectId) {
    if (!projectId && config.platformType) {
      var platformKey = Utils.array.maybeGetProperty(Utils.array
          .findFirstOrNull(Utils.array.namePredicate(config.platformType),
              Constants.platformTypes), 'key');
      return [ {
        name : "Create " + platformKey + " Run",
        handler : function() {
          Utils
              .ajaxWithDialog(
                  'Getting Sequencer',
                  'Get',
                  '/miso/rest/sequencer',
                  null,
                  function(sequencers) {
                    
                    Utils
                        .showWizardDialog(
                            "Create " + platformKey + " Run",
                            sequencers
                                .filter(
                                    function(sequencer) {
                                      return sequencer.platform.platformType == config.platformType && !sequencer.dateDecommissioned;
                                    })
                                .sort(Utils.sorting.standardSort('name'))
                                .map(
                                    function(sequencer) {
                                      return {
                                        name : sequencer.name + " (" + sequencer.platform.instrumentModel + ")",
                                        handler : function() {
                                          window.location = '/miso/run/new/' + sequencer.id;
                                        }
                                      };
                                      
                                    }));
                  });
        }
      } ];
    } else {
      return [];
    }
  },
  createColumns : function(config, projectId) {
    return [
        ListUtils.idHyperlinkColumn("Name", "run", "id", Utils.array.getName,
            1, true),
        ListUtils.labelHyperlinkColumn("Alias", "run", Utils.array.getId,
            "alias", 0, true), {
          "sTitle" : "Status",
          "mData" : "status",
          "mRender" : function(data, type, full) {
            return data || "";
          },
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Start Date",
          "mData" : "startDate",
          "mRender" : function(data, type, full) {
            return data || "";
          },
          "include" : true,
          "iSortPriority" : 2
        }, {
          "sTitle" : "End Date",
          "mData" : "endDate",
          "mRender" : function(data, type, full) {
            return data || "";
          },
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Type",
          "mData" : "platformType",
          "include" : !config.platformType,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Last Modified",
          "mData" : "lastModified",
          "include" : Constants.isDetailedSample,
          "iSortPriority" : 0
        } ];
  }
};
