BulkTarget = window.BulkTarget || {};
BulkTarget.libraryspikein = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.librarySpikeIns.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.librarySpikeIns.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "library-spike-ins");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.librarySpikeIns.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(255)];
    },
  };
})();
