ListTarget.runpurpose = {
  name: "Run Purposes",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.runpurpose.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Run Purposes', 'runpurposes', Utils.array.getAlias));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Run Purposes', 'runpurpose')] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Alias',
      mData: 'alias',
      include: true,
      iSortPriority: 1
    }];
  }
};
