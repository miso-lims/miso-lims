/*
 * SOP embedded table controller (Assay-style)
 *
 * Provides a thin API that ListTarget.sopfield + FormUtils can call on the SOP page:
 *   - Sop.setForm(formApi)
 *   - Sop.setListConfig(config)
 *   - Sop.setFields(fields)
 *   - Sop.getFields()
 *   - Sop.addField(field)
 *   - Sop.removeFields(removeFields)
 */

var Sop = (function () {
  "use strict";

  var fieldsListId = "listSopFields";

  var form = null;
  var listConfig = {};

  function normStr(v) {
    return (v == null ? "" : String(v)).trim();
  }

  function normUpper(v) {
    return normStr(v).toUpperCase();
  }

  function normLower(v) {
    return normStr(v).toLowerCase();
  }

  /**
   * IMPORTANT:
   * FormUtils.getTableData() may return the table model, but depending on how the
   * inline editors are implemented, the latest typed values may still live in DOM inputs.
   * This function copies DOM values back onto the returned objects (index-aligned).
   */
  function syncFromDom(fields) {
    var $table = jQuery("#" + fieldsListId).find("table");
    if (!$table.length) return fields;

    var $rows = $table.find("tbody tr");
    $rows.each(function (i) {
      var f = fields[i];
      if (!f) return;

      var $tr = jQuery(this);

      // Name + Units are the two text inputs in the row.
      var $textInputs = $tr.find("input[type='text']");
      var nameVal = $textInputs.eq(0).val();
      var unitsVal = $textInputs.eq(1).val();

      // Type is the select in the row.
      var typeVal = $tr.find("select").first().val();

      if (typeof nameVal !== "undefined") f.name = nameVal;
      if (typeof typeVal !== "undefined") f.fieldType = typeVal;
      if (typeof unitsVal !== "undefined") f.units = unitsVal;
    });

    return fields;
  }

  function normalizeField(field) {
    return {
      id: field && field.id ? field.id : null,
      name: normStr(field && field.name),
      fieldType: normUpper(field && field.fieldType) || "TEXT",
      units: normStr(field && field.units) || null,
    };
  }

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setListConfig: function (config) {
      listConfig = config || {};
    },

    setFields: function (fields) {
      FormUtils.setTableData(ListTarget.sopfield, listConfig, fieldsListId, fields || [], form);
    },

    getFields: function () {
      var fields = FormUtils.getTableData(fieldsListId) || [];
      fields = syncFromDom(fields);

      // Normalize output so backend gets exactly what it expects
      return fields.map(normalizeField);
    },

    addField: function (addField) {
      var fields = Sop.getFields() || [];

      // Early duplicate prevention (case-insensitive) if a name is present
      var newName = normLower(addField && addField.name);
      if (
        newName &&
        fields.some(function (f) {
          return normLower(f && f.name) === newName;
        })
      ) {
        Utils.showOkDialog("Error", ["This field name is already included"]);
        return;
      }

      fields.push({
        id: null,
        name: addField && addField.name != null ? addField.name : "",
        fieldType: addField && addField.fieldType ? addField.fieldType : "TEXT",
        units: addField && addField.units != null ? addField.units : null,
      });

      Sop.setFields(fields);
    },

    removeFields: function (removeFields) {
      var fields = Sop.getFields() || [];
      var remove = removeFields || [];

      fields = fields.filter(function (field) {
        return !remove.some(function (r) {
          // If FormUtils passes the same object reference, this is the cleanest match:
          if (r === field) return true;

          // Prefer id match if present
          if (r && r.id && field && field.id) {
            return r.id === field.id;
          }

          // Otherwise match by (name, type, units) even if blank
          var fn = normLower(field && field.name);
          var rn = normLower(r && r.name);

          var ft = normUpper(field && field.fieldType) || "TEXT";
          var rt = normUpper(r && r.fieldType) || "TEXT";

          var fu = normStr(field && field.units);
          var ru = normStr(r && r.units);

          return fn === rn && ft === rt && fu === ru;
        });
      });

      Sop.setFields(fields);
    },
  };
})();
