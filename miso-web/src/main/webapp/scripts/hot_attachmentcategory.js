HotTarget.attachmentcategory = {
  createUrl: '/miso/rest/attachmentcategories',
  updateUrl: '/miso/rest/attachmentcategories/',
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
    return !config.isAdmin ? [] : [
        {
          name: 'Edit',
          action: function(items) {
            window.location = window.location.origin + '/miso/attachmentcategories/bulk/edit?' + jQuery.param({
              ids: items.map(Utils.array.getId).join(',')
            });
          }
        },

        {
          name: 'Delete',
          action: function(items) {
            var deleteNext = function(index) {
              if (index == items.length) {
                window.location = window.location.origin + '/miso/attachmentcategories/list';
                return;
              }
              Utils.ajaxWithDialog('Deleting ' + items[index].alias, 'DELETE', '/miso/rest/attachmentcategories/' + items[index].id, null,
                  function() {
                    deleteNext(index + 1);
                  });
            };
            deleteNext(0);
          }
        }, ];
  }
};
