BulkTarget = window.BulkTarget || {};
BulkTarget.contactRole = (function () {
  /*
   * Expected config: {
   * pageMode: string {create, edit}
   * isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.contactRoles.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.contactRoles.bulkSaveProgress(operationId);
    },
    getUserManualUrls: function () {
      return Urls.external.userManual("type_data", "contactRoles");
    },
    getBulkActions: function (config) {
      return [BulkUtils.actions.edit(Urls.ui.contactRoles.bulkEdit)];
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
