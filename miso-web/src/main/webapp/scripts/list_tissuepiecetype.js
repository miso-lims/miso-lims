ListTarget.tissuepiecetype = {
  name: "Tissue Piece Types",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "tissue-piece-types");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.tissuepiecetype.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Tissue Piece Types", "tissuepiecetypes", function (item) {
          return item.name;
        })
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          ListUtils.createStaticAddAction(
            "Tissue Piece Types",
            Urls.ui.tissuePieceTypes.bulkCreate,
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
