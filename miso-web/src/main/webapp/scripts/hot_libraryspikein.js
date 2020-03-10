HotTarget.libraryspikein = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'library-spike-ins');
  },
  getCreateUrl: function() {
    return Urls.rest.librarySpikeIns.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.librarySpikeIns.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(libraryspikein, errorHandler) {
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
        window.location = Urls.ui.librarySpikeIns.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
