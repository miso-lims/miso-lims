BulkTarget = window.BulkTarget || {};
BulkTarget.subproject = (function ($) {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.subprojects.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.subprojects.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("projects", "subprojects");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.subprojects.bulkEdit)];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Project",
          type: "dropdown",
          data: "parentProjectId",
          source: config.projects,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          required: true,
          disabled: config.pageMode === "edit",
        },
        BulkUtils.columns.simpleAlias(255),
        BulkUtils.columns.description,
        BulkUtils.columns.makeBoolean("Priority", "priority", true, true),
        {
          title: "Reference Genome",
          data: "referenceGenomeId",
          type: "dropdown",
          source: Constants.referenceGenomes,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          required: true,
        },
      ];
    },
  };
})(jQuery);
