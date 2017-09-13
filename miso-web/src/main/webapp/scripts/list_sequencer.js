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

ListTarget.sequencer = {
  name : "Sequencers",
  createUrl : function(config, projectId) {
    return "/miso/rest/sequencer/dt";
  },
  createBulkActions : function(config, projectId) {
    return [];
  },
  createStaticActions : function(config, projectId) {
    if (config.isAdmin) {
      return [ {
        "name" : "Add",
        "handler" : function() {
          
          Utils
              .showDialog(
                  "Add Sequencer",
                  "Add",
                  [
                      {
                        property : "name",
                        type : "text",
                        label : "Name",
                        required : true
                      },
                      {
                        property : "platform",
                        type : "select",
                        values : Constants.platforms
                            .sort(function(a, b) {
                              return a.platformType
                                  .localeCompare(b.platformType) || a.instrumentModel
                                  .localeCompare(b.instrumentModel);
                            }),
                        getLabel : function(platform) {
                          return platform.platformType + " - " + platform.instrumentModel;
                        },
                        label : "Platform",
                        required : true
                      }, {
                        property : "serialNumber",
                        type : "text",
                        label : "Serial Number",
                        required : true
                      }, {
                        property : "ip",
                        type : "text",
                        label : "Hostname/IP Address",
                        required : true
                      }, {
                        property : "dateCommissioned",
                        type : "date",
                        label : "Date Comissioned",
                        required : false
                      } ], function(seq) {
                    seq.id = 0;
                    Utils.ajaxWithDialog('Saving Sequencer', 'POST',
                        '/miso/rest/sequencer', seq, Utils.page.pageReload);
                    
                  });
          
        }
      } ];
    } else {
      return [];
    }
  },
  createColumns : function(config, projectId) {
    return [
        ListUtils.labelHyperlinkColumn("Name", "sequencer", Utils.array.getId,
            "name", 1, true), {
          "sTitle" : "Platform",
          "mData" : "platform.platformType",
          "include" : true,
          "iSortPriority" : 0,
          "mRender" : ListUtils.render.platformType
        }, {
          "sTitle" : "Model",
          "mData" : "platform.instrumentModel",
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Commissioned",
          "mData" : "dateCommissioned",
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Decommissioned",
          "mData" : "dateDecommissioned",
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Serial Number",
          "mData" : "serialNumber",
          "include" : true,
          "iSortPriority" : 0
        } ];
  }
};
