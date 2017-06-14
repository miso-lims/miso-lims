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
  name : "Pools",
  createUrl : function(config, projectId) {
    if (projectId) {
      return "/miso/rest/pool/dt/project/" + projectId;
    } else {
      return "/miso/rest/pool/dt/platform/" + config.platformType;
    }
  },
  createBulkActions : function(config, projectId) {
    // TODO return HotTarget.pool.bulkActions;
    return [];
  },
  createStaticActions : function(config, prodjectId) {
    return [ {
      name : "Add",
      handler : function() {
        window.location = "/miso/pool/new";
      }
    } ];
  },
  createColumns : function(config, projectId) {
    return [
        {
          "sTitle" : "Name",
          "mData" : "id",
          "mRender" : function(data, type, full) {
            return "<a href=\"/miso/pool/" + data + "\">" + full.name + "</a>";
          },
          "include" : true,
          "iSortPriority" : 1
        },
        {
          "sTitle" : "Alias",
          "mData" : "alias",
          "mRender" : function(data, type, full) {
            return "<a href=\"/miso/pool/" + full.id + "\">" + data + "</a>";
          },
          "include" : true,
          "iSortPriority" : 0
        },
        {
          "sTitle" : "Description",
          "mData" : "description",
          "include" : true,
          "iSortPriority" : 0
        },
        {
          "sTitle" : "Date Created",
          "mData" : "creationDate",
          "include" : true,
          "iSortPriority" : 0
        },
        {
          "sTitle" : "Conc. (" + Constants.poolConcentrationUnits + ")",
          "mData" : "concentration",
          "include" : true,
          "iSortPriority" : 0
        },
        {
          "sTitle" : "Location",
          "mData" : "locationLabel",
          "bSortable" : false,
          "mRender" : function(data, type, full) {
            return full.boxId
                ? "<a href='/miso/box/" + full.boxId + "'>" + data + "</a>"
                : data;
          },
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Last Updated",
          "mData" : "lastModified",
          "include" : Constants.isDetailedSample,
          "iSortPriority" : 2
        } ];
  }
};
