BulkTarget = window.BulkTarget || {};
BulkTarget.libraryaliquot = (function($) {

  /*
   * Expected config: {
   *   pageMode: string {propagate, edit}
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.libraryAliquots.bulkSave;
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('library_aliquots');
    },
    getCustomActions: function() {
      return [{
        name: 'Fill Boxes by Row',
        action: function(api) {
          fillBoxPositions(api, function(a, b) {
            return Utils.sorting.sortBoxPositions(a, b, true);
          });
        }
      }, {
        name: 'Fill Boxes by Column',
        action: function(api) {
          fillBoxPositions(api, function(a, b) {
            return Utils.sorting.sortBoxPositions(a, b, false);
          });
        }
      }];
    },
    getBulkActions: function(config) {
      return [
          {
            name: 'Edit',
            action: function(items) {
              window.location = Urls.ui.libraryAliquots.bulkEdit + '?' + jQuery.param({
                ids: items.map(Utils.array.getId).join(',')
              });
            },
            allowOnLibraryPage: true
          },
          {
            name: 'Propagate',
            action: function(items) {
              HotUtils.warnIfConsentRevoked(items, function() {
                var fields = [];
                HotUtils.showDialogForBoxCreation('Create Library Aliquots', 'Create', fields, Urls.ui.libraryAliquots.bulkRepropagate,
                    function(result) {
                      return {
                        ids: items.map(Utils.array.getId).join(',')
                      };
                    }, function(result) {
                      return items.length;
                    });
              }, getLabel);
            }
          },
          {
            name: 'Create Order',
            action: function(items) {
              HotUtils.warnIfConsentRevoked(items, function() {
                window.location = Urls.ui.poolOrders.create + '?' + jQuery.param({
                  aliquotIds: items.map(Utils.array.getId).join(',')
                });
              }, getLabel);
            }
          },
          {
            name: 'Pool together',
            title: 'Create one pool from many library aliquots',
            action: function(items) {
              HotUtils.warnIfConsentRevoked(items, function() {
                var fields = [];
                HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, Urls.ui.libraryAliquots.bulkPoolTogether, function(
                    result) {
                  return {
                    ids: items.map(Utils.array.getId).join(',')
                  };
                }, function(result) {
                  return 1;
                });
              }, getLabel);
            },
            allowOnLibraryPage: false
          },
          {
            name: 'Pool separately',
            title: 'Create a pool for each library aliquot',
            action: function(items) {
              HotUtils.warnIfConsentRevoked(items, function() {
                var fields = [];
                HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, Urls.ui.libraryAliquots.bulkPoolSeparate, function(
                    result) {
                  return {
                    ids: items.map(Utils.array.getId).join(',')
                  };
                }, function(result) {
                  return items.length;
                });
              }, getLabel);
            },
            allowOnLibraryPage: true
          },
          {
            name: 'Pool custom',
            title: 'Divide library aliquots into several pools',
            action: function(items) {
              HotUtils.warnIfConsentRevoked(items, function() {
                var fields = [{
                  label: 'Quantity',
                  property: 'quantity',
                  type: 'int',
                }];
                HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, Urls.ui.libraryAliquots.bulkPoolCustom,
                    function(result) {
                      console.log(result);
                      return {
                        ids: items.map(Utils.array.getId).join(','),
                        quantity: result.quantity
                      };
                    }, function(result) {
                      return result.quantity;
                    })
              }, getLabel);
            },
            allowOnLibraryPage: true
          },
          HotUtils.printAction('libraryaliquot'),
          HotUtils.spreadsheetAction(Urls.rest.libraryAliquots.spreadsheet, Constants.libraryAliquotSpreadsheets, function(aliquots,
              spreadsheet) {
            var errors = [];
            return errors;
          }),

          HotUtils.makeParents(Urls.rest.libraryAliquots.parents, HotUtils.relationCategoriesForDetailed().concat(
              [HotUtils.relations.library()])),
          HotUtils.makeChildren(Urls.rest.libraryAliquots.children, [HotUtils.relations.pool()]),
          config.worksetId ? HotUtils.makeRemoveFromWorkset('library aliquots', Urls.rest.worksets.removeLibraryAliquots(config.worksetId))
              : HotUtils.makeAddToWorkset('library aliquots', 'libraryAliquotIds', Urls.rest.worksets.addLibraryAliquots),
          HotUtils.makeTransferAction('libraryAliquotIds')];
    },
    getFixedColumns: function(config) {
      return config.pageMode === 'propagate' ? 1 : 2;
    },
    getColumns: function(config, api) {
      if (config.box) {
        var cache = api.getCache('boxes');
        cacheBox(cache, config.box);
      }
      return [
          {
            title: 'Parent Alias',
            type: 'read-only',
            data: 'parentAlias',
            getDisplayValue: function(aliquot) {
              return aliquot.parentAliquotAlias || aliquot.libraryAlias;
            },
            include: config.pageMode === 'propagate',
            omit: true
          },
          {
            title: 'Name',
            type: 'read-only',
            data: 'name'
          },
          {
            title: 'Alias',
            type: 'text',
            data: 'alias',
            required: config.pageMode === 'edit',
            maxLength: 100
          },
          {
            title: 'Matrix Barcode',
            type: 'text',
            data: 'identificationBarcode',
            include: !Constants.automaticBarcodes,
            maxLength: 255
          },
          {
            title: 'Box Search',
            type: 'text',
            data: 'boxSearch',
            omit: true,
            sortable: false,
            onChange: function(rowIndex, newValue, api) {
              if (!newValue) {
                return;
              }
              var applyChanges = function(source) {
                var value;
                if (!source.length) {
                  value = null;
                } else if (source.length === 1) {
                  value = source[0].alias;
                } else {
                  value = 'SELECT';
                  for (var i = 0; i < source.length; i++) {
                    if (source[i].name.toLowerCase() === searchKey || source[i].alias.toLowerCase() == searchKey
                        || (source[i].identificationBarcode && source[i].identificationBarcode.toLowerCase())) {
                      value = source[i].alias;
                    }
                    break;
                  }
                }
                api.updateField(rowIndex, 'box', {
                  source: source,
                  value: value
                });
              };
              var searchCache = api.getCache('boxSearches');
              var searchKey = newValue.toLowerCase();
              if (searchCache[searchKey]) {
                applyChanges(searchCache[searchKey]);
                return;
              }
              api.updateField(rowIndex, 'box', {
                source: [],
                value: '(searching...)'
              });
              $.ajax({
                url: Urls.rest.boxes.searchPartial + '?' + $.param({
                  q: newValue,
                  b: false
                }),
                dataType: "json"
              }).success(function(data) {
                searchCache[searchKey] = data;
                var itemCache = api.getCache('boxes');
                data.forEach(function(item) {
                  cacheBox(itemCache, item);
                });
                applyChanges(data);
              }).fail(function(response, textStatus, serverStatus) {
                api.showError('Box search failed');
              });
            }
          }, {
            title: 'Box Alias',
            type: 'dropdown',
            data: 'box',
            source: function(data, api) {
              if (data.box) {
                var cache = api.getCache('boxes');
                cacheBox(cache, data.box, data.boxPosition);
                return [data.box];
              } else {
                return [];
              }
            },
            validationCache: 'boxes',
            getItemLabel: Utils.array.getAlias,
            onChange: function(rowIndex, newValue, api) {
              if (newValue) {
                var cache = api.getCache('boxes');
                var box = cache[newValue];
                if (box) {
                  api.updateField(rowIndex, 'boxPosition', {
                    source: box.emptyPositions,
                    required: true,
                    disabled: false
                  });
                  return;
                }
              }
              api.updateField(rowIndex, 'boxPosition', {
                source: [],
                value: null,
                required: false,
                disabled: true
              });
            },
            initial: config.box ? config.box.alias : null
          }, {
            title: 'Position',
            type: 'dropdown',
            data: 'boxPosition',
            // source is initialized in box onChange
            source: [],
            customSorting: [{
              name: 'Position (by rows)',
              sort: function(a, b) {
                return Utils.sorting.sortBoxPositions(a, b, true);
              }
            }, {
              name: 'Position (by columns)',
              sort: function(a, b) {
                return Utils.sorting.sortBoxPositions(a, b, false);
              }
            }]
          }, {
            title: 'Discarded',
            type: 'dropdown',
            data: 'discarded',
            required: true,
            source: [{
              label: 'False',
              value: false
            }, {
              label: 'True',
              value: true
            }],
            getItemLabel: function(item) {
              return item.label;
            },
            getItemValue: function(item) {
              return item.value;
            },
            initial: false,
            onChange: function(rowIndex, newValue, api) {
              if (newValue) {
                var boxChanges = {
                  disabled: newValue === 'True'
                };
                if (newValue === 'True') {
                  api.updateField(rowIndex, 'boxPosition', {
                    value: null
                  });
                  boxChanges.value = null;
                }
                api.updateField(rowIndex, 'box', boxChanges);
              }
            }
          }, {
            title: 'Effective Group ID',
            type: 'read-only',
            data: 'effectiveGroupId',
            include: Constants.isDetailedSample
          }, {
            title: 'Group ID',
            type: 'text',
            data: 'groupId',
            include: Constants.isDetailedSample,
            maxLength: 100,
            regex: Utils.validation.alphanumRegex
          }, {
            title: 'Group Desc.',
            type: 'text',
            data: 'groupDescription',
            include: Constants.isDetailedSample,
            maxLength: 255
          }, {
            title: 'Design Code',
            type: 'dropdown',
            data: 'libraryDesignCodeId',
            include: Constants.isDetailedSample,
            source: Constants.libraryDesignCodes,
            getItemLabel: function(item) {
              return item.code;
            },
            getItemValue: Utils.array.getId,
            sortSource: Utils.sorting.standardSort('code'),
            required: true,
            onChange: function(rowIndex, newValue, api) {
              var designCode = Utils.array.findFirstOrNull(function(designCode) {
                return designCode.code === newValue;
              }, Constants.libraryDesignCodes);
              api.updateField(rowIndex, 'targetedSequencingId', {
                required: designCode ? designCode.targetedSequencingRequired : false
              });
            }
          }, {
            title: 'Size (bp)',
            type: 'int',
            data: 'dnaSize',
            min: 1,
            max: 10000000
          }, {
            title: 'Conc.',
            type: 'decimal',
            data: 'concentration',
            precision: 14,
            scale: 10,
            min: 0
          }, {
            title: 'Conc. Units',
            type: 'dropdown',
            data: 'concentrationUnits',
            source: Constants.concentrationUnits,
            getItemLabel: function(item) {
              return Utils.decodeHtmlString(item.units);
            },
            getItemValue: Utils.array.getName,
            required: true,
            initial: 'NANOGRAMS_PER_MICROLITRE',
            initializeOnEdit: true
          }, {
            title: 'Volume',
            type: 'decimal',
            data: 'volume',
            precision: 14,
            scale: 10
          }, {
            title: 'Vol. Units',
            type: 'dropdown',
            data: 'volumeUnits',
            source: Constants.volumeUnits,
            getItemLabel: function(item) {
              return Utils.decodeHtmlString(item.units);
            },
            getItemValue: Utils.array.getName,
            required: true,
            initial: 'MICROLITRES',
            initializeOnEdit: true
          }, {
            title: 'Parent ng Used',
            type: 'decimal',
            data: 'ngUsed',
            precision: 14,
            scale: 10,
            min: 0
          }, {
            title: 'Parent Vol. Used',
            type: 'decimal',
            data: 'volumeUsed',
            precision: 14,
            scale: 10,
            min: 0
          }, {
            title: 'Creation Date',
            type: 'date',
            data: 'creationDate',
            required: true,
            initial: Utils.getCurrentDate()
          }, {
            title: 'Targeted Sequencing',
            type: 'dropdown',
            data: 'targetedSequencingId',
            include: Constants.isDetailedSample,
            source: function(data, api) {
              if (!data.libraryKitDescriptorId) {
                return [];
              }
              return Constants.targetedSequencings.filter(function(tarseq) {
                return tarseq.kitDescriptorIds.indexOf(data.libraryKitDescriptorId) !== -1;
              });
            },
            sortSource: Utils.sorting.standardSort('alias'),
            getItemLabel: Utils.array.getAlias,
            getItemValue: Utils.array.getId
          }];
    },
    prepareData: function(data) {
      data.forEach(function(aliquot) {
        // prepare parent volumes for validation in confirmSave
        if (aliquot.parentVolume && aliquot.volumeUsed) {
          aliquot.parentVolume = Utils.decimalStrings.add(aliquot.parentVolume, aliquot.volumeUsed);
        }
      });
    },
    confirmSave: function(data) {
      var deferred = jQuery.Deferred();

      var overused = data.filter(function(aliquot) {
        return aliquot.volumeUsed && aliquot.parentVolume
            && Utils.decimalStrings.subtract(aliquot.parentVolume, aliquot.volumeUsed).startsWith('-');
      }).length;

      if (overused) {
        Utils.showConfirmDialog('Not Enough Library Volume', 'Save', ['Saving will cause ' + overused
            + (overused > 1 ? ' libraries to have negative volumes. ' : ' library to have a negative volume. ')
            + 'Are you sure you want to proceed?'], function() {
          deferred.resolve();
        }, function() {
          deferred.reject();
        });
      } else {
        deferred.resolve();
      }

      return deferred.promise();
    }

  };

  function getLabel(item) {
    return item.name + ' (' + item.alias + ')';
  }

  function cacheBox(cache, box, itemPos) {
    var cached = cache[box.alias];
    if (!cached) {
      box.emptyPositions = Utils.getEmptyBoxPositions(box);
      cache[box.alias] = box;
      cached = box;
    }
    if (itemPos && cached.emptyPositions.indexOf(itemPos) === -1) {
      cached.emptyPositions.push(itemPos);
      cached.emptyPositions.sort();
    }
  }

  function fillBoxPositions(api, sort) {
    var rowCount = api.getRowCount();
    var freeByAlias = [];
    var boxesByAlias = api.getCache('boxes');

    for (var row = 0; row < rowCount; row++) {
      var boxAlias = api.getValue(row, 'box');
      if (boxAlias) {
        freeByAlias[boxAlias] = freeByAlias[boxAlias] || boxesByAlias[boxAlias].emptyPositions.slice();
        var pos = api.getValue(row, 'boxPosition');
        if (pos) {
          freeByAlias[boxAlias] = freeByAlias[boxAlias].filter(function(freePos) {
            return freePos !== pos;
          });
        }
      }
    }
    for ( var key in freeByAlias) {
      freeByAlias[key].sort(sort);
    }
    var changes = [];
    for (var row = 0; row < rowCount; row++) {
      var boxAlias = api.getValue(row, 'box');
      if (boxAlias && !api.getValue(row, 'boxPosition')) {
        var free = freeByAlias[boxAlias];
        if (free && free.length > 0) {
          var pos = free.shift();
          changes.push([row, 'boxPosition', pos]);
          freeByAlias[boxAlias] = free.filter(function(freePos) {
            return freePos !== pos;
          });
        }
      }
    }
    if (changes.length) {
      api.updateData(changes);
    }
  }

})(jQuery);
