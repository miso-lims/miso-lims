HotTarget.boxuse = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'box-uses');
  },
  getCreateUrl: function() {
    return Urls.rest.boxUses.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.boxUses.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(boxuse, errorHandler) {
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
        window.location = Urls.ui.boxUses.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
