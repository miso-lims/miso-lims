if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.pool = (function($) {

  return {
    getSaveUrl: function(pool) {
      return pool.id ? ('/miso/rest/pools/' + pool.id) : '/miso/rest/pools';
    },
    getSaveMethod: function(pool) {
      return pool.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(pool) {
      return '/miso/pool/' + pool.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Pool Information',
        fields: [{
          title: 'Pool ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(pool) {
            return pool.id || 'Unsaved';
          }
        }, {
          title: 'Name',
          data: 'name',
          type: 'read-only',
          getDisplayValue: function(pool) {
            return pool.name || 'Unsaved';
          }
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 50
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
          title: 'Platform Type',
          data: 'platformType',
          type: 'dropdown',
          include: !object.platformType,
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
            return item.name;
          }
        }, {
          title: 'Platform Type',
          data: 'platformType',
          type: 'read-only',
          include: !!object.platformType,
          getDisplayValue: function(pool) {
            return Utils.array.findUniqueOrThrow(Utils.array.namePredicate(pool.platformType), Constants.platformTypes).key;
          }
        }, {
          title: 'Concentration',
          data: 'concentration',
          type: 'decimal'
        }, FormUtils.makeUnitsField(object, 'concentration'), {
          title: 'Creation Date',
          data: 'creationDate',
          type: 'date',
          required: true,
          initial: Utils.getCurrentDate()
        }, FormUtils.makeQcPassedField(true), {
          title: 'Volume',
          data: 'volume',
          type: 'decimal'
        }, FormUtils.makeUnitsField(object, 'volume'), {
          title: 'Discarded',
          data: 'discarded',
          type: 'checkbox',
          onChange: function(newValue, form) {
            form.updateField('volume', {
              disabled: newValue
            });
          }
        }].concat(FormUtils.makeDistributionFields()).concat(FormUtils.makeBoxLocationField(true))
      }];
    },
    confirmSave: function(pool, saveCallback) {
      if (!pool.id && !pool.identificationBarcode && !Constants.automaticBarcodes) {
        Utils.showConfirmDialog("Missing Barcode", "Save",
            ["Pools should usually have barcodes. Are you sure you wish to save without one?"], saveCallback);
      } else {
        saveCallback();
      }
    }
  }

})(jQuery);
