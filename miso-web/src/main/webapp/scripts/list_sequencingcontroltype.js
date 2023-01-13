ListTarget.sequencingcontroltype = {
  name: "Sequencing Control Types",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "sequencing-control-types");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.sequencingcontroltype.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Sequencing Control Types",
          "sequencingcontroltypes",
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
            "Sequencing Control Types",
            Urls.ui.sequencingControlTypes.bulkCreate,
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
        iSortPriority: 1,
        bSortDirection: true,
      },
    ];
  },
};
