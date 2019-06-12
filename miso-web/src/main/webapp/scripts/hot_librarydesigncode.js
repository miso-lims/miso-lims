HotTarget.librarydesigncode = {
  createUrl: '/miso/rest/librarydesigncodes',
  updateUrl: '/miso/rest/librarydesigncodes/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(librarydesigncode, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Code', true, 'code', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForText('Description', true, 'description', {
      validator: HotUtils.validator.requiredText
    }), HotUtils.makeColumnForBoolean('Targeted Sequencing Req\'d', true, 'targetedSequencingRequired', true)];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = window.location.origin + '/miso/librarydesigncode/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
