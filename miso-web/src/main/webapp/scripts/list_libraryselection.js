ListTarget.libraryselection = {
  name: "Library Selection Types",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "library-selection-types");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  headerMessage: {
    text: "WARNING: Adding or modifying library selection types may cause your data to be invalid for ENA submission",
    level: "important",
  },
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.libraryselection.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Library Selection Types",
          "libraryselections",
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
            "Library Selection Types",
            Urls.ui.librarySelections.bulkCreate,
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
