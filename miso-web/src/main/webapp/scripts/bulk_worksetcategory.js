BulkTarget = window.BulkTarget || {};
BulkTarget.worksetcategory = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.worksetCategories.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.worksetCategories.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "workset-categories");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.worksetCategories.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(20)];
    },
  };
})(jQuery);
