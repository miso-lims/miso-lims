BulkTarget = window.BulkTarget || {};
BulkTarget.detailedqcstatus = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.detailedQcStatuses.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.detailedQcStatuses.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "detailed-qc-status");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.detailedQcStatuses.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Description",
          data: "description",
          type: "text",
          required: true,
          maxLength: 255,
        },
        {
          title: "QC Passed?",
          data: "status",
          type: "dropdown",
          required: true,
          source: [
            {
              label: "True",
              value: true,
            },
            {
              label: "False",
              value: false,
            },
            {
              label: "Unknown",
              value: null,
            },
          ],
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
        },
        {
          title: "Note Required?",
          data: "noteRequired",
          type: "dropdown",
          required: true,
          source: [
            {
              label: "True",
              value: true,
            },
            {
              label: "False",
              value: false,
            },
          ],
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
        },
      ];
    },
  };
})(jQuery);
