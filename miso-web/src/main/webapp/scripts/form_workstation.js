if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.workstation = (function () {
  /*
   * Expected config {
   * isAdmin: boolean
   * }
   */

  return {
    getUserManualUrl: function () {
      Urls.external.userManual("workstations");
    },
    getEditUrl: function (workstation) {
      return Urls.ui.workstations.view(workstation.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Workstation Information",
          fields: [
            {
              title: "Workstation ID",
              data: "id",
              type: "read-only",
            },
            {
              title: "Alias",
              data: "alias",
              type: "read-only",
            },
            {
              title: "Description",
              data: "description",
              type: "read-only",
            },
            {
              title: "Identification Barcode",
              data: "identificationBarcode",
              type: "read-only",
            },
          ],
        },
      ];
    },
  };
})(jQuery);
