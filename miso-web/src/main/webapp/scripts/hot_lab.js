HotTarget.lab = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'labs-and-institutes');
  },
  getCreateUrl: function() {
    return Urls.rest.labs.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.labs.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(lab, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
        HotUtils.makeColumnForText('Name', true, 'alias', {
          unpackAfterSave: true,
          validator: HotUtils.validator.requiredText
        }),
        HotUtils.makeColumnForConstantsList('Institute', true, 'instituteAlias', 'instituteId', 'id', 'alias', config.institutes, true, {},
            null), HotUtils.makeColumnForBoolean('Archived', true, 'archived', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.labs.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    },

    ListUtils.createBulkDeleteAction('Labs', 'labs', Utils.array.getAlias)];
  }
};
