HotTarget.runpurpose = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'run-purposes');
  },
  getCreateUrl: function() {
    return Urls.rest.runPurposes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.runPurposes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(purpose, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      validator: HotUtils.validator.requiredText
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.runPurposes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
