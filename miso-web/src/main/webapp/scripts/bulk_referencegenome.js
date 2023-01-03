BulkTarget = window.BulkTarget || {};
BulkTarget.referencegenome = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.referenceGenomes.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.referenceGenomes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'reference-genomes');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.referenceGenomes.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [
        BulkUtils.columns.simpleAlias(255), {
          title: 'Default Sci. Name',
          type: 'dropdown',
          data: 'defaultScientificNameId',
          source: Constants.scientificNames,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          required: true
        }
      ];
    }
  };

})();
