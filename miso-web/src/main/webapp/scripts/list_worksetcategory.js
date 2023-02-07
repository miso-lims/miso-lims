ListTarget.worksetcategory = {
  name: "Workset Categories",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "workset-categories");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.worksetcategory.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Workset Categories",
          "worksetcategories",
          Utils.array.getAlias
        )
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          ListUtils.createStaticAddAction(
            "Workset Categories",
            Urls.ui.worksetCategories.bulkCreate,
            true
          ),
        ]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
        include: true,
        iSortPriority: 1,
        bSortDirection: true,
      },
    ];
  },
};
