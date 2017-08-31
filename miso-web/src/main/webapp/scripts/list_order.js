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

ListTarget.order = {
  name : "Pool Orders",
  createUrl : function(config, projectId) {
    throw "Orders can only be shown statically.";
  },
  createBulkActions : function(config, projectId) {
    var platformType = Utils.array.findFirstOrNull(function(pt) {
      return pt.name == config.platformType;
    }, Constants.platformTypes);
    return [
        {
          name : 'Order More',
          action : function(orders) {
            Utils.showDialog('Re-order', 'Update', [ {
              type : "int",
              label : "Extra " + platformType.partitionName + " Count",
              property : "count",
              value : 1
            } ], function(results) {
              var updateNext = function(index) {
                if (index >= orders.length) {
                  Utils.page.pageReload();
                  return;
                }
                var copy = JSON.parse(JSON.stringify(orders[index]));
                copy.partitions += results.count;
                Utils.ajaxWithDialog('Updating Order', 'PUT',
                    '/miso/rest/poolorder/' + orders[index].id, copy,
                    function() {
                      updateNext(index + 1)
                    });
              };
              updateNext(0);
            });
          }
        },
        {
          name : 'Delete',
          action : function(orders) {
            var deleteNext = function(index) {
              if (index >= orders.length) {
                Utils.page.pageReload();
                return;
              }
              Utils.ajaxWithDialog('Deleting Order', 'DELETE',
                  '/miso/rest/poolorder/' + orders[index].id, null, function() {
                    deleteNext(index + 1)
                  });
            };
            deleteNext(0);
          }
        }, ];
  },
  createStaticActions : function(config, projectId) {
    var platformType = Utils.array.findFirstOrNull(function(pt) {
      return pt.name == config.platformType;
    }, Constants.platformTypes);
    return [ {
      name : "Create",
      handler : function() {
        Utils.showWizardDialog('Create Order', Constants.platforms.filter(
            function(platform) {
              return platform.platformType == platformType.name; // TODO &&
              // platformType.active
            }).map(
            function(platform) {
              return {
                name : platform.instrumentModel,
                handler : function() {
                  Utils.showDialog('Create Order', 'Save', [
                      {
                        type : "select",
                        label : "Sequencing Parameters",
                        property : "parameters",
                        values : Constants.sequencingParameters
                            .filter(function(parameters) {
                              return parameters.platform.id == platform.id;
                            }),
                        getLabel : Utils.array.getName
                      }, {
                        type : "int",
                        label : platformType.partitionName + " Count",
                        property : "count",
                        value : 1
                      } ], function(results) {
                    
                    Utils.ajaxWithDialog('Creating Order', 'POST',
                        '/miso/rest/poolorder', {
                          "poolId" : config.poolId,
                          "partitions" : results.count,
                          "parameters" : results.parameters,
                        }, Utils.page.pageReload);
                  });
                }
              };
            }));
      }
    } ];
  },
  createColumns : function(config, projectId) {
    var platformType = Utils.array.findFirstOrNull(function(pt) {
      return pt.name == config.platformType;
    }, Constants.platformTypes);
    return [
        {
          "sTitle" : "Platform",
          "mData" : "parameters.platform.instrumentModel",
          "include" : true,
          "iSortPriority" : 1
        },
        {
          "sTitle" : "Sequencing Parameters",
          "mData" : "parameters.id",
          "include" : true,
          "iSortPriority" : 0,
          "mRender" : ListUtils.render.textFromId(
              Constants.sequencingParameters, 'name')
        }, {
          "sTitle" : platformType.partitionName + " Count",
          "mData" : "partitions",
          "include" : true,
          "iSortPriority" : 1
        } ];
  }
};
