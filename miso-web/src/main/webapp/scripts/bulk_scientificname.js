BulkTarget = window.BulkTarget || {};
BulkTarget.scientificname = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.scientificNames.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.scientificNames.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "scientific-names");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.scientificNames.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(255)];
    },
  };
})(jQuery);
