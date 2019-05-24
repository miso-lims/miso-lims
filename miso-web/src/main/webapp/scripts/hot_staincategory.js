HotTarget.staincategory = {
  createUrl: '/miso/rest/staincategories',
  updateUrl: '/miso/rest/staincategories/',
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
        window.location = window.location.origin + '/miso/staincategory/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
