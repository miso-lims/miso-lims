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
    }
    return Urls.rest.samples.datatable;
  },
  getQueryUrl: function() {
    return Urls.rest.samples.query;
  },
  createBulkActions: function(config, projectId) {
    var actions = BulkTarget.sample.getBulkActions(config);

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
    return [{
      name: "Create",
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
            values: Utils.array.removeArchived(Constants.sampleClasses).filter(function(sampleClass) {
              return sampleClass.directCreationAllowed;
            }).sort(Utils.sorting.sampleClassComparator),
            getLabel: Utils.array.getAlias
          });
        }
        BulkUtils.actions.showDialogForBoxCreation('Create Samples', 'Create', fields, Urls.ui.samples.bulkCreate, function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Create Samples', ["That's a peculiar number of samples to create."]);
            return;
          }
          if (result.createBox && Constants.isDetailedSample && result.sampleClass.sampleCategory == 'Identity') {
            Utils.showOkDialog('Error', ["Identities cannot be placed in boxes"]);
            return;
          }
          return {
            quantity: result.quantity,
            projectId: projectId,
            sampleClassId: Constants.isDetailedSample ? result.sampleClass.id : null
          };
        }, function(result) {
          return result.quantity;
        });
      }
    }];
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
      "sTitle": "Tissue Origin",
      "mData": "effectiveTissueOriginLabel",
      "include": Constants.isDetailedSample,
      "mRender": ListUtils.render.naIfNull,
      "iSortPriority": 0
    }, {
      "sTitle": "Tissue Type",
      "mData": "effectiveTissueTypeLabel",
      "include": Constants.isDetailedSample,
      "mRender": ListUtils.render.naIfNull,
      "iSortPriority": 0
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
      "sTitle": "Creation Date",
      "mData": "creationDate",
      "sDefaultContent": "",
      "include": Constants.isDetailedSample,
      "iSortPriority": 0
    }, {
      "sTitle": "Last Modified",
      "mData": "lastModified",
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
    const plainSampleTerms = [searchTerms['id'], searchTerms['requisition'], searchTerms['entered'], searchTerms['changed'],
        searchTerms['received'], searchTerms['creator'], searchTerms['changedby'], searchTerms['box'], searchTerms['freezer'],
        searchTerms['distributed'], searchTerms['distributedto']];
    const detailedSampleTerms = [searchTerms['created'], searchTerms['class'], searchTerms['tissueOrigin'], searchTerms['tissueType'],
        searchTerms['institute'], searchTerms['external'], searchTerms['subproject'], searchTerms['groupid'], searchTerms['ghost']];
    if (Constants.isDetailedSample) {
      return plainSampleTerms.concat(detailedSampleTerms);
    } else {
      return plainSampleTerms;
    }
  }
};
