BulkTarget = window.BulkTarget || {};
BulkTarget.tissuepiecetype = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.tissuePieceTypes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.tissuePieceTypes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "tissue-piece-types");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.tissuePieceTypes.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Name",
          data: "name",
          type: "text",
          required: true,
          maxLength: 500,
        },
        {
          title: "Abbreviation",
          data: "abbreviation",
          type: "text",
          required: true,
          maxLength: 500,
        },
        {
          title: "V2 Naming Code",
          data: "v2NamingCode",
          type: "text",
          required: true,
          maxLength: 2,
        },
        BulkUtils.columns.archived(),
      ];
    },
  };
})(jQuery);
