ListTarget.referencegenome = {
  name: "Reference Genomes",
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'reference-genomes');
  },
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function(config, projectId) {
    var actions = BulkTarget.referencegenome.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Reference Genomes', 'referencegenomes', Utils.array.getAlias));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Reference Genomes', Urls.ui.referenceGenomes.bulkCreate, true)] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Alias',
      mData: 'alias',
      include: true,
      iSortPriority: 1
    }, {
      sTitle: 'Default Scientific Name',
      mData: 'defaultScientificNameId',
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.textFromId(Constants.scientificNames, 'alias', 'n/a')
    }];
  }
};
