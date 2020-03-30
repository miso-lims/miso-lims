HotTarget.sequencingcontroltype = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'sequencing-control-types');
  },
  getCreateUrl: function() {
    return Urls.rest.sequencingControlTypes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.sequencingControlTypes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(lab, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      validator: HotUtils.validator.requiredTextNoSpecialChars
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.sequencingControlTypes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
