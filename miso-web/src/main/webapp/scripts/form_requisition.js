if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.requisition = (function () {
  /*
   * Expected config {
   *   isAdmin: boolean
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("requisitions");
    },
    getSaveUrl: function (requisition) {
      return requisition.id
        ? Urls.rest.requisitions.update(requisition.id)
        : Urls.rest.requisitions.create;
    },
    getSaveMethod: function (requisition) {
      return requisition.id ? "PUT" : "POST";
    },
    getEditUrl: function (requisition) {
      return Urls.ui.requisitions.edit(requisition.id);
    },
    getSections: function (config, object) {
      /* adding the currently assigned assay to the requisition to the options of assays to choose from, because it
           may not be part of the set of mutual assays between all the requisitioned samples' assays. */
      var assayDropdown = [];
      if (config.potentialAssayIds !== undefined) {
        for (var i = 0; i < config.potentialAssayIds.length; i++) {
          var holder = Constants.assays.find(function (x) {
            return x.id === config.potentialAssayIds[i];
          });
          assayDropdown.push(holder);
        }
      }

      return [
        {
          title: "Assay Information",
          fields: [
            FormUtils.makeIdField("Requisition"),
            {
              title: "Alias",
              type: "text",
              data: "alias",
              required: true,
              maxLength: 150,
              description:
                "Should usually match the identifier of a requisition form stored in a separate system",
            },
            {
              title: "Assay",
              type: "dropdown",
              data: "assayId",
              source:
                config.numberOfRequisitionedSamples > 0
                  ? assayDropdown
                  : Constants.assays.filter(function (x) {
                      return !x.archived || x.id === object.assayId;
                    }),
              getItemLabel: function (x) {
                return x.alias + " v" + x.version;
              },
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("alias"),
            },
            {
              title: "Stopped",
              type: "checkbox",
              data: "stopped",
              onChange: function (newValue, form) {
                var options = {
                  disabled: !newValue,
                  required: newValue,
                };
                if (!newValue) {
                  options.value = null;
                }
                form.updateField("stopReason", options);
              },
            },
            {
              title: "Stop Reason",
              type: "text",
              data: "stopReason",
            },
          ],
        },
      ];
    },
  };
})();
