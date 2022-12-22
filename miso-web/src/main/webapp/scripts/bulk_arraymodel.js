BulkTarget = window.BulkTarget || {};
BulkTarget.arraymodel = (function($) {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.arrayModels.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.arrayModels.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'array-models');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.arrayModels.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [BulkUtils.columns.simpleAlias(255), {
        title: 'Rows',
        data: 'rows',
        type: 'int',
        required: true,
        min: 1
      }, {
        title: 'Columns',
        data: 'columns',
        type: 'int',
        required: true,
        min: 1
      }];
    }
  };

})(jQuery);