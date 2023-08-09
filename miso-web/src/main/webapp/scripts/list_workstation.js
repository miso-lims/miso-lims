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
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.workstations.view,
        Utils.array.getId,
        "alias",
        1,
        true
      ),
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
