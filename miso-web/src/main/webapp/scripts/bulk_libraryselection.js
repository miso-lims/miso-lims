BulkTarget = window.BulkTarget || {};
BulkTarget.libraryselection = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.librarySelections.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.librarySelections.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'library-selection-types');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.librarySelections.bulkEdit)];
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