ListTarget.issue = {
  name: "Issues",
  createUrl: function (config, projectId) {
    throw new Error("Static data only");
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
        sTitle: "Key",
        mData: "key",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (type === "display") {
            return '<a href="' + full.url + '">' + data + "</a>";
          }
          return data;
        },
      },
      {
        sTitle: "Summary",
        mData: "summary",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (type === "display") {
            return '<a href="' + full.url + '">' + data + "</a>";
          }
          return data;
        },
      },
      {
        sTitle: "Status",
        mData: "status",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Assignee",
        mData: "assignee",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Last Updated",
        mData: "lastUpdated",
        include: true,
        iSortPriority: 1,
      },
    ];
  },
};
