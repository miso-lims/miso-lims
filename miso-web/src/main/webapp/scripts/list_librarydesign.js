ListTarget.librarydesign = {
  name: "Library Designs",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "library-designs");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.librarydesign.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Library Designs", "librarydesigns", Utils.array.getName)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          ListUtils.createStaticAddAction(
            "Library Designs",
            Urls.ui.libraryDesigns.bulkCreate,
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
        iSortPriority: 1,
      },
      {
        sTitle: "Sample Class",
        mData: "sampleClassAlias",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Selection",
        mData: "selectionName",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Strategy",
        mData: "strategyName",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Design Code",
        mData: "designCodeLabel",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
