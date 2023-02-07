BulkTarget = window.BulkTarget || {};
BulkTarget.tissuematerial = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.tissueMaterials.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.tissueMaterials.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "tissue-materials");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.tissueMaterials.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(255)];
    },
  };
})(jQuery);
