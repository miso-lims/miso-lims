ListTarget.sampletype = {
  name: "Sample Types",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "sample-types");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  headerMessage: {
    text: "WARNING: Adding or modifying sample types may cause your data to be invalid for ENA submission",
    level: "important",
  },
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.sampletype.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Sample Types", "sampletypes", Utils.array.getName)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [ListUtils.createStaticAddAction("Sample Types", Urls.ui.sampleTypes.bulkCreate, true)]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Name",
        mData: "name",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Archived",
        mData: "archived",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.archived,
      },
    ];
  },
};
