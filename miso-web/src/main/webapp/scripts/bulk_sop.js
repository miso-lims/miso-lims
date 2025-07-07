BulkTarget = window.BulkTarget || {};
BulkTarget.sop = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.sops.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.sops.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "standard-operating-procedures");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.sops.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(200),
        {
          title: "Version",
          type: "text",
          data: "version",
          required: true,
          maxLength: 50,
          disabled: config.pageMode === "edit",
          description:
            "Version of the SOP. this is not modifiable as changing the version would " +
            "affect items already using the SOP. A new SOP should be created to represent a new " +
            "version. The old version can then be archived if it will no longer be used.",
        },
        {
          title: "Category",
          type: "dropdown",
          data: "category",
          required: true,
          source: [
            {
              label: "Sample",
              value: "SAMPLE",
            },
            {
              label: "Library",
              value: "LIBRARY",
            },
            {
              label: "Run",
              value: "RUN",
            },
          ],
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
        },
        {
          title: "URL",
          type: "text",
          data: "url",
          required: true,
          maxLength: 500,
          regex: Utils.validation.uriRegex,
        },
        BulkUtils.columns.archived(),
      ];
    },
  };
})();
