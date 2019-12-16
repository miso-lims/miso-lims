if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.library = (function($) {

  /*
   * Expected config {
   *   detailedSample: boolean,
   *   generateLibraryAliases: boolean
   * }
   */

  return {
    getSaveUrl: function(library) {
      if (library.id) {
        return '/miso/rest/libraries/' + library.id;
      } else {
        throw new Error('Page not intended for new library creation');
      }
    },
    getSaveMethod: function(library) {
      return 'PUT';
    },
    getEditUrl: function(library) {
      return '/miso/library/' + library.id;
    },
    getSections: function(config, object) {
      var platformName = Utils.array.findUniqueOrThrow(function(item) {
        return item.key === object.platformType;
      }, Constants.platformTypes).name;

      return [{
        title: 'Library Information',
        fields: [{
          title: 'Library ID',
          data: 'id',
          type: 'read-only'
        }, {
          title: 'Name',
          data: 'name',
          type: 'read-only'
        }, {
          title: 'Parent Sample',
          data: 'parentSampleAlias',
          type: 'read-only',
          getLink: function(library) {
            return '/miso/sample/' + library.parentSampleId;
          }
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 100
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
          title: 'Creation Date',
          data: 'creationDate',
          type: 'date'
        }, {
          title: 'Accession',
          data: 'accession',
          type: 'read-only',
          getLink: function(library) {
            return 'http://www.ebi.ac.uk/ena/data/view/' + library.accession;
          },
          include: object.accession
        }, {
          title: 'Paired',
          data: 'paired',
          type: 'checkbox',
          include: !config.detailedSample
        }, {
          title: 'Platform',
          data: 'platformType',
          type: 'dropdown',
          required: true,
          getSource: function() {
            return Constants.platformTypes.filter(function(platformType) {
              return platformType.active;
            });
          },
          sortSource: Utils.sorting.standardSort('key'),
          getItemLabel: function(item) {
            return item.key;
          },
          getItemValue: function(item) {
            return item.key;
          },
          onChange: function(newValue, form) {
            var name = Utils.array.findUniqueOrThrow(function(item) {
              return item.key === newValue;
            }, Constants.platformTypes).name;
            form.updateField('libraryTypeId', {
              source: Constants.libraryTypes.filter(function(item) {
                return item.platform === name;
              })
            });
            form.updateField('indexFamilyId', {
              source: Constants.indexFamilies.filter(function(item) {
                return item.platformType === name;
              })
            });
          }
        }, {
          title: 'Library Type',
          data: 'libraryTypeId',
          type: 'dropdown',
          required: true,
          getSource: function() {
            return Constants.libraryTypes.filter(function(libraryType) {
              return libraryType.platform === platformName;
            });
          },
          sortSource: Utils.sorting.standardSort('description'),
          getItemLabel: function(item) {
            return item.description;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Library Design',
          data: 'libraryDesignId',
          type: 'dropdown',
          include: config.detailedSample,
          getSource: function() {
            return Constants.libraryDesigns.filter(function(design) {
              return design.sampleClassId === object.parentSampleClassId;
            });
          },
          sortSource: Utils.sorting.standardSort('name'),
          getItemLabel: function(item) {
            return item.name;
          },
          getItemValue: function(item) {
            return item.id;
          },
          onChange: function(newValue, form) {
            if (newValue) {
              var design = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(newValue), Constants.libraryDesigns);
              form.updateField('libraryDesignCodeId', {
                disabled: true,
                value: design.designCodeId
              });
              form.updateField('librarySelectionTypeId', {
                disabled: true,
                value: design.selectionId
              });
              form.updateField('libraryStrategyTypeId', {
                disabled: true,
                value: design.strategyId
              });
            } else {
              form.updateField('libraryDesignCodeId', {
                disabled: false
              });
              form.updateField('librarySelectionTypeId', {
                disabled: false
              });
              form.updateField('libraryStrategyTypeId', {
                disabled: false
              });
            }
          }
        }, {
          title: 'Design Code',
          data: 'libraryDesignCodeId',
          type: 'dropdown',
          include: config.detailedSample,
          required: true,
          getSource: function() {
            return Constants.libraryDesignCodes;
          },
          sortSource: Utils.sorting.standardSort('code'),
          getItemLabel: function(item) {
            return item.code + ' (' + item.description + ')';
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Library Selection Type',
          data: 'librarySelectionTypeId',
          type: 'dropdown',
          getSource: function() {
            return Constants.librarySelections;
          },
          sortSource: Utils.sorting.standardSort('name'),
          getItemLabel: function(item) {
            return item.name;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Library Strategy Type',
          data: 'libraryStrategyTypeId',
          type: 'dropdown',
          getSource: function() {
            return Constants.libraryStrategies;
          },
          sortSource: Utils.sorting.standardSort('name'),
          getItemLabel: function(item) {
            return item.name;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Index Family',
          data: 'indexFamilyId',
          type: 'dropdown',
          nullLabel: 'No indices',
          getSource: function() {
            return Constants.indexFamilies.filter(function(family) {
              return family.platformType === platformName;
            });
          },
          sortSource: Utils.sorting.standardSort('name'),
          getItemLabel: function(item) {
            return item.name;
          },
          getItemValue: function(item) {
            return item.id;
          },
          onChange: function(newValue, form) {
            if (!newValue) {
              form.updateField('index2Id', {
                source: [],
                disabled: true
              });
              form.updateField('index1Id', {
                source: [],
                disabled: true,
                required: false
              });
            } else {
              // update index 2 dropdown before index 1 because index 1 may effect index 2
              var indices2 = getIndices(newValue, 2);
              form.updateField('index2Id', {
                source: indices2,
                disabled: !indices2 || !indices2.length
              });
              form.updateField('index1Id', {
                source: getIndices(newValue, 1),
                disabled: false,
                required: true
              });
            }
          }
        }, makeIndexColumn(object, 1), makeIndexColumn(object, 2), {
          title: 'Has UMIs',
          data: 'umis',
          type: 'checkbox'
        }, FormUtils.makeQcPassedField(true), {
          title: 'Low Quality Sequencing',
          data: 'lowQuality',
          type: 'checkbox',
        }, {
          title: 'Size (bp)',
          data: 'dnaSize',
          type: 'int',
          maxLength: 10,
          min: 1
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
          scale: 10
        }, {
          title: 'Parent Volume Used',
          data: 'volumeUsed',
          type: 'decimal',
          precision: 14,
          scale: 10
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
          title: 'Library Kit',
          data: 'kitDescriptorId',
          type: 'dropdown',
          required: true,
          getSource: function() {
            return Constants.kitDescriptors.filter(function(kit) {
              return kit.kitType === 'Library';
            });
          },
          sortSource: Utils.sorting.standardSort('name'),
          getItemLabel: function(item) {
            return item.name;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Spike-In',
          data: 'spikeInId',
          type: 'dropdown',
          getSource: function() {
            return Constants.spikeIns;
          },
          sortSource: Utils.sorting.standardSort('alias'),
          getItemLabel: function(item) {
            return item.alias;
          },
          getItemValue: function(item) {
            return item.id;
          },
          onChange: function(newValue, form) {
            var options = {
              disabled: !newValue,
              required: !!newValue
            }
            if (!newValue) {
              options.value = '';
            }
            form.updateField('spikeInDilutionFactor', options);
            form.updateField('spikeInVolume', options);
          }
        }, {
          title: 'Spike-In Dilution Factor',
          data: 'spikeInDilutionFactor',
          type: 'dropdown',
          getSource: function() {
            return Constants.dilutionFactors;
          },
          sortSource: function(a, b) {
            return a.length - b.length;
          },
          nullLabel: 'n/a'
        }, {
          title: 'Spike-In Volume',
          data: 'spikeInVolume',
          type: 'decimal',
          precision: 14,
          scale: 10
        }]
      }, {
        title: 'Details',
        include: config.detailedSample,
        fields: [{
          title: 'External Names',
          data: 'effectiveExternalNames',
          type: 'read-only'
        }, {
          title: 'Effective Group ID',
          data: 'effectiveGroupId',
          type: 'read-only',
          getDisplayValue: function(library) {
            if (library.hasOwnProperty('effectiveGroupId') && library.effectiveGroupId !== null) {
              return library.effectiveGroupId + ' (' + library.effectiveGroupIdSample + ')';
            } else {
              return 'None';
            }
          }
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
          title: 'Archived',
          data: 'archived',
          type: 'checkbox'
        }]
      }];
    }
  }

  function makeIndexColumn(library, position) {
    var field = {
      title: 'Index ' + position,
      data: 'index' + position + 'Id',
      type: 'dropdown',
      getSource: function() {
        return getIndices(library.indexFamilyId, position);
      },
      sortSource: Utils.sorting.standardSort('label'),
      getItemLabel: function(item) {
        return item.label;
      },
      getItemValue: function(item) {
        return item.id
      }
    }
    if (position === 1) {
      field.onChange = function(newValue, form) {
        var indexFamilyId = form.get('indexFamilyId');
        if (!indexFamilyId)
          return;
        var indexFamily = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(indexFamilyId), Constants.indexFamilies);
        if (indexFamily.uniqueDualIndex) {
          var index1 = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(newValue), indexFamily.indices);
          var index2 = Utils.array.findUniqueOrThrow(function(index) {
            return index.position === 2 && index.name === index1.name;
          }, indexFamily.indices);
          form.updateField('index2Id', {
            value: index2.id
          });
        }
      }
    }
    return field;
  }

  function getIndices(indexFamilyId, position) {
    if (!indexFamilyId) {
      return [];
    }
    var indexFamily = Utils.array.findUniqueOrThrow(function(family) {
      return family.id == indexFamilyId;
    }, Constants.indexFamilies);
    return indexFamily.indices.filter(function(index) {
      return index.position === position;
    });
  }

})(jQuery);
