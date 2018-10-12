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

ListTarget.issue = {
  name: "Issues",
  createUrl: function(config, projectId) {
    throw new Error("Static data only");
  },
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Key",
      "mData": "key",
      "include": true,
      "iSortPriority": 0,
      "mRender": function(data, type, full) {
        if (type === 'display') {
          return "<a href=\"" + full.url + "\">" + data + "</a>";
        }
        return data;
      }
    }, {
      "sTitle": "Summary",
      "mData": "summary",
      "include": true,
      "iSortPriority": 0,
      "mRender": function(data, type, full) {
        if (type === 'display') {
          return "<a href=\"" + full.url + "\">" + data + "</a>";
        }
        return data;
      }
    }, {
      "sTitle": "Status",
      "mData": "status",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Last Updated",
      "mData": "lastUpdated",
      "include": true,
      "iSortPriority": 1
    }];
  }
};
