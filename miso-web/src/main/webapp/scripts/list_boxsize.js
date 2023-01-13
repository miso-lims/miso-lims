ListTarget.boxsize = {
  name: "Box Sizes",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "box-sizes");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.boxsize.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Box Sizes", "boxsizes", function (size) {
          return size.rows + " × " + size.columns + (size.scannable ? " scannable" : "");
        })
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [ListUtils.createStaticAddAction("Box Sizes", Urls.ui.boxSizes.bulkCreate, true)]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Rows",
        mData: "rows",
      },
      {
        sTitle: "Columns",
        mData: "columns",
      },
      {
        sTitle: "Type",
        mData: "boxTypeLabel",
      },
      {
        sTitle: "Scannable",
        mData: "scannable",
        mRender: ListUtils.render.booleanChecks,
      },
    ];
  },
};
