BulkTarget = window.BulkTarget || {};
BulkTarget.attachmentcategory = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.attachmentCategories.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.attachmentCategories.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'attachment-categories');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.attachmentCategories.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [BulkUtils.columns.simpleAlias(255)];
    }
  };

})();
