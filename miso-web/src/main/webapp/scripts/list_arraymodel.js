ListTarget.arraymodel = {
  name: "Array Models",
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'array-models');
  },
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function(config, projectId) {
    var actions = BulkTarget.arraymodel.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Array Models', 'arraymodels', Utils.array.getAlias));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Array Models', Urls.ui.arrayModels.bulkCreate, true)] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Alias',
      mData: 'alias',
      include: true,
      iSortPriority: 1
    }, {
      sTitle: 'Rows',
      mData: 'rows',
      include: true,
      iSortPriority: 0
    }, {
      sTitle: 'Columns',
      mData: 'columns',
      include: true,
      iSortPriority: 0
    }];
  }
};
