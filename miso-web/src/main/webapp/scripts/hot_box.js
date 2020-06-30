HotTarget.box = (function(box) {

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('boxes');
    },
    getCreateUrl: function() {
      return Urls.rest.boxes.create;
    },
    getUpdateUrl: function(id) {
      return Urls.rest.boxes.update(id);
    },
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
        validator: HotUtils.validator.optionalTextNoSpecialChars
      }), HotUtils.makeColumnForText('Matrix Barcode', true, 'identificationBarcode', {
        validator: HotUtils.validator.optionalTextNoSpecialChars
      }), HotUtils.makeColumnForConstantsList('Box Use', true, 'use', 'useId', 'id', 'alias', Constants.boxUses, true, {
        validator: HotUtils.validator.requiredAutocomplete
      }), HotUtils.makeColumnForConstantsList('Box Size', create, 'size', 'sizeId', 'id', 'label', Constants.boxSizes, true, {
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
          box.storageLocationId = flat.storageLocationId;
        }
      }, {
        header: 'Freezer Location',
        data: 'storageDisplayLocation',
        type: 'text',
        include: true,
        readOnly: true,
        depends: 'storageLocationBarcode',
        update: function(box, flat, flatProperty, value, setReadOnly, setOptions, setData) {
          function setDataAndValidator(value, id, validator) {
            setData(value || '');
            flat.storageLocationId = id;
            setOptions({
              validator: validator
            });
          }

          if (flatProperty != 'storageLocationBarcode') {
            return;
          }
          if (Utils.validation.isEmpty(flat.storageLocationBarcode)) {
            setDataAndValidator(null, null, null);
            return;
          }
          var deferred = jQuery.Deferred();
          setData('(...searching...)');

          jQuery.ajax({
            url: Urls.rest.storageLocations.queryByBarcode + '?' + jQuery.param({
              q: flat.storageLocationBarcode,
            }),
            contentType: "application/json; charset=utf8",
            dataType: "json"
          }).success(function(data) {
            setDataAndValidator(data.fullDisplayLocation, data.id, null);
          }).fail(function(response, textStatus, serverStatus) {
            HotUtils.showServerErrors(response, serverStatus);
            setDataAndValidator('(Not Found)', null, HotUtils.validator.invalid);
          }).always(function() {
            deferred.resolve();
          });

          return deferred.promise();
        },
        unpack: function(box, flat, setCellMeta) {
          flat.storageDisplayLocation = box.storageDisplayLocation;
          flat.storageLocationId = box.storageLocationId;
        },
        pack: function(box, flat, errorHandler) {
          box.storageDisplayLocation = flat.storageDisplayLocation;
        }
      }, ];
    },

    getBulkActions: function(config) {
      return [{
        name: 'Edit',
        action: function(items) {
          window.location = Urls.ui.boxes.bulkEdit + '?' + jQuery.param({
            ids: items.map(Utils.array.getId).join(',')
          });
        }
      }];
    },
  };
})();
