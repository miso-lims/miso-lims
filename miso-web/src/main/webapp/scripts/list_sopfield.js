ListTarget.sopfield = (function ($) {
  "use strict";

  var FIELD_NAME_MAX = 255;
  var FIELD_UNITS_MAX = 50;
  var ALLOWED_TYPES = ["TEXT", "NUMBER", "PERCENTAGE"];

  function addFieldDialog() {
    Utils.showDialog(
      "Add SOP Field",
      "Add",
      [
        {
          label: "Name",
          type: "text",
          property: "name",
          maxlength: FIELD_NAME_MAX,
          required: true,
        },
        {
          label: "Type",
          type: "select",
          property: "fieldType",
          values: ALLOWED_TYPES,
          required: true,
        },
        {
          label: "Units",
          type: "text",
          property: "units",
          maxlength: FIELD_UNITS_MAX,
        },
      ],
      function (result) {
        Sop.addField({
          id: null,
          name: result.name,
          fieldType: result.fieldType,
          units: result.units || null,
        });
      }
    );
  }

  return {
    name: "SOP Fields",

    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "standard-operating-procedures");
    },

    createUrl: function () {
      throw new Error("Must be provided statically");
    },

    getQueryUrl: null,

    createBulkActions: function (config) {
      if (!config.isAdmin) return [];
      return [
        {
          name: "Remove",
          action: Sop.removeFields,
        },
      ];
    },

    createStaticActions: function (config) {
      if (!config.isAdmin) return [];
      return [
        {
          name: "Add",
          handler: addFieldDialog,
        },
      ];
    },

    createColumns: function () {
      return [
        {
          sTitle: "Name",
          mData: "name",
        },
        {
          sTitle: "Type",
          mData: "fieldType",
        },
        {
          sTitle: "Units",
          mData: "units",
        },
      ];
    },
  };
})(jQuery);
