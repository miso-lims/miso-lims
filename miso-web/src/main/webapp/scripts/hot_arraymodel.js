HotTarget.arraymodel = {
  createUrl: '/miso/rest/arraymodels',
  updateUrl: '/miso/rest/arraymodels/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(arraymodel, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForInt('Rows', true, 'rows', HotUtils.validator.requiredPositiveInt),
        HotUtils.makeColumnForInt('Columns', true, 'columns', HotUtils.validator.requiredPositiveInt)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/arraymodel/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
