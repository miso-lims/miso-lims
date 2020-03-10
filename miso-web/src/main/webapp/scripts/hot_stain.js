HotTarget.stain = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'stains');
  },
  getCreateUrl: function() {
    return Urls.rest.stains.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.stains.update(id);
  },
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
        window.location = Urls.ui.stains.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
