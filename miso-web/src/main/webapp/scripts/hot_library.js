/**
 * Library-specific Handsontable code
 */

HotTarget.library = (function() {
  var getPlatformType = function(value) {
    return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(platformType) {
      return platformType.key == value;
    }, Constants.platformTypes), 'name');
  };

  var makeIndexColumn = function(n) {
    return {
      header: 'Index ' + n,
      data: 'index' + n + 'Label',
      type: 'autocomplete',
      strict: true,
      filter: false,
      allowInvalid: true,
      trimDropdown: false,
      source: [''],
      include: true,
      unpack: function(lib, flat, setCellMeta) {
        var id = lib['index' + n + 'Id'];
        flat['index' + n + 'Label'] = id ? Constants.indexFamilies.reduce(function(acc, family) {
          return family.indices.reduce(function(acc, index) {
            return id == index.id ? index.label : acc;
          }, acc);
        }, null) : 'No index';
      },
      pack: function(lib, flat, errorHandler) {
        var label = flat['index' + n + 'Label'];
        var families = Constants.indexFamilies.filter(function(family) {
          return family.name == flat.indexFamilyName;
        });
        lib['index' + n + 'Id'] = families.length == 0 ? null : Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(index) {
          return index.label == label;
        }, families[0].indices), 'id');
      },
      depends: 'indexFamilyName',
      update: function(lib, flat, value, setReadOnly, setOptions, setData) {
        var pt = getPlatformType(flat.platformType);
        var indices = (Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(family) {
          return family.name == value && family.platformType == pt;
        }, Constants.indexFamilies), 'indices') || []).filter(function(index) {
          return index.position == n;
        }).map(function(index) {
          return index.label;
        }).sort();
        var data;
        if (indices.length == 0 || n > 1) {
          indices.unshift('No index');
          data = 'No index';
        } else {
          data = '';
        }
        setOptions({
          'source': indices
        });
        setData(data);
      }
    };
  };

  var makeDesignUpdate = function(id, nameProperty, defaultValue, source) {
    return function(lib, flat, value, setReadOnly, setOptions, setCell) {
      var design = Utils.array.findFirstOrNull(Utils.array.namePredicate(value), Constants.libraryDesigns);
      if (design) {
        setCell(Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(design[id]), source), nameProperty)
            || defaultValue);
      }
      setReadOnly(!!design);
    };
  };

  return {
    createUrl: '/miso/rest/library',
    updateUrl: '/miso/rest/library/',
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: function(lib, errorHandler) {
      lib.paired = Constants.libraryTypes.reduce(function(acc, type) {
        return acc || type.id == lib.libraryTypeId && type.alias.indexOf('Pair') != -1;
      }, lib.paired);
      // if any members are null, fill them with empty objects otherwise
      // things go poorly
      if (!lib.kitDescriptorId) {
        lib.kitDescriptorId = '';
        lib.kitDescriptorName = '';
      }
    },

    createColumns: function(config, create, data) {
      var validationCache = {};
      return [
          {
            header: 'Library Name',
            data: 'name',
            readOnly: true,
            include: true,
            unpackAfterSave: true,
            unpack: function(lib, flat, setCellMeta) {
              flat.name = lib.name;
            },
            pack: function(lib, flat, errorHandler) {
            }
          },
          {
            header: 'Library Alias',
            data: 'alias',
            validator: function(value, callback) {
              (Constants.automaticLibraryAlias ? HotUtils.validator.optionalTextNoSpecialChars
                  : HotUtils.validator.requiredTextNoSpecialChars)(value, function(result) {
                if (!result) {
                  callback(false);
                  return;
                }
                if (!value) {
                  return callback(Constants.automaticLibraryAlias);
                }
                if (validationCache.hasOwnProperty(value)) {
                  return callback(validationCache[value]);
                }
                Fluxion.doAjax('libraryControllerHelperService', 'validateLibraryAlias', {
                  'alias': value,
                  'url': ajaxurl
                }, {
                  'doOnSuccess': function() {
                    validationCache[value] = true;
                    return callback(true);
                  },
                  'doOnError': function(json) {
                    validationCache[value] = false;
                    return callback(false);
                  }
                });
              });
            },
            type: 'text',
            include: config.showLibraryAlias,
            unpackAfterSave: true,
            unpack: function(lib, flat, setCellMeta) {
              validationCache[lib.alias] = true;
              flat.alias = lib.alias;
              if (lib.nonStandardAlias) {
                HotUtils.makeCellNSAlias(setCellMeta);
              }
            },
            pack: function(lib, flat, errorHandler) {
              lib.alias = flat.alias;
            }
          },
          {
            header: 'Sample Alias',
            data: 'parentSampleAlias',
            readOnly: true,
            include: !config.isLibraryReceipt,
            unpack: function(lib, flat, setCellMeta) {
              flat.parentSampleAlias = lib.parentSampleAlias;
            },
            pack: function(lib, flat, errorHandler) {
            }
          },
          {
            header: 'Sample Location',
            data: 'sampleBoxPositionLabel',
            type: 'text',
            readOnly: true,
            include: config.sortableLocation && !config.isLibraryReceipt,
            unpack: function(sam, flat, setCellMeta) {
              flat.sampleBoxPositionLabel = sam.sampleBoxPositionLabel;
            },
            pack: function(sam, flat, errorHandler) {
              sam.sampleBoxPositionLabel = flat.sampleBoxPositionLabel;
            },
            customSorting: [{
              buttonText: 'Sort by Sample Location (rows)',
              sortTarget: 'rows',
              sortFunc: HotUtils.sorting.rowSort
            }, {
              buttonText: 'Sort by Sample Location (columns)',
              sortTarget: 'columns',
              sortFunc: HotUtils.sorting.colSort
            }],
            sortIndicator: true
          },
          HotUtils.makeColumnForText('Matrix Barcode', !Constants.automaticBarcodes, 'identificationBarcode', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),
          HotUtils.makeColumnForText('Description', config.showDescription, 'description', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),
          {
            header: 'Date of receipt',
            data: 'receivedDate',
            type: 'date',
            dateFormat: 'YYYY-MM-DD',
            datePickerConfig: {
              firstDay: 0,
              numberOfMonths: 1
            },
            allowEmpty: true,
            include: config.isLibraryReceipt || !create,
            unpack: function(lib, flat, setCellMeta) {
              flat.receivedDate = lib.receivedDate || null;
            },
            pack: function(lib, flat, errorHandler) {
              lib.receivedDate = flat.receivedDate;
            }
          },
          HotUtils.makeColumnForText('Group ID', Constants.isDetailedSample, 'groupId', {
            validator: HotUtils.validator.optionalTextAlphanumeric
          }),
          HotUtils.makeColumnForText('Group Desc.', Constants.isDetailedSample, 'groupDescription', {}),
          {
            header: 'Design',
            data: 'libraryDesignAlias',
            type: 'dropdown',
            trimDropdown: false,
            validator: Handsontable.AutocompleteValidator,
            source: [''],
            include: Constants.isDetailedSample,
            unpack: function(lib, flat, setCellMeta) {
              flat.libraryDesignAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                  .idPredicate(lib.libraryDesignId), Constants.libraryDesigns), 'name')
                  || '(None)';
            },
            pack: function(lib, flat, errorHandler) {
              if (flat.libraryDesignAlias == '(None)') {
                lib.libraryDesignId = null;
              } else {
                lib.libraryDesignId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(design) {
                  return design.name == flat.libraryDesignAlias && design.sampleClassId == lib.parentSampleClassId;
                }, Constants.libraryDesigns), 'id');
                if (!lib.libraryDesignId) {
                  errorHandler('Invalid library design: ' + flat.libraryDesignAlias);
                }
              }
            },
            depends: '*start', // This is a dummy value that gets this run on
            // creation only
            update: function(lib, flat, value, setReadOnly, setOptions, setData) {
              setOptions({
                'source': ['(None)'].concat(Constants.libraryDesigns.filter(function(design) {
                  return design.sampleClassId == lib.parentSampleClassId;
                }).map(Utils.array.getName).sort())
              });
            }
          },
          HotUtils.makeColumnForConstantsList('Code', Constants.isDetailedSample, 'libraryDesignCode', 'libraryDesignCodeId', 'id', 'code',
              Constants.libraryDesignCodes, true, {
                depends: 'libraryDesignAlias',
                update: makeDesignUpdate('designCodeId', 'code', 'WG', Constants.libraryDesignCodes),
                validator: HotUtils.validator.requiredAutocomplete
              }),
          {
            header: 'Platform',
            data: 'platformType',
            type: 'dropdown',
            readOnly: !create,
            trimDropdown: false,
            source: Constants.platformTypes.filter(function(pt) {
              return pt.active || data.reduce(function(acc, lib) {
                return acc || pt.key == lib.platformType;
              }, false);
            }).map(function(pt) {
              return pt.key;
            }),
            validator: HotUtils.validator.requiredAutocomplete,
            include: true,
            unpack: function(lib, flat, setCellMeta) {
              flat.platformType = lib.platformType;
            },
            pack: function(lib, flat, errorHandler) {
              lib.platformType = flat.platformType;
            }
          },
          {
            'header': 'Type',
            'data': 'libraryTypeAlias',
            'type': 'dropdown',
            'trimDropdown': false,
            'source': [''],
            'validator': HotUtils.validator.requiredAutocomplete,
            'include': true,
            'depends': 'platformType',
            'unpack': function(lib, flat, setCellMeta) {
              flat.libraryTypeAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(lib.libraryTypeId),
                  Constants.libraryTypes), 'alias');
            },
            'pack': function(lib, flat, errorHander) {
              lib.libraryTypeId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                  .aliasPredicate(flat.libraryTypeAlias), Constants.libraryTypes), 'id');
            },
            update: function(lib, flat, value, setReadOnly, setOptions) {
              var pt = getPlatformType(value);
              setOptions({
                'source': Constants.libraryTypes.filter(function(lt) {
                  return lt.platform == pt && (!lt.archived || lib.libraryTypeId == lt.id);
                }).map(function(lt) {
                  return lt.alias;
                }).sort()
              });
            }

          },
          HotUtils.makeColumnForConstantsList('Selection', true, 'librarySelectionTypeAlias', 'librarySelectionTypeId', 'id', 'name',
              Constants.librarySelections, true, {
                depends: 'libraryDesignAlias',
                update: makeDesignUpdate('selectionId', 'name', '(None)', Constants.librarySelections)

              }),
          HotUtils.makeColumnForConstantsList('Strategy', true, 'libraryStrategyTypeAlias', 'libraryStrategyTypeId', 'id', 'name',
              Constants.libraryStrategies, true, {
                depends: 'libraryDesignAlias',
                update: makeDesignUpdate('strategyId', 'name', '(None)', Constants.libraryStrategies)

              }),
          {
            header: 'Index Kit',
            data: 'indexFamilyName',
            type: 'dropdown',
            trimDropdown: false,
            validator: HotUtils.validator.requiredAutocomplete,
            source: [''],
            include: true,
            unpack: function(lib, flat, setCellMeta) {
              flat.indexFamilyName = flat.platformType ? (lib.indexFamilyName || 'No indices') : '';
            },
            pack: function(lib, flat, errorHandler) {
            },
            depends: 'platformType',
            update: function(lib, flat, value, setReadOnly, setOptions) {
              var pt = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(platformType) {
                return platformType.key == value;
              }, Constants.platformTypes), 'name');
              if (!pt) {
                setOptions({
                  'source': ['']
                });
              } else {
                setOptions({
                  'source': ['No indices'].concat(Constants.indexFamilies.filter(function(family) {
                    return family.platformType == pt;
                  }).map(function(family) {
                    return family.name;
                  }).sort())
                });
              }
            }

          },
          makeIndexColumn(1),
          makeIndexColumn(2),
          {
            header: 'Kit',
            data: 'kitDescriptorName',
            type: 'dropdown',
            trimDropdown: false,
            validator: HotUtils.validator.requiredAutocomplete,
            source: [''],
            include: true,
            unpack: function(lib, flat, setCellMeta) {
              flat.kitDescriptorName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                  .idPredicate(lib.kitDescriptorId), Constants.kitDescriptors), 'name');
            },
            pack: function(lib, flat, errorHandler) {
              lib.kitDescriptorId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(kit) {
                return kit.platformType == flat.platformType && kit.kitType == 'Library' && kit.name == flat.kitDescriptorName;
              }, Constants.kitDescriptors), 'id');
            },
            depends: 'platformType',
            update: function(lib, flat, value, setReadOnly, setOptions) {
              setOptions({
                'source': Constants.kitDescriptors.filter(function(kit) {
                  return kit.platformType == flat.platformType && kit.kitType == 'Library';
                }).map(Utils.array.getName).sort()
              });
            }
          }, HotUtils.makeColumnForBoolean('QC Passed?', true, 'qcPassed', false),
          HotUtils.makeColumnForFloat('Size (bp)', true, 'dnaSize', false),
          HotUtils.makeColumnForFloat('Vol. (&#181;l)', config.showVolume, 'volume', false),
          HotUtils.makeColumnForFloat('Conc.', true, 'concentration', false), ];
    },

    bulkActions: [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/library/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }, {
      name: 'Make dilutions',
      action: function(items) {
        window.location = window.location.origin + '/miso/library/dilutions/bulk/propagate?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }, HotUtils.printAction('library'), ].concat(HotUtils.makeQcActions("Library")),

  };
})();
