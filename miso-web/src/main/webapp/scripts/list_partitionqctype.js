ListTarget.partitionqctype = {
  name: "PartitionQC Types",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.partitionqctype.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Partition QC Types', 'partitionqctypes', function(type) {
        return type.description;
      }));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Partition QC Types', 'partitionqctype')] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Description',
      mData: 'description',
      include: true,
      iSortPriority: 0
    }, {
      sTitle: 'Note Required',
      mData: 'noteRequired',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }, {
      sTitle: 'Order Fulfilled',
      mData: 'orderFulfilled',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }, {
      sTitle: 'Analysis Skipped',
      mData: 'analysisSkipped',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }];
  }
};
