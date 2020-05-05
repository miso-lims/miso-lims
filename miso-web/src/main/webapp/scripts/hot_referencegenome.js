HotTarget.referencegenome = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'reference-genomes');
  },
  getCreateUrl: function() {
    return Urls.rest.referenceGenomes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.referenceGenomes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(referencegenome, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
        HotUtils.makeColumnForText('Alias', true, 'alias', {
          validator: HotUtils.validator.requiredText
        }),
        HotUtils.makeColumnForConstantsList('Default Sci. Name', true, 'defaultScientificName', 'defaultScientificNameId', 'id', 'alias',
            Constants.scientificNames, false)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.referenceGenomes.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
