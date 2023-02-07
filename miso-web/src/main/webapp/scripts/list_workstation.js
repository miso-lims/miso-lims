ListTarget.workstation = {
  name: "Workstations",
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.workstation.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Workstations", "workstations", Utils.array.getAlias)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [ListUtils.createStaticAddAction("Workstations", Urls.ui.workstations.bulkCreate, true)]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
        include: true,
        iSortPriority: 2,
        bSortDirection: false,
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
