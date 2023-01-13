BulkTarget = window.BulkTarget || {};
BulkTarget.stain = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   *   stainCategories: array
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.stains.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.stains.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "stains");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.stains.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Name",
          data: "name",
          type: "text",
          required: true,
          maxLength: 20,
        },
        {
          title: "Category",
          data: "categoryId",
          type: "dropdown",
          source: config.stainCategories,
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: true,
        },
      ];
    },
  };
})(jQuery);
