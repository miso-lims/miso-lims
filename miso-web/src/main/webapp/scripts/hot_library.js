/**
 * Library-specific Handsontable code
 */

HotTarget.library = (function() {

  var getDesign = function(name) {
    return Utils.array.findFirstOrNull(Utils.array.namePredicate(name), Constants.libraryDesigns);
  };

  var makeIndexColumn = function(config, n) {
    var dependent = ['indexFamilyName', 'templateAlias', 'boxPosition', 'index' + n + 'Label']
    if (n > 1) {
      dependent.push('index' + (n - 1) + 'Label')
    }
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
      depends: dependent,
      update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
        var indexFamily = null;
        if (flatProperty === 'indexFamilyName' || flatProperty === '*start') {
          var pt = HotUtils.getPlatformType(flat.platformType);
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
        } else if (flatProperty === 'index' + n + 'Label') {
          var pt = HotUtils.getPlatformType(flat.platformType);
          indexFamily = Utils.array.findFirstOrNull(function(family) {
            return family.name == flat.indexFamilyName && family.platformType == pt;
          }, Constants.indexFamilies);
          var indices = (Utils.array.maybeGetProperty(indexFamily, 'indices') || []).filter(function(index) {
            return index.position == n;
          });
          var match = indices.find(function(index) {
            return index.sequence.toLowerCase() == value.toLowerCase() || index.label.toLowerCase() == value.toLowerCase();
          });
          if (match) {
            setData(match.label);
          }
        } else if (flatProperty === 'index' + (n - 1) + 'Label' && !Utils.validation.isEmpty(value)) {
          var pt = HotUtils.getPlatformType(flat.platformType);
          indexFamily = Utils.array.findFirstOrNull(function(family) {
            return family.name == flat.indexFamilyName && family.platformType == pt;
          }, Constants.indexFamilies);
          if (indexFamily && indexFamily.uniqueDualIndex) {
            var indexName = (Utils.array.maybeGetProperty(indexFamily, 'indices') || []).filter(function(index) {
              return index.position == n - 1;
            }).find(function(index) {
              return index.label == value || index.sequence == value;
            }).name;
            if (indexName) {
              var dualIndex = (Utils.array.maybeGetProperty(indexFamily, 'indices') || []).filter(function(index) {
                return index.position == n;
              }).find(function(index) {
                return index.name == indexName;
              });
              if (dualIndex) {
                setData(dualIndex.label);
              } else {
                Utils.showOkDialog('Error', ['There is no dual index for index \'' + indexName + '\'',
                    'Perhaps an index family is incorrectly marked as having unique dual indices']);
              }
            }
          }
        }
        var readOnly = false;
        if (flat.templateAlias && flat.boxPosition && indexFamily) {
          var template = getTemplate(config, lib.parentSampleProjectId, lib.parentSampleClassId, flat.templateAlias);
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

  var makeSpikeInDilutionFactorColumn = function() {
    var column = HotUtils.makeColumnForEnum('Spike-In Dilution Factor', true, true, 'spikeInDilutionFactor', Constants.dilutionFactors,
        'n/a', 'n/a');
    var requiredValidator = column.validator;
    column.depends = "spikeIn";
    column.update = function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
      if (!value || value === '(None)') {
        setReadOnly(true);
        setData('n/a');
        setOptions({
          required: false,
          validator: null
        });
      } else {
        setReadOnly(false);
        setOptions({
          required: true,
          validator: requiredValidator
        });
      }
    };
    return column;
  };

  var makeSpikeInVolumeColumn = function() {
    var column = HotUtils.makeColumnForDecimal('Spike-In Volume', true, 'spikeInVolume', 14, 10, true, false);
    var requiredValidator = column.validator;
    var requiredPack = column.pack;
    column.depends = "spikeIn";
    column.update = function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
      if (!value || value === '(None)') {
        setReadOnly(true);
        setData(null);
        setOptions({
          required: false,
          validator: null
        });
        column.pack = function(obj, flat, errorHandler) {
          obj.spikeInVolume = null;
        }
      } else {
        setReadOnly(false);
        setOptions({
          required: true,
          validator: requiredValidator
        });
        column.pack = requiredPack;
      }
    }
    return column;
  };

  var getProjectId = function(library, flat, config) {
    if (library.parentSampleProjectId) {
      return library.parentSampleProjectId;
    } else if (flat.sample && flat.sample.projectAlias) {
      return Utils.array.findUniqueOrThrow(function(project) {
        if (Constants.isDetailedSample) {
          return project.shortName === flat.sample.projectAlias;
        } else {
          return project.name === flat.sample.projectAlias;
        }
      }, config.projects).id;
    }
    return null;
  }

  var getTemplate = function(config, projectId, parentSampleClassId, templateAlias) {
    if (!config.templatesByProjectId || !config.templatesByProjectId[projectId]) {
      return null;
    }
    return Utils.array.findFirstOrNull(function(x) {
      return x.alias == templateAlias && (parentSampleClassId === null || x.designId === null || Constants.libraryDesigns.some(function(l) {
        return l.id == x.designId && l.sampleClassId == parentSampleClassId;
      }));
    }, config.templatesByProjectId[projectId]);
  };

  var updateFromTemplate = function(template, idProperty, source, displayProperty, setReadOnly, setData) {
    HotUtils.updateFromTemplateOrDesign(null, template, idProperty, source, displayProperty, setReadOnly, setData);
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
            header: 'Date of creation',
            data: 'creationDate',
            type: 'date',
            dateFormat: 'YYYY-MM-DD',
            datePickerConfig: {
              firstDay: 0,
              numberOfMonths: 1
            },
            allowEmpty: true,
            include: !config.isLibraryReceipt,
            unpack: function(lib, flat, setCellMeta) {
              // If creating, default to today's date in format YYYY-MM-DD
              if (!lib.creationDate && create) {
                flat.creationDate = Utils.getCurrentDate();
              } else {
                flat.creationDate = Utils.valOrNull(lib.creationDate);
              }
            },
            pack: function(lib, flat, errorHandler) {
              lib.creationDate = flat.creationDate;
            }
          },
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
          {
            header: 'Effective Group ID',
            data: 'effectiveGroupId',
            include: Constants.isDetailedSample && !config.isLibraryReceipt,
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
          {
            header: 'Template',
            data: 'templateAlias',
            type: 'dropdown',
            trimDropdown: false,
            source: ['(None)'],
            depends: ['*start', 'sample.projectAlias'], // *start is a dummy value that gets this run on creation only
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var projectId = null;
              if (config.templatesByProjectId) {
                if (flatProperty === 'sample.projectAlias') {
                  projectId = Utils.array.findUniqueOrThrow(function(project) {
                    if (Constants.isDetailedSample) {
                      return project.shortName === value;
                    } else {
                      return project.name === value;
                    }
                  }, config.projects).id;
                } else if (lib.parentSampleProjectId) {
                  projectId = lib.parentSampleProjectId;
                }
              }
              var templates = ['(None)'];
              if (projectId && config.templatesByProjectId[projectId]) {
                templates = templates.concat(config.templatesByProjectId[projectId].filter(function(x) {
                  return lib.parentSampleClassId === null || x.designId === null || Constants.libraryDesigns.some(function(l) {
                    return l.id == x.designId && l.sampleClassId == lib.parentSampleClassId;
                  });
                }).map(function(template) {
                  return template.alias;
                }));
              }
              setOptions({
                source: templates
              });
              setData('(None)');
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
                var projectId = getProjectId(lib, flat, config);
                var template = getTemplate(config, projectId, lib.parentSampleClassId, value);
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
                  var projectId = getProjectId(lib, flat, config);
                  var template = getTemplate(config, projectId, lib.parentSampleClassId, flat.templateAlias);
                  HotUtils.updateFromTemplateOrDesign(design, template, 'designCodeId', Constants.libraryDesignCodes, 'code', setReadOnly,
                      setData);
                },
                validator: HotUtils.validator.requiredAutocomplete
              }),
          {
            header: 'Platform',
            data: 'platformType',
            type: 'dropdown',
            readOnly: !create,
            trimDropdown: false,
            source: Constants.platformTypes.map(function(pt) {
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
              var projectId = getProjectId(lib, flat, config);
              var template = getTemplate(config, projectId, lib.parentSampleClassId, value);
              var readOnly = false;
              if (template && template.platformType) {
                setData(Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.namePredicate(template.platformType),
                    Constants.platformTypes), 'key'));
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
                  Constants.libraryTypes), 'alias')
                  || '';
            },
            'pack': function(lib, flat, errorHander) {
              lib.libraryTypeId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                  .aliasPredicate(flat.libraryTypeAlias), Constants.libraryTypes), 'id');
            },
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (flatProperty === 'platformType' || flatProperty === '*start') {
                var pt = HotUtils.getPlatformType(flat.platformType);
                setOptions({
                  'source': Constants.libraryTypes.filter(function(lt) {
                    return lt.platform == pt && (!lt.archived || lib.libraryTypeId == lt.id);
                  }).map(function(lt) {
                    return lt.alias;
                  }).sort()
                });
              }
              if (flat.templateAlias) {
                var projectId = getProjectId(lib, flat, config);
                var template = getTemplate(config, projectId, lib.parentSampleClassId, flat.templateAlias);
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
                  var projectId = getProjectId(lib, flat, config);
                  var template = getTemplate(config, projectId, lib.parentSampleClassId, flat.templateAlias);
                  HotUtils.updateFromTemplateOrDesign(design, template, 'selectionId', Constants.librarySelections, 'name', setReadOnly,
                      setData);
                }
              }),
          HotUtils.makeColumnForConstantsList('Strategy', true, 'libraryStrategyTypeAlias', 'libraryStrategyTypeId', 'id', 'name',
              Constants.libraryStrategies, true, {
                depends: ['libraryDesignAlias', 'templateAlias'],
                update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  var design = getDesign(flat.libraryDesignAlias);
                  var projectId = getProjectId(lib, flat, config);
                  var template = getTemplate(config, projectId, lib.parentSampleClassId, flat.templateAlias);
                  HotUtils.updateFromTemplateOrDesign(design, template, 'strategyId', Constants.libraryStrategies, 'name', setReadOnly,
                      setData);
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
                var projectId = getProjectId(lib, flat, config);
                var template = getTemplate(config, projectId, lib.parentSampleClassId, flat.templateAlias);
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
                var projectId = getProjectId(lib, flat, config);
                var template = getTemplate(config, projectId, lib.parentSampleClassId, flat.templateAlias);
                updateFromTemplate(template, 'kitDescriptorId', Constants.kitDescriptors, 'name', setReadOnly, setData);
              } else {
                setReadOnly(false);
              }
            }
          },
          HotUtils.makeColumnForBoolean('QC Passed?', true, 'qcPassed', false),
          HotUtils.makeColumnForFloat('Size (bp)', true, 'dnaSize', false),
          {
            header: 'Volume',
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
                  errorHandler('Volume' + ' is not a number.');
                  return;
                }
                output = result;
              }
              obj['volume'] = output;
            },
            depends: 'templateAlias',
            update: function(lib, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var projectId = getProjectId(lib, flat, config);
              var template = getTemplate(config, projectId, lib.parentSampleClassId, value);
              if (template && template.defaultVolume) {
                setData(template.defaultVolume);
              } else {
                setData(null);
              }
            }
          },
          {
            header: 'Vol. Units',
            data: 'volumeUnits',
            type: 'dropdown',
            trimDropdown: false,
            source: Constants.volumeUnits.map(function(unit) {
              return unit.units;
            }),
            include: config.showVolume,
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
          {
            header: 'Spike-In',
            data: 'spikeIn',
            type: 'dropdown',
            trimDropdown: false,
            source: ['(None)'].concat(Constants.spikeIns.map(function(spikeIn) {
              return spikeIn.alias;
            })),
            include: true,
            unpack: function(obj, flat, setCellMeta) {
              flat.spikeIn = obj.spikeInId == null ? '(None)' : Utils.array.getAliasFromId(obj.spikeInId, Constants.spikeIns);
            },
            pack: function(obj, flat, errorHandler) {
              obj.spikeInId = (Utils.validation.isEmpty(flat.spikeIn) || flat.spikeIn === '(None)') ? null : Utils.array.getIdFromAlias(
                  flat.spikeIn, Constants.spikeIns);
            }
          }, makeSpikeInDilutionFactorColumn(), makeSpikeInVolumeColumn()];

      var spliceIndex = columns.indexOf(columns.filter(function(column) {
        return column.data === 'identificationBarcode';
      })[0]) + 1;
      columns.splice.apply(columns, [spliceIndex, 0].concat(HotTarget.boxable.makeBoxLocationColumns(config)));
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
            var fields = [ListUtils.createBoxField];
            Utils.showDialog('Make Dilutions', 'Create', fields, function(result) {
              var params = {
                ids: items.map(Utils.array.getId).join(',')
              }
              var loadPage = function() {
                window.location = window.location.origin + '/miso/library/dilutions/bulk/propagate?' + jQuery.param(params);
              }
              if (result.createBox) {
                Utils.createBoxDialog(result, function(result) {
                  return items.length;
                }, function(newBox) {
                  params['boxId'] = newBox.id;
                  loadPage();
                });
              } else {
                loadPage();
              }
            });
          });
        }
      }, HotUtils.printAction('library'),
          HotUtils.spreadsheetAction('/miso/rest/library/spreadsheet', Constants.librarySpreadsheets, function(libraries, spreadsheet) {
            var errors = [];
            return errors;
          }),

          HotUtils.makeParents('library', HotUtils.relationCategoriesForDetailed()),
          HotUtils.makeChildren('library', [HotUtils.relations.dilution(), HotUtils.relations.pool()])].concat(
          HotUtils.makeQcActions("Library")).concat(
          [
              config.worksetId ? HotUtils.makeRemoveFromWorkset('libraries', config.worksetId) : HotUtils.makeAddToWorkset('libraries',
                  'libraryIds'), HotUtils.makeAttachFile('library', function(library) {
                return library.parentSampleProjectId;
              })]);
    }
  };
})();
