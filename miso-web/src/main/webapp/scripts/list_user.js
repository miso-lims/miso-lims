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
  getUserManualUrl: function() {
    return Urls.external.userManual('users_and_groups', 'users');
  },
  createUrl: function(config, projectId) {
    throw new Error("Static data only");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    switch (config.listMode) {
    case 'list':
      return !config.isAdmin ? [] : [ListUtils.createBulkDeleteAction("Users", "users", function(user) {
        return user.fullName;
      })];
    case 'included':
      return [{
        name: 'Remove',
        action: function(items) {
          if (!items.length) {
            Utils.showOkDialog('Error', 'No users selected');
            return;
          }
          Utils.showConfirmDialog('Remove Users', 'Remove', ['Remove '
              + (items.length == 1 ? 'selected user' : items.length + ' selected users') + ' from the group?'], function() {
            var url = Urls.rest.groups.removeUsers(config.groupId);
            var data = items.map(Utils.array.getId);
            Utils.ajaxWithDialog('Removing Users', 'POST', url, data, Utils.page.pageReload);
          });
        }
      }];
    case 'available':
      return [{
        name: 'Add',
        action: function(items) {
          if (!items.length) {
            Utils.showOkDialog('Error', 'No users selected');
            return;
          }
          var url = Urls.rest.groups.addUsers(config.groupId);
          var data = items.map(Utils.array.getId);
          Utils.ajaxWithDialog('Adding Users', 'POST', url, data, Utils.page.pageReload);
        }
      }];
    default:
      throw new Error('Unknown listMode: ' + config.listMode);
    }
  },
  createStaticActions: function(config, projectId) {
    if (config.listMode === 'list' && config.isAdmin && config.allowCreateUser) {
      return [{
        "name": "Add",
        "handler": function() {
          window.location = Urls.ui.users.create;
        }
      }];
    } else {
      return [];
    }
  },
  createColumns: function(config, projectId) {
    var permissionColumn = function(headerName, property, sortPriority, include) {
      return {
        "sTitle": headerName,
        "mData": property,
        "include": include,
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
          return "<a href=\"" + Urls.ui.users.edit(full.id) + "\">" + data + "</a>";
        } else {
          return data;
        }
      }
    }, {
      "sTitle": "Full Name",
      "mData": "fullName",
      "include": true,
      "iSortPriority": 0
    }, permissionColumn("Active", "active", 0, true), permissionColumn("Admin", "admin", 0, true),
        permissionColumn("Internal", "internal", 0, true), permissionColumn("Logged In", "loggedIn", 2, config.listMode === 'list')];
  }
};
