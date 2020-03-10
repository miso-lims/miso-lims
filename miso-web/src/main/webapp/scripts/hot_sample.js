/**
 * Sample-specific Handsontable code
 */
HotTarget.sample = (function() {

  var getSampleClass = function(sample) {
    return Constants.sampleClasses.find(function(sampleClass) {
      return sample.sampleClassId == sampleClass.id;
    });
  };

  var getSampleClasses = function(samples) {
    var classIds = Utils.array.deduplicateNumeric(samples.map(function(sample) {
      return sample.sampleClassId || -1;
    }));
    return Constants.sampleClasses.filter(function(sampleClass) {
      return classIds.indexOf(sampleClass.id) != -1;
    });
  };

  /**
   * Returns an array of sample classes that correspond to given sample class IDs (of parent samples)
   */
  var getChildSampleClasses = function(sampleClasses) {
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
  };

  var getSelectedIdentity = function(flatObj) {
    if (!flatObj.potentialIdentities) {
      return null;
    }
    return Utils.array.findFirstOrNull(function(item) {
      return item.label == flatObj.identityAlias;
    }, flatObj.potentialIdentities);
  };

  var getExternalNames = function(identityAlias) {
    return identityAlias.replace(/.*--(\s*)/, "").toLowerCase().split(",");
  };

  var isTargetIdentity = function(config) {
    return config && config.targetSampleClass && config.targetSampleClass.sampleCategory == 'Identity';
  };

  var makeReferenceSlideLabel = function(sample) {
    return sample.name + ' (' + sample.alias + ')';
  };

  return {

    getCreateUrl: function() {
      return Urls.rest.samples.create;
    },
    getUpdateUrl: function(id) {
      return Urls.rest.samples.update(id);
    },
    requestConfiguration: function(config, callback) {
      if (Constants.isDetailedSample) {
        config.rnaSamples = config.targetSampleClass.alias.indexOf("RNA") != -1;
      }
      callback(config);
    },

    fixUp: function(sam, errorHandler) {

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
    createColumns: function(config, create, data) {
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

      var includeReceiptColumns = config.pageMode === 'create' && (!Constants.isDetailedSample || !isTargetIdentity(config))
          && !config.isLibraryReceipt;

      var columns = [
          {
            header: 'Sample Name',
            data: 'name',
            readOnly: true,
            include: !config.isLibraryReceipt,
            unpackAfterSave: true,
            unpack: function(sam, flat, setCellMeta) {
              flat.name = Utils.valOrNull(sam.name);
            },
            pack: function(sam, flat, errorHandler) {
            }
          },
          {
            header: 'Sample Alias',
            data: 'alias',
            type: 'text',
            unpackAfterSave: true,
            unpack: function(sam, flat, setCellMeta) {
              flat.alias = Utils.valOrNull(sam.alias);
              if (sam.nonStandardAlias) {
                HotUtils.makeCellNSAlias(setCellMeta);
              }
            },
            pack: function(sam, flat, errorHandler) {
              sam.alias = flat.alias;
            },
            include: !config.isLibraryReceipt
          },
          HotUtils.makeColumnForText('Description', !config.isLibraryReceipt, 'description', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),
          {
            header: 'Date of Receipt',
            data: 'receivedDate',
            type: 'date',
            dateFormat: 'YYYY-MM-DD',
            datePickerConfig: {
              firstDay: 0,
              numberOfMonths: 1
            },
            allowEmpty: true,
            description: 'The date that the sample was received from an external source.',
            include: includeReceiptColumns,
            unpack: function(sam, flat, setCellMeta) {
              // If creating, default to today's date in format YYYY-MM-DD
              if (!sam.receivedTime && config.pageMode == 'create') {
                flat.receivedDate = Utils.getCurrentDate();
              } else if (sam.receivedTime) {
                flat.receivedDate = sam.receivedTime.split(' ')[0];
              } else {
                flat.receivedDate = null;
              }
            },
            pack: function(sam, flat, errorHandler) {
              // Do nothing - handled in receivedTime
            }
          },
          {
            header: 'Time of Receipt',
            data: 'receivedTime',
            type: 'time',
            timeFormat: 'h:mm a',
            correctFormat: true,
            allowEmpty: true,
            description: 'The time that the sample was received from an external source. Example of expected format: "2:30 pm"',
            include: includeReceiptColumns,
            depends: ['*start', 'receivedDate'],
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              setReadOnly(!flat.receivedDate);
              setOptions({
                allowEmpty: !flat.receivedDate
              });
              if (!flat.receivedDate) {
                setData(null);
              } else if (!flat.receivedTime) {
                setData(Utils.getCurrentTime());
              }
            },
            unpack: function(sam, flat, setCellMeta) {
              // If creating, default to today's date in format YYYY-MM-DD
              if (!sam.receivedTime && config.pageMode == 'create') {
                flat.receivedTime = Utils.getCurrentTime();
              } else if (sam.receivedTime) {
                flat.receivedTime = Utils.formatTwelveHourTime(sam.receivedTime.split(' ')[1]);
              } else {
                flat.receivedTime = null;
              }
            },
            pack: function(sam, flat, errorHandler) {
              if (flat.receivedDate) {
                sam.receivedTime = flat.receivedDate + ' ' + Utils.formatTwentyFourHourTime(flat.receivedTime);
              } else {
                sam.receivedTime = null;
              }
            }
          },
          HotUtils.makeColumnForConstantsList('Received From', includeReceiptColumns, 'receivedFrom', 'senderLabId', 'id', 'label',
              Constants.labs, true, {
                depends: ['*start', 'receivedDate'],
                update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  setReadOnly(!flat.receivedDate);
                  setOptions({
                    validator: flat.receivedDate ? HotUtils.validator.requiredAutocomplete : null
                  });
                  if (!flat.receivedDate) {
                    setData(null);
                  }
                }
              }),
          HotUtils.makeColumnForConstantsList('Received By', includeReceiptColumns, 'receivedBy', 'recipientGroupId', 'id', 'name',
              config.recipientGroups, true, {
                depends: ['*start', 'receivedDate'],
                update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  setReadOnly(!flat.receivedDate);
                  setOptions({
                    validator: flat.receivedDate ? HotUtils.validator.requiredAutocomplete : null
                  });
                  if (flat.receivedDate) {
                    if (config.recipientGroups.length === 1) {
                      setData(config.recipientGroups[0].name);
                    }
                  } else {
                    setData(null);
                  }
                }
              }),
          HotUtils.makeColumnForBoolean('Receipt Confirmed', includeReceiptColumns, 'received', false, {
            depends: ['*start', 'receivedDate'],
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              setReadOnly(!flat.receivedDate);
              setOptions({
                validator: flat.receivedDate ? HotUtils.validator.requiredAutocomplete : null
              });
              if (flat.receivedDate) {
                setData('True');
              } else {
                setData(null);
              }
            }
          }, config.pageMode == 'create' ? 'True' : null),
          HotUtils.makeColumnForBoolean('Receipt QC Passed', includeReceiptColumns, 'receiptQcPassed', false, {
            depends: ['*start', 'receivedDate'],
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              setReadOnly(!flat.receivedDate);
              setOptions({
                validator: flat.receivedDate ? HotUtils.validator.requiredAutocomplete : null
              });
              if (flat.receivedDate) {
                setData('True');
              } else {
                setData(null);
              }
            }
          }, config.pageMode == 'create' ? 'True' : null),
          HotUtils.makeColumnForText('Receipt QC Note', includeReceiptColumns, 'receiptQcNote', {
            depends: ['*start', 'receivedDate', 'receiptQcPassed'],
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              setReadOnly(!flat.receivedDate);
              setOptions({
                validator: flat.receiptQcPassed === 'False' ? HotUtils.validator.requiredTextNoSpecialChars
                    : HotUtils.validator.optionalTextNoSpecialChars
              });
              if (!flat.receivedDate) {
                setData(null);
              }
            }
          }),
          HotUtils.makeColumnForText('Requisition ID', (!Constants.isDetailedSample || !isTargetIdentity(config))
              && !config.isLibraryReceipt, 'requisitionId', {
            validator: HotUtils.validator.optionalTextAlphanumeric,
            description: 'ID of a requisition form stored in a separate system'
          }),
          HotUtils.makeColumnForText('Matrix Barcode', !Constants.automaticBarcodes && !config.isLibraryReceipt
              && (!Constants.isDetailedSample || !isTargetIdentity(config)), 'identificationBarcode', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),
          {
            'header': 'Sample Type',
            'data': 'sampleType',
            'type': 'dropdown',
            'trimDropdown': false,
            'source': [],
            'include': true,
            'validator': HotUtils.validator.requiredAutocomplete,
            'unpack': function(obj, flat, setCellMeta) {
              flat.sampleType = obj.sampleType;
            },
            'pack': function(obj, flat, errorHandler) {
              obj.sampleType = flat.sampleType;
            },
            depends: '*start',
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              setOptions({
                source: (sam.sampleType && Constants.sampleTypes.indexOf(sam.sampleType) === -1) ? Constants.sampleTypes
                    .concat([sam.sampleType]) : Constants.sampleTypes
              });
            }
          },
          {
            header: 'Project',
            data: 'projectAlias',
            type: (config.hasProject ? 'text' : 'dropdown'),
            trimDropdown: false,
            source: [],
            unpack: function(sam, flat, setCellMeta) {
              var label = Constants.isDetailedSample ? 'shortName' : 'name';
              var projects = null;
              if (config.hasProject) {
                projects = [config.project];
                flat.projectAlias = Utils.valOrNull(config.project[label]);
              } else {
                projects = config.projects.filter(function(project) {
                  return project.status === 'Active' || project.id === sam.projectId;
                }).sort(Utils.sorting.standardSort(Constants.isDetailedSample ? 'shortName' : 'id'));
                var projectAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                  return item.id == sam.projectId;
                }, config.projects), label);
                flat.projectAlias = projectAlias;
                flat.originalProjectAlias = projectAlias;
                flat.identityConsentLevel = sam.identityConsentLevel;
                flat.libraryCount = sam.libraryCount;
              }
              setCellMeta('source', projects.map(function(project) {
                return project[label];
              }));
            },
            pack: function(sam, flat, errorHandler) {
              if (config.hasProject) {
                sam.projectId = config.project.id;
              } else {
                var label = Constants.isDetailedSample ? 'shortName' : 'name';
                sam.projectId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                  return item[label] == flat.projectAlias;
                }, config.projects), 'id');
              }
            },
            readOnly: config.hasProject,
            validator: HotUtils.validator.requiredAutocomplete,
            include: true
          },
          {
            header: 'Subproject',
            data: 'subprojectAlias',
            type: 'dropdown',
            source: ['(None)'],
            depends: ['*start', 'projectAlias'], // *start is a dummy value that gets run on page load only
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var projectId = sam.projectId;
              if (flatProperty === 'projectAlias') {
                // sample's project has changed
                projectId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.aliasPredicate(flat.projectAlias),
                    config.projects), 'id');
              }
              var subprojectsSource = Constants.subprojects.filter(function(subp) {
                return subp.parentProjectId == projectId;
              }).map(function(subp) {
                return subp.alias;
              }).sort();
              setOptions({
                'source': (subprojectsSource.length ? subprojectsSource : ['(None)'])
              });
              setData(subprojectsSource.length ? '' : '(None)');
            },
            validator: HotUtils.validator.requiredAutocomplete,
            include: Constants.isDetailedSample,
            unpack: function(sam, flat, setCellMeta) {
              flat.subprojectAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(sam.subprojectId),
                  Constants.subprojects), 'alias')
                  || '(None)';
            },
            pack: function(sam, flat, errorHandler) {
              sam.subprojectId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.aliasPredicate(flat.subprojectAlias),
                  Constants.subprojects), 'id');
            }
          },
          HotUtils.makeColumnForText('Sci. Name', true, 'scientificName', {
            validator: HotUtils.validator.requiredTextNoSpecialChars,
            unpack: function(obj, flat, setCellMeta) {
              flat.scientificName = obj.scientificName || config.defaultSciName || null;
            },
            depends: 'projectAlias',
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (config.projects) {
                var project = config.projects.find(function(proj) {
                  return proj.shortName == flat.projectAlias;
                });
                if (project && project.defaultSciName) {
                  setData(project.defaultSciName);
                }
              }
            }
          }),

          // Detailed Sample
          // Identity columns
          {
            header: 'External Name',
            data: 'externalName',
            validator: HotUtils.validator.requiredTextNoSpecialChars,
            include: show['Identity'],
            description: 'Search for multiple external names by separating them with commas. '
                + 'A coloured background indicates that new external names will be added to the identity upon creation.',
            unpack: function(sam, flat, setCellMeta) {
              flat.externalName = Utils.valOrNull(sam.externalName);
            },
            pack: function(sam, flat, errorHandler) {
              sam.externalNames = flat.externalName;
              if (!getSelectedIdentity(flat)) {
                sam.externalName = flat.externalName;
              } // else externalName will come from an existing Identity via the Identity Alias column
            },
            depends: 'identityAlias',
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (!value || value == "Delete external name, select a project, then re-enter external name." || value == '(...searching...)'
                  || /First Receipt \(.*\)/.test(value) || !flat['externalName']) {
                setOptions({
                  'renderer': Handsontable.renderers.TextRenderer
                });
                return;
              }

              var existingExternalNames = getExternalNames(value);
              var renderer = null;
              var newExternalNames = flat['externalName'].toLowerCase().split(",").map(function(name) {
                return name.trim();
              });
              var renderer = null;
              if (newExternalNames.every(function(externalName) {
                return existingExternalNames.indexOf(externalName) != -1;
              })) {
                renderer = Handsontable.renderers.TextRenderer;
              } else {
                renderer = HotUtils.notificationRenderer;
              }
              setOptions({
                'renderer': renderer
              });
            }
          },
          {
            header: 'Identity Alias',
            data: 'identityAlias',
            type: 'dropdown',
            trimDropdown: false,
            strict: true,
            source: [],
            description: 'A coloured background indicates that multiple identities correspond to the external name.',
            validator: HotUtils.validator.requiredAutocomplete,
            include: show['Identity'] && !isTargetIdentity(config),
            depends: 'externalName',
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var deferred = jQuery.Deferred();
              var label = Constants.isDetailedSample ? 'shortName' : 'name';
              var selectedProject = config.project || Utils.array.findFirstOrNull(function(project) {
                return project[label] == flat.projectAlias;
              }, config.projects);
              if (selectedProject == null) {
                // the user needs to select a project
                setData("Delete external name, select a project, then re-enter external name.");
                return;
              }
              if (!Utils.validation.isEmpty(flat.externalName)) {
                setData('(...searching...)');
                getIdentities();
              }
              return deferred.promise();

              function getIdentities() {
                // we search by null project in case the user wants to choose an identity from another project
                jQuery.ajax({
                  url: Urls.rest.samples.identitiesLookup + "?exactMatch=true",
                  data: JSON.stringify({
                    "identitiesSearches": [flat.externalName],
                    "project": null
                  }),
                  contentType: "application/json; charset=utf8",
                  dataType: "json",
                  type: "POST"
                }).success(
                    function(data) {
                      var potentialIdentities = [];
                      // sort with identities from selected project on top
                      var identitiesSources = [];
                      var found = [];
                      if (data[0] && data[0][flat.externalName] && data[0][flat.externalName].length > 0) {
                        found = data[0][flat.externalName];
                        found.sort(function(a, b) {
                          var aSortId = a.projectId == selectedProject.id ? 0 : a.projectId;
                          var bSortId = b.projectId == selectedProject.id ? 0 : b.projectId;
                          return aSortId - bSortId;
                        })
                        potentialIdentities = found;
                        for (var i = 0; i < potentialIdentities.length; i++) {
                          var identityLabel = potentialIdentities[i].alias + " -- " + potentialIdentities[i].externalName;
                          potentialIdentities[i].label = identityLabel;
                          identitiesSources.push(identityLabel);
                        }
                      }

                      var indexOfMatchingIdentityInProject = -1;
                      for (i = 0; i < found.length; i++) {
                        if (found[i].projectId == selectedProject.id && found[i].externalName == flat.externalName) {
                          indexOfMatchingIdentityInProject = i;
                          break;
                        }
                      }
                      if (indexOfMatchingIdentityInProject >= 0) {
                        setData(identitiesSources[indexOfMatchingIdentityInProject]);
                      } else {
                        identitiesSources.unshift("First Receipt (" + selectedProject[label] + ")");
                        setData(identitiesSources[0]);
                      }
                      flat.potentialIdentities = potentialIdentities;
                      var cellOptions = {
                        'source': identitiesSources,
                        'renderer': (identitiesSources.length > 1 ? HotUtils.multipleOptionsRenderer
                            : Handsontable.renderers.AutocompleteRenderer)
                      };
                      setOptions(cellOptions);
                    }).fail(function(response, textStatus, serverStatus) {
                  HotUtils.showServerErrors(response, serverStatus);
                }).always(function() {
                  deferred.resolve();
                });
              }
            },
            unpack: function(sam, flat, setCellMeta) {
              // Do nothing; this never comes from the server
            },
            pack: function(sam, flat, errorHandler) {
              var selectedIdentity = getSelectedIdentity(flat);
              if (selectedIdentity) {
                sam.parentAlias = selectedIdentity.alias;
                sam.externalName = selectedIdentity.externalName;
              } // else externalName is for a new Identity and will come from the External Name column
            }
          },
          HotUtils.makeColumnForEnum('Donor Sex', show['Identity'], true, 'donorSex', Constants.donorSexes, 'Unknown'),
          HotUtils.makeColumnForEnum('Consent', show['Identity'], true, 'consentLevel', Constants.consentLevels, 'This Project'),

          // Detailed sample columns
          {
            header: 'Sample Class',
            data: 'sampleClassAlias',
            type: 'text',
            readOnly: true,
            unpack: function(sam, flat, setCellMeta) {
              flat.sampleClassAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                return item.id == sam.sampleClassId;
              }, Constants.sampleClasses), 'alias');
            },
            pack: function(sam, flat, errorHandler) {
              sam.sampleClassId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                return item.alias == flat.sampleClassAlias;
              }, Constants.sampleClasses), 'id');
            },
            include: Constants.isDetailedSample
          },
          {
            header: 'Effective Group ID',
            data: 'effectiveGroupId',
            include: Constants.isDetailedSample && !isTargetIdentity(config) && !config.isLibraryReceipt && config.pageMode === 'edit',
            type: 'text',
            readOnly: true,
            depends: 'groupId',
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (flatProperty === 'groupId')
                setData(flat.groupId);
            },
            unpack: function(sam, flat, setCellMeta) {
              flat.effectiveGroupId = sam.effectiveGroupId ? sam.effectiveGroupId : '(None)';
            },
            pack: function(sam, flat, errorHandler) {
              // left blank as this will never be deserialized into the Sample model
            }
          },
          HotUtils.makeColumnForText('Group ID', Constants.isDetailedSample && !config.isLibraryReceipt, 'groupId', {
            validator: HotUtils.validator.optionalTextAlphanumeric
          }, config.targetSampleClass && config.targetSampleClass.sampleSubcategory === 'Tissue Piece' && config.pageMode != 'edit'
              ? config.defaultLcmTubeGroupId : null),
          HotUtils.makeColumnForText('Group Desc.', Constants.isDetailedSample && !config.isLibraryReceipt, 'groupDescription', {},
              config.targetSampleClass && config.targetSampleClass.sampleSubcategory === 'Tissue Piece' && config.pageMode != 'edit'
                  ? config.defaultLcmTubeGroupDescription : null),
          {
            header: 'Date of Creation',
            data: 'creationDate',
            type: 'date',
            dateFormat: 'YYYY-MM-DD',
            datePickerConfig: {
              firstDay: 0,
              numberOfMonths: 1
            },
            allowEmpty: true,
            description: 'The date that the sample was created in lab',
            include: Constants.isDetailedSample && !config.isLibraryReceipt,
            unpack: function(sam, flat, setCellMeta) {
              if (!sam.creationDate && config.pageMode == 'propagate') {
                flat.creationDate = Utils.getCurrentDate();
              } else {
                flat.creationDate = Utils.valOrNull(sam.creationDate);
              }
            },
            pack: function(sam, flat, errorHandler) {
              sam.creationDate = flat.creationDate;
            }
          },

          // Tissue columns
          HotUtils.makeAutocompleteColumnForConstantsList('Tissue Origin', show['Tissue'], 'tissueOriginAlias', 'tissueOriginId', 'id',
              'label', Constants.tissueOrigins, true, function(item, value) {
                return item.alias.toLowerCase() == value.toLowerCase() || item.description.toLowerCase() == value.toLowerCase();
              }, function(item) {
                return item.label;
              }),
          HotUtils.makeAutocompleteColumnForConstantsList('Tissue Type', show['Tissue'], 'tissueTypeAlias', 'tissueTypeId', 'id', 'label',
              Constants.tissueTypes, true, function(item, value) {
                return item.alias.toLowerCase() == value.toLowerCase() || item.description.toLowerCase() == value.toLowerCase();
              }, function(item) {
                return item.label;
              }),
          HotUtils.makeColumnForInt('Passage #', show['Tissue'], 'passageNumber', null),
          HotUtils.makeColumnForInt('Times Received', show['Tissue'], 'timesReceived', HotUtils.validator.integer(false, 1)),
          HotUtils.makeColumnForInt('Tube Number', show['Tissue'], 'tubeNumber', HotUtils.validator.integer(false, 1)),
          HotUtils.makeColumnForConstantsList('Lab', show['Tissue'] && !config.isLibraryReceipt && config.pageMode === 'edit',
              'labComposite', 'labId', 'id', 'label', Constants.labs, false, {
                description: 'The external lab that a tissue came from. This field is intended for historical data only as the lab should '
                    + 'normally be recorded in a receipt transfer instead'
              }),
          HotUtils.makeColumnForText('Secondary ID', show['Tissue'] && !config.isLibraryReceipt, 'secondaryIdentifier', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),
          HotUtils.makeColumnForConstantsList('Material', show['Tissue'], 'tissueMaterialAlias', 'tissueMaterialId', 'id', 'alias',
              Constants.tissueMaterials, false),
          HotUtils.makeColumnForText('Region', show['Tissue'], 'region', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),

          // Tissue Processing: Slides columns
          HotUtils.makeColumnForInt('Initial Slides',
              (config.pageMode === 'edit' && show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'),
              'initialSlides', HotUtils.validator.integer(true, 0)),
          HotUtils.makeColumnForInt('Slides', (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'),
              'slides', HotUtils.validator.integer(true, 0)),
          HotUtils.makeColumnForInt('Discards', (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'),
              'discards', HotUtils.validator.integer(true, 0)),
          HotUtils.makeColumnForInt('Thickness', (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'),
              'thickness', HotUtils.validator.integer(false, 0)),
          {
            header: 'Stain',
            data: 'stainName',
            type: 'dropdown',
            trimDropdown: false,
            source: function() {
              var stains = Constants.stains.sort(function(a, b) {
                return (a.categoryName || '').localeCompare(b.categoryName || '');
              }).map(function(s) {
                return s.name;
              });
              stains.unshift('(None)');
              return stains;
            }(),
            validator: Handsontable.validators.AutocompleteValidator,
            unpack: function(sam, flat, setCellMeta) {
              if (sam.stainId) {
                flat.stainName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(sam.stainId),
                    Constants.stains), 'name');
              } else {
                flat.stainName = '(None)';
              }
            },
            pack: function(sam, flat, errorHandler) {
              sam.stainId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.namePredicate(flat.stainName),
                  Constants.stains), 'id');
            },
            include: show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'
          },
          HotUtils.makeColumnForDecimal('% Tumour', (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'),
              'percentTumour', 11, 8, false, false),
          HotUtils.makeColumnForDecimal('% Necrosis', (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'),
              'percentNecrosis', 11, 8, false, false),
          HotUtils.makeColumnForDecimal('Marked Area (mm²)',
              (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'), 'markedArea', 11, 8, false, false),
          HotUtils.makeColumnForDecimal('Marked Area % Tumour',
              (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Slide'), 'markedAreaPercentTumour', 11, 8,
              false, false),

          // Tissue Processing: Tissue Piece columns
          HotUtils.makeAutocompleteColumnForConstantsList('Piece Type',
              (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Tissue Piece'), 'tissuePieceTypeName',
              'tissuePieceTypeId', 'id', 'name', Constants.tissuePieceTypes, true, function(item, value) {
                return item.name.toLowerCase() == value.toLowerCase() || item.abbreviation.toLowerCase() == value.toLowerCase();
              }, function(item) {
                return item.name;
              }),
          HotUtils.makeColumnForInt('Slides Consumed',
              (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Tissue Piece'), 'slidesConsumed',
              HotUtils.validator.integer(true, 0)),

          // Tissue Processing: Single Cell columns
          HotUtils.makeColumnForDecimal('Initial Cell Conc.',
              (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory && config.targetSampleClass.sampleSubcategory
                  .startsWith('Single Cell')), 'initialCellConcentration', 14, 10, false, false),
          HotUtils.makeColumnForText('Digestion',
              (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory && config.targetSampleClass.sampleSubcategory
                  .startsWith('Single Cell')), 'digestion', {
                validator: HotUtils.validator.requiredTextNoSpecialChars
              }),

          // Stock columns
          HotUtils.makeColumnForEnum('STR Status', show['Stock'] && !config.isLibraryReceipt, true, 'strStatus', Constants.strStatuses,
              null),
          {
            header: 'DNAse',
            data: 'dnaseTreated',
            type: 'dropdown',
            trimDropdown: false,
            validator: HotUtils.validator.requiredAutocomplete,
            source: ['True', 'False'],
            unpack: function(sam, flat, setCellMeta) {
              flat.dnaseTreated = (sam.dnaseTreated ? 'True' : 'False');
            },
            pack: function(sam, flat, errorHandler) {
              sam.dnaseTreated = flat.dnaseTreated === 'True';
            },
            include: Constants.isDetailedSample && config.dnaseTreatable
          },
          HotUtils.makeColumnForDecimal('Initial Volume',
              (config.pageMode === 'edit' && (show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt), 'initialVolume', 14, 10,
              false, true),
          HotUtils.makeColumnForDecimal('Volume', ((show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt), 'volume', 14, 10,
              false, true),
          {
            header: 'Vol. Units',
            data: 'volumeUnits',
            type: 'dropdown',
            trimDropdown: false,
            source: Constants.volumeUnits.map(function(unit) {
              return unit.units;
            }),
            include: ((show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt),
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
          HotUtils.makeColumnForDecimal('Parent ng Used', ((show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt), 'ngUsed', 14,
              10, false, false),
          HotUtils.makeColumnForDecimal('Parent Vol. Used', ((show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt), 'volumeUsed',
              14, 10, false, false),
          HotUtils.makeColumnForDecimal('Concentration', ((show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt), 'concentration',
              14, 10, false, false),
          {
            header: 'Conc. Units',
            data: 'concentrationUnits',
            type: 'dropdown',
            trimDropdown: false,
            source: Constants.concentrationUnits.map(function(unit) {
              return unit.units;
            }),
            include: ((show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt),
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
          // Stock: Single Cell columns
          HotUtils.makeColumnForDecimal('Target Cell Recovery',
              (show['Stock'] && config.targetSampleClass.sampleSubcategory && config.targetSampleClass.sampleSubcategory
                  .startsWith('Single Cell')), 'targetCellRecovery', 14, 10, false, false),
          HotUtils.makeColumnForDecimal('Cell Viability',
              (show['Stock'] && config.targetSampleClass.sampleSubcategory && config.targetSampleClass.sampleSubcategory
                  .startsWith('Single Cell')), 'cellViability', 14, 10, false, false),
          HotUtils.makeColumnForDecimal('Loading Cell Conc.',
              (show['Stock'] && config.targetSampleClass.sampleSubcategory && config.targetSampleClass.sampleSubcategory
                  .startsWith('Single Cell')), 'loadingCellConcentration', 14, 10, false, false),
          // Stock/Tissue Piece columns
          {
            header: 'Reference Slide',
            data: 'referenceSlideLabel',
            description: 'Indicates a slide whose attributes such as marked area and % tumour are relevant to this sample.'
                + ' May be used for calculating extraction input per yield, for example.',
            type: 'dropdown',
            trimDropdown: false,
            source: [],
            unpack: function(sam, flat, setCellMeta) {
              if (sam.referenceSlideId) {
                var slide = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(sam.referenceSlideId), sam.relatedSlides);
                flat.referenceSlideLabel = makeReferenceSlideLabel(slide);
              }
              if (sam.relatedSlides && sam.relatedSlides.length) {
                setCellMeta('source', sam.relatedSlides.map(function(slide) {
                  return makeReferenceSlideLabel(slide);
                }));
                if (sam.relatedSlides.length === 1) {
                  flat.referenceSlideLabel = makeReferenceSlideLabel(sam.relatedSlides[0]);
                }
              }
            },
            pack: function(sam, flat, errorHandler) {
              if (flat.referenceSlideLabel) {
                var slide = Utils.array.findUniqueOrThrow(function(relatedSlide) {
                  var label = makeReferenceSlideLabel(relatedSlide);
                  return label === flat.referenceSlideLabel;
                }, sam.relatedSlides);
                sam.referenceSlideId = slide.id;
              } else {
                sam.referenceSlideId = null;
              }
            },
            validator: HotUtils.validator.permitEmptyDropdown,
            include: (show['Tissue Processing'] && config.targetSampleClass.sampleSubcategory == 'Tissue Piece')
                || (show['Stock'] && !config.isLibraryReceipt)
          },
          // QC status columns for detailed and non-detailed samples
          {
            header: 'QC Passed?',
            data: 'qcPassed',
            type: 'dropdown',
            trimDropdown: false,
            source: ['Unknown', 'True', 'False'],
            unpack: function(sam, flat, setCellMeta) {
              if (sam.qcPassed === true)
                flat.qcPassed = 'True';
              else if (sam.qcPassed === false)
                flat.qcPassed = 'False';
              else
                flat.qcPassed = 'Unknown';
            },
            pack: function(sam, flat, errorHandler) {
              if (flat.qcPassed === 'True')
                sam.qcPassed = true;
              else if (flat.qcPassed === 'False')
                sam.qcPassed = false;
              else
                sam.qcPassed = null;
            },
            include: !Constants.isDetailedSample && !config.isLibraryReceipt
          },
          {
            header: 'QC Status',
            data: 'detailedQcStatusDescription',
            type: 'dropdown',
            trimDropdown: false,
            source: (function() {
              var statuses = Constants.detailedQcStatuses.sort(function(a, b) {
                function statusToInt(status) {
                  if (status === true)
                    return 0;
                  if (status === false)
                    return 1;
                  return 2;
                }
                return statusToInt(a.status) - statusToInt(b.status);
              }).map(function(s) {
                return s.description;
              });
              statuses.unshift('Not Ready'); // can't chain this because
              // unshift returns the length of
              // the array
              return statuses;
            })(),
            validator: HotUtils.validator.requiredText,
            unpack: function(sam, flat, setCellMeta) {
              if (config.pageMode == 'edit') {
                flat.detailedQcStatusDescription = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                    .idPredicate(sam.detailedQcStatusId), Constants.detailedQcStatuses), 'description')
                    || 'Not Ready';
              } else {
                flat.detailedQcStatusDescription = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                    .idPredicate(sam.detailedQcStatusId), Constants.detailedQcStatuses), 'description');
              }
            },
            pack: function(sam, flat, errorHandler) {
              if (!Utils.validation.isEmpty(flat.detailedQcStatusDescription)) {
                sam.detailedQcStatusId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                    .descriptionPredicate(flat.detailedQcStatusDescription), Constants.detailedQcStatuses), 'id');
              }
            },
            include: Constants.isDetailedSample && !config.isLibraryReceipt
          },
          HotUtils.makeColumnForText('QC Note', Constants.isDetailedSample && !config.isLibraryReceipt, 'detailedQcStatusNote', {
            readOnly: true,
            depends: 'detailedQcStatusDescription',
            update: function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var qcStatus = Utils.array.findFirstOrNull(Utils.array.descriptionPredicate(value), Constants.detailedQcStatuses);
              if (qcStatus != null && qcStatus.noteRequired) {
                setReadOnly(false);
                setData(sam.detailedQcStatusNote ? sam.detailedQcStatusNote : null);
                setOptions({
                  'validator': HotUtils.validator.requiredTextNoSpecialChars
                })
              } else {
                setReadOnly(true);
                setData('');
                setOptions({
                  'validator': Handsontable.validators.TextValidator
                })
              }
            }
          }),

          // Aliquot columns
          HotUtils.makeColumnForConstantsList('Purpose', show['Aliquot'] && !config.isLibraryReceipt, 'samplePurposeAlias',
              'samplePurposeId', 'id', 'alias', Constants.samplePurposes, true, {
                'depends': '*start',
                'update': function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                  setOptions({
                    'source': Constants.samplePurposes.filter(function(samplePurpose) {
                      return !samplePurpose.archived || sam.samplePurposeId == samplePurpose.id;
                    }).map(function(samplePurpose) {
                      return samplePurpose.alias;
                    })
                  });
                }
              }),
          // Aliquot: Single Cell columns
          HotUtils.makeColumnForDecimal('Input into Library',
              (show['Aliquot'] && config.targetSampleClass.sampleSubcategory && config.targetSampleClass.sampleSubcategory
                  .startsWith('Single Cell')), 'inputIntoLibrary', 14, 10, false, false)];

      if (!config.isLibraryReceipt) {
        var spliceIndex = columns.indexOf(columns.filter(function(column) {
          return column.data === 'identificationBarcode';
        })[0]) + 1;
        if (!Constants.isDetailedSample || !isTargetIdentity(config)) {
          // don't add boxable columns to Identities
          columns.splice.apply(columns, [spliceIndex, 0].concat(HotTarget.boxable.makeBoxLocationColumns(config)));
        }

        // parent columns go at start if propagating, or after the sample name and alias if editing
        var parentColumns = [HotUtils.makeColumnForText('Parent Alias', true, 'parentAlias', {
          readOnly: true
        }), {
          header: 'Parent Location',
          data: 'parentBoxPositionLabel',
          type: 'text',
          readOnly: true,
          include: true,
          unpack: function(sam, flat, setCellMeta) {
            flat.parentBoxPositionLabel = Utils.valOrNull(sam.parentBoxPositionLabel);
          },
          pack: function(sam, flat, errorHandler) {
            // not modifiable
          },
          customSorting: [{
            buttonText: 'Sort by Parent Location (rows)',
            sortTarget: 'ParentRows',
            sortFunc: HotUtils.sorting.rowSort
          }, {
            buttonText: 'Sort by Parent Location (columns)',
            sortTarget: 'ParentColumns',
            sortFunc: HotUtils.sorting.colSort
          }]
        }, {
          header: 'Parent Sample Class',
          data: 'parentTissueSampleClassAlias',
          type: 'text',
          readOnly: true,
          unpack: function(sam, flat, setCellMeta) {
            flat.parentTissueSampleClassAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
              return item.id == sam.parentTissueSampleClassId;
            }, Constants.sampleClasses), 'alias');
          },
          pack: function(sam, flat, errorHandler) {
            sam.parentTissueSampleClassId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
              return item.alias == flat.parentTissueSampleClassAlias;
            }, Constants.sampleClasses), 'id');
          },
          include: config.pageMode == 'propagate'
        }];
        if (Constants.isDetailedSample) {
          if (config.pageMode === 'propagate') {
            columns.splice.apply(columns, [0, 0].concat(parentColumns));
          } else if (config.pageMode === 'edit' && !isTargetIdentity(config)) {
            columns.splice.apply(columns, [2, 0].concat(parentColumns));
          }
        }
      }

      return columns;
    },

    getBulkActions: function(config) {
      return [
          {
            name: "Edit",
            action: function(samples) {

              if (samples.some(function(sample) {
                return sample.sampleClassId;
              }) && !Constants.isDetailedSample) {
                alert("There's detailed samples, but MISO is not configured for this.");
                return;
              }

              var classes = getSampleClasses(samples);
              var classesAliases = Utils.array.deduplicateString(classes.map(function(sampleClass) {
                return sampleClass.alias;
              }));
              if (classesAliases.length > 1) {
                alert("You have selected samples of classes " + classesAliases.join(" & ") + ". Please select samples from only one class.");
                return;
              }

              window.location = Urls.ui.samples.bulkEdit + "?" + jQuery.param({
                ids: samples.map(Utils.array.getId).join(',')
              });
            }

          },
          {
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
                      window.location = Urls.ui.samples.bulkPropagate + "?" + jQuery.param({
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
                      window.location = Urls.ui.libraries.bulkPropagate + "?" + jQuery.param(params);
                    }
                  });
                }

                if (targets.length == 0) {
                  alert("No propagation is possible from the samples.");
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
          },
          HotUtils.printAction('sample'),
          HotUtils.spreadsheetAction(Urls.rest.samples.spreadsheet, Constants.sampleSpreadsheets, function(samples, spreadsheet) {
            var errors = [];
            var invalidSamples = [];
            samples.forEach(function(sample) {
              if (!spreadsheet.sheet.allowedClasses.includes(getSampleClass(sample).sampleCategory)) {
                invalidSamples.push(sample);
              }
            })
            if (invalidSamples.length > 0) {
              errors.push("Error: Invalid sample class types");
              errors.push("Allowed types: " + spreadsheet.sheet.allowedClasses.join(", "));
              errors.push("Invalid samples:")
              invalidSamples.forEach(function(sample) {
                errors.push("* " + sample.alias + " (" + getSampleClass(sample).alias + ")");
              })
            }
            return errors;
          }),

          Constants.isDetailedSample ? HotUtils.makeParents(Urls.rest.samples.parents, HotUtils.relationCategoriesForDetailed()) : null,

          HotUtils.makeChildren(Urls.rest.samples.children, HotUtils.relationCategoriesForDetailed().concat(
              [HotUtils.relations.library(), HotUtils.relations.libraryAliquot(), HotUtils.relations.pool()]))].concat(
          HotUtils.makeQcActions("Sample")).concat(
          [
              config && config.worksetId ? HotUtils.makeRemoveFromWorkset('samples', Urls.rest.worksets.removeSamples(config.worksetId))
                  : HotUtils.makeAddToWorkset('samples', 'sampleIds', Urls.rest.worksets.addSamples),
              HotUtils.makeAttachFile('sample', function(sample) {
                return sample.projectId;
              }), HotUtils.makeTransferAction('sampleIds')]);
    },

    getCustomActions: function(table) {
      return HotTarget.boxable.getCustomActions(table);
    },

    confirmSave: function(flatObjects, create, config, table) {
      var deferred = jQuery.Deferred();
      if (!config.hasProject) {
        var changed = flatObjects.filter(function(sample) {
          return sample.originalProjectAlias && sample.projectAlias !== sample.originalProjectAlias;
        });
        var consentWarnings = changed.filter(function(sample) {
          return (sample.identityConsentLevel && sample.identityConsentLevel !== 'All Projects');
        }).map(
            function(sample) {
              return '• ' + (sample.alias || sample.parentAlias) + ' (Consent: ' + sample.identityConsentLevel + '): '
                  + sample.originalProjectAlias + ' → ' + sample.projectAlias;
            });
        var libraryWarnings = changed.filter(function(sample) {
          return sample.libraryCount > 0;
        }).map(
            function(sample) {
              return '• ' + (sample.alias || sample.parentAlias) + ': ' + sample.originalProjectAlias + ' → ' + sample.projectAlias + '; '
                  + sample.libraryCount + ' librar' + (sample.libraryCount > 1 ? 'ies' : 'y') + ' affected'
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
        return deferred.promise();
      }
    }
  };
})();
