ListTarget.tissuetype = {
  name: "Tissue Types",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "tissue-types");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  queryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.tissuetype.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Tissue Types", "tissuetypes", function (type) {
          return type.alias + " (" + type.description + ")";
        })
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [ListUtils.createStaticAddAction("Tissue Types", Urls.ui.tissueTypes.bulkCreate, true)]
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
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
