BulkTarget = window.BulkTarget || {};
BulkTarget.lab = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.labs.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.labs.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'labs');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.labs.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [
        BulkUtils.columns.simpleAlias(255),
        BulkUtils.columns.archived()
      ];
    }
  };

})();
