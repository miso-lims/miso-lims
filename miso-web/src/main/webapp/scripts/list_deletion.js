ListTarget.deletion = {
  name: "Deletions",
  getUserManualUrl: function () {
    return Urls.external.userManual("deletions");
  },
  createUrl: function (config, projectId) {
    return Urls.rest.deletions.datatable;
  },
  createBulkActions: function (config, projectId) {
    return [];
  },
  createStaticActions: function (config, projectId) {
    return [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Type",
        mData: "targetType",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "ID",
        mData: "targetId",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Deleted By",
        mData: "userName",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Time",
        mData: "changeTime",
        include: true,
        iSortPriority: 1,
      },
    ];
  },
};
