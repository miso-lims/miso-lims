BulkTarget = window.BulkTarget || {};
BulkTarget.tissueorigin = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.tissueOrigins.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.tissueOrigins.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "tissue-origins");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.tissueOrigins.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(255),
        {
          title: "Description",
          data: "description",
          type: "text",
          required: true,
          maxLength: 255,
        },
      ];
    },
  };
})(jQuery);
