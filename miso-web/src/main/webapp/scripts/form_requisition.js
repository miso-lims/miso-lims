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
              source: Constants.assays.filter(function (x) {
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
            },
          ],
        },
      ];
    },
  };
})();
