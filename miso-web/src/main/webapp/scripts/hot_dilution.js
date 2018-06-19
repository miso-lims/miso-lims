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
        HotUtils.makeColumnForFloat('Conc. (' + Constants.libraryDilutionConcentrationUnits + ')', true, 'concentration', true),
        HotUtils.makeColumnForFloat('Volume', true, 'volume', false),
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
    columns.splice.apply(columns, [spliceIndex, 0].concat(HotTarget.boxable.makeBoxLocationColumns()));
    return columns;
  },

  getCustomActions: function(table) {
    return HotTarget.boxable.getCustomActions(table);
  },

  getLabel: function(item) {
    return item.name + ' (' + item.library.alias + ')';
  },

  getBulkActions: function(config) {
    return [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/library/dilution/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      },
      allowOnLibraryPage: true
    }, {
      name: 'Pool together',
      title: 'Create one pool from many dilutions',
      action: function(items) {
        HotUtils.warnIfConsentRevoked(items, function() {
          window.location = window.location.origin + '/miso/library/dilution/bulk/merge?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }, HotTarget.dilution.getLabel);
      },
      allowOnLibraryPage: false
    }, {
      name: 'Pool separately',
      title: 'Create a pool for each dilution',
      action: function(items) {
        HotUtils.warnIfConsentRevoked(items, function() {
          window.location = window.location.origin + '/miso/library/dilution/bulk/propagate?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }, HotTarget.dilution.getLabel);
      },
      allowOnLibraryPage: true
    }, {
      name: 'Pool custom',
      title: 'Divide dilutions into several pools',
      action: function(items) {
        HotUtils.warnIfConsentRevoked(items, function() {
          Utils.showDialog("Create Pools", "Create", [{
            label: 'Quantity',
            property: 'quantity',
            type: 'int'
          }], function(data) {
            window.location = window.location.origin + '/miso/library/dilution/bulk/pool?' + jQuery.param({
              ids: items.map(Utils.array.getId).join(','),
              quantity: data.quantity
            });
          });
        }, HotTarget.dilution.getLabel);
      },
      allowOnLibraryPage: true
    }, HotUtils.printAction('dilution'), HotUtils.spreadsheetAction('/miso/rest/librarydilution/spreadsheet', Constants.libraryDilutionSpreadsheets),

    HotUtils.makeParents('librarydilution', HotUtils.relationCategoriesForDetailed().concat([HotUtils.relations.library()])), 
    HotUtils.makeChildren('librarydilution',[HotUtils.relations.pool()])];
  }

};
