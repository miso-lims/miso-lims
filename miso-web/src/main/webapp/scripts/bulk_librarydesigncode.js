BulkTarget = window.BulkTarget || {};
BulkTarget.librarydesigncode = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  var originalDataByRow = {};

  return {
    getSaveUrl: function () {
      return Urls.rest.libraryDesignCodes.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.libraryDesignCodes.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("libraries", "library-designs");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.libraryDesignCodes.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Code",
          type: "text",
          data: "code",
          required: true,
          maxLength: 2,
        },
        BulkUtils.columns.description,
        BulkUtils.columns.makeBoolean(
          "Targeted Sequencing Req'd",
          "targetedSequencingRequired",
          true,
          true
        ),
      ];
    },
  };
})(jQuery);
