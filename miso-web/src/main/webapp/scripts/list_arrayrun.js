ListTarget.arrayrun = {
  name: "Array Runs",
  getUserManualUrl: function () {
    return Urls.external.userManual("array_runs");
  },
  createUrl: function (config, projectId) {
    if (projectId) {
      return Urls.rest.arrayRuns.projectDatatable(projectId);
    } else if (config.requisitionId) {
      return Urls.rest.arrayRuns.requisitionDatatable(config.requisitionId); // if the config field isn't there, add it later/fix it wherever it needs to be fixed
    } else {
      return Urls.rest.arrayRuns.datatable;
    }

    // return projectId
    //   ? Urls.rest.arrayRuns.projectDatatable(projectId)
    //   : Urls.rest.arrayRuns.datatable;

    // add one here for the requisitions page
    // edit this to take in a string to specify project or requisition
    // you could make it a boolean but that's less robust, an int would work though
    // js has switch cases
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
  onFirstLoad: function (data, config) {
    if (config.collapseId && !data.length) {
      Utils.ui.collapse("#" + config.collapseId, "#" + config.collapseId + "_arrowclick");
    }
  },
};
