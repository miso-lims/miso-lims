ListTarget.worksetstage = {
  name: "Workset Stages",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "workset-stages");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.worksetstage.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Workset Stages", "worksetstages", Utils.array.getAlias)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [ListUtils.createStaticAddAction("Workset Stages", Urls.ui.worksetStages.bulkCreate, true)]
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
