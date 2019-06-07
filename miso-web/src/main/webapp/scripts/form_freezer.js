if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.freezer = (function($) {

  /*
   * Expected config {
   *   rooms: array,
   *   locationMaps: array
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
          title: 'Map',
          data: 'mapId',
          type: 'dropdown',
          getSource: function() {
            return config.locationMaps;
          },
          getItemLabel: function(item) {
            return item.filename;
          },
          getItemValue: Utils.array.getId,
          sortSource: Utils.sorting.standardSort('filename'),
          onChange: function(newValue, updateField) {
            var settings = {
              disabled: !newValue
            }
            if (!newValue) {
              settings.value = null;
            }
            updateField('mapAnchor', settings);
            updateMapLink(updateField);
          }
        }, {
          title: 'Map Anchor',
          data: 'mapAnchor',
          type: 'text',
          maxLength: 100,
          onChange: function(newValue, updateField) {
            updateMapLink(updateField);
          }
        }, {
          title: 'View Map',
          data: 'mapLink',
          type: 'read-only',
          omit: true,
          getDisplayValue: function(freezer) {
            return 'n/a';
          }
        }, {
          title: 'Probe ID',
          data: 'probeId',
          type: 'text',
          maxLength: 50
        }]
      }];
    }
  }

  function updateMapLink(updateField) {
    var span = $('#mapLinkLabel');
    span.empty();
    if ($('#mapId').val()) {
      var url = '/freezermaps/' + $('#mapId option:selected').html();
      if ($('#mapAnchor').val()) {
        url += '#' + $('#mapAnchor').val();
      }
      span.append($('<a>').attr('href', url).attr('target', '_blank').text('Open'));
    } else {
      span.text('n/a');
    }
  }

})(jQuery);
