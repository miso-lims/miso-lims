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

ListTarget.submission = {
  name: "Submissions",
  createUrl: function(config, projectId) {
    throw "Submissions must be provided statically";
  },
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("ID", "submission", "id", Utils.array.getId, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "submission", Utils.array.getId, "alias", 0, true), {
          "sTitle": "Created Date",
          "mData": "creationDate",
          "include": true,
          "iSortPriority": 0,
        }, {
          "sTitle": "Submitted Date",
          "mData": "submittedDate",
          "include": true,
          "iSortPriority": 0,
        }, {
          "sTitle": "Verified",
          "mData": "verified",
          "include": true,
          "iSortPriority": 0,
          "mRender": ListUtils.render.booleanChecks
        },

        {
          "sTitle": "Completed",
          "mData": "completed",
          "include": true,
          "iSortPriority": 0,
          "mRender": ListUtils.render.booleanChecks
        }, ];
  }
};
