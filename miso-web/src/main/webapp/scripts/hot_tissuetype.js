HotTarget.tissuetype = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'tissue-types');
  },
  getCreateUrl: function() {
    return Urls.rest.tissueTypes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.tissueTypes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(tissuetype, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.requiredText
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.tissueTypes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
