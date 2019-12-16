ListTarget.transferitem = (function() {
  return {
    name: "Items",
    createUrl: function(config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function(config, projectId) {
      var actions = [];
      if (config.editSend) {
        actions.push({
          name: 'Remove',
          action: function(items) {
            Transfer.removeItems(items);
          }
        });
      }
      if (config.editReceipt) {
        actions.push({
          name: 'Set Received',
          action: makeUpdateBooleanHandler('Mark Item Receipt', 'Received', 'received')
        }, {
          name: 'Set QC',
          action: makeUpdateBooleanHandler('Mark Item QC', 'QC Passed', 'qcPassed')
        }, {
          name: 'Set Location',
          action: function(items) {
            Utils.showDialog('Box Search', 'Search', [{
              label: 'Name, Alias, or Barcode',
              property: 'query',
              type: 'text'
            }], function(results) {
              if (!results.query) {
                Utils.showConfirmDialog('Remove from Box', 'Remove', ['Remove selected items from their current box locations?'],
                    function() {
                      items.forEach(function(item) {
                        item.boxId = null;
                        item.boxAlias = null;
                        item.boxPosition = null;
                      });
                      Transfer.updateItems(items);
                    });
                return;
              }
              Utils.ajaxWithDialog('Searching for Boxes', 'GET', Urls.rest.boxes.searchPartial + '?' + jQuery.param({
                q: results.query,
                b: true
              }), null, function(boxes) {
                Utils.showWizardDialog('Add to Box', boxes.map(function(box) {
                  return {
                    name: box.name + ' - ' + box.alias,
                    handler: function() {
                      Utils.showDialog('Add to Box', 'Add', items.map(function(item) {
                        return {
                          label: item.name + ' - ' + item.alias,
                          property: item.name + 'Position',
                          type: 'select',
                          values: [null].concat(Utils.getEmptyBoxPositions(box)),
                          getLabel: function(value) {
                            return value || 'n/a';
                          }
                        };
                      }), function(positionResults) {
                        items.forEach(function(item) {
                          if (positionResults[item.name + 'Position']) {
                            item.boxId = box.id;
                            item.boxAlias = box.alias;
                            item.boxPosition = positionResults[item.name + 'Position'];
                          } else {
                            item.boxId = null;
                            item.boxAlias = null;
                            item.boxPosition = null;
                          }
                        });
                        Transfer.updateItems(items);
                      });
                    }
                  };
                }));
              });
            });
          }
        })
      }
      return actions;
    },
    createStaticActions: function(config, projectId) {
      return !config.editSend ? [] : [{
        name: 'Add',
        handler: function() {
          Utils.showWizardDialog('Add Items', [makeAddHandler('Samples', 'Add Samples', Urls.rest.samples.query, 'Sample'),
              makeAddHandler('Libraries', 'Add Libraries', Urls.rest.libraries.query, 'Library'),
              makeAddHandler('Library Aliquots', 'Add Library Aliquots', Urls.rest.libraryAliquots.query, 'Library Aliquot'),
              makeAddHandler('Pools', 'Add Pools', Urls.rest.pools.query, 'Pool')]);
        }
      }];
    },
    createColumns: function(config, projectId) {
      return [{
        sTitle: 'Type',
        mData: 'type',
        include: true,
        iSortPriority: 1
      }, {
        sTitle: 'Name',
        mData: 'id', // For sorting purposes
        mRender: function(data, type, full) {
          if (type === 'display') {
            return data ? '<a href="' + getEditUrl(full) + '">' + full.name + '</a>' : '';
          } else {
            return data;
          }
        },
        include: true,
        iSortPriority: 2
      }, {
        sTitle: 'Alias',
        mData: 'alias',
        mRender: function(data, type, full) {
          if (type === 'display') {
            return data ? '<a href="' + getEditUrl(full) + '">' + data + '</a>' : '';
          } else {
            return data;
          }
        },
        include: true,
        iSortPriority: 0
      }, {
        sTitle: 'Received',
        mData: 'received',
        mRender: ListUtils.render.booleanChecks,
        include: true,
        iSortPriority: 0
      }, {
        sTitle: 'Location',
        mData: 'boxId',
        mRender: function(data, type, full) {
          if (type === 'display') {
            return data ? (full.boxAlias + ' ' + full.boxPosition) : 'Unknown';
          }
          return data;
        },
        include: true,
        bSortable: false
      }, {
        sTitle: 'QC Passed',
        mData: 'qcPassed',
        mRender: ListUtils.render.booleanChecks,
        include: true,
        iSortPriority: 0
      }, {
        sTitle: 'QC Note',
        mData: 'qcNote',
        include: true,
        iSortPriority: 0
      }];
    }
  };

  function getEditUrl(item) {
    switch (item.type) {
    case "Sample":
      return Urls.ui.samples.edit(item.id);
    case "Library":
      return Urls.ui.libraries.edit(item.id);
    case "Library Aliquot":
      return Urls.ui.libraryAliquots.edit(item.id);
    case "Pool":
      return Urls.ui.pools.edit(item.id);
    default:
      throw new Error('Unhandled transfer item type: ' + item.type);
    }
  }

  function makeAddHandler(handlerName, title, queryUrl, itemType) {
    return {
      name: handlerName,
      handler: function() {
        Utils.showDialog(title, "Search", [{
          label: "Names, Aliases, or Barcodes",
          type: "textarea",
          property: "names",
          rows: 15,
          cols: 40,
          required: true
        }], function(result) {
          var names = result.names.split(/[ \t\r\n]+/).filter(function(name) {
            return name.length > 0;
          });
          if (names.length == 0) {
            return;
          }
          Utils.ajaxWithDialog('Searching', 'POST', queryUrl, names, function(items) {
            var dupes = [];
            Transfer.getItems().forEach(function(transferItem) {
              if (items.map(Utils.array.getId).indexOf(transferItem.id) !== -1) {
                dupes.push(transferItem);
              }
            });
            if (dupes.length) {
              Utils.showOkDialog('Error', ['The following items are already included in this transfer:'].concat(dupes.map(function(item) {
                return '* ' + item.name + ' (' + item.alias + ')';
              })));
            } else {
              Transfer.addItems(items.map(function(item) {
                return {
                  type: itemType,
                  id: item.id,
                  name: item.name,
                  alias: item.alias,
                  boxId: item.box ? item.box.id : null,
                  boxAlias: item.box ? item.box.alias : null,
                  boxPosition: item.boxPosition,
                  received: null,
                  qcPassed: null,
                  qcNote: null
                }
              }));
            }
          });
        });
      }
    }
  }

  function makeUpdateBooleanHandler(title, fieldLabel, property, includeQcNote) {
    var isQc = property === 'qcPassed';
    return function(items) {
      var fields = [{
        label: fieldLabel,
        property: 'value',
        type: 'select',
        values: [true, false, null],
        getLabel: function(value) {
          switch (value) {
          case true:
            return 'Yes';
          case false:
            return 'No';
          case null:
            return 'Unknown';
          }
        },
        value: 'Unknown'
      }];
      if (isQc) {
        fields.push({
          label: 'QC Note',
          property: 'qcNote',
          type: 'text'
        });
      }
      Utils.showDialog(title, 'Update', fields, function(results) {
        if (isQc) {
          if (results.value === false && !results.qcNote) {
            Utils.showOkDialog('Error', ['QC note is required when QC is failed']);
            return;
          } else if (!new RegExp(Utils.validation.sanitizeRegex).test(results.qcNote)) {
            Utils.showOkDialog('Error', ['QC note contains invalid characters']);
            return;
          }
        }
        items.forEach(function(item) {
          item[property] = results.value;
          if (isQc) {
            item.qcNote = results.qcNote;
          }
        });
        Transfer.updateItems(items);
      });
    };
  }

})();
