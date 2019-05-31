ListTarget.referencegenome = {
  name: "Reference Genomes",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.referencegenome.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction('Reference Genomes', 'referencegenomes', Utils.array.getAlias));
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Reference Genomes', 'referencegenome')] : [];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Alias',
      mData: 'alias',
      include: true,
      iSortPriority: 1
    }, {
      sTitle: 'Default Scientific Name',
      mData: 'defaultScientificName',
      include: true,
      iSortPriority: 0
    }];
  }
};
