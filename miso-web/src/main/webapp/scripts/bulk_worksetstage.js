BulkTarget = window.BulkTarget || {};
BulkTarget.worksetstage = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.worksetStages.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.worksetStages.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "workset-stages");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.worksetStages.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(20)];
    },
  };
})(jQuery);
