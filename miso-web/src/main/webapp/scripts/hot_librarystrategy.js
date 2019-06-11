HotTarget.librarystrategy = {
  createUrl: '/miso/rest/librarystrategies',
  updateUrl: '/miso/rest/librarystrategies/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(librarystrategy, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'name', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.requiredText
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/librarystrategy/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
