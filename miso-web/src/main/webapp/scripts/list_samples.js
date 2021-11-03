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

ListTarget.sample = (function() {

  return {
    name: "Samples",
    getUserManualUrl: function() {
      return Urls.external.userManual('samples');
    },
    createUrl: function(config, projectId) {
      if (projectId) {
        if (config.arrayed) {
          return Urls.rest.samples.projectArrayedDatatable(projectId);
        } else {
          return Urls.rest.samples.projectDatatable(projectId);
        }
      } else if (config.worksetId) {
        return Urls.rest.samples.worksetDatatable(config.worksetId);
      } else if (config.requisitionId) {
        return Urls.rest.samples.requisitionDatatable(config.requisitionId);
      }
      return Urls.rest.samples.datatable;
    },
    getQueryUrl: function() {
      return Urls.rest.samples.query;
    },
    createBulkActions: function(config, projectId) {
      var actions = BulkTarget.sample.getBulkActions(config);
      
      if (config.requisitionId) {
        actions.unshift({
          name: 'Remove',
          action: samplesUpdateFunction(Urls.rest.requisitions.removeSamples(config.requisitionId))
        });
      }

      if (config.worksetId) {
        actions.push(HotUtils.makeMoveFromWorkset('samples', Urls.rest.worksets.moveSamples(config.worksetId)));
      }

      actions.push({
        name: "Delete",
        action: function(items) {
          var lines = ['Are you sure you wish to delete the following samples? This cannot be undone.',
              'Note: a Sample may only be deleted by its creator or an admin.'];
          var ids = [];
          jQuery.each(items, function(index, sample) {
            lines.push('* ' + sample.name + ' (' + sample.alias + ')');
            ids.push(sample.id);
          });
          Utils.showConfirmDialog('Delete Samples', 'Delete', lines, function() {
            Utils.ajaxWithDialog('Deleting Samples', 'POST', Urls.rest.samples.bulkDelete, ids, function() {
              Utils.page.pageReload();
            });
          });
        }
      });

      return actions;

    },
    createStaticActions: function(config, projectId) {
      var actions = [];
      
      if (config.requisitionId) {
        actions.push({
          name: "Add",
          handler: function() {
            Utils.showSearchByNamesDialog('Add Samples', Urls.rest.samples.query,
                samplesUpdateFunction(Urls.rest.requisitions.addSamples(config.requisitionId)));
          }
        });
      }
      
      actions.push({
        name: "Create",
        handler: function() {
          var fields = [{
            property: 'quantity',
            type: 'int',
            label: 'Quantity',
            value: 1,
            required: true
          }];

          if (Constants.isDetailedSample) {
            fields.unshift({
              property: 'sampleCategory',
              type: 'select',
              label: 'Sample Category',
              values: getCreatableCategories(),
              required: true
            });
          }
          BulkUtils.actions.showDialogForBoxCreation('Create Samples', 'Create', fields, Urls.ui.samples.bulkCreate, function(result) {
            if (result.quantity < 1) {
              Utils.showOkDialog('Create Samples', ["That's a peculiar number of samples to create."]);
              return;
            }
            if (result.createBox && Constants.isDetailedSample && result.sampleCategory == 'Identity') {
              Utils.showOkDialog('Error', ["Identities cannot be placed in boxes"]);
              return;
            }
            return {
              quantity: result.quantity,
              projectId: projectId,
              targetCategory: Constants.isDetailedSample ? result.sampleCategory : null
            };
          }, function(result) {
            return result.quantity;
          });
        }
      });
      
      return actions;
    },
    createColumns: function(config, projectId) {
      return [{
        "sTitle": "ID",
        "mData": "id",
        "bVisible": false
      }, {
        "sTitle": "Name",
        "mData": "name",
        "include": true,
        "iSortPriority": 1,
        "iDataSort": 0, // Use ID for sorting
        "mRender": Warning.tableWarningRenderer(WarningTarget.sample, function(sample) {
          return Urls.ui.samples.edit(sample.id);
        }),
        "sClass": "nowrap"
      }, ListUtils.labelHyperlinkColumn("Alias", Urls.ui.samples.edit, Utils.array.getId, "alias", 0, true), {
        "sTitle": "External Name",
        "mData": "effectiveExternalNames",
        "include": Constants.isDetailedSample
      }, {
        "sTitle": "Tissue Attributes",
        "mData": "effectiveTissueTypeLabel",
        "include": Constants.isDetailedSample,
        "mRender": function(data, type, full) {
          if (type !== "display") {
            return data;
          } else if (!data) {
            return 'n/a';
          }
          var label = full.effectiveTissueOriginLabel + " " + full.effectiveTissueTypeLabel;
          if (full.effectiveTimepoint) {
            if (full.effectiveTimepoint.length > 12) {
              label += " " + full.effectiveTimepoint.substring(0,12) + "&#8230;"; // #8230=ellipsis
            } else {
              label += " " + full.effectiveTimepoint;
            }
          }
          return '<div class="tooltip" style="width: 100%;">'
              + '<span>' + label + '</span>'
              + '<span class="tooltiptext">'
              + 'Tissue origin: ' + full.effectiveTissueOriginLabel + '<br/>'
              + 'Tissue type: ' + full.effectiveTissueTypeLabel + '<br/>'
              + (full.effectiveTimepoint ? 'Timepoint: ' + full.effectiveTimepoint + '<br/>' : '')
              + '</span>'
              + '</div>';
        }
      }, {
        "sTitle": "Sample Class",
        "mData": "sampleClassId",
        "include": Constants.isDetailedSample,
        "mRender": ListUtils.render.textFromId(Constants.sampleClasses, 'alias', "Plain"),
        "iSortPriority": 0
      }, {
        "sTitle": "Type",
        "mData": "sampleType",
        "include": !Constants.isDetailedSample,
        "iSortPriority": 0
      }, ListUtils.columns.detailedQcStatus, {
        "sTitle": "Location",
        "mData": "locationLabel",
        "bSortable": false,
        "mRender": function(data, type, full) {
          return full.boxId ? "<a href='" + Urls.ui.boxes.edit(full.boxId) + "'>" + data + "</a>" : data;
        },
        "include": true,
        "iSortPriority": 0
      }, {
        "sTitle": "Created",
        "mData": "creationDate",
        "sDefaultContent": "",
        "include": Constants.isDetailedSample,
        "iSortPriority": 0
      }, {
        "sTitle": "Modified",
        "mData": "lastModified",
        "mRender": function(data, type, full) {
          if (type !== 'display') {
            return data;
          }
          return '<div class="tooltip">'
              + '<span>' + data.split(" ")[0] + '</span>'
              + '<span class="tooltiptext">' + data + '</span>'
              + '</div>'
        },
        "include": Constants.isDetailedSample,
        "iSortPriority": 2
      }, {
        "sTitle": "Added",
        "mData": "worksetAddedTime",
        "sDefaultContent": "n/a",
        "mRender": ListUtils.render.naIfNull,
        "include": config.worksetId,
        "bSortable": false
      }];
    },
    searchTermSelector: function(searchTerms) {
      const plainSampleTerms = [searchTerms['id'], searchTerms['barcode'], searchTerms['requisition'], searchTerms['entered'],
          searchTerms['changed'], searchTerms['received'], searchTerms['creator'], searchTerms['changedby'], searchTerms['box'],
          searchTerms['freezer'], searchTerms['requisition'], searchTerms['distributed'], searchTerms['distributedto']];
      const detailedSampleTerms = [searchTerms['created'], searchTerms['class'], searchTerms['tissueOrigin'], searchTerms['tissueType'],
          searchTerms['timepoint'], searchTerms['lab'], searchTerms['external'], searchTerms['subproject'], searchTerms['groupid'],
          searchTerms['ghost']];
      if (Constants.isDetailedSample) {
        return plainSampleTerms.concat(detailedSampleTerms);
      } else {
        return plainSampleTerms;
      }
    }
  };

  function getCreatableCategories() {
    return ['Identity', 'Tissue', 'Tissue Processing', 'Stock', 'Aliquot'].filter(function(category) {
      return Constants.sampleClasses.some(function(sampleClass) {
        return !sampleClass.archived && sampleClass.directCreationAllowed && sampleClass.sampleCategory === category;
      });
    });
  }
  
  function samplesUpdateFunction(saveUrl) {
    return function(items) {
      var callback = function(update) {
        switch (update.status) {
          case 'completed':
            Utils.page.pageReload();
            break;
          case 'failed':
            Utils.showOkDialog('Error', ['Failed to save samples']);
            break;
          default:
            Utils.showOkDialog('Error', ['Unexpected operation status. The save may still be in progress or completed.']);
          }
      };
      var errorCallback = function(response, textStatus, errorThrown, startedOk) {
        var message = startedOk
            ? 'Failed to check operation status. The save may still be in progress or completed.'
            : 'Save Failed.';
        Utils.showAjaxErrorDialog(response, textStatus, message);
      };
      Utils.saveWithProgressDialog('POST', saveUrl, items.map(Utils.array.getId),
          Urls.rest.requisitions.samplesUpdateProgress, callback, errorCallback);
    };
  }

})();