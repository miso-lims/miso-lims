BulkTarget = window.BulkTarget || {};
BulkTarget.workstation = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.workstations.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.workstations.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "workstations");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.workstations.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(50),
        BulkUtils.columns.description,
        {
          title: "Barcode",
          data: "identificationBarcode",
          type: "text",
          maxLength: 255,
        },
      ];
    },
  };
})();
