ListTarget.detailedqcstatus = {
  name: "Detailed QC Statuses",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.detailedqcstatus.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Detailed QC Statuses', 'detailedqcstatuses', function(status) {
        return status.description;
      }));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Detailed QC Statuses', 'detailedqcstatus')] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Description',
      mData: 'description',
      include: true,
      iSortPriority: 1
    }, {
      sTitle: 'QC Passed?',
      mData: 'status',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }, {
      sTitle: 'Note Required?',
      mData: 'noteRequired',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }];
  }
};
