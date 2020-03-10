HotTarget.tissuepiecetype = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'tissue-piece-types');
  },
  getCreateUrl: function() {
    return Urls.rest.tissuePieceTypes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.tissuePieceTypes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(librarytype, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'name', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForText('Abbreviation', true, 'abbreviation', {
      validator: HotUtils.validator.optionalTextNoSpecialChars
    }), HotUtils.makeColumnForText('V2 Naming Code', true, 'v2NamingCode', {
      validator: HotUtils.validator.requiredTextNoSpecialChars
    }), HotUtils.makeColumnForBoolean('Archived', true, 'archived', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.tissuePieceTypes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
