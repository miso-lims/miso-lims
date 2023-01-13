ListTarget.metricsubcategory = (function () {
  var TYPE_LABEL = "Metric Subcategories";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "metric-subcategories");
    },
    showNewOptionSop: true,
    createBulkActions: function (config) {
      var actions = BulkTarget.metricsubcategory.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(
          ListUtils.createBulkDeleteAction(TYPE_LABEL, "metricsubcategories", makeLabel)
        );
      }
      return actions;
    },
    createStaticActions: function (config) {
      return config.isAdmin
        ? [
            ListUtils.createStaticAddAction(
              TYPE_LABEL,
              Urls.ui.metricSubcategories.bulkCreate,
              true
            ),
          ]
        : [];
    },
    createColumns: function (config) {
      return [
        {
          sTitle: "Alias",
          mData: "alias",
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
          iSortPriority: 1,
          bSortDirection: true,
        },
        {
          sTitle: "Design Code",
          mData: "libraryDesignCodeId",
          mRender: ListUtils.render.textFromId(Constants.libraryDesignCodes, "code", "n/a"),
          include: Constants.isDetailedSample,
        },
        {
          sTitle: "Sort Priority",
          mData: "sortPriority",
          sDefaultContent: "n/a",
          iSortPriority: 2,
          bSortDirection: true,
        },
      ];
    },
  };

  function makeLabel(subcategory) {
    var category = Utils.array.findUniqueOrThrow(
      Utils.array.get("value"),
      Constants.metricCategories
    );
    return subcategory.alias + " (" + category.label + ")";
  }
})();
