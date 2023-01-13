ListTarget.librarystrategy = {
  name: "Library Strategy Types",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "library-strategy-types");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  headerMessage: {
    text: "WARNING: Adding or modifying library strategy types may cause your data to be invalid for ENA submission",
    level: "important",
  },
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.librarystrategy.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Library Strategy Types",
          "librarystrategies",
          Utils.array.getName
        )
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          ListUtils.createStaticAddAction(
            "Library Strategy Types",
            Urls.ui.libraryStrategies.bulkCreate,
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
        iSortPriority: 1,
      },
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
