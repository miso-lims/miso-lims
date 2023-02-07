BulkTarget = window.BulkTarget || {};
BulkTarget.samplepurpose = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.samplePurposes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.samplePurposes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "sample-purposes");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.samplePurposes.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(255), BulkUtils.columns.archived()];
    },
  };
})(jQuery);
