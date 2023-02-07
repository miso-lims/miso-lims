BulkTarget = window.BulkTarget || {};
BulkTarget.sampletype = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.sampleTypes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.sampleTypes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "sample-types");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.sampleTypes.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Name",
          data: "name",
          type: "text",
          maxLength: 255,
          required: true,
        },
        BulkUtils.columns.archived(),
      ];
    },
  };
})(jQuery);
