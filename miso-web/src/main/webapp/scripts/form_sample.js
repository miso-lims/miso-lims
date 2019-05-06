if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.sample = (function($) {

  /*
   * Expected config {
   *   detailedSample: boolean,
   *   dnaseTreatable: boolean,
   *   generateSampleAliases: boolean
   * }
   */

  return {
    getSaveUrl: function(sample) {
      if (sample.id) {
        return '/miso/rest/sample/' + sample.id;
      } else {
        throw new Error('Page not intended for new sample creation');
      }
    },
    getSaveMethod: function(sample) {
      return sample.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(sample) {
      return '/miso/sample/' + sample.id;
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
                  type: 'read-only',
                  getDisplayValue: function(sample) {
                    if (sample.projectShortName) {
                      return sample.projectShortName + ' (' + sample.projectName + ')';
                    } else {
                      return sample.projectName + ': ' + sample.projectAlias;
                    }
                  },
                  getLink: function(sample) {
                    return '/miso/project/' + sample.projectId;
                  }
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
                  required: !config.generateSampleAliases,
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
                  type: 'date'
                }, {
                  title: 'Scientific Name',
                  data: 'scientificName',
                  type: 'text',
                  maxLength: 255,
                  required: true
                }, {
                  title: 'Accession',
                  data: 'accession',
                  type: 'read-only',
                  getLink: function(sample) {
                    return 'http://www.ebi.ac.uk/ena/data/view/' + sample.accession;
                  },
                  include: object.accession
                }, {
                  title: 'Sample Type',
                  data: 'sampleType',
                  type: 'dropdown',
                  getSource: function() {
                    return Constants.sampleTypes.sort();
                  },
                  getItemLabel: function(item) {
                    return item;
                  },
                  getItemValue: function(item) {
                    return item;
                  },
                  required: true
                }, FormUtils.makeQcPassedField(!config.detailedSample), {
                  title: 'QC Status',
                  data: 'detailedQcStatusId',
                  type: 'dropdown',
                  nullLabel: 'Not Ready',
                  getSource: function() {
                    return Constants.detailedQcStatuses.sort(Utils.sorting.standardSort('description'));
                  },
                  getItemLabel: function(item) {
                    return item.description;
                  },
                  getItemValue: function(item) {
                    return item.id;
                  },
                  required: false,
                  include: config.detailedSample,
                  onChange: function(newValue, updateField) {
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
                    updateField('detailedQcStatusNote', updates);
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
                  onChange: function(newValue, updateField) {
                    updateField('volume', {
                      disabled: newValue
                    });
                  }
                }, {
                  title: 'Volume',
                  data: 'volume',
                  type: 'decimal'
                }, {
                  title: 'Volume Units',
                  data: 'volumeUnits',
                  type: 'dropdown',
                  getSource: function() {
                    return Constants.volumeUnits;
                  },
                  getItemLabel: function(item) {
                    return decodeHtmlString(item.units);
                  },
                  getItemValue: function(item) {
                    return item.name;
                  },
                  required: true
                }, {
                  title: 'Concentration',
                  data: 'concentration',
                  type: 'decimal'
                }, {
                  title: 'Concentration Units',
                  data: 'concentrationUnits',
                  type: 'dropdown',
                  getSource: function() {
                    return Constants.concentrationUnits;
                  },
                  getItemLabel: function(item) {
                    return decodeHtmlString(item.units);
                  },
                  getItemValue: function(item) {
                    return item.name;
                  },
                  required: true
                }].concat(FormUtils.makeDistributionFields()).concat([{
              title: 'Location',
              data: 'locationBarcode',
              type: 'text',
              maxLength: 255
            }, FormUtils.makeBoxLocationField()])
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
              getSource: function() {
                return Constants.donorSexes;
              },
              initial: 'Unknown'
            }, {
              title: 'Consent',
              data: 'consentLevel',
              type: 'dropdown',
              required: true,
              getSource: function() {
                return Constants.consentLevels;
              },
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
                return sample.parentId ? '/miso/sample/' + sample.parentId : null;
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
              getSource: function() {
                return Constants.subprojects.filter(function(subproject) {
                  return subproject.parentProjectId === object.projectId;
                }).sort(Utils.sorting.standardSort('alias'));
              },
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
              getSource: function() {
                return Constants.tissueOrigins.sort(Utils.sorting.standardSortWithException('alias', 'nn'));
              },
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
              getSource: function() {
                return Constants.tissueTypes.sort(Utils.sorting.standardSortWithException('alias', 'n'));
              },
              getItemLabel: function(item) {
                var label = item.alias + ' (' + item.description + ')';
                if (label.length > 50) {
                  label = label.substring(0, 49) + '…)';
                }
                return label;
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
              required: true,
              min: 1
            }, {
              title: 'Tube Number',
              data: 'tubeNumber',
              type: 'int',
              required: true,
              min: 1
            }, {
              title: 'Tissue Material',
              data: 'tissueMaterialId',
              type: 'dropdown',
              nullLabel: 'Unknown',
              getSource: function() {
                return Constants.tissueMaterials;
              },
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
              getSource: function() {
                return Constants.labs.sort(Utils.sorting.standardSort('alias'));
              },
              getItemLabel: function(item) {
                return item.alias + ' (' + item.instituteAlias + ')';
              },
              getItemValue: function(item) {
                return item.id;
              }
            }]
          }, {
            title: 'Slide',
            include: config.detailedSample && object.sampleClassAlias === 'Slide',
            fields: [{
              title: 'Slides Remaining',
              data: 'slidesRemaining',
              type: 'read-only'
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
              getSource: function() {
                return Constants.stains.sort(Utils.sorting.standardSort('name'));
              },
              getItemLabel: function(item) {
                return item.name;
              },
              getItemValue: function(item) {
                return item.id;
              }
            }]
          }, {
            title: 'LCM Tube',
            include: config.detailedSample && object.sampleClassAlias === 'LCM Tube',
            fields: [{
              title: 'Slides Consumed',
              data: 'slidesConsumed',
              type: 'int',
              min: 0,
              required: true
            }]
          }, {
            title: 'Single Cell',
            include: config.detailedSample && object.sampleClassAlias === 'Single Cell',
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
              getSource: function() {
                return Constants.strStatuses;
              },
              required: true
            }, {
              title: 'DNAse Treated',
              data: 'dnaseTreated',
              type: 'checkbox',
              include: config.dnaseTreatable
            }, {
              title: 'Target Cell Recovery',
              data: 'targetCellRecovery',
              type: 'decimal',
              precision: 14,
              scale: 10,
              include: object.sampleClassAlias === 'Single Cell DNA (stock)'
            }, {
              title: 'Cell Viability',
              data: 'cellViability',
              type: 'decimal',
              precision: 14,
              scale: 10,
              include: object.sampleClassAlias === 'Single Cell DNA (stock)'
            }, {
              title: 'Loading Cell Concentration',
              data: 'loadingCellConcentration',
              type: 'decimal',
              precision: 14,
              scale: 10,
              include: object.sampleClassAlias === 'Single Cell DNA (stock)'
            }]
          }, {
            title: 'Aliquot',
            include: config.detailedSample && object.sampleCategory === 'Aliquot',
            fields: [{
              title: 'Purpose',
              data: 'samplePurposeId',
              type: 'dropdown',
              getSource: function() {
                return Constants.samplePurposes.sort(Utils.sorting.standardSort('alias'));
              },
              getItemLabel: function(item) {
                return item.alias;
              },
              getItemValue: function(item) {
                return item.id;
              },
              required: true
            }, {
              title: 'Input into Library',
              data: 'inputIntoLibrary',
              type: 'decimal',
              precision: 14,
              scale: 10,
              include: object.sampleClassAlias === 'Single Cell DNA (aliquot)'
            }]
          }];
    }
  }

  function decodeHtmlString(text) {
    var textarea = document.createElement('textarea');
    textarea.innerHTML = text;
    return textarea.value;
  }

})(jQuery);
