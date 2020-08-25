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

ListTarget.sequencingorder = {
  name: "Sequencing Orders",
  getUserManualUrl: function() {
    return Urls.external.userManual('sequencing_orders');
  },
  createUrl: function(config, projectId) {
    throw new Error("Orders can only be shown statically.");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    var platformType = Utils.array.findFirstOrNull(function(pt) {
      return pt.name == config.platformType;
    }, Constants.platformTypes);
    return [{
      name: 'Order More',
      action: function(orders) {
        Utils.showDialog('Re-order', 'Update', [{
          type: "int",
          label: "Extra " + platformType.pluralPartitionName,
          property: "count",
          value: 1
        }], function(results) {
          if (isNaN(results.count) || results.count < 1) {
            Utils.showOkDialog('Error', ['Number to add must be 1 or greater.']);
            return;
          }
          var updateNext = function(index) {
            if (index >= orders.length) {
              Utils.page.pageReload();
              return;
            }
            var copy = JSON.parse(JSON.stringify(orders[index]));
            copy.partitions += results.count;
            Utils.ajaxWithDialog('Updating Order', 'PUT', Urls.rest.sequencingOrders.update(orders[index].id), copy, function() {
              updateNext(index + 1)
            });
          };
          updateNext(0);
        });
      }
    }, ListUtils.createBulkDeleteAction('Sequencing Orders', 'sequencingorders', function(order) {
      return order.pool.alias + ': ' + order.partitions + ' partitions';
    }), ];
  },
  createStaticActions: function(config, projectId) {
    if (config.pool.id) {
      var platformType = Utils.array.findFirstOrNull(function(pt) {
        return pt.name == config.platformType;
      }, Constants.platformTypes);
      return [{
        name: "Create",
        handler: function() {
          window.location = Urls.ui.sequencingOrders.bulkCreate + '?' + jQuery.param({
            ids: config.pool.id
          });
        }
      }];
    }
    return [];
  },
  createColumns: function(config, projectId) {
    var platformType = Utils.array.findFirstOrNull(function(pt) {
      return pt.name == config.platformType;
    }, Constants.platformTypes);
    return [{
      "sTitle": "Purpose",
      "mData": "purposeAlias",
      "include": true,
      "iSortPriority": 0
    }, {
      "sTitle": "Instrument Model",
      "mData": "parameters.instrumentModelAlias",
      "include": true,
      "iSortPriority": 1
    }, {
      sTitle: 'Container Model',
      mData: 'containerModelId',
      mRender: ListUtils.render.textFromId(Constants.containerModels, 'alias', 'n/a')
    }, {
      "sTitle": "Sequencing Parameters",
      "mData": "parameters.id",
      "include": true,
      "iSortPriority": 0,
      "mRender": ListUtils.render.textFromId(Constants.sequencingParameters, 'name')
    }, {
      "sTitle": platformType.pluralPartitionName,
      "mData": "partitions",
      "include": true,
      "iSortPriority": 1
    }, {
      "sTitle": "Description",
      "mData": "description",
      "include": true,
      "iSortPriority": 0
    }];
  }
};
