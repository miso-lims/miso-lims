BulkTarget = window.BulkTarget || {};
BulkTarget.boxsize = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.boxSizes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.boxSizes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "box-sizes");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.boxSizes.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Rows",
          data: "rows",
          type: "int",
          required: true,
          min: 1,
        },
        {
          title: "Columns",
          data: "columns",
          type: "int",
          required: true,
          min: 1,
        },
        {
          title: "Box Type",
          data: "boxType",
          type: "dropdown",
          source: config.boxTypes,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.getName,
          initial: "Storage",
          required: true,
        },
        {
          title: "Scannable",
          data: "scannable",
          type: "dropdown",
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
          initial: "False",
          required: true,
        },
      ];
    },
  };
})(jQuery);
