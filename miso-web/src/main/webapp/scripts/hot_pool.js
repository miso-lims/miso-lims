HotTarget.pool = (function() {

  var customPoolNotes = ['Switch between Pool and Library Aliquot views using the \'Choose Library Aliquots\'/\'Edit Pools\' buttons'];

  // used for custom pooling - table is rebuilt when switching between aliquot/pool views
  var aliquots = null;
  var pools = null;

  function destroyTable(table) {
    table.destroy();
    jQuery('#bulkactions').empty();
    jQuery('#save').off('click');
  }

  function switchToPoolsTable(table, config) {
    aliquots = table.getDtoData();
    destroyTable(table);
    HotUtils.makeTable(HotTarget.pool, true, pools, config);
  }

  function switchToAliquotsTable(table, config) {
    pools = table.getDtoData();
    if (!aliquots) {
      aliquots = config.aliquotsToPool;
    }
    destroyTable(table);
    HotUtils.makeTable(aliquotPoolTarget, false, aliquots, config);
  }

  var aliquotPoolTarget = {
    getNotes: function(config) {
      return customPoolNotes;
    },
    createColumns: function(config, create, data) {
      return [{
        header: 'Library Aliquot Name',
        data: 'name',
        readOnly: true,
        include: true,
        unpack: function(aliquot, flat, setCellMeta) {
          flat.name = Utils.valOrNull(aliquot.name);
        },
        pack: function(aliquot, flat, errorHandler) {
          aliquot.name = flat.name;
        }
      }, {
        header: 'Library Alias',
        data: 'libraryAlias',
        readOnly: true,
        include: true,
        unpack: function(aliquot, flat, setCellMeta) {
          flat.libraryAlias = aliquot.libraryAlias;
        },
        pack: function(aliquot, flat, errorHandler) {
        }
      }, {
        header: 'Library Size',
        data: 'librarySize',
        readOnly: true,
        include: true,
        unpack: function(aliquot, flat, setCellMeta) {
          flat.librarySize = aliquot.dnaSize;
        },
        pack: function(aliquot, flat, errorHandler) {
        }
      }, {
        header: 'Pool',
        data: 'pool',
        include: true,
        type: 'dropdown',
        trimDropdown: false,
        validator: HotUtils.validator.requiredAutocomplete,
        unpack: function(aliquot, flat, setCellMeta) {
          flat.pool = aliquot.pool;
        },
        pack: function(aliquot, flat, errorHandler) {
          aliquot.pool = flat.pool;
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
    getUserManualUrl: function() {
      return Urls.external.userManual('pools');
    },
    getNotes: function(config) {
      return config.aliquotsToPool ? customPoolNotes : null
    },
    getCreateUrl: function() {
      return Urls.rest.pools.create;
    },
    getUpdateUrl: function(id) {
      return Urls.rest.pools.update(id);
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
      }, HotUtils.makeColumnForDecimal('Concentration', true, 'concentration', 14, 10, false, false), {
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
      }, HotUtils.makeColumnForDecimal('Volume', true, 'volume', 14, 10, false, true), {
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
      }, HotUtils.makeColumnForBoolean('QC Passed?', true, 'qcPassed', false)];

      var spliceIndex = columns.indexOf(columns.filter(function(column) {
        return column.data === 'identificationBarcode';
      })[0]) + 1;
      columns.splice.apply(columns, [spliceIndex, 0].concat(HotTarget.boxable.makeBoxLocationColumns(config)));
      return columns;
    },

    getCustomActions: function(table, config) {
      var actions = HotTarget.boxable.getCustomActions(table);
      if (config.aliquotsToPool) {
        actions.unshift({
          buttonText: 'Choose Library Aliquots',
          eventHandler: function() {
            switchToAliquotsTable(table, config);
          }
        });
      }
      return actions;
    },

    getBulkActions: function(config) {
      return [
          {
            name: 'Edit',
            action: function(items) {
              window.location = Urls.ui.pools.bulkEdit + '?' + jQuery.param({
                ids: items.map(Utils.array.getId).join(',')
              });
            }
          },
          {
            name: "Create Orders",
            excludeOnOrders: true,
            action: function(pools) {
              window.location = Urls.ui.sequencingOrders.bulkCreate + '?' + jQuery.param({
                ids: pools.map(Utils.array.getId).join(',')
              });
            }
          },

          HotUtils.printAction('pool'),
          HotUtils.spreadsheetAction(Urls.rest.pools.spreadsheet, Constants.poolSpreadsheets, function(pools, spreadsheet) {
            var errors = [];
            return errors;
          }),
          HotUtils.spreadsheetAction(Urls.rest.pools.contentsSpreadsheet, Constants.libraryAliquotSpreadsheets, function(aliquots,
              spreadsheet) {
            var errors = [];
            return errors;
          }, 'Download Contents'),
          {
            name: "Create Samplesheet",
            action: function(pools) {
              var platformTypes = Utils.array.deduplicateString(pools.map(function(pool) {
                return pool.platformType;
              }));
              if (platformTypes.length > 1) {
                Utils.showOkDialog("Error", ["Cannot create a sample sheet from pools for different platforms."]);
                return;
              }
              if (platformTypes[0] != "ILLUMINA") {
                Utils.showOkDialog("Error", ["Can only create sample sheets for Illumina sequencers."]);
                return;
              }
              var instrumentModels = Constants.instrumentModels.filter(function(model) {
                return (model.instrumentType == "SEQUENCER" && model.platformType == platformTypes[0] && model.active);
              });
              if (instrumentModels.length == 0) {
                Utils.showOkDialog("Error", ["No instruments are available for these pools.", "Please add a sequencer first."]);
                return;
              }
              function showCreateDialog(modelId) {
                Utils.showDialog("Create Samplesheet", "Download", [{
                  property: "experimentType",
                  label: "Type",
                  required: true,
                  type: "select",
                  getLabel: function(type) {
                    return type.description;
                  },
                  values: Constants.illuminaExperimentTypes
                }, {
                  property: "sequencingParameters",
                  label: "Sequencing Parameters",
                  required: true,
                  type: "select",
                  getLabel: Utils.array.getName,
                  values: Constants.sequencingParameters.filter(function(param) {
                    return param.instrumentModelId == modelId;
                  })
                }, {
                  property: 'genomeFolder',
                  type: 'text',
                  label: 'Genome Folder',
                  value: Constants.genomeFolder,
                  required: true
                }, {
                  property: 'customRead1Primer',
                  type: 'text',
                  label: 'Custom Read 1 Primer Well',
                  required: false
                }, {
                  property: 'customIndexPrimer',
                  type: 'text',
                  label: 'Custom Index Primer Well',
                  required: false
                }, {
                  property: 'customRead2Primer',
                  type: 'text',
                  label: 'Custom Read 2 Primer Well',
                  required: false
                }, {
                  property: "pools",
                  label: "Lanes Configuration",
                  required: true,
                  type: "order",
                  getLabel: Utils.array.getAlias,
                  values: pools
                }], function(result) {
                  Utils.ajaxDownloadWithDialog(Urls.rest.pools.samplesheet, {
                    customRead1Primer: result.customRead1Primer,
                    customIndexPrimer: result.customIndexPrimer,
                    customRead2Primer: result.customRead2Primer,
                    experimentType: result.experimentType.name,
                    genomeFolder: result.genomeFolder,
                    sequencingParametersId: result.sequencingParameters.id,
                    poolIds: result.pools.map(Utils.array.getId)
                  });
                }, null);
              }
              Utils.showWizardDialog("Create Samplesheet", Constants.instrumentModels.filter(function(p) {
                return p.platformType === platformTypes[0] && p.instrumentType === 'SEQUENCER' && p.active;
              }).sort(Utils.sorting.standardSort('alias')).map(function(instrumentModel) {
                return {
                  name: instrumentModel.alias,
                  handler: function() {
                    showCreateDialog(instrumentModel.id);
                  }
                }
              }));
            }
          },
          HotUtils.makeParents(Urls.rest.pools.parents, HotUtils.relationCategoriesForDetailed().concat(
              [HotUtils.relations.library(), HotUtils.relations.libraryAliquot()]))].concat(BulkUtils.actions.qc('Pool')).concat(
          [HotUtils.makeTransferAction('poolIds')]);
    },

    confirmSave: function(flatObjects, isCreate, config, table) {
      var deferred = jQuery.Deferred();

      if (config.aliquotsToPool) {
        // ensure Pool aliases are unique, so we can sort the aliquots correctly
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

        // sort aliquots and ensure they're all added to pools
        pools.forEach(function(pool) {
          pool.pooledElements = [];
        });

        if (!aliquots) {
          aliquots = config.aliquotsToPool;
        }
        var aliquotPoolMissing = false;
        aliquots.forEach(function(aliquot) {
          if (!aliquot.pool) {
            aliquotPoolMissing = true;
            return deferred.promise();
          }
          var pool = Utils.array.findFirstOrNull(function(pool) {
            return pool.alias === aliquot.pool;
          }, pools);
          if (!pool) {
            aliquotPoolMissing = true;
          } else {
            pool.pooledElements.push(aliquot);
          }
        });
        if (aliquotPoolMissing) {
          Utils.showOkDialog('Error', ['All aliquots must be added to pools'], function() {
            switchToAliquotsTable(table, config);
            deferred.reject();
          });
          return deferred.promise();
        }

        // check that all pools have aliquots in them
        var empties = pools.filter(function(pool) {
          return !pool.pooledElements || !pool.pooledElements.length;
        });
        if (empties.length) {
          Utils.showOkDialog('Error', [empties.length > 1 ? 'There are empty pools' : 'Pool \'' + empties[0].alias + '\' is empty'],
              function() {
                switchToAliquotsTable(table, config);
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
    },

    headerWarnings: function(config, data) {
      if (data.length == 1 && data[0].mergeChild) {
        var mergeChildDto = data[0];
        if (mergeChildDto.duplicateIndices || mergeChildDto.nearDuplicateIndices) {
          return "Merged pool will contain duplicate or near duplicate indices. Functionality will be limited on save."
        }
      }
    }

  }
})();
