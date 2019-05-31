HotTarget.referencegenome = {
  createUrl: '/miso/rest/referencegenomes',
  updateUrl: '/miso/rest/referencegenomes/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(referencegenome, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForText('Default Sci. Name', true, 'defaultScientificName', {
      validator: HotUtils.validator.optionalTextNoSpecialChars
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/referencegenome/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
