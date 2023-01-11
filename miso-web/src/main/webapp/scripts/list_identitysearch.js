ListTarget.identitysearch = (function($) {

  return {
    name: "Identity Results",
    createBulkActions: function(config, projectId) {
      return [
          {
            name: 'Filter Below',
            action: function(items) {
              var idString = items.map(function(identity) {
                return identity.id;
              }).join(',');
              $('#list_samples').dataTable().fnFilter('identityId:' + idString);
            }
          },
          BulkUtils.actions.children(Urls.rest.samples.children, BulkUtils.relations.categoriesForDetailed().concat(
              [BulkUtils.relations.library(), BulkUtils.relations.libraryAliquot(), BulkUtils.relations.pool(), BulkUtils.relations.run()]))];
    },
    createStaticActions: function(config, projectId) {
      return [];
    },
    createColumns: function(config, projectId) {
      return [ListUtils.idHyperlinkColumn('Alias', Urls.ui.samples.edit, 'id', Utils.array.getAlias, 0, true), {
        sTitle: 'External Name',
        mData: 'externalName'
      }];
    }
  };

})(jQuery);
