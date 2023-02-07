ListTarget.arrayrun = {
  name: "Array Runs",
  getUserManualUrl: function () {
    return Urls.external.userManual("array_runs");
  },
  createUrl: function (config, projectId) {
    return projectId
      ? Urls.rest.arrayRuns.projectDatatable(projectId)
      : Urls.rest.arrayRuns.datatable;
  },
  createBulkActions: function (config, projectId) {
    return [
      ListUtils.createBulkDeleteAction("Array Runs", "arrayruns", function (run) {
        return run.alias;
      }),
    ];
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Add",
        handler: function () {
          window.location = Urls.ui.arrayRuns.create;
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.idHyperlinkColumn("ID", Urls.ui.arrayRuns.edit, "id", Utils.array.getId, 0, true),
      ListUtils.idHyperlinkColumn(
        "Alias",
        Urls.ui.arrayRuns.edit,
        "id",
        Utils.array.getAlias,
        0,
        true
      ),
      {
        sTitle: "Status",
        mData: "status",
        mRender: function (data, type, full) {
          return data || "";
        },
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Start Date",
        mData: "startDate",
        mRender: function (data, type, full) {
          return data || "";
        },
        include: true,
        iSortPriority: 2,
      },
      {
        sTitle: "End Date",
        mData: "completionDate",
        mRender: function (data, type, full) {
          return data || "";
        },
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Modified",
        mData: "lastModified",
        include: Constants.isDetailedSample,
        iSortPriority: 0,
      },
    ];
  },
};
