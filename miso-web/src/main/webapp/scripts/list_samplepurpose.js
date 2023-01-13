ListTarget.samplepurpose = {
  name: "Sample Purposes",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "sample-purposes");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.samplepurpose.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Sample Purposes", "samplepurposes", Utils.array.getAlias)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          ListUtils.createStaticAddAction(
            "Sample Purposes",
            Urls.ui.samplePurposes.bulkCreate,
            true
          ),
        ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
      },
      {
        sTitle: "Archived",
        mData: "archived",
        mRender: ListUtils.render.archived,
      },
    ];
  },
};
