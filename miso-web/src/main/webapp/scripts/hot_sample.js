/**
 * Sample-specific Handsontable code
 */
HotTarget.sample = (function() {

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
  }

  return {

    createUrl: '/miso/rest/tree/sample/',
    updateUrl: '/miso/rest/tree/sample/',
    requestConfiguration: function(config, callback) {
      if (Constants.isDetailedSample) {
        config.rnaSamples = config.targetSampleClass.alias.indexOf("RNA") != -1;
      }
      callback(config);
    },

    fixUp: function(sam, errorHandler) {

    },

    createColumns: function(config, create, data) {
      var validationCache = {};
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
      } else if (config.create == true) {
        startProgression = 0;
      } else if (config.edit == true) {
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
      if (sourceCategory != 'Tissue Processing' && targetCategory != 'Tissue Processing') {
        show['Tissue Processing'] = false;
      }

      return [
          {
            header: 'Sample Name',
            data: 'name',
            readOnly: true,
            include: !config.isLibraryReceipt,
            unpackAfterSave: true,
            unpack: function(sam, flat, setCellMeta) {
              flat.name = sam.name || null;
            },
            pack: function(sam, flat, errorHandler) {
            }
          },
          {
            header: 'Sample Alias',
            data: 'alias',
            validator: function(value, callback) {
              (Constants.automaticSampleAlias ? HotUtils.validator.optionalTextNoSpecialChars
                  : HotUtils.validator.requiredTextNoSpecialChars)(value, function(result) {
                if (!result) {
                  callback(false);
                  return;
                }
                if (!value) {
                  return callback(Constants.automaticSampleAlias);
                }
                if (validationCache.hasOwnProperty(value)) {
                  return callback(validationCache[value]);
                }
                Fluxion.doAjax('sampleControllerHelperService', 'validateSampleAlias', {
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
            unpackAfterSave: true,
            unpack: function(sam, flat, setCellMeta) {
              validationCache[sam.alias] = true;
              flat.alias = sam.alias || null;
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
            header: 'Date of receipt',
            data: 'receivedDate',
            type: 'date',
            dateFormat: 'YYYY-MM-DD',
            datePickerConfig: {
              firstDay: 0,
              numberOfMonths: 1
            },
            allowEmpty: true,
            include: (!Constants.isDetailedSample || config.targetSampleClass.alias != 'Identity') && !config.isLibraryReceipt,
            unpack: function(sam, flat, setCellMeta) {
              flat.receivedDate = sam.receivedDate || null;
            },
            pack: function(sam, flat, errorHandler) {
              sam.receivedDate = flat.receivedDate;
            }
          },
          HotUtils.makeColumnForText('Matrix Barcode', !Constants.automaticBarcodes && !config.isLibraryReceipt, 'identificationBarcode', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),
          HotUtils.makeColumnForEnum('Sample Type', true, true, 'sampleType', Constants.sampleTypes, null),
          HotUtils.makeColumnForText('Sci. Name', true, 'scientificName', {
            validator: HotUtils.validator.requiredTextNoSpecialChars,
            unpack: function(obj, flat, setCellMeta) {
              flat.scientificName = obj.scientificName || config.defaultSciName;
            }
          }),
          {
            header: 'Project',
            data: 'projectAlias',
            type: (config.hasProject ? 'text' : 'dropdown'),
            source: (function() {
              if ((!config.projects || config.projects.length == 0) && config.create && !config.propagate && !config.hasProject) {
                /* projects list failed to generate when it should have, and we can't proceed. Notify the user. */
                var serverErrorMessages = document.getElementById('serverErrors');
                serverErrorMessages.innerHTML = '<p>Failed to generate list of projects. Please notify your MISO administrators.</p>';
                document.getElementById('errors').classList.remove('hidden');
                /* throw an error to keep the table from generating */
                throw 'Server error generating list of projects';
              }
              var comparator = Constants.isDetailedSample ? 'shortName' : 'id';
              var label = Constants.isDetailedSample ? 'shortName' : 'name';
              var projectLabels = (config.projects ? config.projects.sort(Utils.sorting.standardSort(comparator)).map(function(item) {
                return item[label];
              }) : []); /* use empty array if projects are not provided (should only happen during propagate or edit) */
              return projectLabels;
            })(),
            unpack: function(sam, flat, setCellMeta) {
              var label = Constants.isDetailedSample ? 'shortName' : 'name';
              if (config.hasProject) {
                flat.projectAlias = config.project[label] || null;
              } else {
                flat.projectAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                  return item.id == sam.projectId;
                }, config.projects), 'alias') || null;
              }
            },
            pack: function(sam, flat, errorHandler) {
              var label = Constants.isDetailedSample ? 'shortName' : 'name';
              if (config.hasProject) {
                sam.projectId = config.project.id;
              } else {
                sam.projectId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                  return item[label] == flat.projectAlias;
                }, config.projects), 'id');
              }
            },
            readOnly: config.hasProject,
            validator: HotUtils.validator.requiredAutocomplete,
            include: config.create
          },

          // Detailed Sample
          // parent columns
          HotUtils.makeColumnForText('Parent Alias', (Constants.isDetailedSample && config.propagate && !config.isLibraryReceipt),
              'parentAlias', {
                readOnly: true
              }),
          {
            header: 'Parent Sample Class',
            data: 'parentTissueSampleClassAlias',
            type: 'text',
            readOnly: true,
            unpack: function(sam, flat, setCellMeta) {
              flat.parentTissueSampleClassAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                return item.id == sam.parentTissueSampleClassId;
              }, Constants.sampleClasses), 'alias') || null;
            },
            pack: function(sam, flat, errorHandler) {
              sam.parentTissueSampleClassId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                return item.alias == flat.parentTissueSampleClassAlias;
              }, Constants.sampleClasses), 'id');
            },
            include: Constants.isDetailedSample && config.propagate && !config.isLibraryReceipt
          },

          // Identity columns
          {
            header: 'External Name',
            data: 'externalName',
            validator: HotUtils.validator.requiredTextNoSpecialChars,
            include: show['Identity'],
            unpack: function(sam, flat, setCellMeta) {
              flat.externalName = sam.externalName || null;
            },
            pack: function(sam, flat, errorHandler) {
              if (!getSelectedIdentity(flat)) {
                sam.externalName = flat.externalName;
              } // else externalName will come from an existing Identity via the Identity Alias column
            }
          },
          {
            header: 'Identity Alias',
            data: 'identityAlias',
            type: 'dropdown',
            trimDropdown: false,
            strict: true,
            source: [],
            validator: HotUtils.validator.requiredAutocomplete,
            include: show['Identity'] && config.targetSampleClass.alias != 'Identity'
                && config.targetSampleClass.sampleCategory != 'Identity',
            depends: 'externalName',
            update: function(sam, flat, value, setReadOnly, setOptions, setData) {
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
                getIdentities(HotUtils.counter);
              }

              function getIdentities(requestCounter) {
                jQuery.ajax({
                  url: "/miso/rest/tree/identities",
                  data: JSON.stringify({
                    "identitiesSearches": flat.externalName,
                    "requestCounter": requestCounter
                  }),
                  contentType: "application/json; charset=utf8",
                  dataType: "json",
                  type: "POST"
                }).success(
                    function(data) {
                      // make sure the counter lines up with the one from
                      // the original request
                      if (data.requestCounter == requestCounter) {
                        var potentialIdentities = [];

                        if (selectedProject == null) {
                          // let the user know they need to select a project
                          setData("Select a project");
                        }
                        // sort with identities from selected project on top
                        var identitiesSources = [];
                        if (data.matchingIdentities.length > 0) {
                          data.matchingIdentities.sort(function(a, b) {
                            var aSortId = a.projectId == selectedProject.id ? 0 : a.projectId;
                            var bSortId = b.projectId == selectedProject.id ? 0 : b.projectId;
                            return aSortId - bSortId;
                          })
                          potentialIdentities = data.matchingIdentities;
                          for (var i = 0; i < potentialIdentities.length; i++) {
                            var identityLabel = potentialIdentities[i].alias + " -- " + potentialIdentities[i].externalName;
                            potentialIdentities[i].label = identityLabel;
                            identitiesSources.push(identityLabel);
                          }
                        }

                        var indexOfMatchingIdentityInProject = -1;
                        for (i = 0; i < data.matchingIdentities.length; i++) {
                          if (data.matchingIdentities[i].projectId == selectedProject.id
                              && data.matchingIdentities[i].externalName == flat.externalName) {
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
                        requestCounter++;
                        flat.potentialIdentities = potentialIdentities;
                        var cellOptions = {
                          'source': identitiesSources,
                          'renderer': (identitiesSources.length > 1 ? HotUtils.multipleOptionsRenderer : Handsontable.AutocompleteRenderer)
                        };
                        setOptions(cellOptions);
                      }
                    }).fail(function(response, textStatus, serverStatus) {
                  HotUtils.showServerErrors(response, serverStatus);
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
          HotUtils.makeColumnForEnum('&nbsp;&nbsp;Donor Sex&nbsp;&nbsp;', show['Identity'], true, 'donorSex', Constants.donorSexes,
              'Unknown'),

          // Detailed sample columns
          {
            header: 'Sample Class',
            data: 'sampleClassAlias',
            type: 'text',
            readOnly: true,
            unpack: function(sam, flat, setCellMeta) {
              flat.sampleClassAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                return item.id == sam.sampleClassId;
              }, Constants.sampleClasses), 'alias') || null;
            },
            pack: function(sam, flat, errorHandler) {
              sam.sampleClassId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
                return item.alias == flat.sampleClassAlias;
              }, Constants.sampleClasses), 'id');
            },
            include: Constants.isDetailedSample
          },
          HotUtils.makeColumnForText('Group ID', Constants.isDetailedSample && !config.isLibraryReceipt, 'groupId', {
            validator: HotUtils.validator.optionalTextAlphanumeric
          }),
          HotUtils.makeColumnForText('Group Desc.', Constants.isDetailedSample && !config.isLibraryReceipt, 'groupDescription', {}),

          // Tissue columns
          HotUtils.makeColumnForConstantsList('Tissue Origin', show['Tissue'], 'tissueOriginAlias', 'tissueOriginId', 'id', 'label',
              Constants.tissueOrigins, true),
          HotUtils.makeColumnForConstantsList('Tissue Type', show['Tissue'], 'tissueTypeAlias', 'tissueTypeId', 'id', 'label',
              Constants.tissueTypes, true),
          HotUtils.makeColumnForInt('Passage #', show['Tissue'], 'passageNumber', null),
          HotUtils.makeColumnForInt('Times Received', show['Tissue'], 'timesReceived', HotUtils.validator.requiredNumber),
          HotUtils.makeColumnForInt('Tube Number', show['Tissue'], 'tubeNumber', HotUtils.validator.requiredNumber),
          HotUtils.makeColumnForConstantsList('Lab', show['Tissue'] && !config.isLibraryReceipt, 'labComposite', 'labId', 'id', 'label',
              Constants.labs, false),
          HotUtils.makeColumnForText('Secondary ID', show['Tissue'] && !config.isLibraryReceipt, 'secondaryIdentifier', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),
          HotUtils.makeColumnForConstantsList('Material', show['Tissue'], 'tissueMaterialAlias', 'tissueMaterialId', 'id', 'alias',
              Constants.tissueMaterials, false),
          HotUtils.makeColumnForText('Region', show['Tissue'], 'region', {
            validator: HotUtils.validator.optionalTextNoSpecialChars
          }),

          // Tissue Processing: Slides columns
          HotUtils.makeColumnForInt('Slides', (show['Tissue Processing'] && config.targetSampleClass.alias == 'Slide'), 'slides',
              HotUtils.validator.requiredNumber),
          HotUtils.makeColumnForInt('Discards', (show['Tissue Processing'] && config.targetSampleClass.alias == 'Slide'), 'discards',
              HotUtils.validator.requiredNumber),
          HotUtils.makeColumnForInt('Thickness', (show['Tissue Processing'] && config.targetSampleClass.alias == 'Slide'), 'thickness',
              null),
          {
            header: 'Stain',
            data: 'stainName',
            type: 'dropdown',
            trimDropdown: false,
            source: function() {
              var stains = Constants.stains.sort(function(a, b) {
                return (a.category || '').localeCompare(b.category || '');
              }).map(function(s) {
                return s.name;
              });
              stains.unshift('(None)');
              return stains;
            }(),
            validator: Handsontable.AutocompleteValidator,
            unpack: function(sam, flat, setCellMeta) {
              if (sam.stain) {
                flat.stainName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(sam.stain.id),
                    Constants.stains), 'name')
                    || null;
              } else {
                flat.stainName = '(None)';
              }
            },
            pack: function(sam, flat, errorHandler) {
              sam.stain = Utils.array.findFirstOrNull(Utils.array.namePredicate(flat.stainName), Constants.stains);
            },
            include: show['Tissue Processing'] && config.targetSampleClass.alias == 'Slide'
          },

          // Tissue Processing: LCM Tube columns
          HotUtils.makeColumnForInt('Slides Consumed', (show['Tissue Processing'] && config.targetSampleClass.alias == 'LCM Tube'),
              'slidesConsumed', HotUtils.validator.requiredNumber),

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
          HotUtils.makeColumnForFloat('Vol. (&#181;l)', ((show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt), 'volume'),
          HotUtils.makeColumnForFloat('Conc. (ng/&#181;l)', ((show['Stock'] || show['Aliquot']) && !config.isLibraryReceipt),
              'concentration'),

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
              if (config.edit) {
                flat.detailedQcStatusDescription = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                    .idPredicate(sam.detailedQcStatusId), Constants.detailedQcStatuses), 'description')
                    || 'Not Ready';
              } else {
                flat.detailedQcStatusDescription = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                    .idPredicate(sam.detailedQcStatusId), Constants.detailedQcStatuses), 'description')
                    || null;
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
            update: function(sam, flat, value, setReadOnly, setOptions, setData) {
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
                  'validator': Handsontable.TextValidator
                })
              }
            }
          }),

          // Aliquot columns
          HotUtils.makeColumnForConstantsList('Purpose', show['Aliquot'] && !config.isLibraryReceipt, 'samplePurposeAlias',
              'samplePurposeId', 'id', 'alias', Constants.samplePurposes, true)];
    },

    bulkActions: [
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
            var categories = Utils.array.deduplicateString(classes.map(function(sampleClass) {
              return sampleClass.sampleCategory;
            }));
            if (categories.length > 1) {
              alert("You have selected samples of categories " + categories.join(" & ") + ". Please select samples from only one category.");
              return;
            }

            if (categories[0] == 'Tissue Processing' && classes.length > 1) {
              alert("You have selected samples of classes " + classes.map(Utils.array.getAlias).join(" & ")
                  + ". Please select samples from only one tissue processing class.");
              return;
            }

            window.location = "/miso/sample/bulk/edit?" + jQuery.param({
              ids: samples.map(Utils.array.getId).join(',')
            });
          }

        }, {
          name: "Propagate",
          action: function(samples) {
            var idsString = samples.map(Utils.array.getId).join(",");
            var classes = getSampleClasses(samples);

            // In the case of plain samples, this will be empty, which is fine.
            var targets = getChildSampleClasses(classes).sort(Utils.sorting.sampleClassComparator).map(function(sampleClass) {

              return {
                name: sampleClass.alias,
                action: function(replicates) {
                  window.location = "/miso/sample/bulk/propagate?" + jQuery.param({
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
                action: function(replicates) {
                  window.location = "/miso/library/bulk/propagate?" + jQuery.param({
                    ids: idsString,
                    replicates: replicates
                  });
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
              value: 1
            }, targets.length > 1 ? {
              property: 'target',
              type: 'select',
              label: 'To',
              values: targets,
              getLabel: Utils.array.getName
            } : null].filter(function(x) {
              return !!x;
            }), function(result) {
              (result.target || targets[0]).action(result.replicates);
            });
          }
        }, HotUtils.printAction('sample'), ].concat(HotUtils.makeQcActions("Sample"))
  };
})();
