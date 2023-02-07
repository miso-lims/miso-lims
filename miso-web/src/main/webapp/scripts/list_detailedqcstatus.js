ListTarget.detailedqcstatus = {
  name: "Detailed QC Statuses",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "detailed-qc-status");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.detailedqcstatus.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Detailed QC Statuses",
          "detailedqcstatuses",
          Utils.array.get("description")
        )
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          ListUtils.createStaticAddAction(
            "Detailed QC Statuses",
            Urls.ui.detailedQcStatuses.bulkCreate,
            true
          ),
        ]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 1,
      },
      {
        sTitle: "QC Passed",
        mData: "status",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.booleanChecks,
      },
      {
        sTitle: "Note Required",
        mData: "noteRequired",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.booleanChecks,
      },
    ];
  },
};
