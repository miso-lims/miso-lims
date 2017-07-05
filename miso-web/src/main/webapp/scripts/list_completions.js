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

ListTarget.completion = {
  name : "Pool Orders",
  createUrl : function(config, projectId) {
    if (config.poolId) {
      return '/miso/rest/pool/' + config.poolId + '/dt/completions';
    }
    return '/miso/rest/poolorder/dt/completions' + (config.activeOnly
        ? '/active' : '');
  },
  createBulkActions : function(config, projectId) {
    return [];
  },
  createStaticActions : function(config, prodjectId) {
    return [];
  },
  createColumns : function(config, projectId) {
    var nonZero = function(items) {
      return items.some(function(x) {
        return x != 0;
      });
    }
    return [
        ListUtils.idHyperlinkColumn("Name", "pool", "pool.id", function(
            completion) {
          return completion.pool.name;
        }, 1),
        ListUtils.labelHyperlinkColumn("Alias", "pool", function(completion) {
          return completion.pool.id;
        }, "pool.alias", 0), {
          "sTitle" : "Description",
          "mData" : "pool.description",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true
        }, {
          "sTitle" : "Platform",
          "mData" : "parameters.platform.instrumentModel",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true
        }, {
          "sTitle" : "Longest Index",
          "mData" : "pool.longestIndex",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : !config.poolId
        }, {
          "sTitle" : "Sequencing Parameters",
          "mData" : "parameters.name",
          "iSortPriority" : 0,
          "include" : true
        
        }, {
          "sTitle" : "Completed",
          "mData" : "completed",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true,
          "visibilityFilter" : nonZero
        
        }, {
          "sTitle" : "Failed",
          "mData" : "failed",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true,
          "visibilityFilter" : nonZero
        }, {
          "sTitle" : "Requested",
          "mData" : "requested",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true,
          "visibilityFilter" : nonZero
        }, {
          "sTitle" : "Running",
          "mData" : "running",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true,
          "visibilityFilter" : nonZero
        }, {
          "sTitle" : "Started",
          "mData" : "started",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true,
          "visibilityFilter" : nonZero
        }, {
          "sTitle" : "Stopped",
          "mData" : "stopped",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true,
          "visibilityFilter" : nonZero
        }, {
          "sTitle" : "Unknown",
          "mData" : "unknown",
          "bSortable" : false,
          "iSortPriority" : 0,
          "include" : true,
          "visibilityFilter" : nonZero
        }, {
          "sTitle" : "Remaining",
          "mData" : "remaining",
          "iSortPriority" : 0,
          "include" : true,
          "visibilityFilter" : nonZero
        }, {
          "sTitle" : "Last Updated",
          "mData" : "lastUpdated",
          "iSortPriority" : 2,
          "iSortPriority" : 0,
          "include" : true
        } ];
  }
};
