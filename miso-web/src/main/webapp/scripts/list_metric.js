ListTarget.metric = (function () {
  var TYPE_LABEL = "Metrics";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "metrics");
    },
    showNewOptionSop: true,
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.metric.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(ListUtils.createBulkDeleteAction(TYPE_LABEL, "metrics", Utils.array.getAlias));
      }
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return config.isAdmin
        ? [ListUtils.createStaticAddAction(TYPE_LABEL, Urls.ui.metrics.bulkCreate, true)]
        : [];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Alias",
          mData: "label",
          iSortPriority: 1,
          bSortDirection: true,
        },
        {
          sTitle: "Category",
          mData: "category",
          mRender: function (data, type, full) {
            var category = Utils.array.findUniqueOrThrow(function (x) {
              return x.value === data;
            }, Constants.metricCategories);
            return category.label;
          },
        },
        {
          sTitle: "Subcategory",
          mData: "subcategoryId",
          mRender: ListUtils.render.textFromId(Constants.metricSubcategories, "alias", ""),
        },
        {
          sTitle: "Threshold Type",
          mData: "thresholdType",
          mRender: function (data, type, full) {
            var category = Utils.array.findUniqueOrThrow(function (x) {
              return x.value === data;
            }, Constants.thresholdTypes);
            return category.sign;
          },
          bSortable: false,
        },
        {
          sTitle: "Units",
          mData: "units",
          sDefaultContent: "n/a",
        },
      ];
    },
  };
})();
