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

ListTarget.user = {
  name: "Users",
  createUrl: function(config, projectId) {
    throw new Error("Static data only");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    if (config.isAdmin && config.allowCreateUser) {
      return [{
        "name": "Add",
        "handler": function() {
          window.location = "/miso/admin/user/new";
        }
      }

      ];
    } else {
      return [];
    }
  },
  createColumns: function(config, projectId) {
    var permissionColumn = function(headerName, property, sortPriority) {
      return {
        "sTitle": headerName,
        "mData": property,
        "include": true,
        "iSortPriority": sortPriority,
        "mRender": function(data, type, full) {
          return data ? "✔" : "";
        }
      };
    }

    return [{
      "sTitle": "Login Name",
      "mData": "loginName",
      "include": true,
      "iSortPriority": 1,
      "mRender": function(data, type, full) {
        if (config.isAdmin) {
          return "<a href=\"/miso/admin/user/" + full.id + "\">" + data + "</a>";
        } else {
          return data;
        }
      }
    }, {
      "sTitle": "Use Name",
      "mData": "fullName",
      "include": true,
      "iSortPriority": 0
    }, permissionColumn("Active", "active", 0), permissionColumn("Admin", "admin", 0), permissionColumn("Internal", "internal", 0),
        permissionColumn("External", "external", 0), permissionColumn("Logged In", "loggedIn", 2)];
  }
};
