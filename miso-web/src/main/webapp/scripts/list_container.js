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
  name : "Sequencing Container",
  createUrl : function(config, projectId) {
    return "/miso/rest/container/dt" + (config.platformType
        ? "/platform/" + config.platformType : "");
  },
  createBulkActions : function(config, projectId) {
    return [];
  },
  createStaticActions : function(config, projectId) {
    return [{ "name":"Add", "handler": function() { window.location = "/miso/container/new"; }}];
  },
  createColumns : function(config, projectId) {
return [
        {
          "sTitle": "Serial Number",
          "mData": "identificationBarcode",
          "mRender": function (data, type, full) {
            return "<a href=\"/miso/container/" + full.id + "\">" + data + "</a>";
          },
          "include" : true,
          "iSortPriority" : 0
        },
        {
          "sTitle": "Platform",
          "mData": "platform",
          "include" : !config.platformType,
          "iSortPriority" : 0
        },
        {
          "sTitle": "Last Associated Run",
          "include" : true,
          "mData": "lastRunAlias",
          "mRender": function (data, type, full) {
            return (data ? "<a href=\"/miso/run/" + full.lastRunId + "\">" + data + "</a>" : "");
          },
          "bSortable": false,
          "include" : true,
          "iSortPriority" : 0
        },
        {
          "sTitle": "Last Sequencer Used",
          "include" : true,
          "mData": "lastSequencerName",
          "bSortable": false ,
          "include" : true,
          "iSortPriority" : 0,
          "mRender": function (data, type, full) {
            return (data ? "<a href=\"/miso/sequencer/" + full.lastSequencerId + "\">" + data + "</a>" : "");
          },
        },
        {
          "sTitle": "Last Modified",
          "mData": "lastModified",
          "include" : true,
          "iSortPriority" : 0
        }
      ];
}
};
