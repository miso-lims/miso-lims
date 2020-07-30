HotTarget.boxsize = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'box-sizes');
  },
  getCreateUrl: function() {
    return Urls.rest.boxSizes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.boxSizes.update(id)
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(boxsize, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
        HotUtils.makeColumnForInt('Rows', true, 'rows', HotUtils.validator.integer(true, 1)),
        HotUtils.makeColumnForInt('Columns', true, 'columns', HotUtils.validator.integer(true, 1)),
        HotUtils.makeColumnForConstantsList('Box Type', true, 'boxType', 'boxType', 'name', 'label', config.boxTypes, true, null, null,
            null, 'Storage'), HotUtils.makeColumnForBoolean('Scannable', true, 'scannable', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.boxSizes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
