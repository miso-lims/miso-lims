BulkTarget = window.BulkTarget || {};
BulkTarget.sequencingparameters = (function () {
  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function () {
      return Urls.rest.sequencingParameters.bulkSave;
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.sequencingParameters.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "sequencing-parameters");
    },
    getBulkActions: function (config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.sequencingParameters.bulkEdit)];
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
          title: "Instrument Model",
          type: "dropdown",
          data: "instrumentModelId",
          disabled: config.pageMode === "edit",
          source: Constants.instrumentModels.filter(function (x) {
            return x.instrumentType === "SEQUENCER";
          }),
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          sortSource: true,
          required: true,
          onChange: function (rowIndex, newValue, api) {
            var model = Utils.array.findFirstOrNull(
              Utils.array.aliasPredicate(newValue),
              Constants.instrumentModels
            );
            togglePlatformField(api, rowIndex, "chemistry", model, "ILLUMINA", "UNKNOWN");
            togglePlatformField(api, rowIndex, "runType", model, "OXFORDNANOPORE");
            togglePlatformField(api, rowIndex, "movieTime", model, "PACBIO");
            togglePlatformField(api, rowIndex, "flows", model, "ULTIMA");
          },
        },
        {
          title: "Read 1 Length",
          description:
            "For Illumina instruments, read 1 length should be greater than zero. For other platforms, read" +
            " lengths should be set to zero.",
          type: "int",
          data: "read1Length",
          required: true,
          min: 0,
        },
        {
          title: "Read 2 Length",
          description:
            "For Illumina instruments, read 2 should be set to zero for single end, or greater than zero for" +
            " paired end. For other platforms, read lengths should be set to zero.",
          type: "int",
          data: "read2Length",
          required: true,
          min: 0,
        },
        {
          title: "Illumina Chemistry",
          description: 'Should be set for Illumina instruments, and "UNKNOWN" for other platforms.',
          type: "dropdown",
          data: "chemistry",
          source: Constants.illuminaChemistry,
          sortSource: true,
          required: true,
        },
        {
          title: "ONT Run Type",
          description:
            "Should be set for Oxford Nanopore instruments, and blank for other platforms.",
          type: "text",
          data: "runType",
          maxLength: 255,
        },
        {
          title: "PacBio Movie Time",
          description: "Should be set for PacBio instruments, and blank for other platforms.",
          type: "int",
          data: "movieTime",
          min: 0,
        },
        {
          title: "Ultima Flows",
          description: "Should be set for Ultima instruments, and blank for other platforms.",
          type: "int",
          data: "flows",
          min: 0,
          max: 65535,
        },
      ];
    },
  };

  function togglePlatformField(api, rowIndex, fieldName, instrumentModel, platform, value) {
    var options = {
      disabled: false,
    };
    if (!instrumentModel || instrumentModel.platformType !== platform) {
      options.disabled = true;
      options.value = value || null;
    }
    api.updateField(rowIndex, fieldName, options);
  }
})();
