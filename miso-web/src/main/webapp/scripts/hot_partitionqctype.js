HotTarget.partitionqctype = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'partition-qc-types');
  },
  getCreateUrl: function() {
    return Urls.rest.partitionQcTypes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.partitionQcTypes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(partitionqctype, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForBoolean('Note Required', true, 'noteRequired', true),
        HotUtils.makeColumnForBoolean('Order Fulfilled', true, 'orderFulfilled', true),
        HotUtils.makeColumnForBoolean('Analysis Skipped', true, 'analysisSkipped', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.partitionQcTypes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
