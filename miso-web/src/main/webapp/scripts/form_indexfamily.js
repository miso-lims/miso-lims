FormTarget = FormTarget || {};
FormTarget.indexfamily = (function ($) {
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
      return family.id ? Urls.rest.indexFamilies.update(family.id) : Urls.rest.indexFamilies.create;
    },
    getSaveMethod: function (family) {
      return family.id ? "PUT" : "POST";
    },
    getEditUrl: function (family) {
      return Urls.ui.indexFamilies.edit(family.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Index Family Information",
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
      {
        title: "Platform",
        data: "platformType",
        type: "dropdown",
        include: config.pageMode === "create",
        required: true,
        source: Constants.platformTypes,
        getItemLabel: function (item) {
          return item.key;
        },
        getItemValue: Utils.array.getName,
      },
      {
        title: "Platform",
        data: "platformType",
        include: config.pageMode !== "create",
        type: "read-only",
        getDisplayValue: function (family) {
          return Utils.array.findUniqueOrThrow(
            Utils.array.namePredicate(family.platformType),
            Constants.platformTypes
          ).key;
        },
      },
      {
        title: "Multi-Sequence Indices",
        data: "fakeSequence",
        type: "checkbox",
        disabled: config.pageMode !== "create",
        description:
          "Indicates that there are multiple sequences belonging to each index, and all" +
          " of those sequences should be treated as the same index. These indices can be given an" +
          ' additional "demultiplexing name" to be used by downstream analysis for demultiplexing.',
      },
      {
        title: "Unique Dual Indices",
        data: "uniqueDualIndex",
        type: "checkbox",
        description:
          "For dual-index families, indicates that indices are usually added to libraries" +
          " in the same pairs, as opposed to mixing any index 1 with any index 2. When selecting" +
          " the first index for a library, the second will be selected automatically. For this to" +
          " work, both indices in a pair must have the same name.",
      },
      {
        title: "Archived",
        data: "archived",
        type: "checkbox",
        description:
          "Archived index families will not show up in the options list when creating new libraries",
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
      {
        title: "Platform",
        data: "platformType",
        type: "read-only",
        getDisplayValue: function (family) {
          return Utils.array.findUniqueOrThrow(
            Utils.array.namePredicate(family.platformType),
            Constants.platformTypes
          ).key;
        },
      },
      {
        title: "Multi-Sequence Indices",
        data: "fakeSequence",
        type: "checkbox",
        disabled: true,
      },
      {
        title: "Unique Dual Indices",
        data: "uniqueDualIndex",
        type: "checkbox",
        disabled: true,
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
