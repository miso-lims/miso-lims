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
  name : "Sequencing Containers",
  createUrl : function(config, projectId) {
    return "/miso/rest/container/dt" + (config.platformType
        ? "/platform/" + config.platformType : "");
  },
  createBulkActions : function(config, projectId) {
    return [];
  },
  createStaticActions : function(config, projectId) {
    return [ {
      "name" : "Add",
      "handler" : function() {
        window.location = "/miso/container/new";
      }
    }, HotUtils.printAction('container'), ];
  },
  createColumns : function(config, projectId) {
    return [
        ListUtils.labelHyperlinkColumn("Serial Number", "container",
            Utils.array.getId, "identificationBarcode", 1, true),
        {
          "sTitle" : "Platform",
          "mData" : "platform",
          "include" : !config.platformType,
          "iSortPriority" : 0
        },
        ListUtils.idHyperlinkColumn("Last Run Name", "run", "lastRunId",
            function(container) {
              return "RUN" + container.lastRunId;
            }, -1, true),
        ListUtils.labelHyperlinkColumn("Last Run Alias", "run", function(
            container) {
          return container.lastRunId;
        }, "lastRunAlias", -1, true),
        ListUtils.labelHyperlinkColumn("Last Sequencer Used", "sequencer",
            function(container) {
              return container.lastSequencerId;
            }, "lastSequencerName", -1, true), {
          "sTitle" : "Last Modified",
          "mData" : "lastModified",
          "include" : true,
          "iSortPriority" : 0
        } ];
  }
};
