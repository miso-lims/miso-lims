BulkTarget = window.BulkTarget || {};
BulkTarget.librarytype = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.libraryTypes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.libraryTypes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "library-types");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.libraryTypes.bulkEdit)];
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
        {
          title: "Platform",
          type: "dropdown",
          data: "platform",
          disabled: config.pageMode === "edit",
          source: Constants.platformTypes,
          getItemLabel: Utils.array.get("key"),
          getItemValue: Utils.array.getName,
          sortSource: true,
          required: true,
        },
        {
          title: "Abbreviation",
          type: "text",
          data: "abbreviation",
          maxLength: 5,
        },
        BulkUtils.columns.archived(),
      ];
    },
  };
})();
