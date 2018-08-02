HotTarget.dilution = {
  createUrl: '/miso/rest/librarydilution',
  updateUrl: '/miso/rest/librarydilution/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(lib, errorHandler) {
  },
  createColumns: function(config, create, data) {
    var columns = [
        {
          header: 'Dilution Name',
          data: 'name',
          readOnly: true,
          include: true,
          validator: HotUtils.validator.optionalTextNoSpecialChars,
          unpackAfterSave: true,
          unpack: function(dil, flat, setCellMeta) {
            flat.name = Utils.valOrNull(dil.name);
          },
          pack: function(dil, flat, errorHandler) {
            dil.name = flat.name;
          }

        },
        HotUtils.makeColumnForText('Matrix Barcode', !Constants.automaticBarcodes, 'identificationBarcode', {
          validator: HotUtils.validator.optionalTextNoSpecialChars
        }),
        {
          header: 'Library Alias',
          data: 'libraryAlias',
          readOnly: true,
          include: true,
          unpack: function(dil, flat, setCellMeta) {
            flat.libraryAlias = dil.library.alias;
          },
          pack: function(dil, flat, errorHandler) {
          }
        },
        HotUtils.makeColumnForFloat('Conc.', true, 'concentration', false),
        {
          header: 'Conc. Units',
          data: 'concentrationUnits',
          type: 'dropdown',
          trimDropdown: false,
          source: ['(None)'].concat(Constants.concentrationUnits.map(function(unit){
            return unit.units;
          })),
          include: true,
          allowHtml: true,
          validator: Handsontable.validators.AutocompleteValidator,
          unpack: function(obj, flat, setCellMeta) {
            var units = Constants.concentrationUnits.find(function(unit){
              return unit.name == obj.concentrationUnits;
            });
            flat['concentrationUnits'] = !!units ? units.units : '(None)';
          },
          pack: function(obj, flat, errorHandler) {
            var units = Constants.concentrationUnits.find(function(unit){
              return unit.units == flat['concentrationUnits'];
            });
            obj['concentrationUnits'] = !!units ? units.name : null;
          }
        },
        HotUtils.makeColumnForFloat('Volume', true, 'volume', false),
        {
          header: 'Vol. Units',
          data: 'volumeUnits',
          type: 'dropdown',
          trimDropdown: false,
          source: ['(None)'].concat(Constants.volumeUnits.map(function(unit){
            return unit.units;
          })),
          include: true,
          allowHtml: true,
          validator: Handsontable.validators.AutocompleteValidator,
          unpack: function(obj, flat, setCellMeta) {
            var units = Constants.volumeUnits.find(function(unit){
              return unit.name == obj.volumeUnits;
            });
            flat['volumeUnits'] = !!units ? units.units : '(None)';
          },
          pack: function(obj, flat, errorHandler) {
            var units = Constants.volumeUnits.find(function(unit){
              return unit.units == flat['volumeUnits'];
            });
            obj['volumeUnits'] = !!units ? units.name : null;
          }
        },
        HotUtils.makeColumnForFloat('ng Lib. Used', true, 'ngUsed', false),
        HotUtils.makeColumnForFloat('Vol. Lib. Used', true, 'volumeUsed', false),
        {
          header: 'Creation Date',
          data: 'creationDate',
          type: 'date',
          dateFormat: 'YYYY-MM-DD',
          datePickerConfig: {
            firstDay: 0,
            numberOfMonths: 1
          },
          allowEmpty: false,
          validator: HotUtils.validator.requiredText,
          include: true,
          unpack: function(dil, flat, setCellMeta) {
            if (!dil.creationDate && create) {
              flat.creationDate = Utils.getCurrentDate();
            } else {
              flat.creationDate = Utils.valOrNull(dil.creationDate);
            }
          },
          pack: function(dil, flat, errorHandler) {
            dil.creationDate = flat.creationDate;
          }
        },
        {
          header: 'Targeted Sequencing',
          data: 'targetedSequencingAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: [],
          include: Constants.isDetailedSample,
          unpack: function(dil, flat, setCellMeta) {
            var missingValueString;
            // whether targeted sequencing is required depends on library's design code
            var designCode = Utils.array.findFirstOrNull(function(code) {
              return dil.library.libraryDesignCodeId == code.id;
            }, Constants.libraryDesignCodes);
            if (Utils.array.maybeGetProperty(designCode, 'targetedSequencingRequired')) {
              setCellMeta('validator', HotUtils.validator.requiredAutocomplete);
              missingValueString = '';
            } else {
              setCellMeta('validator', HotUtils.validator.permitEmptyDropdown);
              missingValueString = '(None)';
            }

            flat.targetedSequencingAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                .idPredicate(dil.targetedSequencingId), Constants.targetedSequencings), 'alias')
                || missingValueString;

          },
          pack: function(dil, flat, errorHandler) {
            dil.targetedSequencingId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(tarSeq) {
              return flat.targetedSequencingAlias == tarSeq.alias && tarSeq.kitDescriptorIds.indexOf(dil.library.kitDescriptorId) != -1;
            }, Constants.targetedSequencings), 'id');
          },
          depends: '*start', // This is a dummy value that gets this run on
          // table creation only
          update: function(dil, flat, flatProperty, value, setReadOnly, setOptions, setData) {
            var baseOptionList;
            var designCode = Utils.array.findFirstOrNull(function(code) {
              return dil.library.libraryDesignCodeId == code.id;
            }, Constants.libraryDesignCodes);
            if (Utils.array.maybeGetProperty(designCode, 'targetedSequencingRequired')) {
              baseOptionList = [];
            } else {
              baseOptionList = ['(None)'];
            }
            setOptions({
              source: baseOptionList.concat(Constants.targetedSequencings.filter(function(targetedSequencing) {
                return targetedSequencing.kitDescriptorIds.indexOf(dil.library.kitDescriptorId) != -1;
              }).map(Utils.array.getAlias).sort())
            });
          }
        }];

    var spliceIndex = columns.indexOf(columns.filter(function(column) {
      return column.data === 'identificationBarcode';
    })[0]) + 1;
    columns.splice.apply(columns, [spliceIndex, 0].concat(HotTarget.boxable.makeBoxLocationColumns(config)));
    return columns;
  },

  getCustomActions: function(table) {
    return HotTarget.boxable.getCustomActions(table);
  },

  getLabel: function(item) {
    return item.name + ' (' + item.library.alias + ')';
  },

  getBulkActions: function(config) {
    return [
        {
          name: 'Edit',
          action: function(items) {
            window.location = window.location.origin + '/miso/library/dilution/bulk/edit?' + jQuery.param({
              ids: items.map(Utils.array.getId).join(',')
            });
          },
          allowOnLibraryPage: true
        },
        {
          name: 'Pool together',
          title: 'Create one pool from many dilutions',
          action: function(items) {
            HotUtils.warnIfConsentRevoked(items, function() {
              var fields = [];
              HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, '/miso/library/dilution/bulk/merge?', function(result) {
                return {
                  ids: items.map(Utils.array.getId).join(',')
                };
              }, function(result) {
                return 1;
              });
            }, HotTarget.dilution.getLabel);
          },
          allowOnLibraryPage: false
        },
        {
          name: 'Pool separately',
          title: 'Create a pool for each dilution',
          action: function(items) {
            HotUtils.warnIfConsentRevoked(items, function() {
              var fields = [];
              HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, '/miso/library/dilution/bulk/propagate?',
                  function(result) {
                    return {
                      ids: items.map(Utils.array.getId).join(',')
                    };
                  }, function(result) {
                    return items.length;
                  });
            }, HotTarget.dilution.getLabel);
          },
          allowOnLibraryPage: true
        },
        {
          name: 'Pool custom',
          title: 'Divide dilutions into several pools',
          action: function(items) {
            HotUtils.warnIfConsentRevoked(items, function() {
              var fields = [{
                label: 'Quantity',
                property: 'quantity',
                type: 'int',
              }];
              HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, '/miso/library/dilution/bulk/pool?', function(result) {
                console.log(result);
                return {
                  ids: items.map(Utils.array.getId).join(','),
                  quantity: result.quantity
                };
              }, function(result) {
                return result.quantity;
              })
            }, HotTarget.dilution.getLabel);
          },
          allowOnLibraryPage: true
        },
        HotUtils.printAction('dilution'),
        HotUtils.spreadsheetAction('/miso/rest/librarydilution/spreadsheet', Constants.libraryDilutionSpreadsheets, function(dilutions,
            spreadsheet) {
          var errors = [];
          return errors;
        }),

        HotUtils.makeParents('librarydilution', HotUtils.relationCategoriesForDetailed().concat([HotUtils.relations.library()])),
        HotUtils.makeChildren('librarydilution', [HotUtils.relations.pool()]),
        config.worksetId ? HotUtils.makeRemoveFromWorkset('dilutions', config.worksetId) : HotUtils.makeAddToWorkset('dilutions',
            'dilutionIds')];
  },

  confirmSave: function(flatObjects, isCreate, config, table) {
    var deferred = jQuery.Deferred();

    var dilutions = table.getDtoData();

    var seen = {};
    var libraries = dilutions.filter(function(dilution) {
      return !(Utils.validation.isEmpty(dilution.volumeUsed) || dilution.volumeUsed <= 0);
    }).map(function(dilution) {
      return dilution.library;
    }).filter(function(library) {
      return library != null;
    }).filter(function(library) {
      return seen.hasOwnProperty(library.id) ? false : (seen[library.id] = true);
    }).map(function(library) {
      return jQuery.extend(true, {}, library);
    });

    if (libraries.length == 0) {
      deferred.resolve();
      return deferred.promise();
    }

    dilutions.filter(function(dilution) {
      return dilution.library != null && dilution.library.volume != null && dilution.volumeUsed != null;
    }).forEach(function(dilution) {
      libraries.find(function(library) {
        return library.id == dilution.library.id;
      }).volume -= dilution.volumeUsed;
    });

    var overUsedCount = libraries.filter(function(library) {
      return library.volume < 0;
    }).length

    if (overUsedCount) {
      Utils.showConfirmDialog('Not Enough Library Volume', 'Save', ['Saving will cause ' + overUsedCount
          + (overUsedCount > 1 ? ' libraries to have negative volumes. ' : ' library to have a negative volume. ')
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
