BulkTarget = window.BulkTarget || {};
BulkTarget.library = (function($) {

  /*
   * Expected config: {
   *   pageMode: string {create, propagate, edit}
   *   isLibraryReceipt: boolean
   *   libraryAliasMaybeRequired: boolean
   *   sampleAliasMaybeRequired: boolean
   *   showLibraryAlias: boolean
   *   showDescription: boolean
   *   showVolume: boolean
   *   recipientGroups
   *   workstations: array
   *   templatesByProjectId: map
   */

  var originalDataByRow = {};

  return {
    getSaveUrl: function() {
      return Urls.rest.libraries.bulkSave;
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('samples');
    },
    getCustomActions: function() {
      return BulkUtils.actions.boxable();
    },
    getBulkActions: function(config) {
      return [{
        name: 'Edit',
        action: function(items) {
          window.location = Urls.ui.libraries.bulkEdit + '?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }
      }, {
        name: 'Make aliquots',
        action: function(items) {
          HotUtils.warnIfConsentRevoked(items, function() {
            var fields = [ListUtils.createBoxField];
            Utils.showDialog('Make Aliquots', 'Create', fields, function(result) {
              var params = {
                ids: items.map(Utils.array.getId).join(',')
              }
              var loadPage = function() {
                window.location = Urls.ui.libraryAliquots.bulkPropagate + '?' + jQuery.param(params);
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
          HotUtils.spreadsheetAction(Urls.rest.libraries.spreadsheet, Constants.librarySpreadsheets, function(libraries, spreadsheet) {
            var errors = [];
            return errors;
          }),

          HotUtils.makeParents(Urls.rest.libraries.parents, HotUtils.relationCategoriesForDetailed()),
          HotUtils.makeChildren(Urls.rest.libraries.children, [HotUtils.relations.libraryAliquot(), HotUtils.relations.pool()])].concat(
          HotUtils.makeQcActions("Library")).concat(
          [
              config.worksetId ? HotUtils.makeRemoveFromWorkset('libraries', Urls.rest.worksets.removeLibraries(config.worksetId))
                  : HotUtils.makeAddToWorkset('libraries', 'libraryIds', Urls.rest.worksets.addLibraries),
              HotUtils.makeAttachFile('library', function(library) {
                return library.projectId;
              }), HotUtils.makeTransferAction('libraryIds')]);
    },
    prepareData: function(data, config) {
      data.forEach(function(library, index) {
        originalDataByRow[index] = {
          effectiveGroupId: library.effectiveGroupId,
          projectId: library.projectId,
          libraryTypeId: library.libraryTypeId,
          indexFamilyId: library.indexFamilyId
        };
      });
    },
    getFixedColumns: function(config) {
      switch (config.pageMode) {
      case 'propagate':
        return 1;
      case 'edit':
        return config.showLibraryAlias ? 2 : 1;
      case 'create':
        return config.sampleAliasMaybeRequired ? 1 : 0;
      default:
        throw new Error('Unexpected pageMode: ' + config.pageMode);
      }
    },
    getColumns: function(config, api) {
      var columns = [{
        title: 'Sample Alias',
        type: 'text',
        data: 'parentSampleAlias',
        setData: function(library, value, rowIndex, api) {
          if (config.isLibraryReceipt && config.sampleAliasMaybeRequired) {
            library.sample.alias = value;
          }
        },
        include: config.pageMode === 'propagate' || config.sampleAliasMaybeRequired,
        disabled: !config.isLibraryReceipt
      }, {
        title: 'Sample Location',
        type: 'text',
        data: 'sampleBoxPositionLabel',
        disabled: true,
        include: config.pageMode === 'propagate',
        customSorting: [{
          name: 'Sample Location (by rows)',
          sort: function(a, b) {
            return Utils.sorting.sortBoxPositions(a, b, true);
          }
        }, {
          name: 'Sample Location (by columns)',
          sort: function(a, b) {
            return Utils.sorting.sortBoxPositions(a, b, false);
          }
        }]
      }, BulkUtils.columns.name];

      if (config.showLibraryAlias) {
        columns.push(BulkUtils.columns.alias(config));
      }

      if (config.isLibraryReceipt) {
        var sampleProp = function(dataProperty) {
          return 'sample.' + dataProperty;
        };

        var interceptApi = function(api) {
          return {
            getCache: api.getCache,
            showError: api.showError,
            getRowCount: api.getRowCount,
            getValue: function(row, dataProperty) {
              return api.getValue(row, sampleProp(dataProperty));
            },
            getValueObject: function(row, dataProperty) {
              return api.getValueObject(row, sampleProp(dataProperty));
            },
            getSourceData: function(row, dataProperty) {
              return api.getSourceData(row, sampleProp(dataProperty));
            },
            updateField: function(rowIndex, dataProperty, options) {
              api.updateField(rowIndex, sampleProp(dataProperty), options);
            },
            updateData: function(changes) {
              // changes = [[row, prop, value]...]
              api.updateData(changes.map(function(change) {
                return [change[0], sampleProp(change[1]), change[2]];
              }));
            },
            isSaved: function() {
              return api.isSaved();
            }
          };
        };

        var samColumns = BulkTarget.sample.getColumns(config, api);
        samColumns.forEach(function(samCol) {
          if (samCol.setData) {
            throw new Error('sample column setData function not handled for library receipt');
          }
          samCol.data = sampleProp(samCol.data);
          samCol.includeSaved = false;
          if (samCol.getData) {
            var originalGetData = samCol.getData;
            samCol.getData = function(library) {
              return originalGetData(library.sample);
            };
          }
          if (samCol.onChange) {
            var originalOnChange = samCol.onChange;
            samCol.onChange = function(rowIndex, newValue, api) {
              originalOnChange(rowIndex, newValue, interceptApi(api));
            }
          }
        });

        if (config.templatesByProjectId) {
          var projectColumn = Utils.array.findUniqueOrThrow(function(samCol) {
            return samCol.data === 'sample.projectId';
          }, samColumns);

          var originalOnChange = projectColumn.onChange;
          projectColumn.onChange = function(rowIndex, newValue, api) {
            originalOnChange(rowIndex, newValue, api);

            var projectLabel = Constants.isDetailedSample ? 'shortName' : 'name';
            var project = config.projects.find(function(proj) {
              return proj[projectLabel] === newValue;
            });
            var source = [];
            if (project && config.templatesByProjectId[project.id]) {
              var parentSampleClassAlias = api.getValue(rowIndex, 'sample.sampleClassId');
              var parentSampleClass = Utils.array.findUniqueOrThrow(Utils.array.aliasPredicate(parentSampleClassAlias),
                  Constants.sampleClasses);
              source = config.templatesByProjectId[project.id].filter(function(template) {
                return !template.designId || Constants.libraryDesigns.some(function(design) {
                  return design.id === template.designId && design.sampleClassId === parentSampleClass.id;
                });
              });
            }
            api.updateField(rowIndex, 'template', {
              source: source
            });
          }
        }

        columns = columns.concat(samColumns);
      }

      if (config.showDescription) {
        columns.push(BulkUtils.columns.description);
      }

      if (config.isLibraryReceipt) {
        columns = columns.concat(BulkUtils.columns.receipt(config));
      }

      columns = columns.concat(BulkUtils.columns.boxable(config, api));
      if (config.templatesByProjectId && !api.isSaved()) {
        var posColumn = Utils.array.findUniqueOrThrow(function(col) {
          return col.data === 'boxPosition';
        }, columns);
        posColumn.onChange = function(rowIndex, newValue, api) {
          if (!api.isSaved()) {
            // If template is set and specifies indices, update indices
            var template = api.getValueObject(rowIndex, 'template');
            if (template && template.indexFamilyId) {
              var indexFamily = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(template.indexFamilyId), Constants.indexFamilies);
              updateIndicesFromTemplate(rowIndex, template, indexFamily, newValue, api);
            }
          }
        };
      }

      columns.push(BulkUtils.columns.creationDate(!config.isLibraryReceipt, config.pageMode == 'propagate', 'library'), {
        title: 'Workstation',
        type: 'dropdown',
        data: 'workstationId',
        include: !config.isLibraryReceipt,
        source: config.workstations,
        sortSource: Utils.sorting.standardSort('alias'),
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId
      }, {
        title: 'Thermal Cycler',
        type: 'dropdown',
        data: 'thermalCyclerId',
        include: !config.isLibraryReceipt,
        source: config.thermalCyclers,
        sortSource: Utils.sorting.standardSort('name'),
        getItemLabel: Utils.array.getName,
        getItemValue: Utils.array.getId
      });

      columns = columns.concat(BulkUtils.columns.groupId(!config.isLibraryReceipt, function(rowIndex) {
        return originalDataByRow[rowIndex].effectiveGroupId;
      }));

      columns
          .push({
            title: 'Template',
            type: 'dropdown',
            data: 'template',
            omit: true,
            include: !!config.templatesByProjectId,
            includeSaved: false,
            source: function(library, api) {
              var projectId = library.sample ? library.sample.projectId : library.projectId;
              if (!projectId || !config.templatesByProjectId[projectId]) {
                return [];
              }
              return config.templatesByProjectId[projectId].filter(function(template) {
                return !template.designId || Constants.libraryDesigns.some(function(design) {
                  return design.id === template.designId && design.sampleClassId === library.parentSampleClassId;
                });
              });
            },
            sortSource: Utils.sorting.standardSort('alias'),
            getItemLabel: Utils.array.getAlias,
            getItemValue: Utils.array.getId,
            onChange: function(rowIndex, newValue, api) {
              var template = api.getValueObject(rowIndex, 'template');
              if (template) {
                // volume can still be changed
                if (template.defaultVolume) {
                  api.updateField(rowIndex, 'volume', {
                    value: template.defaultVolume
                  });
                }
                if (template.volumeUnits) {
                  var unit = Utils.array.findUniqueOrThrow(Utils.array.namePredicate(template.volumeUnits), Constants.volumeUnits);
                  api.updateField(rowIndex, 'volumeUnits', {
                    value: unit.units
                  });
                }
              }

              if (Constants.isDetailedSample) {
                if (template && template.designId) {
                  var design = getPropertyForItemId(Constants.libraryDesigns, template.designId, 'name');
                  api.updateField(rowIndex, 'libraryDesignId', {
                    value: design,
                    disabled: true
                  });
                  // design change will trigger code, selection, and strategy updates
                } else if (template && (template.designCodeId || template.selectionId || template.strategyId)) {
                  api.updateField(rowIndex, 'libraryDesignId', {
                    value: null,
                    disabled: true
                  });
                  // design change will trigger code, selection, and strategy updates
                } else {
                  api.updateField(rowIndex, 'libraryDesignId', {
                    disabled: false
                  });
                  var design = api.getValueObject(rowIndex, 'libraryDesignId');
                  updateDesignCode(rowIndex, api, template, design);
                  updateSelection(rowIndex, api, template, design);
                  updateStrategy(rowIndex, api, template, design);
                }
              } else {
                updateFromTemplate(rowIndex, 'librarySelectionTypeId', api, template, 'selectionId', Constants.librarySelections, 'id',
                    'name');
                updateFromTemplate(rowIndex, 'libraryStrategyTypeId', api, template, 'strategyId', Constants.libraryStrategies, 'id',
                    'name');
              }
              updateFromTemplate(rowIndex, 'platformType', api, template, 'platformType', Constants.platformTypes, 'name', 'key');
              updateFromTemplate(rowIndex, 'libraryTypeId', api, template, 'libraryTypeId', Constants.libraryTypes, 'id', 'description');
              updateFromTemplate(rowIndex, 'indexFamilyId', api, template, 'indexFamilyId', Constants.indexFamilies, 'id', 'name');
              if (template && template.indexFamilyId) {
                var indexFamily = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(template.indexFamilyId), Constants.indexFamilies);
                var boxPos = api.getValue(rowIndex, 'boxPosition');
                updateIndicesFromTemplate(rowIndex, template, indexFamily, boxPos, api);
              } else {
                // Note: can't use api.getValueObject as source won't be initialized yet during initialization onChange
                var indexFamilyName = api.getValue(rowIndex, 'indexFamilyId');
                var indexFamily = Constants.indexFamilies.find(Utils.array.namePredicate(indexFamilyName));
                api.updateField(rowIndex, 'index1Id', {
                  disabled: !indexFamily
                });
                api.updateField(rowIndex, 'index2Id', {
                  disabled: !indexFamily || !indexFamily.indices.some(function(index) {
                    return index.position === 2;
                  })
                });
              }
              updateFromTemplate(rowIndex, 'kitDescriptorId', api, template, 'kitDescriptorId', Constants.kitDescriptors, 'id', 'name');
            }
          }, {
            title: 'Design',
            type: 'dropdown',
            data: 'libraryDesignId',
            include: Constants.isDetailedSample,
            source: function(library, api) {
              return Constants.libraryDesigns.filter(function(design) {
                return design.sampleClassId == library.parentSampleClassId;
              });
            },
            sortSource: Utils.sorting.standardSort('name'),
            getItemLabel: Utils.array.getName,
            getItemValue: Utils.array.getId,
            onChange: function(rowIndex, newValue, api) {
              if (!api.isSaved()) {
                var design = api.getValueObject(rowIndex, 'libraryDesignId');
                var template = config.templatesByProjectId ? api.getValueObject(rowIndex, 'template') : null;
                updateDesignCode(rowIndex, api, template, design);
                updateSelection(rowIndex, api, template, design);
                updateStrategy(rowIndex, api, template, design);
              }
            }
          }, {
            title: 'Code',
            type: 'dropdown',
            data: 'libraryDesignCodeId',
            include: Constants.isDetailedSample,
            required: true,
            source: Constants.libraryDesignCodes,
            sortSource: Utils.sorting.standardSort('code'),
            getItemLabel: Utils.array.get('code'),
            getItemValue: Utils.array.getId
          }, {
            title: 'Platform',
            type: 'dropdown',
            disabled: config.pageMode === 'edit',
            data: 'platformType',
            required: true,
            source: Constants.platformTypes,
            getItemLabel: Utils.array.get('key'),
            getItemValue: Utils.array.get('key'),
            onChange: function(rowIndex, newValue, api) {
              var selectedPlatform = Constants.platformTypes.find(function(pt) {
                return pt.key === newValue;
              });
              api.updateField(rowIndex, 'libraryTypeId', {
                source: newValue ? Constants.libraryTypes.filter(function(lt) {
                  return lt.platform === selectedPlatform.name && (!lt.archived || lt.id === originalDataByRow[rowIndex].libraryTypeId);
                }) : []
              });
              var indexFamilies = [];
              if (newValue) {
                indexFamilies = Constants.indexFamilies.filter(
                    function(family) {
                      return family.platformType === selectedPlatform.name
                          && (!family.archived || family.id === originalDataByRow[rowIndex].indexFamilyId);
                    }).sort(Utils.sorting.standardSort('name'));
                indexFamilies.unshift({
                  id: null,
                  name: 'No indices'
                });
              }
              api.updateField(rowIndex, 'indexFamilyId', {
                source: indexFamilies
              });
              api.updateField(rowIndex, 'kitDescriptorId', {
                source: newValue ? Constants.kitDescriptors.filter(function(kit) {
                  return kit.kitType === 'Library' && kit.platformType === selectedPlatform.key;
                }) : []
              });
            }
          }, {
            title: 'Type',
            type: 'dropdown',
            data: 'libraryTypeId',
            getData: function(library) {
              return getPropertyForItemId(Constants.libraryTypes, library.libraryTypeId, 'description');
            },
            required: true,
            source: [], // initialized in platformType onChange
            sortSource: Utils.sorting.standardSort('description'),
            getItemLabel: Utils.array.get('description'),
            getItemValue: Utils.array.getId
          }, {
            title: 'Selection',
            type: 'dropdown',
            data: 'librarySelectionTypeId',
            required: true,
            source: Constants.librarySelections,
            sortSource: Utils.sorting.standardSort('name'),
            getItemLabel: Utils.array.getName,
            getItemValue: Utils.array.getId
          }, {
            title: 'Strategy',
            type: 'dropdown',
            data: 'libraryStrategyTypeId',
            required: true,
            source: Constants.libraryStrategies,
            sortSource: Utils.sorting.standardSort('name'),
            getItemLabel: Utils.array.getName,
            getItemValue: Utils.array.getId
          },
              {
                title: 'Index Kit',
                type: 'dropdown',
                data: 'indexFamilyId',
                getData: function(library) {
                  if (library.indexFamilyId) {
                    return getPropertyForItemId(Constants.indexFamilies, library.indexFamilyId, 'name')
                  } else if (config.pageMode === 'edit' || api.isSaved()) {
                    return 'No indices';
                  } else {
                    // user must explicitly choose if no indices (null)
                    return '';
                  }
                },
                required: true,
                source: [], // initialized in platformType onChange
                getItemLabel: Utils.array.getName,
                getItemValue: Utils.array.getId,
                onChange: function(rowIndex, newValue, api) {
                  var indexFamily = newValue ? Utils.array.findFirstOrNull(Utils.array.namePredicate(newValue), Constants.indexFamilies)
                      : null;
                  var index1changes = null;
                  var index2changes = null;
                  if (indexFamily) {
                    var template = (!api.isSaved() && config.templatesByProjectId) ? api.getValueObject(rowIndex, 'template') : null;
                    var boxPos = api.getValue(rowIndex, 'boxPosition');

                    var getIndexChanges = function(pos, templateMapProperty) {
                      var indices = indexFamily.indices.filter(function(index) {
                        return index.position === pos;
                      });
                      var changes = {
                        source: indices,
                        required: !!indices.length,
                        disabled: !indices.length
                            || (template && boxPos && template[templateMapProperty] && template[templateMapProperty][boxPos])
                      };
                      if (!indices.length) {
                        changes.value = null;
                      }
                      return changes;
                    };
                    index1Changes = getIndexChanges(1, 'indexOneIds');
                    index2Changes = getIndexChanges(2, 'indexTwoIds');
                  } else {
                    index1Changes = {
                      source: [],
                      required: false,
                      disabled: true,
                      value: null
                    };
                    index2Changes = index1Changes;
                  }
                  api.updateField(rowIndex, 'index1Id', index1Changes);
                  api.updateField(rowIndex, 'index2Id', index2Changes);
                }
              }, makeIndexColumn(1), makeIndexColumn(2), {
                title: 'Has UMIs',
                type: 'dropdown',
                data: 'umis',
                required: true,
                source: [{
                  label: 'True',
                  value: true
                }, {
                  label: 'False',
                  value: false
                }],
                getItemLabel: Utils.array.get('label'),
                getItemValue: Utils.array.get('value'),
                initial: 'False'
              }, {
                title: 'Kit',
                type: 'dropdown',
                data: 'kitDescriptorId',
                getData: function(library) {
                  return getPropertyForItemId(Constants.kitDescriptors, library.kitDescriptorId, 'name');
                },
                required: true,
                source: [], // initialized in platformType onChange
                sortSource: Utils.sorting.standardSort('name'),
                getItemLabel: Utils.array.getName,
                getItemValue: Utils.array.getId
              }, {
                title: 'Kit Lot',
                type: 'text',
                data: 'kitLot'
              }, BulkUtils.columns.qcPassed(true), BulkUtils.columns.librarySize);

      if (config.showVolume) {
        columns = columns.concat(BulkUtils.columns.volume(true, config));
        if (!config.isLibraryReceipt) {
          columns = columns.concat(BulkUtils.columns.parentUsed);
        }
      }
      columns = columns.concat(BulkUtils.columns.concentration());

      columns.push({
        title: 'Spike-In',
        type: 'dropdown',
        data: 'spikeInId',
        source: Constants.spikeIns,
        sortSource: Utils.sorting.standardSort('alias'),
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId,
        onChange: function(rowIndex, newValue, api) {
          var changes = {
            disabled: !newValue,
            required: !!newValue
          };
          if (!newValue) {
            changes.value = null;
          }
          api.updateField(rowIndex, 'spikeInDilutionFactor', changes);
          api.updateField(rowIndex, 'spikeInVolume', changes);
        }
      }, {
        title: 'Spike-In Dilution Factor',
        type: 'dropdown',
        data: 'spikeInDilutionFactor',
        source: Constants.dilutionFactors
      }, {
        title: 'Spike-In Volume',
        type: 'decimal',
        data: 'spikeInVolume',
        precision: 14,
        scale: 10
      });

      return columns;
    }
  };

  function makeIndexColumn(position) {
    var column = {
      title: 'Index ' + position,
      type: 'dropdown',
      data: 'index' + position + 'Id',
      source: function(library, api) {
        if (!library.indexFamilyId) {
          return [];
        }
        var indexFamily = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(library.indexFamilyId), Constants.indexFamilies);
        return indexFamily.indices.filter(function(index) {
          return index.position === position;
        });
      },
      sortSource: Utils.sorting.standardSort('label'),
      getItemLabel: Utils.array.get('label'),
      getItemValue: Utils.array.getId
    };
    if (position === 1) {
      column.onChange = function(rowIndex, newValue, api) {
        if (!newValue) {
          return;
        }
        var indexFamilyName = api.getValue(rowIndex, 'indexFamilyId');
        var indexFamily = Utils.array.findFirstOrNull(Utils.array.namePredicate(indexFamilyName), Constants.indexFamilies);
        if (indexFamily && indexFamily.uniqueDualIndex) {
          var index1 = indexFamily.indices.find(function(index) {
            return index.position === 1 && index.label === newValue;
          });
          if (index1) {
            var index2 = indexFamily.indices.find(function(index) {
              return index.position === 2 && index.name === index1.name;
            });
            if (index2) {
              api.updateField(rowIndex, 'index2Id', {
                value: index2.label
              });
            }
          }
        }
      };
    }
    return column;
  }

  function getPropertyForItemId(items, id, property) {
    if (!id) {
      return null;
    }
    var item = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(id), items);
    return item[property];
  }

  function updateDesignCode(rowIndex, api, template, design) {
    updateFromDesignOrTemplate(rowIndex, api, 'libraryDesignCodeId', design, 'designCodeLabel', template, 'designCodeId',
        Constants.libraryDesignCodes, 'code');
  }

  function updateSelection(rowIndex, api, template, design) {
    updateFromDesignOrTemplate(rowIndex, api, 'librarySelectionTypeId', design, 'selectionName', template, 'selectionId',
        Constants.librarySelections, 'name');
  }

  function updateStrategy(rowIndex, api, template, design) {
    updateFromDesignOrTemplate(rowIndex, api, 'libraryStrategyTypeId', design, 'strategyName', template, 'strategyId',
        Constants.libraryStrategies, 'name');
  }

  function updateFromDesignOrTemplate(rowIndex, api, dataProperty, design, designItemLabelField, template, templateItemIdField, items,
      itemLabelField) {
    if (design) {
      api.updateField(rowIndex, dataProperty, {
        value: design[designItemLabelField],
        disabled: true
      });
    } else if (template && template.designCodeId) {
      var item = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(template[templateItemIdField]), items);
      api.updateField(rowIndex, dataProperty, {
        value: item[itemLabelField],
        disabled: true
      });
    } else {
      api.updateField(rowIndex, dataProperty, {
        disabled: false
      });
    }
  }

  function updateFromTemplate(rowIndex, dataProperty, api, template, templateProperty, items, idField, labelField) {
    if (template && template[templateProperty]) {
      var item = Utils.array.findUniqueOrThrow(function(x) {
        return x[idField] === template[templateProperty];
      }, items);
      api.updateField(rowIndex, dataProperty, {
        value: item[labelField],
        disabled: true
      });
    } else {
      api.updateField(rowIndex, dataProperty, {
        disabled: false
      });
    }
  }

  function updateIndicesFromTemplate(rowIndex, template, indexFamily, boxPos, api) {
    if (boxPos && template.indexOneIds && template.indexOneIds[boxPos]) {
      api.updateField(rowIndex, 'index1Id', {
        value: Utils.array.findUniqueOrThrow(Utils.array.idPredicate(template.indexOneIds[boxPos]), indexFamily.indices).label,
        disabled: true
      });
    } else {
      api.updateField(rowIndex, 'index1Id', {
        disabled: false
      });
    }
    if (boxPos && template.indexTwoIds && template.indexTwoIds[boxPos]) {
      api.updateField(rowIndex, 'index2Id', {
        value: Utils.array.findUniqueOrThrow(Utils.array.idPredicate(template.indexTwoIds[boxPos]), indexFamily.indices).label,
        disabled: true
      });
    } else {
      api.updateField(rowIndex, 'index2Id', {
        disabled: indexFamily.indices.some(function(index) {
          return index.position === 2;
        })
      });
    }
  }

})(jQuery);