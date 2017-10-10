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

ListTarget.sample = {
  name: "Samples",
  createUrl: function(config, projectId) {
    return "/miso/rest/tree/samples/dt" + (projectId ? '/project/' + projectId : '');
  },
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.sample.bulkActions;

    if (projectId) {
      actions = actions.concat([{
        name: "Get Form",
        action: function(items) {
          Utils.showDialog("Get Information Form", "Next", [{
            property: "format",
            type: "select",
            label: "Type",
            values: [{
              name: "Tubes",
              value: false
            }, {
              name: "Plate",
              value: true
            }],
            getLabel: Utils.array.getName
          }], function(result) {
            Project.ui.processSampleDeliveryForm(projectId, result.format.value, items.map(Utils.array.getId));
          });
        }
      }, {
        name: "Receive",
        action: function(items) {
          Project.ui.receiveSelectedSamples(items.map(Utils.array.getId));
        }
      }, ]);
    }
    return actions;

  },
  createStaticActions: function(config, projectId) {
    return [{
      name: "Create",
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
            label: 'Sample Class',
            values: Constants.sampleClasses.sort(Utils.sorting.sampleClassComparator),
            getLabel: Utils.array.getAlias
          });
        }

        Utils.showDialog('Create Samples', 'Create', fields, function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Create Samples', ["That's a peculiar number of samples to create."]);
            return;
          }
          if (!Constants.isDetailedSample && result.quantity == 1) {
            window.location = '/miso/sample/new' + (projectId ? '/' + projectId : '');
            return;
          }
          window.location = '/miso/sample/bulk/new?' + jQuery.param({
            quantity: result.quantity,
            projectId: projectId,
            sampleClassId: Constants.isDetailedSample ? result.sampleClass.id : null
          });
        });
      }
    }, {
      name: "Get Input Form",
      include: !!projectId,
      handler: function() {
        Utils.showDialog("Get Bulk Sample Input Form", "Get Form", [{
          property: 'formType',
          type: 'select',
          label: 'Format',
          values: [{
            name: "OpenOffice (ODS)",
            value: "ods"
          }, {
            name: "Excel (XLSX)",
            value: "xlsx"
          }],
          getLabel: Utils.array.getName
        }], function(result) {
          Project.ui.downloadBulkSampleInputForm(projectId, result.formType.value);
        });
      }
    }, {
      name: "Import/Export",
      include: !!projectId,
      handler: function() {
        Utils.showWizardDialog('Import/Export', [

        {
          name: "Import Input Form",
          handler: function() {
            Project.ui.uploadBulkSampleInputForm();
          }
        }, {
          name: "Import Information",
          handler: function() {
            Project.ui.uploadSampleDeliveryForm();
          }
        }, {
          name: "Export Sample QC Sheet",
          handler: function() {
            window.location = "/miso/importexport/exportsamplesheet";
          }
        }, {
          name: "Import Sample QC Sheet",
          handler: function() {
            window.location = "/miso/importexport/importsamplesheet";
          }
        }, {
          name: "Import Library Sheet",
          handler: function() {
            window.location = "/miso/importexport/importlibrarypoolsheet";
          }
        }]);
      }
    }].filter(function(x) {
      return !x || x.include;
    });
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", "sample", "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "sample", Utils.array.getId, "alias", 0, true), {
          "sTitle": "Sample Class",
          "mData": "sampleClassId",
          "include": Constants.isDetailedSample,
          "mRender": ListUtils.render.textFromId(Constants.sampleClasses, 'alias', "Plain"),
          "bVisible": "true",
          "bSortable": false,
          "iSortPriority": 0
        }, {
          "sTitle": "Type",
          "mData": "sampleType",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "QC Passed",
          "mData": "qcPassed",
          "mRender": ListUtils.render.booleanChecks,
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
