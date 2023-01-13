BulkTarget = window.BulkTarget || {};
BulkTarget.pipeline = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.pipelines.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.pipelines.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "pipelines");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.pipelines.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(50)];
    },
  };
})();
