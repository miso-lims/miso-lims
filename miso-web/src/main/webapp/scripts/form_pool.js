if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.pool = (function($) {

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('pools');
    },
    getSaveUrl: function(pool) {
      return pool.id ? Urls.rest.pools.update(pool.id) : Urls.rest.pools.create;
    },
    getSaveMethod: function(pool) {
      return pool.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(pool) {
      return Urls.ui.pools.edit(pool.id);
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
          maxLength: 255
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
          source: Constants.platformTypes.filter(function(platformType) {
            return platformType.active;
          }),
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
        }, {
          title: 'QC Status',
          data: 'qcPassed',
          type: 'dropdown',
          source: [{
            label: 'Ready',
            value: true
          }, {
            label: 'Failed',
            value: false
          }],
          convertToBoolean: true,
          getItemLabel: function(item) {
            return item.label;
          },
          getItemValue: function(item) {
            return item.value;
          },
          nullLabel: 'Not Ready'
        }, {
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
        }, FormUtils.makeBoxLocationField(true)]
      }];
    },
    confirmSave: function(pool, saveCallback, isDialog) {
      if (!isDialog) {
        pool.pooledElements = Pool.getAliquots();
      }
      if (!pool.id && !pool.identificationBarcode && !Constants.automaticBarcodes) {
        Utils.showConfirmDialog("Missing Barcode", "Save",
            ["Pools should usually have barcodes. Are you sure you wish to save without one?"], saveCallback);
      } else {
        saveCallback();
      }
    }
  }

})(jQuery);
