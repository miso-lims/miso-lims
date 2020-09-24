BulkTarget = window.BulkTarget || {};
BulkTarget.libraryaliquot = (function($) {

  /*
   * Expected config: {
   *   pageMode: string {propagate, edit}
   *   box: optional new box created to put items in
   * }
   */

  var originalEffectiveGroupIdsByRow = {};
  var parentVolumesByRow = {};

  return {
    getSaveUrl: function() {
      return Urls.rest.libraryAliquots.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.libraryAliquots.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('library_aliquots');
    },
    getCustomActions: function() {
      return BulkUtils.actions.boxable();
    },
    getBulkActions: function(config) {
      var editAction = BulkUtils.actions.edit(Urls.ui.libraryAliquots.bulkEdit);
      editAction.allowOnLibraryPage = true;

      return [
          editAction,
          {
            name: 'Propagate',
            action: function(items) {
              HotUtils.warnIfConsentRevoked(items, function() {
                BulkUtils.actions.showDialogForBoxCreation('Create Library Aliquots', 'Create', [],
                    Urls.ui.libraryAliquots.bulkRepropagate, function(result) {
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
      var columns = [{
        title: 'Parent Alias',
        type: 'text',
        data: 'parentAlias',
        disabled: true,
        getData: function(aliquot) {
          return aliquot.parentAliquotAlias || aliquot.libraryAlias;
        },
        include: config.pageMode === 'propagate',
        omit: true
      }, BulkUtils.columns.name, BulkUtils.columns.generatedAlias(config)];

      columns = columns.concat(BulkUtils.columns.boxable(config, api));
      columns = columns.concat(BulkUtils.columns.groupId(true, function(rowIndex) {
        return originalEffectiveGroupIdsByRow[rowIndex];
      }));

      columns.push({
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
      });

      columns = columns.concat(BulkUtils.columns.detailedQcStatus());
      columns.push(BulkUtils.columns.librarySize);
      columns = columns.concat(BulkUtils.columns.concentration());
      columns = columns.concat(BulkUtils.columns.volume(false, config));
      columns = columns.concat(BulkUtils.columns.parentUsed);

      columns.push(BulkUtils.columns.creationDate(true, true, 'library aliquot'), {
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
      });
      return columns;
    },
    prepareData: function(data) {
      data.forEach(function(aliquot, index) {
        originalEffectiveGroupIdsByRow[index] = aliquot.effectiveGroupId;
        // prepare parent volumes for validation in confirmSave
        if (aliquot.parentVolume !== undefined && aliquot.parentVolume !== null) {
          if (aliquot.volumeUsed) {
            parentVolumesByRow[index] = Utils.decimalStrings.add(aliquot.parentVolume, aliquot.volumeUsed);
          } else {
            parentVolumesByRow[index] = aliquot.parentVolume;
          }
        }
      });
    },
    confirmSave: function(data) {
      var deferred = jQuery.Deferred();

      var overused = data.filter(function(aliquot, index) {
        return aliquot.volumeUsed && parentVolumesByRow.hasOwnProperty(index)
            && Utils.decimalStrings.subtract(parentVolumesByRow[index], aliquot.volumeUsed).startsWith('-');
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

})(jQuery);
