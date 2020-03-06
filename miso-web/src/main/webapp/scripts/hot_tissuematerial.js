HotTarget.tissuematerial = {
  getCreateUrl: function() {
    return Urls.rest.tissueMaterials.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.tissueMaterials.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(tissuematerial, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'alias', {
      unpackAfterSave: true,
      validator: HotUtils.validator.requiredText
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + Urls.ui.tissueMaterials.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    },

    ListUtils.createBulkDeleteAction("Tissue Materials", "tissuematerials", Utils.array.getAlias)];
  }
};
