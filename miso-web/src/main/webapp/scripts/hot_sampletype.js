HotTarget.sampletype = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'sample-types');
  },
  getCreateUrl: function() {
    return Urls.rest.sampleTypes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.sampleTypes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(lab, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'name', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForBoolean('Archived', true, 'archived', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.sampleTypes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
