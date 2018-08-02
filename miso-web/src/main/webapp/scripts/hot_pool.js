HotTarget.pool = (function() {

  var customPoolNotes = ['Switch between Pool and Dilution views using the \'Choose Dilutions\'/\'Edit Pools\' buttons'];

  // used for custom pooling - table is rebuilt when switching between dilution/pool views
  var dilutions = null;
  var pools = null;

  function destroyTable(table) {
    table.destroy();
    jQuery('#bulkactions').empty();
    jQuery('#save').off('click');
  }

  function switchToPoolsTable(table, config) {
    dilutions = table.getDtoData();
    destroyTable(table);
    HotUtils.makeTable(HotTarget.pool, true, pools, config);
  }

  function switchToDilutionsTable(table, config) {
    pools = table.getDtoData();
    if (!dilutions) {
      dilutions = config.dilutionsToPool;
    }
    destroyTable(table);
    HotUtils.makeTable(dilutionPoolTarget, false, dilutions, config);
  }

  var dilutionPoolTarget = {
    getNotes: function(config) {
      return customPoolNotes;
    },
    createColumns: function(config, create, data) {
      return [{
        header: 'Dilution Name',
        data: 'name',
        readOnly: true,
        include: true,
        unpack: function(dil, flat, setCellMeta) {
          flat.name = Utils.valOrNull(dil.name);
        },
        pack: function(dil, flat, errorHandler) {
          dil.name = flat.name;
        }
      }, {
        header: 'Library Alias',
        data: 'libraryAlias',
        readOnly: true,
        include: true,
        unpack: function(dil, flat, setCellMeta) {
          flat.libraryAlias = dil.library.alias;
        },
        pack: function(dil, flat, errorHandler) {
        }
      }, {
        header: 'Library Size',
        data: 'librarySize',
        readOnly: true,
        include: true,
        unpack: function(dil, flat, setCellMeta) {
          flat.librarySize = dil.library.dnaSize;
        },
        pack: function(dil, flat, errorHandler) {
        }
      }, {
        header: 'Pool',
        data: 'pool',
        include: true,
        type: 'dropdown',
        trimDropDown: false,
        validator: HotUtils.validator.requiredAutocomplete,
        unpack: function(dil, flat, setCellMeta) {
          flat.pool = dil.pool;
        },
        pack: function(dil, flat, errorHandler) {
          dil.pool = flat.pool;
        },
        source: pools ? pools.filter(function(pool) {
          return pool.alias;
        }).map(function(pool) {
          return pool.alias;
        }) : []
      }];
    },
    getCustomActions: function(table, config) {
      return [{
        buttonText: 'Edit Pools',
        eventHandler: function() {
          switchToPoolsTable(table, config);
        }
      }];
    },
    customSave: function(table, config) {
      switchToPoolsTable(table, config);
      jQuery('#save').click();
    }
  };

  return {
    getNotes: function(config) {
      return config.dilutionsToPool ? customPoolNotes : null
    },
    createUrl: '/miso/rest/pool',
    updateUrl: '/miso/rest/pool/',
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: function(lib, errorHandler) {
    },
    createColumns: function(config, create, data) {
      var columns = [{
        header: 'Pool Name',
        data: 'name',
        readOnly: true,
        include: true,
        unpackAfterSave: true,
        unpack: function(pool, flat, setCellMeta) {
          flat.name = pool.name;
        },
        pack: function(pool, flat, errorHandler) {
        }
      }, {
        header: 'Pool Alias',
        data: 'alias',
        include: true,
        validator: HotUtils.validator.requiredText,
        unpackAfterSave: true,
        unpack: function(pool, flat, setCellMeta) {
          flat.alias = pool.alias || null;
        },
        pack: function(pool, flat, errorHandler) {
          pool.alias = flat.alias;
        }
      }, {
        header: 'Description',
        data: 'description',
        include: true,
        unpack: function(pool, flat, setCellMeta) {
          flat.description = pool.description || null;
        },
        pack: function(pool, flat, errorHandler) {
          pool.description = flat.description;
        }
      }, {
        header: 'Matrix Barcode',
        data: 'identificationBarcode',
        validator: HotUtils.validator.optionalTextNoSpecialChars,
        include: !Constants.automaticBarcodes,
        unpack: function(pool, flat, setCellMeta) {
          flat.identificationBarcode = pool.identificationBarcode || null;
        },
        pack: function(pool, flat, errorHandler) {
          pool.identificationBarcode = flat.identificationBarcode;
        }
      }, {
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
        unpack: function(pool, flat, setCellMeta) {
          if (!pool.creationDate && create) {
            flat.creationDate = Utils.getCurrentDate();
          } else {
            flat.creationDate = Utils.valOrNull(pool.creationDate);
          }
        },
        pack: function(pool, flat, errorHandler) {
          pool.creationDate = flat.creationDate;
        }
      }, HotUtils.makeColumnForFloat('Concentration', true, 'concentration', false),
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
      HotUtils.makeColumnForBoolean('QC Passed?', true, 'qcPassed', false)];

      var spliceIndex = columns.indexOf(columns.filter(function(column) {
        return column.data === 'identificationBarcode';
      })[0]) + 1;
      columns.splice.apply(columns, [spliceIndex, 0].concat(HotTarget.boxable.makeBoxLocationColumns(config)));
      return columns;
    },

    getCustomActions: function(table, config) {
      var actions = HotTarget.boxable.getCustomActions(table);
      if (config.dilutionsToPool) {
        actions.unshift({
          buttonText: 'Choose Dilutions',
          eventHandler: function() {
            switchToDilutionsTable(table, config);
          }
        });
      }
      return actions;
    },

    getBulkActions: function(config) {
      return [{
        name: 'Edit',
        action: function(items) {
          window.location = window.location.origin + '/miso/pool/bulk/edit?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }
      }, {
        name: "Create Orders",
        excludeOnOrders: true,
        action: function(pools) {
          window.location = window.location.origin + '/miso/order/bulk/create?' + jQuery.param({
            ids: pools.map(Utils.array.getId).join(',')
          });
        }
      },

      HotUtils.printAction('pool'), HotUtils.spreadsheetAction('/miso/rest/pool/spreadsheet', Constants.poolSpreadsheets, 
          function(pools, spreadsheet){
        var errors = [];
        return errors;
      }),
      
      HotUtils.makeParents('pool', HotUtils.relationCategoriesForDetailed().concat([HotUtils.relations.library(), HotUtils.relations.dilution()]))
      
      ].concat(HotUtils.makeQcActions("Pool"));
    },

    confirmSave: function(flatObjects, isCreate, config, table) {
      var deferred = jQuery.Deferred();

      if (config.dilutionsToPool) {
        // ensure Pool aliases are unique, so we can sort the dilutions correctly
        pools = table.getDtoData();

        var duplicateAliases = false;
        for (var i = 0; i < pools.length; i++) {
          for (var j = i + 1; j < pools.length; j++) {
            if (pools[i].alias === pools[j].alias) {
              duplicateAliases = true;
            }
          }
        }
        if (duplicateAliases) {
          Utils.showOkDialog('Error', ['There are duplicate Pool aliases'], function() {
            deferred.reject();
          });
          return deferred.promise();
        }

        // sort dilutions and ensure they're all added to pools
        pools.forEach(function(pool) {
          pool.pooledElements = [];
        });

        if (!dilutions) {
          dilutions = config.dilutionsToPool;
        }
        var dilutionPoolMissing = false;
        dilutions.forEach(function(dilution) {
          if (!dilution.pool) {
            dilutionPoolMissing = true;
            return deferred.promise();
          }
          var pool = Utils.array.findFirstOrNull(function(pool) {
            return pool.alias === dilution.pool;
          }, pools);
          if (!pool) {
            dilutionPoolMissing = true;
          } else {
            pool.pooledElements.push(dilution);
          }
        });
        if (dilutionPoolMissing) {
          Utils.showOkDialog('Error', ['All dilutions must be added to pools'], function() {
            switchToDilutionsTable(table, config);
            deferred.reject();
          });
          return deferred.promise();
        }

        // check that all pools have dilutions in them
        var empties = pools.filter(function(pool) {
          return !pool.pooledElements || !pool.pooledElements.length;
        });
        if (empties.length) {
          Utils.showOkDialog('Error', [empties.length > 1 ? 'There are empty pools' : 'Pool \'' + empties[0].alias + '\' is empty'],
              function() {
                switchToDilutionsTable(table, config);
                deferred.reject();
              });
          return deferred.promise();
        }
      }

      var missingBarcodesCount = flatObjects.filter(function(item) {
        return !item.identificationBarcode;
      }).length;
      if (!isCreate || Constants.automaticBarcodes || !missingBarcodesCount) {
        deferred.resolve();
      } else {
        Utils.showConfirmDialog('Missing Barcodes', 'Save', ['Pools should usually have barcodes. Are you sure you wish to save '
            + missingBarcodesCount + (missingBarcodesCount > 1 ? ' pools without barcodes' : ' pool without one') + '?'], function() {
          deferred.resolve();
        }, function() {
          deferred.reject();
        });
      }
      return deferred.promise();
    }

  }
})();
