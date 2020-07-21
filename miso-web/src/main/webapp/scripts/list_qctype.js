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

ListTarget.qctype = {
  name: "QC Types",
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'qc-types');
  },
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createBulkDeleteAction("QC Types", "qctypes", function(qcType) {
      return qcType.name + ' (' + qcType.qcTarget + ' QC)';
    })] : [];
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [{
      "name": "Add",
      "handler": function() {
        window.location = Urls.ui.qcTypes.create;
      }
    }] : [];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.labelHyperlinkColumn('Name', Urls.ui.qcTypes.edit, Utils.array.getId, 'name', 0, true), {
      "sTitle": "Description",
      "mData": "description",
      "include": true,
      "iSortPriority": 0,
      "bSortable": false
    }, {
      "sTitle": "Target",
      "mData": "qcTarget",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Units",
      "mData": "units",
      "include": true,
      "iSortPriority": 0,
      "bSortable": false
    }, {
      "sTitle": "Corresponding Field",
      "mData": "correspondingField",
      "include": true,
      "iSortPriority": 0,
      "bSortable": true
    }, {
      "sTitle": "Auto Update Field",
      "mData": "autoUpdateField",
      "include": true,
      "iSortPriority": 0,
      "bSortable": true
    }, ];
  }
};
