ListTarget.sop = {
  name: "SOPs",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "standard-operating-procedures");
  },
  createUrl: function (config, projectId) {
    return Urls.rest.sops.categoryDatatable(config.category);
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.sop.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction("SOPs", "sops", Utils.array.getAlias));
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction("SOPs", Urls.ui.sops.bulkCreate)] : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
        iSortPriority: 1,
        bSortDirection: true,
      },
      {
        sTitle: "Version",
        mData: "version",
        iSortPriority: 2,
        bSortDirection: true,
      },
      {
        sTitle: "SOP",
        mData: "url",
        mRender: function (data, type, full) {
          if (type === "display" && data) {
            return '<a href="' + data + '">View SOP</a>';
          }
          return data || "";
        },
      },
      {
        sTitle: "Archived",
        mData: "archived",
        mRender: ListUtils.render.archived,
      },
    ];
  },
  searchTermSelector: function (searchTerms) {
    return [searchTerms["id"]];
  },
};
