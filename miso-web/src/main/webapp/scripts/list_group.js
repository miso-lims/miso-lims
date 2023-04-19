ListTarget.group = {
  name: "Groups",
  getUserManualUrl: function () {
    return Urls.external.userManual("users_and_groups", "groups");
  },
  createUrl: function (config, projectId) {
    throw new Error("Static data only");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    if (config.isAdmin && !config.userId) {
      return [ListUtils.createBulkDeleteAction("Groups", "groups", Utils.array.getName)];
    } else {
      return [];
    }
  },
  createStaticActions: function (config, projectId) {
    if (config.isAdmin && !config.userId) {
      return [
        {
          name: "Add",
          handler: function () {
            window.location = Urls.ui.groups.create;
          },
        },
      ];
    } else {
      return [];
    }
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Name",
        mData: "name",
        include: true,
        iSortPriority: 1,
        bSortDirection: true,
        mRender: function (data, type, full) {
          if (config.isAdmin) {
            return '<a href="' + Urls.ui.groups.edit(full.id) + '">' + data + "</a>";
          } else {
            return data;
          }
        },
      },
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
