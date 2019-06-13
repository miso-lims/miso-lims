HotTarget.librarytype = {
  createUrl: '/miso/rest/librarytypes',
  updateUrl: '/miso/rest/librarytypes/',
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
        window.location = window.location.origin + '/miso/librarytype/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
