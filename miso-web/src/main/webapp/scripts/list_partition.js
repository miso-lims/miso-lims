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

ListTarget.partition = {
  name : "Partition",
  createUrl : function(config, projectId) {
    throw "Can only be created statically";
  },
  createBulkActions : function(config, projectId) {
    var maxDilutions = 5;
    var platformType = Utils.array.findFirstOrNull(function(pt) {
      return pt.name == config.platformType;
    }, Constants.platformTypes);
    
    var showPoolTiles = function(response, assignCallback, backHandler) {
      var dialogArea = document.getElementById('dialog');
      while (dialogArea.hasChildNodes()) {
        dialogArea.removeChild(dialogArea.lastChild);
      }
      
      response.errors
          .forEach(function(errorMessage) {
            var errorLine = document.createElement('P');
            errorLine.setAttribute('class', 'parsley-error');
            errorLine.innerText = "... " + (response.numMatches - response.items.length) + " more";
            dialogArea.appendChild(errorLine);
          });
      
      response.items
          .forEach(function(item) {
            var div = document.createElement('DIV');
            div.setAttribute('class', 'pool-tile');
            
            var title = document.createElement('DIV');
            title.setAttribute('style', 'font-weight:bold');
            title.innerText = item.pool.name + " (" + item.pool.alias + ")";
            div.appendChild(title);
            
            var dilutionP = document.createElement('P');
            item.pool.pooledElements
                .filter(
                    function(element, index, array) {
                      return array.length < maxDilutions || index < maxDilutions - 1;
                    })
                .forEach(
                    function(dilution) {
                      dilutionP
                          .appendChild(document
                              .createTextNode(dilution.name + " - " + dilution.library.name + " (" + dilution.library.alias + ")"));
                      dilutionP.appendChild(document.createElement('BR'));
                    });
            if (item.pool.pooledElements.length >= maxDilutions) {
              dilutionP
                  .appendChild(document
                      .createTextNode("...and " + (item.pool.pooledElements.length - maxDilutions + 1) + " more dilutions"));
              
            }
            div.appendChild(dilutionP);
            var orderP = document.createElement('P');
            orderP.setAttribute('style', 'font-style:italic');
            item.orders
                .filter(function(order) {
                  return order.remaining > 0;
                })
                .forEach(
                    function(order) {
                      orderP
                          .appendChild(document
                              .createTextNode(order.parameters.name + ": " + order.remaining + " " + platformType.partitionName + " remaining"));
                      orderP.appendChild(document.createElement('BR'));
                    });
            div.appendChild(orderP);
            div.onclick = function() {
              dialog.dialog("close");
              assignCallback(item.pool.id);
              return false;
            };
            dialogArea.appendChild(div);
          });
      if (response.numMatches > response.items.length) {
        var moreMatches = document.createElement('P');
        moreMatches.innerText = "...and " + (response.numMatches - response.items.length) + " more pools not shown";
        dialogArea.appendChild(moreMatches);
      }
      if (response.items.length == 0) {
        var noMatches = document.createElement('P');
        noMatches.innerText = "No pools found.";
        dialogArea.appendChild(noMatches);
      }
      
      var dialog = jQuery('#dialog').dialog({
        autoOpen : true,
        height : 500,
        width : 600,
        title : 'Select Pool',
        modal : true,
        buttons : {
          "Back" : function() {
            dialog.dialog("close");
            backHandler();
          },
          "Cancel" : function() {
            dialog.dialog("close");
          }
        }
      });
    };
    
    var assignFromRest = function(url, name, assignCallback, backHandler) {
      var handler = function() {
        Utils.ajaxWithDialog('Getting Pools', 'GET', url, null, function(
            response) {
          showPoolTiles(response, assignCallback, backHandler);
        });
      };
      return {
        name : name,
        handler : handler
      };
    };
    
    var actions = [ {
      name : "Assign Pool",
      action : function(partitions) {
        var assign = function(poolId) {
          Utils.ajaxWithDialog('Assigning Pool', 'POST',
              '/miso/rest/pool/' + poolId + '/assign', partitions
                  .map(Utils.array.getId), Utils.page.pageReload);
        };
        var makeSearch = function(defaultQuery, backHandler) {
          return function() {
            Utils.showDialog('Search for Pool to Assign', 'Search', [ {
              type : "text",
              label : "Search",
              property : "query",
              value : defaultQuery
            }, ], function(results) {
              Utils.ajaxWithDialog('Getting Pools', 'GET',
                  '/miso/rest/pool/picker/search?' + jQuery.param({
                    platform : platformType.name,
                    query : results.query
                  }), null, function(response) {
                    showPoolTiles(response, assign, makeSearch(results.query,
                        backHandler));
                  });
            }, backHandler);
            
          };
        };
        
        var assignActions;
        var assignDialog = function() {
          Utils.showWizardDialog("Assign Pool", assignActions);
        }

        assignActions = [
            {
              name : "No Pool",
              handler : function() {
                assign(0);
              }
            },
            {
              name : "Search",
              handler : makeSearch("", assignDialog)
            },
            config.sequencingParametersId ? assignFromRest(
                '/miso/rest/poolorder/picker/chemistry?' + jQuery.param({
                  platform : platformType.name,
                  seqParamsId : config.sequencingParametersId,
                  fulfilled : false
                }), 'Outstanding Orders (Matched Chemistry)', assign,
                assignDialog) : null,
            assignFromRest('/miso/rest/poolorder/picker/active?' + jQuery
                .param({
                  platform : platformType.name
                }), 'Outstanding Orders (All)', assign, assignDialog),
            assignFromRest('/miso/rest/pool/picker/readytorun?' + jQuery
                .param({
                  platform : platformType.name,
                  readyToRun : true
                }), 'Ready to Run', assign, assignDialog),
            assignFromRest('/miso/rest/pool/picker/recent?' + jQuery.param({
              platform : platformType.name
            }), 'Recently Modified', assign, assignDialog), ]
            .filter(function(x) {
              return x;
            });
        assignDialog();
      }
    } ];
    if (config.runId) {
      actions.push({
        name : "Set QC",
        action : function(partitions) {
          var setQc = function(id, notes) {
            Utils.ajaxWithDialog('Setting QC', 'POST',
                '/miso/rest/run/' + config.runId + '/qc', {
                  "partitionIds" : partitions.map(Utils.array.getId),
                  "qcTypeId" : id,
                  "notes" : notes
                }, Utils.page.pageReload);
          };
          
          Utils.showWizardDialog("Set QC", Constants.partitionQcTypes
              .map(function(qcType) {
                return {
                  name : qcType.description,
                  handler : function() {
                    if (qcType.noteRequired) {
                      Utils.showDialog(qcType.description + " Notes", "Set",
                          [ {
                            type : "text",
                            label : "Notes",
                            property : "notes"
                          } ], function(results) {
                            setQc(qcType.id, results.notes);
                          });
                    } else {
                      setQc(qcType.id, null);
                    }
                  }
                };
              }));
        }
      });
    }
    return actions;
  },
  createStaticActions : function(config, projectId) {
    return [];
  },
  createColumns : function(config, projectId) {
    return [
        {
          "sTitle" : "Container",
          "mData" : "containerName",
          "include" : config.showContainer,
          "iSortPriority" : 0
        },
        {
          "sTitle" : "Number",
          "mData" : "partitionNumber",
          "include" : true,
          "iSortPriority" : 1,
          "bSortDirection" : true
        },
        {
          "sTitle" : "Pool",
          "mData" : "pool",
          "include" : true,
          "iSortPriority" : 0,
          "mRender" : function(data, type, full) {
            if (!data) {
              if (type === 'display') {
                return "(None)";
              } else {
                return "";
              }
            }
            var prettyName = data.name + " (" + data.alias + ")";
            if (type === 'display') {
              return "<a href=\"/miso/pool/" + data.id + "\">" + prettyName + "</a>" + (data.duplicateIndices
                  ? ' <span class="lowquality">DUPLICATE INDICES</span><img style="float:right; height:25px;" src="/styles/images/fail.png" />'
                  : "");
            } else {
              return prettyName;
            }
            
          }
        },
        {
          "sTitle" : "QC Status",
          "mData" : "qcType",
          "include" : config.runId,
          "mRender" : ListUtils.render.textFromId(Constants.partitionQcTypes,
              'description', '(Unset)')
        }, {
          "sTitle" : "QC Notes",
          "mData" : "qcNotes",
          "include" : config.runId
        } ];
  }
};
