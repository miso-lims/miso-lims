ListTarget.changelog = {
  name: "Changes",
  createUrl: function (config, projectId) {
    throw new Error("Static data only");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return [];
  },
  createStaticActions: function (config, projectId) {
    return [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Name",
        mData: "userName",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Summary",
        mData: "summary",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (type === "display" && data.indexOf("\n") > -1) {
            var html = '<ul class="unformatted-list">';
            data.split("\n").forEach(function (item) {
              html += "<li>" + item + "</li>";
            });
            html += "</ul>";
            return html;
          }
          return data;
        },
      },
      {
        sTitle: "Time",
        mData: "time",
        include: true,
        iSortPriority: 1,
      },
    ];
  },
};
