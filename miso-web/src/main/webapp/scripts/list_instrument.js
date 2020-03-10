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

ListTarget.instrument = {
  name: "Instruments",
  getUserManualUrl: function() {
    return Urls.external.userManual('instruments');
  },
  createUrl: function(config, projectId) {
    if (config.instrumentType) {
      return Urls.rest.instruments.instrumentTypeDatatable(config.instrumentType);
    } else {
      return Urls.rest.instruments.datatable;
    }
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    return !config.isAdmin ? [] : [ListUtils.createBulkDeleteAction("Instruments", "instruments", function(instrument) {
      return instrument.name + ' (' + instrument.instrumentModelAlias + ')';
    })];
  },
  createStaticActions: function(config, projectId) {
    if (config.isAdmin) {
      return [{
        "name": "Add",
        "handler": function() {
          window.location = Urls.ui.instruments.create;
        }
      }];
    } else {
      return [];
    }
  },
  createColumns: function(config, projectId) {
    return [ListUtils.labelHyperlinkColumn("Instrument Name", Urls.ui.instruments.edit, Utils.array.getId, "name", 1, true), {
      "sTitle": "Platform",
      "mData": "platformType",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Instrument Model",
      "mData": "instrumentModelAlias",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Status",
      "mData": "status",
      "include": true,
      "bSortable": false,
      "mRender": function(data, type, full) {
        return full.outOfService ? "Out of Service" : data;
      }
    }, {
      "sTitle": "Serial Number",
      "mData": "serialNumber",
      "include": true,
      "iSortPriority": 0
    }];
  }
};
