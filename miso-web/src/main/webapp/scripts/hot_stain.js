HotTarget.stain = {
  createUrl: '/miso/rest/stains',
  updateUrl: '/miso/rest/stains/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(stain, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
        HotUtils.makeColumnForText('Name', true, 'name', {
          validator: HotUtils.validator.requiredText
        }),
        HotUtils.makeColumnForConstantsList('Category', true, 'categoryName', 'categoryId', 'id', 'name', config.stainCategories, false,
            {}, Utils.sorting.standardSort('name'), '(none)')];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/stain/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
