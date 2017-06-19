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

ListTarget.kit = {
  name : "Kits",
  createUrl : function(config, projectId) {
    return "/miso/rest/kitdescriptor/dt" + (config.kitType
        ? "/type/" + config.kitType : "");
  },
  createBulkActions : function(config, projectId) {
    
    return [];
  },
  createStaticActions : function(config, projectId) {
    return [ {
      "name" : "Add",
      "handler" : function() {
        window.location = '/miso/kitdescriptor/new';
      }
    } ];
  },
  createColumns : function(config, projectId) {
    return [
        {
          "sTitle" : "Name",
          "include" : true,
          "iSortPriority" : 1,
          "mData" : "name",
          "mRender" : function(data, type, full) {
            return "<a href=\"/miso/kitdescriptor/" + full.id + "\">" + data + "</a>";
          }
        }, {
          "sTitle" : "Version",
          "include" : true,
          "iSortPriority" : 0,
          "mData" : "version"
        }, {
          "sTitle" : "Manufacturer",
          "include" : true,
          "iSortPriority" : 0,
          "mData" : "manufacturer"
        }, {
          "sTitle" : "Part Number",
          "include" : true,
          "iSortPriority" : 0,
          "mData" : "partNumber"
        }, {
          "sTitle" : "Type",
          "include" : !config.kitType,
          "iSortPriority" : 0,
          "mData" : "kitType"
        }, {
          "sTitle" : "Stock Level",
          "include" : true,
          "iSortPriority" : 0,
          "mData" : "stockLevel"
        }, {
          "sTitle" : "Platform",
          "include" : !config.platformType,
          "iSortPriority" : 0,
          "mData" : "platformType"
        } ];
  }
};
