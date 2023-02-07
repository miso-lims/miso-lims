BulkTarget = window.BulkTarget || {};
BulkTarget.tissuetype = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.tissueTypes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.tissueTypes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "tissue-types");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.tissueTypes.bulkEdit)];
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
