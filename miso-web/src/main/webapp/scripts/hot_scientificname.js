HotTarget.scientificname = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'scientific-names');
  },
  getCreateUrl: function() {
    return Urls.rest.scientificNames.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.scientificNames.update(id);
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
        window.location = Urls.ui.scientificNames.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
