HotTarget.tissueorigin = {
  getCreateUrl: function() {
    return Urls.rest.tissueOrigins.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.tissueOrigins.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(tissueorigin, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Alias', true, 'alias', {
      unpackAfterSave: true,
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForText('Description', true, 'description', {
      unpackAfterSave: true,
      validator: HotUtils.validator.requiredText
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + Urls.ui.tissueOrigins.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    },

    ListUtils.createBulkDeleteAction("Tissue Origins", "tissueorigins", Utils.array.getAlias)];
  }
};
