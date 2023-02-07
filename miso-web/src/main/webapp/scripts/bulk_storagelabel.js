BulkTarget = window.BulkTarget || {};
BulkTarget.storagelabel = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.storageLabels.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.storageLabels.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "storage-labels");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.storageLabels.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Label",
          type: "text",
          data: "label",
          required: true,
          maxLength: 100,
        },
      ];
    },
  };
})();
