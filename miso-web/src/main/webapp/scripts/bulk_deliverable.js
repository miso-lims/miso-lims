BulkTarget = window.BulkTarget || {};
BulkTarget.deliverable = (function () {
  /*
   * Expected config: {
   * pageMode: string {create, edit}
   * isAdmin: boolean
   *  }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.deliverables.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.deliverables.bulkSaveProgress(operationId);
    },
    getUserManualUrls: function () {
      return Urls.external.userManual("type_data", "deliverables");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.deliverables.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Name",
          type: "text",
          data: "name",
          required: true,
          maxLength: 255,
        },
        BulkUtils.columns.makeBoolean("Analysis Review Required", "analysisReviewRequired", true),
      ];
    },
  };
})();
