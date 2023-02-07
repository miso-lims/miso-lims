BulkTarget = window.BulkTarget || {};
BulkTarget.boxuse = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.boxUses.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.boxUses.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "box-uses");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.boxUses.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(255)];
    },
  };
})(jQuery);
