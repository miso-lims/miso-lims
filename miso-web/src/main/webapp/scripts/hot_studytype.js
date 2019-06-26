HotTarget.studytype = {
  getCreateUrl: function() {
    return Urls.rest.studyTypes.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.studyTypes.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(studytype, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'name', {
      validator: HotUtils.validator.requiredText
    })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/studytype/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
