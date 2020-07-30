BulkTarget = window.BulkTarget || {};
BulkTarget.sample = (function($) {

  /*
   * Expected config: {
   *   pageMode: string {create, propagate, edit}
   *   box: optional new box created to put items in
   *   sourceSampleClass: required if propagating; class of parent samples
   *   targetSampleClass: required if detailed sample; class being created
   *   dnaseTreatable: true if the DNAse column should be shown
   *   isLibraryReceipt: optional boolean (default: false)
   *   recipientGroups: groups to include in Received By column
   *   project: project to create sample in,
   *   projects: all projects
   *   defaultLcmTubeGroupId: string
   *   defaultLcmTubeGroupDescription: string
   *   sortLibraryPropagate: string; column for default sort when propagating libraries
   *   sops: array
   * }
   */

  var originalProjectIdsBySampleId = {};
  var originalEffectiveGroupIdsByRow = {};

  return {
    getSaveUrl: function() {
      return Urls.rest.samples.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.samples.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('samples');
    },
    getCustomActions: function() {
      return BulkUtils.actions.boxable();
    },
    getBulkActions: function(config) {
      var actions = [
          {
            name: "Edit",
            action: function(samples) {

              if (samples.some(function(sample) {
                return sample.sampleClassId;
              }) && !Constants.isDetailedSample) {
                Utils.showOkDialog("Error", ["There are detailed samples, but MISO is not configured for this."]);
                return;
              }

              var classes = getSampleClasses(samples);
              var classesAliases = Utils.array.deduplicateString(classes.map(function(sampleClass) {
                return sampleClass.alias;
              }));
              if (classesAliases.length > 1) {
                Utils.showOkDialog("Error", ["You have selected samples of classes " + classesAliases.join(" & ")
                    + ". Please select samples from only one class."]);
                return;
              }
              Utils.page.post(Urls.ui.samples.bulkEdit, {
                ids: samples.map(Utils.array.getId).join(',')
              });
            }

          }, {
            name: "Propagate",
            action: function(samples) {
              HotUtils.warnIfConsentRevoked(samples, function() {
                var idsString = samples.map(Utils.array.getId).join(",");
                var classes = getSampleClasses(samples);

                // In the case of plain samples, this will be empty, which is fine.
                var targets = Utils.array.removeArchived(getChildSampleClasses(classes)).filter(function(sampleClass) {
                  return sampleClass.directCreationAllowed;
                }).sort(Utils.sorting.sampleClassComparator).map(function(sampleClass) {

                  return {
                    name: sampleClass.alias,
                    action: function(replicates, newBoxId) {
                      Utils.page.post(Urls.ui.samples.bulkPropagate, {
                        boxId: newBoxId,
                        parentIds: idsString,
                        replicates: replicates,
                        sampleClassId: sampleClass.id
                      });
                    }
                  };
                });
                if (!Constants.isDetailedSample || classes.every(function(sampleClass) {
                  return sampleClass.sampleCategory == "Aliquot";
                })) {
                  targets.push({
                    name: "Library",
                    action: function(replicates, newBoxId) {
                      var params = {
                        boxId: newBoxId,
                        ids: idsString,
                        replicates: replicates
                      }
                      if (config.sortLibraryPropagate) {
                        params.sort = config.sortLibraryPropagate;
                      }
                      Utils.page.post(Urls.ui.libraries.bulkPropagate, params)
                    }
                  });
                }

                if (targets.length == 0) {
                  Utils.showOkDialog("Error", ["No propagation is possible from the selected samples."]);
                  return;
                }

                Utils.showDialog(targets.length > 1 ? 'Propagate Samples' : ('Propagate to ' + targets[0].name), 'Propagate', [{
                  property: 'replicates',
                  type: 'int',
                  label: 'Replicates',
                  value: 1,
                  required: true
                }, (samples.length > 1 ? {
                  property: 'customReplication',
                  type: 'checkbox',
                  label: 'Specify replicates per sample',
                  value: false
                } : null), (targets.length > 1 ? {
                  property: 'target',
                  type: 'select',
                  label: 'To',
                  values: targets,
                  getLabel: Utils.array.getName
                } : null), ListUtils.createBoxField].filter(function(x) {
                  return !!x;
                }), function(result) {
                  var loadPage = function(boxId, replicates) {
                    (result.target || targets[0]).action(replicates, boxId);
                  };
                  var createBox = function(sampleCount, replicates) {
                    Utils.createBoxDialog(result, function(result) {
                      return sampleCount;
                    }, function(newBox) {
                      loadPage(newBox.id, replicates);
                    });
                  };
                  if (result.customReplication) {
                    var replicateFields = [];
                    for (var i = 0; i < samples.length; i++) {
                      replicateFields.push({
                        property: 'replicates' + i,
                        type: 'int',
                        label: samples[i].alias,
                        value: result.replicates,
                        required: true
                      });
                    }
                    Utils.showDialog('Propagate Samples - Replicates', 'OK', replicateFields, function(replicatesResult) {
                      var replicates = [];
                      for ( var key in replicatesResult) {
                        replicates.push(replicatesResult[key]);
                      }
                      var replicatesString = replicates.join(',');
                      if (result.createBox) {
                        createBox(replicates.reduce(function(total, num) {
                          return total + num;
                        }), replicatesString);
                      } else {
                        loadPage(null, replicatesString);
                      }
                    });
                  } else if (result.createBox) {
                    createBox(result.replicates * samples.length, result.replicates);
                  } else {
                    loadPage(null, result.replicates);
                  }
                });
              });
            }
          }, HotUtils.printAction('sample'),
          HotUtils.spreadsheetAction(Urls.rest.samples.spreadsheet, Constants.sampleSpreadsheets.filter(function(sheet) {
            return Constants.isDetailedSample || sheet.allowedClasses.indexOf('Plain') !== -1;
          }), function(samples, spreadsheet) {
            var errors = [];
            var invalidSamples = [];
            samples.forEach(function(sample) {
              if (!spreadsheet.sheet.allowedClasses.includes(getSampleCategory(sample))) {
                invalidSamples.push(sample);
              }
            })
            if (invalidSamples.length > 0) {
              errors.push("Error: Invalid sample class types");
              errors.push("Allowed types: " + spreadsheet.sheet.allowedClasses.join(", "));
              errors.push("Invalid samples:")
              invalidSamples.forEach(function(sample) {
                errors.push("* " + sample.alias + " (" + getSampleCategory(sample) + ")");
              })
            }
            return errors;
          })];

      if (Constants.isDetailedSample) {
        actions.push(HotUtils.makeParents(Urls.rest.samples.parents, HotUtils.relationCategoriesForDetailed()));
      }

      actions.push(HotUtils.makeChildren(Urls.rest.samples.children, HotUtils.relationCategoriesForDetailed().concat(
          [HotUtils.relations.library(), HotUtils.relations.libraryAliquot(), HotUtils.relations.pool()])));

      actions = actions.concat(BulkUtils.actions.qc('Sample'));

      if (config && config.worksetId) {
        actions.push(HotUtils.makeRemoveFromWorkset('samples', Urls.rest.worksets.removeSamples(config.worksetId)));
      } else {
        actions.push(HotUtils.makeAddToWorkset('samples', 'sampleIds', Urls.rest.worksets.addSamples));
      }

      actions.push(HotUtils.makeAttachFile('sample', function(sample) {
        return sample.projectId;
      }), HotUtils.makeTransferAction('sampleIds'));

      return actions;
    },
    prepareData: function(data, config) {
      data.forEach(function(sample, index) {
        originalEffectiveGroupIdsByRow[index] = sample.effectiveGroupId;
        if (config.pageMode === 'edit') {
          originalProjectIdsBySampleId[sample.id] = sample.projectId;
        } else if (sample.relatedSlides && sample.relatedSlides.length === 1) {
          sample.referenceSlideId = sample.relatedSlides[0].id;
        }
      });
    },
    getFixedColumns: function(config) {
      switch (config.pageMode) {
      case 'edit':
        return 2;
      case 'propagate':
        return 1;
      default:
        return 0;
      }
    },
    getColumns: function(config, api) {
      var targetCategory = (config.targetSampleClass ? config.targetSampleClass.sampleCategory : null);
      var sourceCategory = (config.sourceSampleClass ? config.sourceSampleClass.sampleCategory : null);
      // (Detailed sample) Columns to show
      var show = {};

      // We assume we have a linear progression of information that must be
      // collected as a sample progressed through the hierarchy.
      var progression = ['Identity', 'Tissue', 'Tissue Processing', 'Stock', 'Aliquot'];
      // First, set all the groups of detailed columns we will show to off.
      for (var i = 0; i < progression.length; i++) {
        show[progression[i]] = false;
      }
      // Determine the indices of the first and less steps in the progression.
      var endProgression = targetCategory == null ? -1 : progression.indexOf(targetCategory);
      var startProgression;
      if (!Constants.isDetailedSample) {
        startProgression = -1;
      } else if (config.pageMode == 'create') {
        startProgression = 0;
      } else if (config.pageMode == 'edit') {
        startProgression = endProgression;
      } else {
        startProgression = progression.indexOf(sourceCategory);
        // Increment to display columns of next category in progression unless
        // source and target category are the same (happens during
        // editing or propagation within a category).
        if (progression.indexOf(targetCategory) > progression.indexOf(sourceCategory)) {
          startProgression += 1;
        }
      }
      // Now, mark all the appropriate column groups active
      for (i = startProgression; i <= endProgression && i != -1; i++) {
        show[progression[i]] = true;
      }
      // If we aren't starting or finished with a tissue processing, hide those
      // columns since we don't really want to show tissue processing unless the
      // user specifically requested it.
      if (sourceCategory != 'Tissue Processing' && targetCategory != 'Tissue Processing' && config.targetSampleClass
          && (!config.targetSampleClass.sampleSubcategory || !config.targetSampleClass.sampleSubcategory.startsWith('Single Cell'))) {
        show['Tissue Processing'] = false;
      }

      var columns = [];

      if (!config.isLibraryReceipt) {
        columns.push(BulkUtils.columns.name, BulkUtils.columns.generatedAlias(config));

        if (Constants.isDetailedSample) {
          // parent columns go at start if propagating, or after the sample name and alias if editing
          var parentColumns = [{
            title: 'Parent Alias',
            type: 'text',
            data: 'parentAlias',
            disabled: true
          }, {
            title: 'Parent Location',
            type: 'text',
            data: 'parentBoxPositionLabel',
            disabled: true,
            customSorting: [{
              name: 'Parent Location (by rows)',
              sort: function(a, b) {
                return Utils.sorting.sortBoxPositions(a, b, true);
              }
            }, {
              name: 'Parent Location (by columns)',
              sort: function(a, b) {
                return Utils.sorting.sortBoxPositions(a, b, false);
              }
            }]
          }, {
            title: 'Parent Sample Class',
            type: 'text',
            data: 'parentTissueSampleClassAlias',
            disabled: true,
            include: config.pageMode === 'propagate',
            getData: function(sample) {
              return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                return item.id == sample.parentTissueSampleClassId;
              }, Constants.sampleClasses), 'alias');
            },
            omit: true
          }];
          if (config.pageMode === 'propagate') {
            columns = parentColumns.concat(columns);
          } else if (config.pageMode === 'edit' && !isTargetIdentity(config)) {
            columns = columns.concat(parentColumns);
          }
        }

        columns.push(BulkUtils.columns.description);

        if (config.pageMode === 'create' && (!Constants.isDetailedSample || !isTargetIdentity(config))) {
          columns = columns.concat(BulkUtils.columns.receipt(config));
        }
      }

      columns.push({
        title: 'Requisition ID',
        type: 'text',
        data: 'requisitionId',
        include: (!Constants.isDetailedSample || !isTargetIdentity(config)) && !config.isLibraryReceipt,
        regex: Utils.validation.alphanumRegex,
        maxLength: 50,
        description: 'ID of a requisition form stored in a separate system'
      });

      if (!isTargetIdentity(config) && !config.isLibraryReceipt) {
        columns = columns.concat(BulkUtils.columns.boxable(config, api));
      }

      columns.push({
        title: 'Sample Type',
        type: 'dropdown',
        data: 'sampleType',
        required: true,
        source: function(data, api) {
          // include current sample type in-case archived
          var source = Constants.sampleTypes;
          if (data.sampleType && source.indexOf(data.sampleType) === -1) {
            source = source.concat([data.sampleType]);
          }
          return source;
        },
        sortSource: Utils.sorting.standardSortItems
      }, {
        title: 'Project',
        type: 'dropdown',
        data: 'projectId',
        required: true,
        disabled: !!config.project,
        source: function(sample, api) {
          return config.projects.filter(function(project) {
            return project.status === 'Active' || project.id === sample.projectId;
          });
        },
        sortSource: Utils.sorting.standardSort(Constants.isDetailedSample ? 'shortName' : 'id'),
        getItemLabel: Constants.isDetailedSample ? function(item) {
          return item.shortName;
        } : Utils.array.getName,
        getItemValue: Utils.array.getId,
        initial: config.project ? config.project[Constants.isDetailedSample ? 'shortName' : 'name'] : null,
        onChange: function(rowIndex, newValue, api) {
          var project = config.projects.find(function(item) {
            if (Constants.isDetailedSample) {
              return item.shortName === newValue;
            } else {
              return item.name === newValue;
            }
          });

          if (Constants.isDetailedSample) {
            var subprojects = project ? Constants.subprojects.filter(function(subproject) {
              return subproject.parentProjectId === project.id;
            }) : [];
            var changes = {
              source: subprojects,
              required: !!subprojects.length,
              disabled: !subprojects.length
            };
            if (!subprojects.length) {
              changes.value = null;
            }
            api.updateField(rowIndex, 'subprojectId', changes);
          }

          if (project && project.defaultSciName && config.pageMode !== 'edit') {
            api.updateField(rowIndex, 'scientificNameId', {
              value: project.defaultSciName
            });
          }
        }
      }, {
        title: 'Subproject',
        type: 'dropdown',
        data: 'subprojectId',
        include: Constants.isDetailedSample,
        source: function(sample, api) {
          if (!sample.projectId) {
            return [];
          }
          var project = config.projects.find(Utils.array.idPredicate(sample.projectId));
          return Constants.subprojects.filter(function(subproject) {
            return subproject.parentProjectId === project.id;
          });
        },
        sortSource: Utils.sorting.standardSort('alias'),
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId
      }, {
        title: 'Sci. Name',
        type: 'dropdown',
        data: 'scientificNameId',
        required: true,
        source: Constants.scientificNames,
        sortSource: Utils.sorting.standardSort('alias'),
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId,
        initial: config.project && config.project.defaultSciName ? config.project.defaultSciName : config.defaultSciName
      }, {
        title: 'External Name',
        type: 'text',
        data: 'externalName',
        include: show['Identity'],
        includeSaved: isTargetIdentity(config),
        required: true,
        onChange: function(rowIndex, newValue, api) {
          if (isTargetIdentity(config)) {
            return;
          } else if (!newValue) {
            api.updateField(rowIndex, 'identityId', {
              source: [],
              value: null,
              formatter: null
            });
            return;
          }
          var label = Constants.isDetailedSample ? 'shortName' : 'name';
          var selectedProject = Utils.array.findFirstOrNull(function(project) {
            return project[label] === api.getValue(rowIndex, 'projectId');
          }, config.projects);
          if (selectedProject == null) {
            // the user needs to select a project
            api.updateField(rowIndex, 'identityId', {
              value: 'Delete external name, select a project, then re-enter external name.'
            });
            return;
          }
          api.updateField(rowIndex, 'identityId', {
            source: [],
            value: '(searching...)',
            formatter: null
          });
          // we search by null project in case the user wants to choose an identity from another project
          $.ajax({
            url: Urls.rest.samples.identitiesLookup + '?' + $.param({
              exactMatch: true
            }),
            data: JSON.stringify({
              identitiesSearches: [newValue],
              project: null
            }),
            contentType: 'application/json; charset=utf8',
            dataType: 'json',
            type: 'POST'
          }).success(function(data) {
            // sort with identities from selected project on top
            var potentialIdentities = [];
            if (data && data.length && data[0][newValue] && data[0][newValue].length) {
              potentialIdentities = data[0][newValue].sort(function(a, b) {
                var aSortId = a.projectId == selectedProject.id ? 0 : a.projectId;
                var bSortId = b.projectId == selectedProject.id ? 0 : b.projectId;
                return aSortId - bSortId;
              });
            }
            var setValue = null;
            var exactMatch = potentialIdentities.find(function(identity) {
              return identity.projectId === selectedProject.id && identity.externalName === newValue;
            });
            if (exactMatch) {
              setValue = exactMatch.alias + ' -- ' + exactMatch.externalName;
            } else {
              var firstReceiptLabel = "First Receipt (" + selectedProject[label] + ")";
              potentialIdentities.unshift({
                id: null,
                label: firstReceiptLabel
              });
              setValue = firstReceiptLabel;
            }
            api.updateField(rowIndex, 'identityId', {
              source: potentialIdentities,
              value: setValue,
              formatter: potentialIdentities.length > 1 ? 'multipleOptions' : null
            });
          }).fail(function(response, textStatus, serverStatus) {
            var error = JSON.parse(response.responseText);
            api.showError(error.detail);
          });
        }
      }, {
        title: 'Identity Alias',
        type: 'dropdown',
        data: 'identityId',
        include: show['Identity'] && !isTargetIdentity(config),
        includeSaved: false,
        required: true,
        source: [],
        getItemLabel: function(item) {
          return item.label || (item.alias + ' -- ' + item.externalName);
        },
        getItemValue: Utils.array.getId,
        description: 'A coloured background indicates that multiple identities correspond to the external name.',
        onChange: function(rowIndex, newValue, api) {
          var formatter = null;
          var source = api.getSourceData(rowIndex, 'identityId');
          if (source && source.length) {
            var identity = source.find(function(item) {
              return newValue === (item.alias + ' -- ' + item.externalName);
            });
            if (identity) {
              var existingExternalNames = identity.externalName.toLowerCase().split(",").map(function(name) {
                return name.trim();
              });
              var newExternalNames = api.getValue(rowIndex, 'externalName').toLowerCase().split(",").map(function(name) {
                return name.trim();
              });
              if (!newExternalNames.every(function(externalName) {
                return existingExternalNames.indexOf(externalName) != -1;
              })) {
                formatter = 'notification';
              }
            }
          }
          api.updateField(rowIndex, 'externalName', {
            formatter: formatter
          });
        }
      }, {
        title: 'Donor Sex',
        type: 'dropdown',
        data: 'donorSex',
        include: show['Identity'],
        includeSaved: isTargetIdentity(config),
        required: true,
        source: Constants.donorSexes,
        initial: 'Unknown'
      }, {
        title: 'Consent',
        type: 'dropdown',
        data: 'consentLevel',
        include: show['Identity'],
        includeSaved: isTargetIdentity(config),
        required: true,
        source: Constants.consentLevels,
        initial: 'This Project'
      }, {
        title: 'Sample Class',
        type: 'text',
        disabled: true,
        data: 'sampleClassId',
        include: Constants.isDetailedSample,
        getData: function(sample) {
          return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(sample.sampleClassId), Constants.sampleClasses).alias;
        },
        omit: true
      }, {
        title: 'Piece Type',
        type: 'dropdown',
        data: 'tissuePieceTypeId',
        include: show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory === 'Tissue Piece',
        includeSaved: isTargetTissuePiece(config),
        required: true,
        source: Constants.tissuePieceTypes,
        sortSource: Utils.sorting.standardSort('name'),
        getItemLabel: Utils.array.getName,
        getItemValue: Utils.array.getId,
        onChange: function(rowIndex, newValue, api) {
          if (newValue === 'LCM Tube' && (config.defaultLcmTubeGroupId || config.defaultLcmTubeGroupDescription)) {
            var currentGroupId = api.getValue(rowIndex, 'groupId');
            var currentGroupDesc = api.getValue(rowIndex, 'groupDescription');
            if (!currentGroupId && !currentGroupDesc) {
              if (config.defaultLcmTubeGroupId) {
                api.updateField(rowIndex, 'groupId', {
                  value: config.defaultLcmTubeGroupId
                });
              }
              if (config.defaultLcmTubeGroupDescription) {
                api.updateField(rowIndex, 'groupDescription', {
                  value: config.defaultLcmTubeGroupDescription
                });
              }
            }
          }
        }
      });

      if (!Constants.isDetailedSample || (!isTargetIdentity(config) && !isTargetTissue(config))) {
        columns.push(BulkUtils.columns.sop(config.sops));
      }

      if (Constants.isDetailedSample && !config.isLibraryReceipt) {
        var showEffective = !isTargetIdentity(config) && config.pageMode === 'edit';
        columns = columns.concat(BulkUtils.columns.groupId(showEffective, function(rowIndex) {
          return originalEffectiveGroupIdsByRow[rowIndex];
        }));
      }

      columns.push(BulkUtils.columns.creationDate(Constants.isDetailedSample && !config.isLibraryReceipt, config.pageMode == 'propagate',
          'sample'), {
        title: 'Tissue Origin',
        type: 'dropdown',
        data: 'tissueOriginId',
        include: show['Tissue'],
        includeSaved: isTargetTissue(config),
        required: true,
        source: Constants.tissueOrigins,
        sortSource: Utils.sorting.standardSort('alias'),
        getItemLabel: Utils.array.get('label'),
        getItemValue: Utils.array.getId
      }, {
        title: 'Tissue Type',
        type: 'dropdown',
        data: 'tissueTypeId',
        include: show['Tissue'],
        includeSaved: isTargetTissue(config),
        required: true,
        source: Constants.tissueTypes,
        sortSource: Utils.sorting.standardSort('alias'),
        getItemLabel: Utils.array.get('label'),
        getItemValue: Utils.array.getId
      }, {
        title: 'Passage #',
        type: 'int',
        data: 'passageNumber',
        include: show['Tissue'],
        includeSaved: isTargetTissue(config),
        min: 1
      }, {
        title: 'Times Received',
        type: 'int',
        data: 'timesReceived',
        include: show['Tissue'],
        includeSaved: isTargetTissue(config),
        min: 1
      }, {
        title: 'Tube Number',
        type: 'int',
        data: 'tubeNumber',
        include: show['Tissue'],
        includeSaved: isTargetTissue(config),
        min: 1
      }, {
        title: 'Lab',
        type: 'dropdown',
        data: 'labId',
        include: show['Tissue'] && !config.isLibraryReceipt && config.pageMode === 'edit',
        source: Constants.labs,
        sortSource: Utils.sorting.standardSort('label'),
        getItemLabel: Utils.array.get('label'),
        getItemValue: Utils.array.getId,
        description: 'The external lab that a tissue came from. This field is intended for historical data only as the lab should '
            + 'normally be recorded in a receipt transfer instead'
      }, {
        title: 'Secondary ID',
        type: 'text',
        data: 'secondaryIdentifier',
        include: show['Tissue'] && !config.isLibraryReceipt,
        includeSaved: isTargetTissue(config),
        maxLength: 255
      }, {
        title: 'Material',
        type: 'dropdown',
        data: 'tissueMaterialId',
        include: show['Tissue'],
        includeSaved: isTargetTissue(config),
        source: Constants.tissueMaterials,
        sortSource: Utils.sorting.standardSort('alias'),
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId
      }, {
        title: 'Region',
        type: 'text',
        data: 'region',
        include: show['Tissue'],
        includeSaved: isTargetTissue(config),
        maxLength: 255
      }, {
        title: 'Initial Slides',
        type: 'int',
        data: 'initialSlides',
        include: config.pageMode === 'edit' && isTargetSlide(config),
        required: true,
        min: 0
      }, {
        title: 'Slides',
        type: 'int',
        data: 'slides',
        include: isTargetSlide(config),
        required: true,
        min: 0
      }, {
        title: 'Discards',
        type: 'int',
        data: 'discards',
        include: isTargetSlide(config),
        required: true,
        min: 0
      }, {
        title: 'Thickness',
        type: 'int',
        data: 'thickness',
        include: isTargetSlide(config),
        min: 1
      }, {
        title: 'Stain',
        type: 'dropdown',
        data: 'stainId',
        include: isTargetSlide(config),
        source: Constants.stains,
        sortSource: Utils.sorting.standardSort('name'),
        getItemLabel: Utils.array.getName,
        getItemValue: Utils.array.getId
      }, {
        title: '% Tumour',
        type: 'decimal',
        data: 'percentTumour',
        include: isTargetSlide(config),
        precision: 11,
        scale: 8,
        min: 0,
        max: 100
      }, {
        title: '% Necrosis',
        type: 'decimal',
        data: 'percentNecrosis',
        include: isTargetSlide(config),
        precision: 11,
        scale: 8,
        min: 0,
        max: 100
      }, {
        title: 'Marked Area (mm²)',
        type: 'decimal',
        data: 'markedArea',
        include: isTargetSlide(config),
        precision: 11,
        scale: 8,
        min: 0
      }, {
        title: 'Marked Area % Tumour',
        type: 'decimal',
        data: 'markedAreaPercentTumour',
        include: isTargetSlide(config),
        precision: 11,
        scale: 8,
        min: 0,
        max: 100
      }, {
        title: 'Slides Consumed',
        type: 'int',
        data: 'slidesConsumed',
        include: isTargetTissuePiece(config),
        required: true,
        min: 0
      }, {
        title: 'Initial Cell Conc.',
        type: 'decimal',
        data: 'initialCellConcentration',
        include: show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory
            && config.targetSampleClass.sampleSubcategory.startsWith('Single Cell'),
        includeSaved: isTargetSingleCell(config),
        precision: 14,
        scale: 10
      }, {
        title: 'Digestion',
        type: 'text',
        data: 'digestion',
        include: show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory
            && config.targetSampleClass.sampleSubcategory.startsWith('Single Cell'),
        includeSaved: isTargetSingleCell(config),
        required: true
      }, {
        title: 'STR Status',
        type: 'dropdown',
        data: 'strStatus',
        include: show['Stock'] && !config.isLibraryReceipt,
        includeSaved: isTargetStock(config),
        required: true,
        source: Constants.strStatuses
      }, {
        title: 'DNAse',
        type: 'dropdown',
        data: 'dnaseTreated',
        include: Constants.isDetailedSample && config.dnaseTreatable,
        includeSaved: isTargetStock(config),
        required: true,
        source: [{
          label: 'True',
          value: true
        }, {
          label: 'False',
          value: false
        }],
        getItemLabel: Utils.array.get('label'),
        getItemValue: Utils.array.get('value')
      });

      if ((!Constants.isDetailedSample || show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt) {
        columns = columns.concat(BulkUtils.columns.volume(true, config));
        if (Constants.isDetailedSample) {
          columns = columns.concat(BulkUtils.columns.parentUsed);
        }
        columns = columns.concat(BulkUtils.columns.concentration());
      }

      columns.push({
        title: 'Target Cell Recovery',
        type: 'decimal',
        data: 'targetCellRecovery',
        include: show['Stock'] && config.targetSampleClass.sampleSubcategory
            && config.targetSampleClass.sampleSubcategory.startsWith('Single Cell'),
        includeSaved: isTargetStockSingleCell,
        precision: 14,
        scale: 10
      }, {
        title: 'Cell Viability',
        type: 'decimal',
        data: 'cellViability',
        include: show['Stock'] && config.targetSampleClass.sampleSubcategory
            && config.targetSampleClass.sampleSubcategory.startsWith('Single Cell'),
        includeSaved: isTargetStockSingleCell,
        precision: 14,
        scale: 10
      }, {
        title: 'Loading Cell Conc.',
        type: 'decimal',
        data: 'loadingCellConcentration',
        include: show['Stock'] && config.targetSampleClass.sampleSubcategory
            && config.targetSampleClass.sampleSubcategory.startsWith('Single Cell'),
        includeSaved: isTargetStockSingleCell,
        precision: 14,
        scale: 10
      }, {
        title: 'Reference Slide',
        type: 'dropdown',
        data: 'referenceSlideId',
        include: (isTargetTissuePiece(config) || show['Stock']) && !config.isLibraryReceipt,
        includeSaved: isTargetTissuePiece(config) || isTargetStock(config),
        source: function(data, api) {
          return data.relatedSlides || [];
        },
        sortSource: Utils.sorting.standardSort('id'),
        getItemLabel: function(item) {
          return item.name + ' (' + item.alias + ')';
        },
        getItemValue: Utils.array.getId,
        description: 'Indicates a slide whose attributes such as marked area and % tumour are relevant to this sample.'
            + ' May be used for calculating extraction input per yield, for example.',
      }, BulkUtils.columns.qcPassed(!Constants.isDetailedSample && !config.isLibraryReceipt), {
        title: 'QC Status',
        type: 'dropdown',
        data: 'detailedQcStatusId',
        include: Constants.isDetailedSample && !config.isLibraryReceipt,
        required: true,
        source: [{
          id: null,
          description: 'Not Ready'
        }].concat(Constants.detailedQcStatuses),
        sortSource: function(a, b) {
          return statusToInt(a) - statusToInt(b);
        },
        getItemLabel: Utils.array.get('description'),
        getItemValue: Utils.array.getId,
        initial: '', // user must explicitly choose if not ready (null)
        onChange: function(rowIndex, newValue, api) {
          var status = Constants.detailedQcStatuses.find(function(item) {
            return item.description === newValue;
          });
          var changes = {
            required: status && status.noteRequired,
            disabled: !status || !status.noteRequired
          };
          if (status && !status.noteRequired) {
            changes.value = null;
          }
          api.updateField(rowIndex, 'detailedQcStatusNote', changes);
        }
      }, {
        title: 'QC Note',
        type: 'text',
        data: 'detailedQcStatusNote',
        include: Constants.isDetailedSample && !config.isLibraryReceipt
      }, {
        title: 'Purpose',
        type: 'dropdown',
        data: 'samplePurposeId',
        include: show['Aliquot'] && !config.isLibraryReceipt,
        includeSaved: isTargetAliquot(config),
        source: function(sample, api) {
          return Constants.samplePurposes.filter(function(item) {
            return !item.archived || sample.samplePurposeId === item.id;
          });
        },
        sortSource: Utils.sorting.standardSort('alias'),
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId
      }, {
        title: 'Input into Library',
        type: 'decimal',
        data: 'inputIntoLibrary',
        include: show['Aliquot'] && config.targetSampleClass.sampleSubcategory
            && config.targetSampleClass.sampleSubcategory.startsWith('Single Cell'),
        includeSaved: isTargetAliquotSingleCell(config),
        precision: 14,
        scale: 10
      });

      return columns;
    },

    confirmSave: function(data, config) {
      var deferred = $.Deferred();
      if (config.pageMode === 'edit') {
        var changed = data.filter(function(sample) {
          return sample.projectId !== originalProjectIdsBySampleId[sample.id];
        }).map(
            function(sample) {
              return {
                sample: sample,
                originalProject: Utils.array.findUniqueOrThrow(Utils.array.idPredicate(originalProjectIdsBySampleId[sample.id]),
                    config.projects),
                newProject: Utils.array.findUniqueOrThrow(Utils.array.idPredicate(sample.projectId), config.projects)
              };
            });

        var projectLabel = Constants.isDetailedSample ? 'shortName' : 'name';
        var consentWarnings = changed.filter(function(change) {
          return (change.sample.identityConsentLevel && change.sample.identityConsentLevel !== 'All Projects');
        }).map(
            function(change) {
              return '• ' + (change.sample.alias || change.sample.parentAlias) + ': ' + change.originalProject[projectLabel] + ' → '
                  + change.newProject[projectLabel] + '; Consent: ' + change.sample.identityConsentLevel;
            });
        var libraryWarnings = changed.filter(function(change) {
          return change.sample.libraryCount > 0;
        }).map(
            function(change) {
              return '• ' + (change.sample.alias || change.sample.parentAlias) + ': ' + change.originalProject[projectLabel] + ' → '
                  + change.newProject[projectLabel] + '; ' + change.sample.libraryCount + ' librar'
                  + (change.sample.libraryCount > 1 ? 'ies' : 'y') + ' affected';
            });
        var messages = [];
        if (consentWarnings.length) {
          messages.push('The following project changes may violate consent:');
          messages = messages.concat(consentWarnings);
        }
        if (libraryWarnings.length) {
          messages.push('The following project changes affect existing libraries:');
          messages = messages.concat(libraryWarnings);
        }
        if (messages.length) {
          messages.push('Are you sure you wish to save?');
          Utils.showConfirmDialog('Project Changes', 'Save', messages, deferred.resolve, deferred.reject);
        } else {
          deferred.resolve();
        }
      } else {
        deferred.resolve();
      }
      return deferred.promise();
    },

    detailedQcStatusSort: function(a, b) {
      return statusToInt(a) - statusToInt(b);
    }
  };

  function isTargetIdentity(config) {
    return isTarget(config, 'Identity');
  }

  function isTargetTissue(config) {
    return isTarget(config, 'Tissue');
  }

  function isTargetSlide(config) {
    return isTarget(config, 'Tissue Processing', 'Slide');
  }

  function isTargetTissuePiece(config) {
    return isTarget(config, 'Tissue Processing', 'Tissue Piece');
  }

  function isTargetSingleCell(config) {
    return isTarget(config, 'Tissue Processing', 'Single Cell');
  }

  function isTargetStock(config) {
    return isTarget(config, 'Stock');
  }

  function isTargetStockSingleCell(config) {
    return isTarget(config, 'Stock', 'Single Cell (stock)');
  }

  function isTargetAliquot(config) {
    return isTarget(config, 'Aliquot');
  }

  function isTargetAliquotSingleCell(config) {
    return isTarget(config, 'Aliquot', 'Single Cell (aliquot)');
  }

  function isTarget(config, category, subcategory) {
    return config && config.targetSampleClass && config.targetSampleClass.sampleCategory === category
        && (!subcategory || config.targetSampleClass.sampleSubcategory === subcategory);
  }

  function statusToInt(status) {
    if (status.description === 'Not Ready') {
      return 0;
    } else if (status.status === true) {
      return 1;
    } else if (status.status === false) {
      return 2;
    }
    return 3;
  }

  function getSampleCategory(sample) {
    if (!Constants.isDetailedSample) {
      return 'Plain';
    }
    return Constants.sampleClasses.find(function(sampleClass) {
      return sample.sampleClassId == sampleClass.id;
    }).sampleCategory;
  }

  function getSampleClasses(samples) {
    var classIds = Utils.array.deduplicateNumeric(samples.map(function(sample) {
      return sample.sampleClassId || -1;
    }));
    return Constants.sampleClasses.filter(function(sampleClass) {
      return classIds.indexOf(sampleClass.id) !== -1;
    });
  }

  /**
   * Returns an array of sample classes that correspond to given sample class IDs (of parent samples)
   */
  function getChildSampleClasses(sampleClasses) {
    if (sampleClasses.length == 0) {
      return [];
    }
    return Constants.sampleClasses.filter(function(childClass) {
      return sampleClasses.every(function(parentClass) {
        return Constants.sampleValidRelationships.some(function(svr) {
          return svr.parentId == parentClass.id && svr.childId == childClass.id && !svr.archived;
        });
      });
    });
  }

})(jQuery);
