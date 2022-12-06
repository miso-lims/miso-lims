BulkTarget = window.BulkTarget || {};
BulkTarget.runpurpose = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.runPurposes.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.runPurposes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'run-purposes');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.runPurposes.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [BulkUtils.columns.simpleAlias(50)];
    }
  };

})();
