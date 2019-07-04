HotTarget.libraryaliquot = {
  getCreateUrl: function() {
    return Urls.rest.libraryAliquots.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.libraryAliquots.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(lib, errorHandler) {
  },
  getFixedColumns: function(config) {
    return 2;
  },
  createColumns: function(config, create, data) {
    var columns = [
        {
          header: 'Library Aliquot Name',
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
        HotUtils.makeColumnForText('Library Aliquot Alias', true, 'alias', {
          validator: HotUtils.validator.requiredTextNoSpecialChars
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
        HotUtils.makeColumnForText('Matrix Barcode', !Constants.automaticBarcodes, 'identificationBarcode', {
          validator: HotUtils.validator.optionalTextNoSpecialChars
        }),
        {
          header: 'Effective Group ID',
          data: 'effectiveGroupId',
          include: Constants.isDetailedSample,
          type: 'text',
          readOnly: true,
          depends: 'groupId',
          update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
            if (flatProperty === 'groupId')
              setData(flat.groupId);
          },
          unpack: function(lib, flat, setCellMeta) {
            flat.effectiveGroupId = lib.effectiveGroupId ? lib.effectiveGroupId : '(None)';
          },
          pack: function(lib, flat, errorHandler) {
            // left blank as this will never be deserialized into the Library model
          }
        },
        HotUtils.makeColumnForText('Group ID', Constants.isDetailedSample, 'groupId', {
          validator: HotUtils.validator.optionalTextAlphanumeric
        }),
        HotUtils.makeColumnForText('Group Desc.', Constants.isDetailedSample, 'groupDescription', {}),
        HotUtils.makeColumnForConstantsList('Design Code', Constants.isDetailedSample, 'libraryDesignCode', 'libraryDesignCodeId', 'id',
            'code', Constants.libraryDesignCodes, true, {
              validator: HotUtils.validator.requiredAutocomplete
            }),
        HotUtils.makeColumnForFloat('Size (bp)', true, 'dnaSize', false),
        HotUtils.makeColumnForFloat('Conc.', true, 'concentration', false),
        {
          header: 'Conc. Units',
          data: 'concentrationUnits',
          type: 'dropdown',
          trimDropdown: false,
          source: Constants.concentrationUnits.map(function(unit) {
            return unit.units;
          }),
          include: true,
          allowHtml: true,
          validator: HotUtils.validator.requiredAutocomplete,
          unpack: function(obj, flat, setCellMeta) {
            var units = Constants.concentrationUnits.find(function(unit) {
              return unit.name == obj.concentrationUnits;
            });
            flat['concentrationUnits'] = !!units ? units.units : 'ng/&#181;L';
          },
          pack: function(obj, flat, errorHandler) {
            var units = Constants.concentrationUnits.find(function(unit) {
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
          source: Constants.volumeUnits.map(function(unit) {
            return unit.units;
          }),
          include: true,
          allowHtml: true,
          validator: HotUtils.validator.requiredAutocomplete,
          unpack: function(obj, flat, setCellMeta) {
            var units = Constants.volumeUnits.find(function(unit) {
              return unit.name == obj.volumeUnits;
            });
            flat['volumeUnits'] = !!units ? units.units : '&#181;L';
          },
          pack: function(obj, flat, errorHandler) {
            var units = Constants.volumeUnits.find(function(unit) {
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
            window.location = Urls.ui.libraryAliquots.bulkEdit + '?' + jQuery.param({
              ids: items.map(Utils.array.getId).join(',')
            });
          },
          allowOnLibraryPage: true
        },
        {
          name: 'Pool together',
          title: 'Create one pool from many library aliquots',
          action: function(items) {
            HotUtils.warnIfConsentRevoked(items, function() {
              var fields = [];
              HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, Urls.ui.libraryAliquots.bulkPoolTogether,
                  function(result) {
                    return {
                      ids: items.map(Utils.array.getId).join(',')
                    };
                  }, function(result) {
                    return 1;
                  });
            }, HotTarget.libraryaliquot.getLabel);
          },
          allowOnLibraryPage: false
        },
        {
          name: 'Pool separately',
          title: 'Create a pool for each library aliquot',
          action: function(items) {
            HotUtils.warnIfConsentRevoked(items, function() {
              var fields = [];
              HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, Urls.ui.libraryAliquots.bulkPoolSeparate,
                  function(result) {
                    return {
                      ids: items.map(Utils.array.getId).join(',')
                    };
                  }, function(result) {
                    return items.length;
                  });
            }, HotTarget.libraryaliquot.getLabel);
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
              HotUtils.showDialogForBoxCreation('Create Pools', 'Create', fields, Urls.ui.libraryAliquots.bulkPoolCustom, function(result) {
                console.log(result);
                return {
                  ids: items.map(Utils.array.getId).join(','),
                  quantity: result.quantity
                };
              }, function(result) {
                return result.quantity;
              })
            }, HotTarget.libraryaliquot.getLabel);
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
            : HotUtils.makeAddToWorkset('library aliquots', 'libraryAliquotIds', Urls.rest.worksets.addLibraryAliquots)];
  },

  confirmSave: function(flatObjects, isCreate, config, table) {
    var deferred = jQuery.Deferred();

    var aliquots = table.getDtoData();

    var seen = {};
    var libraries = aliquots.filter(function(aliquot) {
      return !(Utils.validation.isEmpty(aliquot.volumeUsed) || aliquot.volumeUsed <= 0);
    }).map(function(aliquot) {
      return aliquot.library;
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

    aliquots.filter(function(aliquot) {
      return aliquot.library != null && aliquot.library.volume != null && aliquot.volumeUsed != null;
    }).forEach(function(aliquot) {
      libraries.find(function(library) {
        return library.id == aliquot.library.id;
      }).volume -= aliquot.volumeUsed;
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
