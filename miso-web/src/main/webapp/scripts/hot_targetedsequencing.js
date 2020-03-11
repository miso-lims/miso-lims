HotTarget.targetedsequencing = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'targeted-sequencing');
  },
  getCreateUrl: function() {
    return Urls.rest.targetedSequencings.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.targetedSequencings.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(targetedSequencing, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      validator: HotUtils.validator.requiredTextNoSpecialChars
    }), HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.optionalTextNoSpecialChars
    }), HotUtils.makeColumnForBoolean('Archived', true, 'archived', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.targetedSequencings.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
