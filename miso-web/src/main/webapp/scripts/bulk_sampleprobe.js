BulkTarget = window.BulkTarget || {};
BulkTarget.sampleprobe = (function () {
  /*
   * Expected config: {
   *   pageMode: string; expected to always be "edit"
   *   sample: sample
   * }
   */

  var CRISPR_LABEL = "CRISPR Guide Capture";

  return {
    getSaveUrl: function (config) {
      return Urls.rest.samples.bulkSaveProbes(config.sampleId);
    },
    getSaveProgressUrl: function (operationId) {
      return Urls.rest.samples.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function () {
      return Urls.external.userManual("samples"); // TODO: update to more specific when available
    },
    getBulkActions: function (config) {
      return [
        {
          name: "Return to Sample Page",
          action: function () {
            Utils.page.pageRedirect(Urls.ui.samples.edit(config.sampleId));
          },
        },
      ];
    },
    getCustomActions: function (config, api) {
      return [
        {
          name: "Cancel Probe Changes",
          action: function () {
            Utils.page.pageRedirect(Urls.ui.samples.edit(config.sampleId));
          },
        },
      ];
    },
    getColumns: function (config, api) {
      return [
        {
          title: "Identifier",
          type: "text",
          data: "identifier",
          required: true,
          maxLength: 255,
        },
        {
          title: "Name",
          type: "text",
          data: "name",
          required: true,
          maxLength: 255,
        },
        {
          title: "Read",
          type: "dropdown",
          data: "read",
          required: true,
          source: ["R1", "R2"],
          initial: "R2",
          initializeOnEdit: true,
        },
        {
          title: "Pattern",
          type: "text",
          data: "pattern",
          required: true,
          maxLength: 50,
          regex: "^(?:5P|\\^)?[NACGT]*\\(BC\\)[NACGT]*(?:3P|\\$)?$",
          description:
            "Defines how to locate the probe within a read." +
            ' May begin with "5P" or "^", OR end with "3P" or "$".' +
            ' Must contain "(BC)".' +
            ' May contain sequences made up of A, C, G, T, and/or N before and/or after the "(BC)".',
        },
        {
          title: "Sequence",
          type: "text",
          data: "sequence",
          required: true,
          maxLength: 255,
          regex: "^[ACGT]+$",
          description: "Can only include the characters [A, C, G, T]",
        },
        {
          title: "Feature Type",
          type: "dropdown",
          data: "featureType",
          required: true,
          source: [
            {
              label: "Antibody Capture",
              value: "ANTIBODY_CAPTURE",
            },
            {
              label: CRISPR_LABEL,
              value: "CRISPR",
            },
            {
              label: "Antigen Capture",
              value: "ANTIGEN_CAPTURE",
            },
            {
              label: "Custom",
              value: "CUSTOM",
            },
          ],
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
          onChange: function (rowIndex, newValue, api) {
            var crispr = newValue === CRISPR_LABEL;
            options = {
              required: crispr,
              disabled: !crispr,
            };
            if (!crispr) {
              options.value = null;
            }
            api.updateField(rowIndex, "targetGeneId", options);
            api.updateField(rowIndex, "targetGeneName", options);
          },
        },
        {
          title: "Target Gene ID",
          type: "text",
          data: "targetGeneId",
          maxLength: 50,
        },
        {
          title: "Target Gene Name",
          type: "text",
          data: "targetGeneName",
          maxLength: 50,
        },
      ];
    },
    manipulateSavedData: function (samples) {
      if (samples.length !== 1) {
        throw Error("Expected 1 sample, but received " + samples.length);
      }
      return samples[0].probes;
    },
  };
})();
