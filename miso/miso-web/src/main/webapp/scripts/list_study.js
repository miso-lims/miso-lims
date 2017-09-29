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
    return "/miso/rest/study/dt" + (projectId ? "/project/" + projectId : "");
  },
  createBulkActions: function(config, projectId) {
    if (config.isAdmin) {
      return [{
        "name": "Delete",
        "action": function(ids) {
          if (confirm("Are you sure you really want to delete? This operation is permanent!")) {
            var deleter = function(index) {
              if (index >= ids.length) {
                Utils.page.pageReload();
                return;
              }
              Utils.ajaxWithDialog('Deleting study', 'DELETE', '/miso/rest/study/' + ids[index], function() {
                deleter(index + 1);
              });
            };

            deleter(0);
          }
        }
      }];
    } else {
      return [];
    }
  },
  createStaticActions: function(config, projectId) {
    if (projectId) {
      return [{
        "name": "Add",
        "handler": function() {
          window.location = "/miso/study/new/" + projectId;
        }
      }, {
        "name": "Create Experiments",
        "handler": function() {
          window.location = "/miso/experimentwizard/new/" + projectId;
        }
      }, ];
    } else {
      return [];
    }
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
