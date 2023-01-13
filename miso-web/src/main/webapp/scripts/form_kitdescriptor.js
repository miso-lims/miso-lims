if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.kitdescriptor = (function ($) {
  /*
   * Expected config {
   *   isAdmin: boolean
   *   kitTypes: array (only required if creating a new kitdescriptor)
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "kit-descriptors");
    },
    getSaveUrl: function (kit) {
      return kit.id ? Urls.rest.kitDescriptors.update(kit.id) : Urls.rest.kitDescriptors.create;
    },
    getSaveMethod: function (kit) {
      return kit.id ? "PUT" : "POST";
    },
    getEditUrl: function (kit) {
      return Urls.ui.kitDescriptors.edit(kit.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Kit Descriptor Information",
          fields: config.isAdmin
            ? getAdminFields(config, object)
            : getReadOnlyFields(config, object),
        },
      ];
    },
  };

  function getAdminFields(config, object) {
    var isNew = !object.id;
    return [
      {
        title: "Kit Descriptor ID",
        data: "id",
        type: "read-only",
        getDisplayValue: function (kit) {
          return kit.id || "Unsaved";
        },
      },
      {
        title: "Name",
        data: "name",
        type: "text",
        required: true,
        maxLength: 255,
      },
      {
        title: "Version",
        data: "version",
        type: "int",
      },
      {
        title: "Manufacturer",
        data: "manufacturer",
        type: "text",
        required: true,
        maxLength: 100,
      },
      {
        title: "Part Number",
        data: "partNumber",
        type: "text",
        required: true,
        maxLength: 50,
      },
      {
        title: "Stock Level",
        data: "stockLevel",
        type: "int",
        required: true,
        min: 0,
        initial: 0,
      },
      {
        title: "Description",
        data: "description",
        type: "text",
        maxLength: 255,
      },
      {
        title: "Kit Type",
        include: isNew,
        data: "kitType",
        type: "dropdown",
        required: true,
        source: config.kitTypes,
      },
      {
        title: "Kit Type",
        include: !isNew,
        data: "kitType",
        type: "read-only",
      },
      {
        title: "Platform Type",
        include: isNew,
        data: "platformType",
        type: "dropdown",
        required: true,
        source: Constants.platformTypes,
        getItemLabel: function (item) {
          return item.key;
        },
        getItemValue: function (item) {
          return item.key;
        },
      },
      {
        title: "Platform Type",
        include: !isNew,
        data: "platformType",
        type: "read-only",
      },
      {
        title: "Archived",
        data: "archived",
        type: "checkbox",
      },
    ];
  }

  function getReadOnlyFields(config, object) {
    return [
      {
        title: "Kit Descriptor ID",
        data: "id",
        type: "read-only",
      },
      {
        title: "Name",
        data: "name",
        type: "read-only",
      },
      {
        title: "Version",
        data: "version",
        type: "read-only",
      },
      {
        title: "Manufacturer",
        data: "manufacturer",
        type: "read-only",
      },
      {
        title: "Part Number",
        data: "partNumber",
        type: "read-only",
      },
      {
        title: "Stock Level",
        data: "stockLevel",
        type: "read-only",
      },
      {
        title: "Description",
        data: "description",
        type: "read-only",
      },
      {
        title: "Kit Type",
        data: "kitType",
        type: "read-only",
      },
      {
        title: "Platform Type",
        data: "platformType",
        type: "read-only",
      },
      {
        title: "Archived",
        data: "archived",
        type: "checkbox",
        disabled: true,
      },
    ];
  }
})(jQuery);
