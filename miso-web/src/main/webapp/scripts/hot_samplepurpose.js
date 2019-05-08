HotTarget.samplepurpose = {
  createUrl: '/miso/rest/samplepurposes',
  updateUrl: '/miso/rest/samplepurposes/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(samplepurpose, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [HotUtils.makeColumnForText('Name', true, 'alias', {
      unpackAfterSave: true,
      validator: HotUtils.validator.requiredText
    }), ];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [
        {
          name: 'Edit',
          action: function(items) {
            window.location = window.location.origin + '/miso/samplepurpose/bulk/edit?' + jQuery.param({
              ids: items.map(Utils.array.getId).join(',')
            });
          }
        },

        {
          name: 'Delete',
          action: function(items) {
            var deleteNext = function(index) {
              if (index == items.length) {
                window.location = window.location.origin + '/miso/samplepurpose/list';
              }
              Utils.ajaxWithDialog('Deleting ' + items[index].alias, 'DELETE', '/miso/rest/samplepurposes/' + items[index].id, null,
                  function() {
                    deleteNext(index + 1);
                  });
            };
            deleteNext(0);
          }
        }, ];
  }
};
