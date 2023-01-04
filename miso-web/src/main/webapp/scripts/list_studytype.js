ListTarget.studytype = {
  name: "Study Types",
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'study-types');
  },
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  headerMessage: {
    text: 'WARNING: Adding or modifying study types may cause your data to be invalid for ENA submission',
    level: 'important'
  },
  showNewOptionSop: true,
  createBulkActions: function(config, projectId) {
    var actions = BulkTarget.studytype.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Study Types', 'studytypes', Utils.array.getName));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Study Types', Urls.ui.studyTypes.bulkCreate, true)] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Name',
      mData: 'name',
      include: true,
      iSortPriority: 1
    }];
  }
};
