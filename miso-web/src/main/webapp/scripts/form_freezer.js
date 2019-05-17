if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.freezer = (function($) {

  /*
   * Expected config {
   *   rooms: array
   * }
   */

  return {
    getSaveUrl: function(freezer) {
      return freezer.id ? ('/miso/rest/storagelocations/freezers/' + freezer.id) : '/miso/rest/storagelocations/freezers';
    },
    getSaveMethod: function(freezer) {
      return freezer.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(freezer) {
      return '/miso/freezer/' + freezer.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Freezer Information',
        fields: [{
          title: 'Location ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(freezer) {
            return freezer.id || 'Unsaved';
          }
        }, {
          title: 'Room',
          data: 'parentLocationId',
          type: 'dropdown',
          required: true,
          getSource: function() {
            return config.rooms;
          },
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: Utils.sorting.standardSort('alias')
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 255
        }, {
          title: 'Barcode',
          data: 'identificationBarcode',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Map URL',
          data: 'mapUrl',
          type: 'text',
          regex: 'url',
          maxLength: 1024
        }, {
          title: 'Probe ID',
          data: 'probeId',
          type: 'text',
          maxLength: 50
        }]
      }];
    }
  }

})(jQuery);
