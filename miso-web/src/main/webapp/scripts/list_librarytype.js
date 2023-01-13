ListTarget.librarytype = {
  name: "Library Types",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "library-types");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.librarytype.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Library Types", "librarytypes", function (item) {
          return item.description;
        })
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [ListUtils.createStaticAddAction("Library Types", Urls.ui.libraryTypes.bulkCreate, true)]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 2,
      },
      {
        sTitle: "Platform",
        mData: "platform",
        include: true,
        iSortPriority: 1,
      },
      {
        sTitle: "Abbreviation",
        mData: "abbreviation",
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
