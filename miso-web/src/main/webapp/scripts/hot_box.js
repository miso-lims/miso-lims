
HotTarget.box = (function(box) {
  
  return {
    createUrl: '/miso/rest/box',
    updateUrl: '/miso/rest/box/',
    requestConfiguration: function(config, callback) {
      callback(config);
    },

    fixUp: function(box, errorHandler) {

    },

    createColumns: function(config, create, data) {
      return [{
        header: 'Name',
        data: 'name',
        readOnly: true,
        include: true,
        unpackAfterSave: false,
        unpack: function(box, flat, setCellMeta) {
          flat.name = box.name;
        },
        pack: function(box, flat, errorHandler) {
        }
      }, HotUtils.makeColumnForText('Alias', true, 'alias', {
        validator: HotUtils.validator.requiredTextNoSpecialChars
      }), HotUtils.makeColumnForText('Description', true, 'description', {
        validator: HotUtils.validator.requiredText
      }), HotUtils.makeColumnForText('Matrix Barcode', true, 'identificationBarcode', {
        validator: HotUtils.validator.optionalTextNoSpecialChars
      }), HotUtils.makeColumnForConstantsList('Box Use', true, 'use', 'useId', 'id', 'alias', Constants.boxUses, true, {
        validator: HotUtils.validator.requiredAutocomplete
      }), HotUtils.makeColumnForConstantsList('Box Size', create, 'size', 'sizeId', 'id', 'rowsByColumns', Constants.boxSizes, true, {
        validator: HotUtils.validator.requiredAutocomplete
      }), HotUtils.makeColumnForText('Location', true, 'locationBarcode', {
        validator: HotUtils.validator.optionalTextNoSpecialChars
      }), {
        header: 'Freezer Location Barcode',
        data: 'storageLocationBarcode',
        validator: HotUtils.validator.optionalTextNoSpecialChars,
        include: true,
        unpack: function(box, flat, setCellMeta) {
          flat.storageLocationBarcode = box.storageLocationBarcode;
        },
        pack: function(box, flat, errorHandler) {
          box.storageLocationBarcode = flat.storageLocationBarcode;
        }
      },
      {
        header: 'Freezer Location',
        data: 'storageDisplayLocation',
        type: 'text',
        include: true,
        readOnly: true,
        depends: 'storageLocationBarcode',
        update: function(box, flat, flatProperty, value, setReadOnly, setOptions, setData) {
          if (Utils.validation.isEmpty(flat.storageLocationBarcode)) {
            setOptions({
              validator: HotUtils.validator.requiredEmpty
            });
            setData('');
            return;
          }
          var deferred = jQuery.Deferred();
          setData('(...searching...)');
          getFreezerLocations();
          return deferred.promise();

          function getFreezerLocations() {
            jQuery.ajax({
              url: '/miso/rest/storagelocations/bybarcode?' + jQuery.param({
                q: flat.storageLocationBarcode,
                }),
              contentType: "application/json; charset=utf8",
              dataType: "json"
            }).success(
              function(data) {
                setData(data.fullDisplayLocation);
                setOptions({
                  validator: HotUtils.validator.requiredText
                });
            }).fail(function(response, textStatus, serverStatus) {
              HotUtils.showServerErrors(response, serverStatus);
              setData('(Not Found)');
              setOptions({
                validator: HotUtils.validator.invalid
              });
            }).always(function() {
              deferred.resolve();
            });
          }
        },
        unpack: function(box, flat, setCellMeta) {
          flat.storageDisplayLocation = box.storageDisplayLocation;
        },
        pack: function(box, flat, errorHandler) {
          box.storageDisplayLocation = flat.storageDisplayLocation;
        }
      },];
    },

    getBulkActions: function(config) {
      return [{
        name: 'Edit',
        action: function(items) {
          window.location = window.location.origin + '/miso/box/bulk/edit' + '?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }
      }];
    },
  };
})();
