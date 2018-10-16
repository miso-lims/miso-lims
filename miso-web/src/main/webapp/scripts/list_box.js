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

ListTarget.box = {
  name: "Boxes",
  createUrl: function(config, projectId) {
    return "/miso/rest/box/dt" + (config.boxUse ? "/use/" + config.boxUse : "");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return HotTarget.box.getBulkActions(config).concat(
        [
            HotUtils.printAction('box'),
            {
              name: "Delete",
              action: function(items) {
                var lines = ['Are you sure you wish to delete the following boxes? This cannot be undone.',
                    'Note: a Box may only be deleted by its creator or an admin.'];
                var ids = [];
                jQuery.each(items, function(index, box) {
                  lines.push('* ' + box.name + ' (' + box.alias + ')');
                  ids.push(box.id);
                });
                Utils.showConfirmDialog('Delete Boxes', 'Delete', lines, function() {
                  Utils.ajaxWithDialog('Deleting Boxes', 'POST', '/miso/rest/boxes/bulk-delete', ids, function() {
                    window.location = window.location.origin + '/miso/boxes';
                  });
                });
              }
            }]);
  },
  createStaticActions: function(config, projectId) {
    return [{
      "name": "Add",
      "handler": function() {
        var fields = [{
          property: 'quantity',
          type: 'int',
          label: 'Quantity',
          value: 1
        }];

        Utils.showDialog('Create Boxes', 'Create', fields, function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Create Boxes', ["That's a peculiar number of boxes to create."]);
            return;
          }
          if (result.quantity == 1) {
            window.location = '/miso/box/new';
            return;
          }
          window.location = '/miso/box/bulk/new?' + jQuery.param({
            quantity: result.quantity,
          });
        });
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", "box", "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "box", Utils.array.getId, "alias", 0, true), {
          "sTitle": "Description",
          "mData": "description",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Freezer Location",
          "mData": "freezerDisplayLocation",
          "include": config.showFreezerLocation,
          "bSortable": false,
          "iSortPriority": 0
        }, {
          "sTitle": "Storage Location",
          "mData": "storageDisplayLocation",
          "include": config.showStorageLocation,
          "bSortable": false,
          "iSortPriority": 0
        }, {
          "sTitle": "Location",
          "mData": "locationBarcode",
          "include": true,
          "iSortPriority": 0
        }, {
          "sTitle": "Items/Capacity",
          "mData": "tubeCount",
          "include": true,
          "iSortPriority": 0,
          "bSortable": false,
          "mRender": function(data, type, full) {
            return full.tubeCount + "/" + (full.rows * full.cols);
          }
        }, {
          "sTitle": "Size",
          "mData": "sizeId",
          "include": true,
          "iSortPriority": 0,
          "mRender": ListUtils.render.textFromId(Constants.boxSizes, 'rowsByColumns')
        }, {
          "sTitle": "Use",
          "mData": "useId",
          "include": !config.boxUse,
          "iSortPriority": 0,
          "mRender": ListUtils.render.textFromId(Constants.boxUses, 'alias')
        }];
  }
};
