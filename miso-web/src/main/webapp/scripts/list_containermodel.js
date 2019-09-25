ListTarget.containermodel = {
  name: "Container Models",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.containermodel.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Container Models', 'containermodels', function(model) {
        var platform = Utils.array.findUniqueOrThrow(Utils.array.namePredicate(model.platformType), Constants.platformTypes);
        return model.alias + ' (' + platform.key + ')';
      }));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Container Models', 'containermodel')] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Alias',
      mData: 'alias',
      include: true,
      iSortPriority: 1
    }, {
      sTitle: 'Platform',
      mData: 'platformType',
      include: true,
      iSortPriority: 0
    }, {
      sTitle: 'Fallback',
      mData: 'fallback',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }, {
      sTitle: 'Archived',
      mData: 'archived',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }];
  }
};
