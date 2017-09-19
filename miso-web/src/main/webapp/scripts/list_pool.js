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

ListTarget.pool = {
  name: "Pools",
  createUrl: function(config, projectId) {
    if (projectId) {
      return "/miso/rest/pool/dt/project/" + projectId;
    } else {
      return "/miso/rest/pool/dt/platform/" + config.platformType;
    }
  },
  createBulkActions: function(config, projectId) {
    return HotTarget.pool.bulkActions;
  },
  createStaticActions: function(config, prodjectId) {
    return [{
      name: "Add",
      handler: function() {
        window.location = "/miso/pool/new";
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [
        ListUtils.idHyperlinkColumn("Name", "pool", "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "pool", Utils.array.getId, "alias", 0, true),
        {
          "sTitle": "Description",
          "mData": "description",
          "mRender": function(data, type, full) {
            return (data ? data : "")
                + (full.duplicateIndices ? " <span class='parsley-custom-error-message'><strong>(DUPLICATE INDICES)</strong></span>" : "");
          },
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Date Created",
          "mData": "creationDate",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Conc. (" + Constants.poolConcentrationUnits + ")",
          "mData": "concentration",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Location",
          "mData": "locationLabel",
          "bSortable": false,
          "mRender": function(data, type, full) {
            return full.boxId ? "<a href='/miso/box/" + full.boxId + "'>" + data + "</a>" : data;
          },
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Last Modified",
          "mData": "lastModified",
          "include": Constants.isDetailedSample,
          "iSortPriority": 2
        }];
  }
};
