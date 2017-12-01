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
  name: "Libraries",
  createUrl: function(config, projectId) {
    return "/miso/rest/library/dt" + (projectId ? '/project/' + projectId : '');
  },
  createBulkActions: function(config, projectId) {
    return HotTarget.library.bulkActions;
  },
  createStaticActions: function(config, projectId) {
    return [{
      name: "Receive",
      include: true,
      handler: function() {
        var fields = [{
          property: 'quantity',
          type: 'int',
          label: 'Quantity',
          value: 1
        }];

        if (Constants.isDetailedSample) {
          fields.unshift({
            property: 'sampleClass',
            type: 'select',
            label: 'Aliquot Class',
            values: Constants.sampleClasses.filter(function(sampleClass) {
              return sampleClass.sampleCategory === 'Aliquot';
            }).sort(Utils.sorting.sampleClassComparator),
            getLabel: Utils.array.getAlias
          });
        }

        Utils.showDialog('Receive Libraries', 'Receive', fields, function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Receive Libraries', ["That's a peculiar number of libraries to receive."]);
            return;
          }
          window.location = '/miso/library/bulk/receive?' + jQuery.param({
            quantity: result.quantity,
            projectId: projectId,
            sampleClassId: Constants.isDetailedSample ? result.sampleClass.id : null
          });
        });
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", "library", "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "library", Utils.array.getId, "alias", 0, true),
        ListUtils.idHyperlinkColumn("Sample Name", "sample", "parentSampleId", function(library) {
          return "SAM" + library.parentSampleId;
        }, 0, true), ListUtils.labelHyperlinkColumn("Sample Alias", "sample", function(library) {
          return library.parentSampleId;
        }, "parentSampleAlias", 0, true), {
          "sTitle": "QC Passed",
          "mData": "qcPassed",
          "include": true,
          "iSortPriority": 0,
          "mRender": ListUtils.render.booleanChecks
        }, {
          "sTitle": "Index(es)",
          "mData": "index1Label",
          "mRender": function(data, type, full) {
            return (data ? (full.index2Label ? data + ", " + full.index2Label : data) : "None");
          },
          "include": true,
          "iSortPriority": 0,
          "bSortable": false
        }, {
          "sTitle": "Location",
          "mData": "locationLabel",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            return full.boxId ? "<a href='/miso/box/" + full.boxId + "'>" + data + "</a>" : data;
          },
          "bSortable": false
        }, {
          "sTitle": "Last Modified",
          "mData": "lastModified",
          "include": true,
          "iSortPriority": 2,
          "bVisible": (Sample.detailedSample ? "true" : "false")
        }, {
          "sTitle": "Barcode",
          "mData": "identificationBarcode",
          "include": true,
          "iSortPriority": 0,
          "bVisible": false
        }];
  }
};
