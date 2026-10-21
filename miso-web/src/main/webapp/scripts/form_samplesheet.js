if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.samplesheet = (function () {
  /*
   * Expected config {
   *   isAdmin: boolean,
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "sample-sheets");
    },
    getSaveUrl: function (sampleSheet) {
      return sampleSheet.id
        ? Urls.rest.sampleSheets.update(sampleSheet.id)
        : Urls.rest.sampleSheets.create;
    },
    getSaveMethod: function (sampleSheet) {
      return sampleSheet.id ? "PUT" : "POST";
    },
    getEditUrl: function (sampleSheet) {
      return Urls.ui.sampleSheets.edit(sampleSheet.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Sample Sheet Information",
          fields: [
            FormUtils.makeIdField("Sample Sheet"),
            {
              title: "Name",
              type: "text",
              data: "name",
              required: true,
              maxLength: 255,
              disabled: !config.isAdmin,
            },
            {
              title: "Platform",
              data: "platformType",
              type: "dropdown",
              required: true,
              source: Constants.platformTypes.filter(function (x) {
                return x.active || x.name === object.platformType;
              }),
              getItemLabel: function (item) {
                return item.key;
              },
              getItemValue: Utils.array.getName,
              disabled: !config.isAdmin,
            },
          ],
        },
      ];
    },
    confirmSave: function (object, isDialog, form) {
      object.parameters = SampleSheet.getParameters();
      object.sections = SampleSheet.getSections();
    },
  };
})();
