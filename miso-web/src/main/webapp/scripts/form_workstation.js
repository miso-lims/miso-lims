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
      return Urls.external.userManual("workstations");
    },
    getSaveUrl: function (workstation) {
      return Urls.rest.workstations.update(workstation.id);
    },
    getSaveMethod: function (workstation) {
      return "PUT";
    },
    getEditUrl: function (workstation) {
      return Urls.ui.workstations.edit(workstation.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Workstation Information",
          fields: config.isAdmin
            ? getAdminFields(config, object)
            : getReadOnlyFields(config, object),
        },
      ];
    },
  };

  function getAdminFields(config, object) {
    return [
      {
        title: "Workstation ID",
        data: "id",
        type: "read-only",
      },
      BulkUtils.columns.simpleAlias(50),
      BulkUtils.columns.description,
      {
        title: "Identification Barcode",
        data: "identificationBarcode",
        type: "text",
        maxLength: 255,
      },
    ];
  }

  function getReadOnlyFields(config, object) {
    return [
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
    ];
  }
})(jQuery);
