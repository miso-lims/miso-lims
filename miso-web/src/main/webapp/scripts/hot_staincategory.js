HotTarget.staincategory = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'stains');
  },
  getCreateUrl: function() {
    return Urls.rest.stainCategories.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.stainCategories.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(staincategory, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'name', {
      validator: HotUtils.validator.requiredText
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.stainCategories.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
