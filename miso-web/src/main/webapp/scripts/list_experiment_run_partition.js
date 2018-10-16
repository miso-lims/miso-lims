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

ListTarget.experiment_run_partition = {
  name: "Run + Partition",
  createUrl: function(config, projectId) {
    throw new Error("Must be created statically.");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Run Name", "run", "run.id", function(pair) {
      return pair.run.name;
    }, 1, true), ListUtils.labelHyperlinkColumn("Run Alias", "run", function(pair) {
      return pair.run.id;
    }, "run.alias", 0, true), {
      "sTitle": "Status",
      "mData": "run.status",
      "mRender": function(data, type, full) {
        return data || "";
      },
      "include": true,
      "iSortPriority": 0
    }, ListUtils.labelHyperlinkColumn("Container", "container", function(pair) {
      return pair.partition.containerId;
    }, "partition.containerName", 0, true), {
      "sTitle": "Number",
      "mData": "partition.partitionNumber",
      "include": true,
      "iSortPriority": 1,
      "bSortDirection": true
    }, {
      "sTitle": "Pool",
      "mData": "partition.pool",
      "include": true,
      "iSortPriority": 0,
      "mRender": WarningTarget.experiment_run_partition.tableWarnings
    }, ];
  }
};
