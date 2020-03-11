HotTarget.attachmentcategory = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'attachment-categories');
  },
  getCreateUrl: function() {
    return Urls.rest.attachmentCategories.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.attachmentCategories.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(attachmentcategory, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      unpackAfterSave: true,
      validator: HotUtils.validator.requiredText
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.attachmentCategories.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    },

    ListUtils.createBulkDeleteAction("Attachment Categories", "attachmentcategories", Utils.array.getAlias)];
  }
};
