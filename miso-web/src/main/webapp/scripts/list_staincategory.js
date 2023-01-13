ListTarget.staincategory = {
  name: "Stain Categories",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "stains");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.staincategory.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Stain Categories", "staincategories", Utils.array.getName)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          ListUtils.createStaticAddAction(
            "Stain Categories",
            Urls.ui.stainCategories.bulkCreate,
            true
          ),
        ]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Name",
        mData: "name",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
