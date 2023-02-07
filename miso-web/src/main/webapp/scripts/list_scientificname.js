ListTarget.scientificname = {
  name: "Scientific Names",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "scientific-names");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.scientificname.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Scientific Names",
          "scientificnames",
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
            "Scientific Names",
            Urls.ui.scientificNames.bulkCreate,
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
