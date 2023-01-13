BulkTarget = window.BulkTarget || {};
BulkTarget.sequencingcontroltype = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.sequencingControlTypes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.sequencingControlTypes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "sequencing-control-types");
    },
    getBulkActions: function (config) {
      return !config.isAdmin
        ? []
        : [BulkUtils.actions.edit(Urls.ui.sequencingControlTypes.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [BulkUtils.columns.simpleAlias(255)];
    },
  };
})(jQuery);
