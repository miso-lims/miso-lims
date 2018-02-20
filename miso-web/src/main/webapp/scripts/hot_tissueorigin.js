HotTarget.tissueorigin = {
  createUrl: '/miso/rest/tissueorigin',
  updateUrl: '/miso/rest/tissueorigin/',
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
        window.location = window.location.origin + '/miso/tissueorigin/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    },

    {
      name: 'Delete',
      action: function(items) {
        var deleteNext = function(index) {
          if (index >= items.length) {
            window.location = window.location.origin + '/miso/tissueorigin/list';
            return;
          }
          var next = function() {
            deleteNext(index + 1);
          };
          Utils.ajaxWithDialog('Deleting ' + items[index].alias, 'DELETE', '/miso/rest/tissueorigin/' + items[index].id, null, next, next);
        };
        deleteNext(0);
      }
    }, ];
  }
};
