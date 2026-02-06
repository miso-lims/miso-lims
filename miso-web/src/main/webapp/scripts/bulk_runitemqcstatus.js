BulkTarget = window.BulkTarget || {};
BulkTarget.runItemQcStatus = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.runItemQcStatuses.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.runItemQcStatuses.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "run-library-qc-statuses");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.runItemQcStatuses.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Description",
          type: "text",
          data: "description",
          required: true,
          maxLength: 50,
        },
        {
          title: "QC Passed?",
          type: "dropdown",
          data: "qcPassed",
          source: [
            {
              label: "Yes",
              value: true,
            },
            {
              label: "No",
              value: false,
            },
          ],
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
        },
      ];
    },
  };
})();
