BulkTarget = window.BulkTarget || {};
BulkTarget.contact = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.contacts.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.contacts.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "contacts");
    },
    getBulkActions: function (config) {
      return [BulkUtils.actions.edit(Urls.ui.contacts.bulkEdit)];
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
        {
          title: "Email",
          type: "text",
          data: "email",
          required: true,
          maxLength: 255,
          regex: Utils.validation.emailRegex,
        },
      ];
    },
  };
})();
