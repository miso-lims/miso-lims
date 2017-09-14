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

ListTarget.index = {
  name: "Indices",
  createUrl: function(config, projectId) {
    return "/miso/rest/index/dt" + (config.platformType ? "/platform/" + config.platformType : "");
  },
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [{
      "sTitle": "Platform",
      "include": !config.platformType,
      "iSortPriority": 3,
      "mData": "family.platformType",
      "mRender": ListUtils.render.platformType
    }, {
      "sTitle": "Family",
      "include": true,
      "iSortPriority": 2,
      "mData": "family.name"
    }, {
      "sTitle": "Name",
      "include": true,
      "iSortPriority": 1,
      "mData": "name"
    }, {
      "sTitle": "Sequence",
      "include": true,
      "iSortPriority": 0,
      "mData": "sequence"
    }];
  }
};
