ListTarget.sequencingparameters = {
  name: "Sequencing Parameters",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "sequencing-parameters");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.sequencingparameters.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Sequencing Parameters",
          "sequencingparameters",
          function (item) {
            return item.name + " (" + item.instrumentModelAlias + ")";
          }
        )
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          ListUtils.createStaticAddAction(
            "Sequencing Parameters",
            Urls.ui.sequencingParameters.bulkCreate,
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
        iSortPriority: 2,
      },
      {
        sTitle: "Instrument Model",
        mData: "instrumentModelAlias",
        include: true,
        iSortPriority: 1,
      },
    ];
  },
};
