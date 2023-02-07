if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.array = (function ($) {
  /*
   * Expected config {
   *   arrayModels: array
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("arrays");
    },
    getSaveUrl: function (array) {
      return array.id ? Urls.rest.arrays.update(array.id) : Urls.rest.arrays.create;
    },
    getSaveMethod: function (array) {
      return array.id ? "PUT" : "POST";
    },
    getEditUrl: function (array) {
      return Urls.ui.arrays.edit(array.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Array Information",
          fields: [
            {
              title: "Array ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (array) {
                return array.id || "Unsaved";
              },
            },
            {
              title: "Array Model",
              data: "arrayModelId",
              type: "dropdown",
              include: !object.id,
              required: true,
              source: config.arrayModels,
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
              sortSource: Utils.sorting.standardSort("alias"),
            },
            {
              title: "Array Model",
              data: "arrayModelId",
              type: "read-only",
              include: !!object.id,
              getDisplayValue: function (array) {
                return array.arrayModelAlias;
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Serial Number",
              data: "serialNumber",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
          ],
        },
      ];
    },
  };
})(jQuery);
