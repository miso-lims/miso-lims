BulkTarget = window.BulkTarget || {};
BulkTarget.librarystrategy = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.libraryStrategies.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.libraryStrategies.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'library-strategy-types');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.libraryStrategies.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [
        {
          title: 'Name',
          type: 'text',
          data: 'name',
          required: true,
          maxLength: 50
        }, {
          title: 'Description',
          type: 'text',
          data: 'description',
          required: true,
          maxLength: 255
        }
      ];
    }
  };

})();
