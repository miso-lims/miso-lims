ListTarget.libraryspikein = {
  name: "Library Spike-Ins",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "library-spike-ins");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.libraryspikein.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Library Spike-Ins",
          "libraryspikeins",
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
            "Library Spike-Ins",
            Urls.ui.librarySpikeIns.bulkCreate,
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
      },
    ];
  },
};
