ListTarget.sopfield = (function ($) {
  "use strict";

  var FIELD_NAME_MAX = 255;
  var FIELD_UNITS_MAX = 50;
  var ALLOWED_TYPES = ["TEXT", "NUMBER", "PERCENTAGE"];

  function normStr(v) {
    return (v == null ? "" : String(v)).trim();
  }

  function normalizeType(v) {
    var t = normStr(v).toUpperCase() || "TEXT";
    return ALLOWED_TYPES.indexOf(t) !== -1 ? t : "TEXT";
  }

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
        },
        {
          label: "Type",
          type: "select",
          property: "fieldType",
          values: ALLOWED_TYPES,
        },
        {
          label: "Units",
          type: "text",
          property: "units",
          maxlength: FIELD_UNITS_MAX,
        },
      ],
      function (result) {
        var name = normStr(result && result.name);
        if (!name) {
          Utils.showOkDialog("Error", ["Name is required"]);
          return;
        }

        Sop.addField({
          id: null,
          name: name,
          fieldType: normalizeType(result && result.fieldType),
          units: normStr(result && result.units) || null,
        });
      }
    );
  }

  function renderTextCell(data, type) {
    if (type === "display") return Utils.escapeHtml(data || "");
    return data || "";
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
          mRender: renderTextCell,
        },
        {
          sTitle: "Type",
          mData: "fieldType",
          sWidth: "220px",
          mRender: function (data, type) {
            var v = normalizeType(data);
            return type === "display" ? Utils.escapeHtml(v) : v;
          },
        },
        {
          sTitle: "Units",
          mData: "units",
          sWidth: "220px",
          mRender: renderTextCell,
        },
      ];
    },
  };
})(jQuery);
