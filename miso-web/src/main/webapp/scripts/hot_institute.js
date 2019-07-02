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
        window.location = window.location.origin + '/miso/institute/bulk/edit?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    },

    {
      name: 'Delete',
      action: function(items) {
        var deleteNext = function(index) {
          if (index == items.length) {
            window.location = window.location.origin + '/miso/institute/list';
            return;
          }
          Utils.ajaxWithDialog('Deleting ' + items[index].alias, 'DELETE', '/miso/rest/institutes/' + items[index].id, null, function() {
            deleteNext(index + 1);
          });
        };
        deleteNext(0);
      }
    }, ];
  }
};
