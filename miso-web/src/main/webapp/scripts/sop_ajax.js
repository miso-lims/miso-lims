var Sop = (function () {
  "use strict";

  var fieldsListId = "listSopFields";

  var form = null;
  var listConfig = {};

  var ALLOWED_TYPES = ["TEXT", "NUMBER", "PERCENTAGE"];

  function trimOrEmpty(v) {
    return v == null ? "" : String(v).trim();
  }

  function toLower(v) {
    return trimOrEmpty(v).toLowerCase();
  }

  function normalizeType(v) {
    var t = trimOrEmpty(v).toUpperCase() || "TEXT";
    return ALLOWED_TYPES.indexOf(t) !== -1 ? t : "TEXT";
  }

  function getFields() {
    return FormUtils.getTableData(fieldsListId) || [];
  }

  function setFields(fields) {
    FormUtils.setTableData(ListTarget.sopfield, listConfig, fieldsListId, fields || [], form);
  }

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setListConfig: function (config) {
      listConfig = config || {};
    },

    setFields: setFields,

    getFields: getFields,

    addField: function (newField) {
      var fields = getFields();

      var name = trimOrEmpty(newField && newField.name);
      if (!name) {
        Utils.showOkDialog("Error", ["Name is required"]);
        return;
      }

      var newName = toLower(name);
      if (
        fields.some(function (f) {
          return toLower(f && f.name) === newName;
        })
      ) {
        Utils.showOkDialog("Error", ["This field name is already included"]);
        return;
      }

      fields.push({
        id: null,
        name: name,
        fieldType: normalizeType(newField && newField.fieldType),
        units: trimOrEmpty(newField && newField.units) || null,
      });

      setFields(fields);
    },

    removeFields: function (toRemove) {
      if (!toRemove || !toRemove.length) return;

      var fields = getFields().filter(function (field) {
        return toRemove.indexOf(field) === -1;
      });

      setFields(fields);
    },
  };
})();
