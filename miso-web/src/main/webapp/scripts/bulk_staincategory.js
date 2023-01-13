BulkTarget = window.BulkTarget || {};
BulkTarget.staincategory = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.stainCategories.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.stainCategories.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "stains");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.stainCategories.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Name",
          data: "name",
          type: "text",
          required: true,
          maxLength: 20,
        },
      ];
    },
  };
})(jQuery);
