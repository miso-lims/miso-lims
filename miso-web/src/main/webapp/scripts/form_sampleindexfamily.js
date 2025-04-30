FormTarget = FormTarget || {};
FormTarget.sampleindexfamily = (function ($) {
  /*
   * Expected config {
   *   isAdmin: boolean
   *   pageMode: string ['create', 'edit']
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "indices");
    },
    getSaveUrl: function (family) {
      return family.id
        ? Urls.rest.sampleIndexFamilies.update(family.id)
        : Urls.rest.sampleIndexFamilies.create;
    },
    getSaveMethod: function (family) {
      return family.id ? "PUT" : "POST";
    },
    getEditUrl: function (family) {
      return Urls.ui.sampleIndexFamilies.edit(family.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Sample Index Family Information",
          fields: config.isAdmin
            ? getEditableFields(config, object)
            : getReadOnlyFields(config, object),
        },
      ];
    },
  };

  function getEditableFields(config, object) {
    return [
      {
        title: "Index Family ID",
        data: "id",
        type: "read-only",
        getDisplayValue: function (family) {
          return family.id || "Unsaved";
        },
      },
      {
        title: "Name",
        data: "name",
        type: "text",
        required: true,
        maxLength: 255,
      },
    ];
  }

  function getReadOnlyFields(config, object) {
    $("#save").remove();
    return [
      {
        title: "Index Family ID",
        data: "id",
        type: "read-only",
        getDisplayValue: function (family) {
          return family.id || "Unsaved";
        },
      },
      {
        title: "Name",
        data: "name",
        type: "read-only",
      },
    ];
  }
})(jQuery);
