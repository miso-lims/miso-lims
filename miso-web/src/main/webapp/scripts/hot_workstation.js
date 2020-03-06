HotTarget.workstation = {
  getCreateUrl: function() {
    return Urls.rest.workstations.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.workstations.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(librarytype, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      validator: HotUtils.validator.requiredTextNoSpecialChars
    }), HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.optionalTextNoSpecialChars
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.workstations.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
