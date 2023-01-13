BulkTarget = window.BulkTarget || {};
BulkTarget.metricsubcategory = (function () {
  /*
   * Expected config: {
   *   isAdmin: boolean,
   *   pageMode: {create, edit}
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.metricSubcategories.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.metricSubcategories.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "metric-subcategories");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.metricSubcategories.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(100),
        {
          title: "Category",
          type: "dropdown",
          data: "category",
          source: Constants.metricCategories,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
          required: true,
          disabled: config.pageMode === "edit",
        },
        {
          title: "Design Code",
          type: "dropdown",
          data: "libraryDesignCodeId",
          source: Constants.libraryDesignCodes,
          getItemLabel: Utils.array.get("code"),
          getItemValue: Utils.array.getId,
          description: "Subcategory is only applicable to the selected library design code, if any",
          include: Constants.isDetailedSample,
        },
        {
          title: "Sort Priority",
          type: "int",
          data: "sortPriority",
          min: 1,
          max: 255,
          description:
            "Defines sorting order for subcategories within a category. Lower numbers should be sorted" +
            " higher, and unspecified should be at the bottom. Must be between 1 and 255 inclusive",
        },
      ];
    },
  };
})();
