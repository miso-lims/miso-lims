BulkTarget = window.BulkTarget || {};
BulkTarget.sop = (function ($) {
  "use strict";

  var FIELD_NAME_MAX = 255;
  var FIELD_UNITS_MAX = 50;
  var ALLOWED_TYPES = ["TEXT", "NUMBER", "PERCENTAGE"];

  function normalizeFieldName(name) {
    return (name || "").trim().toLowerCase();
  }

  function formatSopFields(sop) {
    if (!sop || !sop.sopFields || !sop.sopFields.length) return "";
    return sop.sopFields
      .filter(function (f) {
        return f && f.name;
      })
      .map(function (f) {
        var name = (f.name || "").trim();
        var type = (f.fieldType || "TEXT").toString().trim().toUpperCase();
        var units = (f.units || "").toString().trim();
        return name + "|" + type + "|" + units;
      })
      .join("; ");
  }

  function parseSopFields(text) {
    var raw = (text || "").trim();
    if (!raw) return [];

    var parts = raw
      .split(";")
      .map(function (p) {
        return p.trim();
      })
      .filter(Boolean);

    var seen = {};
    var fields = [];

    parts.forEach(function (part, idx) {
      var tokens = part.split("|").map(function (t) {
        return t.trim();
      });

      var name = tokens[0] || "";
      var fieldType = tokens.length >= 2 ? (tokens[1] || "").toUpperCase() : "";
      var units = tokens.length >= 3 ? tokens[2] || "" : "";

      if (!name.trim()) {
        throw new Error("SOP Fields: field #" + (idx + 1) + " has an empty name.");
      }
      name = name.trim();

      if (name.length > FIELD_NAME_MAX) {
        throw new Error("SOP Fields: field name too long (max " + FIELD_NAME_MAX + "): " + name);
      }

      if (!fieldType) {
        fieldType = "TEXT";
      }

      if (ALLOWED_TYPES.indexOf(fieldType) === -1) {
        throw new Error(
          "SOP Fields: invalid type '" +
            fieldType +
            "' for field '" +
            name +
            "'. Allowed: " +
            ALLOWED_TYPES.join(", ")
        );
      }

      units = (units || "").trim();
      if (units && units.length > FIELD_UNITS_MAX) {
        throw new Error(
          "SOP Fields: units too long (max " + FIELD_UNITS_MAX + ") for field: " + name
        );
      }

      var key = normalizeFieldName(name);
      if (seen[key]) {
        throw new Error("SOP Fields: duplicate field name: " + name);
      }
      seen[key] = true;

      fields.push({
        name: name,
        fieldType: fieldType,
        units: units ? units : null,
      });
    });

    return fields;
  }

  function validateAllRows(data, api) {
    for (var i = 0; i < (data || []).length; i++) {
      var row = data[i];
      var rawCell = row && row._sopFieldsText ? row._sopFieldsText : "";
      try {
        parseSopFields(rawCell);
      } catch (e) {
        if (api && api.showError) {
          api.showError("Row " + (i + 1) + ": " + e.message);
        }
        throw e;
      }
    }
  }

  return {
    description:
      "SOP Fields can contain multiple definitions. Separate fields with ';'. " +
      "Format: Name|TYPE|Units. TYPE defaults to TEXT if omitted. Units are optional.\n\n" +
      "Example: Flow Cell Lot|TEXT|; PhiX %|PERCENTAGE|%; Library Conc|NUMBER|nM",

    getSaveUrl: function () {
      return Urls.rest.sops.bulkSave; // /rest/sops/bulk
    },

    getSaveProgressUrl: function (operationId) {
      return Urls.rest.sops.bulkSaveProgress(operationId); // /rest/sops/bulk/{uuid}
    },

    getUserManualUrl: function () {
      return Urls.external.userManual("sops");
    },

    getEditUrl: function (ids) {
      return "/rest/sops/bulk?ids=" + (ids || []).join(",");
    },

    getBulkActions: function (config) {
      return [];
    },

    prepareData: function (data) {
      if (!data) return;
      data.forEach(function (sop) {
        if (!sop) return;

        if (typeof sop._sopFieldsText === "undefined") {
          sop._sopFieldsText = formatSopFields(sop);
        }

        if (sop._sopFieldsText && (!sop.sopFields || !sop.sopFields.length)) {
          try {
            sop.sopFields = parseSopFields(sop._sopFieldsText);
          } catch (e) {
            // leave invalid text as-is; confirmSave will block and show error
          }
        }
      });
    },

    confirmSave: function (data, config, api) {
      try {
        validateAllRows(data, api);
        return;
      } catch (e) {
        var d = $.Deferred();
        d.reject();
        return d.promise();
      }
    },

    getColumns: function (config, api) {
      return [
        BulkUtils.columns.simpleAlias(255),
        {
          title: "Version",
          type: "text",
          data: "version",
          required: true,
          maxLength: 50,
        },
        {
          title: "Category",
          type: "dropdown",
          data: "category",
          source: (function () {
            if (window.Constants && Constants.sopCategories) {
              return Object.keys(Constants.sopCategories).map(function (key) {
                return { name: Constants.sopCategories[key], value: key };
              });
            }
            return [
              { name: "Sample", value: "SAMPLE" },
              { name: "Library", value: "LIBRARY" },
              { name: "Run", value: "RUN" },
            ];
          })(),
          getItemLabel: function (item) {
            return item.name;
          },
          getItemValue: function (item) {
            return item.value;
          },
          required: true,
        },
        {
          title: "URL",
          type: "text",
          data: "url",
          maxLength: 255,
        },
        {
          title: "Archived",
          type: "dropdown",
          data: "archived",
          source: [
            { label: "False", value: false },
            { label: "True", value: true },
          ],
          getItemLabel: function (item) {
            return item.label;
          },
          getItemValue: function (item) {
            return item.value;
          },
          convertToBoolean: true,
          required: true,
        },
        {
          title: "SOP Fields",
          type: "text",
          data: "_sopFieldsText",
          maxLength: 2000,
          description:
            "Multiple fields: separate with ';'. " +
            "Format: Name|TYPE|Units. TYPE defaults to TEXT if omitted. Units optional. " +
            "Example: Flow Cell Lot|TEXT|; PhiX %|PERCENTAGE|%; Library Conc|NUMBER|nM",
          getData: function (sop) {
            if (typeof sop._sopFieldsText === "undefined") {
              sop._sopFieldsText = formatSopFields(sop);
            }
            return sop._sopFieldsText;
          },
          setData: function (sop, value) {
            sop._sopFieldsText = value || "";
            sop.sopFields = parseSopFields(sop._sopFieldsText);
          },
        },
      ];
    },
  };
})(jQuery);
