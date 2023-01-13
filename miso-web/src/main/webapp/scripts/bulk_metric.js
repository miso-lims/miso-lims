BulkTarget = window.BulkTarget || {};
BulkTarget.metric = (function () {
  /*
   * Expected config: {
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.metrics.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.metrics.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "metrics");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.metrics.bulkEdit)];
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
          onChange: function (rowIndex, newValue, api) {
            var category = Constants.metricCategories.find(function (x) {
              return x.label === newValue;
            });
            var subcategories = !category
              ? []
              : Constants.metricSubcategories.filter(function (x) {
                  return x.category === category.value;
                });
            api.updateField(rowIndex, "subcategoryId", {
              source: subcategories,
              disabled: !subcategories.length,
              value: subcategories.length ? undefined : null,
            });
          },
        },
        {
          title: "Subcategory",
          type: "dropdown",
          data: "subcategoryId",
          getData: function (metric) {
            if (metric.subcategoryId) {
              var subcategory = Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(metric.subcategoryId),
                Constants.metricSubcategories
              );
              return subcategory.alias;
            } else {
              return "";
            }
          },
          source: [], // initialized in category onChange
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          disabled: true,
        },
        {
          title: "Threshold Type",
          type: "dropdown",
          data: "thresholdType",
          source: Constants.thresholdTypes,
          getItemLabel: function (item) {
            return Utils.decodeHtmlString(item.sign);
          },
          getItemValue: Utils.array.get("value"),
          required: true,
        },
        {
          title: "Units",
          type: "text",
          data: "units",
        },
        {
          title: "Sort Priority",
          type: "int",
          data: "sortPriority",
          min: 1,
          max: 255,
          description:
            "Defines sorting order for metrics within a category and subcategory. Lower numbers should be" +
            " sorted higher, and unspecified should be at the bottom. Must be between 1 and 255 inclusive",
        },
        {
          title: "Nucleic Acid Type",
          type: "dropdown",
          data: "nucleicAcidType",
          source: ["DNA", "RNA"],
          description: "Metric only applies to samples of this nucleic acid type if selected",
        },
        {
          title: "Tissue Material",
          type: "dropdown",
          data: "tissueMaterialId",
          source: Constants.tissueMaterials,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
          description: "Metric only applies to samples of this tissue material if selected",
        },
        {
          title: "Tissue Type",
          type: "dropdown",
          data: "tissueTypeId",
          source: Constants.tissueTypes,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
          description:
            "Metric only applies to samples of this tissue type if selected. If negate tissue type is" +
            " selected, applies instead to all EXCEPT the selected tissue type",
        },
        {
          title: "Negate Tissue Type",
          type: "dropdown",
          data: "negateTissueType",
          source: [
            {
              label: "No",
              value: false,
            },
            {
              label: "Yes",
              value: true,
            },
          ],
          required: true,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
          include: Constants.isDetailedSample,
          description: "If yes, the metric applies to all EXCEPT the selected tissue type",
        },
        {
          title: "Tissue Origin",
          type: "dropdown",
          data: "tissueOriginId",
          source: Constants.tissueOrigins,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          include: Constants.isDetailedSample,
          description: "Metric only applies to samples of this tissue origin if selected",
        },
        {
          title: "Container Model",
          type: "dropdown",
          data: "containerModelId",
          source: Constants.containerModels,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          description: "Metric only applies to runs of this container model if selected",
        },
        {
          title: "Read 1 Length",
          type: "int",
          data: "readLength",
          min: 0,
          description: "Metric only applies to runs with this read 1 length if specified",
        },
        {
          title: "Read 2 Length",
          type: "int",
          data: "readLength2",
          min: 0,
          description:
            "Metric only applies to runs with this read 2 length if specified. Use zero (0) to indicate" +
            " single end",
        },
      ];
    },
  };
})();
