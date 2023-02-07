BulkTarget = window.BulkTarget || {};
BulkTarget.partitionqctype = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.partitionQcTypes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.partitionQcTypes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "partition-qc-types");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.partitionQcTypes.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Description",
          type: "text",
          data: "description",
          required: true,
          maxLength: 255,
        },
        BulkUtils.columns.makeBoolean("Note Required", "noteRequired", true),
        BulkUtils.columns.makeBoolean("Order Fulfilled", "orderFulfilled", true),
        BulkUtils.columns.makeBoolean("Disable Pipeline", "analysisSkipped", true),
      ];
    },
  };
})();
