/**
 * Library-specific Handsontable code
 */

HotTarget.library = (function() {
  var getPlatformType = function(value) {
    return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(platformType) {
      return platformType.key == value;
    }, Constants.platformTypes), 'name');
  };

  var makeIndexColumn = function(config, n) {
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
          return index.label == label && index.position == n;
        }, families[0].indices), 'id');
      },
      depends: ['indexFamilyName', 'templateAlias', 'boxPosition', 'index' + n + 'Label'],
      update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
        var indexFamily = null;
        if (flatProperty === 'indexFamilyName' || flatProperty === '*start') {
          var pt = getPlatformType(flat.platformType);
          indexFamily = Utils.array.findFirstOrNull(function(family) {
            return family.name == flat.indexFamilyName && family.platformType == pt;
          }, Constants.indexFamilies);
          var indices = (Utils.array.maybeGetProperty(indexFamily, 'indices') || []).filter(function(index) {
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
        } else if (flatProperty === 'index' + n + 'Label'){
          var pt = getPlatformType(flat.platformType);
          indexFamily = Utils.array.findFirstOrNull(function(family) {
            return family.name == flat.indexFamilyName && family.platformType == pt;
          }, Constants.indexFamilies);
          var indices = (Utils.array.maybeGetProperty(indexFamily, 'indices') || []).filter(function(index) {
            return index.position == n;
          });
        	var match = indices.find(function(index){
						return index.sequence.toLowerCase() == value.toLowerCase() || index.label.toLowerCase() == value.toLowerCase();
					});
					if (match) {
						setData(match.label);
					}
        }
        var readOnly = false;
        if (flat.templateAlias && flat.boxPosition && indexFamily) {
          var template = getTemplate(config, lib.parentSampleProjectId, flat.templateAlias);
          var positionProp = n == 1 ? 'indexOneIds' : 'indexTwoIds';
          if (template.indexFamilyId && template[positionProp] && template[positionProp][flat.boxPosition]) {
            var index = Utils.array.getObjById(template[positionProp][flat.boxPosition], indexFamily.indices);
            if (index) {
              setData(index.label);
              readOnly = true;
            }
          }
        }
        setReadOnly(readOnly);
      }
    };
  };

  var getTemplate = function(config, projectId, templateAlias) {
    if (!config.templatesByProjectId || !config.templatesByProjectId[projectId]) {
      return null;
    }
    return Utils.array.findFirstOrNull(Utils.array.aliasPredicate(templateAlias), config.templatesByProjectId[projectId]);
  };

  var getDesign = function(name) {
    return Utils.array.findFirstOrNull(Utils.array.namePredicate(name), Constants.libraryDesigns);
  };

  var updateFromTemplate = function(template, idProperty, source, displayProperty, setReadOnly, setData) {
    var readOnly = false;
    if (template && template[idProperty]) {
      var change = Utils.array.findFirstOrNull(Utils.array.idPredicate(template[idProperty]), source);
      if (change) {
        setData(change[displayProperty]);
        readOnly = true;
      }
    }
    setReadOnly(readOnly);
  };

  var updateFromTemplateOrDesign = function(design, template, idProperty, source, displayProperty, setReadOnly, setData) {
    var id = null;
    if (design) {
      id = design[idProperty];
    } else if (template) {
      id = template[idProperty];
    }
    if (id) {
      var change = Utils.array.findFirstOrNull(Utils.array.idPredicate(id), source);
      if (change) {
        setData(change[displayProperty]);
      }
    }
    setReadOnly(design || (template && template.idProperty));
  };

  var checkQcs = function(table) {
    Utils.showDialog('QC Criteria', 'Check', [{
      label: 'Concentration',
      type: 'compare',
      property: 'concentrationComparator',
    }, {
      label: 'Volume',
      type: 'compare',
      property: 'volumeComparator',
    }, {
      label: 'Size',
      type: 'compare',
      property: 'sizeComparator',
    }], function(output) {
      var rowCount = table.countRows();
      var changes = [];
      for (var row = 0; row < rowCount; row++) {
        var pass = output.concentrationComparator(table.getDataAtRowProp(row, 'concentration'))
            && output.volumeComparator(table.getDataAtRowProp(row, 'volume'))
            && output.sizeComparator(table.getDataAtRowProp(row, 'dnaSize'))
        changes.push([row, 'qcPassed', pass ? 'True' : 'False']);
      }
      table.setDataAtRowProp(changes);
    });
  };

  var sortOptions = {
    sampleBoxColumn: {
      sortFunction: HotUtils.sorting.colSort,
      sortColumn: 'sampleBoxPositionLabel'
    }
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
    },
    onLoad: function(config, table) {
      if (config.sort) {
        var sortOption = sortOptions[config.sort];
        var sortColIndex = table.propToCol(sortOption.sortColumn);
        HotUtils.sortTable(table, sortColIndex, sortOption.sortFunction);
      }
    },
    createColumns: function(config, create, data) {
      var validationCache = {};
      var columns = [
          {
            header: 'Library Name',
            data: 'name',
            readOnly: true,
            include: true,
            unpackAfterSave: true,
            unpack: function(lib, flat, setCellMeta) {
              flat.name = Utils.valOrNull(lib.name);
            },
            pack: function(lib, flat, errorHandler) {
            }
          },
          {
            header: 'Library Alias',
            data: 'alias',
            type: 'text',
            include: config.showLibraryAlias,
            unpackAfterSave: true,
            unpack: function(lib, flat, setCellMeta) {
              validationCache[lib.alias] = true;
              flat.alias = Utils.valOrNull(lib.alias);
              if (lib.nonStandardAlias) {
                HotUtils.makeCellNSAlias(setCellMeta);
              }
              setCellMeta('validator', function(value, callback) {
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
                  if (lib.nonStandardAlias) {
                    return callback(true);
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
              });
            },
            pack: function(lib, flat, errorHandler) {
              lib.alias = flat.alias;
            }
          },
          {
            header: 'Sample Alias',
            data: 'parentSampleAlias',
            readOnly: !config.isLibraryReceipt,
            include: !config.isLibraryReceipt || !Constants.automaticSampleAlias,
            unpack: function(lib, flat, setCellMeta) {
              flat.parentSampleAlias = lib.parentSampleAlias;
            },
            pack: function(lib, flat, errorHandler) {
              if (config.isLibraryReceipt && !Constants.automaticSampleAlias) {
                if (!lib.sample) {
                  lib.sample = {};
                }
                lib.sample.alias = flat.parentSampleAlias;
              }
            }
          },
          {
            header: 'Sample Location',
            data: 'sampleBoxPositionLabel',
            type: 'text',
            readOnly: true,
            include: config.sortableLocation && !config.isLibraryReceipt,
            unpack: function(sam, flat, setCellMeta) {
              flat.sampleBoxPositionLabel = Utils.valOrNull(sam.sampleBoxPositionLabel);
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
            allowEmpty: !config.isLibraryReceipt,
            include: config.isLibraryReceipt || !create,
            unpack: function(lib, flat, setCellMeta) {
              // If creating, default to today's date in format YYYY-MM-DD
              if (!lib.receivedDate && create) {
                flat.receivedDate = Utils.getCurrentDate();
              } else {
                flat.receivedDate = Utils.valOrNull(lib.receivedDate);
              }
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
            header: 'Template',
            data: 'templateAlias',
            type: 'dropdown',
            trimDropdown: false,
            source: ['(None)'],
            depends: '*start', // This is a dummy value that gets this run on creation only
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (config.templatesByProjectId && config.templatesByProjectId[lib.parentSampleProjectId]) {
                setOptions({
                  'source': ['(None)'].concat(config.templatesByProjectId[lib.parentSampleProjectId].map(function(template) {
                    return template.alias;
                  }))
                });
              }
            },
            include: config.templatesByProjectId,
            unpack: function(lib, flat, setCellMeta) {
              flat.templateAlias = '(None)';
            },
            pack: function(lib, flat, errorHandler) {

            }
          },
          {
            header: 'Design',
            data: 'libraryDesignAlias',
            type: 'dropdown',
            trimDropdown: false,
            validator: Handsontable.validators.AutocompleteValidator,
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
            depends: ['*start', 'templateAlias'], // *start is a dummy value that gets this run on creation only
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (flatProperty === 'templateAlias') {
                var template = getTemplate(config, lib.parentSampleProjectId, value);
                updateFromTemplate(template, 'designId', Constants.libraryDesigns, 'name', setReadOnly, setData);
              } else {
                // must have been triggered by *start
                setOptions({
                  'source': ['(None)'].concat(Constants.libraryDesigns.filter(function(design) {
                    return design.sampleClassId == lib.parentSampleClassId;
                  }).map(Utils.array.getName).sort())
                });
              }
            }
          },
          HotUtils.makeColumnForConstantsList('Code', Constants.isDetailedSample, 'libraryDesignCode', 'libraryDesignCodeId', 'id', 'code',
              Constants.libraryDesignCodes, true, {
                depends: ['libraryDesignAlias', 'templateAlias'],
                update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  var design = getDesign(flat.libraryDesignAlias);
                  var template = getTemplate(config, lib.parentSampleProjectId, flat.templateAlias);
                  updateFromTemplateOrDesign(design, template, 'designCodeId', Constants.libraryDesignCodes, 'code', setReadOnly, setData);
                },
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
              flat.platformType = Utils.valOrNull(lib.platformType);
            },
            pack: function(lib, flat, errorHandler) {
              lib.platformType = flat.platformType;
            },
            depends: 'templateAlias',
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var template = getTemplate(config, lib.parentSampleProjectId, value);
              var readOnly = false;
              if (template && template.platformType) {
                setData(template.platformType);
                readOnly = true;
              }
              setReadOnly(readOnly);
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
            'depends': ['platformType', 'templateAlias'],
            'unpack': function(lib, flat, setCellMeta) {
              flat.libraryTypeAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(lib.libraryTypeId),
                  Constants.libraryTypes), 'alias');
            },
            'pack': function(lib, flat, errorHander) {
              lib.libraryTypeId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                  .aliasPredicate(flat.libraryTypeAlias), Constants.libraryTypes), 'id');
            },
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (flatProperty === 'platformType' || flatProperty === '*start') {
                var pt = getPlatformType(flat.platformType);
                setOptions({
                  'source': Constants.libraryTypes.filter(function(lt) {
                    return lt.platform == pt && (!lt.archived || lib.libraryTypeId == lt.id);
                  }).map(function(lt) {
                    return lt.alias;
                  }).sort()
                });
              }
              if (flat.templateAlias) {
                var template = getTemplate(config, lib.parentSampleProjectId, flat.templateAlias);
                updateFromTemplate(template, 'libraryTypeId', Constants.libraryTypes, 'alias', setReadOnly, setData);
              } else {
                setReadOnly(false);
              }
            }

          },
          HotUtils.makeColumnForConstantsList('Selection', true, 'librarySelectionTypeAlias', 'librarySelectionTypeId', 'id', 'name',
              Constants.librarySelections, true, {
                depends: ['libraryDesignAlias', 'templateAlias'],
                update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  var design = getDesign(flat.libraryDesignAlias);
                  var template = getTemplate(config, lib.parentSampleProjectId, flat.templateAlias);
                  updateFromTemplateOrDesign(design, template, 'selectionId', Constants.librarySelections, 'name', setReadOnly, setData);
                }
              }),
          HotUtils.makeColumnForConstantsList('Strategy', true, 'libraryStrategyTypeAlias', 'libraryStrategyTypeId', 'id', 'name',
              Constants.libraryStrategies, true, {
                depends: ['libraryDesignAlias', 'templateAlias'],
                update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  var design = getDesign(flat.libraryDesignAlias);
                  var template = getTemplate(config, lib.parentSampleProjectId, flat.templateAlias);
                  updateFromTemplateOrDesign(design, template, 'strategyId', Constants.libraryStrategies, 'name', setReadOnly, setData);
                }
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
              flat.indexFamilyName = flat.platformType ? (lib.indexFamilyName || 'No indices') : null;
            },
            pack: function(lib, flat, errorHandler) {
            },
            depends: ['platformType', 'templateAlias'],
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (flatProperty === 'platformType' || flatProperty === '*start') {
                var pt = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(platformType) {
                  return platformType.key == flat.platformType;
                }, Constants.platformTypes), 'name');
                if (!pt) {
                  setOptions({
                    'source': ['']
                  });
                } else {
                  setOptions({
                    'source': ['No indices'].concat(Constants.indexFamilies.filter(function(family) {
                      return family.platformType == pt && (!family.archived || lib.indexFamilyName === family.name);
                    }).map(function(family) {
                      return family.name;
                    }).sort())
                  });
                }
              }
              if (flat.templateAlias) {
                var template = getTemplate(config, lib.parentSampleProjectId, flat.templateAlias);
                updateFromTemplate(template, 'indexFamilyId', Constants.indexFamilies, 'name', setReadOnly, setData);
              } else {
                setReadOnly(false);
              }
            }
          },
          makeIndexColumn(config, 1),
          makeIndexColumn(config, 2),
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
            depends: ['platformType', 'templateAlias'],
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (flatProperty === 'platformType' || flatProperty === '*start') {
                setOptions({
                  'source': Constants.kitDescriptors.filter(function(kit) {
                    return kit.platformType == flat.platformType && kit.kitType == 'Library';
                  }).map(Utils.array.getName).sort()
                });
              }
              if (flat.templateAlias) {
                var template = getTemplate(config, lib.parentSampleProjectId, flat.templateAlias);
                updateFromTemplate(template, 'kitDescriptorId', Constants.kitDescriptors, 'name', setReadOnly, setData);
              } else {
                setReadOnly(false);
              }
            }
          }, HotUtils.makeColumnForBoolean('QC Passed?', true, 'qcPassed', false),
          HotUtils.makeColumnForFloat('Size (bp)', true, 'dnaSize', false),
          {
            header: 'Vol. (&#181;l)',
            data: 'volume',
            type: 'text',
            include: config.showVolume,
            unpack: function(obj, flat, setCellMeta) {
              flat['volume'] = Utils.valOrNull(obj['volume']);
            },
            validator: HotUtils.validator.optionalNumber,
            pack: function(obj, flat, errorHandler) {
              var output;
              if (Utils.validation.isEmpty(flat['volume'])) {
                output = null;
              } else {
                var result = parseFloat(flat['volume']);
                if (isNaN(result)) {
                  errorHandler('Vol. (&#181;l)' + ' is not a number.');
                  return;
                }
                output = result;
              }
              obj['volume'] = output;
            },
            depends: 'templateAlias',
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var template = getTemplate(config, lib.parentSampleProjectId, value);
              if (template && template.defaultVolume) {
                setData(template.defaultVolume);
              } else {
                setData(null);
              }
            }
          },
          HotUtils.makeColumnForFloat('Conc.', true, 'concentration', false), ];

      var spliceIndex = columns.indexOf(columns.filter(function(column) {
        return column.data === 'identificationBarcode';
      })[0]) + 1;
      columns.splice.apply(columns, [spliceIndex, 0].concat(HotTarget.boxable.makeBoxLocationColumns()));
      return columns;
    },

    getCustomActions: function(table) {
      return HotTarget.boxable.getCustomActions(table).concat([{
        buttonText: 'Check QCs',
        eventHandler: function() {
          checkQcs(table);
        }
      }]);
    },

    getBulkActions: function(config) {
      return [{
        name: 'Edit',
        action: function(items) {
          window.location = window.location.origin + '/miso/library/bulk/edit?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }
      }, {
        name: 'Make dilutions',
        action: function(items) {
          HotUtils.warnIfConsentRevoked(items, function() {
            window.location = window.location.origin + '/miso/library/dilutions/bulk/propagate?' + jQuery.param({
              ids: items.map(Utils.array.getId).join(',')
            });
          });
        }
      }, HotUtils.printAction('library'), HotUtils.spreadsheetAction('/miso/rest/library/spreadsheet', Constants.librarySpreadsheets),

      HotUtils.makeParents('library', HotUtils.relationCategoriesForDetailed()), 
      HotUtils.makeChildren('library',[HotUtils.relations.dilution(), HotUtils.relations.pool()])
      ].concat(HotUtils.makeQcActions("Library"));
    }
  };
})();
