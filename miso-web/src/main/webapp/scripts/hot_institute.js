HotTarget.institute = {
  getCreateUrl: function() {
    return Urls.rest.institutes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.institutes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(institute, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'alias', {
      unpackAfterSave: true,
      validator: HotUtils.validator.requiredText
    }), ];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.institutes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    },

    ListUtils.createBulkDeleteAction('Institutes', 'institutes', Utils.array.getAlias)];
  }
};
