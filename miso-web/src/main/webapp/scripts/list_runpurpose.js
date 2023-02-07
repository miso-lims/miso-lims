ListTarget.runpurpose = {
  name: "Run Purposes",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "run-purposes");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.runpurpose.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Run Purposes", "runpurposes", Utils.array.getAlias)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [ListUtils.createStaticAddAction("Run Purposes", Urls.ui.runPurposes.bulkCreate, true)]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
        include: true,
        iSortPriority: 1,
      },
    ];
  },
};
