BulkTarget = window.BulkTarget || {};
BulkTarget.targetedsequencing = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.targetedSequencings.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.targetedSequencings.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "targeted-sequencing");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.targetedSequencings.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(255),
        BulkUtils.columns.description,
        BulkUtils.columns.archived(),
      ];
    },
  };
})();
