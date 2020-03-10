HotTarget.containermodel = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'sequencing-container-models');
  },
  getCreateUrl: function() {
    return Urls.rest.containerModels.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.containerModels.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(model, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
        HotUtils.makeColumnForText('Alias', true, 'alias', {
          validator: HotUtils.validator.requiredTextNoSpecialChars
        }),
        HotUtils.makeColumnForText('Barcode', true, 'identificationBarcode', {
          validator: HotUtils.validator.optionalTextNoSpecialChars
        }),
        HotUtils.makeColumnForEnum('Platform', true, true, 'platformType', Constants.platformTypes.map(Utils.array.getName)),
        HotUtils.makeColumnForInt('Partitions', true, 'partitionCount', HotUtils.validator.integer(true, 1)),
        HotUtils
            .makeColumnForBoolean(
                'Fallback',
                true,
                'fallback',
                true,
                {
                  description: 'Fallback containers do not represent actual products, and are only intended for use when the real container model cannot be determined.'
                }), HotUtils.makeColumnForBoolean('Archived', true, 'archived', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.containerModels.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
