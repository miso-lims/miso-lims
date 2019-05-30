HotTarget.boxuse = {
  createUrl: '/miso/rest/boxuses',
  updateUrl: '/miso/rest/boxuses/',
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
        window.location = window.location.origin + '/miso/boxuse/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
