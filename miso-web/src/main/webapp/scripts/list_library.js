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

ListTarget.library = {
  name : "Libraries",
  createUrl : function(config, projectId) {
    return "/miso/rest/library/dt" + (projectId ? '/project/' + projectId : '');
  },
  createBulkActions : function(config, projectId) {
    return HotTarget.library.bulkActions;
  },
  createStaticActions : function(config, prodjectId) {
    return [];
  },
  createColumns : function(config, projectId) {
    return [
        {
          "sTitle" : "Library Name",
          "mData" : "id",
          "include" : true,
          "iSortPriority" : 1,
          "mRender" : function(data, type, full) {
            return "<a href=\"/miso/library/" + data + "\">" + full.name + "</a>";
          }
        },
        {
          "sTitle" : "Alias",
          "mData" : "alias",
          "include" : true,
          "iSortPriority" : 0,
          "mRender" : function(data, type, full) {
            return "<a href=\"/miso/library/" + full.id + "\">" + data + "</a>";
          }
        },
        {
          "sTitle" : "Sample Name",
          "sType" : "no-sam",
          "mData" : "parentSampleId",
          "include" : true,
          "iSortPriority" : 0,
          "mRender" : function(data, type, full) {
            return "<a href=\"/miso/sample/" + data + "\">" + full.parentSampleAlias + " (SAM" + data + ")</a>";
          }
        },
        {
          "sTitle" : "QC Passed",
          "mData" : "qcPassed",
          "include" : true,
          "iSortPriority" : 0,
          "mRender" : function(data, type, full) {
            // data is returned as "true", "false", or "null"
            return (data != null ? (data ? "True" : "False") : "Unknown");
          }
        },
        {
          "sTitle" : "Index(es)",
          "mData" : "index1Label",
          "mRender" : function(data, type, full) {
            return (data ? (full.index2Label ? data + ", " + full.index2Label
                : data) : "None");
          },
          "include" : true,
          "iSortPriority" : 0,
          "bSortable" : false
        },
        {
          "sTitle" : "Location",
          "mData" : "locationLabel",
          "include" : true,
          "iSortPriority" : 0,
          "mRender" : function(data, type, full) {
            return full.boxId
                ? "<a href='/miso/box/" + full.boxId + "'>" + data + "</a>"
                : data;
          },
          "bSortable" : false
        }, {
          "sTitle" : "Last Updated",
          "mData" : "lastModified",
          "include" : true,
          "iSortPriority" : 2,
          "bVisible" : (Sample.detailedSample ? "true" : "false")
        }, {
          "sTitle" : "Barcode",
          "mData" : "identificationBarcode",
          "include" : true,
          "iSortPriority" : 0,
          "bVisible" : false
        } ];
  }
};
