ListTarget.librarydesigncode = {
  name: "Library Design Codes",
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'library-designs');
  },
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function(config, projectId) {
    var actions = BulkTarget.librarydesigncode.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Library Design Codes', 'librarydesigncodes', function(item) {
        return item.code;
      }));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Library Design Codes', Urls.ui.libraryDesignCodes.bulkCreate, true)] : [];
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
