HotTarget.samplepurpose = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'sample-purposes');
  },
  getCreateUrl: function() {
    return Urls.rest.samplePurposes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.samplePurposes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(samplepurpose, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'alias', {
      unpackAfterSave: true,
      validator: HotUtils.validator.requiredText
    }), ];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.samplePurposes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    },

    ListUtils.createBulkDeleteAction("Sample Purposes", "samplepurposes", Utils.array.getAlias)];
  }
};
