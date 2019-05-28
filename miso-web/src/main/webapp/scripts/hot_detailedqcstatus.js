HotTarget.detailedqcstatus = {
  createUrl: '/miso/rest/detailedqcstatuses',
  updateUrl: '/miso/rest/detailedqcstatuses/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(detailedqcstatus, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForBoolean('QC Passed?', true, 'status', false),
        HotUtils.makeColumnForBoolean('Note Required?', true, 'noteRequired', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/detailedqcstatus/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
