HotTarget.boxsize = {
  createUrl: '/miso/rest/boxsizes',
  updateUrl: '/miso/rest/boxsizes/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(boxsize, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForInt('Rows', true, 'rows', HotUtils.validator.requiredPositiveInt),
        HotUtils.makeColumnForInt('Columns', true, 'columns', HotUtils.validator.requiredPositiveInt),
        HotUtils.makeColumnForBoolean('Scannable', true, 'scannable', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/boxsize/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
