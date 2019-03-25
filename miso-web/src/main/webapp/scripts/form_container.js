if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.container = (function($) {

  return {
    getSaveUrl: function(container) {
      if (container.id) {
        return '/miso/rest/container/' + container.id;
      } else {
        return '/miso/rest/container';
      }
    },
    getSaveMethod: function(container) {
      return container.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(container) {
      return '/miso/container/' + container.id;
    },
    getSections: function(config) {
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
          title: 'Description',
          data: 'description',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Clustering Kit',
          data: 'clusteringKitId',
          type: 'dropdown',
          getSource: function() {
            return getKitsByType('Clustering');
          },
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
          getSource: function() {
            return getKitsByType('Multiplexing');
          },
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
          getSource: function() {
            return Constants.poreVersions;
          },
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

  function getKitsByType(type) {
    return Constants.kitDescriptors.filter(function(kit) {
      return kit.kitType === type;
    });
  }

})(jQuery);
