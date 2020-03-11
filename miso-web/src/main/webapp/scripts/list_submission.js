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
  getUserManualUrl: function() {
    return Urls.external.userManual('european_nucleotide_archive_support', 'submissions');
  },
  createUrl: function(config, projectId) {
    throw new Error("Submissions must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    return !config.isAdmin ? [] : [ListUtils.createBulkDeleteAction("Submissions", "submissions", Utils.array.getAlias)];
  },
  createStaticActions: function(config, projectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("ID", Urls.ui.submissions.edit, "id", Utils.array.getId, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", Urls.ui.submissions.edit, Utils.array.getId, "alias", 0, true), {
          "sTitle": "Creation Date",
          "mData": "creationDate",
          "include": true,
          "iSortPriority": 0,
        }, {
          "sTitle": "Submission Date",
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
