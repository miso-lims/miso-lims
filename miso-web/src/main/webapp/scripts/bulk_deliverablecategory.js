BulkTarget = window.BulkTarget || {};
BulkTarget.deliverablecategory = (function () {
  /*
   * Expected config: {
   * pageMode: string {create, edit}
   * isAdmin: boolean
   *  }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.deliverableCategories.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.deliverableCategories.bulkSaveProgress(operationId);
    },
    getUserManualUrls: function () {
      return Urls.external.userManual("type_data", "deliverables");
    },
    getBulkActions: function (config) {
      return !config.isAdmin
        ? []
        : [BulkUtils.actions.edit(Urls.ui.deliverableCategories.bulkEdit)];
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
      ];
    },
  };
})();
