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

ListTarget.study = {
  name: "Studies",
  getUserManualUrl: function() {
    return Urls.external.userManual('european_nucleotide_archive_support', 'studies');
  },
  createUrl: function(config, projectId) {
    return projectId ? Urls.rest.studies.projectDatatable(projectId) : Urls.rest.studies.datatable;
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    if (config.isAdmin) {
      return [ListUtils.createBulkDeleteAction("Studies", "studies", Utils.array.getAlias)];
    } else {
      return [];
    }
  },
  createStaticActions: function(config, projectId) {
    // If a projectId was provided, add that to the URL so it fills in the page with the project's info
    return [{
      "name": "Add",
      "handler": function() {
        window.location = projectId ? Urls.ui.studies.createInProject(projectId) : Urls.ui.studies.create;
      }
    }]
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", Urls.ui.studies.edit, "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", Urls.ui.studies.edit, Utils.array.getId, "alias", 0, true), {
          "sTitle": "Description",
          "mData": "description",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Type",
          "mData": "studyTypeId",
          "include": true,
          "iSortPriority": 0,
          "mRender": ListUtils.render.textFromId(Constants.studyTypes, 'name')
        }];
  }
};
