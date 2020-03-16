if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.container = (function($) {

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('sequencing_containers');
    },
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
          type: 'dropdown',
          required: true,
          source: getValidContainerModels(object),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: Utils.sorting.standardSort('alias')
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
          },
          onChange: function(newValue, form) {
            var opts = {
              disabled: !newValue
            }
            if (!newValue) {
              opts.value = null;
            }
            form.updateField('clusteringKitLot', opts);
          }
        }, {
          title: 'Clustering Kit Lot',
          data: 'clusteringKitLot',
          type: 'text',
          maxLength: 100
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
          },
          onChange: function(newValue, form) {
            var opts = {
              disabled: !newValue
            }
            if (!newValue) {
              opts.value = null;
            }
            form.updateField('multiplexingKitLot', opts);
          }
        }, {
          title: 'Multiplexing Kit Lot',
          data: 'multiplexingKitLot',
          type: 'text',
          maxLength: 100
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

  function getValidContainerModels(container) {
    var currentModel = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(container.model.id), Constants.containerModels);
    var platformType = Utils.array.findUniqueOrThrow(Utils.array.namePredicate(currentModel.platformType), Constants.platformTypes);
    var instrumentModels = null;
    if (container.lastRunInstrumentModelId) {
      instrumentModels = [Utils.array.findUniqueOrThrow(Utils.array.idPredicate(container.lastRunInstrumentModelId),
          Constants.instrumentModels)];
    } else {
      instrumentModels = Constants.instrumentModels.filter(function(instrumentModel) {
        return currentModel.instrumentModelIds.indexOf(instrumentModel.id) !== -1;
      });
    }
    return instrumentModels.flatMap(function(instrumentModel) {
      return instrumentModel.containerModels;
    }).reduce(function(accumulator, currentValue) {
      if (currentValue.partitionCount === currentModel.partitionCount && !accumulator.find(function(model) {
        return model.id === currentValue.id;
      })) {
        accumulator.push(currentValue);
      }
      return accumulator;
    }, []);
  }

  function getKitsByType(type) {
    return Constants.kitDescriptors.filter(function(kit) {
      return kit.kitType === type;
    });
  }

})(jQuery);
