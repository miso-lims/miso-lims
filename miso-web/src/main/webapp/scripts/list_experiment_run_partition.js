ListTarget.experiment_run_partition = {
  name: "Run + Partition",
  createUrl: function (config, projectId) {
    throw new Error("Must be created statically.");
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
      ListUtils.idHyperlinkColumn(
        "Run Name",
        Urls.ui.runs.edit,
        "run.id",
        function (pair) {
          return pair.run.name;
        },
        1,
        true
      ),
      ListUtils.labelHyperlinkColumn(
        "Run Alias",
        Urls.ui.runs.edit,
        function (pair) {
          return pair.run.id;
        },
        "run.alias",
        0,
        true
      ),
      {
        sTitle: "Status",
        mData: "run.status",
        mRender: function (data, type, full) {
          return data || "";
        },
        include: true,
        iSortPriority: 0,
      },
      ListUtils.labelHyperlinkColumn(
        "Container",
        Urls.ui.containers.edit,
        function (pair) {
          return pair.partition.containerId;
        },
        "partition.containerName",
        0,
        true
      ),
      {
        sTitle: "Number",
        mData: "partition.partitionNumber",
        include: true,
        iSortPriority: 1,
        bSortDirection: true,
      },
      {
        sTitle: "Pool",
        mData: function (full, type) {
          return full.partition.pool.name + " (" + full.partition.pool.alias + ")";
        },
        include: true,
        iSortPriority: 0,
        mRender: Warning.tableWarningRenderer(
          WarningTarget.experiment_run_partition,
          function (data) {
            return Urls.ui.pools.edit(data.partition.pool.id);
          }
        ),
      },
    ];
  },
};
