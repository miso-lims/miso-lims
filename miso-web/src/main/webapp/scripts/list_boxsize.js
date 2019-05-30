ListTarget.boxsize = {
  name: "Box Sizes",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.boxsize.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Box Sizes', 'boxsizes', function(size) {
        return size.rows + ' × ' + size.columns + (size.scannable ? ' scannable' : '');
      }));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Box Sizes', 'boxsize')] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Rows',
      mData: 'rows',
      include: true,
      iSortPriority: 0
    }, {
      sTitle: 'Columns',
      mData: 'columns',
      include: true,
      iSortPriority: 0
    }, {
      sTitle: 'Scannable',
      mData: 'scannable',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }];
  }
};
