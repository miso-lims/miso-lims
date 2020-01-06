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
      return freezer.id ? Urls.rest.storageLocations.updateFreezer(freezer.id) : Urls.rest.storageLocations.createFreezer;
    },
    getSaveMethod: function(freezer) {
      return freezer.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(freezer) {
      return Urls.ui.freezers.edit(freezer.id);
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
          onChange: function(newValue, form) {
            var settings = {
              disabled: !newValue
            }
            if (!newValue) {
              settings.value = null;
            }
            form.updateField('mapAnchor', settings);
            updateMapLink(form, config.locationMaps);
          }
        }, {
          title: 'Map Anchor',
          data: 'mapAnchor',
          type: 'text',
          maxLength: 100,
          onChange: function(newValue, form) {
            updateMapLink(form, config.locationMaps);
          }
        }, {
          title: 'View Map',
          data: 'mapLink',
          type: 'read-only',
          openNewTab: true,
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

  function updateMapLink(form, locationMaps) {
    var mapId = form.get('mapId');
    var map = !mapId ? null : Utils.array.findUniqueOrThrow(Utils.array.idPredicate(mapId), locationMaps);
    var mapAnchor = form.get('mapAnchor');
    form.updateField('mapLink', {
      label: mapId ? 'Open' : 'n/a',
      link: mapId ? Urls.ui.freezerMaps.view(map.filename, mapAnchor) : null
    });
  }

})(jQuery);
