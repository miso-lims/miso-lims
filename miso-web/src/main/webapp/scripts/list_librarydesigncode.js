ListTarget.librarydesigncode = {
  name: "Library Design Codes",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.librarydesigncode.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Library Design Codes', 'librarydesigncodes', function(item) {
        return item.code;
      }));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Library Design Codes', 'librarydesigncode')] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Code',
      mData: 'code',
      include: true,
      iSortPriority: 1
    }, {
      sTitle: 'Description',
      mData: 'description',
      include: true,
      iSortPriority: 0
    }, {
      sTitle: 'Targeted Sequencing Required',
      mData: 'targetedSequencingRequired',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.booleanChecks
    }];
  }
};
