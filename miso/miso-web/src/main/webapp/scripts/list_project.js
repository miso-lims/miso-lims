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

ListTarget.project = {
  name: "Projects",
  createUrl: function(config, projectId) {
    throw "Static display only";
  },
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return [{
      "name": "Add",
      "handler": function() {
        window.location.href = '/miso/project/new';
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", "project", "id", Utils.array.getName, 0, true),
        ListUtils.labelHyperlinkColumn("Alias", "project", Utils.array.getId, "alias", 1, true),
        ListUtils.labelHyperlinkColumn("Short Name", "project", Utils.array.getId, "shortName", 0, true),

        {
          "sTitle": "Description",
          "mData": "description",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Progress",
          "mData": "progress",
          "include": true,
          "iSortPriority": 0
        }];
  }
};
