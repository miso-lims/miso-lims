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

ListTarget.dilution = {
  name : "Library Dilutions",
  createUrl : function(config, projectId) {
    return "/miso/rest/librarydilution/dt" + (projectId
        ? "/project/" + projectId : "");
  },
  createBulkActions : function(config, projectId) {
    return HotTarget.dilution.bulkActions;
  },
  createStaticActions : function(config, projectId) {
    return [];
  },
  createColumns : function(config, projectId) {
    return [
        {
          "sTitle" : "Name",
          "mData" : "id",
          "include" : true,
          "iSortPriority" : 1,
          "mRender" : function(data, type, full) {
            return "<a href=\"/miso/library/" + full.library.id + "\">" + full.name + "</a>";
          }
        },
        ListUtils.idHyperlinkColumn("Library Name", "library", "library.id",
            function(dilution) {
              return dilution.library.name;
            }, 0),
        ListUtils.labelHyperlinkColumn("Library Alias", "library", function(
            dilution) {
          return dilution.library.id;
        }, "library.alias", 0), {
          "sTitle" : "Creator",
          "mData" : "dilutionUserName",
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Creation Date",
          "mData" : "creationDate",
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Platform",
          "mData" : "library.platformType",
          "include" : true,
          "iSortPriority" : 0
        }, {
          "sTitle" : "Concentration",
          "mData" : "concentration",
          "include" : true,
          "iSortPriority" : 0
        } ];
  }
};
