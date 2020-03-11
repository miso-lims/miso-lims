HotTarget.librarydesigncode = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'library-designs');
  },
  getCreateUrl: function() {
    return Urls.rest.libraryDesignCodes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.libraryDesignCodes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(librarydesigncode, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Code', true, 'code', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForBoolean('Targeted Sequencing Req\'d', true, 'targetedSequencingRequired', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.libraryDesignCodes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
