if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.sample = (function($) {

  /*
   * Expected config {
   *   detailedSample: boolean,
   *   dnaseTreatable: boolean,
   *   projects: array
   * }
   */

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('samples');
    },
    getSaveUrl: function(sample) {
      if (sample.id) {
        return Urls.rest.samples.update(sample.id);
      } else {
        throw new Error('Page not intended for new sample creation');
      }
    },
    getSaveMethod: function(sample) {
      return sample.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(sample) {
      return Urls.ui.samples.edit(sample.id);
    },
    getSections: function(config, object) {
      return [
          {
            title: 'Sample Information',
            fields: [
                {
                  title: 'Sample ID',
                  data: 'id',
                  type: 'read-only',
                  getDisplayValue: function(sample) {
                    return sample.id;
                  }
                },
                {
                  title: 'Project',
                  data: 'projectId',
                  type: 'dropdown',
                  required: true,
                  source: config.projects.filter(function(project) {
                    return project.status === 'Active' || project.id === object.projectId;
                  }),
                  getItemLabel: function(item) {
                    return Constants.isDetailedSample ? item.shortName : item.name;
                  },
                  getItemValue: Utils.array.getId,
                  sortSource: Utils.sorting.standardSort(Constants.isDetailedSample ? 'shortName' : 'id')
                },
                {
                  title: 'Name',
                  data: 'name',
                  type: 'read-only',
                  getDisplayValue: function(sample) {
                    return sample.name;
                  }
                },
                {
                  title: 'Alias',
                  data: 'alias',
                  type: 'text',
                  required: true,
                  maxLength: 100,
                  note: config.detailedSample && object.nonStandardAlias
                      ? 'Double-check this alias -- it will be saved even if it is duplicated or does not follow the naming standard!'
                      : null
                }, {
                  title: 'Description',
                  data: 'description',
                  type: 'text',
                  maxLength: 255
                }, {
                  title: 'Matrix Barcode',
                  data: 'identificationBarcode',
                  type: 'text',
                  maxLength: 255
                }, {
                  title: 'Date of Receipt',
                  data: 'receivedDate',
                  type: 'date',
                  include: !config.detailedSample || object.sampleCategory !== 'Identity'
                }, {
                  title: 'Requisition ID',
                  data: 'requisitionId',
                  type: 'text',
                  maxLength: 50,
                  regex: Utils.validation.alphanumRegex,
                  include: !config.detailedSample || object.sampleCategory !== 'Identity'
                }, {
                  title: 'Scientific Name',
                  data: 'scientificNameId',
                  type: 'dropdown',
                  required: true,
                  source: Constants.scientificNames,
                  sortSource: Utils.sorting.standardSort('alias'),
                  getItemLabel: Utils.array.getAlias,
                  getItemValue: Utils.array.getId
                }, {
                  title: 'Accession',
                  data: 'accession',
                  type: 'read-only',
                  getLink: function(sample) {
                    return Urls.external.enaAccession(sample.accession);
                  },
                  include: object.accession
                }, {
                  title: 'Sample Type',
                  data: 'sampleType',
                  type: 'dropdown',
                  source: Constants.sampleTypes.sort(),
                  getItemLabel: function(item) {
                    return item;
                  },
                  getItemValue: function(item) {
                    return item;
                  },
                  required: true
                }, FormUtils.makeQcPassedField(!config.detailedSample), { // TODO: sort as on bulk page
                  title: 'QC Status',
                  data: 'detailedQcStatusId',
                  type: 'dropdown',
                  nullLabel: 'Not Ready',
                  source: Constants.detailedQcStatuses.sort(Utils.sorting.standardSort('description')),
                  getItemLabel: function(item) {
                    return item.description;
                  },
                  getItemValue: function(item) {
                    return item.id;
                  },
                  required: false,
                  include: config.detailedSample,
                  onChange: function(newValue, form) {
                    var noteRequired = newValue ? Utils.array.findUniqueOrThrow(function(item) {
                      return item.id === Number(newValue);
                    }, Constants.detailedQcStatuses).noteRequired : false;
                    var updates = {
                      disabled: !noteRequired,
                      required: noteRequired
                    }
                    if (!noteRequired) {
                      updates.value = null;
                    }
                    form.updateField('detailedQcStatusNote', updates);
                  }
                }, {
                  title: 'QC Status Note',
                  data: 'detailedQcStatusNote',
                  type: 'text',
                  maxLength: 500,
                  include: config.detailedSample
                }, {
                  title: 'Discarded',
                  data: 'discarded',
                  type: 'checkbox',
                  onChange: function(newValue, form) {
                    form.updateField('volume', {
                      disabled: newValue
                    });
                  }
                }, {
                  title: 'Initial Volume',
                  data: 'initialVolume',
                  type: 'decimal',
                  precision: 14,
                  scale: 10
                }, {
                  title: 'Volume',
                  data: 'volume',
                  type: 'decimal',
                  precision: 14,
                  scale: 10
                }, FormUtils.makeUnitsField(object, 'volume'), {
                  title: 'Parent ng Used',
                  data: 'ngUsed',
                  type: 'decimal',
                  precision: 14,
                  scale: 10,
                  include: config.detailedSample && (object.sampleCategory === 'Stock' || object.sampleCategory === 'Aliquot')
                }, {
                  title: 'Parent Volume Used',
                  data: 'volumeUsed',
                  type: 'decimal',
                  precision: 14,
                  scale: 10,
                  include: config.detailedSample && (object.sampleCategory === 'Stock' || object.sampleCategory === 'Aliquot')
                }, {
                  title: 'Concentration',
                  data: 'concentration',
                  type: 'decimal',
                  precision: 14,
                  scale: 10
                }, FormUtils.makeUnitsField(object, 'concentration'), {
                  title: 'Location',
                  data: 'locationBarcode',
                  type: 'text',
                  maxLength: 255
                }, FormUtils.makeBoxLocationField(), {
                  title: 'Sequencing Control Type',
                  data: 'sequencingControlTypeId',
                  type: 'dropdown',
                  source: Constants.sequencingControlTypes,
                  sortSource: Utils.sorting.standardSort('alias'),
                  getItemLabel: Utils.array.getAlias,
                  getItemValue: Utils.array.getId,
                  nullLabel: 'n/a',
                  include: !Constants.isDetailedSample || object.sampleCategory === 'Aliquot'
                }]
          }, {
            title: 'Identity',
            include: config.detailedSample && object.sampleCategory === 'Identity',
            fields: [{
              title: 'External Names (comma separated)',
              data: 'externalName',
              type: 'text',
              required: true,
              maxLength: 255
            }, {
              title: 'Sex',
              data: 'donorSex',
              type: 'dropdown',
              required: true,
              source: Constants.donorSexes,
              initial: 'Unknown'
            }, {
              title: 'Consent',
              data: 'consentLevel',
              type: 'dropdown',
              required: true,
              source: Constants.consentLevels,
              initial: 'This Project'
            }]
          }, {
            title: 'Details',
            include: config.detailedSample,
            fields: [{
              title: 'Parent',
              data: 'parentId',
              type: 'read-only',
              getDisplayValue: function(sample) {
                return sample.parentAlias || 'n/a';
              },
              getLink: function(sample) {
                return sample.parentId ? Urls.ui.samples.edit(sample.parentId) : null;
              }
            }, {
              title: 'Sample Class',
              data: 'sampleClassId',
              type: 'read-only',
              getDisplayValue: function(sample) {
                return sample.sampleClassAlias;
              }
            }, {
              title: 'Sub-project',
              data: 'subprojectId',
              type: 'dropdown',
              source: Constants.subprojects.filter(function(subproject) {
                return subproject.parentProjectId === object.projectId;
              }).sort(Utils.sorting.standardSort('alias')),
              getItemLabel: function(item) {
                return item.alias;
              },
              getItemValue: function(item) {
                return item.id;
              }
            }, {
              title: 'Effective External Names',
              data: 'effectiveExternalNames',
              type: 'read-only',
              include: object.sampleCategory !== 'Identity'
            }, {
              title: 'Effective Group ID',
              data: 'effectiveGroupId',
              type: 'read-only',
              getDisplayValue: function(sample) {
                if (sample.hasOwnProperty('effectiveGroupId') && sample.effectiveGroupId !== null) {
                  return sample.effectiveGroupId + ' (' + sample.effectiveGroupIdSample + ')';
                } else {
                  return 'None';
                }
              },
              include: object.sampleCategory !== 'Identity'
            }, {
              title: 'Group ID',
              data: 'groupId',
              type: 'text',
              maxLength: 100,
              regex: Utils.validation.alphanumRegex
            }, {
              title: 'Group Description',
              data: 'groupDescription',
              type: 'text',
              maxLength: 255
            }, {
              title: 'Date of Creation',
              data: 'creationDate',
              type: 'date'
            }]
          }, {
            title: 'Tissue',
            include: config.detailedSample && object.sampleCategory === 'Tissue',
            fields: [{
              title: 'Tissue Origin',
              data: 'tissueOriginId',
              type: 'dropdown',
              source: Constants.tissueOrigins.sort(Utils.sorting.standardSortWithException('alias', 'nn')),
              getItemLabel: function(item) {
                return item.alias + ' (' + item.description + ')';
              },
              getItemValue: function(item) {
                return item.id;
              },
              required: true
            }, {
              title: 'Tissue Type',
              data: 'tissueTypeId',
              type: 'dropdown',
              source: Constants.tissueTypes.sort(Utils.sorting.standardSortWithException('alias', 'n')),
              getItemLabel: function(item) {
                return item.label;
              },
              getItemValue: function(item) {
                return item.id;
              },
              required: true
            }, {
              title: 'Passage Number',
              data: 'passageNumber',
              type: 'int',
              min: 1
            }, {
              title: 'Times Received',
              data: 'timesReceived',
              type: 'int',
              min: 1
            }, {
              title: 'Tube Number',
              data: 'tubeNumber',
              type: 'int',
              min: 1
            }, {
              title: 'Tissue Material',
              data: 'tissueMaterialId',
              type: 'dropdown',
              nullLabel: 'Unknown',
              source: Constants.tissueMaterials,
              getItemLabel: function(item) {
                return item.alias;
              },
              getItemValue: function(item) {
                return item.id;
              }
            }, {
              title: 'Region',
              data: 'region',
              type: 'text',
              maxLength: 255
            }, {
              title: 'Secondary Identifier',
              data: 'secondaryIdentifier',
              type: 'text',
              maxLength: 255
            }, {
              title: 'Lab',
              data: 'labId',
              type: 'dropdown',
              nullLabel: 'n/a',
              source: Constants.labs.sort(Utils.sorting.standardSort('alias')),
              getItemLabel: function(item) {
                return item.label;
              },
              getItemValue: function(item) {
                return item.id;
              },
              include: !!object.lab
            }]
          }, {
            title: 'Slide',
            include: config.detailedSample && object.sampleSubcategory === 'Slide',
            fields: [{
              title: 'Initial Slides',
              data: 'initialSlides',
              type: 'int',
              min: 0,
              required: true
            }, {
              title: 'Slides',
              data: 'slides',
              type: 'int',
              min: 0,
              required: true
            }, {
              title: 'Discards',
              data: 'discards',
              type: 'int',
              min: 0,
              required: true
            }, {
              title: 'Thickness (µm)',
              data: 'thickness',
              type: 'int',
              min: 1
            }, {
              title: 'Stain',
              data: 'stainId',
              type: 'dropdown',
              source: Constants.stains.sort(Utils.sorting.standardSort('name')),
              getItemLabel: function(item) {
                return item.name;
              },
              getItemValue: function(item) {
                return item.id;
              }
            }, {
              title: '% Tumour',
              data: 'percentTumour',
              type: 'decimal',
              precision: 11,
              scale: 8,
              min: 0,
              max: 100
            }, {
              title: '% Necrosis',
              data: 'percentNecrosis',
              type: 'decimal',
              precision: 11,
              scale: 8,
              min: 0,
              max: 100
            }, {
              title: 'Marked Area (mm²)',
              data: 'markedArea',
              type: 'decimal',
              precision: 11,
              scale: 8,
              min: 0
            }, {
              title: 'Marked Area % Tumour',
              data: 'markedAreaPercentTumour',
              type: 'decimal',
              precision: 11,
              scale: 8,
              min: 0,
              max: 100
            }]
          }, { // TODO: piece type field missing
            title: 'Tissue Pieces',
            include: config.detailedSample && object.sampleSubcategory === 'Tissue Piece',
            fields: [{
              title: 'Slides Consumed',
              data: 'slidesConsumed',
              type: 'int',
              min: 0,
              required: true
            }, {
              title: 'Reference Slide',
              data: 'referenceSlideId',
              type: 'dropdown',
              source: object.relatedSlides,
              getItemLabel: function(item) {
                return item.name + ' (' + item.alias + ')';
              },
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort('id')
            }]
          }, {
            title: 'Single Cell',
            include: config.detailedSample && object.sampleSubcategory === 'Single Cell',
            fields: [{
              title: 'Initial Cell Concentration',
              data: 'initialCellConcentration',
              type: 'decimal',
              precision: 14,
              scale: 10
            }, {
              title: 'Digestion',
              data: 'digestion',
              type: 'text',
              maxLength: 255,
              required: true
            }]
          }, {
            title: 'Stock',
            include: config.detailedSample && object.sampleCategory === 'Stock',
            fields: [{
              title: 'STR Status',
              data: 'strStatus',
              type: 'dropdown',
              source: Constants.strStatuses,
              required: true
            }, {
              title: 'DNAse Treated',
              data: 'dnaseTreated',
              type: 'checkbox',
              include: config.dnaseTreatable,
              required: true
            }, {
              title: 'Reference Slide',
              data: 'referenceSlideId',
              type: 'dropdown',
              source: object.relatedSlides,
              getItemLabel: function(item) {
                return item.name + ' (' + item.alias + ')';
              },
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort('id')
            }, {
              title: 'Target Cell Recovery',
              data: 'targetCellRecovery',
              type: 'decimal',
              precision: 14,
              scale: 10,
              include: object.sampleSubcategory === 'Single Cell (stock)'
            }, {
              title: 'Cell Viability',
              data: 'cellViability',
              type: 'decimal',
              precision: 14,
              scale: 10,
              include: object.sampleSubcategory === 'Single Cell (stock)'
            }, {
              title: 'Loading Cell Concentration',
              data: 'loadingCellConcentration',
              type: 'decimal',
              precision: 14,
              scale: 10,
              include: object.sampleSubcategory === 'Single Cell (stock)'
            }]
          }, {
            title: 'Aliquot',
            include: config.detailedSample && object.sampleCategory === 'Aliquot',
            fields: [{
              title: 'Purpose',
              data: 'samplePurposeId',
              type: 'dropdown',
              source: Constants.samplePurposes.sort(Utils.sorting.standardSort('alias')),
              getItemLabel: function(item) {
                return item.alias;
              },
              getItemValue: function(item) {
                return item.id;
              }
            }, {
              title: 'Input into Library',
              data: 'inputIntoLibrary',
              type: 'decimal',
              precision: 14,
              scale: 10,
              include: object.sampleSubcategory === 'Single Cell (aliquot)'
            }]
          }];
    },
    confirmSave: function(object, saveCallback, isDialog, form) {
      if (form.isChanged('projectId')) {
        var messages = [];
        if (object.identityConsentLevel && object.identityConsentLevel !== 'All Projects') {
          messages.push('• Identity consent level is set to ' + object.identityConsentLevel);
        }
        if (object.libraryCount > 0) {
          messages.push('• ' + object.libraryCount + ' existing librar' + (object.libraryCount > 1 ? 'ies' : 'y') + ' will be affected');
        }
        if (messages.length) {
          messages.unshift('Are you sure you wish to transfer the sample to a different project?');
          Utils.showConfirmDialog('Confirm', 'Save', messages, saveCallback);
          return;
        }
      }
      saveCallback();
    }
  }

  function decodeHtmlString(text) {
    var textarea = document.createElement('textarea');
    textarea.innerHTML = text;
    return textarea.value;
  }

})(jQuery);
