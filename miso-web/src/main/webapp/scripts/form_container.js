if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.container = (function($) {

  return {
    getSaveUrl: function(container) {
      return container.id ? Urls.rest.containers.update(container.id) : Urls.rest.containers.create;
    },
    getSaveMethod: function(container) {
      return container.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(container) {
      return Urls.ui.containers.edit(container.id);
    },
    getSections: function(config, object) {
      return [{
        title: 'Container Information',
        fields: [{
          title: 'Container ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(container) {
            return container.id || 'Unsaved';
          }
        }, {
          title: 'Serial Number',
          data: 'identificationBarcode',
          type: 'text',
          required: true,
          maxLength: 255
        }, {
          title: 'Container Model',
          data: 'model.id',
          type: 'read-only',
          getDisplayValue: function(container) {
            return container.model.alias;
          }
        }, {
          title: 'Change Model',
          type: 'special',
          makeControls: makeModelSelect(object)
        }, {
          title: 'Description',
          data: 'description',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Clustering Kit',
          data: 'clusteringKitId',
          type: 'dropdown',
          source: getKitsByType('Clustering'),
          getItemLabel: function(item) {
            return item.name;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Multiplexing Kit',
          data: 'multiplexingKitId',
          type: 'dropdown',
          source: getKitsByType('Multiplexing'),
          getItemLabel: function(item) {
            return item.name;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Pore Version',
          data: 'poreVersionId',
          type: 'dropdown',
          source: Constants.poreVersions,
          getItemLabel: function(item) {
            return item.alias;
          },
          getItemValue: function(item) {
            return item.id;
          },
          include: config.platformType === 'OXFORDNANOPORE'
        }, {
          title: 'Received Date',
          data: 'receivedDate',
          type: 'date',
          required: true,
          include: config.platformType === 'OXFORDNANOPORE'
        }, {
          title: "Returned Date",
          data: 'returnedDate',
          type: 'date',
          include: config.platformType === 'OXFORDNANOPORE'
        }]
      }];
    }
  }

  function makeModelSelect(container) {
    return function(form) {
      return $('<button>').addClass('ui-state-default').attr('type', 'button').text('Select').click(
          function() {
            var currentModelId = form.get('model.id');
            if (currentModelId) {
              var currentSelection = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(currentModelId), Constants.containerModels);
              var platformType = Utils.array.findUniqueOrThrow(Utils.array.namePredicate(currentSelection.platformType),
                  Constants.platformTypes);
              if (container.lastRunInstrumentModelId) {
                showContainerModelSelect(form, platformType, container.lastRunInstrumentModelId, currentSelection.partitionCount);
              } else {
                showInstrumentModelSelect(form, platformType, currentSelection.partitionCount);
              }
            } else {
              showPlatformSelect(form);
            }
          });
    }
  }

  function showPlatformSelect(form) {
    var options = Constants.platformTypes.filter(function(platform) {
      return platform.active;
    }).sort(Utils.sorting.standardSort('key')).map(function(platform) {
      return {
        name: platform.key,
        handler: function() {
          showInstrumentModelSelect(form, platform);
        }
      };
    });
    Utils.showWizardDialog('Select Platform', options);
  }

  function showInstrumentModelSelect(form, platformType, partitionCount) {
    var options = Constants.instrumentModels.filter(function(model) {
      return model.platformType === platformType.name && model.instrumentType === 'SEQUENCER' && model.active;
    }).sort(Utils.sorting.standardSort('alias')).map(function(model) {
      return {
        name: model.alias,
        handler: function() {
          showContainerModelSelect(form, platformType, model.id, partitionCount);
        }
      };
    });
    Utils.showWizardDialog('Select Instrument Model', options);
  }

  function showContainerModelSelect(form, platformType, instrumentModelId, partitionCount) {
    var options = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(instrumentModelId), Constants.instrumentModels).containerModels
        .filter(function(model) {
          return !model.archived && (!partitionCount || model.partitionCount === partitionCount);
        }).sort(Utils.sorting.standardSort('alias')).map(function(model) {
          return {
            name: model.alias,
            handler: function() {
              form.updateField('model.id', {
                value: model.id,
                label: model.alias
              });
            }
          };
        });
    if (options.length) {
      Utils.showWizardDialog('Select ' + platformType.containerName + ' Model', options);
    } else {
      Utils.showOkDialog('Error', ['No eligible container models found. Must have the same number of '
          + platformType.pluralPartitionName.toLowerCase() + '.']);
    }
  }

  function getKitsByType(type) {
    return Constants.kitDescriptors.filter(function(kit) {
      return kit.kitType === type;
    });
  }

})(jQuery);
