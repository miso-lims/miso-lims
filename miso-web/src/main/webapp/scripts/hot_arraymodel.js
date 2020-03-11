HotTarget.arraymodel = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'array-models');
  },
  getCreateUrl: function() {
    return Urls.rest.arrayModels.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.arrayModels.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(arraymodel, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForInt('Rows', true, 'rows', HotUtils.validator.integer(true, 1)),
        HotUtils.makeColumnForInt('Columns', true, 'columns', HotUtils.validator.integer(true, 1))];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.arrayModels.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
