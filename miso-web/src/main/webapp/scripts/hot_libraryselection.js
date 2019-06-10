HotTarget.libraryselection = {
  createUrl: '/miso/rest/libraryselections',
  updateUrl: '/miso/rest/libraryselections/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(libraryselection, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'name', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.optionalTextNoSpecialChars
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/libraryselection/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
