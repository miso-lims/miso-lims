ListTarget.libraryindexfamily = {
  name: "Library Index Families",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "indices");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          ListUtils.createBulkDeleteAction(
            "Library Index Families",
            "libraryindexfamilies",
            Utils.array.getName
          ),
        ];
  },
  createStaticActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          {
            name: "Add",
            handler: function () {
              window.location = Urls.ui.libraryIndexFamilies.create;
            },
          },
        ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.labelHyperlinkColumn(
        "Name",
        Urls.ui.libraryIndexFamilies.edit,
        Utils.array.getId,
        "name",
        1,
        true
      ),
      {
        sTitle: "Platform",
        mData: "platformType",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.platformType,
      },
      {
        sTitle: "Multi-Sequence Indices",
        mData: "fakeSequence",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.booleanChecks,
      },
      {
        sTitle: "Unique Dual Indices",
        mData: "uniqueDualIndex",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.booleanChecks,
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
