HotTarget.librarytype = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'library-types');
  },
  getCreateUrl: function() {
    return Urls.rest.libraryTypes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.libraryTypes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(librarytype, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForConstantsList('Platform', true, 'platform', 'platform', 'name', 'key', Constants.platformTypes, true, {}),
        HotUtils.makeColumnForText('Abbreviation', true, 'abbreviation', {
          validator: HotUtils.validator.optionalTextNoSpecialChars
        }), HotUtils.makeColumnForBoolean('Archived', true, 'archived', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.libraryTypes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
