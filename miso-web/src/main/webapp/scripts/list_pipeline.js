ListTarget.pipeline = {
  name: "Pipelines",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "pipelines");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.pipeline.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Pipelines", "pipelines", Utils.array.getAlias)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [ListUtils.createStaticAddAction("Pipelines", Urls.ui.pipelines.bulkCreate)]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
        iSortPriority: 1,
        bSortDirection: true,
      },
    ];
  },
};
