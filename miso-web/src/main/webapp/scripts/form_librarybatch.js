if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.librarybatch = (function ($) {
  /*
   * Expected config {
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("libraries");
    },
    getSaveUrl: function (library) {
      throw new Error("Batches cannot be saved");
    },
    getSaveMethod: function (library) {
      throw new Error("Batches cannot be saved");
    },
    getEditUrl: function (batch) {
      return Urls.ui.libraries.batch(batch.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Batch Information",
          fields: [
            {
              title: "Batch ID",
              data: "batchId",
              type: "read-only",
            },
            {
              title: "Creation Date",
              data: "date",
              type: "read-only",
            },
            {
              title: "Creator",
              data: "username",
              type: "read-only",
            },
            {
              title: "SOP",
              data: "sopLabel",
              type: "read-only",
              getLink: function (batch) {
                return batch.sopUrl;
              },
            },
            {
              title: "Kit",
              data: "kitName",
              type: "read-only",
            },
            {
              title: "Kit Lot",
              data: "kitLot",
              type: "read-only",
            },
          ],
        },
      ];
    },
  };
})(jQuery);
