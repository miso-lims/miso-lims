/*
 * SOP Fields list target ( Embedded table on SOP create/edit)
 *
 * This is used on the Create/Edit SOP page as a FormUtils embedded table.
 *
 * Requires Sop controller module (sop_ajax.js) with:
 *   - Sop.addField(field)
 *   - Sop.removeFields(removeFields)
 *
 * Note: SOP fields are user-defined, so "Add" creates a blank editable row.
 */
ListTarget.sopfield = (function () {
  "use strict";

  var FIELD_NAME_MAX = 255;
  var FIELD_UNITS_MAX = 50;
  var ALLOWED_TYPES = ["TEXT", "NUMBER", "PERCENTAGE"];

  return {
    name: "SOP Fields",

    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "standard-operating-procedures");
    },

    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },

    getQueryUrl: null,

    // Keep consistent with other MISO list targets
    showNewOptionSop: true,

    createBulkActions: function (config, projectId) {
      var actions = [];
      if (config.isAdmin) {
        if (config.pageMode === "edit" || config.pageMode === "create") {
          // Embedded table on SOP page
          actions.push({
            name: "Remove",
            action: Sop.removeFields,
          });
        }
      }
      return actions;
    },

    createStaticActions: function (config, projectId) {
      var actions = [];
      if (config.isAdmin) {
        if (config.pageMode === "edit" || config.pageMode === "create") {
          actions.push({
            name: "Add",
            handler: addBlankField,
          });
        }
      }
      return actions;
    },

    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Name",
          mData: "name",
          mRender: function (data, type, full) {
            if (type !== "display") return data;
            return (
              '<input type="text" class="sopfield_name" ' +
              'maxlength="' +
              FIELD_NAME_MAX +
              '" value="' +
              Utils.escapeHtml(data || "") +
              '" />'
            );
          },
        },
        {
          sTitle: "Type",
          mData: "fieldType",
          sWidth: "220px",
          mRender: function (data, type, full) {
            if (type !== "display") return data;

            var current = (data || "TEXT").toString().toUpperCase();
            var html = '<select class="sopfield_type">';
            for (var i = 0; i < ALLOWED_TYPES.length; i++) {
              var t = ALLOWED_TYPES[i];
              html +=
                '<option value="' +
                t +
                '"' +
                (t === current ? ' selected="selected"' : "") +
                ">" +
                t +
                "</option>";
            }
            html += "</select>";
            return html;
          },
        },
        {
          sTitle: "Units",
          mData: "units",
          sWidth: "220px",
          mRender: function (data, type, full) {
            if (type !== "display") return data;
            return (
              '<input type="text" class="sopfield_units" ' +
              'maxlength="' +
              FIELD_UNITS_MAX +
              '" value="' +
              Utils.escapeHtml(data || "") +
              '" />'
            );
          },
        },
      ];
    },

    getModified: function (row, data) {
      var $row = jQuery(row);
      data.name = $row.find("input.sopfield_name").val();
      data.fieldType = ($row.find("select.sopfield_type").val() || "TEXT").toUpperCase();
      var units = $row.find("input.sopfield_units").val();
      data.units = units && units.trim() ? units : null;
      return data;
    },
  };

  function addBlankField() {
    Sop.addField({
      id: null,
      name: "",
      fieldType: "TEXT",
      units: null,
    });
  }
})();
