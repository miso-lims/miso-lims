/**
 * Library-specific Handsontable code
 */

HotTarget.library = (function() {
  var getPlatformType = function(value) {
    return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(
        platformType) {
      return platformType.key == value;
    }, Constants.platformTypes), 'name');
  };
  
  var makeIndexColumn = function(n) {
    return {
      header : 'Index ' + n,
      data : 'index' + n + 'Label',
      type : 'autocomplete',
      strict : true,
      filter : false,
      allowInvalid : true,
      trimDropdown : false,
      source : [ '' ],
      include : true,
      unpack : function(lib, flat, setCellMeta) {
        var id = lib['index' + n + 'Id'];
        flat['index' + n + 'Label'] = id ? Constants.indexFamilies.reduce(
            function(acc, family) {
              return family.indices.reduce(function(acc, index) {
                return id == index.id ? index.label : acc;
              }, acc);
            }, null) : 'No index';
      },
      pack : function(lib, flat, errorHandler) {
        var label = flat['index' + n + 'Label'];
        var families = Constants.indexFamilies.filter(function(family) {
          return family.name == flat.indexFamilyName;
        });
        lib['index' + n + 'Id'] = families.length == 0 ? null : Utils.array
            .maybeGetProperty(Utils.array.findFirstOrNull(function(index) {
              return index.label == label;
            }, families[0].indices), 'id');
      },
      depends : 'indexFamilyName',
      update : function(lib, flat, value, setReadOnly, setOptions, setData) {
        var pt = getPlatformType(flat.platformType);
        var indices = (Utils.array.maybeGetProperty(Utils.array
            .findFirstOrNull(function(family) {
              return family.name == value && family.platformType == pt;
            }, Constants.indexFamilies), 'indices') || []).filter(
            function(index) {
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
        setOptions(indices);
        setData(data);
      }
    };
  };
  
  var makeDesignUpdate = function(id, nameProperty, defaultValue, source) {
    return function(lib, flat, value, setReadOnly, setOptions, setCell) {
      var design = Utils.array.findFirstOrNull(
          Utils.array.namePredicate(value), Constants.libraryDesigns);
      if (design) {
        setCell(Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(
            Utils.array.idPredicate(design[id]), source), nameProperty) || defaultValue);
      }
      setReadOnly(!!design);
    };
  };
  
  return {
    createUrl : '/miso/rest/library',
    updateUrl : '/miso/rest/library/',
    requestConfiguration : function(config, callback) {
      callback(config)
    },
    fixUp : function(lib, errorHandler) {
      lib.paired = Constants.libraryTypes.reduce(function(acc, type) {
        return acc || type.id == lib.libraryTypeId && type.alias
            .indexOf('Pair') != -1;
      }, lib.paired);
      if (Constants.isDetailedSample) {
        // if any members are null, fill them with empty objects otherwise
        // things
        // go poorly
        if (!lib.kitDescriptorId) {
          lib.kitDescriptorId = '';
          lib.kitDescriptorName = '';
        }
      }
    },
    
    createColumns : function(config, create, data) {
      return [
          {
            header : 'Library Alias',
            data : 'alias',
            validator : function(value, callback) {
              (Constants.automaticLibraryAlias
                  ? HotUtils.validator.optionalTextNoSpecialChars
                  : HotUtils.validator.requiredTextNoSpecialChars)(value,
                  function(result) {
                    if (!result) {
                      callback(false);
                      return;
                    }
                    if (!value) {
                      return callback(Constants.automaticLibraryAlias);
                    }
                    Fluxion.doAjax('libraryControllerHelperService',
                        'validateLibraryAlias', {
                          'alias' : value,
                          'url' : ajaxurl
                        }, {
                          'doOnSuccess' : function() {
                            return callback(true);
                          },
                          'doOnError' : function(json) {
                            return callback(false);
                          }
                        });
                  });
            },
            type : 'text',
            include : config.showLibraryAlias,
            unpack : function(lib, flat, setCellMeta) {
              flat.alias = lib.alias;
              if (lib.nonStandardAlias) {
                HotUtils.makeCellNSAlias(setCellMeta);
              }
            },
            pack : function(lib, flat, errorHandler) {
              lib.alias = flat.alias;
            }
          },
          {
            header : 'Sample Alias',
            data : 'parentSampleAlias',
            readOnly : true,
            include : true,
            unpack : function(lib, flat, setCellMeta) {
              flat.parentSampleAlias = lib.parentSampleAlias;
            },
            pack : function(lib, flat, errorHandler) {
            }
          },
          {
            header : 'Matrix Barcode',
            data : 'identificationBarcode',
            type : 'text',
            validator : HotUtils.validator.optionalTextNoSpecialChars,
            include : !Constants.automaticBarcodes,
            unpack : function(lib, flat, setCellMeta) {
              flat.identificationBarcode = lib.identificationBarcode;
            },
            pack : function(lib, flat, errorHandler) {
              if (flat.identificationBarcode) {
                lib.identificationBarcode = flat.identificationBarcode;
              }
            }
          },
          {
            header : 'Description',
            data : 'description',
            include : config.showDescription,
            validator : HotUtils.validator.optionalTextNoSpecialChars,
            unpack : function(lib, flat, setCellMeta) {
              flat.description = lib.description;
            },
            pack : function(lib, flat, errorHandler) {
              lib.description = flat.description;
            }
          },
          {
            header : 'Design',
            data : 'libraryDesignAlias',
            type : 'dropdown',
            trimDropdown : false,
            validator : HotUtils.validator.permitEmpty,
            source : [ '' ],
            include : Constants.isDetailedSample,
            unpack : function(lib, flat, setCellMeta) {
              flat.libraryDesignAlias = Hot.maybeGetProperty(Utils.array
                  .findFirstOrNull(
                      Utils.array.idPredicate(lib.libraryDesignId),
                      Constants.libraryDesigns), 'name') || '(None)';
            },
            pack : function(lib, flat, errorHandler) {
              if (flat.libraryDesignAlias == '(None)') {
                lib.libraryDesignId = null;
              } else {
                lib.libraryDesignId = Utils.array
                    .maybeGetProperty(
                        Utils.array
                            .findFirstOrNull(
                                function(design) {
                                  return design.name == flat.libraryDesignAlias && design.sampleClassId == lib.parentSampleClassId;
                                }, Constants.libraryDesigns), 'id');
                if (!lib.libraryDesignId) {
                  errorHandler('Invalid library design: ' + flat.libraryDesignAlias);
                }
              }
            },
            depends : '*start', // This is a dummy value that gets this run on
            // creation only
            update : function(lib, flat, value, setReadOnly, setOptions,
                setData) {
              setOptions([ '(None)' ].concat(Constants.libraryDesigns.filter(
                  function(design) {
                    return design.sampleClassId == lib.parentSampleClassId;
                  }).map(Utils.array.getName).sort()));
            }
          },
          HotUtils.makeColumnForConstantsList('Code',
              Constants.isDetailedSample, 'libraryDesignCode',
              'libraryDesignCodeId', 'id', 'code',
              Constants.libraryDesignCodes, {
                depends : 'libraryDesignAlias',
                update : makeDesignUpdate('designCodeId', 'code', 'WG',
                    Constants.libraryDesignCodes)
              }),
          {
            header : 'Platform',
            data : 'platformType',
            type : 'dropdown',
            trimDropdown : false,
            source : Constants.platformTypes.filter(function(pt) {
              return pt.active || data.reduce(function(acc, lib) {
                return acc || pt.key == lib.platformType;
              }, false);
            }).map(function(pt) {
              return pt.key;
            }),
            validator : HotUtils.validator.requiredText,
            include : true,
            unpack : function(lib, flat, setCellMeta) {
              flat.platformType = lib.platformType;
            },
            pack : function(lib, flat, errorHandler) {
              lib.platformType = flat.platformType;
            }
          },
          {
            'header' : 'Type',
            'data' : 'libraryTypeAlias',
            'type' : 'dropdown',
            'trimDropdown' : false,
            'source' : [ '' ],
            'validator' : HotUtils.validator.requiredText,
            'include' : true,
            'depends' : 'platformType',
            'unpack' : function(lib, flat, setCellMeta) {
              flat.libraryTypeAlias = Utils.array.maybeGetProperty(Utils.array
                  .findFirstOrNull(Utils.array.idPredicate(lib.libraryTypeId),
                      Constants.libraryTypes), 'alias');
            },
            'pack' : function(obj, flat, errorHander) {
              obj.libraryTypeId = Utils.array.maybeGetProperty(Utils.array
                  .findFirstOrNull(Utils.array
                      .aliasPredicate(flat.libraryTypeAlias),
                      Constants.libraryTypes), 'id');
            },
            update : function(lib, flat, value, setReadOnly, setOptions) {
              var pt = getPlatformType(value);
              setOptions(Constants.libraryTypes
                  .filter(
                      function(lt) {
                        return lt.platform == pt && (!lt.archived || lib.libraryTypeId == lt.id);
                      }).map(function(lt) {
                    return lt.alias;
                  }).sort());
            }
          
          },
          HotUtils.makeColumnForConstantsList('Selection', true,
              'librarySelectionTypeAlias', 'librarySelectionTypeId', 'id',
              'name', Constants.librarySelections, {
                depends : 'libraryDesignAlias',
                update : makeDesignUpdate('selectionId', 'name', '(None)',
                    Constants.librarySelections)
              
              }),
          HotUtils.makeColumnForConstantsList('Strategy', true,
              'libraryStrategyTypeAlias', 'libraryStrategyTypeId', 'id',
              'name', Constants.libraryStrategies, {
                depends : 'libraryDesignAlias',
                update : makeDesignUpdate('strategyId', 'name', '(None)',
                    Constants.libraryStrategies)
              
              }),
          {
            header : 'Index Kit',
            data : 'indexFamilyName',
            type : 'dropdown',
            trimDropdown : false,
            validator : HotUtils.validator.requiredText,
            source : [ '' ],
            include : true,
            unpack : function(lib, flat, setCellMeta) {
              flat.indexFamilyName = flat.platformType
                  ? (lib.indexFamilyName || 'No indices') : '';
            },
            pack : function(lib, flat, errorHandler) {
            },
            depends : 'platformType',
            update : function(lib, flat, value, setReadOnly, setOptions) {
              var pt = Utils.array.maybeGetProperty(Utils.array
                  .findFirstOrNull(function(platformType) {
                    return platformType.key == value;
                  }, Constants.platformTypes), 'name');
              if (!pt) {
                setOptions([ '' ]);
              } else {
                setOptions([ 'No indices' ].concat(Constants.indexFamilies
                    .filter(function(family) {
                      return family.platformType == pt;
                    }).map(function(family) {
                      return family.name;
                    }).sort()));
              }
            }
          
          },
          makeIndexColumn(1),
          makeIndexColumn(2),
          {
            header : 'Kit',
            data : 'kitDescriptorName',
            type : 'dropdown',
            trimDropdown : false,
            validator : HotUtils.validator.requiredText,
            source : [ '' ],
            include : true,
            unpack : function(lib, flat, setCellMeta) {
              flat.kitDescriptorName = Utils.array.maybeGetProperty(Utils.array
                  .findFirstOrNull(
                      Utils.array.idPredicate(lib.kitDescriptorId),
                      Constants.kitDescriptors), 'name') || 'None';
            },
            pack : function(lib, flat, errorHandler) {
              lib.kitDescriptorId = Utils.array
                  .maybeGetProperty(
                      Utils.array
                          .findFirstOrNull(
                              function(kit) {
                                return kit.platformType == flat.platformType && kit.kitType == 'Library' && kit.name == flat.kitDescriptorName;
                              }, Constants.kitDescriptors), 'id');
            },
            depends : 'platformType',
            update : function(lib, flat, value, setReadOnly, setOptions) {
              setOptions(Constants.kitDescriptors
                  .filter(
                      function(kit) {
                        return kit.platformType == flat.platformType && kit.kitType == 'Library';
                      }).map(Utils.array.getName).sort());
            }
          },
          HotUtils.makeColumnForOptionalBoolean('QC Passed?', true, 'qcPassed'),
          HotUtils.makeColumnForFloat('Size (bp)', true, 'dnaSize'),
          HotUtils.makeColumnForFloat('Vol. (&#181;l)', config.showVolume,
              'volume'),
          HotUtils.makeColumnForFloat('Qubit (ng/&#181;l)', !create, 'qcQubit'),
          HotUtils.makeColumnForFloat('TapeStation (bp)', !create,
              'qcTapeStation'),
          HotUtils.makeColumnForFloat('qPCR (mol/&#181;l)', !create, 'qcQPcr'), ];
    },
    
    bulkActions : [
        {
          name : 'Edit',
          action : function(ids) {
            window.location = window.location.origin + '/miso/library/bulk/edit/' + ids
                .join(',');
          }
        },
        {
          name : 'Make dilutions',
          action : function(ids) {
            window.location = window.location.origin + '/miso/library/dilutions/bulk/propagate/' + ids
                .join(',');
          }
        }, ],
  
  };
})();
