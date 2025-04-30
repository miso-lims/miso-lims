BulkTarget = window.BulkTarget || {};
BulkTarget.sampleindex = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.sampleIndices.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.sampleIndices.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "indices");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.sampleIndices.bulkEdit)];
    },
    prepareData: function (data, config) {
      data.forEach(function (index) {
        index.indexFamilyId = config.indexFamily.id;
        index.familyName = config.indexFamily.name;
      });
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Family",
          data: "familyName",
          type: "text",
          disabled: true,
        },
        {
          title: "Name",
          data: "name",
          type: "text",
          maxLength: 24,
          required: true,
        },
      ];
    },
  };
})(jQuery);
