BulkTarget = window.BulkTarget || {};
BulkTarget.sop = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.sops.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.sops.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'standard-operating-procedures');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.sops.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [BulkUtils.columns.simpleAlias(100), {
        title: 'Version',
        type: 'text',
        data: 'version',
        required: true,
        maxLength: 50
      }, {
        title: 'Category',
        type: 'dropdown',
        data: 'category',
        required: true,
        source: [{
          label: 'Sample',
          value: 'SAMPLE'
        }, {
          label: 'Library',
          value: 'LIBRARY'
        }, {
          label: 'Run',
          value: 'RUN'
        }],
        getItemLabel: Utils.array.get('label'),
        getItemValue: Utils.array.get('value')
      }, {
        title: 'URL',
        type: 'text',
        data: 'url',
        required: true,
        maxLength: 255
      }, BulkUtils.columns.archived()];
    }
  };

})();