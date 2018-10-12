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

ListTarget.changelog = {
  name: "Changes",
  createUrl: function(config, projectId) {
    throw new Error("Static data only");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Name",
      "mData": "userName",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Summary",
      "mData": "summary",
      "include": true,
      "iSortPriority": 0,
      "mRender": function(data, type, full) {
        if (type === 'display' && data.indexOf('\n') > -1) {
          var html = '<ul class="unformatted-list">';
          data.split('\n').forEach(function(item) {
            html += '<li>' + item + '</li>';
          });
          html += '</ul>';
          return html;
        }
        return data;
      }
    }, {
      "sTitle": "Time",
      "mData": "time",
      "include": true,
      "iSortPriority": 1
    }];
  }
};
