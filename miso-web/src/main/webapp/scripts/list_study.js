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
  createUrl: function(config, projectId) {
    return "/miso/rest/studies/dt" + (projectId ? "/project/" + projectId : "");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    if (config.isAdmin) {
      return [{
        "name": "Delete",
        "action": function(studies) {
          Utils.showConfirmDialog("Confirm Delete", "OK", ["Are you sure you really want to delete? This operation is permanent!"],
              function() {
                var deleter = function(index) {
                  if (index >= studies.length) {
                    Utils.page.pageReload();
                    return;
                  }
                  Utils.ajaxWithDialog('Deleting study', 'DELETE', '/miso/rest/studies/' + studies[index].id, null, function() {
                    deleter(index + 1);
                  });
                };

                deleter(0);
              });
        }
      }];
    } else {
      return [];
    }
  },
  createStaticActions: function(config, projectId) {
    // If a projectId was provided, add that to the URL so it fills in the page with the project's info
    return [{
      "name": "Add",
      "handler": function() {
        window.location = "/miso/study/new" + (projectId ? '/' + projectId : '')
      }
    }]
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", "study", "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "study", Utils.array.getId, "alias", 0, true), {
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
